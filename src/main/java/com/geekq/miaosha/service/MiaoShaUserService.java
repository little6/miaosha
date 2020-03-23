package com.geekq.miaosha.service;

import com.geekq.miaosha.common.SnowflakeIdWorker;
import com.geekq.miaosha.common.enums.MessageStatus;
import com.geekq.miaosha.controller.RegisterController;
import com.geekq.miaosha.dao.MiaoShaUserDao;
import com.geekq.miaosha.domain.MiaoshaUser;
import com.geekq.miaosha.exception.GlobleException;
import com.geekq.miaosha.rabbitmq.MQSender;
import com.geekq.miaosha.redis.MiaoShaUserKey;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.utils.MD5Utils;
import com.geekq.miaosha.utils.UUIDUtil;
import com.geekq.miaosha.vo.LoginVo;
import com.geekq.miaosha.vo.MiaoShaMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import static com.geekq.miaosha.common.enums.ResultStatus.*;

@Service
public class MiaoShaUserService {

    //cookie中token的名称
    public static final String COOKIE_NAME_TOKEN = "token" ;

    private static Logger logger = LoggerFactory.getLogger(MiaoShaUserService.class);

    @Autowired
    private MiaoShaUserDao miaoShaUserDao ;

    @Autowired
    private RedisService redisService ;

    @Autowired
    private MQSender sender ;


    /**
     * 根据token从redis中获取用户session信息
     * @param response
     * @param token
     * @return
     */
    public MiaoshaUser getByToken(HttpServletResponse response , String token) {

        if(StringUtils.isEmpty(token)){
            return null ;
        }
        MiaoshaUser user =redisService.get(MiaoShaUserKey.token,token,MiaoshaUser.class) ;
        if(user!=null) {
            //重新设置token有效期(延长有效期)
            addCookie(response, token, user);
        }
        return user ;

    }

    /**
     * 根据用户名(手机号)查询秒杀用户-------->对象级缓存技术(细粒度对象缓存)
     * @param nickName
     * @return
     */
    public MiaoshaUser getByNickName(String nickName) {
        //1、取缓存
        MiaoshaUser user = redisService.get(MiaoShaUserKey.getByNickName, ""+nickName, MiaoshaUser.class);
        if(user != null) {
            return user;
        }
        //2、取数据库
        user = miaoShaUserDao.getByNickname(nickName);
        if(user != null) {
            //取出来后，设置到redis缓存中
            redisService.set(MiaoShaUserKey.getByNickName, ""+nickName, user);
        }
        return user;
    }


    /**
     * 修改密码(数据库和缓存 数据一致性)------>更新缓存
     * @param token
     * @param nickName
     * @param formPass
     * @return
     */
    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, String nickName, String formPass) {
        //1、获取user
        MiaoshaUser user = getByNickName(nickName);
        if(user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }
        //2、更新数据库（最小更新，仅仅更新密码）
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setNickname(nickName);
        toBeUpdate.setId(user.getId());
        toBeUpdate.setPassword(MD5Utils.formPassToDBPass(formPass, user.getSalt()));
        miaoShaUserDao.update(toBeUpdate);
        //3、处理缓存(删除nickname缓存)
        redisService.delete(MiaoShaUserKey.getByNickName, ""+nickName);
        user.setPassword(toBeUpdate.getPassword());
        //4、更新token缓存
        redisService.set(MiaoShaUserKey.token, token, user);
        return true;
    }


    /**
     * 用户注册
     * @param response
     * @param userName 用户昵称(手机号)
     * @param passWord 经过一次md5后的密码
     * @param salt 加密盐
     * @return
     */
    public boolean register(HttpServletResponse response , String userName , String passWord , String salt) {
        MiaoshaUser miaoShaUser =  new MiaoshaUser();
        miaoShaUser.setNickname(userName);
        String DBPassWord =  MD5Utils.formPassToDBPass(passWord , salt);
        miaoShaUser.setPassword(DBPassWord);
        miaoShaUser.setRegisterDate(new Date());
        miaoShaUser.setSalt(salt);
        miaoShaUser.setNickname(userName);
        try {
            miaoShaUserDao.insertMiaoShaUser(miaoShaUser);
            MiaoshaUser user = miaoShaUserDao.getByNickname(miaoShaUser.getNickname());
            if(user == null){
                return false;
            }
            //生成token 将token放入cookie返回游览器，实现分布式session
            String token= UUIDUtil.uuid();
            addCookie(response, token, user);
        } catch (Exception e) {
            logger.error("注册失败",e);
            return false;
        }
        return true;
    }

    /**
     * 用户登陆
     * @param response
     * @param loginVo
     * @return
     */
    public boolean login(HttpServletResponse response , LoginVo loginVo) {
        if(loginVo ==null){
            throw  new GlobleException(SYSTEM_ERROR);
        }

        String mobile =loginVo.getMobile();
        String password =loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getByNickName(mobile);
        if(user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }

        //验证密码
        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        //对加密一次后的密码进行二次加密(使用数据库中保存的随机盐)
        String calcPass = MD5Utils.formPassToDBPass(password,saltDb);
        if(!calcPass.equals(dbPass)){
            throw new GlobleException(PASSWORD_ERROR);
        }
        //生成cookie 将session返回游览器 分布式session
        String token= UUIDUtil.uuid();
        addCookie(response, token, user);
        return true ;
    }


    /**
     * 生成测试数据的方法， 返回token信息
     * @param response
     * @param loginVo
     * @return
     */
    public String createToken(HttpServletResponse response , LoginVo loginVo) {
        if(loginVo ==null){
            throw  new GlobleException(SYSTEM_ERROR);
        }

        String mobile =loginVo.getMobile();
        String password =loginVo.getPassword();
        MiaoshaUser user = getByNickName(mobile);
        if(user == null) {
            throw new GlobleException(MOBILE_NOT_EXIST);
        }

        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass = MD5Utils.formPassToDBPass(password,saltDb);
        if(!calcPass.equals(dbPass)){
            throw new GlobleException(PASSWORD_ERROR);
        }
        //生成cookie 将session返回游览器 分布式session
        String token= UUIDUtil.uuid();
        addCookie(response, token, user);
        return token ;
    }


    /**
     * 将token放入cookie，返回客户端浏览器，用户session信息保存到redis中
     * @param response
     * @param token
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoShaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期，和session保持一致
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
