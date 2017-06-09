package cn.com.xiaoyaoji.test;

import cn.com.xiaoyaoji.extension.email.EMailUtils;
import org.junit.Test;

/**
 * @author zhoujingjie
 * @date 2016-08-31
 */
public class TestEmail {

    @Test
   public void testCaptcha(){
        EMailUtils.sendCaptcha("1111","");
        System.out.println("ok");
    }
    @Test
   public void testFindpassword(){
        EMailUtils.findPassword("123123","");
        System.out.println("ok");
    }

}
