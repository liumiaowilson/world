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

        <title>List Idea</title>
    </head>

    <body>
        <%
        String ideas = (String)request.getSession().getAttribute("world-public-ideas");
        if(ideas == null) {
            ideas = "";
        }
        else {
            request.getSession().removeAttribute("world-public-ideas");
        }
        %>
        <%=ideas%>
        <form action="<%=basePath%>/api/idea/list_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
