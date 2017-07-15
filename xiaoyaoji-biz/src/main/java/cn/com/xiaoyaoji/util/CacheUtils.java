package cn.com.xiaoyaoji.util;

import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.integration.cache.CacheManager;
import com.alibaba.fastjson.JSON;

import java.util.UUID;

/**
 * @author: zhoujingjie
 * @Date: 16/5/4
 */
public class CacheUtils {
    private static int expires = ConfigUtils.getTokenExpires();

    public static String token() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    public static void putUser(String token, User user) {
        CacheManager.getCacheProvider().put(token, "user", user, expires);
    }

    public static User getUser(String token) {
        if (org.apache.commons.lang3.StringUtils.isBlank(token))
            throw new IllegalArgumentException("token is null");
        String userjson = (String) CacheManager.getCacheProvider().get(token, "user", expires);
        if (userjson == null)
            return null;
        return JSON.parseObject(userjson, User.class);
    }

    public static void put(String token, String key, Object data) {
        CacheManager.getCacheProvider().put(token, key, data, expires);
    }

    public static Object get(String token, String key) {
        return CacheManager.getCacheProvider().get(token, key, expires);
    }

    public static void remove(String token){
        CacheManager.getCacheProvider().remove(token);
    }
    public static void remove(String token,String key){
        CacheManager.getCacheProvider().remove(token,key);
    }

}
