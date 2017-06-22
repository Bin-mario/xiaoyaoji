package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.Config;
import cn.com.xiaoyaoji.core.annotations.Ignore;
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
        request.getRequestDispatcher(Config.PLUGINS_SOURCE_DIR+id+"/web/"+path).forward(request,response);
    }
}
