<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <title>View Novel Document</title>
    </head>

    <body>
        <%
        String novel_document = (String)request.getSession().getAttribute("world-public-novel_document");
        if(novel_document == null) {
            novel_document = "";
        }
        else {
            request.getSession().removeAttribute("world-public-novel_document");
        }
        String novel_document_id = (String)request.getSession().getAttribute("world-public-novel_document_id");
        if(novel_document_id == null) {
            novel_document_id = "";
        }
        else {
            request.getSession().removeAttribute("world-public-novel_document_id");
        }
        %>
        <%=novel_document%>
        <form action="<%=basePath%>/api/novel_document/view_public" method="post">
            <textarea name="comment" style="width: 100%" rows=5></textarea>
            <label><input type="checkbox" name="showImage" checked="checked"/>Show Image</label><br/>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
            <input type="hidden" name="docId" value="<%=novel_document_id%>"/>
        </form>
    </body>
</html>
