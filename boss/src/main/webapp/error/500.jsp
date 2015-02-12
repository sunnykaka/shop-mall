<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.PrintStream" %>
<%@ page isErrorPage="true" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>500错误</title>
</head>
<body>
<h1>不好意思，服务器发生错误了</h1>

<p style="float:right;color:white;">
    <%
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(ostr));
        out.println(ostr.toString());
    %>
</p>

</body>
</html>