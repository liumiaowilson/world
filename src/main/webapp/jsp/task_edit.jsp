<%@ page import="org.wilson.world.task.*" %>
<%
String page_title = "Task Edit";
%>
<%
Task task = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
task = TaskManager.getInstance().getTask(id);
if(task == null) {
    if(TaskIterator.getInstance().isEnabled()) {
        String direction = request.getParameter("dir");
        if("prev".equals(direction)) {
            task = TaskIterator.getInstance().previous();
        }
        else if("next".equals(direction)) {
            task = TaskIterator.getInstance().next();
        }
        else {
            response.sendRedirect("task_list.jsp");
            return;
        }
    }
    else {
        response.sendRedirect("task_list.jsp");
        return;
    }
}
if(TaskIterator.getInstance().isEnabled()) {
    TaskIterator.getInstance().setTaskId(id);
}
boolean marked = MarkManager.getInstance().isMarked("task", String.valueOf(task.id));
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="import_css_tag.jsp" %>
<%@ include file="navbar.jsp" %>
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
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=task.content%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="tags">Tags</label>
        <input type="text" class="form-control" data-role="tagsinput" id="tags" maxlength="200" placeholder="Enter tags" value="<%=task.tag == null ? "" : task.tag.tags%>">
    </fieldset>
    <fieldset class="form-group">
        <label for="template">Template</label>
        <select class="combobox form-control" id="template">
            <option></option>
            <%
            List<TaskTemplateInfo> template_infos = TaskTemplateManager.getInstance().getTaskTemplateInfos();
            Collections.sort(template_infos, new Comparator<TaskTemplateInfo>(){
                public int compare(TaskTemplateInfo i1, TaskTemplateInfo i2) {
                    return i1.name.compareTo(i2.name);
                }
            });
            for(TaskTemplateInfo template_info : template_infos) {
            %>
            <option value="<%=template_info.name%>"><%=template_info.name%></option>
            <%
            }
            %>
        </select>
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
                Collections.sort(task.attrs, new Comparator<TaskAttr>(){
                    public int compare(TaskAttr a1, TaskAttr a2) {
                        return a1.name.compareTo(a2.name);
                    }
                });
                for(int i = 0; i < task.attrs.size(); i++) {
                    TaskAttr attr = task.attrs.get(i);
                    String attr_value = (String)TaskAttrManager.getInstance().getRealValue(attr);
                %>
                <tr>
                    <td id="id" style="display:none"><%=attr.id%></td>
                    <td id="name"><%=attr.name%></td>
                    <td id="value"><%=attr_value%></td>
                    <td><button type="button" class="btn btn-warning btn-xs del_attr_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td>
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
        <button type="button" class="btn btn-default" id="left_btn">
            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        </button>
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="finishTask(false)">Finish</a></li>
                <li><a href="javascript:void(0)" onclick="finishTask(true)">Done & Run</a></li>
                <li><a href="javascript:void(0)" onclick="abandonTask()">Abandon</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="viewRelatedTask()">View Related</a></li>
                <li><a href="javascript:void(0)" onclick="viewDepsTask()">View Deps</a></li>
                <li><a href="javascript:void(0)" onclick="viewChildTask()">View Child</a></li>
                <li><a href="<%=TaskManager.getInstance().getRelatedLink(task)%>">View Link</a></li>
                <%
                Document refer_doc = TaskManager.getInstance().getReferredDocument(task);
                boolean hasDoc = refer_doc != null;
                int refer_doc_id = hasDoc ? refer_doc.id : 0;
                %>
                <li <%=hasDoc ? "" : "class='disabled'"%>><a href="javascript:void(0)" onclick="<%=hasDoc ? "viewDocument(" + refer_doc_id + ")" : ""%>">View Document</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="genDependentTask()">Gen Dependent</a></li>
                <%
                boolean isChildTask = TaskManager.getInstance().isChildTask(task);
                %>
                <li <%=isChildTask ? "class='disabled'" : ""%>><a href="javascript:void(0)" onclick="<%=isChildTask ? "" : "genChildTask()"%>">Gen Child</a></li>
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
                <li class="<%=disabled%>"><a href="javascript:void(0)" onclick="<%=!disabled.equals("") ? "" : "mergeTask()"%>">Merge</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteTask()">Delete</a></li>
            </ul>
        </div>
        <button type="button" class="btn btn-default" id="right_btn">
            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        </button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<%@ include file="import_script_typeahead.jsp" %>
<%@ include file="import_script_tag.jsp" %>
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
            var contexts = {
            <%
            List<Context> contexts = ContextManager.getInstance().getContexts();
            Collections.sort(contexts, new Comparator<Context>(){
                public int compare(Context t1, Context t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
            for(Context t : contexts) {
            %>
                '<%=t.id%>': '<%=t.name%>',
            <%
            }
            %>
            };
            var plans = {
            <%
            List<Plan> plans = PlanManager.getInstance().getPlans();
            Collections.sort(plans, new Comparator<Plan>(){
                public int compare(Plan d1, Plan d2) {
                    return d1.name.compareTo(d2.name);
                }
            });
            for(Plan plan : plans) {
            %>
                '<%=plan.id%>': '<%=plan.name%>',
            <%
            }
            %>
            };
            var documents = {
            <%
            List<Document> docs = DocumentManager.getInstance().getDocuments();
            Collections.sort(docs, new Comparator<Document>(){
                public int compare(Document d1, Document d2) {
                    return d1.name.compareTo(d2.name);
                }
            });
            for(Document doc : docs) {
            %>
                '<%=doc.id%>': '<%=doc.name%>',
            <%
            }
            %>
            };
            var types = {
            <%
            List<String> types = TaskManager.getInstance().getTaskTypes();
            for(String type : types) {
            %>
                '<%=type%>': '<%=type%>',
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
            var context_source = [];
            for(var i in contexts) {
                context_source.push({id: i, text: contexts[i]});
            }
            var document_source = [];
            for(var i in documents) {
                document_source.push({id: i, text: documents[i]});
            }
            var plan_source = [];
            for(var i in plans) {
                plan_source.push({id: i, text: plans[i]});
            }
            var type_source = [];
            for(var i in types) {
                type_source.push({id: i, text: types[i]});
            }

            var templates = [
            <%
            for(TaskTemplateInfo template_info : template_infos) {
                Collections.sort(template_info.attrs, new Comparator<TaskAttr>(){
                    public int compare(TaskAttr a1, TaskAttr a2) {
                        return a1.name.compareTo(a2.name);
                    }
                });
            %>
            {
                name: '<%=template_info.name%>',
                attrs: {
                    <%
                    for(TaskAttr attr : template_info.attrs) {
                    %>
                    <%=attr.name%>: '<%=TaskAttrManager.getInstance().getRealValue(attr)%>',
                    <%
                    }
                    %>
                }
            },
            <%
            }
            %>
            ];

            var tags = [
                <%
                List<String> tagnames = TaskTagManager.getInstance().getTagNames();
                for(String tagname : tagnames) {
                %>
                "<%=tagname%>",
                <%
                }
                %>
                ];
            var tagnames = new Bloodhound({
                datumTokenizer: Bloodhound.tokenizers.whitespace,
                queryTokenizer: Bloodhound.tokenizers.whitespace,
                local: tags
            });
            tagnames.initialize();

            $('#tags').tagsinput({
                typeaheadjs: {
                    name: 'tagnames',
                    source: tagnames
                }
            });

            $('.bootstrap-tagsinput').css("width", "100%");

            function setEditor(obj, newValue) {
                var newType = attr_defs[newValue];
                if("DateTime" == newType) {
                    if(obj.text() == "0") {
                        var d = new Date();
                        var dateStr = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate() + " " + d.getHours() + ":" + d.getMinutes();
                        obj.text(dateStr);
                    }
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D HH:mm',
                        format: 'YYYY-MM-DD HH:mm',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            minYear: <%=ConfigManager.getInstance().getConfigAsInt("combodate.year.min", 1950)%>,
                            smartDays: true,
                            minuteStep: 1
                        }
                    });
                }
                else if("Date" == newType) {
                    if(obj.text() == "0") {
                        var d = new Date();
                        var dateStr = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
                        obj.text(dateStr);
                    }
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D',
                        format: 'YYYY-MM-DD',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            minYear: <%=ConfigManager.getInstance().getConfigAsInt("combodate.year.min", 1950)%>,
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
                else if("Context" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        value: obj.val(),
                        placeholder: 'Choose Context',
                        source: context_source
                    });
                }
                else if("Document" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        placeholder: 'Choose Document',
                        source: document_source
                    });
                }
                else if("Plan" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        placeholder: 'Choose Plan',
                        source: plan_source
                    });
                }
                else if("Type" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        placeholder: 'Choose Type',
                        source: type_source
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
                        var obj = $(this).closest('tr').find('td#value');
                        setEditor(obj, newValue);
                    }
                });
                $('#attr_table tbody tr').each(function(){
                    var name = $(this).find('#name').text();
                    setEditor($(this).find('#value'), name);
                });

                $('.del_attr_btn').click(function(){
                    $(this).closest("tr").remove();
                });
            }

            function replaceRows(attrs) {
                $('#attr_table tbody tr').remove();
                addRows(attrs);
            }

            function addRows(attrs) {
                for(var i in attrs) {
                    $('#attr_table').append('<tr><td id="id" style="display:none">0</td><td id="name" data-type="select">' + i + '</td><td id="value">' + attrs[i] + '</td><td><button type="button" class="btn btn-warning btn-xs del_attr_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td></tr>');
                }
                configTable();
            }

            function splitTask() {
                var id = $('#id').val();
                jumpTo("task_split.jsp?id=" + id);
            }
            function mergeTask() {
                var id = $('#id').val();
                jumpTo("task_merge.jsp?id=" + id);
            }
            function markTask() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/mark?type=task&id=" + id), function(data){
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
            function unmarkTask() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/unmark?type=task&id=" + id), function(data){
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
            function genDependentTask() {
                bootbox.prompt("Please enter the name of the dependent task.", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.post(getAPIURL("api/task/gen_dependent"), { 'id': id, 'name': result }, function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpTo("task_edit.jsp?id=" + data.result.data.id);
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            function genChildTask() {
                bootbox.prompt("Please enter the name of the child task.", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.post(getAPIURL("api/task/gen_child"), { 'id': id, 'name': result }, function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpTo("task_edit.jsp?id=" + data.result.data.id);
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            function deleteTask() {
                bootbox.confirm("Are you sure to delete this task?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/task/delete?id=" + id), function(data){
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
            function viewRelatedTask() {
                var id = $('#id').val();
                jumpTo("task_related.jsp?id=" + id);
            }
            function viewDepsTask() {
                var id = $('#id').val();
                jumpTo("task_dep.jsp?id=" + id);
            }
            function viewChildTask() {
                var id = $('#id').val();
                jumpTo("task_child.jsp?id=" + id);
            }
            function viewDocument(id) {
                jumpTo("document_edit.jsp?id=" + id);
            }
            function finishTask(run) {
                bootbox.confirm("Are you sure to finish this task?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/task/finish?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if(run) {
                                    jumpTo("task_new.jsp");
                                }
                                else {
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            function abandonTask() {
                bootbox.confirm("Are you sure to abandon this task?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/task/abandon?id=" + id), function(data){
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
                $.fn.editable.defaults.mode = 'inline';
                configTable();

                $('#template').change(function(){
                    var template_name = $('#template option:selected').val();
                    if(template_name) {
                        for(var i in templates) {
                            if(templates[i].name == template_name) {
                                replaceRows(templates[i].attrs);
                            }
                        }
                    }
                });

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
                        $.post(getAPIURL("api/task/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val(), attrs: JSON.stringify(attrs), 'tags': $('#tags').val()}, function(data) {
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
                    addRows({ 'attr name': '0' })
                });

                $('#delete_btn').click(function(){
                    $('#attr_table tbody tr:last').remove();
                });

                $('#left_btn').click(function(){
                    $.get(getAPIURL("api/task/prev?id=<%=task.id%>"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var prev = parseInt(data.result.data.$);
                            if(prev < 0) {
                                showDanger("Previous task does not exist.");
                            }
                            else {
                                jumpTo("task_edit.jsp?id=" + prev);
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#right_btn').click(function(){
                    $.get(getAPIURL("api/task/next?id=<%=task.id%>"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var next = parseInt(data.result.data.$);
                            if(next < 0) {
                                showDanger("Next task does not exist.");
                            }
                            else {
                                jumpTo("task_edit.jsp?id=" + next);
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
