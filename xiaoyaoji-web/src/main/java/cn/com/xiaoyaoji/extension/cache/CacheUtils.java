package cn.com.xiaoyaoji.extension.cache;

import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.extension.cache.factory.CacheFactory;
import cn.com.xiaoyaoji.extension.cache.provider.CacheProvider;
import cn.com.xiaoyaoji.utils.ConfigUtils;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * @author: zhoujingjie
 * @Date: 16/5/4
 */
public class CacheUtils {
    private static CacheProvider cacheProvider;
    private static int expires = ConfigUtils.getTokenExpires();
    private static Logger logger = Logger.getLogger(CacheUtils.class);
    static {
        try {
            CacheFactory cacheFactory = (CacheFactory) Class.forName(ConfigUtils.getProperty("cache.provider.factory")).newInstance();
            cacheProvider = cacheFactory.create();
        } catch (InstantiationException |ClassNotFoundException |IllegalAccessException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static String token() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    public static void putUser(String token, User user) {
        cacheProvider.put(token, "user", user, expires);
    }

    public static User getUser(String token) {
        if (org.apache.commons.lang3.StringUtils.isBlank(token))
            throw new IllegalArgumentException("token is null");
        String userjson = (String) cacheProvider.get(token, "user", expires);
        if (userjson == null)
            return null;
        return JSON.parseObject(userjson, User.class);
    }

    public static void put(String token, String key, Object data) {
        cacheProvider.put(token, key, data, expires);
    }

    public static Object get(String token, String key) {
        return cacheProvider.get(token, key, expires);
    }

    public static void remove(String token){
        cacheProvider.remove(token);
    }
    public static void remove(String token,String key){
        cacheProvider.remove(token,key);
    }

}
