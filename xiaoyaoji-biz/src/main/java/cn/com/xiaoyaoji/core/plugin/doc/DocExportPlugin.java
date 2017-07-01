package cn.com.xiaoyaoji.core.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.Plugin;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhoujingjie
 *         created on 2017/7/1
 */
public interface DocExportPlugin extends Plugin {


    void doExport(String projectId, HttpServletResponse response, PluginInfo pluginInfo) throws IOException;

}
