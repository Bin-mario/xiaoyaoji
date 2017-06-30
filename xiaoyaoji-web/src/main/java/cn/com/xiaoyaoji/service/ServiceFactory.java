package cn.com.xiaoyaoji.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.com.xiaoyaoji.Config;
import cn.com.xiaoyaoji.core.common.Pagination;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.Attach;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.DocHistory;
import cn.com.xiaoyaoji.data.bean.Folder;
import cn.com.xiaoyaoji.data.bean.Interface;
import cn.com.xiaoyaoji.data.bean.Module;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.ProjectLog;
import cn.com.xiaoyaoji.data.bean.ProjectUser;
import cn.com.xiaoyaoji.data.bean.Share;
import cn.com.xiaoyaoji.data.bean.TableNames;
import cn.com.xiaoyaoji.data.bean.Team;
import cn.com.xiaoyaoji.data.bean.Thirdparty;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.extension.file.FileUtils;
import cn.com.xiaoyaoji.utils.ResultUtils;
import cn.com.xiaoyaoji.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author: zhoujingjie
 * @Date: 16/5/15
 */
public class ServiceFactory {
    private static Logger logger = Logger.getLogger(ServiceFactory.class);
    private static ServiceFactory instance;

    public static final String DOC_DEFAULT_PARENTID = "0";
    
