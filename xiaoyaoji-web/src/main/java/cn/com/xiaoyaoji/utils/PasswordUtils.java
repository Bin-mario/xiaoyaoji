package cn.com.xiaoyaoji.utils;

import cn.com.xiaoyaoji.Config;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author zhoujingjie
 *         created on 2017/7/1
 */
public class PasswordUtils {
    public static String password(String password){
        return DigestUtils.md5Hex(Config.SALT +password);
    }
}
