package cn.com.xiaoyaoji;

import cn.com.xiaoyaoji.util.PluginUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
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

    public static void started(ServletContext servletContext) {
        //MessageBus.instance().register(new MessageNotify());
        initBeanUtilsConfig();
        //todo 清理回收站超过30天的项目
        //todo 功能模块化

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

    /**
     * beanutils 日期格式化
     */
    private static void initBeanUtilsConfig(){
        DateConverter converter = new DateConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        ConvertUtils.register(converter, Date.class);
    }


}
