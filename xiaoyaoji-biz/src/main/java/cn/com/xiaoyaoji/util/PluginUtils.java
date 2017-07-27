package cn.com.xiaoyaoji.util;

import cn.com.xiaoyaoji.core.util.ConfigUtils;

/**
 * @author zhoujingjie
 *         created on 2017/7/24
 */
public class PluginUtils {

    public static String getPluginDir(){
        return ConfigUtils.getProperty("xyj.plugin.dir");
    }

    public static String getPluginSourceDir(){
        return ConfigUtils.getProperty("xyj.plugin.source.dir","/WEB-INF/plugins/");
    }
}
