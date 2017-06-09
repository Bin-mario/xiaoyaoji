package cn.com.xiaoyaoji.controller;

import java.util.List;
import java.util.Map;

import cn.com.xiaoyaoji.core.common.Pagination;
import cn.com.xiaoyaoji.service.ServiceFactory;

import cn.com.xiaoyaoji.data.bean.ProjectLog;
import cn.com.xiaoyaoji.core.common._HashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhoujingjie
 * @date 2016-11-01
 */
@RestController
@RequestMapping("/projectlog")
public class ProjectLogController {

    @GetMapping
    public Object get(Map<String,String> params) {
        Pagination pagination = Pagination.build(params);
        List<ProjectLog> logs = ServiceFactory.instance().getProjectLogs(pagination);
        return new _HashMap<>()
                .add("logs", logs);
    }
}
