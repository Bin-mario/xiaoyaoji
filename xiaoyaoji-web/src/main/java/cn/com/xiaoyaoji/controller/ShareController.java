package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.plugin.Event;
import cn.com.xiaoyaoji.core.plugin.PluginInfo;
import cn.com.xiaoyaoji.core.plugin.PluginManager;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.data.DataFactory;
import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.Share;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.DocService;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.service.ServiceTool;
import cn.com.xiaoyaoji.util.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 分享
 *
 * @author zhoujingjie
 *         created on 2017/8/24
 */
@RequestMapping("/share")
@RestController
public class ShareController {


    /**
     * @param id
     * @param password 输入的密码
     * @param docId    当前的docId
     * @param user
     * @return
     */
    @Ignore
    @GetMapping("{id}")
    public ModelAndView shareDetails(@PathVariable("id") String id,
                                     @RequestParam(value = "password", required = false) String password,
                                     HttpServletRequest request,
                                     String docId,
                                     User user) {
        Share share = ServiceFactory.instance().getById(id, Share.class);
        AssertUtils.notNull(share, "无效地址");

        if (StringUtils.isNotBlank(password)) {
            request.getSession().setAttribute("share_" + id + "_password", password);
        }
        password = (String) request.getSession().getAttribute("share_" + id + "_password");

        Map<String,Object> resultModel = new HashMap<>();

        //ajax请求
        boolean isXHR = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        //是否已验证
        boolean validated = false;
        if (StringUtils.isBlank(share.getPassword()) || share.getPassword().equals(password)) {
            validated = true;
            if (!isXHR) {
                List<Doc> resultDocs = getDocs(share);
                if(StringUtils.isBlank(docId) && resultDocs.size()>0){
                    docId = resultDocs.get(0).getId();
                }
                resultModel.put("docs",resultDocs);
            }

        }

        if(validated){
            //当前文档
            Doc currentDoc = null;
            if (docId != null) {
                currentDoc = DocService.instance().getDoc(docId);
            }
            resultModel.put("doc",currentDoc);
            if(currentDoc != null) {
                List<PluginInfo> pluginInfos = PluginManager.getInstance().getPlugins(Event.DOC_EV);
                PluginInfo pluginInfo = null;
                for (PluginInfo info : pluginInfos) {
                    if (currentDoc.getType().equals(info.getId())) {
                        pluginInfo = info;
                        break;
                    }
                }
                resultModel.put("pluginInfo",pluginInfo);
                resultModel.put("projectGlobal",ProjectService.instance().getProjectGlobal(currentDoc.getProjectId()));
            }
            //
            resultModel.put("project",ProjectService.instance().getProject(share.getProjectId()));

        }

        resultModel.put("share",share);
        resultModel.put("isXHR",isXHR);
        ModelAndView mav = new ModelAndView("/doc/share/share")
                .addAllObjects(resultModel)
                ;
        if(!validated){
            mav.setViewName("/doc/share/share-unvalidated");
        }
        return mav;
    }


    /**
     * 查询单个分享内容
     * @param id
     * @param docId
     * @param password
     * @param request
     * @param user
     * @return
     */
    @Ignore
    @GetMapping("{id}/{docId}")
    public ModelAndView shareDocItem(@PathVariable("id") String id,
                                     @PathVariable("docId") String docId,
                                     @RequestParam(value = "password", required = false) String password,
                                     HttpServletRequest request,
                                     User user) {
        return shareDetails(id,password,request,docId,user);
    }

    private List<Doc> getDocs(Share share) {
        Set<String> tempSet = new HashSet<>(Arrays.asList(share.getDocIdsArray()));
        List<Doc> projectDocs = DocService.instance().getProjectDocs(share.getProjectId());
        //如果是查询全部则直接返回所有doc
        if(Share.ShareAll.YES.equals(share.getShareAll())){
            return projectDocs;
        }

        List<Doc> resultDocs = new ArrayList<>(tempSet.size());
        for (Doc item : projectDocs) {
            if (tempSet.contains(item.getId())) {
                resultDocs.add(item);
            }
        }
        return resultDocs;
    }


    @GetMapping("/project/{projectId}")
    public Object getByProjectId(@PathVariable("projectId") String projectId, User user) {
        ServiceTool.checkUserHasAccessPermission(projectId, user);
        return new _HashMap<>().add("shares", ServiceFactory.instance().getSharesByProjectId(projectId));
    }

    /**
     * 删除
     *
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/{id}")
    public int delete(@PathVariable("id") String id, User user) {
        Share share = ServiceFactory.instance().getById(id, Share.class);
        AssertUtils.notNull(share, "无效ID");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(), user);
        int rs = ServiceFactory.instance().delete(SqlUtils.getTableName(share), id);
        AssertUtils.isTrue(rs > 0, "删除失败");
        return rs;
    }

    /**
     * 修改
     *
     * @param id
     * @param password
     * @param user
     * @return
     */
    @PostMapping("/{id}")
    public int update(@PathVariable("id") String id, @RequestParam("password") String password, User user) {
        Share share = ServiceFactory.instance().getById(id, Share.class);
        AssertUtils.notNull(share, "无效ID");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(), user);
        Share temp = new Share();
        temp.setId(share.getId());
        temp.setPassword(password);
        int rs = ServiceFactory.instance().update(temp);
        AssertUtils.isTrue(rs > 0, "操作失败");
        return rs;
    }

    /**
     * 新增
     * @param share
     * @param user
     * @return
     */
    @PostMapping
    public int create(Share share,User user){
        AssertUtils.notNull(share.getName(),"分享名称不能为空");
        AssertUtils.notNull(share.getProjectId(),"missing projectId");
        share.setCreateTime(new Date());
        share.setId(cn.com.xiaoyaoji.core.util.StringUtils.id());
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),user);
        share.setUserId(user.getId());
        int rs = ServiceFactory.instance().create(share);
        AssertUtils.isTrue(rs>0,"操作失败");
        return rs;
    }
}
