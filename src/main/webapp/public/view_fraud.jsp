<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <title>View Artifact</title>
    </head>

    <body>
        <%
        String fraud = (String)request.getSession().getAttribute("world-public-fraud");
        if(fraud == null) {
            fraud = "";
        }
        else {
            request.getSession().removeAttribute("world-public-fraud");
        }
        %>
        <%=fraud%>
        <form action="api/fraud/view_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
