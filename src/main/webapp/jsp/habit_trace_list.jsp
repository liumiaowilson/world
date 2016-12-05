<%
String page_title = "Habit Trace List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Habit Trace List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Links</th>
                </tr>
            </thead>
            <tbody>
                <%
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                List<Habit> habits = HabitManager.getInstance().getCheckableHabits(tz);
                Collections.sort(habits, new Comparator<Habit>(){
                    public int compare(Habit h1, Habit h2) {
                        return h1.name.compareTo(h2.name);
                    }
                });
                for(Habit habit : habits) {
                %>
                <tr>
                    <td><%=habit.name%></td>
                    <td><%=HabitManager.getInstance().getHabitLinksDisplay(habit)%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
