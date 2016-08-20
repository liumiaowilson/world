<%@ page import="org.wilson.world.checklist.*" %>
<%
String page_title = "Checklist Track";
%>
<%@ include file="header.jsp" %>
<%
Checklist checklist = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
checklist = ChecklistManager.getInstance().getChecklist(id);
if(checklist == null) {
    response.sendRedirect("checklist_list.jsp");
    return;
}
ChecklistDef checklist_def = ChecklistDefManager.getInstance().getChecklistDef(checklist.defId);
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=checklist.name%></h3>
    </div>
    <div class="panel-body">
        <%
        for(ChecklistDefItem item : checklist_def.items) {
            String checkedStr = checklist.checked.contains(item.id) ? "checked" : "";
        %>
        <div class="checkbox">
            <label><input type="checkbox" value="<%=item.id%>" <%=checkedStr%>><%=item.name%></label>
        </div>
        <%
        }
        %>
    </div>
</div>
<button type="submit" class="btn btn-primary" id="save_btn">Save</button>
<%@ include file="import_script.jsp" %>
<script>
            $('#save_btn').click(function(){
                var ids = [];
                $('input[type=checkbox]').each(function () {
                    if (this.checked) {
                        ids.push(this.value);
                    }
                });

                $.post(getAPIURL("api/checklist/update"), { id: <%=id%>, name: "<%=checklist.name%>", defId: <%=checklist.defId%>, 'progress': ids.join(",") }, function(data) {
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpBack();
                    }
                    else {
                        showDanger(msg);
                    }
                }, "json");
            });
</script>
<%@ include file="footer.jsp" %>
