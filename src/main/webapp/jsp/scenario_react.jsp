<%
String page_title = "Scenario React";
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
        <h3 class="panel-title">Scenario React (from: <%=scenario.name%>)</h3>
    </div>
    <div class="panel-body">
        <fieldset class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" id="description" rows="5" maxlength="400" placeholder="Enter detailed description"></textarea>
            <small class="text-muted">The more detailed the description is, the bigger chance to earn more!</small>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-default disabled" id="left_btn">
        <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default disabled" id="right_btn">
        <span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
    </button>
</div>
<%@ include file="import_script.jsp" %>
<script>
var debug = <%=ConfigManager.getInstance().isInDebugMode()%>;
var period = 1000;
if(debug) {
    period = 10;
}
function stopReact() {
    $('#description').prop('disabled', true);
    $('#right_btn').removeClass('disabled');
}

$(document).ready(function(){
    var progress = setInterval(function() {
        var bar = $('.progress-bar');
        var value = parseInt(bar.attr("aria-valuenow"));
        if(value >= 101) {
            clearInterval(progress);
            stopReact();
        }
        else {
            value = value + 1;
            bar.attr("aria-valuenow", value);
            bar.css("width", value + "%");
        }
    }, period);

    $('#right_btn').click(function(){
        $.post(getAPIURL("api/scenario/react"), { id: <%=scenario.id%>, description: $('#description').val() }, function(data) {
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                showSuccess(msg);
                jumpTo("scenario_recap.jsp?id=<%=scenario.id%>");
            }
            else {
                showDanger(msg);
            }
        }, "json");
    });
});
</script>
<%@ include file="footer.jsp" %>
