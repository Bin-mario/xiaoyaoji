package cn.com.xiaoyaoji.core.converter;

import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.utils.JsonUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author: zhoujingjie
 * @Date: 17/3/30
 */
public class JsonMessageConverter extends AbstractHttpMessageConverter {
    /**
     * Construct an {@code AbstractHttpMessageConverter} with no supported media types.
     *
     * @see #setSupportedMediaTypes
     */
    public JsonMessageConverter() {
        this(MediaType.APPLICATION_JSON);
    }

    /**
     * Construct an {@code AbstractHttpMessageConverter} with one supported media type.
     *
     * @param supportedMediaType the supported media type
     */
    public JsonMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    /**
     * Construct an {@code AbstractHttpMessageConverter} with multiple supported media types.
     *
     * @param supportedMediaTypes the supported media types
     */
    public JsonMessageConverter(MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
    }

    /**
     * Construct an {@code AbstractHttpMessageConverter} with a default charset and
     * multiple supported media types.
     *
     * @param defaultCharset      the default character set
     * @param supportedMediaTypes the supported media types
     * @since 4.3
     */
    public JsonMessageConverter(Charset defaultCharset, MediaType... supportedMediaTypes) {
        super(defaultCharset, supportedMediaTypes);
    }

    /**
     * Indicates whether the given class is supported by this converter.
     *
     * @param clazz the class to test for support
     * @return {@code true} if supported; {@code false} otherwise
     */
    @Override
    protected boolean supports(Class clazz) {
        return true;
    }

    /**
     * Abstract template method that reads the actual object. Invoked from {@link #read}.
     *
     * @param clazz        the type of object to return
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return JSON.parseObject(IOUtils.toByteArray(inputMessage.getBody()),clazz);
    }

    /**
     * Abstract template method that writes the actual body. Invoked from {@link #write}.
     *
     * @param o             the object to write to the output message
     * @param outputMessage the HTTP output message to write to
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotWritableException in case of conversion errors
     */
    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Result result;
        if(o instanceof Result){
            result = (Result) o;
        }else{
            result = new Result(true,o);
        }
        outputMessage.getBody().write(JsonUtils.toString(result).getBytes("UTF-8"));
    }
}
