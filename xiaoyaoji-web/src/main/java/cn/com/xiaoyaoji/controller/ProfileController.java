package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.utils.ConfigUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 个人中心
 * @author: zhoujingjie
 * @Date: 17/5/22
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @RequestMapping
    public ModelAndView index(User user){
        return new ModelAndView("/profile/index")
                .addObject("pageName","index")
                .addObject("user",user)
                .addObject("fileAccess", ConfigUtils.getFileAccessURL())
                ;
    }

    @RequestMapping("relation")
    public ModelAndView relation(User user){
        return new ModelAndView("/profile/relation").addObject("user",user)
                .addObject("pageName","relation")
                .addObject("fileAccess", ConfigUtils.getFileAccessURL())
                ;
    }

    @RequestMapping("security")
    public ModelAndView security(User user){
        return new ModelAndView("/profile/security").addObject("user",user)
                .addObject("pageName","security")
                .addObject("fileAccess", ConfigUtils.getFileAccessURL())
                ;
    }


}

