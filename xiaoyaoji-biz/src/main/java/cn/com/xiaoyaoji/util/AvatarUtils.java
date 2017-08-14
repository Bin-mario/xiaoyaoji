package cn.com.xiaoyaoji.util;

import cn.com.xiaoyaoji.core.util.ConfigUtils;

/**
 * @author zhoujingjie
 *         created on 2017/8/14
 */
public class AvatarUtils {

    public static String getAvatar(String avatar){
        if(avatar == null || avatar.length() == 0)
            return avatar;
        if(avatar.startsWith("http")){
            return avatar;
        }
        return ConfigUtils.getProperty("file.access.url")+avatar;
    }
}
