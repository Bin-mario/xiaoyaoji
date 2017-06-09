package cn.com.xiaoyaoji.data.bean;

import cn.com.xiaoyaoji.core.annotations.Alias;
import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 内容
 * @author: zhoujingjie
 * @Date: 17/4/1
 */
@Alias("doc")
public class Doc {
    //
    private String id;
    //名称
    private String name;
    //排序
    private Integer sort;
    //类型
    private String type;
    //内容
    private String content;
    //创建时间
    private Date createTime;
    //最后更新时间
    private Date lastUpdateTime;
    //项目id
    private String projectId;
    //
    private String parentId;
    @Ignore
    private List<Doc> children;
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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<Doc> getChildren() {
        if(children == null){
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<Doc> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }

}
