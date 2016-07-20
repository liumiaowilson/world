<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Welcome</title>
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
        <form action="api/post/send" method="post">
            <textarea name="post" style="width: 100%" rows=10></textarea>
            <br/>
            <input type="submit" value="Send"/>
        </form>
    </body>
</html>
