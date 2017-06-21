<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/12
  Time: 22:17
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<li class="cb name-item" data-id="${item.id}"  data-name="${item.name}" data-type="${item.type}" data-sort="${item.sort}">
    <div class="dl-doc">
        <div class="doc-name cb ${docId == item.id?'active':''} _ ${item.children.size() ==0 ?'':'folder'}" v-on:click.stop="fold($event)">
            <span class="dl-background"></span>
            <span class="el-tree-expand ${item.children.size() ==0 ?'is-leaf':''}"></span>
            <a class="item-name" href="${ctx}/doc/${item.id}${edit?'/edit':''}">${item.name}</a href="${ctx}/doc/${item.id}">
            <c:if test="${item.type=='sys.folder' && edit}">
                <i class="iconfont fr icon-angeldownblock" v-on:click.stop="contextMenu"></i>
            </c:if>
        </div>
        <c:if test="${item.children.size()>0}">
        <ul class="dl-docs dl-docs-sub hide" data-id="${item.id}" >
            <c:forEach items="${item.children}" var="item">
                <c:set var="item" value="${item}" scope="request"/>
                <jsp:include page="doc-left-item.jsp"/>
            </c:forEach>
        </ul>
        </c:if>
    </div>
</li>