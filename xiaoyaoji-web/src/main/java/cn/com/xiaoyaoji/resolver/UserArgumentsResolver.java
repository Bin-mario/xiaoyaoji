package cn.com.xiaoyaoji.resolver;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.util.CacheUtils;
import cn.com.xiaoyaoji.core.exception.ArgumentRequirementException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: zhoujingjie
 * @Date: 17/4/15
 */
public class UserArgumentsResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(parameter.getParameterAnnotation(Ignore.class) != null){
            return false;
        }
        if (parameter.getParameterType().equals(User.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Cookie[] cookies = servletRequest.getCookies();
        if(cookies != null){
            for(Cookie cookie:cookies){
                if(Constants.TOKEN_COOKIE_NAME.equals(cookie.getName())){
                    User user = CacheUtils.getUser(cookie.getValue());
                    if(user == null){
                        if(parameter.getParameterAnnotation(Ignore.class) != null){
                            throw new ArgumentRequirementException("require parameter user");
                        }
                    }
                    return user;
                }
            }
        }
        //如果方法有ignore注解,则不用强制参数非空。
        if(parameter.getMethod().getAnnotation(Ignore.class) != null){
            return null;
        }
        if(parameter.getMethod().getClass().getAnnotation(Ignore.class) != null){
            return null;
        }
        throw new MissingServletRequestParameterException("user","User");
    }
}
