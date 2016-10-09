<%
String page_title = "Role Detail New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
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
            %>
            <option value="<%=role.id%>"><%=role.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="roleAttrId">Role Attribute</label>
        <select class="combobox form-control" id="roleAttrId" required>
            <option></option>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="10" placeholder="Enter detailed description" required></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

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

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/role_detail/create"), { 'roleId': $('#roleId').val(), 'roleAttrId': $('#roleAttrId').val(), 'content': $('#content').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    jumpCurrent();
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                }
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#create_new').val("false");
                    $('#form').submit();
                });

                $('#save_new_btn').click(function(){
                    $('#create_new').val("true");
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
