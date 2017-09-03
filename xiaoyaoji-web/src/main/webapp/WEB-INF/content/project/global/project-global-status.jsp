<%--
  User: zhoujingjie
  Date: 17/4/25
  Time: 21:20
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../../doc/doc-header.jsp"/>
<style>
    body{
        min-width:inherit;
    }
    .xd-header,.xd-header-placeholder{
        display: none;
    }
</style>
<div id="global">

    <div v-cloak >
        <div>
            <button v-on:click="tempStatus={name:'',value:''}" uk-toggle="target:#modal-status" class="uk-button uk-button-default">新建</button>
        </div>
        <table class="uk-table">
            <thead>
            <tr>
                <th>状态名称</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(item,index) in global.status">
                <td>{{item.name}}</td>
                <td><i v-on:click.stop="tempStatus=item" uk-toggle="target:#modal-status"  class="iconfont icon-edit"></i>
                    <i v-on:click.stop="statusRemove(index)" class="iconfont icon-close"></i></td>
            </tr>
            </tbody>
        </table>
    </div>

    <!-- This is the modal -->
    <div id="modal-status" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title">新建状态</h2>
            <div class="uk-modal-body">
                <label>变量名称:</label>
                <input class="uk-input" type="text" :value="tempStatus.name" v-on:keyup.enter="statusOk" autofocus="" v-model="tempStatus.name">
            </div>
            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close" type="button">取消</button>
                <button class="uk-button uk-button-primary uk-modal-close" v-on:click.enter="statusOk" type="button">确定</button>
            </p>
        </div>
    </div>
</div>
<script>
    var global = ${projectGlobal},_projectId_='${project.id}';
</script>
<script src="${assets}/js/project/global.js"></script>
</div>

</body>
</html>