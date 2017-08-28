package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common.Message;
import cn.com.xiaoyaoji.core.common.Pagination;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.core.plugin.doc.DocExportPlugin;
import cn.com.xiaoyaoji.core.plugin.doc.DocImportPlugin;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.core.util.ConfigUtils;
import cn.com.xiaoyaoji.core.util.StringUtils;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.ProjectUser;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.DocService;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.service.ServiceTool;
import cn.com.xiaoyaoji.view.MultiView;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目
 *
 * @author zhoujingjie
 * @date 2016-07-20
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
    private static Logger logger = Logger.getLogger(ProjectController.class);

    @GetMapping("/{id}/info")
    public ModelAndView detailInfo(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserIsOwner(project, user);
        return new ModelAndView("/project/info")
                .addObject("project", project)
                .addObject("pageName", "info")
                ;
    }

    @GetMapping("/{id}/member")
    public ModelAndView detailMember(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserIsMember(project, user);

        List<User> users = ServiceFactory.instance().getUsersByProjectId(id);
        return new ModelAndView("/project/member")
                .addObject("users", users)
                .addObject("project", project)
                .addObject("pageName", "member")
                .addObject("fileAccess", ConfigUtils.getFileAccessURL())
                ;
    }

    @GetMapping("/{id}/function")
    public ModelAndView detailFunction(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserIsOwner(project, user);

        List<User> users = ServiceFactory.instance().getUsersByProjectId(id);
        return new ModelAndView("/project/function")
                .addObject("pageName", "function")
                .addObject("users", users)
                .addObject("project", project)
                .addObject("fileAccess", ConfigUtils.getFileAccessURL())
                ;
    }

    @GetMapping("/{id}/transfer")
    public ModelAndView baseTransfer(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserIsOwner(project, user);
        List<User> users = ServiceFactory.instance().getUsersByProjectId(id);
        return new ModelAndView("/project/transfer")
                .addObject("pageName", "transfer")
                .addObject("project", project)
                .addObject("users", users)
                .addObject("fileAccess", ConfigUtils.getFileAccessURL())
                ;
    }

    @GetMapping("/{id}/quit")
    public ModelAndView baseRelease(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserIsMember(project, user);
        return new ModelAndView("/project/quit")
                .addObject("pageName", "quit")
                .addObject("project", project);
    }

    @Ignore
    @GetMapping("/{id}/export")
    public ModelAndView baseExport(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserHasAccessPermission(project, user);
        return new ModelAndView("/project/export")
                .addObject("project", project)
                .addObject("pageName", "export");
    }

    /**
     * 导出
     *
     * @param id   项目id
     * @param user 当前登录用户
     * @return pdfview
     */
    @Ignore
    @GetMapping(value = "/{id}/export/{pluginId}/do")
    @ResponseBody
    public void export(@PathVariable("id") String id, @PathVariable String pluginId, User user, HttpServletResponse response) throws IOException {

        Project project = ProjectService.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserHasAccessPermission(project, user);

        PluginInfo<DocExportPlugin> docExportPluginPluginInfo = PluginManager.getInstance().getExportPlugin(pluginId);
        AssertUtils.notNull(docExportPluginPluginInfo,"不支持该操作");
        docExportPluginPluginInfo.getPlugin().doExport(id,response);
    }

    /**
     * 导入json
     * @param user
     * @return
     */
    @PostMapping(value = "/import")
    @ResponseBody
    public Object projectImport(String pluginId, User user,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam(value = "projectId",required = false) String projectId,
                                @RequestParam(value = "parentId",required = false) String parentId
                                ) throws IOException {
        PluginInfo<DocImportPlugin> docImportPluginPluginInfo = PluginManager.getInstance().getImportPlugin(pluginId);
        AssertUtils.notNull(docImportPluginPluginInfo,"不支持该操作");
        docImportPluginPluginInfo.getPlugin().doImport(file.getName(),file.getInputStream(),user.getId(),projectId,parentId);
        return true;
    }


    @GetMapping("/{id}/import")
    public ModelAndView baseImport(@PathVariable("id") String id, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, Message.PROJECT_NOT_FOUND);
        ServiceTool.checkUserIsOwner(project, user);
        return new ModelAndView("/project/import")
                .addObject("pageName", "import")
                .addObject("project", project);
    }


    @Ignore
    @GetMapping("/list")
    public MultiView list(User user, @RequestParam(value = "status", required = false) String status) {
        List<Project> projects = new ArrayList<>();
        if(org.apache.commons.lang3.StringUtils.isBlank(status)){
            status = Project.Status.VALID;
        }
        if(user != null){
            Pagination p = Pagination.build(new _HashMap<String, String>().add("status", status)
                    .add("userId", user.getId()));
            projects = ServiceFactory.instance().getProjects(p);
        }
        return new MultiView("/dashboard/index")
                .addObject("projects", projects)
                ;
    }

    /**
     * 查询单个module对应的接口
     *
     * @param id
     * @return
     */
    @Ignore
    @GetMapping(value = "{id}/edit")
    public ModelAndView get(@PathVariable("id") String id, User user) {
        //获取project
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, "项目不存在或者无访问权限");
        String docId = DocService.instance().getFirstDocId(id);
        if (docId == null) {
            docId = DocService.instance().createDefaultDoc(id).getId();
        }
        //重定向到第一个
        return new ModelAndView("redirect:/doc/" + docId + "/edit");

    }

    @Ignore
    @GetMapping("{id}")
    public ModelAndView projectView(@PathVariable("id") String id) {
        //获取project
        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, "项目不存在或者无访问权限");
        String docId = DocService.instance().getFirstDocId(id);
        if (docId == null) {
            docId = DocService.instance().createDefaultDoc(id).getId();
        }
        //重定向到第一个
        return new ModelAndView("redirect:/doc/" + docId)
                ;
    }


    @GetMapping("/{id}/shares")
    @ResponseBody
    public Object shares(@PathVariable("id") String id) {
        return new _HashMap<>().add("shares", ServiceFactory.instance().getSharesByProjectId(id));
    }

    /**
     * 设置是否常用项目
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}/commonly")
    @ResponseBody
    public int updateCommonlyUsed(@PathVariable("id") String id, User user,
                                  @RequestParam String isCommonlyUsed
    ) {
        AssertUtils.notNull(isCommonlyUsed, "isCommonlyUsed is null");
        int rs = ServiceFactory.instance().updateCommonlyUsedProject(id, user.getId(), isCommonlyUsed);
        AssertUtils.isTrue(rs > 0, "操作失败");
        return rs;
    }

    /**
     * 创建项目
     *
     * @param user
     * @param project
     * @return
     */
    @PostMapping
    @ResponseBody
    public Object create(User user, Project project) {
        project.setId(StringUtils.id());
        project.setCreateTime(new Date());
        project.setUserId(user.getId());
        project.setStatus(Project.Status.VALID);
        project.setEditable(ProjectUser.Editable.YES);
        project.setLastUpdateTime(new Date());
        AssertUtils.notNull(project.getPermission(), "missing permission");
        AssertUtils.notNull(project.getName(), "missing name");
        int rs = ProjectService.instance().createProject(project);
        Doc doc = DocService.instance().createDefaultDoc(project.getId());
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return new _HashMap<>()
                .add("projectId", project.getId())
                .add("docId", doc.getId())
                ;
    }


    /**
     * 更新
     *
     * @param id
     * @return
     */
    @PostMapping("{id}")
    @ResponseBody
    public Object update(@PathVariable("id") String id, User user, Project project) {
        ServiceTool.checkUserHasEditPermission(id, user);
        project.setId(id);
        project.setUserId(null);
        project.setLastUpdateTime(new Date());
        int rs = ServiceFactory.instance().update(project);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 项目转让
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}/transfer")
    @ResponseBody
    public Object transfer(@PathVariable("id") String id, User user, @RequestParam String userId) {

        AssertUtils.isTrue(org.apache.commons.lang3.StringUtils.isNoneBlank(userId), "missing userId");
        Project before = ProjectService.instance().getProject(id);
        ServiceTool.checkUserIsOwner(before, user);
        Project temp = new Project();
        temp.setId(id);
        temp.setUserId(userId);
        int rs = ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        ServiceFactory.instance().updateProjectUserEditable(id, userId, "YES");
        return rs;
    }

    /**
     * 删除项目
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    @ResponseBody
    public Object delete(@PathVariable("id") String id, User user) {
        Project before = ProjectService.instance().getProject(id);
        AssertUtils.notNull(before,"无效请求");
        ServiceTool.checkUserIsOwner(before, user);
        Project temp = new Project();
        temp.setStatus(Project.Status.DELETED);
        temp.setLastUpdateTime(new Date());
        temp.setId(id);
        int rs = ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 彻底删除
     *
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/{id}/actual")
    @ResponseBody
    public Object deleteActual(@PathVariable("id") String id, User user) {
        ServiceTool.checkUserHasEditPermission(id, user);
        int rs = ServiceFactory.instance().deleteProject(id);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }


    /**
     * 邀请成员
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}/invite")
    @ResponseBody
    public String invite(@PathVariable("id") String id, User user, @RequestParam String userId) {
        ProjectUser pu = new ProjectUser();
        pu.setId(StringUtils.id());
        pu.setUserId(userId);
        AssertUtils.isTrue(org.apache.commons.lang3.StringUtils.isNotBlank(pu.getUserId()), "missing userId");
        AssertUtils.isTrue(!ServiceFactory.instance().checkProjectUserExists(id, pu.getUserId()), "用户已存在该项目中");
        AssertUtils.isTrue(!pu.getUserId().equals(user.getId()), "不能邀请自己");
        pu.setCreateTime(new Date());
        pu.setStatus(ProjectUser.Status.PENDING);
        pu.setEditable(ProjectUser.Editable.NO);
        pu.setProjectId(id);
        int rs = ServiceFactory.instance().create(pu);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return pu.getId();
    }

    /**
     * 邀请成员
     *
     * @param id
     * @return
     */
    @PostMapping("/{id}/invite/email")
    @ResponseBody
    public String inviteByEmail(@PathVariable("id") String id, @RequestParam("email") String email, User user) {
        String userId = ServiceFactory.instance().getUserIdByEmail(email);
        AssertUtils.isTrue(userId != null, "该邮箱未注册");
        AssertUtils.isTrue(!userId.equals(user.getId()), "不能邀请自己");
        AssertUtils.isTrue(!ServiceFactory.instance().checkProjectUserExists(id, userId), "用户已存在该项目中");

        ProjectUser pu = new ProjectUser();
        pu.setId(StringUtils.id());
        pu.setUserId(userId);
        pu.setProjectId(id);
        pu.setEditable(ProjectUser.Editable.YES);
        AssertUtils.isTrue(org.apache.commons.lang3.StringUtils.isNotBlank(pu.getProjectId()), "missing projectId");
        pu.setCreateTime(new Date());
        pu.setStatus(ProjectUser.Status.PENDING);
        int rs = ServiceFactory.instance().create(pu);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return pu.getId();
    }

    /**
     * 接受邀请
     *
     * @param inviteId
     * @return
     */
    @PostMapping("/{id}/pu/{inviteId}/accept")
    @ResponseBody
    public int acceptInvite(@PathVariable("inviteId") String inviteId) {
        ProjectUser pu = new ProjectUser();
        pu.setId(inviteId);
        pu.setStatus(ProjectUser.Status.ACCEPTED);
        int rs = ServiceFactory.instance().create(pu);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 拒绝邀请
     */
    @PostMapping("/{id}/pu/{inviteId}/refuse")
    @ResponseBody
    public int acceptRefuse(@PathVariable("inviteId") String inviteId) {
        ProjectUser pu = new ProjectUser();
        pu.setId(inviteId);
        pu.setStatus(ProjectUser.Status.REFUSED);
        int rs = ServiceFactory.instance().create(pu);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 移除成员
     *
     * @param userId userId
     * @param id     projectId
     * @return
     */
    @DeleteMapping("/{id}/pu/{userId}")
    @ResponseBody
    public int removeMember(@PathVariable("id") String id, @PathVariable("userId") String userId, User user) {
        Project project = ServiceFactory.instance().getProject(id);
        ServiceTool.checkUserIsOwner(project, user);
        AssertUtils.isTrue(!project.getUserId().equals(userId), "不能移除自己");
        AssertUtils.isTrue(user.getId().equals(project.getUserId()), "无操作权限");
        int rs = ServiceFactory.instance().deleteProjectUser(id, userId);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 设置是否可编辑
     *
     * @param projectId 项目id
     * @param userId
     * @param editable
     * @return
     */
    @PostMapping("/{id}/pu/{userId}/{editable}")
    @ResponseBody
    public int editProjectEditable(@PathVariable("id") String projectId, @PathVariable("userId") String userId, @PathVariable("editable") String editable,
                                   User user) {
        AssertUtils.isTrue(ProjectUser.Editable.YES.equals(editable) || ProjectUser.Editable.NO.equals(editable), "参数错误");
        Project project = ServiceFactory.instance().getProject(projectId);
        ServiceTool.checkUserIsOwner(project, user);
        AssertUtils.isTrue(!project.getUserId().equals(userId), "项目所有人不能修改自己的权限");
        int rs = ServiceFactory.instance().updateProjectUserEditable(projectId, userId, editable);
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 退出项目
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}/quit")
    @ResponseBody
    public int quit(@PathVariable("id") String id, User user) {

        Project project = ServiceFactory.instance().getProject(id);
        AssertUtils.notNull(project, "project not exists");
        AssertUtils.isTrue(!project.getUserId().equals(user.getId()), "项目所有人不能退出项目");
        int rs = ServiceFactory.instance().deleteProjectUser(id, user.getId());
        AssertUtils.isTrue(rs > 0, Message.OPER_ERR);
        return rs;
    }

    /**
     * 项目归档
     * @param id
     * @param user
     * @return
     */
    @PostMapping("/{id}/archive")
    public int archive(@PathVariable("id") String id, User user ){
        Project project = ProjectService.instance().getProject(id);
        AssertUtils.notNull(project, "项目不存在");
        AssertUtils.isTrue(project.getUserId().equals(user.getId()),"非项目所有者不能操作");
        Project temp = new Project();
        temp.setId(id);
        temp.setStatus(Project.Status.ARCHIVE);
        int rs= ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs>0,"操作失败");
        return rs;
    }
}
