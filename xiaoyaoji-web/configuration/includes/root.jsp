<%@ page import="cn.com.xiaoyaoji.core.common._HashMap" %>
<%@ page import="cn.com.xiaoyaoji.utils.JspFn" %>
<%@ page import="cn.com.xiaoyaoji.core.util.ConfigUtils" %><%--
  User: zhoujingjie
  Date: 16/5/22
  Time: 09:21
--%>
<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" language="java" %>
<%
    request.setAttribute("ctx",request.getContextPath());
    request.setAttribute("assets",request.getContextPath()+"/assets");
    request.setAttribute("v", ConfigUtils.getProperty("xyj.version"));
    request.setAttribute("site",new _HashMap<String,String>().add("name","小幺鸡")
            .add("keywords","小幺鸡,在线文档管理,接口文档管理,接口测试,websocket在线测试")
    );
    //扩展功能
    if(request.getAttribute("fn") == null) {
        request.setAttribute("fn", new JspFn(request));
    }
    request.setAttribute("cdn","//f1.xiaoyaoji.cn");
%>
