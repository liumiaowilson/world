<%@ page import="org.wilson.world.controller.*" %>
<%@ include file="header.jsp" %>
<%
Page pageOb = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
pageOb = PageManager.getInstance().getPage(id);
if(pageOb == null) {
    response.sendRedirect("page_list.jsp");
    return;
}

PageRedirector redirector = (PageRedirector)request.getSession().getAttribute("world-page_redirector");
if(redirector == null) {
    response.sendRedirect("page_list.jsp");
    return;
}
request.getSession().removeAttribute("world-page_redirector");
String page_title = redirector.getPageTitle();
if(page_title == null) {
    page_title = pageOb.name;
}
%>
<%@ include file="import_css.jsp" %>
<%
for(String style : redirector.getStyles()) {
    String import_style = "import_css_" + style + ".jsp";
%>
<jsp:include page="<%= import_style %>" />
<%
}
%>
<%@ include file="navbar.jsp" %>
<%
String pageContent = redirector.getPageContent(pageOb);
%>
<%=pageContent%>
<%@ include file="import_script.jsp" %>
<%
for(String script : redirector.getScripts()) {
    String import_script = "import_script_" + script + ".jsp";
%>
<jsp:include page="<%= import_script %>" />
<%
}
%>
<script>
            <%
            String scriptPageName = redirector.getScriptPageName();
            if(scriptPageName != null) {
                Page scriptPage = PageManager.getInstance().getPage(scriptPageName);
                if(scriptPage != null) {
                    String scriptContent = redirector.getPageContent(scriptPage);
                    if(scriptContent != null) {
            %>
                <%=scriptContent%>
            <%
                    }
                }
            }
            %>
</script>
<%@ include file="footer.jsp" %>
