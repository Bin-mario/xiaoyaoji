package cn.com.xiaoyaoji.utils;

/**
 * @author zhoujingjie
 * @date 2016-05-12
 */
public class AssertUtils {

    public static void notNull(Object data, String errorMsg) {
        if (data == null)
            throw new IllegalArgumentException(errorMsg);
        if (data instanceof String) {
            if (org.apache.commons.lang3.StringUtils.isBlank((String) data)) {
                throw new IllegalArgumentException(errorMsg);
            }
        }
    }

    public static void notNull(String errorMsg, Object... data) {

        for (Object obj : data) {
            notNull(obj, errorMsg);
        }
    }

    public static void isTrue(boolean expression, String errorMsg) {
        if (!expression)
            throw new IllegalArgumentException(errorMsg);
    }

    public static void error(String errorMsg) {
        throw new IllegalArgumentException(errorMsg);
    }

    public static void result(boolean rs, String errorMsg) {
        if (!rs)
            throw new IllegalArgumentException(errorMsg);
    }

}
