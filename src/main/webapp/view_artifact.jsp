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
        String artifact = (String)request.getSession().getAttribute("world-public-artifact");
        if(artifact == null) {
            artifact = "";
        }
        else {
            request.getSession().removeAttribute("world-public-artifact");
        }
        %>
        <%=artifact%>
        <form action="api/artifact/view_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
