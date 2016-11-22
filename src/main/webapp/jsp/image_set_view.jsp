<%@ page import="org.wilson.world.image.*" %>
<%
String page_title = "Image Set View";
%>
<%@ include file="header.jsp" %>
<%
ImageSet image_set = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
image_set = ImageSetManager.getInstance().getImageSet(id);
if(image_set == null) {
    response.sendRedirect("image_set_list.jsp");
    return;
}

List<ImageRef> refs = ImageSetManager.getInstance().getImageRefs(image_set);
int pages = refs.size();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Image Set View</h3>
    </div>
    <div class="panel-body">
        <div id="image_set" class="carousel slide" data-ride="carousel">
            <!-- Indicators -->
            <ol class="carousel-indicators">
                <%
                for(int i = 0; i < pages; i++) {
                    String active = i == 0 ? "class='active'" : "";
                %>
                <li data-target="#image_set" data-slide-to="<%=i%>" <%=active%>></li>
                <%
                }
                %>
            </ol>

            <!-- Wrapper for slides -->
            <div class="carousel-inner" role="listbox">
                <%
                for(int i = 1; i <= pages; i++) {
                    String active = i == 1 ? "active" : "";
                    ImageRef ref = refs.get(i - 1);
                %>
                <div class="item <%=active%>">
                    <img src="<%=ref.getUrl()%>" alt="<%=i%>.jpg">
                </div>
                <%
                }
                %>
            </div>

            <!-- Left and right controls -->
            <a class="left carousel-control" href="#image_set" role="button" data-slide="prev">
                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="right carousel-control" href="#image_set" role="button" data-slide="next">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
