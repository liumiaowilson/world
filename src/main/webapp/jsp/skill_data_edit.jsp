<%
String page_title = "Skill Data Edit";
%>
<%@ include file="header.jsp" %>
<%
SkillData skill_data = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
skill_data = SkillDataManager.getInstance().getSkillData(id);
if(skill_data == null) {
    response.sendRedirect("skill_data_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=skill_data.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=skill_data.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=skill_data.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Skill Type</label>
        <select class="combobox form-control" id="type" required>
            <option></option>
            <%
            List<String> types = SkillDataManager.getInstance().getSkillTypes();
            Collections.sort(types);
            for(String type : types) {
                boolean selected = (type.equals(skill_data.type));
                String selectedStr = selected ? "selected" : "";
            %>
            <option value="<%=type%>" <%=selectedStr%>><%=type%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="scope">Skill Scope</label>
        <select class="combobox form-control" id="scope" required>
            <option></option>
            <%
            List<String> scopes = SkillDataManager.getInstance().getSkillScopes();
            Collections.sort(scopes);
            for(String scope : scopes) {
                boolean selected = (scope.equals(skill_data.scope));
                String selectedStr = selected ? "selected" : "";
            %>
            <option value="<%=scope%>" <%=selectedStr%>><%=scope%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="target">Skill Target</label>
        <select class="combobox form-control" id="target" required>
            <option></option>
            <%
            List<String> targets = SkillDataManager.getInstance().getSkillTargets();
            Collections.sort(targets);
            for(String target : targets) {
                boolean selected = (target.equals(skill_data.target));
                String selectedStr = selected ? "selected" : "";
            %>
            <option value="<%=target%>" <%=selectedStr%>><%=target%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="cost">Cost</label>
        <input type="number" class="form-control" id="cost" placeholder="Enter cost" value="<%=skill_data.cost%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="cooldown">Cooldown</label>
        <input type="number" class="form-control" id="cooldown" placeholder="Enter cooldown" value="<%=skill_data.cooldown%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="canTrigger">Can Trigger Implementation</label>
        <input type="text" class="form-control" id="canTrigger" maxlength="50" placeholder="Enter can-trigger implementation" value="<%=skill_data.canTrigger%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="trigger">Trigger Implementation</label>
        <input type="text" class="form-control" id="trigger" maxlength="50" placeholder="Enter trigger implementation" value="<%=skill_data.trigger%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteSkillData()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteSkillData() {
                bootbox.confirm("Are you sure to delete this skill data?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/skill_data/delete?id=" + id), function(data){
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

                        l.ladda('start');
                        $.post(getAPIURL("api/skill_data/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'type': $('#type').val(), 'scope': $('#scope').val(), 'target': $('#target').val(), 'cost': $('#cost').val(), 'cooldown': $('#cooldown').val(), 'canTrigger': $('#canTrigger').val(), 'trigger': $('#trigger').val()}, function(data) {
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
