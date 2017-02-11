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

PageInterceptor interceptor = new PageInterceptor(pagelet);
String next = interceptor.executeServerCode(request, response);
if(next != null) {
    response.sendRedirect(next);
    return;
}

String page_title = pagelet.title;
%>
<%@ include file="import_css.jsp" %>
<style>
<%
interceptor.renderCSS(out);
%>
</style>
<%@ include file="navbar.jsp" %>
<%
interceptor.renderHTML(out);
%>
<%@ include file="import_script.jsp" %>
<script>
            <%
            interceptor.renderClientScript(out);
            %>
</script>
<%@ include file="footer.jsp" %>
