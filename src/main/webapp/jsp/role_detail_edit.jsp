<%
String page_title = "Role Detail Edit";
%>
<%@ include file="header.jsp" %>
<%
RoleDetail role_detail = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
role_detail = RoleDetailManager.getInstance().getRoleDetail(id);
if(role_detail == null) {
    response.sendRedirect("role_detail_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=role_detail.id%>" disabled>
    </fieldset>
    <div class="form-group">
        <label for="roleId">Role</label>
        <select class="combobox form-control" id="roleId" required>
            <option></option>
            <%
            List<Role> roles = RoleManager.getInstance().getRoles();
            Collections.sort(roles, new Comparator<Role>(){
                public int compare(Role r1, Role r2) {
                    return r1.name.compareTo(r2.name);
                }
            });
            for(Role role : roles) {
                String selectedStr = role_detail.roleId == role.id ? "selected" : "";
            %>
            <option value="<%=role.id%>" <%=selectedStr%>><%=role.name%></option>
            <%
            }
            %>
        </select>
    </div>
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
                String selectedStr = role_detail.roleAttrId == roleAttr.id ? "selected" : "";
            %>
            <option value="<%=roleAttr.id%>" <%=selectedStr%>><%=roleAttr.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="10" placeholder="Enter detailed description" required><%=role_detail.content%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteRoleDetail()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteRoleDetail() {
                bootbox.confirm("Are you sure to delete this role detail?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/role_detail/delete?id=" + id), function(data){
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

                $('#roleId').change(function(){
                    $.get(getAPIURL("api/role/get_attrs?id=" + $('#roleId').val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            $('#roleAttrId').empty();
                            $('#roleAttrId').append("<option></option>");
                            for(var i in data.result.list) {
                                var info = data.result.list[i];
                                $('#roleAttrId').append("<option value='" + info.id + "'>" + info.name + "</option>");
                            }
                            $('#roleAttrId').combobox('refresh');
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post(getAPIURL("api/role_detail/update"), { id: $('#id').val(), 'roleId': $('#roleId').val(), 'roleAttrId': $('#roleAttrId').val(), content: $('#content').val()}, function(data) {
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
