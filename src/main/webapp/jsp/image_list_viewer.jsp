<%@ page import="org.wilson.world.image.*" %>
<%
String page_title = "Image List Viewer";
%>
<%@ include file="header.jsp" %>
<%
ImageList image_list = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
image_list = ImageListManager.getInstance().getImageList(id);
if(image_list == null) {
    response.sendRedirect("image_list_list.jsp");
    return;
}

List<ImageRef> refs = ImageListManager.getInstance().getImageRefs(image_list);
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_viewer.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Image List Viewer</h3>
    </div>
    <div class="panel-body">
        <div>
            <ul class="images">
                <%
                for(ImageRef ref : refs) {
                %>
                <li style="display:none"><img src="<%=ref.getUrl()%>" alt="<%=ref.getName()%>"></li>
                <%
                }
                %>
            </ul>
        </div>
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
