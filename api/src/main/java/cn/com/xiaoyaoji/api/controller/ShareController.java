package cn.com.xiaoyaoji.api.controller;

import cn.com.xiaoyaoji.api.annotations.Ignore;
import cn.com.xiaoyaoji.api.service.ServiceTool;
import cn.com.xiaoyaoji.api.data.bean.*;
import cn.com.xiaoyaoji.api.ex.Handler;
import cn.com.xiaoyaoji.api.ex.Result;
import cn.com.xiaoyaoji.api.ex._HashMap;
import cn.com.xiaoyaoji.api.service.ServiceFactory;
import cn.com.xiaoyaoji.api.utils.*;
import cn.com.xiaoyaoji.api.utils.StringUtils;
import org.mangoframework.core.annotation.*;
import org.mangoframework.core.dispatcher.Parameter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujingjie
 * @date 2016-10-20
 */
@RequestMapping("/share")
public class ShareController {

    @Ignore
    @Get("/{id}/view")
    public Object get(@RequestParam("id")String id, Parameter parameter){
        Share share = ServiceFactory.instance().getShare(id);
        AssertUtils.notNull(share,"该分享不存在或已删除");


        String code = parameter.getParamString().get("code");

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
        List<InterfaceFolder> folders = null;
        List<Interface> interfaces = null;
        if(Share.ShareAll.YES.equals(share.getShareAll())) {
            modules = ServiceFactory.instance().getModules(share.getProjectId());
            //获取该项目下所有文件夹
            folders = ServiceFactory.instance().getFoldersByProjectId(share.getProjectId());
            //获取该项目下所有接口
            interfaces = ServiceFactory.instance().getInterfacesByProjectId(project.getId());
        }else{
            String[] moduleIds = share.getModuleIdsArray();
            modules = ServiceFactory.instance().getModules(moduleIds);
            folders = ServiceFactory.instance().getFoldersByModuleIds(moduleIds);
            interfaces = ServiceFactory.instance().getInterfacesByModuleIds(moduleIds);
        }

        Map<String, List<InterfaceFolder>> folderMap = ResultUtils.listToMap(folders, new Handler<InterfaceFolder>() {
            @Override
            public String key(InterfaceFolder item) {
                return item.getModuleId();
            }
        });
        for (Module module : modules) {
            List<InterfaceFolder> temp = folderMap.get(module.getId());
            if (temp != null) {
                module.setFolders(temp);
            }
        }

        Map<String, List<Interface>> interMap = ResultUtils.listToMap(interfaces, new Handler<Interface>() {
            @Override
            public String key(Interface item) {
                return item.getFolderId();
            }
        });
        for (InterfaceFolder folder : folders) {
            List<Interface> temp = interMap.get(folder.getId());
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
     * @param parameter
     * @return
     */
    @Post
    public Object create(Parameter parameter){
        String moduleId = parameter.getParamString().get("moduleId");

        String projectId = parameter.getParamString().get("projectId");
        AssertUtils.notNull(projectId,"项目id为空");
        ServiceTool.checkUserHasEditPermission(projectId,parameter);

        Share share = new Share();
        share.setId(StringUtils.id());
        share.setModuleIds(moduleId);
        share.setName(parameter.getParamString().get("name"));
        if(org.apache.commons.lang3.StringUtils.isBlank(share.getName())){
            share.setName(DateUtils.toStr(new Date()));
        }
        share.setPassword(parameter.getParamString().get("password"));
        User user = MemoryUtils.getUser(parameter);
        share.setUserId(user.getId());
        share.setShareAll(parameter.getParamString().get("shareAll"));
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
     * @param parameter
     * @return
     */
    @Delete("/{id}")
    public Object delete(@RequestParam("id")String id, Parameter parameter){
        Share share = ServiceFactory.instance().getShare(id);
        AssertUtils.notNull(share,"该数据不存在");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),parameter);
        int rs = ServiceFactory.instance().delete(SqlUtils.getTableName(Share.class),id);
        AssertUtils.isTrue(rs>0,"操作失败");
        return share;
    }
    /**
     * 修改
     * @param id
     * @param parameter
     * @return
     */
    @Post("/{id}")
    public Object update(@RequestParam("id")String id, Parameter parameter){
        Share share = ServiceFactory.instance().getShare(id);
        AssertUtils.notNull(share,"该数据不存在");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),parameter);
        share = BeanUtils.convert(Share.class,parameter.getParamString());
        share.setId(id);
        int rs = ServiceFactory.instance().update(share);
        AssertUtils.isTrue(rs>0,"操作失败");
        return share;
    }


}
