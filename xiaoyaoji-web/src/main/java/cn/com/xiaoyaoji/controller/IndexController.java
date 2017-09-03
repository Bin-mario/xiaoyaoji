package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.util.CacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * @author zhoujingjie
 * @date 2016-07-21
 */
@RestController
@RequestMapping("/")
@Ignore
public class IndexController {
    @GetMapping(value = {"/login","/login.html"})
    public Object login() {
        return new ModelAndView("/login");
    }

    @GetMapping(value = {"/register","/register.html"})
    public Object register() {
        return new ModelAndView("/register");
    }


    @GetMapping({"/logout","/logout.html"})
    public Object logout(HttpSession session, @CookieValue(name = Constants.TOKEN_COOKIE_NAME,required = false)String token) {
        session.invalidate();
        if(StringUtils.isNoneBlank(token)){
            CacheUtils.remove(token);
        }
        return new ModelAndView("redirect:/");
    }

    @GetMapping(value = {"/about","/about.html"})
    public Object about() {
        return new ModelAndView("/about");
    }


    @GetMapping(value = {"/","/index"})
    public Object index() {
        return new ModelAndView("/index");
    }

    @GetMapping(value = {"/help","/help.html"})
    public Object help() {
        return new ModelAndView("/help");
    }
    @GetMapping(value = "/forget")
    public Object forget() {
        return new ModelAndView("/forget");
    }
    @GetMapping(value = "/findpassword")
    public Object findpassword() {
        return new ModelAndView("/findpassword");
    }
    @GetMapping(value = "/unsupport")
    public Object unsupport() {
        return new ModelAndView("/unsupport");
    }


    @GetMapping(value = "/donate")
    public Object donate() {
        return new ModelAndView("/donate");
    }


    @RequestMapping("/error")
    public Object error(){
        return new ModelAndView("/error");
    }

    @RequestMapping("/error/{code}")
    public Object errorWithCode(@PathVariable("code")String code){
        return new ModelAndView("/error");
    }
}
