<%
String from_url = "task_edit.jsp";
%>
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
boolean marked = MarkManager.getInstance().isMarked("task", String.valueOf(task.id));
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=task.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=task.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholde="Enter detailed description" required><%=task.content%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="attr_table">Attributes</label>
        <table id="attr_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th style="display:none">ID</th>
                    <th>Name</th>
                    <th>Value</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(int i = 0; i < task.attrs.size(); i++) {
                    TaskAttr attr = task.attrs.get(i);
                    String attr_value = (String)TaskAttrManager.getInstance().getRealValue(attr);
                %>
                <tr>
                    <td id="id" style="display:none"><%=attr.id%></td>
                    <td id="name"><%=attr.name%></td>
                    <td id="value"><%=attr_value%></td>
                    <td><button type="button" class="btn btn-warning btn-xs" onclick="javascript:deleteRow(<%=i%>)"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td>
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
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="finishTask()">Finish</a></li>
                <li role="separator" class="divider"></li>
                <%
                if(marked) {
                %>
                <li><a href="javascript:void(0)" onclick="unmarkTask()">Unmark</a></li>
                <%
                }
                else {
                %>
                <li><a href="javascript:void(0)" onclick="markTask()">Mark</a></li>
                <%
                }
                %>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="splitTask()">Split</a></li>
                <%
                boolean hasMarked = MarkManager.getInstance().hasMarked("task");
                String disabled = (hasMarked ? "" : "disabled");
                %>
                <li class="<%=disabled%>"><a href="javascript:void(0)" onclick="mergeTask()">Merge</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteTask()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            var attr_defs = {
            <%
            List<String> taskAttrDefNames = TaskAttrDefManager.getInstance().getTaskAttrNames();
            Collections.sort(taskAttrDefNames);
            for(String taskAttrDefName : taskAttrDefNames) {
                String type = TaskAttrDefManager.getInstance().getTaskAttrType(taskAttrDefName);
            %>
                '<%=taskAttrDefName%>': '<%=type%>',
            <%
            }
            %>
            };
            var tasks = {
            <%
            List<Task> tasks = TaskManager.getInstance().getTasks();
            Collections.sort(tasks, new Comparator<Task>(){
                public int compare(Task t1, Task t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
            for(Task t : tasks) {
            %>
                '<%=t.id%>': '<%=t.name%>',
            <%
            }
            %>
            };
            var attr_name_source = [];
            for(var i in attr_defs) {
                attr_name_source.push({value: i, text: i});
            }
            var task_source = [];
            for(var i in tasks) {
                task_source.push({id: i, text: tasks[i]});
            }

            function setEditor(obj, newValue) {
                var newType = attr_defs[newValue];
                if("DateTime" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D HH:mm',
                        format: 'YYYY-MM-DD HH:mm',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            smartDays: true,
                            minuteStep: 1
                        }
                    });
                }
                else if("Date" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D',
                        format: 'YYYY-MM-DD',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            smartDays: true
                        }
                    });
                }
                else if("Boolean" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select',
                        value: obj.text(),
                        source: [
                            { value: 'true', text: 'true' },
                            { value: 'false', text: 'false' },
                        ]
                    });
                }
                else if("Integer" == newType || "Long" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'number',
                    });
                }
                else if("Task" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        value: obj.val(),
                        placeholder: 'Choose Task',
                        source: task_source
                    });
                }
                else {
                    obj.editable();
                }
            }

            function configTable() {
                $('#attr_table td[id="name"]').editable({
                    type: 'select',
                    source: attr_name_source,
                    success: function(response, newValue) {
                        var obj = $(this).parent().parent().find('td#value');
                        setEditor(obj, newValue);
                    }
                });
                $('#attr_table tbody tr').each(function(){
                    var name = $(this).find('#name').text();
                    setEditor($(this).find('#value'), name);
                });
            }

            function splitTask() {
                var id = $('#id').val();
                window.location.href = "task_split.jsp?id=" + id;
            }
            function mergeTask() {
                var id = $('#id').val();
                window.location.href = "task_merge.jsp?id=" + id;
            }
            function markTask() {
                var id = $('#id').val();
                $.get("api/item/mark?type=task&id=" + id, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        window.location.href = "task_list.jsp";
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function unmarkTask() {
                var id = $('#id').val();
                $.get("api/item/unmark?type=task&id=" + id, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        window.location.href = "task_list.jsp";
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function deleteTask() {
                bootbox.confirm("Are you sure to delete this task?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get("api/task/delete?id=" + id, function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                window.location.href = "task_list.jsp";
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            function finishTask() {
                var id = $('#id').val();
                $.get("api/task/finish?id=" + id, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        window.location.href = "task_list.jsp";
                    }
                    else {
                        showDanger(msg);
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
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        var attrs = [];
                        var validation = "";
                        $('#attr_table tbody tr').each(function(){
                            $this = $(this);
                            var id = $this.find("#id").text();
                            var name = $this.find("#name").text();
                            var value = $this.find("#value").text();
                            if(!name) {
                                validation = "Attribute name should be provided.";
                                return;
                            }
                            if(!value) {
                                validation = "Attribute value should be provided.";
                                return;
                            }
                            attrs.push({'name': name, 'value': value, 'id': id});
                        });
                        if(validation) {
                            showDanger(validation);
                            return;
                        }

                        l.ladda('start');
                        $.post("api/task/update", { id: $('#id').val(), name: $('#name').val(), content: $('#content').val(), attrs: JSON.stringify(attrs)}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                window.location.href = "task_list.jsp";
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#view_all_btn').click(function(){
                    window.location.href = "task_list.jsp";
                });

                $('#add_btn').click(function(){
                    var count = $('#attr_table tbody tr').length;
                    $('#attr_table').append('<tr><td id="id" style="display:none">0</td><td id="name" data-type="select">attr_name</td><td id="value">0</td><td><button type="button" class="btn btn-warning btn-xs" onclick="javascript:deleteRow(' + count + ')"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td></tr>');
                    configTable();
                });

                $('#delete_btn').click(function(){
                    $('#attr_table tbody tr:last').remove();
                });
            });

        function deleteRow(num) {
            $('#attr_table tbody tr:eq(' + num + ')').remove();
        }
</script>
<%@ include file="footer.jsp" %>
