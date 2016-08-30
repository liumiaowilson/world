<%
String page_title = "Strategy Edit";
%>
<%@ include file="header.jsp" %>
<%
Strategy strategy = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
strategy = StrategyManager.getInstance().getStrategy(id);
if(strategy == null) {
    response.sendRedirect("strategy_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=strategy.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=strategy.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <p><b><%=strategy.definition%></b></p>
        <%
        for(String example : strategy.examples) {
        %>
        <p><%=example%></p>
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
