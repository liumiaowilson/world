<%
String page_title = "Management";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
if(ConfigManager.getInstance().isOpenShiftApp()) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">OpenShift</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            String uuid = System.getenv("OPENSHIFT_APP_UUID");
            String name = System.getenv("OPENSHIFT_APP_NAME");
            %>
            <a href="https://openshift.redhat.com/app/console/application/<%=uuid%>-<%=name%>/restart" class="list-group-item">Restart Application</a>
            <a href="https://openshift.redhat.com/app/console/application/<%=uuid%>-<%=name%>" class="list-group-item">OpenShift Console</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Jenkins</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            String jenkins_url = System.getenv("JENKINS_URL");
            String jenkins_username = System.getenv("JENKINS_USERNAME");
            String jenkins_password = System.getenv("JENKINS_PASSWORD");
            %>
            <a href="<%=jenkins_url%>" class="list-group-item">Jenkins Server</a>
        </div>
        <div class="alert alert-info" role="alert">
            Username is <strong><%=jenkins_username%></strong> and password is <strong><%=jenkins_password%></strong>.
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">UptimeRobot</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            String uptimerobot_url = "https://uptimerobot.com/dashboard.php#mainDashboard";
            String uptimerobot_username = DataManager.getInstance().getValue("uptimerobot.username");
            String uptimerobot_password = DataManager.getInstance().getValue("uptimerobot.password");
            %>
            <a href="<%=uptimerobot_url%>" class="list-group-item">UptimeRobot Dashboard</a>
        </div>
        <%
        if(uptimerobot_username != null) {
        %>
        <div class="alert alert-info" role="alert">
            Username is <strong><%=uptimerobot_username%></strong> and password is <strong><%=uptimerobot_password%></strong>.
        </div>
        <%
        }
        %>
    </div>
</div>
<%
}
else {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">OpenShift</h3>
    </div>
    <div class="panel-body">
        <div class="alert alert-warning" role="alert">
            Management is not enabled unless run on OpenShift.
        </div>
    </div>
</div>
<%
}
%>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
