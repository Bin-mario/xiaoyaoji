package cn.com.xiaoyaoji.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
public class ConfigUtils {
    private static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    private static Properties properties;
    static {
        properties = new Properties();
        ClassLoader classLoader =Thread.currentThread().getContextClassLoader();
        try {
            properties.load(classLoader.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        try {
            properties.load(classLoader.getResourceAsStream("config.dev.properties"));
        } catch (Exception e) {
            logger.info("not found config.dev.properties");
        }
    }
    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    public static String getProperty(String key,String defaultValue){
        String value = getProperty(key);
        if(value == null || value.length()==0)
            return defaultValue;
        return value;
    }


    public static String getFileAccessURL(){
        return properties.getProperty("file.access.url");
    }

    public static String getBucketURL(){
        return properties.getProperty("file.qiniu.bucket");
    }

    public static String getUploadServer(){
        return properties.getProperty("file.upload.server","owner");
    }

    public static String getJdbcURL(){
        return properties.getProperty("jdbc.url");
    }
    public static String getJdbcUsername(){
        return properties.getProperty("jdbc.username");
    }
    public static String getJdbcPassword(){
        return properties.getProperty("jdbc.password");
    }
    public static String getJdbcDriverclass(){
        return properties.getProperty("jdbc.driverclass");
    }

    public static String getJdbcInitSize(){
        return properties.getProperty("jdbc.initsize");
    }
    public static String getJdbcMaxWait(){
        return properties.getProperty("jdbc.maxwait");
    }
    public static String getJdbcMinIdle(){
        return properties.getProperty("jdbc.minidle");
    }

    public static String getQiniuAccessKey(){
        return properties.getProperty("file.qiniu.accessKey");
    }

    public static String getQiniuSecretKey(){
        return properties.getProperty("file.qiniu.secretKey");
    }

    public static String getSalt(){
        return properties.getProperty("salt");
    }

    public static int getTokenExpires() {
        return Integer.parseInt(properties.getProperty("token.expires"));
    }
}
