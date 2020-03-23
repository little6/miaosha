package com.geekq.miaosha.service;

import com.geekq.miaosha.dao.UserDao;
import com.geekq.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//用户service
@Service
public class UserService {

	@Autowired
	private UserDao userDao ;
	
	public User getById(int id) {
		return userDao.getById(id);
	}

	public int insertUser(User user){ return userDao.insert(user);}

}
