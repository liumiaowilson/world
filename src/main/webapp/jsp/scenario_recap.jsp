<%
String page_title = "Scenario Recap";
%>
<%@ include file="header.jsp" %>
<%
Scenario scenario = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
scenario = ScenarioManager.getInstance().getScenario(id);
if(scenario == null) {
    response.sendRedirect("scenario_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Scenario Recap (from: <%=scenario.name%>)</h3>
    </div>
    <div class="panel-body">
        <fieldset class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" id="description" rows="5" maxlength="400" placeholder="Enter detailed description"><%=scenario.reaction%></textarea>
            <small class="text-muted">Recap what you have come up with.</small>
        </fieldset>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-default disabled" id="left_btn">
        <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default" id="right_btn">
        <span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
    </button>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function(){
    $('#right_btn').click(function(){
        $.post(getAPIURL("api/scenario/recap"), { id: <%=scenario.id%>, description: $('#description').val() }, function(data) {
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                showSuccess(msg);
                jumpTo("scenario_list.jsp");
            }
            else {
                showDanger(msg);
            }
        }, "json");
    });
});
</script>
<%@ include file="footer.jsp" %>
