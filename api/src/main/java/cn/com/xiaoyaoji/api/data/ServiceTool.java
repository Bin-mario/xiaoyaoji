package cn.com.xiaoyaoji.api.data;

import cn.com.xiaoyaoji.api.data.bean.User;
import cn.com.xiaoyaoji.api.service.ServiceFactory;
import cn.com.xiaoyaoji.api.utils.AssertUtils;
import cn.com.xiaoyaoji.api.utils.MemoryUtils;
import org.mangoframework.core.dispatcher.Parameter;

/**
 * @author zhoujingjie
 * @date 2016-10-20
 */
public class ServiceTool {

    public static void checkUserHasEditPermission(String projectId,Parameter parameter){
        User user = MemoryUtils.getUser(parameter);
        AssertUtils.notNull(user,"无操作权限");
        boolean permission = ServiceFactory.instance().checkUserHasProjectPermission(user.getId(),projectId);
        AssertUtils.isTrue(permission,"无操作权限");
    }
}
