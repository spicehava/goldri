package club.goldri.core.util;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Cache工具类
 */
public class CacheUtil {

	private static CacheManager cacheManager = (EhCacheCacheManager) SpringUtil.getBean("ehCacheCacheManager");

	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object get(String cacheName, String key) {
		Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();
	}

	/**
	 * 写入缓存
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	/**
	 * 从缓存中移除
	 * @param cacheName
	 * @param key
	 */
	public static void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}
	
	private static Ehcache getCache(String cacheName){
		Ehcache cache = ((EhCacheCacheManager) cacheManager).getCacheManager().getCache(cacheName);
		return cache;
	}
}
