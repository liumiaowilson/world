<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.util.*" %>
<%@ page import="java.util.*" %>
<%
ConfigManager cm = ConfigManager.getInstance();
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
if(request.getQueryString() != null) {
    URLManager.getInstance().setCurrentUrl(basePath + request.getRequestURI() + "?" + request.getQueryString());
}
else {
    if(request.getRequestURI() != null) {
        URLManager.getInstance().setCurrentUrl(basePath + request.getRequestURI());
    }
    else {
        URLManager.getInstance().setCurrentUrl(basePath + "/index.jsp");
    }
}

String token = (String)session.getAttribute("world-token");
if(token == null || !SecManager.getInstance().isValidToken(token)) {
    response.sendRedirect(basePath + "/signin.jsp");
    return;
}
%>
