package cn.com.xiaoyaoji.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/7/31
 */
public class MapParameter {
    private Map<String,String> map;

    public Map<String, String> getMap() {
        if(map == null){
            map = new HashMap<>();
        }
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
