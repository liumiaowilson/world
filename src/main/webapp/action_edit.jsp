<%
String from_url = "action_edit.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<%
Action action = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    action = new Action();
}
action = ActionManager.getInstance().getAction(id);
if(action == null) {
    action = new Action();
}
boolean marked = MarkManager.getInstance().isMarked("action", String.valueOf(action.id));
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=action.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=action.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="params_table">Parameters</label>
        <table id="params_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th style="display:none">ID</th>
                    <th>Name</th>
                    <th>Default Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(ActionParam param : action.params) {
                %>
                <tr>
                    <td id="id" style="display:none"><%=param.id%></td>
                    <td id="name"><%=param.name%></td>
                    <td id="defaultValue"><%=param.defaultValue%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <button type="button" class="btn btn-default" id="add_btn">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="delete_btn">
            <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
        </button>
    </div>
    <fieldset class="form-group">
        <label for="script">Script</label>
        <textarea class="form-control" id="script" rows="10" maxlength="400" placeholde="Enter script" required><%=action.script%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteAction()">Delete</a></li>
                <li role="separator" class="divider"></li>
                <%
                if(marked) {
                %>
                <li><a href="javascript:void(0)" onclick="unmarkAction()">Unmark</a></li>
                <%
                }
                else {
                %>
                <li><a href="javascript:void(0)" onclick="markAction()">Mark</a></li>
                <%
                }
                %>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="dryRun()">Dry Run</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            function dryRun() {
                var name = $('#name').val();
                var params = [];
                var validation = "";
                $('#params_table tbody tr').each(function(){
                    $this = $(this);
                    var id = $this.find("#id").text();
                    var name = $this.find("#name").text();
                    var defaultValue = $this.find("#defaultValue").text();
                    if(!name) {
                        validation = "Param name should be provided.";
                        return;
                    }
                    if(!defaultValue) {
                        validation = "Param default value should be provided.";
                        return;
                    }
                    params.push({'name': name, 'defaultValue': defaultValue, 'id': id});
                });
                if(validation) {
                    showDanger(validation);
                    return;
                }
                $.post("api/action/dry_run", {'name': name, 'params': JSON.stringify(params) }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function markAction() {
                var id = $('#id').val();
                $.get("api/item/mark?type=action&id=" + id, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        window.location.href = "action_list.jsp";
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function unmarkAction() {
                var id = $('#id').val();
                $.get("api/item/unmark?type=action&id=" + id, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        window.location.href = "action_list.jsp";
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function deleteAction() {
                bootbox.confirm("Are you sure to delete this action?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get("api/action/delete?id=" + id, function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                window.location.href = "action_list.jsp";
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
                $.fn.editable.defaults.mode = 'inline';
                $('#params_table td').editable();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var params = [];
                        var validation = "";
                        $('#params_table tbody tr').each(function(){
                            $this = $(this);
                            var id = $this.find("#id").text();
                            var name = $this.find("#name").text();
                            var defaultValue = $this.find("#defaultValue").text();
                            if(!name) {
                                validation = "Param name should be provided.";
                                return;
                            }
                            if(!defaultValue) {
                                validation = "Param default value should be provided.";
                                return;
                            }
                            params.push({'name': name, 'defaultValue': defaultValue, 'id': id});
                        });
                        if(validation) {
                            showDanger(validation);
                            return;
                        }
                        l.ladda('start');
                        $.post("api/action/update", { id: $('#id').val(), name: $('#name').val(), script: $('#script').val(), params: JSON.stringify(params)}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                window.location.href = "action_list.jsp";
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#view_all_btn').click(function(){
                    window.location.href = "action_list.jsp";
                });

                $('#add_btn').click(function(){
                    $('#params_table').append('<tr><td id="id" style="display:none">0</td><td id="name">param_name</td><td id="defaultValue">0</td></tr>');
                    $('#params_table tbody td').editable();
                });

                $('#delete_btn').click(function(){
                    $('#params_table tbody tr:last').remove();
                    $('#params_table tbody td').editable();
                });
            });
</script>
<%@ include file="footer.jsp" %>
