package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.*;

//秒杀用户dao
@Mapper
public interface MiaoShaUserDao {

    @Select("select * from miaosha_user where nickname = #{nickname}")
    public MiaoshaUser getByNickname(@Param("nickname") String nickname ) ;

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(@Param("id") long id ) ;


    //修改密码
    @Update("update miaosha_user set password = #{password} where id = #{id}")
    public void update(MiaoshaUser toBeUpdate);


    @Insert("insert into miaosha_user (id , nickname ,password , salt ,head,register_date,last_login_date)value (#{id},#{nickname},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insertMiaoShaUser(MiaoshaUser miaoshaUser);

}