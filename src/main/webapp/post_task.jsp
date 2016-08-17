<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Task</title>
    </head>

    <body>
        <%
        Task task = TaskManager.getInstance().getLastCreatedTask();
        String last = null;
        if(task == null) {
            last = "Last task not found.";
        }
        else {
            last = "Last task is [" + task.name + "].";
        }
        %>
        <%=last%>
        <form action="api/task/create_public" method="post">
            <textarea name="content" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
