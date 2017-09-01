<%--
  User: zhoujingjie
  Date: 17/4/4
  Time: 12:31
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="hide">
    <script src="${cdn}/assets/jquery/jquery.min.1.11.js"></script>
    <script src="${cdn}/assets/uikit/v3.0.0-beta.30/js/uikit.js"></script>
    <script src="${cdn}/assets/uikit/v3.0.0-beta.30/js/uikit-icons.js"></script>
    <script src="${cdn}/assets/toastr/toastr.min.js"></script>
    <script src="${cdn}/assets/pace/pace.min.js"></script>
    <script src="${cdn}/assets/requirejs/require.min.2.3.3.js"></script>
    <script>
        window.ctx='${ctx}';
        window.x={v:'${v}',ctx:'${ctx}',cdn:'http:${cdn}'};

        requirejs.config({
            baseUrl:'${assets}',
            urlArgs:'v=1',
            paths:{
                'vue':'${cdn}/assets/vue/vue.2.3.3',
                'doc-common':'${assets}/js/doc/doc-common',
                'jquery':'${cdn}/assets/jquery/jquery.min.1.11',
                'veeValidate':'${cdn}/assets/vue/vee-validate.min',
                'vueResource':'${cdn}/assets/vue/vue.resources',
                'vueEx':'${cdn}/assets/vue/vue.ex',
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
            hm.src = "https://hm.baidu.com/hm.js?81159ad58fbecc2d27d9d510ca516684";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>

</div>