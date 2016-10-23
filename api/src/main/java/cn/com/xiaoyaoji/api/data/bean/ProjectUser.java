package cn.com.xiaoyaoji.api.data.bean;

import cn.com.xiaoyaoji.api.annotations.Alias;

import java.util.Date;

/**
 * @author zhoujingjie
 * @date 2016-07-20
 */
@Alias("project_user")
public class ProjectUser {
    private String id;
    private String projectId;
    private String userId;
    private Date createTime;
    private String status;
    private String editable;
    private String commonlyUsed;

    public interface Status{
        String PENDING="PENDING";
        String ACCEPTED="ACCEPTED";
        String REFUSED="REFUSED";
    }

    public interface Editable {
        String YES="YES";
        String NO="NO";
    }
    public interface CommonlyUsed{
        String YES="YES";
        String NO="NO";
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommonlyUsed() {
        return commonlyUsed;
    }

    public void setCommonlyUsed(String commonlyUsed) {
        this.commonlyUsed = commonlyUsed;
    }
}
