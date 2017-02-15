<%
String page_title = "JsFile Edit";
%>
<%@ include file="header.jsp" %>
<%
JsFile js_file = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
js_file = JsFileManager.getInstance().getJsFile(id, false);
if(js_file == null) {
    response.sendRedirect("js_file_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=js_file.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=js_file.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=js_file.description%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="status">Status</label>
        <select class="combobox form-control" id="status" required>
            <%
            List<String> statusList = JsFileManager.getInstance().getStatusList();
            for(String status : statusList) {
                String selectedStr = (status.equals(js_file.status) ? "selected" : "");
            %>
            <option value="<%=status%>" <%=selectedStr%>><%=status%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="source">Source</label>
        <div class="form-control" id="source" required><%=FormatUtils.escapeHtml(js_file.source)%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="test">Test</label>
        <div class="form-control" id="test"><%=FormatUtils.escapeHtml(js_file.test)%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteJsFile()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var source = ace.edit("source");
            source.setTheme("ace/theme/monokai");
            source.getSession().setMode("ace/mode/javascript");
            $("#source").css("width", "100%").css("height", "400");

            var test = ace.edit("test");
            test.setTheme("ace/theme/monokai");
            test.getSession().setMode("ace/mode/javascript");
            $("#test").css("width", "100%").css("height", "400");

            function deleteJsFile() {
                bootbox.confirm("Are you sure to delete this js file?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/js_file/delete?id=" + id), function(data){
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

                        l.ladda('start');
                        $.post(getAPIURL("api/js_file/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), status: $("#status").val(), source: source.getValue(), test: test.getValue() }, function(data) {
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
