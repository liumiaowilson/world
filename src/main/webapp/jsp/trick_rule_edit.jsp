<%
String page_title = "Trick Rule Edit";
%>
<%@ include file="header.jsp" %>
<%
TrickRule trick_rule = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
trick_rule = TrickRuleManager.getInstance().getTrickRule(id);
if(trick_rule == null) {
    response.sendRedirect("trick_rule_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=trick_rule.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=trick_rule.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <p><b><%=trick_rule.definition%></b></p>
        <%
        for(String example : trick_rule.examples) {
        %>
        <p><i><%=example%></i></p>
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
