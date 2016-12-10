<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.manga.*" %>
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

        <title>View Manga</title>
    </head>

    <body>
        <%
        Manga manga = (Manga)request.getSession().getAttribute("world-public-manga");
        request.getSession().removeAttribute("world-public-manga");

        List<String> urls = MangaManager.getInstance().getImageUrls(manga);
        for(int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
        %>
        <a href="<%=url%>"><%=manga.name + "_" + i%></a><br/>
        <%
        }
        %>
        <form action="<%=basePath%>/api/manga/view_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
