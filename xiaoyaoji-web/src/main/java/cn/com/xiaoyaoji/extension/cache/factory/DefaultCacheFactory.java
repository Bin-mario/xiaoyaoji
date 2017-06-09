package cn.com.xiaoyaoji.extension.cache.factory;

import cn.com.xiaoyaoji.extension.cache.provider.CacheProvider;
import cn.com.xiaoyaoji.extension.cache.provider.MemoryCacheProvider;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class DefaultCacheFactory implements CacheFactory{
    @Override
    public CacheProvider create() {
        return new MemoryCacheProvider();
    }
}
