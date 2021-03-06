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

        <title>Finish Task</title>
    </head>

    <body>
        <form action="<%=basePath%>/api/task/finish_outdoor" method="post">
                <%
                List<Task> tasks = TaskManager.getInstance().getOutdoorTasks();
                Collections.sort(tasks, new Comparator<Task>(){
                    public int compare(Task t1, Task t2) {
                        return t1.name.compareTo(t2.name);
                    }
                });
                for(Task task : tasks) {
                %>
                <label><input type="checkbox" id="id" name="id" value="<%=task.id%>"/><%=task.name%></label><br/>
                <%
                }
                %>
                Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Finish"/>
        </form>
    </body>
    <script>
    </script>
</html>
