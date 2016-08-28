<%
String page_title = "Code Editor";
%>
<%
String lang = request.getParameter("lang");
if(lang == null) {
    lang = "text";
}
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="editor">Editor</label>
        <div class="form-control" id="editor"></div>
    </fieldset>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var editor = ace.edit("editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/<%=lang%>");
            $("#editor").css("width", "100%").css("height", "500");
</script>
<%@ include file="footer.jsp" %>
