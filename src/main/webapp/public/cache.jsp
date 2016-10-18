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

        <title>Cache</title>
    </head>

    <body>
        <%
        String msg = (String)request.getSession().getAttribute("world-cache-msg");
        if(msg != null) {
            request.getSession().removeAttribute("world-cache-msg");
        %>
        <%=msg%>
        <%
        }
        %>
        <form action="<%=basePath%>/api/item/reload_cache_public" method="post">
            <table>
                <tr>
                    <td>Cache</td>
                    <td>
                        <select name="name">
                            <option></option>
                            <%
                            List<String> names = CacheManager.getInstance().getCacheNames();
                            for(String name : names) {
                            %>
                            <option value="<%=name%>"><%=name%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Key</td>
                    <td>
                        <input type="password" name="key"/>
                    </td>
                </tr>
            </table>
            <input type="submit" value="Reload"/>
        </form>
    </body>
</html>
