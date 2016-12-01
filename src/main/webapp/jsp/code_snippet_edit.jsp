<%
String page_title = "Code Snippet Edit";
%>
<%@ include file="header.jsp" %>
<%
CodeSnippet code_snippet = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
code_snippet = CodeSnippetManager.getInstance().getCodeSnippet(id);
if(code_snippet == null) {
    response.sendRedirect("code_snippet_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=code_snippet.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="languageId">Language</label>
        <%
        CodeLanguage lang = CodeLanguageManager.getInstance().getCodeLanguage(code_snippet.languageId);
        String selectedLangType = lang.type;
        %>
        <input type="text" class="form-control" id="languageId" maxlength="20" placeholder="Enter language" value="<%=lang.name%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="templateId">Template</label>
        <%
        CodeTemplate template = CodeTemplateManager.getInstance().getCodeTemplate(code_snippet.templateId);
        %>
        <input type="text" class="form-control" id="templateId" maxlength="20" placeholder="Enter template" value="<%=template.name%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="editor">Content</label>
        <div class="form-control" id="editor"><%=code_snippet.content%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteCodeSnippet()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var editor = ace.edit("editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/<%=selectedLangType%>");
            $("#editor").css("width", "100%").css("height", "500");

            function deleteCodeSnippet() {
                bootbox.confirm("Are you sure to delete this code_snippet?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/code_snippet/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }

            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post(getAPIURL("api/code_snippet/update"), { id: $('#id').val(), 'languageId': <%=lang.id%>, 'templateId': <%=template.id%>, 'content': editor.getValue() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
