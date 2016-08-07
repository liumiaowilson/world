<%
String page_title = "Goal Def Edit";
%>
<%@ include file="header.jsp" %>
<%
GoalDef goal_def = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
goal_def = GoalDefManager.getInstance().getGoalDef(id);
if(goal_def == null) {
    response.sendRedirect("goal_def_list.jsp");
    return;
}
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datepicker.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=goal_def.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=goal_def.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description"><%=goal_def.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="steps">Steps</label>
        <input type="number" class="form-control" id="steps" placeholder="Enter steps" value="<%=goal_def.steps%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="startTime">Start Time</label>
        <input type="text" class="form-control datepicker" id="startTime" placeholder="Enter start time" value="<%=TimeUtils.toDateString(goal_def.startTime, tz)%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="startAmount">Start Amount</label>
        <input type="number" class="form-control" id="startAmount" placeholder="Enter start amount" value="<%=goal_def.startAmount%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="endTime">End Time</label>
        <input type="text" class="form-control datepicker" id="endTime" placeholder="Enter end time" value="<%=TimeUtils.toDateString(goal_def.endTime, tz)%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="endAmount">End Amount</label>
        <input type="number" class="form-control" id="endAmount" placeholder="Enter end amount" value="<%=goal_def.endAmount%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteGoalDef()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datepicker.jsp" %>
<script>
            function deleteGoalDef() {
                bootbox.confirm("Are you sure to delete this goal def?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/goal_def/delete?id=" + id), function(data){
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
                $('.datepicker').datepicker({
                    format: 'yyyy-mm-dd'
                });
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
                        $.post(getAPIURL("api/goal_def/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'steps': $('#steps').val(), 'startTime': $('#startTime').val(), 'startAmount': $('#startAmount').val(), 'endTime': $('#endTime').val(), 'endAmount': $('#endAmount').val()}, function(data) {
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
