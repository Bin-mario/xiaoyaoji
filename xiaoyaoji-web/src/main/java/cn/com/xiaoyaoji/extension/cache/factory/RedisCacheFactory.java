package cn.com.xiaoyaoji.extension.cache.factory;

import cn.com.xiaoyaoji.extension.cache.provider.CacheProvider;
import cn.com.xiaoyaoji.extension.cache.provider.RedisCacheProvider;
import cn.com.xiaoyaoji.utils.ConfigUtils;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class RedisCacheFactory implements CacheFactory {
    @Override
    public CacheProvider create() {
        return new RedisCacheProvider(ConfigUtils.getProperty("redis.host"),
                Integer.parseInt(ConfigUtils.getProperty("redis.port")),
                Integer.parseInt(ConfigUtils.getProperty("redis.connection.timeout")),
                ConfigUtils.getProperty("redis.password")
        );
    }
}
