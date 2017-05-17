<%@ page import="org.wilson.world.file.*" %>
<%
String page_title = "Local File Backup Edit";
%>
<%@ include file="header.jsp" %>
<%
LocalFileInfo local_file = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
local_file = LocalFileManager.getInstance().getBackupLocalFiles().get(id);
if(local_file == null) {
    response.sendRedirect("local_file_backup_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=local_file.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Name like /abc/xyz" value="<%=local_file.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <div class="form-control" id="content" required><%=FormatUtils.escapeHtml(local_file.content)%></div>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="save_btn" disabled>Save</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="restoreLocalFile()">Restore</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var content = ace.edit("content");
            content.setTheme("ace/theme/monokai");
            content.getSession().setMode("ace/mode/<%=DataFileManager.getInstance().getFileSyntax(local_file.name)%>");
            $("#content").css("width", "100%").css("height", "400");

            function restoreLocalFile() {
                bootbox.confirm("Are you sure to restore this local file?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/local_file/restore_backup?id=" + id), function(data){
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
</script>
<%@ include file="footer.jsp" %>
