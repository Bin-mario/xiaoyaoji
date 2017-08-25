package cn.com.xiaoyaoji.test;

import cn.com.xiaoyaoji.core.util.StringUtils;
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
        System.out.println("2_0".compareTo("2_0_1"));
        System.out.println("2_1".compareTo("2_0_1"));
    }
}
