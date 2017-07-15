package cn.com.xiaoyaoji.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * websocket
 * @author: zhoujingjie
 * @Date: 17/6/6
 */
public class WebsocketDoc {
    //请求地址
    private String url;
    //描述
    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
