<%--
  User: zhoujingjie
  Date: 17/4/12
  Time: 22:44
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: zhoujingjie
  Date: 17/4/12
  Time: 22:15
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:if test="${!isXHR}">
    <jsp:include page="doc-header.jsp"/>
    <jsp:include page="doc-left.jsp"/>
    <div class="doc-content" >
    <div class="hide" id="loading">
        <div class="spinner">
            <div class="double-bounce1"></div>
            <div class="double-bounce2"></div>
        </div>
    </div>
    <div id="doc-content">
</c:if>
<c:if test="${editProjectGlobal}">
    <jsp:include page="../project/global/project-global.jsp"/>
</c:if>
<c:if test="${!editProjectGlobal && doc!=null}">
    <c:if test="${pluginInfo == null}">
        <jsp:include page="/WEB-INF/includes/doc-type-not-support.jsp"/>
    </c:if>
    <c:if test="${pluginInfo != null}">
        <jsp:include page="/WEB-INF/plugins/${pluginInfo.runtimeFolder}/web/${pluginInfo.plugin.editPage}"/>
    </c:if>
</c:if>
<script>window._edit_ = '${edit}', _projectName_ = '${project.name}', _projectId_ = '${project.id}', _docId_ = '${docId}'</script>
<c:if test="${!isXHR}">
    <button class="doc-save-button" v-cloak uk-toggle="target: #save-desc">保存</button>

    <div id="save-desc" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title">保存文档</h2>
            <p class="uk-margin"><textarea autofocus class="uk-textarea" rows="5" v-model="submitComment"
                                           placeholder="可以在这儿输入一些备注"></textarea></p>
            <p style="color: rgb(204, 204, 204);">提示:CTRL+S 可快速保存</p>
            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                <button class="uk-button uk-button-primary uk-modal-close" type="button" v-on:click="submit">
                    保存
                </button>
            </p>
        </div>
    </div>

    </div>
    </div>
    </div>
    <script src="${assets}/js/project/header.js"></script>
    </body>
    </html>
</c:if>