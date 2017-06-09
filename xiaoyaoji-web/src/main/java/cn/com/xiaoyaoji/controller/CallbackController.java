package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.extension.thirdly.Github;
import cn.com.xiaoyaoji.extension.thirdly.QQ;
import cn.com.xiaoyaoji.extension.thirdly.Weibo;
import cn.com.xiaoyaoji.extension.thirdly.github.User;
import cn.com.xiaoyaoji.extension.thirdly.qq.AccessToken;
import cn.com.xiaoyaoji.utils.ConfigUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhoujingjie
 * @date 2016-06-03
 */
@RestController
@RequestMapping("callback")
public class CallbackController {
    private static Logger logger = Logger.getLogger(CallbackController.class);
    private Set<String> states = new HashSet<String>() {{
        add("login");
        add("relation");
    }};

    @Ignore
    @GetMapping(value = "qq")
    public ModelAndView qqCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        logger.info("callback qq -> code:" + code + " state:" + state);
        if (states.contains(state)) {
            QQ qq = new QQ();
            AccessToken accessToken = qq.getAccessToken(code, ConfigUtils.getProperty("qq.redirect_uri"));
            String openId = qq.getOpenid(accessToken.getAccess_token());
            return new ModelAndView("/third-party")
                    .addObject("openId", openId)
                    .addObject("type", "qq")
                    .addObject("state", state)
                    .addObject("accessToken", accessToken.getAccess_token());
        }

        return illegalView();
    }

    private ModelAndView illegalView() {
        ModelAndView view = new ModelAndView("/illegal");
        view.addObject("exception", "非法请求");
        return view;
    }


    @Ignore
    @GetMapping(value = "weibo")
    public Object weibo(@RequestParam("code") String code, @RequestParam("state") String state) {
        logger.info("callback weibo -> code:" + code + " state:" + state);
        if (states.contains(state)) {
            Weibo weibo = new Weibo();
            cn.com.xiaoyaoji.extension.thirdly.weibo.AccessToken accessToken = weibo.getAccessToken(ConfigUtils.getProperty("weibo.appkey"), ConfigUtils.getProperty("weibo.appsecret"), code, ConfigUtils.getProperty("weibo.redirect_uri"));
            return new ModelAndView("/third-party")
                    .addObject("type", "weibo")
                    .addObject("state", state)
                    .addObject("accessToken", accessToken.getAccess_token())
                    .addObject("uid", accessToken.getUid());


        }
        return illegalView();
    }

    @Ignore
    @GetMapping(value = "weibo/cancel")
    public Object weiboCancel() {
        logger.info("callback weibo cancel");
        return null;
    }


    @Ignore
    @GetMapping(value = "github")
    public Object github(@RequestParam("code") String code, @RequestParam("state") String state) {
        logger.info("callback github -> code:" + code + " state:" + state);
        if (states.contains(state)) {
            Github github = new Github();
            cn.com.xiaoyaoji.extension.thirdly.AccessToken accessToken = github.getAccessToken(ConfigUtils.getProperty("github.clientid"), ConfigUtils.getProperty("github.secret"), code, ConfigUtils.getProperty("github.redirect_uri"));
            User user = github.getUser(accessToken.getAccess_token());
            return new ModelAndView("/third-party")
                    .addObject("type", "github")
                    .addObject("gitid", user.getId())
                    .addObject("state", state)
                    .addObject("accessToken", accessToken.getAccess_token());
        }
        return illegalView();
    }


}
