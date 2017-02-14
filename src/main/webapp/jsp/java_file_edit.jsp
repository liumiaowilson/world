<%@ page import="org.wilson.world.java.*" %>
<%
String page_title = "Java File Edit";
%>
<%@ include file="header.jsp" %>
<%
JavaFile java_file = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
java_file = JavaFileManager.getInstance().getJavaFile(id, false);
if(java_file == null) {
    response.sendRedirect("java_file_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=java_file.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=java_file.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=java_file.description%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="template">Template</label>
        <select class="combobox form-control" id="template">
            <option></option>
            <%
            List<JavaTemplate> templates = JavaTemplateManager.getInstance().getJavaTemplates();
            Collections.sort(templates, new Comparator<JavaTemplate>(){
                public int compare(JavaTemplate t1, JavaTemplate t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
            for(JavaTemplate template : templates) {
            %>
            <option value="<%=template.name%>"><%=template.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="source">Source</label>
        <div class="form-control" id="source" required><%=FormatUtils.escapeHtml(java_file.source)%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="script">Script</label>
        <div class="form-control" id="script"><%=FormatUtils.escapeHtml(java_file.script)%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="runJavaFile()">Run</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteJavaFile()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var source = ace.edit("source");
            source.setTheme("ace/theme/monokai");
            source.getSession().setMode("ace/mode/java");
            $("#source").css("width", "100%").css("height", "400");

            var script = ace.edit("script");
            script.setTheme("ace/theme/monokai");
            script.getSession().setMode("ace/mode/javascript");
            $("#script").css("width", "100%").css("height", "400");

            $('#template').change(function(){
                var template = $('#template').val();
                if(template) {
                    $.post(getAPIURL("api/java_template/get_template"), { 'name': $('#name').val(), 'templateName': template }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var javaCode = data.result.data.javaCode;
                            source.setValue(javaCode, -1);
                            var scriptCode = data.result.data.scriptCode;
                            script.setValue(scriptCode, -1);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                }
            });

            function runJavaFile() {
                var id = $('#id').val();
                $.get(getAPIURL("api/java_file/run?id=" + id), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        msg = msg.replace(/\n/g, "<br/>");
                        showSuccess(msg, true);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function deleteJavaFile() {
                bootbox.confirm("Are you sure to delete this java file?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/java_file/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg, true);
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
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/java_file/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'source': source.getValue(), 'script': script.getValue() }, function(data) {
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
