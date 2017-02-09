<%@ page import="org.wilson.world.file.*" %>
<%
String page_title = "Remote File Edit";
%>
<%@ include file="header.jsp" %>
<%
RemoteFile remote_file = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
remote_file = RemoteFileManager.getInstance().getRemoteFile(id);
if(remote_file == null) {
    response.sendRedirect("remote_file_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=remote_file.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Name like /abc/xyz" value="<%=remote_file.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <div class="form-control" id="content" required><%=remote_file.getContent()%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteRemoteFile()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var content = ace.edit("content");
            content.setTheme("ace/theme/monokai");
            content.getSession().setMode("ace/mode/<%=DataFileManager.getInstance().getFileSyntax(remote_file.name)%>");
            $("#content").css("width", "100%").css("height", "400");

            function deleteRemoteFile() {
                bootbox.confirm("Are you sure to delete this remote file?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/remote_file/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/remote_file/update"), { id: $('#id').val(), name: $('#name').val(), content: content.getValue() }, function(data) {
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
