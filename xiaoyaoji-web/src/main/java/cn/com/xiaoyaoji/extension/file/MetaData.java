package cn.com.xiaoyaoji.extension.file;


import cn.com.xiaoyaoji.core.common.FileType;

/**
 * @author: zhoujingjie
 * @Date: 17/5/2
 */
public class MetaData {
    private String path;
    private long size;
    private FileType type;

    public MetaData() {
    }

    public MetaData(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
