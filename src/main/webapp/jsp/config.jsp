<%
String page_title = "Config";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_fileinput.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Configuration</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<String> confignames = ConfigManager.getInstance().getConfigNames();
                Collections.sort(confignames);
                for(String name : confignames) {
                %>
                <tr>
                    <td><%=name%></td>
                    <td><%=String.valueOf(ConfigManager.getInstance().getConfig(name))%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Configuration Override</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            List<String> lines = ConfigManager.getInstance().getOverrideConfigContent();
            if(lines != null) {
                for(String line : lines) {
            %>
            <%=line%><br/>
            <%
                }
            }
            %>
        </div>
        <form role="form" id="uploadForm" method="POST" action="<%=basePath%>/api/console/upload_config" enctype="multipart/form-data">
            <label class="control-label">Select File</label>
            <input id="file" name="file" type="file" class="file">
        </form>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Configuration Edit</h3>
    </div>
    <div class="panel-body">
        <fieldset class="form-group">
            <label for="content">Content</label>
            <%
            StringBuffer sb = new StringBuffer();
            for(String line : lines) {
                sb.append(line + "\n");
            }
            %>
            <textarea class="form-control" id="content" rows="5" placeholder="Enter the config to be overridden."><%=sb.toString()%></textarea>
        </fieldset>
        <button type="button" class="btn btn-primary" id="save_btn">Save</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_fileinput.jsp" %>
<script>
            $('#save_btn').click(function(){
                $.post(getAPIURL("api/console/save_config"), { 'content': $('#content').val() }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpCurrent();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
