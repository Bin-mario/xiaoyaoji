package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.util.PluginUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: zhoujingjie
 * @Date: 17/4/11
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Ignore
    @RequestMapping
    public void load(@RequestParam String path,@RequestParam String id,
                     HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        path = path.replace("..","");
        id = id.replace("..","");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PluginInfo info = PluginManager.getInstance().getPluginInfo(id);
        if(info == null){
            response.setStatus(503);
            response.getWriter().write("plugin not found");
        }else {
            request.getRequestDispatcher(PluginUtils.getPluginSourceDir() + info.getRuntimeFolder() + "/web/" + path).forward(request, response);
        }
    }


    @Ignore
    @RequestMapping("/assets/{id}")
    public void assets(@RequestParam String path,@PathVariable("id") String pluginId,
                       HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
