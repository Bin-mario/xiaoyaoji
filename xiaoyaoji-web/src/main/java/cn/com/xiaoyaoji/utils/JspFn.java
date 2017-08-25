package cn.com.xiaoyaoji.utils;

import cn.com.xiaoyaoji.data.bean.Share;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhoujingjie
 *         created on 2017/8/25
 */
public class JspFn {
    private HttpServletRequest servletRequest;
    private String contextPath ;
    public JspFn(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        this.contextPath = servletRequest.getContextPath();
    }

    public String getDocViewURL(String docId){
        if(servletRequest.getAttribute("share") == null){
            return contextPath+"/doc/"+docId;
        }
        Share share = (Share) servletRequest.getAttribute("share");
        return contextPath+"/share/"+share.getId()+"/"+docId;
    }


}
