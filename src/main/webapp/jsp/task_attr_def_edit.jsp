<%
String page_title = "Task Attr Def Edit";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
TaskAttrDef task_attr_def = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    task_attr_def = new TaskAttrDef();
}
task_attr_def = TaskAttrDefManager.getInstance().getTaskAttrDef(id);
if(task_attr_def == null) {
    task_attr_def = new TaskAttrDef();
}
String disabledStr = "";
if(task_attr_def.isSystem) {
    disabledStr = "disabled";
}
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=task_attr_def.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=task_attr_def.name%>" required autofocus <%=disabledStr%>>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Type</label>
        <select class="combobox form-control" id="type" <%=disabledStr%>>
            <option></option>
            <%
            List<String> types = TaskAttrDefManager.getInstance().getSupportedTypes();
            Collections.sort(types);
            for(String type : types) {
                boolean selected = (type.equals(task_attr_def.type));
                String selectedStr = (selected ? "selected" : "");
            %>
            <option value="<%=type%>" <%=selectedStr%>><%=type%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholde="Enter detailed description" required <%=disabledStr%>><%=task_attr_def.description%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn" <%=disabledStr%>><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li class="<%=disabledStr%>"><a href="javascript:void(0)" onclick="deleteTaskAttrDef()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteTaskAttrDef() {
                bootbox.confirm("Are you sure to delete this task attr def?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/task_attr_def/delete?id=" + id), function(data){
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
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var type = $('#type').val();
                        if(!type) {
                            type = "String";
                        }

                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/task_attr_def/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), type: $('#type').val()}, function(data) {
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