    private static final String EXPORT_KEY_DOCS = "docs";
    private static final String EXPORT_KEY_VER = "version";
	private static final String IMPORT_KEY_DOC_CHILDREN = "children";
    
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
                    FileUtils.delete((String) map.get(field));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return DataFactory.instance().delete(tableName, id);
    }
    public int updateAndImage(Object instance,String... imgKeys) {
        return DataFactory.instance().updateAndImage(instance,imgKeys);
    }

    public User login(String email, String password) {
        User user = DataFactory.instance().login(email,password);
        if(user != null){
            initUserThirdlyBinds(user);
            user.setPassword(null);
        }
        return user;
    }

    public User loginByThirdparty(Thirdparty thirdparty) {
        User user = DataFactory.instance().getUserByThirdId(thirdparty.getId());
        if(user != null){
            user.setPassword(null);
            initUserThirdlyBinds(user);
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


    public List<User> getUsersByProjectId(String projectId){
        return ResultUtils.list(DataFactory.instance().getUsersByProjectId(projectId));
    }
    public List<User> getAllProjectUsersByUserId(String userId){
        return ResultUtils.list(DataFactory.instance().getAllProjectUsersByUserId(userId));
    }


    public int deleteTeam(String id) {
        return DataFactory.instance().deleteTeam(id);
    }

    public int deleteProject(String id) {
        return DataFactory.instance().deleteProject(id);
    }

    public List<User> searchUsers(String key,String... excludeIds) {
        return ResultUtils.list(DataFactory.instance().searchUsers(key,excludeIds));
    }

    public Project getProject(String id) {
        return DataFactory.instance().getById(Project.class,id);
    }

    public boolean checkEmailExists(String email) {
        return DataFactory.instance().checkEmailExists(email);
    }

    public boolean checkProjectUserExists(String projectId, String userId) {
        return DataFactory.instance().checkProjectUserExists(projectId,userId);
    }

    public int deleteProjectUser(String projectId, String userId) {
        return DataFactory.instance().deleteProjectUser(projectId,userId);
    }

    public List<Folder> getFoldersByProjectId(String projectId) {
        //return ResultUtils.list(DataFactory.instance().getFoldersByProjectId(projectId));
        //todo
        return null;
    }

    public List<Doc> getDocsByProjectId(String projectId) {
        return ResultUtils.list(DataFactory.instance().getDocsByProjectId(projectId));
    }
    public String getUserIdByEmail(String email) {
        return DataFactory.instance().getUserIdByEmail(email);
    }

    public int createProjectUserRelation(String userId, String projectId) {
        if(!checkProjectUserExists(projectId,userId)) {
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

    public <T> T getById(String id,Class<T> clazz) {
        return DataFactory.instance().getById(clazz,id);
    }


    public int findPassword(String id, String email, String password) {
        return DataFactory.instance().findPassword(id,email,password);
    }

    public boolean checkUserHasProjectEditPermission(String userId, String projectId) {
        return DataFactory.instance().checkUserHasProjectEditPermission(userId,projectId);
    }

    public int importFromRap(Project project, List<Module> modules, List<Folder> folders, List<Interface> interfaces) {
        return DataFactory.instance().importFromRap(project,modules,folders,interfaces);
    }

    public void initUserThirdlyBinds(User user) {
        DataFactory.instance().initUserThirdlyBinds(user);
    }

    public int copyFolder(String folderId, String moduleId) {
        //return DataFactory.instance().copyFolder(folderId,moduleId);
        return 0;
        //todo
    }

    public int unbindUserThirdPartyRelation(String userId, String type) {
        return DataFactory.instance().removeUserThirdPartyRelation(userId,type);
    }

    public int createProject(Project project) {
        return DataFactory.instance().createProject(project);
    }

    public String getProjectName(String projectId) {
        return DataFactory.instance().getProjectName(projectId);
    }
    public String getInterfaceFolderName(String folderId) {
        //return DataFactory.instance().getInterfaceFolderName(folderId);
        return null;
        //todo
    }
    public String getModuleName(String moduleId) {
        //return DataFactory.instance().getModuleName(moduleId);
        return null;
    }
    public String getInterfaceName(String interfaceId) {
        return DataFactory.instance().getInterfaceName(interfaceId);
    }

    public String getProjectEditable(String projectId, String userId) {
        return DataFactory.instance().getProjectEditable(projectId,userId);
    }

    public int updateProjectUserEditable(String projectId, String userId, String editable) {
        return DataFactory.instance().updateProjectUserEditable(projectId,userId,editable);
    }

    public int updateCommonlyUsedProject(String projectId, String userId, String isCommonlyUsed) {
        return DataFactory.instance().updateCommonlyUsedProject(projectId,userId,isCommonlyUsed);
    }

    public Share getShare(String id) {
        return DataFactory.instance().getById(Share.class,id);
    }

    public List<Module> getModules(String[] moduleIds) {
        //return ResultUtils.list(DataFactory.instance().getModules(moduleIds));
        return null;
        //todo
    }

    public List<Folder> getFoldersByModuleIds(String[] moduleIds) {
        //todo
        //return ResultUtils.list(DataFactory.instance().getFoldersByModuleIds(moduleIds));
        return null;
    }

    public List<Doc> getInterfacesByModuleIds(String[] moduleIds) {
        //return ResultUtils.list(DataFactory.instance().getContentsByModuleIds(moduleIds));
        //todo
        return null;
    }

    public List<Share> getSharesByProjectId(String projectId) {
        List<Share> shares =  ResultUtils.list(DataFactory.instance().getSharesByProjectId(projectId));
        if(shares.size()>0){
            for(Share s:shares){
                if(!Share.ShareAll.YES.equals(s.getShareAll())){
                    //todo
                    //s.setShareModules(ResultUtils.list(DataFactory.instance().getModuleNameIdsInIds(s.getModuleIdsArray())));
                    return null;
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

    public int importFromMJSON(Project project, List<Module> moduleList) {
        return DataFactory.instance().importFromMJSON(project,moduleList);
    }

    public int moveFolder(String folderId,String newModuleId) {
        //return DataFactory.instance().moveFolder(folderId, newModuleId);
        return 0;
        //todo
    }

    public List<Attach> getAttachsByRelatedId(String relatedId) {
        return ResultUtils.list(DataFactory.instance().getAttachsByRelatedId(relatedId));
    }


    public void getDocIdsByParentId(Set<String> ids, String parentId){
        List<String> temp = ResultUtils.list(DataFactory.instance().getDocIdsByParentId(parentId));
        ids.addAll(temp);
        for(String id:temp){
            getDocIdsByParentId(ids,id);
        }
    }

    public int deleteDoc(String id) {
        //需要优化
        Set<String> ids = new HashSet<>();
        getDocIdsByParentId(ids,id);
        ids.add(id);
        //删除数据
        int rs =deleteByIds(new ArrayList<>(ids));
        for(String temp:ids) {
            //删除附件
            List<Attach> attaches = getAttachsByRelatedId(temp);
            for (Attach attach : attaches) {
                try {
                    FileUtils.delete(attach.getUrl());
                    delete(TableNames.ATTACH, attach.getId());
                    rs++;
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            //删除历史记录
            DataFactory.instance().deleteDocHistoryByDocId(temp);
        }
        return rs;
    }

    private int deleteByIds(List<String> ids) {
        return DataFactory.instance().deleteByIds(Doc.class,ids);
    }

    public List<DocHistory> getDocHistorys(String docId) {
        return ResultUtils.list(DataFactory.instance().getDocHistorys(docId));
    }

    public List<Doc> getDocsByProjectId(String projectId, boolean full) {
        return ResultUtils.list(DataFactory.instance().getDocsByProjectId(projectId, full));
    }
    
    public String exportJson(String projectId){
    	
    	Project project = getProject(projectId);
    	JSONObject json = (JSONObject) JSON.toJSON(project);
    	List<Doc> docs = ProjectService.instance().getProjectDocs(projectId, true);;
    	json.put(EXPORT_KEY_DOCS, docs);
    	json.put(EXPORT_KEY_VER, Config.VERSION);
    	return json.toJSONString();
    }
    
    public String importJson(User user, String json){
    	
    	JSONObject obj = JSON.parseObject(json);
    	String version = obj.getString(EXPORT_KEY_VER);
    	if(version == null) {
    		//TODO
    		return null;
    	}
		Project project = JSON.toJavaObject(obj, Project.class);
		if (org.springframework.util.StringUtils.isEmpty(project.getId())){
			project.setId(StringUtils.id());
			createProject(project);
		} else {
			ServiceTool.checkUserHasAccessPermission(project, user);
			update(project);
		}
		importDoc(project.getId(), DOC_DEFAULT_PARENTID, obj.getJSONArray(EXPORT_KEY_DOCS));
		return project.getId();
    }
    
    private void importDoc(String projectId, String parentId, JSONArray docArr){
    	
    	for(int i=0; i < docArr.size(); i++){
    		JSONObject obj = docArr.getJSONObject(i);
    		Doc doc = JSON.toJavaObject(obj, Doc.class);
    		doc.setProjectId(projectId);
    		doc.setParentId(parentId);
    		if (org.springframework.util.StringUtils.isEmpty(doc.getId())){
    			doc.setId(StringUtils.id());
    			create(doc);
    		} else {
    			update(doc);
    		}
    		JSONArray children = obj.getJSONArray(IMPORT_KEY_DOC_CHILDREN);
    		if (children != null && !children.isEmpty()){
    			importDoc(projectId, doc.getId(), children);
    		}
    	}
    }
}
