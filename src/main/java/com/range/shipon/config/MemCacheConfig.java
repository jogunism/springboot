package com.range.shipon.config;

import java.util.ArrayList;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.ssm.Cache;
import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.config.AddressProvider;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.elasticache.MemcacheClientFactoryImpl;
import com.google.code.ssm.spring.SSMCache;
import com.google.code.ssm.spring.SSMCacheManager;

@Configuration
@EnableCaching
public class MemCacheConfig {

	private static final String MEMCACHEED_SERVER = "range-memcashed.upeywp.cfg.euc1.cache.amazonaws.com:11211";
	private static final int EXPIRATION = 60 * 60 * 24;	// one day
	
	@Bean
	public CacheManager cacheManager() {

	    MemcacheClientFactoryImpl cacheClientFactory = new MemcacheClientFactoryImpl();
	    AddressProvider addressProvider = new DefaultAddressProvider(MEMCACHEED_SERVER);
	    com.google.code.ssm.providers.CacheConfiguration cacheConfiguration = new com.google.code.ssm.providers.CacheConfiguration();
	    cacheConfiguration.setConsistentHashing(true); //TODO check this

	    CacheFactory cacheFactory = new CacheFactory();
	    cacheFactory.setCacheName("defaultCache");
	    cacheFactory.setCacheClientFactory(cacheClientFactory);
	    cacheFactory.setAddressProvider(addressProvider);
	    cacheFactory.setConfiguration(cacheConfiguration);

	    Cache object = null;
	    try {
	        object = cacheFactory.getObject();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    SSMCache ssmCache = new SSMCache(object, EXPIRATION, true); //TODO be very carefully here, third param allow remove all entries!!

	    ArrayList<SSMCache> ssmCaches = new ArrayList<SSMCache>();
	    ssmCaches.add(0, ssmCache);

	    SSMCacheManager ssmCacheManager = new SSMCacheManager();
	    ssmCacheManager.setCaches(ssmCaches);

	    return ssmCacheManager;
	}
	
}
