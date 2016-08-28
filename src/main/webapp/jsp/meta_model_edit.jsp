<%
String page_title = "Meta Model Edit";
%>
<%@ include file="header.jsp" %>
<%
MetaModel meta_model = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
meta_model = MetaModelManager.getInstance().getMetaModel(id);
if(meta_model == null) {
    response.sendRedirect("meta_model_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=meta_model.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=meta_model.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <p><b><%=meta_model.definition%></b></p>
        <%
        for(Map.Entry<String, String> entry : meta_model.examples.entrySet()) {
        %>
        <p><b><%=entry.getKey()%></b></p>
        <p><i><%=entry.getValue()%></i></p>
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
