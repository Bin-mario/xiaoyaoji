package cn.com.xiaoyaoji.handler;

import cn.com.xiaoyaoji.converter.JsonMessageConverter;
import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.view.MultiView;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujingjie
 *         created on 2017/8/9
 */
public class MultiModelAndViewReturnValueHandler extends ModelAndViewMethodReturnValueHandler {
    private RequestResponseBodyMethodProcessor processor;


    public JsonMessageConverter jsonMessageConverter;

    @PostConstruct
    public void init(){
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(jsonMessageConverter);
        processor = new RequestResponseBodyMethodProcessor(converters);
    }

    public void setJsonMessageConverter(JsonMessageConverter jsonMessageConverter) {
        this.jsonMessageConverter = jsonMessageConverter;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        //return MultiView.class.isAssignableFrom(returnType.getParameterType());
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if(!(returnValue instanceof MultiView)){
            processor.handleReturnValue(new Result<>(true,returnValue),returnType,mavContainer,webRequest);
            return;
        }
        MultiView view = (MultiView) returnValue;
        //如果是ajax请求，则返回json
        if("XMLHttpRequest".equals(webRequest.getHeader("X-Requested-With"))){
            processor.handleReturnValue(new Result<>(true,view.getModelMap()),returnType,mavContainer,webRequest);
            return;
        }

        ModelAndView temp = new ModelAndView(view.getViewName());
        if(view.getView() != null) {
            temp.setView(view.getView());
        }
        temp.setStatus(view.getStatus());
        temp.addAllObjects(view.getModelMap());
        super.handleReturnValue(temp, returnType, mavContainer, webRequest);
    }
}
