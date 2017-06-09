package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.core.common._HashMap;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhoujingjie
 * @Date: 16/8/27
 */
@Ignore
@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/raw")
    public Object testRaw(HttpServletRequest request) throws IOException {
        Enumeration<String> names = request.getHeaderNames();
        Map<String, String> headerMap = new HashMap<>();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        String raw = IOUtils.toString(request.getInputStream(), encoding);
        return new _HashMap<>()
                .add("headers", headerMap)
                .add("raw", raw);
    }

    @GetMapping
    public ModelAndView get(){
        return new ModelAndView("/test/index");
    }
}
