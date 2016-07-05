<%@ page import="java.util.*" %>
<%@ page import="org.wilson.world.manager.*" %>
<%
String from_url = "log.jsp";
%>
<%@ include file="header.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Logs</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            if(!ConfigManager.getInstance().isOpenShiftApp()) {
            %>
            This feature is only enabled on openshift hosted apps.
            <%
            }
            else {
                String log = ConsoleManager.getInstance().run("tail -200 ../app-root/logs/jbossews.log");
                log = log.replaceAll("\n", "<br/>");
            %>
            <%=log%>
            <%
            }
            %>
        </div>
    </div>
</div>
<%@ include file="import_scripts.jsp" %>
<%@ include file="footer.jsp" %>
