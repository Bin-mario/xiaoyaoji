<%--
  User: zhoujingjie
  Date: 17/4/12
  Time: 22:44
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../doc-header.jsp"/>


<div class="uk-modal uk-open" style="display: block;" id="share-password-box">
    <div class="uk-modal-dialog ">
        <div class="uk-modal-header">
            <h2 class="uk-modal-title">${share.name}</h2>
        </div>
        <div class="uk-modal-body">
            <div class="uk-form-stacked">
                <div class="uk-margin">
                    <label class="uk-form-label" for="form-stacked-text">阅读密码</label>
                    <div class="uk-form-controls">
                        <input class="uk-input" id="form-stacked-text" v-on:keyup.enter="check" v-model="password" type="text" placeholder="请输入分享密码">
                    </div>
                </div>
            </div>
        </div>
        <p class="uk-modal-footer uk-text-right">
            <button class="uk-button uk-button-primary" v-on:click="check" type="button">确定</button>
        </p>
    </div>
</div>

<script>
    require(['vue','utils'],function(Vue,utils){
        new Vue({
            el:'#share-password-box',
            data:{
                password:''
            },
            methods:{
                check:function(){
                    if(!this.password){
                        toastr.error('请输入密码');
                    }else{
                        location.href='?password='+this.password;
                    }
                }
            }

        });
    });
</script>

<%--</div>--%>
<!-- loading end -->
</body>
</html>
