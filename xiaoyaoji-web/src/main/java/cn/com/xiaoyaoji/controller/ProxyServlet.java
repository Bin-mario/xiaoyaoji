package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.util.PluginUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhoujingjie
 *         created on 2017/8/2
 */
public class ProxyServlet extends HttpServlet{

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String prefix = request.getServletContext().getContextPath()+"/proxy/";
        String uri = request.getRequestURI().replace(prefix,"");
        String pluginId = uri.substring(0,uri.indexOf("/"));
        String path = uri.replace(pluginId,"");
        path = path.replace("..","");
        PluginInfo info = PluginManager.getInstance().getPluginInfo(pluginId);
        if(info == null){
            response.setStatus(503);
            response.getWriter().write("plugin not found");
        }else {
            request.getRequestDispatcher(PluginUtils.getPluginSourceDir() + info.getRuntimeFolder() + path).forward(request, response);
        }
    }
}
