<%
String page_title = "Writing Skill Edit";
%>
<%@ include file="header.jsp" %>
<%
WritingSkill writing_skill = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
writing_skill = WritingSkillManager.getInstance().getWritingSkill(id);
if(writing_skill == null) {
    response.sendRedirect("writing_skill_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=writing_skill.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=writing_skill.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <h4><%=writing_skill.topic%></h4>
        <p><b><%=writing_skill.definition%></b></p>
        <%
        for(Map.Entry<String, String> entry : writing_skill.examples.entrySet()) {
            String a = entry.getKey();
            String b = entry.getValue();
        %>
        <p><b><%=a%></b></p>
        <p><i><%=b%></i></p>
        <%
        }
        %>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
