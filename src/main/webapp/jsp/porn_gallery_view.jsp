<%@ page import="org.wilson.world.porn.*" %>
<%
String page_title = "Porn Gallery View";
%>
<%@ include file="header.jsp" %>
<%
PornItem porn_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
porn_item = PornManager.getInstance().getPornItem(id);
if(porn_item == null) {
    response.sendRedirect("porn_gallery.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Porn Gallery View</h3>
    </div>
    <div class="panel-body">
        <div id="image">
            <img src="<%=PornManager.getInstance().getImageUrl(porn_item)%>" alt="<%=porn_item.name%>"/>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
