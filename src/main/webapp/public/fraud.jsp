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

        <title>Fraud</title>
    </head>

    <body>
        <%
        Fraud fraud = FraudManager.getInstance().getLastCreatedFraud();
        String last = null;
        if(fraud == null) {
            last = "Last fraud not found.";
        }
        else {
            last = "Last fraud is [" + fraud.name + "].";
        }
        %>
        <%=last%>
        <form action="<%=basePath%>/api/fraud/create_public" method="post">
            <table>
                <tr>
                    <td>Name</td>
                    <td>
                        <input type="text" name="name"/>
                    </td>
                </tr>
            </table>
            <textarea name="content" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
    <script>
    </script>
</html>
