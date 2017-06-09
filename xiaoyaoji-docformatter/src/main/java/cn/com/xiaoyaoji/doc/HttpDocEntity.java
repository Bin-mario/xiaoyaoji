package cn.com.xiaoyaoji.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhoujingjie
 * @Date: 17/6/6
 */
public class HttpDocEntity {
    //字段名称
    private String name;
    //是否必须
    private String required;
    //默认值
    private String defaultValue;
    //描述
    private String description;
    //类型
    private String type;
    //子节点
    private List<HttpDocEntity> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HttpDocEntity> getChildren() {
        if(children == null){
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<HttpDocEntity> children) {
        this.children = children;
    }
}
