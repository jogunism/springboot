package com.range.shipon.component;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.range.shipon.config.MemCacheConfig;

@Component
public class CacheWrapper {

	private static final String CACHE_NAME = "defaultCache";

	private ApplicationContext context;

	private Cache cache;
	
	private void connection() throws Exception {
		if (this.cache == null) {
			throw new Exception("There's no connection.");
		}
	}

	
	public void open() throws Exception {
		if (cache == null) {
			this.context = new AnnotationConfigApplicationContext(MemCacheConfig.class);
			CacheManager cacheManager = context.getBean(CacheManager.class);
			if (cacheManager != null) {
				this.cache = cacheManager.getCache(CACHE_NAME);
			}
		}
	}

	public void close() throws Exception {
		((AnnotationConfigApplicationContext) this.context).close();
	}

	public void put(String key, Object value) throws Exception {
		connection();
//		System.out.println(key +" : "+ value);
	   	this.cache.put(key, value);
	}

	public Object get(String key) throws Exception {
		connection();
		return this.cache.get(key).get();
	}

	public void evict(String key) throws Exception {
		connection();
		this.cache.evict(key);
	}

	public void clear() throws Exception {
		connection();
		this.cache.clear();
	}

}
