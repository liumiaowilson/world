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

PageInterceptor interceptor = new PageInterceptor(pagelet);
String next = interceptor.executeServerCode(request, response);
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
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">
        <link rel="icon" href="<%=basePath%>/favicon.ico?v=2">

        <title><%=page_title%></title>
        <link href="<%=cm.getConfig("css.bootstrap.url", "../css/bootstrap.min.css")%>" rel="stylesheet">
        <style>
<%
interceptor.renderCSS(out);
%>
        </style>
    </head>
    <body>
        <%
        interceptor.renderHTML(out);
        %>

        <script src="<%=cm.getConfig("js.jquery.url", "../js/jquery-2.2.4.min.js")%>"></script>
        <script src="<%=cm.getConfig("js.bootstrap.url", "../js/bootstrap.min.js")%>"></script>
        <script>
                <%
                interceptor.renderClientScript(out);
                %>
        </script>
    </body>
</html>
