package cn.com.xiaoyaoji.test;

import cn.com.xiaoyaoji.utils.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhoujingjie
 * @Date: 16/8/21
 */
public class TestCode {

    @Test
    public void test(){
        for(int i=0;i<1000;i++) {
            System.out.println(StringUtils.code(10));
        }
    }

    @Test
    public void testChp(){
        Map map = new HashMap();
        System.out.println(map.get(null));
    }
}
