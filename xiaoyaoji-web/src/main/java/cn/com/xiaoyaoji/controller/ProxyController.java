package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.utils.HttpUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujingjie
 * @date 2016-07-18
 */
@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @GetMapping
    public Object get(@RequestParam String url){
        return HttpUtils.get(url);
    }

    @PostMapping
    public Object post(@RequestParam String url){
        List<NameValuePair> nvp = new ArrayList<>();
        return HttpUtils.post(url,nvp.toArray(new NameValuePair[nvp.size()]));
    }

}
