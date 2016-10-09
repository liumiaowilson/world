<%
String page_title = "Role Update";
%>
<%@ include file="header.jsp" %>
<%
Role role = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
role = RoleManager.getInstance().getRole(id);
if(role == null) {
    response.sendRedirect("role_overview.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=role.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=role.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="roleAttrId">Role Attribute</label>
        <select class="combobox form-control" id="roleAttrId" required>
            <option></option>
            <%
            List<RoleAttr> roleAttrs = RoleAttrManager.getInstance().getRoleAttrs();
            Collections.sort(roleAttrs, new Comparator<RoleAttr>(){
                public int compare(RoleAttr r1, RoleAttr r2) {
                    return r1.name.compareTo(r2.name);
                }
            });
            for(RoleAttr roleAttr : roleAttrs) {
            %>
            <option value="<%=roleAttr.id%>"><%=roleAttr.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="10" placeholder="Enter detailed description" required></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="save_btn">Save</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();

                $('#roleAttrId').change(function(){
                    $.get(getAPIURL("api/role/get_content?id=<%=role.id%>&attrId=" + $('#roleAttrId').val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            $('#content').val(data.result.data.$);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#save_btn').click(function(){
                    $.post(getAPIURL("api/role/update_detail"), { 'id': <%=role.id%>, 'attrId': $('#roleAttrId').val(), 'content': $('#content').val() }, function(data) {
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
            });
</script>
<%@ include file="footer.jsp" %>
