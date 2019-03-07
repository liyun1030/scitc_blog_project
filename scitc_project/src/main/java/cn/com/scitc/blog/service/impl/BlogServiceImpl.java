package cn.com.scitc.blog.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import cn.com.scitc.blog.dao.BlogDao;
import cn.com.scitc.blog.domain.Blog;
import cn.com.scitc.blog.service.BlogService;

@Service
public class BlogServiceImpl implements BlogService {
	
	@Autowired
	private BlogDao blogDao;
	
	@Autowired
	private RedisTemplate redisTemplate;
	

	public Blog displayBlog(int blogid){
		
		//TODO: 判断redis是否连接

		try {
			
			//从缓存中读取博客信息
			String key = String.valueOf(blogid);
			ValueOperations<String, Blog> operations = redisTemplate.opsForValue();
				
			//缓存存在则返回缓存中的内容
			boolean hasKey = redisTemplate.hasKey(key);
			
			if(hasKey){
				Blog blog = operations.get(key);
				operations.set(key, blog, 5, TimeUnit.SECONDS);
				System.out.println("是缓存中读取的");
				return blog;
			}
			//不存在缓存则在数据库中获取数据，然后插入缓存中
			Blog blog = blogDao.findBlogByID(blogid);
			System.out.println("是数据库中读取的");
			operations.set(key, blog, 5, TimeUnit.SECONDS);
			
			return blog;
		} catch (Exception e) {
			// TODO: handle exception
			Blog blog = blogDao.findBlogByID(blogid);
			System.out.println("redis服务器未连接，直接从数据库中读取");
			return blog;
		}
		
		
	}
	//发布博客
	public Long ReleaseBlog(Blog blog){
		
		return blogDao.insertBlog(blog);
	}
	

}
