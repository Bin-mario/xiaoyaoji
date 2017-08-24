package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.core.util.AssertUtils;
import cn.com.xiaoyaoji.data.bean.Share;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.service.ServiceTool;
import cn.com.xiaoyaoji.util.SqlUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.security.provider.SHA;

/**
 * 分享
 * @author zhoujingjie
 *         created on 2017/8/24
 */
@RequestMapping("/share")
@RestController
public class ShareController {


    @GetMapping("{id}")
    public ModelAndView id(@PathVariable("id")String id, User user){
        return null;
    }

    @GetMapping("/project/{projectId}")
    public Object getByProjectId(@PathVariable("projectId")String projectId, User user){
        ServiceTool.checkUserHasAccessPermission(projectId,user);
        return new _HashMap<>().add("shares",ServiceFactory.instance().getSharesByProjectId(projectId));
    }

    /**
     * 删除
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/id")
    public int delete(@PathVariable("id")String id, User user){
        Share share = ServiceFactory.instance().getById(id,Share.class);
        AssertUtils.notNull(share,"无效ID");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),user);
        int rs = ServiceFactory.instance().delete(SqlUtils.getTableName(share),id);
        AssertUtils.isTrue(rs>0,"删除失败");
        return rs;
    }

    /**
     * 修改
     * @param id
     * @param password
     * @param user
     * @return
     */
    @PutMapping("/{id}")
    public int update(@PathVariable("id")String id,@RequestParam("password") String password,User user){
        Share share = ServiceFactory.instance().getById(id,Share.class);
        AssertUtils.notNull(share,"无效ID");
        ServiceTool.checkUserHasEditPermission(share.getProjectId(),user);
        Share temp = new Share();
        temp.setId(share.getId());
        temp.setPassword(password);
        int rs= ServiceFactory.instance().update(share);
        AssertUtils.isTrue(rs>0,"操作失败");
        return rs;
    }
}
