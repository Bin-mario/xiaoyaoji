package cn.com.xiaoyaoji.service;

import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.util.CacheUtils;

/**
 * @author zhoujingjie
 * @date 2016-10-20
 */
public class ServiceTool {

    public static void checkUserHasEditPermission(String projectId, String token) {
        User user = CacheUtils.getUser(token);
        checkUserHasEditPermission(projectId,user);
    }

    /**
     * 检查是否有编辑权限
     * @param projectId
     * @param user
     */
    public static void checkUserHasEditPermission(String projectId, User user) {
        AssertUtils.notNull(user, "无操作权限");
        boolean permission = ServiceFactory.instance().checkUserHasProjectEditPermission(user.getId(), projectId);
        AssertUtils.isTrue(permission, "无操作权限");
    }

    /**
     * 检查是否是拥有者
     * @param project
     * @see
     */
    public static void checkUserIsOwner(Project project, User user) {
        AssertUtils.notNull(user, "无操作权限");
        AssertUtils.notNull(project, "项目不存在");
        AssertUtils.isTrue(user.getId().equals(project.getUserId()), "无操作权限");
    }


    /**
     * 检查是否有访问权限
     * @param projectId
     * @param user
     */
    public static void checkUserHasAccessPermission(String projectId,User user){
        //判断项目是否公开
        if(!ProjectService.instance().checkProjectIsPublic(projectId)){
            AssertUtils.notNull(user,"无访问权限");
            //判断用户是否有操作权限
            AssertUtils.isTrue(ServiceFactory.instance().checkProjectUserExists(projectId, user.getId()),"无访问权限");
        }
    }

    public static void checkUserHasAccessPermission(Project project,User user){
        if(project != null){
            if(!Project.Permission.PUBLIC.equals(project.getPermission())){
                AssertUtils.notNull(user,"无操作权限");
                checkUserIsMember(project, user);
            }
        }
    }

    /**
     * 检查用户是否是成员
     * @param project
     * @param user
     */
    public static void checkUserIsMember(Project project,User user){
        AssertUtils.isTrue(ServiceFactory.instance().checkProjectUserExists(project.getId(), user.getId()),"无访问权限");
    }

    public static boolean modified(String before, String now) {
        if (now != null && !now.equals(before))
            return true;
        return false;
    }
}
