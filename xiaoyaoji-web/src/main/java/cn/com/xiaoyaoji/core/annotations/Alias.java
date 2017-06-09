package cn.com.xiaoyaoji.core.annotations;

/**
 * 用于DbUtils识别表名
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
public @interface Alias {
    String value();
}
