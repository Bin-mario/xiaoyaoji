<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="cn.com.xiaoyaoji.core.global.GlobalProperties" %>
<%@ page import="cn.com.xiaoyaoji.data.bean.Doc" %>
<%@ page import="cn.com.xiaoyaoji.data.bean.Project" %>
<%@ page import="cn.com.xiaoyaoji.service.ProjectService" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.List" %>
<%--
  User: zhoujingjie
  Date: 17/4/16
  Time: 16:25
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Project project = (Project) request.getAttribute("project");
    List<Doc> docs = ProjectService.instance().getProjectDocs(project.getId());
    request.setAttribute("docs", docs);
    String docId = (String) request.getAttribute("docId");
    if (StringUtils.isBlank(docId) && docs.size() > 0) {
        String firstDocId = docs.get(0).getId();
        request.setAttribute("docId", firstDocId);
    }
    request.setAttribute("createSupports", GlobalProperties.getCreateDocuments());
    request.setAttribute("importDocuments", GlobalProperties.getImportDocuments());
%>
<div class="doc-left" id="docLeft">
    <c:if test="${edit}">
        <div class="dl-doc-actions cb">
            <div class="fl dl-doc-action">
                <a><i class="el-icon-plus"></i>新建</a>
                <ul class="dl-menus hide">
                    <c:forEach items="${createSupports}" var="sup">
                        <li uk-toggle="target: #docCreateModal" v-on:click="createFn('${sup.type}',0)">
                            <div data-type="${sup.type}" class="dl-menu-name">${sup.name}</div>
                        </li>
                    </c:forEach>
                </ul>
            </div>
            <%--<div class="fl dl-doc-action">
                <a><i class="el-icon-upload"></i>导入</a>
                <ul class="dl-menus hide">
                    <c:forEach var="document" items="${importDocuments}">
                        <li>
                            <div data-type="${document.type}" class="dl-menu-name">${document.name}</div>
                        </li>
                    </c:forEach>
                </ul>
            </div>--%>
        </div>
    </c:if>

    <input type="text" class="doc-search" v-model="searchText" v-on:keyup.enter="search" placeholder="搜索...">
    <div class="dl-content" id="doc-names">
        <ul class="dl-docs" v-on:contextmenu.prevent="contextMenu($event)" v-show="!showSearch">
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
                <jsp:include page="doc-left-item.jsp"/>
            </c:forEach>
        </ul>
        <ul class="dl-docs doc-search-result" v-cloak v-show="showSearch">
            <li class="cb" v-for="item in searchResults">
                <div class="dl-doc">
                    <div class="doc-name cb ">
                        <span class="dl-background"></span>
                        <a class="item-name" :href="ctx+'/doc/'+(item.id)+(edit?'/edit':'')">{{item.name}}</a>
                    </div>
                </div>
            </li>
            <div v-show="searchResults.length == 0" class="searh-no-results">
                没有找到对应的结果
            </div>
        </ul>
    </div>

    <ul class="dl-menus" id="dl-menus" v-on:click.stop v-cloak v-show="menu.show"
        v-bind:style="{top:menu.top+'px',left:menu.left+'px'}">
        <li v-if="menu.isFolder">
            <div class="dl-menu-name folder">新建</div>
            <ul class="dl-menus sub">
                <c:forEach items="${createSupports}" var="sup">
                    <li uk-toggle="target: #docCreateModal" v-on:click="createFn('${sup.type}')">
                        <div data-type="${sup.type}" class="dl-menu-name">${sup.name}</div>
                    </li>
                </c:forEach>
            </ul>
        </li>
        <%--<c:if test="${importDocuments != null && importDocuments.size()>0}">
            <li v-if="menu.isFolder">
                <div class="dl-menu-name folder">导入</div>
                <ul class="dl-menus sub dl-docs">
                    <c:forEach var="document" items="${importDocuments}">
                        <li>
                            <div data-type="${document.type}" class="dl-menu-name">${document.name}</div>
                        </li>
                    </c:forEach>
                </ul>
            </li>
        </c:if>--%>
        <li class="line" v-if="menu.isFolder"></li>
        <%--<li>
            <div class="dl-menu-name">移动</div>
        </li>--%>
        <li>
            <div class="dl-menu-name">复制</div>
        </li>
        <li v-on:click="updateName($event)" uk-toggle="target: #docCreateModal">
            <div class="dl-menu-name">重命名</div>
        </li>
        <li v-on:click="deleteDoc">
            <div class="dl-menu-name">删除</div>
        </li>
    </ul>

    <div uk-modal id="docCreateModal" v-cloak>
        <div class="uk-modal-dialog">
            <div class="uk-modal-body">
                <div>
                    <label>请输入名称:</label>
                    <input class="uk-input" v-model="createModal.value" v-on:keyup.enter="createSubmit" :value="createModal.value" type="text"
                           autofocus="">
                </div>
            </div>
            <div class="uk-modal-footer uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                <button class="uk-button uk-button-primary" v-on:click="createSubmit">{{createModal.id?'更新':'创建'}}
                </button>
            </div>
        </div>
    </div>

    <div class="dl-placehoder" v-cloak>
        本文档由<a href="http://www.xiaoyaoji.com.cn" target="_blank">小幺鸡</a>编辑
    </div>
</div>
<script>window._projectId_ = '${project.id}';
window._edit_ = '${edit}'</script>
<script src="${assets}/js/project/left.js"></script>