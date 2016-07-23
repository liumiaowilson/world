<%
String page_title = "Habit Edit";
%>
<%@ include file="header.jsp" %>
<%
Habit habit = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
habit = HabitManager.getInstance().getHabit(id);
if(habit == null) {
    response.sendRedirect("habit_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=habit.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=habit.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=habit.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="interval">Interval</label>
        <input type="number" class="form-control" id="interval" placeholder="Enter interval" value="<%=habit.interval%>">
        <small class="text-muted">Intervals are based on days.</small>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <%
                HabitTrace trace = HabitTraceManager.getInstance().getHabitTraceByHabitId(habit.id);
                String disabled = (trace == null ? "disabled" : "");
                %>
                <li class="<%=disabled%>"><a href="javascript:void(0)" onclick="viewHabitTrace()">View Trace</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteHabit()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function viewHabitTrace() {
                jumpTo("habit_trace_edit.jsp?id=" + $('#id').val())
            }
            function deleteHabit() {
                bootbox.confirm("Are you sure to delete this habit?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/habit/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/habit/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), interval: $('#interval').val()}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
