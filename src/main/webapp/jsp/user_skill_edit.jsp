<%@ page import="org.wilson.world.skill.*" %>
<%
String page_title = "User Skill Edit";
%>
<%@ include file="header.jsp" %>
<%
UserSkill user_skill = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
user_skill = UserSkillManager.getInstance().getUserSkill(id);
if(user_skill == null) {
    response.sendRedirect("user_skill_list.jsp");
    return;
}
Skill s = SkillDataManager.getInstance().getSkill(user_skill.skillId);
if(s == null) {
    response.sendRedirect("user_skill_list.jsp");
    return;
}
boolean disabled = UserSkillManager.getInstance().isDisabled(user_skill);
String disabledStr = disabled ? "disabled" : "";
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=user_skill.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=user_skill.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" disabled><%=user_skill.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Type</label>
        <input type="text" class="form-control" id="type" maxlength="20" placeholder="Enter type" value="<%=s.getType()%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="scope">Scope</label>
        <input type="text" class="form-control" id="scope" maxlength="20" placeholder="Enter scope" value="<%=s.getScope()%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="target">Target</label>
        <input type="text" class="form-control" id="target" maxlength="20" placeholder="Enter target" value="<%=s.getTarget()%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="cost">Cost</label>
        <input type="text" class="form-control" id="cost" maxlength="20" placeholder="Enter cost" value="<%=s.getCost()%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="cooldown">Cooldown</label>
        <input type="text" class="form-control" id="cooldown" maxlength="20" placeholder="Enter cooldown" value="<%=s.getCooldown()%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="level">Level</label>
        <input type="text" class="form-control" id="level" maxlength="20" placeholder="Enter level" value="<%=user_skill.level%>" disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li class="<%=disabledStr%>"><a href="javascript:void(0)" onclick="<%=disabled ? "" : "useUserSkill()"%>">Use</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteUserSkill()">Forget</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function useUserSkill() {
                bootbox.confirm("Are you sure to use this skill?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/user_skill/use?id=" + id), function(data){
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
            function deleteUserSkill() {
                bootbox.confirm("Are you sure to forget this skill?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/user_skill/delete?id=" + id), function(data){
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
            });
</script>
<%@ include file="footer.jsp" %>
