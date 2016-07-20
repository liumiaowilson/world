<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String name = request.getParameter("name");
Action boundAction = ExtManager.getInstance().getBoundAction(name);
if(boundAction == null) {
    boundAction = new Action();
}
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Bind Action to Extension Point</h3>
    </div>
    <div class="panel-body">
        <form id="form" role="form">
            <div class="form-group">
                <label for="action_combobox">Extension Point</label>
                <select class="combobox form-control" id="action_combobox">
                    <option></option>
                    <%
                    List<Action> actions = ActionManager.getInstance().getActions();
                    List<String> actionNames = new ArrayList<String>();
                    for(Action action : actions) {
                        actionNames.add(action.name);
                    }
                    Collections.sort(actionNames);
                    for(String actionName : actionNames) {
                        boolean selected = (actionName.equals(boundAction.name));
                        String selectedStr = (selected ? "selected" : "");
                    %>
                    <option value="<%=actionName%>" <%=selectedStr%>><%=actionName%></option>
                    <%
                    }
                    %>
                </select>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
                <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function(){
    $('.combobox').combobox();
    var l = $('#save_btn').ladda();

    $('#save_btn').click(function(){
        l.ladda('start');
        var action_name = $('#action_combobox').val();
        if(action_name) {
            $.post("api/extension_point/bind", { 'ext_name': "<%=name%>", 'action_name': action_name }, function(data) {
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    l.ladda('stop');
                    window.location.href = "extension_point_list.jsp";
                }
                else {
                    showDanger(msg);
                    l.ladda('stop');
                }
            }, "json");
        }
        else {
            $.post("api/extension_point/unbind", { 'ext_name': "<%=name%>" }, function(data) {
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    l.ladda('stop');
                    window.location.href = "extension_point_list.jsp";
                }
                else {
                    showDanger(msg);
                    l.ladda('stop');
                }
            }, "json");
        }
    });

    $('#view_all_btn').click(function(){
        window.location.href = "extension_point_list.jsp";
    });
});
</script>
<%@ include file="footer.jsp" %>
