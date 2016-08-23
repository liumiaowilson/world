<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>List Festival</title>
    </head>

    <body>
        <%
        String festivals = (String)request.getSession().getAttribute("world-public-festivals");
        if(festivals == null) {
            festivals = "";
        }
        else {
            request.getSession().removeAttribute("world-public-festivals");
        }
        %>
        <%=festivals%>
        <form action="api/festival_data/list_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
