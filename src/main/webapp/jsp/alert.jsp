<%
String page_title = "Alert";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Alerts</h3>
    </div>
    <div class="panel-body">
        <%
        Map<String, Alert> alerts = MonitorManager.getInstance().getAlerts();
        for(String id : alerts.keySet()) {
            Alert alert = alerts.get(id);
            String msg = alert.message;
        %>
        <div class="alert alert-warning" role="alert">
            <strong><%=id%></strong><br/>
            <%=msg%>
            <%
            if(alert.canAck) {
            %>
            <br/>
            <button type="button" class="btn btn-info btn-sm" onclick="javascript:ack('<%=alert.id%>')">OK</button>
            <%
            }
            %>
            <%
            if(alert.url != null) {
            %>
            <br/>
            <button type="button" class="btn btn-info btn-sm" onclick="javascript:action('<%=alert.url%>')">Action</button>
            <%
            }
            %>
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
function action(url) {
    jumpTo(url);
}

function ack(name) {
    $.post(getAPIURL("api/monitor/ack_alert"), { 'name': name }, function(data){
        var status = data.result.status;
        var msg = data.result.message;
        if("OK" == status) {
            showSuccess(msg);
            jumpCurrent();
        }
        else {
            showDanger(msg);
        }
    }, "json");
}
</script>
<%@ include file="footer.jsp" %>
