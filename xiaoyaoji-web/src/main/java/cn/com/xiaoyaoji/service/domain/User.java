package cn.com.xiaoyaoji.service.domain;

import cn.com.xiaoyaoji.utils.BeanUtils;

import java.util.Date;

/**
 * 业务场景中需要用到的User
 * @author: zhoujingjie
 * @Date: 17/5/28
 */
public class User {
    private String id;
    private String nickname;
    private String email;
    private String password;
    private String type;
    private String avatar;
    private String status;
    private Date createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public cn.com.xiaoyaoji.data.bean.User toUser(){
        return BeanUtils.convert(cn.com.xiaoyaoji.data.bean.User.class,this);
    }
}
