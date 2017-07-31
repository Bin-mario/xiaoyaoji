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
public abstract class LoginPlugin extends Plugin<LoginPlugin> {

    /**
     * 登录请求操作
     * @param request
     * @return 如果登录成功返回User。登录失败抛异常或返回null
     */
    public abstract User doRequest(HttpServletRequest request);


    public abstract String getOpenURL();
    /**
     * 回调地址
     * @param action
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public abstract void callback(String action ,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
