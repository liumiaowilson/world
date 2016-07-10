<%
String from_url = "action_edit.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
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
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
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

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        l.ladda('start');
                        $.post("api/action/update", { id: $('#id').val(), name: $('#name').val(), script: $('#script').val()}, function(data) {
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
            });
</script>
<%@ include file="footer.jsp" %>
