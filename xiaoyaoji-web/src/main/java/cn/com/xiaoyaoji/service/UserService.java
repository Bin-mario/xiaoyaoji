package cn.com.xiaoyaoji.service;

import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.User;

/**
 * @author: zhoujingjie
 * @Date: 17/5/28
 */
public class UserService {
    private static UserService userService;
    static {
        userService = new UserService();
    }
    private UserService(){}

    public static UserService instance(){
        return userService;
    }

    public User getUser(String id){
        User user= DataFactory.instance().getById(User.class,id);
        if(user!=null) {
            DataFactory.instance().initUserThirdlyBinds(user);
        }
        return user;
    }
}
