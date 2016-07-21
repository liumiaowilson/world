<%
String page_title = "Task Attr Rule Sort";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Attr Rule Sort</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered" id="sort_table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<TaskAttrRule> rules = TaskAttrRuleManager.getInstance().getTaskAttrRules();
                for(TaskAttrRule rule : rules) {
                %>
                <tr>
                    <td id="id"><a href="javascript:jumpTo('task_attr_rule_edit.jsp?id=<%=rule.id%>')"><%=rule.id%></a></td>
                    <td><%=rule.name%></td>
                    <td>
                        <button type="button" class="btn btn-xs" id="up_btn">
                            <span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>
                        </button>
                        <button type="button" class="btn btn-xs" id="down_btn">
                            <span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span>
                        </button>
                    </td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function(){
    var l = $('#save_btn').ladda();

    $("#up_btn, #down_btn").click(function(){
        var row = $(this).parents("tr:first");
        if ($(this).is("#up_btn")) {
            row.insertBefore(row.prev());
        } else {
            row.insertAfter(row.next());
        }
    });

    $("#save_btn").click(function(){
        var ids = [];
        $("#sort_table tbody tr").each(function(){
            ids.push($(this).find("#id").text());
        });

        l.ladda('start');
        $.post(getAPIURL("api/task_attr_rule/sort"), { 'ids': ids.join(",") }, function(data) {
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
    });
});
</script>
<%@ include file="footer.jsp" %>
