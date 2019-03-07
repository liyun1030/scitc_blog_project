package cn.com.scitc.blog.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.scitc.blog.domain.User;
import cn.com.scitc.blog.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login")
	public ModelAndView Login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("user:" + username + " pass:" + password);

		ModelAndView modelAndView = new ModelAndView();

		User user = userService.Login(username, password);

		if (user != null) {
			//设置session属性
			request.getSession().setAttribute("user", user);

			response.sendRedirect("/listed");

		}else {
			System.out.println("user is null");
			modelAndView.setViewName("login");
		}

		return modelAndView;
	}

		@RequestMapping("/logout")
		public String loginout(HttpServletRequest request,HttpServletResponse response) throws IOException{
			//防止系统创建session
			HttpSession httpSession = request.getSession(false);
			//清除session返回到未登录主页面
			httpSession.removeAttribute("user");
			response.sendRedirect("/unlisted");
			return null;
		//	return "unlisted";
		}
	


	@RequestMapping("/register")
	public String register(){
		return "register";
	}


	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> registerapi(@RequestBody User user) {

		System.out.println(user.getUsername());
		// 插入数据库
		userService.Register(user);
		
		Map<String, String> result = new HashMap<String, String>();
		
		result.put("username",user.getUsername());
		result.put("password",user.getPassword());
		
		return result;

	}

	@RequestMapping("/username")
	@ResponseBody
	public Map<String, Object> checkUser(@RequestParam(value = "username") String username) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (userService.findNameByName(username) != null) {
			result.put("result", "用户已存在");
			result.put("code", 1);
		} else {
			result.put("result", "用户不存在");
			result.put("code", 0);
		}
		return result;
	}

}
