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

        <title>Journal</title>
    </head>

    <body>
        <%
        Journal journal = JournalManager.getInstance().getLastCreatedJournal();
        String last = null;
        if(journal == null) {
            last = "Last journal not found.";
        }
        else {
            last = "Last journal is [" + journal.name + "].";
        }
        %>
        <%=last%>
        <form action="<%=basePath%>/api/journal/create_public" method="post">
            <table>
                <tr>
                    <td>Name</td>
                    <td>
                        <input type="text" name="name"/>
                    </td>
                </tr>
                <tr>
                    <td>Weather</td>
                    <td>
                        <input type="text" name="weather"/>
                    </td>
                </tr>
            </table>
            <textarea name="content" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
