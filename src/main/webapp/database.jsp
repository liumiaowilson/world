<%@ page import="java.util.*" %>
<%@ page import="org.wilson.world.manager.*" %>
<%
String from_url = "database.jsp";
%>
<%@ include file="header.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Clear Table</h3>
    </div>
    <div class="panel-body">
        <%
        List<String> names = ItemManager.getInstance().getItemTableNames();
        Map<String, ItemTableInfo> infos = ItemManager.getInstance().getItemTableInfos();
        for(String name : names) {
            ItemTableInfo info = infos.get(name);
            if(info == null) {
                info = new ItemTableInfo();
            }
        %>
        <div class="checkbox">
            <label><input type="checkbox" value="<%=name%>"><%=name%> (Row Count: <%=info.rowCount%>)</label>
        </div>
        <%
        }
        %>
        <button type="button" class="btn btn-danger ladda-button" id="clear_btn"><span class="ladda-label">Clear</span></button>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Tools</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            if(ConfigManager.getInstance().isOpenShiftApp()) {
            %>
            <a href="phpmyadmin" class="list-group-item">MySQL Admin</a>
            <%
            }
            %>
        </div>
    </div>
</div>
<%@ include file="import_scripts.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#alert_warning').text("This operation may cause damage to your data. Please execute with caution!");
                $('#alert_warning').show();

                $('#clear_btn').click(function(){
                    var names = [];
                    $('input[type=checkbox]').each(function () {
                        if (this.checked) {
                            names.push(this.value);
                        }
                    });
                    if(names.length > 0) {
                        bootbox.confirm("Are you sure to clear the table(s)?", function(result){
                            if(result) {
                                l.ladda('start');
                                $.get("api/item/clear_tables?names=" + names, function(data){
                                    var status = data.result.status;
                                    var msg = data.result.message;
                                    if("OK" == status) {
                                        $('#alert_success').text(msg);
                                        $('#alert_success').show();
                                        l.ladda('stop');
                                    }
                                    else {
                                        $('#alert_danger').text(msg);
                                        $('#alert_danger').show();
                                        l.ladda('stop');
                                    }
                                });
                            }
                        });
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
