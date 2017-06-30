package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.data.bean.*;
import cn.com.xiaoyaoji.core.common.Message;
import cn.com.xiaoyaoji.service.DocService;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.service.ServiceTool;
import cn.com.xiaoyaoji.utils.AssertUtils;
import cn.com.xiaoyaoji.utils.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import static cn.com.xiaoyaoji.service.ServiceFactory.DOC_DEFAULT_PARENTID;

/**
 * @author: zhoujingjie
 * @Date: 16/7/13
 */
@RestController
@RequestMapping(value = {"/doc"})
public class DocController {
    private static Logger logger = Logger.getLogger(DocController.class);

    /**
     * 新增
     *
     * @return
     */
    @PostMapping
    public String createDoc(User user,Doc doc) {
        AssertUtils.isTrue(ServiceFactory.instance().checkUserHasProjectEditPermission(user.getId(), doc.getProjectId()), "无操作权限");
        if(org.apache.commons.lang3.StringUtils.isBlank(doc.getParentId())){
            doc.setParentId(DOC_DEFAULT_PARENTID);
        }
        doc.setId(StringUtils.id());
        if(org.apache.commons.lang3.StringUtils.isBlank(doc.getName())) {
            doc.setName("默认文档");
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(doc.getType())){
            doc.setType(DocType.SYS_DOC_RICH_TEXT.getTypeName());
        }
        doc.setLastUpdateTime(new Date());
        doc.setCreateTime(new Date());
        AssertUtils.notNull(doc.getProjectId(),"missing projectId");
        AssertUtils.notNull(doc.getParentId(),"missing parentId");
        int rs = ServiceFactory.instance().create(doc);
        AssertUtils.isTrue(rs > 0, "增加失败");
        return doc.getId();
    }



    /**
     * 更新
     *
     * @param id id
     * @return
     */
    @PostMapping("{id}")
    public int update(@PathVariable("id") String id, Doc doc, User user,String comment) {
        AssertUtils.notNull(id, "missing id");
        Doc temp = ServiceFactory.instance().getById(id,Doc.class);
        AssertUtils.notNull(temp, "文档不存在或已删除");
        DocHistory history = new DocHistory();
        temp.setId(null);
        doc.setId(id);
        AssertUtils.isTrue(ServiceFactory.instance().checkUserHasProjectEditPermission(user.getId(), temp.getProjectId()), "无操作权限");
        doc.setLastUpdateTime(new Date());
        doc.setCreateTime(null);
        if(org.apache.commons.lang3.StringUtils.isBlank(doc.getName())){
            doc.setName(null);
        }
        int rs = ServiceFactory.instance().update(doc);
        AssertUtils.isTrue(rs > 0, "修改失败");
        if(org.apache.commons.lang3.StringUtils.isBlank(comment)){
            comment = "修改文档";
        }
        try {
            BeanUtils.copyProperties(history, temp);
            history.setName(doc.getName());
            history.setComment(comment);
            history.setUserId(user.getId());
            history.setCreateTime(new Date());
            history.setDocId(id);
            ServiceFactory.instance().create(history);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e);
        }
        ProjectService.instance().updateLastUpdateTime(temp.getProjectId());

        return rs;
    }

    /**
     * 获取历史修改记录
     * @param docId
     * @return
     */
    @RequestMapping("/history/{docId}")
    public Object getHistory(@PathVariable String docId){
        return ServiceFactory.instance().getDocHistorys(docId);
    }


    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public int delete(@PathVariable("id") String id, User user) {
        AssertUtils.notNull(id, "missing id");
        Doc temp = ServiceFactory.instance().getById(id, Doc.class);
        AssertUtils.notNull(temp, "接口不存在或已删除");
        AssertUtils.isTrue(ServiceFactory.instance().checkUserHasProjectEditPermission(user.getId(), temp.getProjectId()), "无操作权限");
        int rs = ServiceFactory.instance().deleteDoc(id);

        AssertUtils.isTrue(rs > 0, "删除失败");
        /*AsyncTaskBus.instance().push(temp.getProjectId(), Project.Action.DELETE_INTERFACE, id, token, "删除接口-" + temp.getName());*/
        return rs;
    }

    @PostMapping("sort")
    public Object sort(@RequestParam("id") String id,
                       @RequestParam("parentId")String parentId,
                       @RequestParam("sorts")String sorts
                       ) {
        AssertUtils.notNull(id, "参数为空");
        AssertUtils.notNull(parentId, "参数为空");
        AssertUtils.notNull(sorts, "参数为空");
        //更新parentId
        Doc doc = new Doc();
        doc.setId(id);
        doc.setParentId(parentId);
        ServiceFactory.instance().update(doc);

        String[] idsorts = sorts.split(",");
        int rs = ServiceFactory.instance().updateDocSorts(idsorts);
        AssertUtils.notNull(rs > 0, Message.OPER_ERR);
        return true;
    }


    /**
     * 预览文档
     * @param docId
     * @param docHistoryId
     * @param user
     * @return
     */
    @GetMapping("{docId}")
    @Ignore
    public ModelAndView docView(@PathVariable String docId,
                                @RequestParam(value = "docHistoryId",required = false)String docHistoryId,User user){
        AssertUtils.notNull(docId, "参数丢失");
        Doc doc=null;
        if(org.apache.commons.lang3.StringUtils.isNotBlank(docHistoryId)){
            doc = DocService.instance().getByHistoryId(docHistoryId);
            AssertUtils.isTrue(doc.getId().equals(docId),"数据无效");
        }else {
            doc = DocService.instance().getDoc(docId);
        }
        AssertUtils.notNull(doc,"文档不可见或已删除");
        //获取project
        Project project = ServiceFactory.instance().getProject(doc.getProjectId());
        AssertUtils.notNull(project,"项目不存在或者无访问权限");

        if(org.apache.commons.lang3.StringUtils.isBlank(doc.getType())){
            doc.setType(DocType.SYS_DOC_RICH_TEXT.getTypeName());
        }

        ServiceTool.checkUserHasAccessPermission(project,user);

        boolean editPermission = false;
        if(user!=null) {
            //访问权限
            editPermission = ServiceFactory.instance().checkUserHasProjectEditPermission(user.getId(), doc.getProjectId());
        }
        return new ModelAndView("/doc/view")
                .addObject("project",project)
                .addObject("doc",doc)
                .addObject("user",user)
                .addObject("editPermission",editPermission)
                .addObject("projectGlobal", ProjectService.instance().getProjectGlobal(doc.getProjectId()))
                ;
    }

    /**
     * 编辑文档
     * @param docId
     * @param docHistoryId
     * @param user
     * @return
     */
    @GetMapping("{docId}/edit")
    public ModelAndView docEdit(@PathVariable String docId,
                                @RequestParam(value = "docHistoryId",required = false)String docHistoryId,User user){
        ModelAndView view =  docView(docId,docHistoryId,user)
                .addObject("edit",true);
        view.setViewName("/doc/edit");
        return view;
    }

    @GetMapping("/search")
    public Object search(@RequestParam String text,@RequestParam("projectId")String projectId,User user){
        ServiceTool.checkUserHasAccessPermission(projectId,user);
        List<Doc> docs = DocService.instance().searchDocs(text,projectId);
        return new _HashMap<>()
                .add("docs",docs);
    }
}
