package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.common.Constants;
import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.extension.thirdly.Github;
import cn.com.xiaoyaoji.utils.ConfigUtils;
import cn.com.xiaoyaoji.extension.cache.CacheUtils;
import cn.com.xiaoyaoji.utils.StringUtils;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.data.bean.Thirdparty;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.extension.thirdly.QQ;
import cn.com.xiaoyaoji.extension.thirdly.Weibo;
import cn.com.xiaoyaoji.extension.thirdly.qq.UserInfo;
import cn.com.xiaoyaoji.utils.AssertUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhoujingjie
 * @date 2016-06-03
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    private Cookie setCookie(String token,User user){
        CacheUtils.putUser(token, user);
        Cookie cookie = new Cookie(Constants.TOKEN_COOKIE_NAME,token);
        cookie.setPath("/");
        cookie.setMaxAge(ConfigUtils.getTokenExpires());
        return cookie;
    }


    @Ignore
    @PostMapping()
    public Object login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        AssertUtils.notNull(email, "用户名为空");
        AssertUtils.notNull(password, "密码为空");
        password = StringUtils.password(password);
        User user = ServiceFactory.instance().login(email, password);
        AssertUtils.notNull(user, "用户名或密码错误");
        if (user.getStatus().equals(User.Status.INVALID)) {
            return new Result(Result.ERROR, "invalid status");
        }
        String token = CacheUtils.token();
        response.addCookie(setCookie(token,user));
        return true;
    }

    @Ignore
    @PostMapping("/qq")
    public Object thirdlyQQ(@RequestParam(required = false) String openId,@RequestParam(required = false) String accessToken,HttpServletResponse response) {
        AssertUtils.notNull(openId, "missing openId");
        AssertUtils.notNull(accessToken, "missing accessToken");
        UserInfo userInfo = new QQ().getUserInfo(openId, accessToken);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(openId);
        thirdparty.setLogo(userInfo.getFigureurl_qq_2());
        thirdparty.setNickName(userInfo.getNickname());
        thirdparty.setType(Thirdparty.Type.QQ);
        User user = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(user,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        //MemoryUtils.putUser(parameter, user);
        AssertUtils.isTrue(!User.Status.INVALID.equals(user.getStatus()), "invalid status");
        String token = CacheUtils.token();
        response.addCookie(setCookie(token,user));
        return new _HashMap<>()
                .add("token",token);
    }

    @Ignore
    @PostMapping("/weibo")
    public Object thirdlyWeibo(@RequestParam(required = false) String uid,@RequestParam(required = false) String accessToken,HttpServletResponse response) {
        AssertUtils.notNull(uid, "missing uid");
        AssertUtils.notNull(accessToken, "missing accessToken");
        cn.com.xiaoyaoji.extension.thirdly.weibo.User weiboUser = new Weibo().showUser(accessToken, uid);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(weiboUser.getId());
        thirdparty.setLogo(weiboUser.getAvatar_large());
        thirdparty.setNickName(weiboUser.getScreen_name());
        thirdparty.setType(Thirdparty.Type.WEIBO);
        User user = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(user,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        //MemoryUtils.putUser(parameter, user);
        AssertUtils.isTrue(!User.Status.INVALID.equals(user.getStatus()), "invalid status");
        String token = CacheUtils.token();
        response.addCookie(setCookie(token,user));
        return new _HashMap<>()
                .add("token",token);
    }

    @Ignore
    @PostMapping("/github")
    public Object thirdPartyGithub(@RequestParam(required = false) String accessToken,HttpServletResponse response){
        AssertUtils.notNull(accessToken, "missing accessToken");
        Github github = new Github();
        cn.com.xiaoyaoji.extension.thirdly.github.User user = github.getUser(accessToken);
        Thirdparty thirdparty = new Thirdparty();
        thirdparty.setId(user.getId());
        thirdparty.setLogo(user.getAvatar_url());
        thirdparty.setNickName(user.getName());
        thirdparty.setType(Thirdparty.Type.WEIBO);
        thirdparty.setEmail(user.getEmail());
        User loginUser = ServiceFactory.instance().loginByThirdparty(thirdparty);
        AssertUtils.notNull(loginUser,"该账户暂未绑定小幺鸡账户,请绑定后使用");
        AssertUtils.isTrue(!User.Status.INVALID.equals(loginUser.getStatus()), "invalid status");
        String token = CacheUtils.token();
        response.addCookie(setCookie(token,loginUser));
        return new _HashMap<>()
                .add("token",token);
    }



}
