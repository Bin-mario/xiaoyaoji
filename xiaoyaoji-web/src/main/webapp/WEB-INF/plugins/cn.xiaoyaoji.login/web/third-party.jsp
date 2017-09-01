<%--
  User: zhoujingjie
  Date: 16/9/2
  Time: 19:54
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
    <title></title>
</head>
<body>
<script>
    var data={
        'accessToken':'${accessToken}',
        'thirdpartyId':'${thirdpartyId}',
        'type':'${type}'
    };
    window.opener.postMessage(JSON.stringify(data),'*');
    window.close();
</script>
</body>
</html>