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
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
