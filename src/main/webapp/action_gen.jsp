<%
String from_url = "action_gen.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String name = request.getParameter("name");
Action action = ActionManager.getInstance().generateAction(name);
if(action == null) {
    action = new Action();
}
%>
<form id="form" data-toggle="validator" role="form">
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
                    <th>Name</th>
                    <th>Default Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(ActionParam param : action.params) {
                %>
                <tr>
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
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();
                $.fn.editable.defaults.mode = 'inline';
                $('#params_table td').editable();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var script = $('#script').val();

                        var params = [];
                        var validation = "";
                        $('#params_table tbody tr').each(function(){
                            $this = $(this);
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
                            params.push({'name': name, 'defaultValue': defaultValue});
                        });
                        if(validation) {
                            showDanger(validation);
                            return;
                        }
                        l.ladda('start');
                        $.post("api/action/create", { name: $('#name').val(), 'script': script, 'params': JSON.stringify(params)}, function(data) {
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

                $('#save_btn').click(function(){
                    $('#form').submit();
                });
            });

            $('#add_btn').click(function(){
                $('#params_table').append('<tr><td id="name">param_name</td><td id="defaultValue">null</td></tr>');
                $('#params_table tbody td').editable();
            });

            $('#delete_btn').click(function(){
                $('#params_table tbody tr:last').remove();
                $('#params_table tbody td').editable();
            });
</script>
<%@ include file="footer.jsp" %>
