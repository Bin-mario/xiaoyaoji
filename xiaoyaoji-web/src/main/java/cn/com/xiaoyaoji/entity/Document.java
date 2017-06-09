package cn.com.xiaoyaoji.entity;

/**
 * @author: zhoujingjie
 * @Date: 17/4/16
 */
public class Document {
    private String type;
    private String name;

    public Document(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
