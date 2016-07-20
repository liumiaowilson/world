<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quick Links</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            if(ConfigManager.getInstance().isOpenShiftApp()) {
            String uuid = System.getenv("OPENSHIFT_APP_UUID");
            String name = System.getenv("OPENSHIFT_APP_NAME");
            %>
            <a href="https://openshift.redhat.com/app/console/application/<%=uuid%>-<%=name%>" class="list-group-item">OpenShift Console</a>
            <%
            }
            %>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
