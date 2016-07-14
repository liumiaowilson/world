<%
String from_url = "alert.jsp";
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
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
function ack(name) {
    $.post("api/monitor/ack_alert", { 'name': name }, function(data){
        var status = data.result.status;
        var msg = data.result.message;
        if("OK" == status) {
            showSuccess(msg);
            window.location.href = "alert.jsp";
        }
        else {
            showDanger(msg);
        }
    }, "json");
}
</script>
<%@ include file="footer.jsp" %>
