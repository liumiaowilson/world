<%@ page import="org.wilson.world.checklist.*" %>
<%
String page_title = "Checklist Edit";
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
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=checklist.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=checklist.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="defId">Checklist Def</label>
        <select class="combobox form-control" id="defId" disabled>
            <option></option>
            <%
            List<ChecklistDef> defs = ChecklistDefManager.getInstance().getChecklistDefs();
            Collections.sort(defs, new Comparator<ChecklistDef>(){
                public int compare(ChecklistDef d1, ChecklistDef d2) {
                    return d1.name.compareTo(d2.name);
                }
            });
            for(ChecklistDef def : defs) {
                String selectedStr = def.id == checklist.defId ? "selected" : "";
            %>
            <option value="<%=def.id%>" <%=selectedStr%>><%=def.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="progress">Progress</label>
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
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteChecklist()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteChecklist() {
                bootbox.confirm("Are you sure to delete this checklist?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/checklist/delete?id=" + id), function(data){
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

                        var ids = [];
                        $('input[type=checkbox]').each(function () {
                            if (this.checked) {
                                ids.push(this.value);
                            }
                        });

                        l.ladda('start');
                        $.post(getAPIURL("api/checklist/update"), { id: $('#id').val(), name: $('#name').val(), defId: $('#defId').val(), 'progress': ids.join(",") }, function(data) {
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
