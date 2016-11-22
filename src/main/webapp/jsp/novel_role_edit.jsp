<%@ page import="org.wilson.world.novel.*" %>
<%
String page_title = "Novel Role Edit";
%>
<%@ include file="header.jsp" %>
<%
NovelRole novel_role = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
novel_role = NovelRoleManager.getInstance().getNovelRole(id);
if(novel_role == null) {
    response.sendRedirect("novel_role_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=novel_role.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=novel_role.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="10" placeholder="Enter detailed description" required><%=novel_role.description%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="definition">Variables</label>
        <table id="definition" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<NovelVariable> variables = NovelVariableManager.getInstance().getNovelVariables();
                Collections.sort(variables, new Comparator<NovelVariable>(){
                    public int compare(NovelVariable v1, NovelVariable v2) {
                        return v1.name.compareTo(v2.name);
                    }
                });
                for(NovelVariable variable : variables) {
                    String value = novel_role.variables.get(variable.name);
                    if(value == null) {
                        value = variable.defaultValue;
                    }
                %>
                <tr>
                    <td id="name"><%=variable.name%></td>
                    <td id="value"><%=value%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
    <div class="form-group">
        <label for="image">Image</label>
        <select class="combobox form-control" id="image">
            <option></option>
            <%
            List<String> imageNames = NovelConfig.getInstance().getImageRefNames();
            Collections.sort(imageNames);
            for(String imageName : imageNames) {
                String selectedStr = imageName.equals(novel_role.image) ? "selected" : "";
            %>
            <option value="<%=imageName%>" <%=selectedStr%>><%=imageName%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteNovelRole()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            function deleteNovelRole() {
                bootbox.confirm("Are you sure to delete this novel role?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/novel_role/delete?id=" + id), function(data){
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

                $.fn.editable.defaults.mode = 'inline';
                $('#definition tbody td#value').editable();

                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        var vars = {};
                        $('#definition tbody tr').each(function(){
                            $this = $(this);
                            var name = $this.find("#name").text();
                            var value = $this.find("#value").text();
                            if(!name) {
                                validation = "Variable name should be provided.";
                                return;
                            }
                            if(name.length > 20) {
                                validation = "Variable name length should be less than 20.";
                                return;
                            }
                            if(!value) {
                                validation = "Variable value should be provided.";
                                return;
                            }
                            if(value.length > 200) {
                                validation = "Variable value length should be less than 200.";
                                return;
                            }
                            vars[name] = value;
                        });

                        l.ladda('start');
                        $.post(getAPIURL("api/novel_role/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'definition': JSON.stringify(vars), 'image': $('#image').val() }, function(data) {
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
