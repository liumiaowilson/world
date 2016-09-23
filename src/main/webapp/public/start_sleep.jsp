<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Start Sleep</title>
    </head>

    <body>
        <%
        String msg = "In Sleep";
        if(!SleepManager.getInstance().isInSleep()) {
            msg = "Not In Sleep";
        }
        %>
        <%=msg%>
        <form action="api/sleep/start_sleep_public" method="post">
            <table>
                <tr>
                    <td>Key</td>
                    <td>
                        <input type="password" name="key"/>
                    </td>
                </tr>
            </table>
            <br/>
            <input type="submit" value="Start"/>
        </form>
    </body>
</html>
