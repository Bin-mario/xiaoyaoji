package cn.com.xiaoyaoji.controller;

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
    public void load(@RequestParam String plugin, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        plugin = plugin.replace("..","");
        request.getRequestDispatcher("/WEB-INF/plugins"+plugin).forward(request,response);
    }
}
