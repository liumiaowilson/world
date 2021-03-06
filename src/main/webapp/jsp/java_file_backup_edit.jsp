<%@ page import="org.wilson.world.java.*" %>
<%
String page_title = "Java File Backup Edit";
%>
<%@ include file="header.jsp" %>
<%
JavaFile java_file = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
java_file = JavaFileManager.getInstance().getBackupJavaFiles().get(id);
if(java_file == null) {
    response.sendRedirect("java_file_backup_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=java_file.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=java_file.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=java_file.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="source">Source</label>
        <div class="form-control" id="source" required><%=FormatUtils.escapeHtml(java_file.source)%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="script">Script</label>
        <div class="form-control" id="script"><%=FormatUtils.escapeHtml(java_file.script)%></div>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="save_btn" disabled>Save</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="restoreJavaFile()">Restore</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var source = ace.edit("source");
            source.setTheme("ace/theme/monokai");
            source.getSession().setMode("ace/mode/java");
            $("#source").css("width", "100%").css("height", "400");

            var script = ace.edit("script");
            script.setTheme("ace/theme/monokai");
            script.getSession().setMode("ace/mode/javascript");
            $("#script").css("width", "100%").css("height", "400");

            function restoreJavaFile() {
                bootbox.confirm("Are you sure to restore this java file?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/java_file/restore_backup?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg, true);
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
