<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.util.*" %>
<%@ page import="java.util.*" %>
<%
String token = (String)session.getAttribute("world-token");
if(token == null || !SecManager.getInstance().isValidToken(token)) {
    response.sendRedirect("signin.jsp?from=" + from_url);
}
ConfigManager cm = ConfigManager.getInstance();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">
        <!--<link rel="icon" href="../../favicon.ico">-->

        <title>World</title>
