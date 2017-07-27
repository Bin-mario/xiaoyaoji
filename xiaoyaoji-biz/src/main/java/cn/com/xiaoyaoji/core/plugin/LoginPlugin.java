package cn.com.xiaoyaoji.core.plugin;


import cn.com.xiaoyaoji.data.bean.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录插件
 * @author zhoujingjie
 *         created on 2017/7/24
 */
public interface LoginPlugin extends Plugin {

    /**
     * 登录请求操作
     * @param request
     * @return 如果登录成功返回User。登录失败抛异常或返回null
     */
    User doRequest(HttpServletRequest request);


    /**
     * 回调地址
     * @param action
     * @param pluginInfo
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    void callback(String action,PluginInfo<LoginPlugin> pluginInfo ,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
