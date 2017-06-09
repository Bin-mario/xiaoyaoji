package cn.com.xiaoyaoji.extension.cache.factory;

import cn.com.xiaoyaoji.extension.cache.provider.CacheProvider;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public interface CacheFactory {

    CacheProvider create();
}
