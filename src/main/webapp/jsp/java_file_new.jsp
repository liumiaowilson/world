<%@ page import="org.wilson.world.java.*" %>
<%
String page_title = "Java File New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description"></textarea>
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
        <div class="form-control" id="source" required><%=FormatUtils.escapeHtml(JavaManager.getInstance().getDefaultJavaContent())%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="script">Script</label>
        <div class="form-control" id="script"></div>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
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

            $(document).ready(function(){
                $('.combobox').combobox();

                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/java_file/create"), { name: $('#name').val(), 'description': description, 'source': source.getValue(), 'script': script.getValue() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    jumpCurrent();
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                }
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#create_new').val("false");
                    $('#form').submit();
                });

                $('#save_new_btn').click(function(){
                    $('#create_new').val("true");
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
