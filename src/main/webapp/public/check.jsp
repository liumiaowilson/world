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

        <title>Check</title>
    </head>

    <body>
        <form action="<%=basePath%>/api/check/send" method="post">
            <input type="hidden" name="timezone" value=""/>
            <%
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            if(tz != null) {
                List<Habit> habits = HabitManager.getInstance().getCheckableHabits(tz);
                Collections.sort(habits, new Comparator<Habit>(){
                    public int compare(Habit h1, Habit h2) {
                        return h1.name.compareTo(h2.name);
                    }
                });
                for(Habit habit : habits) {
                %>
                <label><input type="checkbox" id="id" name="id" value="<%=habit.id%>"/><%=habit.name%></label><br/>
                <%
                }
            }
                %>
                Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
    <script>
        var dateVar = new Date();
        var timezone = dateVar.getTimezoneOffset()/60 * (-1);
        document.forms[0].children[0].value = timezone;
    </script>
</html>
