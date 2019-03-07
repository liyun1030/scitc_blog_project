package cn.com.scitc.blog.dao;

import cn.com.scitc.blog.domain.Blog;
import org.apache.ibatis.annotations.Param;

public interface BlogDao {
	//根据id查询一篇blog，显示在博客页面
	Blog findBlogByID(@Param("blogid")int blogid);
	//发布文章，插入blog，发布博客页面
	Long insertBlog(Blog blog);


}
