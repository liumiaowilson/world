<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <title>View Novel Document</title>
    </head>

    <body>
        <%
        String novel_document = (String)request.getSession().getAttribute("world-public-novel_document");
        if(novel_document == null) {
            novel_document = "";
        }
        else {
            request.getSession().removeAttribute("world-public-novel_document");
        }
        %>
        <%=novel_document%>
        <form action="<%=basePath%>/api/novel_document/view_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
