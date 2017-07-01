package cn.com.xiaoyaoji.utils;

/**
 * @author zhoujingjie
 *         created on 2017/5/18
 */
public class ClassUtils {
    public static Object newInstance(String className){
        try {
            return Class.forName(className).newInstance();
        } catch (InstantiationException |IllegalAccessException |ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T> T newInstance(String className,Class<T> clazz){
        return (T) newInstance(className);
    }

}
