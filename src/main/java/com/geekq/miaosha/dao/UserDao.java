package com.geekq.miaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.geekq.miaosha.domain.User;

@Mapper
public interface UserDao {

	//根据id查询
	@Select("select * from user where id = #{id}")
	public User getById(@Param("id")int id);

	//插入user
	@Insert("insert into user (id, name) values(#{id}, #{name})")
	public int insert(User user);
	
	

}
