<%
String page_title = "Rhetoric Edit";
%>
<%@ include file="header.jsp" %>
<%
RhetoricalDevice device = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
device = RhetoricManager.getInstance().getRhetoricalDevice(id);
if(device == null) {
    response.sendRedirect("rhetoric_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=device.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=device.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="well">
        <p><b><%=device.definition%></b></p>
        <%
        for(String example : device.examples) {
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
