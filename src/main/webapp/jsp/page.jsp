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

PageCreator creator = new CompositePageCreator(pagelet);
String next = creator.executeServerCode(request, response);
if(next != null) {
    response.sendRedirect(next);
    return;
}

String page_title = pagelet.title;
%>
<%@ include file="import_css.jsp" %>
<%
creator.renderStyles(out);
%>
<style>
<%
creator.renderCSS(out);
%>
</style>
<%@ include file="navbar.jsp" %>
<%
creator.renderHTML(out);
%>
<%@ include file="import_script.jsp" %>
<%
creator.renderScripts(out);
%>
<script>
            <%
            creator.renderClientScript(out);
            %>
</script>
<%@ include file="footer.jsp" %>
