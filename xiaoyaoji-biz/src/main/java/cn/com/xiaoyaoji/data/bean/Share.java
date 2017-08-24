package cn.com.xiaoyaoji.data.bean;

import cn.com.xiaoyaoji.core.annotations.Alias;
import cn.com.xiaoyaoji.core.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujingjie
 * @date 2016-10-20
 */
@Alias("share")
public class Share {
    private String id;
    private String name;
    private String userId;
    @Ignore
    private String username;
    private Date createTime;
    private String shareAll;
    @Deprecated
    private String moduleIds;

    //分享id
    private String docIds;
    private String password;
    private String projectId;
    @Ignore
    private String docNames;

    public interface ShareAll{
        String YES="YES";
        String NO="NO";
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

    public String getShareAll() {
        return shareAll;
    }

    public void setShareAll(String shareAll) {
        this.shareAll = shareAll;
    }

    public String getModuleIds() {
        return moduleIds;
    }
    public String[] getModuleIdsArray(){
        if(StringUtils.isNotBlank(moduleIds)){
            return moduleIds.split(",");
        }
        return new String[]{};
    }

    public String[] getDocIdsArray(){
        if(StringUtils.isNotBlank(docIds)){
            return docIds.split(",");
        }
        return new String[]{};
    }

    public void setModuleIds(String moduleIds) {
        this.moduleIds = moduleIds;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDocIds() {
        return docIds;
    }

    public void setDocIds(String docIds) {
        this.docIds = docIds;
    }

    public String getDocNames() {
        return docNames;
    }

    public void setDocNames(String docNames) {
        this.docNames = docNames;
    }
}
