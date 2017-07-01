package cn.com.xiaoyaoji.core.common;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public enum FileType {
    PDF,
    MARKDOWN,
    IMG,
    FILE,
    RAP,
    POSTMAN,
    DOC,
    XLS;
    public static FileType parse(String type){
        if(type == null)
            return null;
        type = type.toUpperCase();
        for(FileType fileType:FileType.values()){
            if(fileType.name().equals(type)){
                return fileType;
            }
        }
        return null;
    }

}
