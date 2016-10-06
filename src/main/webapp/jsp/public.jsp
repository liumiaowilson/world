<%@ page import="org.wilson.world.console.*" %>
<%
String page_title = "Public";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Public Pages</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('../public/public.jsp')" class="list-group-item">Public Pages</a>
        </div>
    </div>
</div>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="key">Key</label>
        <%
        String key = DataManager.getInstance().getValue("public.key");
        if(key == null) {
            key = "";
        }
        %>
        <input type="text" class="form-control" id="key" maxlength="20" placeholder="Enter key" value="<%=key%>" required>
        <small class="text-muted">The key is used to control access from outside.</small>
    </fieldset>
    <div class="form-group">
        <label for="toolbarPolicy">Toolbar Policy</label>
        <select class="combobox form-control" id="toolbarPolicy">
            <option></option>
            <%
            ToolbarPolicy tp = ConsoleManager.getInstance().getToolbarPolicy();
            for(ToolbarPolicy policy : ToolbarPolicy.values()) {
                boolean selected = (tp == policy);
                String selectedStr = (selected ? "selected" : "");
            %>
            <option value="<%=policy.name()%>" <%=selectedStr%>><%=policy.name()%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        $.post(getAPIURL("api/console/set_key"), { key: $('#key').val(), 'toolbarPolicy': $('#toolbarPolicy').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpCurrent();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
