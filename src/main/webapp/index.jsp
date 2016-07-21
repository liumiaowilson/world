<%
String page_title = "World";
%>
<%@ include file="jsp/header.jsp" %>
<%@ include file="jsp/import_css.jsp" %>
<%@ include file="jsp/navbar.jsp" %>
<input type="hidden" id="numOfAlerts" value="<%=MonitorManager.getInstance().getAlerts().size()%>"/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Tasks</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('task_todo.jsp')" class="list-group-item">Todos</a>
            <a href="javascript:jumpTo('task_new.jsp')" class="list-group-item">New Task</a>
            <a href="javascript:jumpTo('task_list.jsp')" class="list-group-item">List Tasks</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Ideas</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('idea_new_batch.jsp')" class="list-group-item">New Idea</a>
            <a href="javascript:jumpTo('idea_list.jsp')" class="list-group-item">List Ideas</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Habits</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('habit_trace_check.jsp')" class="list-group-item">Habit Check</a>
        </div>
    </div>
</div>
<%@ include file="jsp/import_script.jsp" %>
<script>
$(document).ready(function(){
    var numOfAlerts = $('#numOfAlerts').val();
    if(numOfAlerts != '0') {
        var alerts_str;
        if(numOfAlerts == '1') {
            alerts_str = numOfAlerts + " alert is";
        }
        else {
            alerts_str = numOfAlerts + " alerts are";
        }
        showWarning("<strong>" + alerts_str + "</strong> found. Please see <a href=\"javascript:jumpTo('alert.jsp')\">HERE</a>.", true);
    }
});
</script>
<%@ include file="jsp/footer.jsp" %>
