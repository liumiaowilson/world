<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
NotifyManager.getInstance().notifyReminder("Test Reminder");
%>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Demo</h3>
        </div>
        <div class="panel-body">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
