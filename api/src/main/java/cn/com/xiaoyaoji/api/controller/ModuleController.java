package cn.com.xiaoyaoji.api.controller;

import java.io.IOException;
import java.util.Date;

import org.mangoframework.core.annotation.Get;
import org.mangoframework.core.annotation.RequestMapping;
import org.mangoframework.core.annotation.RequestMethod;
import org.mangoframework.core.annotation.RequestParam;
import org.mangoframework.core.dispatcher.Parameter;

import cn.com.xiaoyaoji.api.asynctask.AsyncTaskBus;
import cn.com.xiaoyaoji.api.data.bean.Module;
import cn.com.xiaoyaoji.api.data.bean.Project;
import cn.com.xiaoyaoji.api.ex._HashMap;
import cn.com.xiaoyaoji.api.service.ServiceFactory;
import cn.com.xiaoyaoji.api.service.ServiceTool;
import cn.com.xiaoyaoji.api.utils.AssertUtils;
import cn.com.xiaoyaoji.api.utils.BeanUtils;
import cn.com.xiaoyaoji.api.utils.StringUtils;

/**
 * @author zhoujingjie
 * @date 2016-05-25
 */
@RequestMapping("/module")
public class ModuleController {

    @Get("{id}")
    public Object id(Parameter parameter, @RequestParam("id") String id) {
        Module module = ServiceFactory.instance().getById(id, Module.class);
        AssertUtils.notNull(module, "无效id");
        return new _HashMap<>().add("module", module);
    }

    /**
     * 创建m模块
     * @param parameter
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    public Object createModule(Parameter parameter) throws IOException {
        String token = parameter.getParamString().get("token");
        Module module = BeanUtils.convert(Module.class, parameter.getParamString());
        AssertUtils.notNull(module, "数据为空");
        AssertUtils.notNull(module.getName(), "分类名为空");
        AssertUtils.notNull(module.getProjectId(), "项目id为空");
        ServiceTool.checkUserHasEditPermission(module.getProjectId(), parameter);
        module.setId(StringUtils.id());
        module.setLastUpdateTime(new Date());
        module.setCreateTime(new Date());
        int rs = ServiceFactory.instance().create(module);
        AssertUtils.isTrue(rs > 0, "操作失败");
        // AsyncTaskBus.instance().push(Log.create(token, Log.CREATE_MODULE,module.getName(),module.getProjectId()));
        AsyncTaskBus.instance().push(module.getProjectId(), Project.Action.CREATE_MODULE, module.getId(), token, "创建模块-" + module.getName());
        return module.getId();
    }

    /**
     * 更新module
     * @param id
     * @return int
     */
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public Object updateModule(@RequestParam("id") String id, Parameter parameter) throws IOException {
        String token = parameter.getParamString().get("token");
        Module temp = ServiceFactory.instance().getById(id, Module.class);
        AssertUtils.notNull(temp, "无效id");
        ServiceTool.checkUserHasEditPermission(temp.getProjectId(), parameter);

        Module module = BeanUtils.convert(Module.class, parameter.getParamString());
        if (module == null) {
            module = new Module();
        }
        module.setId(id);
        module.setLastUpdateTime(new Date());
        int rs = ServiceFactory.instance().update(module);
        AssertUtils.isTrue(rs > 0, "操作失败");
        // AsyncTaskBus.instance().push(Log.create(token, Log.UPDATE_MODULE,module.getName(),module.getProjectId()));
        AsyncTaskBus.instance().push(temp.getProjectId(), Project.Action.UPDATE_MODULE, temp.getId(), token, "修改模块-" + temp.getName());
        return rs;
    }

    /**
     * 删除module
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Object deleteModule(@RequestParam("id") String id, Parameter parameter) {
        String token = parameter.getParamString().get("token");
        Module module = ServiceFactory.instance().getById(id, Module.class);
        AssertUtils.notNull(module, "无效id");
        ServiceTool.checkUserHasEditPermission(module.getProjectId(), parameter);
        AssertUtils.notNull(id, "id为空");
        int rs = ServiceFactory.instance().deleteModule(id);
        AssertUtils.isTrue(rs > 0, "操作失败");
        AsyncTaskBus.instance().push(module.getProjectId(), Project.Action.DELETE_MODULE, module.getId(), token, "删除模块-" + module.getName());
        return rs;
    }

}
