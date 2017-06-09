package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.extension.cache.CacheUtils;
import cn.com.xiaoyaoji.service.ServiceTool;
import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.utils.result.Handler;
import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.utils.*;
import cn.com.xiaoyaoji.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujingjie
 * @date 2016-10-20
 */
@RestController
@RequestMapping("/share")
public class ShareController {

    @Ignore
    @GetMapping("/{id}/view")
    public Object get(@PathVariable("id")String id, @RequestParam("code") String code){
        Share share = ServiceFactory.instance().getShare(id);
        AssertUtils.notNull(share,"该分享不存在或已删除");

        //如果需要密码
        if(org.apache.commons.lang3.StringUtils.isNotBlank(share.getPassword())){
            //表示输入了密码
             if(org.apache.commons.lang3.StringUtils.isNotBlank(code)){
                if(!code.equals(share.getPassword())){
                    return new Result<>(-3,"密码错误");
                }
             }else{ //表示需要输入密码
                 return new _HashMap<>().add("share",true).add("needPassword",true);
             }
        }

        //获取项目
        Project project = ServiceFactory.instance().getProject(share.getProjectId());
        if(project == null)
            return new _HashMap<>().add("project", "");
        //获取模块
        List<Module> modules = null;
        List<Folder> folders = null;
        List<Doc> interfaces = null;
        if(Share.ShareAll.YES.equals(share.getShareAll())) {
            modules = ServiceFactory.instance().getModules(share.getProjectId());
            //获取该项目下所有文件夹
            folders = ServiceFactory.instance().getFoldersByProjectId(share.getProjectId());
            //获取该项目下所有接口
            interfaces = ServiceFactory.instance().getDocsByProjectId(project.getId());
        }else{
            String[] moduleIds = share.getModuleIdsArray();
            modules = ServiceFactory.instance().getModules(moduleIds);
            folders = ServiceFactory.instance().getFoldersByModuleIds(moduleIds);
            interfaces = ServiceFactory.instance().getInterfacesByModuleIds(moduleIds);
        }

        Map<String, List<Folder>> folderMap = ResultUtils.listToMap(folders, new Handler<Folder>() {
            @Override
            public String key(Folder item) {
                return item.getModuleId();
            }
        });
        for (Module module : modules) {
            List<Folder> temp = folderMap.get(module.getId());
            if (temp != null) {
                module.setFolders(temp);
            }
        }

        Map<String, List<Doc>> interMap = ResultUtils.listToMap(interfaces, new Handler<Doc>() {
            @Override
            public String key(Doc item) {
                //return item.getFolderId();
                return null;
                //todo
            }
        });
        for (Folder folder : folders) {
            List<Doc> temp = interMap.get(folder.getId());
            if (temp != null) {
                folder.setChildren(temp);
            }
        }
        return new _HashMap<>()
                .add("modules", modules)
                .add("project", project)
                .add("share",true)
                ;
    }

    /**
     * 新增
     * @return
     */
    @PostMapping
    public Object create(HttpServletRequest request,@RequestParam("token") String token){
        String moduleId = request.getParameter("moduleId");

        String projectId = request.getParameter("projectId");
        AssertUtils.notNull(projectId,"项目id为空");
        ServiceTool.checkUserHasEditPermission(projectId,token);

        Share share = new Share();
        share.setId(StringUtils.id());
        share.setModuleIds(moduleId);
        share.setName(request.getParameter("name"));
        if(org.apache.commons.lang3.StringUtils.isBlank(share.getName())){
            share.setName(DateUtils.toStr(new Date()));
        }
        share.setPassword(request.getParameter("password"));
        User user = CacheUtils.getUser(token);
        share.setUserId(user.getId());
        share.setShareAll(request.getParameter("shareAll"));
        if(!Share.ShareAll.YES.equals(share.getShareAll())){
            AssertUtils.notNull(moduleId,"模块id为空");
        }
        share.setCreateTime(new Date());
        share.setProjectId(projectId);
        int rs = ServiceFactory.instance().create(share);
        AssertUtils.isTrue(rs>0,"操作失败");
        return rs;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable("id")String id, @RequestParam("token") String token){
        Share share = ServiceFactory.instance().getShare(id);
        AssertUtils.notNull(share,"该数据不存在");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),token);
        int rs = ServiceFactory.instance().delete(SqlUtils.getTableName(Share.class),id);
        AssertUtils.isTrue(rs>0,"操作失败");
        return share;
    }
    /**
     * 修改
     * @param id
     * @return
     */
    @PostMapping("/{id}")
    public Object update(@PathVariable("id")String id,Share share, @RequestParam("token") String token){
        Share temp = ServiceFactory.instance().getShare(id);
        AssertUtils.notNull(share,"该数据不存在");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),token);
        share.setId(id);
        int rs = ServiceFactory.instance().update(share);
        AssertUtils.isTrue(rs>0,"操作失败");
        return share;
    }


}
