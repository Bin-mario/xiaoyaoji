package cn.com.xiaoyaoji.integration.cache;

/**
 * @author zhoujingjie
 * @date 2016-07-28
 */
public interface CacheProvider {

    void put(String table,String key,Object data,int expires);

    Object get(String token,String key,int expires);

    void remove(String table);

    void remove(String table,String key);


}
