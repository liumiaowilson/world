<%
String page_title = "Code Snippet Add";
%>
<%
String selectedCodeLangId = request.getParameter("selectedCodeLangId");
String selectedLangType = "text";
String selectedLangName = null;
try {
    CodeLanguage selectedCodeLang = CodeLanguageManager.getInstance().getCodeLanguage(Integer.parseInt(selectedCodeLangId));
    selectedLangType = selectedCodeLang.type;
    selectedLangName = selectedCodeLang.name;
}
catch(Exception e) {}
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <div class="form-group">
        <label for="languageId">Language</label>
        <select class="combobox form-control" id="languageId">
            <option></option>
            <%
            List<CodeLanguage> languages = CodeLanguageManager.getInstance().getCodeLanguages();
            Collections.sort(languages, new Comparator<CodeLanguage>(){
                public int compare(CodeLanguage l1, CodeLanguage l2) {
                    return l1.name.compareTo(l2.name);
                }
            });
            for(CodeLanguage lang : languages) {
                String selectedStr = lang.name.equals(selectedLangName) ? "selected" : "";
            %>
            <option value="<%=lang.id%>" <%=selectedStr%>><%=lang.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="templateId">Template</label>
        <select class="combobox form-control" id="templateId">
            <option></option>
            <%
            List<CodeTemplate> templates = CodeTemplateManager.getInstance().getCodeTemplates();
            if(selectedLangName != null) {
                templates = CodeSnippetManager.getInstance().getMissingCodeTemplates(Integer.parseInt(selectedCodeLangId));
            }
            Collections.sort(templates, new Comparator<CodeTemplate>(){
                public int compare(CodeTemplate t1, CodeTemplate t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
            for(CodeTemplate template : templates) {
            %>
            <option value="<%=template.id%>"><%=template.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="editor">Content</label>
        <div class="form-control" id="editor"></div>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="save_btn">Save</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var editor = ace.edit("editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/<%=selectedLangType%>");
            $("#editor").css("width", "100%").css("height", "500");

            $(document).ready(function(){
                $('.combobox').combobox();

                $('#languageId').change(function(){
                    var selectedCodeLangId = $('#languageId').val();
                    jumpTo('code_snippet_add.jsp?selectedCodeLangId=' + selectedCodeLangId);
                });

                $('#save_btn').click(function(){
                    var languageId = $('#languageId').val();
                    var templateId = $('#templateId').val();
                    $.post(getAPIURL("api/code_snippet/save"), { 'languageId': $('#languageId').val(), 'templateId': $('#templateId').val(), 'content': editor.getValue() }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            jumpTo('code_snippet_add.jsp?selectedCodeLangId=' + languageId);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

            });
</script>
<%@ include file="footer.jsp" %>
