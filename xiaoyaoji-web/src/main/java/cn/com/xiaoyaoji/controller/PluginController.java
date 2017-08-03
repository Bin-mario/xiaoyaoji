package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.plugin.Event;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.entity.MapParameter;
import cn.com.xiaoyaoji.util.PluginUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    private void checkAuthorization(HttpSession session){
        AssertUtils.isTrue(session.getAttribute("plugin.config.key")!=null,"无操作权限");
    }
    @Ignore
    @GetMapping("/config")
    public ModelAndView config(@RequestParam(value = "id",required = false) String pluginId,
                               @RequestParam(value = "action",required = false) String action,
                               @RequestParam(value = "key",required = false)String key,HttpSession session
                               ){
        String _key = (String) session.getAttribute("plugin.config.key");
        if(_key == null) {
            AssertUtils.notNull(key, "无操作权限");
            AssertUtils.isTrue(key.equals(ConfigUtils.getProperty("plugin.config.key")),"无操作权限");
            session.setAttribute("plugin.config.key",key);
        }
        PluginInfo info = null;
        Map<Event,List<PluginInfo>> pluginInfos = PluginManager.getInstance().getPluginInfos();
        if(pluginId != null) {
            if("reload".equals(action)){
                PluginUtils.reloadPlugin(pluginId);
            }else if("unload".equals(action)){
                PluginUtils.destroyPlugin(pluginId);
            }else if("delete".equals(action)){
                PluginUtils.deletePlugin(pluginId);
            }
            info = PluginManager.getInstance().getPluginInfo(pluginId);
        }
        return new ModelAndView("/plugin/config")
                .addObject("config",info!=null?info.getConfig():null)
                .addObject("pluginInfos",pluginInfos)
                .addObject("pluginId",pluginId);
    }

    @Ignore
    @PostMapping("/config")
    public ModelAndView updateConfig(@RequestParam("pluginId") String pluginId,
                                     MapParameter mapParameter, HttpSession session){
        checkAuthorization(session);
        PluginInfo info = PluginManager.getInstance().getPluginInfo(pluginId);
        AssertUtils.notNull(info,"插件不存在");
        info.setConfig(mapParameter.getMap());
        info.getPlugin().init();
        return new ModelAndView("redirect:/plugin/config?id="+pluginId);
    }

    @Ignore
    @PostMapping("/upload")
    public ModelAndView uploadPlugin(HttpServletRequest request,@RequestParam("file") MultipartFile file) throws Exception {
        checkAuthorization(request.getSession());
        AssertUtils.notNull(file,"请上传文件");
        AssertUtils.isTrue(file.getSize()>0,"请上传正确的文件");
        String fileName = file.getOriginalFilename();
        AssertUtils.isTrue(fileName.endsWith(".zip"),"请上传application/zip");
        String outputDir = request.getServletContext().getResource(PluginUtils.getPluginSourceDir()).toURI().getPath();
        File newfile = new File(outputDir+File.separator+fileName);
        if(newfile.exists()){
            newfile = new File(outputDir+File.separator+ StringUtils.code(3)+"_"+fileName);
        }
        File output = new File(newfile.getAbsolutePath().replace(".zip",""));
        file.transferTo(newfile);
        PluginUtils.extractPlugin(newfile,output.getAbsolutePath());
        PluginUtils.loadPlugin(output);
        return new ModelAndView("redirect:/plugin/config");
    }


}
