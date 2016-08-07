<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" style="width:40%">
                Free Space
            </div>
            <div class="progress-bar progress-bar-warning" role="progressbar" style="width:10%">
                Warning
            </div>
            <div class="progress-bar progress-bar-danger" role="progressbar" style="width:20%">
                Danger
            </div>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
