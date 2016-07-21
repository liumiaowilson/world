<%
String page_title = "Task Seed Edit";
%>
<%@ include file="header.jsp" %>
<%
TaskSeed task_seed = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
task_seed = TaskSeedManager.getInstance().getTaskSeed(id);
if(task_seed == null) {
    response.sendRedirect("task_seed_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=task_seed.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=task_seed.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="pattern">Pattern</label>
        <input type="text" class="form-control" id="pattern" maxlength="50" placeholder="Enter pattern" value="<%=task_seed.pattern%>" required>
        <small class="text-muted">Pattern is used to schedule the seed to spawn.</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="spawner">Spawner</label>
        <input type="text" class="form-control" id="spawner" maxlength="50" placeholder="Enter spawner" value="<%=task_seed.spawner%>" required>
        <small class="text-muted">Spawner is used to generate tasks.</small>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteTaskSeed()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteTaskSeed() {
                bootbox.confirm("Are you sure to delete this task seed?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/task_seed/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/task_seed/update"), { id: $('#id').val(), name: $('#name').val(), pattern: $('#pattern').val(), spawner: $('#spawner').val()}, function(data) {
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
