<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.pagelet.*" %>
<%@ page import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

ConfigManager cm = ConfigManager.getInstance();

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
    request.getSession().setAttribute("world-public-error", "No pagelet is found.");
    response.sendRedirect("../public_error.jsp");
    return;
}

if(!"Public".equals(pagelet.type)) {
    request.getSession().setAttribute("world-public-error", "No pagelet is found.");
    response.sendRedirect("../public_error.jsp");
    return;
}

PageCreator creator = new PageCreator(pagelet);
String next = creator.executeServerCode(request, response);
if(next != null) {
    response.sendRedirect(next);
    return;
}

String page_title = pagelet.title;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <%
        List<org.wilson.world.pagelet.Page> pages = creator.getPages();
        if(pages != null) {
            for(org.wilson.world.pagelet.Page p : pages) {
                for(Map.Entry<String, String> meta : p.getMetas().entrySet()) {
                    String metaName = meta.getKey();
                    String metaContent = meta.getValue();
        %>
        <meta name="<%=metaName%>" content="<%=metaContent%>">
        <%
                }
            }
        }
        %>

        <link rel="icon" href="<%=basePath%>/favicon.ico?v=2">

        <title><%=page_title%></title>
        <%
        creator.renderStyles(out);
        %>
        <style>
<%
creator.renderCSS(out);
%>
        </style>
    </head>
    <body>
        <%
        creator.renderHTML(out);
        %>

        <%
        creator.renderScripts(out);
        %>
        <script>
                <%
                creator.renderClientScript(out);
                %>
        </script>
    </body>
</html>
