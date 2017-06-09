package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.data.DataFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhoujingjie
 * @Date: 17/4/2
 */
@RestController
@RequestMapping("/sys")
public class SysController {

    @Ignore
    @PostMapping("/update")
    public String update(){
        int rs = DataFactory.instance().updateSystem("");
        return "升级系统成功,更新条数:"+rs;
    }
}
