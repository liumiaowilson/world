<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Demo</h3>
        </div>
        <div class="panel-body">
            <div id="getting-started"></div>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_countdown.jsp" %>
<script>
$("#getting-started").countdown("2017/01/01", function(event) {
    $(this).text(
        event.strftime('%D days %H:%M:%S')
    );
});
</script>
<%@ include file="footer.jsp" %>
