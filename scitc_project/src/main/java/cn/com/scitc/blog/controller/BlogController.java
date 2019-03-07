package cn.com.scitc.blog.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.scitc.blog.domain.Blog;
import cn.com.scitc.blog.domain.User;
import cn.com.scitc.blog.domain.UserBlog;
import cn.com.scitc.blog.service.BlogService;
import cn.com.scitc.blog.service.UserBlogService;


@Controller
public class BlogController {

	@Autowired
	private BlogService blogService;
	@Autowired
	private UserBlogService userblogService;

	// 未登录页面
	@RequestMapping("/unlisted")
	public ModelAndView unlistedPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView modelAndView = new ModelAndView();

		// 设置页数和每页显示条数的默认值
		int page = 0;
		int pagesize = 10;

		if (request.getParameter("page") != null) {
			page = new Integer(request.getParameter("page")) - 1;
		}

		if (request.getParameter("pagesize") != null) {

			pagesize = new Integer(request.getParameter("pagesize"));
		}

		int offset = page * pagesize;

		int count = userblogService.countAll();

		List<Blog> blogs = userblogService.allBlogPaged(offset, pagesize);
		modelAndView.addObject("page", page + 1);
		modelAndView.addObject("pagesize", pagesize);
		modelAndView.addObject("pages", Math.ceil(count / pagesize) + 1);
		modelAndView.addObject("total", count);
		modelAndView.addObject("results", blogs);
		modelAndView.setViewName("unlisted");
		return modelAndView;
	}

	// 返回的JSON信息的接口
	@RequestMapping("/api/listed")
	@ResponseBody
	public Map<String, Object> paged(HttpServletRequest request) {
		// 得到session信息
		HttpSession httpSession = request.getSession();
		// 得到session信息中，登录账户的userid
		User user = (User) httpSession.getAttribute("user");
		int userid = user.getUserid();

		int page = 0;
		int pagesize = 10;

		if (request.getParameter("page") != null) {
			page = new Integer(request.getParameter("page")) - 1;
		}

		if (request.getParameter("pagesize") != null) {

			pagesize = new Integer(request.getParameter("pagesize"));
		}

		int offset = page * pagesize;

		Map<String, Object> result = new HashMap<String, Object>();

		int count = userblogService.count(userid);

		result.put("page", page + 1);
		result.put("pagesize", pagesize);
		result.put("total", count);
		result.put("results", userblogService.pagedquery(userid, offset, pagesize));
		result.put("user", user);

		return result;
	}

	// 登录后，我的文章页面，listed
	@RequestMapping(value = "/listed",method = RequestMethod.POST)
	public ModelAndView listedPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 得到session信息
		HttpSession httpSession = request.getSession();

		ModelAndView modelAndView = new ModelAndView();
		// 得到session信息中的登录用户的userid
		User user = (User) httpSession.getAttribute("user");
		if (user == null) {
			response.sendRedirect("/login");
			return null;
		}

		int userid = user.getUserid();
		// 设置页数和每页显示条数的默认值
		int page = 0;
		int pagesize = 10;

		if (request.getParameter("page") != null) {
			page = new Integer(request.getParameter("page")) - 1;
		}

		if (request.getParameter("pagesize") != null) {

			pagesize = new Integer(request.getParameter("pagesize"));
		}

		int offset = page * pagesize;

		int count = userblogService.count(userid);

		List<UserBlog> userBlogs = userblogService.pagedquery(userid, offset, pagesize);
		modelAndView.addObject("page", page + 1);
		modelAndView.addObject("pagesize", pagesize);
		modelAndView.addObject("pages", Math.ceil(count / pagesize) + 1);
		modelAndView.addObject("total", count);
		modelAndView.addObject("results", userBlogs);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("listed");
		return modelAndView;
	}

	// 关键字搜索博客，返回list

	@RequestMapping(value = "/search")
	public ModelAndView search(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();

		String keyword = null;
		int page = 0;
		int pagesize = 10;

		if (request.getParameter("keyword") != null) {
			keyword = new String(request.getParameter("keyword"));
		}

		if (request.getParameter("page") != null) {
			page = new Integer(request.getParameter("page")) - 1;
		}

		if (request.getParameter("pagesize") != null) {

			pagesize = new Integer(request.getParameter("pagesize"));
		}

		int offset = page * pagesize;
		int searchcount = userblogService.searchcount(keyword);
		int pages = (int) Math.ceil(searchcount / pagesize);

		modelAndView.addObject("keyword", keyword);
		modelAndView.addObject("total", searchcount);
		modelAndView.addObject("pages", pages);
		modelAndView.addObject("page", page + 1);
		modelAndView.addObject("pagesize", pagesize);
		modelAndView.addObject("results", userblogService.search(keyword, offset, pagesize));
		modelAndView.setViewName("search");

		return modelAndView;

	}

	// 点击列表链接，根据blogid查询，跳转到显示博客详情paper页面
	@RequestMapping("/paper/{id}")
	public ModelAndView paper(@PathVariable String id) {

		ModelAndView modelAndView = new ModelAndView();

		int blogid = Integer.parseInt(id);
		// int blogid =Integer.valueOf(id);

		Blog blog = blogService.displayBlog(blogid);
		String username = userblogService.findNameByBlogid(blogid);

		modelAndView.addObject("title", blog.getTitle());
		modelAndView.addObject("blog", blog.getBlog());
		modelAndView.addObject("username", username);
		modelAndView.addObject("date", blog.getDate());
		modelAndView.setViewName("paper");

		return modelAndView;
	}


	@RequestMapping(value = "/write", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> write(HttpServletRequest request, HttpServletResponse response, @RequestBody Blog blog)
			throws IOException {
		// 得到session信息
		HttpSession httpSession = request.getSession();

		// 得到session信息中的登录用户的userid，如果session失效，返回登录页面，程序进行登录
		User user = (User) httpSession.getAttribute("user");
		if (user == null) {
			response.sendRedirect("/login");
			return null;
		}

		Map<String, String> result = new HashMap<String, String>();
		int userid = user.getUserid();
		blog.setUserid(userid);
		blog.setDate(new Date());
		blogService.ReleaseBlog(blog);

		result.put("msg", "ok");
		return result;
	}

	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public String writeGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 得到session信息
		HttpSession httpSession = request.getSession();

		// 得到session信息中的登录用户的userid，如果session失效，或者没有session信心，返回登录页面，进行登录
		User user = (User) httpSession.getAttribute("user");
		if (user == null) {
			response.sendRedirect("/login");
			return null;
		}

		return "write";
	}



}
