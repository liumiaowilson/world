<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
TaskTemplateInfo task_template_info = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    task_template_info = new TaskTemplateInfo();
}
task_template_info = TaskTemplateManager.getInstance().getTaskTemplateInfo(id);
if(task_template_info == null) {
    task_template_info = new TaskTemplateInfo();
}
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=task_template_info.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=task_template_info.name%>" required disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="attrs">Attributes</label>
        <table id="attrs" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(TaskAttr attr : task_template_info.attrs) {
                    String attr_value = (String)TaskAttrManager.getInstance().getRealValue(attr);
                %>
                <tr>
                    <td><%=attr.name%></td>
                    <td><%=attr_value%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn" disabled><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
