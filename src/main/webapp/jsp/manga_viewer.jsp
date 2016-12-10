<%@ page import="org.wilson.world.manga.*" %>
<%
String page_title = "Manga Viewer";
%>
<%@ include file="header.jsp" %>
<%
Manga manga = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
manga = MangaManager.getInstance().getManga(id);
if(manga == null) {
    response.sendRedirect("manga_list.jsp");
    return;
}
List<String> urls = MangaManager.getInstance().getImageUrls(manga);
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_viewer.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=manga.name%></h3>
    </div>
    <div class="panel-body">
        <%
        boolean pass = InventoryItemManager.getInstance().readGalleryTicket();
        if(pass) {
        %>
        <div>
            <ul class="images">
                <%
                for(int i = 0; i < urls.size(); i++) {
                    String url = urls.get(i);
                %>
                <li style="display:none"><img src="<%=url%>" alt="<%=(i + 1)%>"></li>
                <%
                }
                %>
            </ul>
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
<%@ include file="import_script_viewer.jsp" %>
<script>
            $(document).ready(function(){
                $(".images").viewer({
                    inline: true,
                    minWidth: 400,
                    minHeight: 400,
                    interval: 60000,
                });
            });
</script>
<%@ include file="footer.jsp" %>
