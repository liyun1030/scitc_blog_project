package cn.com.scitc.blog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.scitc.blog.dao.UserDao;
import cn.com.scitc.blog.domain.User;
import cn.com.scitc.blog.service.UserService;


@Service
public class UserServiceImpl implements UserService {
	@Autowired
private UserDao userDao;
	
	//实现登录
	public User Login(String username,String password){
		User user = userDao.checkUser(username,password);
		if(user != null && user.getPassword().equals(password)){
			return user;
		}
		return null;
	}
	//实现注册
	public Long Register(User user){
		return userDao.insertUser(user);
	}

	//注册是否重复
	public User findNameByName(String username){
		return userDao.findNameByName(username);
	}

}
