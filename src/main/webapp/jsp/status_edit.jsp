<%
String page_title = "Status Edit";
%>
<%@ include file="header.jsp" %>
<%
Status status = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
status = StatusManager.getInstance().getStatus(id);
if(status == null) {
    response.sendRedirect("status_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=status.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=status.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="icon">Icon</label>
        <input type="text" class="form-control" id="icon" maxlength="20" placeholder="Enter path of icon" value="<%=status.icon%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=status.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="activator">Activator</label>
        <input type="text" class="form-control" id="activator" maxlength="50" placeholder="Enter activator" value="<%=status.activator%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="deactivator">Deactivator</label>
        <input type="text" class="form-control" id="deactivator" maxlength="50" placeholder="Enter deactivator" value="<%=status.deactivator%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteStatus()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteStatus() {
                bootbox.confirm("Are you sure to delete this status?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/status/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/status/update"), { id: $('#id').val(), name: $('#name').val(), icon: $('#icon').val(), description: $('#description').val(), activator: $('#activator').val(), deactivator: $('#deactivator').val()}, function(data) {
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
