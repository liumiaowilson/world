<%
String page_title = "Goal Report";
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
Goal goal = GoalManager.getInstance().getNextGoal(goal_def);
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datepicker.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=goal_def.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" disabled><%=goal_def.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="endTime">Next Time</label>
        <input type="text" class="form-control datepicker" id="endTime" placeholder="Enter end time" value="<%=TimeUtils.toDateString(goal.time, tz)%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="endAmount">Next Amount</label>
        <input type="number" class="form-control" id="endAmount" placeholder="Enter end amount" value="<%=goal.amount%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="time">Time</label>
        <input type="text" class="form-control datepicker" id="time" placeholder="Enter time" value="<%=TimeUtils.toDateString(new Date(), tz)%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="amount">Amount</label>
        <input type="number" class="form-control" id="amount" placeholder="Enter amount" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datepicker.jsp" %>
<script>
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

                        l.ladda('start');
                        $.post(getAPIURL("api/goal/report"), { defId: <%=id%>, 'time': $('#time').val(), 'amount': $('#amount').val() }, function(data) {
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
