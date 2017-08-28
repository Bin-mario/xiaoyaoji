package cn.com.xiaoyaoji.data.bean;

import cn.com.xiaoyaoji.core.annotations.Alias;
import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.util.JsonUtils;

import java.util.Date;

/**
 * 项目
 * @author zhoujingjie
 * @date 2016-07-13
 */
@Alias("project")
public class Project {
    private String id;
    //项目名称
    private String name;
    //简单描述
    private String description;
    private String userId;
    @Ignore
    private String userName;
    //创建时间
    private Date createTime;
    //状态
    private String status;
    //权限
    private String permission;

    /**
     * //环境 json
     * @see ProjectGlobal
     */
    @Deprecated
    private String environments;
    //详细说明
    private String details;
    //最后更新时间
    private Date lastUpdateTime;
    //是否可编辑
    @Ignore
    private String editable;
    //是否常用项目
    @Ignore
    private String commonlyUsed;


    public interface Status{
        //有效的
        String VALID="VALID";
        //无效
        String INVALID="INVALID";
        //已删除
        String DELETED="DELETED";
        //已归档
        String ARCHIVE="ARCHIVE";
    }

    public interface Permission{
        String PUBLIC="PUBLIC";
        String PRIVATE="PRIVATE";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getEnvironments() {
        return environments;
    }

    public void setEnvironments(String environments) {
        this.environments = environments;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getCommonlyUsed() {
        return commonlyUsed;
    }

    public void setCommonlyUsed(String commonlyUsed) {
        this.commonlyUsed = commonlyUsed;
    }

    public String getExpires(){
        if(lastUpdateTime == null){
            return "1天";
        }
        String time = null;
        int day = (int) ((lastUpdateTime.getTime() - System.currentTimeMillis() ) / 1000 / 60/60/24);
        day += 30;
        if(day <= 0){
            time = "即将";
        }else{
            time = day+"天";
        }
        return time;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}
