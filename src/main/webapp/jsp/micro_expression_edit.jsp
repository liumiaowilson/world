<%
String page_title = "Micro Expression Edit";
%>
<%@ include file="header.jsp" %>
<%
MicroExpression micro_expression = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
micro_expression = MicroExpressionManager.getInstance().getMicroExpression(id);
if(micro_expression == null) {
    response.sendRedirect("micro_expression_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=micro_expression.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=micro_expression.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <ul>
            <%
            for(String def : micro_expression.definition) {
            %>
            <li><%=def%></li>
            <%
            }
            %>
        </ul>
        <%
        for(String example : micro_expression.examples) {
        %>
        <img src="<%=URLManager.getInstance().getBaseUrl()%><%=example%>"/>
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
