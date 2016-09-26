<%@ page import="org.wilson.world.dress.*" %>
<%
String page_title = "Dress Color Family Edit";
%>
<%@ include file="header.jsp" %>
<%
DressColorFamily family = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
family = DressManager.getInstance().getDressColorFamily(id);
if(family == null) {
    response.sendRedirect("dress_color_family_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=family.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=family.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <table class="table table-striped table-bordered">
        <thead>
            <tr>
                <th>Name</th>
                <th>Value</th>
                <th>Color</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Spring</td>
                <td><%=family.spring%></td>
                <td <%=family.display(family.spring)%>></td>
            </tr>
            <tr>
                <td>Summer</td>
                <td><%=family.summer%></td>
                <td <%=family.display(family.summer)%>></td>
            </tr>
            <tr>
                <td>Autumn</td>
                <td><%=family.autumn%></td>
                <td <%=family.display(family.autumn)%>></td>
            </tr>
            <tr>
                <td>Winter</td>
                <td><%=family.winter%></td>
                <td <%=family.display(family.winter)%>></td>
            </tr>
            <tr>
                <td>Clear</td>
                <td><%=family.clear%></td>
                <td <%=family.display(family.clear)%>></td>
            </tr>
            <tr>
                <td>Soft</td>
                <td><%=family.soft%></td>
                <td <%=family.display(family.soft)%>></td>
            </tr>
            <tr>
                <td>Icy Warm</td>
                <td><%=family.icyWarm%></td>
                <td <%=family.display(family.icyWarm)%>></td>
            </tr>
            <tr>
                <td>Icy Cool</td>
                <td><%=family.icyCool%></td>
                <td <%=family.display(family.icyCool)%>></td>
            </tr>
        </tbody>
    </table>
    <div class="form-group">
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
