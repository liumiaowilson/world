<%@ page import="org.wilson.world.beauty.*" %>
<%
String page_title = "Beauty Gallery View";
%>
<%@ include file="header.jsp" %>
<%
BeautyItem beauty_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
beauty_item = BeautyManager.getInstance().getBeautyItem(id);
if(beauty_item == null) {
    response.sendRedirect("beauty_gallery.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Beauty Gallery View</h3>
    </div>
    <div class="panel-body">
        <%
        boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
        if(pass) {
        %>
        <div id="image">
            <img src="<%=BeautyManager.getInstance().getImageUrl(beauty_item)%>" alt="<%=beauty_item.name%>"/>
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
