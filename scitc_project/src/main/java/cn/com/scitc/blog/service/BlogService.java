package cn.com.scitc.blog.service;

import cn.com.scitc.blog.domain.Blog;

public interface BlogService {
	//显示一篇博客
	public Blog displayBlog(int blogid);
	//发布博客
	public Long ReleaseBlog(Blog blog);
	

}
