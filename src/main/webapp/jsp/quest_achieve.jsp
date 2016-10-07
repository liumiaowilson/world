<%
String page_title = "Quest Achieve";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quest Achieve</h3>
    </div>
    <div class="panel-body">
        <form id="form" role="form">
            <div class="form-group">
                <label for="defId">Quest</label>
                <select class="combobox form-control" id="defId">
                    <option></option>
                    <%
                    List<QuestDef> quest_defs = QuestDefManager.getInstance().getQuestDefs();
                    Collections.sort(quest_defs, new Comparator<QuestDef>(){
                        public int compare(QuestDef d1, QuestDef d2) {
                            return d1.name.compareTo(d2.name);
                        }
                    });
                    for(QuestDef quest_def : quest_defs) {
                    %>
                    <option value="<%=quest_def.id%>"><%=quest_def.name%></option>
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
            $.post(getAPIURL("api/quest_def/achieve"), { 'id': defId }, function(data) {
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
