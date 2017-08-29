package cn.com.xiaoyaoji.converter;

import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.core.util.JsonUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author: zhoujingjie
 * @Date: 17/3/30
 */
public class JsonMessageConverter extends FastJsonHttpMessageConverter {


    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Result result;
        if(o instanceof Result){
            result = (Result) o;
        }else{
            result = new Result(true,o);
        }
        outputMessage.getHeaders().add("Content-Type","application/json;charset=utf-8");
        super.writeInternal(result,outputMessage);
    }
}
