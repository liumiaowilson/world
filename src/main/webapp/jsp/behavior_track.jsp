<%
String page_title = "Behavior Track";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Behavior Track</h3>
    </div>
    <div class="panel-body">
        <form id="form" role="form">
            <div class="form-group">
                <label for="defId">Behavior</label>
                <select class="combobox form-control" id="defId">
                    <option></option>
                    <%
                    List<BehaviorDef> behavior_defs = BehaviorDefManager.getInstance().getBehaviorDefs();
                    Collections.sort(behavior_defs, new Comparator<BehaviorDef>(){
                        public int compare(BehaviorDef d1, BehaviorDef d2) {
                            return d1.name.compareTo(d2.name);
                        }
                    });
                    for(BehaviorDef behavior_def : behavior_defs) {
                    %>
                    <option value="<%=behavior_def.id%>"><%=behavior_def.name%></option>
                    <%
                    }
                    %>
                </select>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary" id="save_btn">Save</button>
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function(){
    $('.combobox').combobox();

    $('#save_btn').click(function(){
        var defId = $('#defId').val();
        if(defId) {
            $.post(getAPIURL("api/behavior/create"), { 'defId': defId }, function(data) {
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                }
                else {
                    showDanger(msg);
                }
            }, "json");
        }
    });
});
</script>
<%@ include file="footer.jsp" %>
