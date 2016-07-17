<%
String from_url = "task_attr_rule_edit.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
TaskAttrRule task_attr_rule = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    task_attr_rule = new TaskAttrRule();
}
task_attr_rule = TaskAttrRuleManager.getInstance().getTaskAttrRule(id);
if(task_attr_rule == null) {
    task_attr_rule = new TaskAttrRule();
}
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=task_attr_rule.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <select class="combobox form-control" id="name" required disabled>
            <option></option>
            <option value="<%=task_attr_rule.name%>" selected><%=task_attr_rule.name%></option>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="priority">Priority</label>
        <input type="number" class="form-control" id="priority" maxlength="20" placeholder="Enter priority" value="<%=task_attr_rule.priority%>" required autofocus>
    </fieldset>
    <fieldset class="form-group">
        <label for="policy">Policy</label>
        <select class="combobox form-control" id="policy" required>
            <option></option>
            <%
            String normalSelected = ("normal".equals(task_attr_rule.policy) ? "selected" : "");
            String reversedSelected = ("reversed".equals(task_attr_rule.policy) ? "selected" : "");
            %>
            <option value="normal" <%=normalSelected%>>normal</option>
            <option value="reversed" <%=reversedSelected%>>reversed</option>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="impl">Implementation</label>
        <input type="text" class="form-control" id="impl" maxlength="50" placeholder="Enter implementation" value="<%=task_attr_rule.impl%>">
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteTaskAttrRule()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteTaskAttrRule() {
                bootbox.confirm("Are you sure to delete this task attr rule?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get("api/task_attr_rule/delete?id=" + id, function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                window.location.href = "task_attr_rule_list.jsp";
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
                        var policy = $('#policy').val();
                        if(!policy) {
                            policy = 'normal';
                        }

                        l.ladda('start');
                        $.post("api/task_attr_rule/update", { id: $('#id').val(), name: $('#name').val(), policy: $('#policy').val(), priority: $('#priority').val(), impl: $('#impl').val()}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                window.location.href = "task_attr_rule_list.jsp";
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#view_all_btn').click(function(){
                    window.location.href = "task_attr_rule_list.jsp";
                });
            });
</script>
<%@ include file="footer.jsp" %>
