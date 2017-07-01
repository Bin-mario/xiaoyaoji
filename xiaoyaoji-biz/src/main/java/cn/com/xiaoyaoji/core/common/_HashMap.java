package cn.com.xiaoyaoji.core.common;

import java.util.HashMap;

/**
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
public class _HashMap<K,V> extends HashMap<K,V> {

    public _HashMap<K,V> add(K key,V value){
        put(key,value);
        return this;
    }
}
