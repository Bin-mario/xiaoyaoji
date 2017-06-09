package cn.com.xiaoyaoji.data;


import java.util.List;
import java.util.Map;

import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.core.common.Pagination;

/**
 * @author: zhoujingjie
 * @Date: 16/5/2
 */
public interface Data {

    int insert(Object instance);

    int update(Object instance);

    int delete(Object instance);

    int delete(String tableName, String id);

    Map<String,Object> getById(String tableName, String id);

    <T> T getById(Class<T> clazz, String id);


    User login(String email, String password);

    int updateAndImage(Object instance, String... imgKeys);

    User getUserByThirdId(String thirdId);

    int bindUserWithThirdParty(Thirdparty thirdparty);

    List<Doc> getDocs(String parentId);

    int deleteInterface(String parentId);

    List<Team> getTeams(String userId);

    List<Project> getProjects(Pagination pagination);

    List<User> getUsersByProjectId(String projectId);

    List<User> getAllProjectUsersByUserId(String userId);

    int deleteTeam(String id);

    int deleteProject(String id);

    List<User> searchUsers(String key,String... excludeIds);

    boolean checkEmailExists(String email);

    boolean checkProjectUserExists(String projectId, String userId);

    int deleteProjectUser(String projectId, String userId);

    List<Doc> getDocsByProjectId(String projectId);

    String getUserIdByEmail(String email);

    int findPassword(String id, String email, String password);

    boolean checkUserHasProjectEditPermission(String userId, String projectId);

    int importFromRap(Project project, List<Module> modules, List<Folder> folders, List<Interface> interfaces);

    void initUserThirdlyBinds(User user);

    int removeUserThirdPartyRelation(String userId, String type);

    int createProject(Project project);

    String getProjectName(String projectId);

    String getInterfaceName(String interfaceId);

    String getProjectEditable(String projectId, String userId);

    int updateProjectUserEditable(String projectId, String userId, String editable);

    int updateCommonlyUsedProject(String projectId, String userId, String isCommonlyUsed);

    List<Share> getSharesByProjectId(String projectId);

    int updateDocSorts(String[] idsorts);

    String getUserName(String userId);

    List<ProjectLog> getProjectLogs(Pagination pagination);

    int importFromMJSON(Project project, List<Module> moduleList);

    //void test();
    int updateSystem(String version);

    ProjectGlobal getProjectGlobal(String projectId);

    List<Attach> getAttachsByRelatedId(String relatedId);

    List<DocHistory> getDocHistorys(String docId);

    int deleteDocHistoryByDocId(String id);

    boolean checkProjectIsPublic(String projectId);

    List<Doc> searchDocs(String text, String projectId);

    String getFirstDocId(String projectId);

    List<Doc> getDocsByProjectId(String projectId, boolean full);

    List<String> getDocIdsByParentId(String parentId);

    int deleteByIds(Class<?> clazz, List<String> ids);
}
