<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Finish Task</title>
    </head>

    <body>
        <form action="api/task/finish_outdoor" method="post">
            <input type="hidden" name="timezone" value=""/>
                <%
                List<Task> tasks = TaskManager.getInstance().getOutdoorTasks();
                Collections.sort(tasks, new Comparator<Task>(){
                    public int compare(Task t1, Task t2) {
                        return t1.name.compareTo(t2.name);
                    }
                });
                for(Task task : tasks) {
                %>
                <input type="checkbox" id="id" name="id" value="<%=task.id%>"/><%=task.name%><br/>
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
