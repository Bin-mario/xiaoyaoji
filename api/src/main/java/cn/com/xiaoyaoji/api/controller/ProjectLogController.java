package cn.com.xiaoyaoji.api.controller;

import java.util.List;

import cn.com.xiaoyaoji.api.ex.Pagination;
import cn.com.xiaoyaoji.api.service.ServiceFactory;
import org.mangoframework.core.annotation.Get;
import org.mangoframework.core.annotation.RequestMapping;
import org.mangoframework.core.dispatcher.Parameter;

import cn.com.xiaoyaoji.api.data.bean.ProjectLog;
import cn.com.xiaoyaoji.api.ex._HashMap;

/**
 * @author zhoujingjie
 * @date 2016-11-01
 */
@RequestMapping("/projectlog")
public class ProjectLogController {

    @Get
    public Object get(Parameter parameter) {
        Pagination pagination = Pagination.build(parameter);
        List<ProjectLog> logs = ServiceFactory.instance().getProjectLogs(pagination);
        return new _HashMap<>()
                .add("logs", logs);
    }
}
