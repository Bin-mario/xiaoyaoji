package cn.com.xiaoyaoji.interceptor;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.exception.NotLoginException;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.util.CacheUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhoujingjie
 * @date 2016-07-22
 */
public class AuthorityInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        if (method.getMethod().getAnnotation(Ignore.class) != null) {
            return true;
        }
        if (method.getMethod().getDeclaringClass().getAnnotation(Ignore.class) != null)
            return true;
        User user = (User) request.getSession().getAttribute("user");
        if(user != null){
            return true;
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for(Cookie cookie:cookies){
                if(Constants.TOKEN_COOKIE_NAME.equals(cookie.getName())){
                    user =CacheUtils.getUser(cookie.getValue());
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                        return true;
                    }
                }
            }
        }
        throw new NotLoginException();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView!=null && modelAndView.getViewName()!=null) {
            if (modelAndView.getModel().get("fileAccess") == null) {
                if(!modelAndView.getViewName().startsWith("redirect:")) {
                    modelAndView.getModel().put("fileAccess", ConfigUtils.getFileAccessURL());
                }
            }
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
