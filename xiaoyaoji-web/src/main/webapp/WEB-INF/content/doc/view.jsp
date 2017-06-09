<%--
  User: zhoujingjie
  Date: 17/4/12
  Time: 22:44
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="doc-header.jsp"/>
<jsp:include page="doc-left.jsp">
    <jsp:param name="view" value="true"/>
</jsp:include>
<div class="doc-content doc-full-width">
    <div class="doc-content-header">
        <div class="doc-ops">
            <a onclick="$('.doc').toggleClass('left-collapsed')">
                <i class="iconfont icon-list2"></i>
            </a>
            <%--<a href=""><i class="iconfont icon-search"></i>
            </a>
            <a href=""><i class="iconfont icon-ziti"></i>
            </a>--%>
        </div>
    </div>
    <c:if test="${editProjectGlobal}">
        <jsp:include page="../project/global/project-global.jsp"/>
    </c:if>
    <c:if test="${!editProjectGlobal && doc!=null }">
        <jsp:include page="/WEB-INF/plugins/${doc.type}/${edit?'edit':'view'}.jsp"/>
    </c:if>
</div>
<!-- loading start -->
<%--<div v-if="status.loading">
    <div class="spinner">
        <div class="double-bounce1"></div>
        <div class="double-bounce2"></div>
    </div>
</div>--%>
</div>
<!-- loading end -->
<script>window._edit_ = '${edit}', _projectName_ = '${project.name}', _projectId_ = '${project.id}', _docId_ = '${docId}'</script>
<script src="${assets}/js/project/header.js"></script>
</body>
</html>