<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
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

        <title>Post</title>
    </head>

    <body>
        <%
        List<String> posts = PostManager.getInstance().getPosts();
        for(String post : posts) {
        %>
        <%=post%><br/>
        <%
        }
        %>
        <form action="<%=basePath%>/api/post/send" method="post">
            <textarea name="post" style="width: 100%" rows=10></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Send"/>
        </form>
    </body>
</html>
