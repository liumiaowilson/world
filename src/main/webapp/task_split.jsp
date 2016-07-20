<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<%
Task task = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    task = new Task();
}
task = TaskManager.getInstance().getTask(id);
if(task == null) {
    task = new Task();
}
%>
<table class="table table-striped table-bordered" id="split_table">
    <thead>
        <tr>
            <th>Name</th>
            <th>Content</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td id="name"><%=task.name%></td>
            <td id="content"><%=FormatUtils.safeString(task.content)%></td>
        </tr>
    </tbody>
</table>
<div class="form-group">
    <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
    <button type="button" class="btn btn-default" id="add_btn">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default" id="delete_btn">
        <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
$(document).ready(function(){
    var l = $('#save_btn').ladda();
    $.fn.editable.defaults.mode = 'inline';
    configTable();

    $('#save_btn').click(function(){
        var id = <%=id%>;
        var tasks = [];
        var validation = "";
        $('#split_table tbody tr').each(function(){
            $this = $(this);
            var name = $this.find("#name").text();
            var content = $this.find("#content").text();
            if(!name) {
                validation = "New task name should be provided.";
                return;
            }
            if(name.length > 20) {
                validation = "New task name should be less than 20.";
                return;
            }
            if(content.length > 200) {
                validation = "New task content should be less than 200.";
                return;
            }
            tasks.push({'name': name, 'content': content});
        });
        if(tasks.length == 0) {
            validation = "No new tasks are provided.";
        }
        if(validation) {
            showDanger(validation);
            return;
        }
        l.ladda('start');
        $.post("api/task/split", { 'id': id, 'tasks': JSON.stringify(tasks) }, function(data){
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
    });

    $('#add_btn').click(function(){
        $('#split_table tbody tr:last').after('<tr><td id="name"><%=task.name%></td><td id="content"><%=FormatUtils.safeString(task.content)%></td></tr>');
        configTable();
    });

    $('#delete_btn').click(function(){
        $('#split_table tbody tr:last').remove();
    });
});

function configTable() {
    $('#split_table tbody td[id="name"]').editable();
}
</script>
<%@ include file="footer.jsp" %>
