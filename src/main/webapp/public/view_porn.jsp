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

        <title>View Porn</title>
    </head>

    <body>
        <%
        String porn = (String)request.getSession().getAttribute("world-public-porn");
        if(porn == null) {
            porn = "";
        }
        else {
            request.getSession().removeAttribute("world-public-porn");
        }
        %>
        <img src="<%=porn%>" alt="porn"/>
        <form action="<%=basePath%>/api/porn/view_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
