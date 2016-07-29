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
        <textarea id="editor"></textarea>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_richtexteditor.jsp" %>
<script>
$(document).ready(function() {
    tinymce.init({
        selector: '#editor'
    });
});
</script>
<%@ include file="footer.jsp" %>
