package cn.com.xiaoyaoji.test;

import cn.com.xiaoyaoji.api.Config;
import cn.com.xiaoyaoji.api.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * @author zhoujingjie
 * @date 2016-10-10
 */
public class TestPwd {

    @Test
    public void test(){
        System.out.println(DigestUtils.md5Hex("api123456"));
        //System.out.println(StringUtils.password("123456"));
    }
}
