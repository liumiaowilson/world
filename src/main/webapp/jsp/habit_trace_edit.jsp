<%
String page_title = "Habit Trace";
%>
<%@ include file="header.jsp" %>
<%
HabitTrace habit_trace = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
habit_trace = HabitTraceManager.getInstance().getHabitTraceByHabitId(id);
if(habit_trace == null) {
    response.sendRedirect("habit_list.jsp");
    return;
}
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=habit_trace.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="habitName">Habit Name</label>
        <input type="text" class="form-control" id="habitName" value="<%=HabitManager.getInstance().getHabit(habit_trace.habitId).name%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="max_streak">Max Streak</label>
        <input type="text" class="form-control" id="max_streak" value="<%=habit_trace.maxStreak%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="streak">Streak</label>
        <input type="text" class="form-control" id="streak" value="<%=habit_trace.streak%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="last">Last Check</label>
        <%
        String lastCheck = HabitTraceManager.getInstance().getLastTime(habit_trace.habitId, tz);
        if(lastCheck == null) {
            lastCheck = "";
        }
        %>
        <input type="text" class="form-control" id="last" value="<%=lastCheck%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="next">Next Check</label>
        <%
        String nextCheck = HabitTraceManager.getInstance().getNextTime(habit_trace.habitId, tz);
        if(nextCheck == null) {
            nextCheck = "";
        }
        %>
        <input type="text" class="form-control" id="next" value="<%=nextCheck%>" disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn" disabled><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
