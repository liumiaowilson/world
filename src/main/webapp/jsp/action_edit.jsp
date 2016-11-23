<%
String page_title = "Action Edit";
%>
<%
Action action = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
action = ActionManager.getInstance().getAction(id);
if(action == null) {
    response.sendRedirect("action_list.jsp");
    return;
}
boolean marked = MarkManager.getInstance().isMarked("action", String.valueOf(action.id));
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
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
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(int i = 0; i < action.params.size(); i++) {
                    ActionParam param = action.params.get(i);
                %>
                <tr>
                    <td id="id" style="display:none"><%=param.id%></td>
                    <td id="name"><%=param.name%></td>
                    <td id="defaultValue"><%=param.defaultValue%></td>
                    <td><button type="button" class="btn btn-warning btn-xs del_param_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td>
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
        <div class="form-control" id="script"><%=action.script%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
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
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var script = ace.edit("script");
            script.setTheme("ace/theme/monokai");
            script.getSession().setMode("ace/mode/javascript");
            $("#script").css("width", "100%").css("height", "400");

            function configTable() {
                $('#params_table td[id="name"]').editable();
                $('#params_table td[id="defaultValue"]').editable();

                $('.del_param_btn').click(function(){
                    $(this).closest("tr").remove();
                });
            }

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
                    if(name.length > 20) {
                        validation = "Param name length should be less than 20.";
                        return;
                    }
                    if(!defaultValue) {
                        validation = "Param default value should be provided.";
                        return;
                    }
                    if(defaultValue.length > 200) {
                        validation = "Param default value length should be less than 200.";
                        return;
                    }
                    params.push({'name': name, 'defaultValue': defaultValue, 'id': id});
                });
                if(validation) {
                    showDanger(validation);
                    return;
                }
                $.post(getAPIURL("api/action/dry_run"), {'name': name, 'params': JSON.stringify(params) }, function(data){
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
                $.get(getAPIURL("api/item/mark?type=action&id=" + id), function(data){
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
            function unmarkAction() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/unmark?type=action&id=" + id), function(data){
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
            function deleteAction() {
                bootbox.confirm("Are you sure to delete this action?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/action/delete?id=" + id), function(data){
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
                $.fn.editable.defaults.mode = 'inline';
                configTable();

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
                        $.post(getAPIURL("api/action/update"), { id: $('#id').val(), name: $('#name').val(), script: script.getValue(), params: JSON.stringify(params)}, function(data) {
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

                $('#add_btn').click(function(){
                    $('#params_table').append('<tr><td id="id" style="display:none">0</td><td id="name">param_name</td><td id="defaultValue">0</td><td><button type="button" class="btn btn-warning btn-xs del_param_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td></tr>');
                    configTable();
                });

                $('#delete_btn').click(function(){
                    $('#params_table tbody tr:last').remove();
                });
            });
</script>
<%@ include file="footer.jsp" %>
