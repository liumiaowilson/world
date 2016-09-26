<%@ page import="org.wilson.world.dress.*" %>
<%
String page_title = "Season Energy Edit";
%>
<%@ include file="header.jsp" %>
<%
SeasonEnergy season_energy = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
season_energy = DressManager.getInstance().getSeasonEnergy(id);
if(season_energy == null) {
    response.sendRedirect("season_energy_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=season_energy.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=season_energy.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <table class="table table-striped table-bordered">
        <thead>
            <tr>
                <th>Name</th>
                <th>Value</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Keywords</td>
                <td><%=season_energy.getKeywords()%></td>
            </tr>
            <tr>
                <td>Form</td>
                <td><%=season_energy.form%></td>
            </tr>
            <tr>
                <td>Line</td>
                <td><%=season_energy.line%></td>
            </tr>
            <tr>
                <td>Energy</td>
                <td><%=season_energy.energy%></td>
            </tr>
            <tr>
                <td>Color</td>
                <td><%=season_energy.color%></td>
            </tr>
            <tr>
                <td>Texture</td>
                <td><%=season_energy.texture%></td>
            </tr>
            <tr>
                <td>Contrast</td>
                <td><%=season_energy.contrast%></td>
            </tr>
            <tr>
                <td>Colors</td>
                <td><%=season_energy.colors%></td>
            </tr>
            <tr>
                <td>Size</td>
                <td><%=season_energy.size%></td>
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
