package cn.com.xiaoyaoji.api.data.bean;

import cn.com.xiaoyaoji.api.annotations.Alias;
import cn.com.xiaoyaoji.api.annotations.Ignore;

import java.util.Date;

/**
 * 项目操作记录
 * @author zhoujingjie
 * @date 2016-07-13
 */
@Alias("project_log")
public class ProjectLog {
    private String id;
    private String userId;
    private Date createTime;
    private String log;
    private String action;
    private String projectId;
    @Ignore
    private String nickname;
    @Ignore
    private String avatar;

    public ProjectLog() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
