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

        <title>Alert</title>
    </head>

    <body>
        <%
        for(Alert alert : MonitorManager.getInstance().getAlerts().values()) {
        %>
        <p><b><%=alert.id%></b></p>
        <p><i><%=alert.message%></i></p>
        <hr/>
        <%
        }
        %>
    </body>
    <script>
    </script>
</html>
