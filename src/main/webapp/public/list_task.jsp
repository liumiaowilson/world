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

        <title>List Task</title>
    </head>

    <body>
        <%
        String tasks = (String)request.getSession().getAttribute("world-public-tasks");
        if(tasks == null) {
            tasks = "";
        }
        else {
            request.getSession().removeAttribute("world-public-tasks");
        }
        %>
        <%=tasks%>
        <form action="<%=basePath%>/api/task/list_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
