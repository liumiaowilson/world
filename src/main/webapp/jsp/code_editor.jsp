<%
String page_title = "Code Editor";
%>
<%
String lang = request.getParameter("lang");
if(lang == null) {
    lang = "text";
}
Map<String, String> langs = new HashMap<String, String>();
langs.put("Text", "text");
langs.put("Bash", "sh");
langs.put("SQL", "sql");
langs.put("JavaScript", "javascript");
langs.put("Java", "java");
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <div class="form-group">
        <label for="lang">Language</label>
        <select class="combobox form-control" id="lang">
            <option></option>
            <%
            List<String> keys = new ArrayList<String>(langs.keySet());
            Collections.sort(keys);
            for(String key : keys) {
                String value = langs.get(key);
                boolean selected = (value.equals(lang));
                String selectedStr = (selected ? "selected" : "");
            %>
            <option value="<%=value%>" <%=selectedStr%>><%=key%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="apply_btn">Apply</button>
    </div>
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

            $('.combobox').combobox();

            $('#apply_btn').click(function(){
                jumpTo("code_editor.jsp?lang=" + $('#lang').val());
            });
</script>
<%@ include file="footer.jsp" %>
