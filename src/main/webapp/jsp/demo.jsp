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
            <div id="editor">function foo(items) {
                var x = "All this is syntax highlighted";
                return x;
                }</div>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
var editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");
editor.getSession().setMode("ace/mode/javascript");
$("#editor").css("width", "100%").css("height", "500");
</script>
<%@ include file="footer.jsp" %>
