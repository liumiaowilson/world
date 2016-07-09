<%
String from_url = "index.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<input type="hidden" id="numOfAlerts" value="<%=MonitorManager.getInstance().getAlerts().size()%>"/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quick Links</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="idea_new_batch.jsp" class="list-group-item">New Idea</a>
            <a href="idea_list.jsp" class="list-group-item">List Ideas</a>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
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
        showWarning("<strong>" + alerts_str + "</strong> found. Please see <a href='alert.jsp'>HERE</a>.", true);
    }
});
</script>
<%@ include file="footer.jsp" %>
