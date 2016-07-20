<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_colorpicker.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <input id="cp1" type="text" class="form-control" value="#5367ce" />
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_colorpicker.jsp" %>
<script>
    $(function() {
        $('#cp1').colorpicker();
    });
</script>
<%@ include file="footer.jsp" %>
