<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 12:31
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style="display: none">
    <script src="${assets}/jquery/jquery.min.1.11.js?v=${v}"></script>
    <script src="${assets}/uikit/js/uikit.js?v=${v}"></script>
    <script src="${assets}/uikit/js/uikit-icons.js?v=${v}"></script>
    <script src="${assets}/toastr/toastr.min.js?v=${v}"></script>
    <script src="${assets}/pace/pace.min.js?v=${v}"></script>
    <script src="${assets}/requirejs/require.min.2.3.3.js?v=${v}"></script>
    <script>
        window.ctx='${ctx}';
        toastr.options.escapeHtml = true;
        toastr.options.closeButton = true;
        toastr.options.positionClass = 'toast-top-center';
        toastr.options.preventDuplicates=true;
        requirejs.config({
            baseUrl:'${assets}',
            urlArgs:'v=${v}',
            paths:{
                'vue':'vue/vue.2.3.3',
                'jquery':'jquery/jquery.min.1.11',
                'vueValidator':'vue/vue-validator.min',
                'veeValidate':'vue/vee-validate.min',
                'vueResource':'vue/vue.resources',
                'vueEx':'vue/vue.ex',
                'utils':'js/utils'
            }
        });

        $(function(){
            setTimeout(function(){
                $('.uk-icon').each(function(){
                    if($(this).find('svg').size()>1){
                        $(this).find('svg:eq(0)').remove();
                    }
                });
            },500);
        });
    </script>

    <script>
        var _hmt = _hmt || [];
        (function() {
            var hm = document.createElement("script");
            hm.src = "https://hm.baidu.com/hm.js?9bd56a6d0766b887592ee921aa94763f";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
</div>