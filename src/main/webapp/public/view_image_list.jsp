<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.image.*" %>
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

        <title>View Image List</title>
    </head>

    <body>
        <%
        ImageList image_list = (ImageList)request.getSession().getAttribute("world-public-image_list");
        request.getSession().removeAttribute("world-public-image_list");

        List<ImageRef> refs = ImageListManager.getInstance().getImageRefs(image_list);
        for(ImageRef ref : refs) {
        %>
        <a href="<%=ref.getUrl()%>"><%=ref.getName()%></a><br/>
        <%
        }
        %>
        <form action="<%=basePath%>/api/image_list/view_public" method="post">
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="View"/>
        </form>
    </body>
</html>
