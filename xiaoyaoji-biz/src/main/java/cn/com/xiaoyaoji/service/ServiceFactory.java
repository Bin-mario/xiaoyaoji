package cn.com.xiaoyaoji.service;

import cn.com.xiaoyaoji.core.common.Pagination;
import cn.com.xiaoyaoji.core.util.ResultUtils;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.integration.file.FileManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * @author: zhoujingjie
 * @Date: 16/5/15
 */
public class ServiceFactory {
    private static Logger logger = Logger.getLogger(ServiceFactory.class);
    private static ServiceFactory instance;

    static {
        instance = new ServiceFactory();
    }

    public static ServiceFactory instance() {
        return instance;
    }

    public int create(Object instance) {
        return DataFactory.instance().insert(instance);
    }

    public int update(Object instance) {
        return DataFactory.instance().update(instance);
    }

    public int delete(Object instance) {
        return DataFactory.instance().delete(instance);
    }

    public int delete(String tableName, String id) {
        return DataFactory.instance().delete(tableName, id);
    }

    /**
     * 删除数据与图片
     *
     * @param tableName
     * @param id
     * @param imgFields
     * @return
     */
    public int deleteWithImage(String tableName, String id, String... imgFields) {
        Map<String, Object> map = DataFactory.instance().getById(tableName, id);
        if (map == null)
            return 0;
        if (imgFields != null && imgFields.length > 0) {
            for (String field : imgFields) {
                try {
                    FileManager.getFileProvider().delete((String) map.get(field));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return DataFactory.instance().delete(tableName, id);
    }

    public int updateAndImage(Object instance, String... imgKeys) {
        return DataFactory.instance().updateAndImage(instance, imgKeys);
    }

    public User login(String email, String password) {
        User user = DataFactory.instance().login(email, password);
        if (user != null) {
            initUserThirdlyBinds(user);
            user.setPassword(null);
        }
        return user;
    }

    public User loginByThirdparty(Thirdparty thirdparty) {
        User user = DataFactory.instance().getUserByThirdId(thirdparty.getId());
        if (user != null) {
            user.setPassword(null);
            initUserThirdlyBinds(user);
        } else {
            user = new User();
            user.setId(StringUtils.id());
            user.setNickname(thirdparty.getNickName());
            user.setCreatetime(new Date());
            user.setType(User.Type.USER);
            user.setAvatar(thirdparty.getLogo());
            user.setStatus(User.Status.VALID);
            create(user);
            thirdparty.setUserId(user.getId());
            bindUserWithThirdParty(thirdparty);
            user.getBindingMap().put(thirdparty.getId(),true);
        }
        return user;
    }

    public int bindUserWithThirdParty(Thirdparty thirdparty) {
        return DataFactory.instance().bindUserWithThirdParty(thirdparty);
    }


    public List<Module> getModules(String projectId) {
        //return ResultUtils.list(DataFactory.instance().getModules(projectId));
        return null;
    }

    public List<Team> getTeams(String userId) {
        return ResultUtils.list(DataFactory.instance().getTeams(userId));
    }

    public List<Project> getProjects(Pagination pagination) {
        return ResultUtils.list(DataFactory.instance().getProjects(pagination));
    }


    public List<User> getUsersByProjectId(String projectId) {
        return ResultUtils.list(DataFactory.instance().getUsersByProjectId(projectId));
    }

    public List<User> getAllProjectUsersByUserId(String userId) {
        return ResultUtils.list(DataFactory.instance().getAllProjectUsersByUserId(userId));
    }


    public int deleteTeam(String id) {
        return DataFactory.instance().deleteTeam(id);
    }

    public int deleteProject(String id) {
        return DataFactory.instance().deleteProject(id);
    }

    public List<User> searchUsers(String key, String... excludeIds) {
        return ResultUtils.list(DataFactory.instance().searchUsers(key, excludeIds));
    }

    /**
     * @param id
     * @return
     * @see ProjectService#getProject(String)
     */
    @Deprecated
    public Project getProject(String id) {
        return DataFactory.instance().getById(Project.class, id);
    }

    public boolean checkEmailExists(String email) {
        return DataFactory.instance().checkEmailExists(email);
    }

    public boolean checkProjectUserExists(String projectId, String userId) {
        return DataFactory.instance().checkProjectUserExists(projectId, userId);
    }

    public int deleteProjectUser(String projectId, String userId) {
        return DataFactory.instance().deleteProjectUser(projectId, userId);
    }


    public List<Doc> getDocsByProjectId(String projectId) {
        return ResultUtils.list(DataFactory.instance().getDocsByProjectId(projectId));
    }

    public String getUserIdByEmail(String email) {
        return DataFactory.instance().getUserIdByEmail(email);
    }

    public int createProjectUserRelation(String userId, String projectId) {
        if (!checkProjectUserExists(projectId, userId)) {
            ProjectUser pu = new ProjectUser();
            pu.setUserId(userId);
            pu.setId(StringUtils.id());
            pu.setCreateTime(new Date());
            pu.setProjectId(projectId);
            pu.setStatus(ProjectUser.Status.ACCEPTED);
            return create(pu);
        }
        return 0;
    }

    public <T> T getById(String id, Class<T> clazz) {
        return DataFactory.instance().getById(clazz, id);
    }


    public int findPassword(String id, String email, String password) {
        return DataFactory.instance().findPassword(id, email, password);
    }

    public boolean checkUserHasProjectEditPermission(String userId, String projectId) {
        return DataFactory.instance().checkUserHasProjectEditPermission(userId, projectId);
    }

    public void initUserThirdlyBinds(User user) {
        DataFactory.instance().initUserThirdlyBinds(user);
    }

    public int unbindUserThirdPartyRelation(String userId, String type) {
        return DataFactory.instance().removeUserThirdPartyRelation(userId, type);
    }

    /**
     * @param project
     * @return
     * @see ProjectService#createProject(Project)
     */
    @Deprecated
    public int createProject(Project project) {
        return DataFactory.instance().createProject(project);
    }


    public int updateProjectUserEditable(String projectId, String userId, String editable) {
        return DataFactory.instance().updateProjectUserEditable(projectId, userId, editable);
    }

    public int updateCommonlyUsedProject(String projectId, String userId, String isCommonlyUsed) {
        return DataFactory.instance().updateCommonlyUsedProject(projectId, userId, isCommonlyUsed);
    }


    public List<Share> getSharesByProjectId(String projectId) {
        List<Share> shares = ResultUtils.list(DataFactory.instance().getSharesByProjectId(projectId));
        if (shares.size() > 0) {
            for (Share s : shares) {
                if (!Share.ShareAll.YES.equals(s.getShareAll())) {
                    s.setDocNames(DataFactory.instance().getDocNamesFromIds(s.getDocIdsArray()));
                }
            }
        }
        return shares;
    }

    public int updateDocSorts(String[] idsorts) {
        return DataFactory.instance().updateDocSorts(idsorts);
    }

    public String getUserName(String userId) {
        return DataFactory.instance().getUserName(userId);
    }

    public List<ProjectLog> getProjectLogs(Pagination pagination) {
        return ResultUtils.list(DataFactory.instance().getProjectLogs(pagination));
    }


    public List<Attach> getAttachsByRelatedId(String relatedId) {
        return ResultUtils.list(DataFactory.instance().getAttachsByRelatedId(relatedId));
    }




    public List<DocHistory> getDocHistorys(String docId) {
        return ResultUtils.list(DataFactory.instance().getDocHistorys(docId));
    }

    public List<Doc> getDocsByProjectId(String projectId, boolean full) {
        return ResultUtils.list(DataFactory.instance().getDocsByProjectId(projectId, full));
    }


    public List<String> getAllProjectValidIds() {
        return ResultUtils.list(DataFactory.instance().getAllProjectValidIds());
    }
}
