<%
String page_title = "Chat Skill Edit";
%>
<%@ include file="header.jsp" %>
<%
ChatSkill chat_skill = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
chat_skill = ChatSkillManager.getInstance().getChatSkill(id);
if(chat_skill == null) {
    response.sendRedirect("chat_skill_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=chat_skill.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=chat_skill.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <p><b><%=chat_skill.definition%></b></p>
        <%
        for(List<String> example : chat_skill.examples) {
            for(String statement : example) {
        %>
        <p><i><%=statement%></i></p>
        <%
            }
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
