package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.task.SiteMapTask;
import cn.com.xiaoyaoji.util.PluginUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.util.Date;

/**
 * @author zhoujingjie
 * @date 2016-07-26
 */
public class Application {
    private static Logger logger = Logger.getLogger(Application.class);

    //todo 清理回收站超过30天的项目
    public static void started(ServletContext servletContext) {
        //MessageBus.instance().register(new MessageNotify());
        initializeBeanUtilsConfig();

        initializePlugins(servletContext);

        if("true".equals(ConfigUtils.getProperty("xyj.sitemap.enable"))) {
            SiteMapTask.start(servletContext);
        }
    }

    /**
     * beanutils 日期格式化
     */
    private static void initializeBeanUtilsConfig(){
        DateConverter converter = new DateConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        ConvertUtils.register(converter, Date.class);
    }

    /**
     * 初始化插件
     * @param servletContext
     */
    private static void initializePlugins(ServletContext servletContext){
        try {
            String outputURI =servletContext.getRealPath(PluginUtils.getPluginSourceDir());

            String pluginsDir = PluginUtils.getPluginDir();
            //如果为空则与sourcedir 同目录
            if( pluginsDir == null || pluginsDir.length() == 0 ){
                pluginsDir = outputURI;
            }
            PluginUtils.extractPlugins(pluginsDir,outputURI);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }


}
