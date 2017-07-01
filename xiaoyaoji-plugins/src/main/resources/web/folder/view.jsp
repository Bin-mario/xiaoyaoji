<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="cn.com.xiaoyaoji.service.DocService" %>
<%@ page import="cn.com.xiaoyaoji.data.bean.Doc" %>
<%@ page import="java.util.List" %><%--
  User: zhoujingjie
  Date: 17/5/7
  Time: 21:40
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Doc doc = (Doc) request.getAttribute("doc");
    List<Doc> docs = DocService.instance().getDocs(doc.getId());
    request.setAttribute("docs",docs);
%>
<div id="folder">
    <div style="padding: 50px 100px;font-size: 16px">
    <c:if test="${docs.size()>0}">
    <ul class="uk-list uk-list-bullet">
        <c:forEach items="${docs}" var="item">
            <li><a href="${ctx}/doc/${item.id}">${item.name}</a></li>
        </c:forEach>
    </ul>
    </c:if>
    </div>
</div>