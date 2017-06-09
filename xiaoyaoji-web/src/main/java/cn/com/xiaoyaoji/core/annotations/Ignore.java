package cn.com.xiaoyaoji.core.annotations;

import java.lang.annotation.ElementType;

/**
 * 如果在controller中使用，则表示该类或方法不被权限拦截
 * 如果在bean里面使用，则表示某个字段不被修改或新增
 * 如果在参数里面,则表示不执行UserArgumentsResolver
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
public @interface Ignore {
}
