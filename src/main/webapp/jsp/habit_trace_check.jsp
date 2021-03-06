<%
String page_title = "Habit Check";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Habit Check</h3>
    </div>
    <div class="panel-body">
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
        <div class="checkbox">
            <label><input type="checkbox" value="<%=habit.id%>"><%=habit.name%> <%=HabitManager.getInstance().getHabitLinksDisplay(habit)%><button type="button" class="btn btn-success btn-xs" onclick="checkHabit(<%=habit.id%>)">Done</button></label>
        </div>
        <%
        }
        %>
        <button type="button" class="btn btn-danger ladda-button" id="check_btn"><span class="ladda-label">Check</span></button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            var l = $('#check_btn').ladda();

            function checkHabit(id) {
                var ids = [];
                if(id) {
                    ids.push(id);
                }
                else {
                    $('input[type=checkbox]').each(function () {
                        if (this.checked) {
                            ids.push(this.value);
                        }
                    });
                }
                if(ids.length > 0) {
                    l.ladda('start');
                    $.get(getAPIURL("api/habit_trace/check?ids=" + ids), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            l.ladda('stop');
                            jumpCurrent();
                        }
                        else {
                            showDanger(msg);
                            l.ladda('stop');
                        }
                    });
                }
            }

            $(document).ready(function(){
                $('#check_btn').click(function(){
                    checkHabit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
