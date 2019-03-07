package cn.com.scitc.blog.dao;

import cn.com.scitc.blog.domain.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
	//检查user的信息是否和数据库匹配，登录检测
	public User checkUser(@Param("username")String username, @Param("password")String password);
	//注册 添加用户
	public Long insertUser(User user);
	//注册是否重复
	public User findNameByName(String username);

}
