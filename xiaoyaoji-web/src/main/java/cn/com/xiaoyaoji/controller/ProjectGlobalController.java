package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.ProjectGlobal;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceTool;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author: zhoujingjie
 * @Date: 17/4/25
 */
@RestController
@RequestMapping("/project/global")
public class ProjectGlobalController {

    @GetMapping("{projectId}")
    public ModelAndView index(@PathVariable String projectId,User user){
        ServiceTool.checkUserHasAccessPermission(projectId,user);
        Project project =  ProjectService.instance().getProject(projectId);
        AssertUtils.notNull(project,"项目不存在或已删除");
        ProjectGlobal pg = ProjectService.instance().getProjectGlobal(projectId);
        //return new ModelAndView("/doc/edit")
        return new ModelAndView("/project/global/project-global")
                .addObject("projectGlobal",pg)
                .addObject("project",project)
                .addObject("editProjectGlobal",true)
                .addObject("edit",true)
                ;
    }

    /**
     * 查询环境变量
     * @param projectId
     * @param user
     * @return
     */
    @GetMapping("{projectId}/environments")
    public ModelAndView getEnvironments(@PathVariable String projectId,User user){
        ModelAndView mav= index(projectId,user);
        mav.setViewName("/project/global/project-global-environment");
        return mav;
    }

    /**
     * status
     * @param projectId
     * @param user
     * @return
     */
    @GetMapping("{projectId}/status")
    public ModelAndView getStatus(@PathVariable String projectId,User user){
        ModelAndView mav= index(projectId,user);
        mav.setViewName("/project/global/project-global-status");
        return mav;
    }



    @PostMapping("{projectId}")
    public int save(@PathVariable String projectId,ProjectGlobal pg,User user){
        ProjectGlobal temp = ProjectService.instance().getProjectGlobal(projectId);
        AssertUtils.notNull(temp,"全局对象不存在");
        pg.setId(temp.getId());
        pg.setProjectId(null);
        ServiceTool.checkUserHasEditPermission(temp.getProjectId(),user);
        int rs = DataFactory.instance().update(pg);
        AssertUtils.isTrue(rs>0,"操作失败");
        return rs;
    }
}
