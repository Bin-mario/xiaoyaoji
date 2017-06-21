package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.utils.ConfigUtils;

/**
 * @author zhoujingjie
 * @date 2016-07-12
 */
public class Config {

    public static final String SALT = ConfigUtils.getSalt();

    public static final String PLUGINS_DIR=ConfigUtils.getProperty("xyj.plugin.dir");
    //解压后的位置
    //public static final String PLUGINS_SOURCE_DIR="/WEB-INF/plugins/";
    public static final String PLUGINS_SOURCE_DIR="/WEB-INF/plugins/";
}
