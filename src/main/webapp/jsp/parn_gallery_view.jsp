<%@ page import="org.wilson.world.parn.*" %>
<%
String page_title = "Parn Gallery View";
%>
<%@ include file="header.jsp" %>
<%
ParnItem parn_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
parn_item = ParnManager.getInstance().getParnItem(id);
if(parn_item == null) {
    response.sendRedirect("parn_gallery.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Parn Gallery View</h3>
    </div>
    <div class="panel-body">
        <%
        boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
        if(pass) {
        %>
        <div id="image">
            <img src="<%=ParnManager.getInstance().getImageUrl(parn_item)%>" alt="<%=parn_item.name%>"/>
        </div>
        <%
        }
        else {
        %>
        <div class="alert alert-danger" role="alert">No valid gallery ticket could be found</div>
        <%
        }
        %>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
