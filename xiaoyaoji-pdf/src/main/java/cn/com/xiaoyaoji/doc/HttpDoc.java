package cn.com.xiaoyaoji.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * http
 * @author: zhoujingjie
 * @Date: 17/6/6
 */
public class HttpDoc {
    //请求地址
    private String url;
    //请求方法 GET、POST..
    private String requestMethod;
    //请求数据类型,form-data  urlencoded..
    private String dataType;
    //响应数据类型 json,xml,html..
    private String contentType;
    //例子
    private String example;
    //描述
    private String description;
    //请求头
    private List<HttpDocEntity> requestHeaders;
    //请求参数
    private List<HttpDocEntity> requestArgs;
    //响应头
    private List<HttpDocEntity> responseHeaders;
    //响应数据
    private List<HttpDocEntity> responseArgs;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<HttpDocEntity> getRequestHeaders() {
        if(requestHeaders == null){
            requestHeaders= new ArrayList<>();
        }
        return requestHeaders;
    }

    public void setRequestHeaders(List<HttpDocEntity> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public List<HttpDocEntity> getRequestArgs() {
        if(requestArgs == null){
            requestArgs = new ArrayList<>();
        }
        return requestArgs;
    }

    public void setRequestArgs(List<HttpDocEntity> requestArgs) {
        this.requestArgs = requestArgs;
    }

    public List<HttpDocEntity> getResponseHeaders() {
        if(responseHeaders == null){
            responseHeaders = new ArrayList<>();
        }
        return responseHeaders;
    }

    public void setResponseHeaders(List<HttpDocEntity> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public List<HttpDocEntity> getResponseArgs() {
        if(responseArgs == null){
            responseArgs = new ArrayList<>();
        }
        return responseArgs;
    }

    public void setResponseArgs(List<HttpDocEntity> responseArgs) {
        this.responseArgs = responseArgs;
    }
}
