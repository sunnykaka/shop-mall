<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.PrintStream" %>
<%@ page isErrorPage="true" language="java" pageEncoding="UTF-8" %>


<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="description" content="" />
    <meta name="keywords" content="" />
    <title>内部服务器错误 - 易居尚</title>
    <link rel="stylesheet" href="http://assets.yijushang.com/css/error.css" />
</head>

<body>
<div class="error500">
    <div class="img_500_01">&nbsp;</div>
    <a href="http://www.yijushang.com" class="back_home">返回首页</a>
    <p style="float:right;color:white;">
        <%
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(ostr));
            out.println(ostr.toString());
        %>
    </p>
</div>
</body>
</html>