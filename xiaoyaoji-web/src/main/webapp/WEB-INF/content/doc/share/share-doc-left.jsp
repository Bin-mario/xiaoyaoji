<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  User: zhoujingjie
  Date: 17/4/16
  Time: 16:25
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="doc-left" id="docLeft">
    <div class="dl-content" id="doc-names">
        <ul class="dl-docs" v-show="!showSearch">
            <c:if test="${!edit}">
                <li class="cb">
                    <div class="dl-doc dl-project-name">
                        <div class="doc-name cb ">
                            <span class="el-tree-expand is-leaf"></span>
                            ${project.name}
                        </div>
                    </div>
                </li>
                <li class="divider"></li>
            </c:if>
            <c:forEach items="${docs}" var="doc">
                <c:set var="item" value="${doc}" scope="request"/>
                <jsp:include page="share-doc-left-item.jsp"/>
            </c:forEach>
        </ul>
    </div>

    <div class="dl-placehoder" v-cloak>
        本文档由<a href="http://www.xiaoyaoji.com.cn" target="_blank">小幺鸡</a>编辑
    </div>
</div>
<script>window._projectId_ = '${project.id}';
window._edit_ = '${edit}'</script>
<script src="${assets}/js/project/left.js?v=${v}"></script>