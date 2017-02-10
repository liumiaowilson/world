<%@ page import="org.wilson.world.pagelet.*" %>
<%@ include file="header.jsp" %>
<%
Pagelet pagelet = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
pagelet = PageletManager.getInstance().getPagelet(id, false);
if(pagelet == null) {
    response.sendRedirect("pagelet_list.jsp");
    return;
}

org.wilson.world.pagelet.Page pageObj = PageletManager.getInstance().executeServerCode(pagelet, request, response);
String next = pageObj.getNext();
if(next != null) {
    response.sendRedirect(next);
    return;
}

String page_title = pagelet.title;
%>
<%@ include file="import_css.jsp" %>
<style>
<%=pagelet.css%>
</style>
<%@ include file="navbar.jsp" %>
<%=pagelet.html%>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                <%=pageObj.getClientScript()%>

                <%=pagelet.clientCode%>
            });
</script>
<%@ include file="footer.jsp" %>
