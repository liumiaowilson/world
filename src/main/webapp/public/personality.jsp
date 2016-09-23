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

        <title>Personality</title>
    </head>

    <body>
        <%
        Personality personality = PersonalityManager.getInstance().getLastCreatedPersonality();
        String last = null;
        if(personality == null) {
            last = "Last personality not found.";
        }
        else {
            last = "Last personality is [" + personality.name + "].";
        }
        %>
        <%=last%>
        <form action="<%=basePath%>/api/personality/create_public" method="post">
            <table>
                <tr>
                    <td>Name</td>
                    <td>
                        <input type="text" name="name"/>
                    </td>
                </tr>
                <tr>
                    <td>Tags</td>
                    <td>
                        <select onchange="selectTag(this)">
                            <%
                            List<String> tags = PersonalityManager.getInstance().getTags();
                            Collections.sort(tags);
                            for(String tag : tags) {
                            %>
                            <option value="<%=tag%>"><%=tag%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Tag</td>
                    <td>
                        <input type="text" name="tags" id="tags"/>
                    </td>
                </tr>
            </table>
            <textarea name="description" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
    <script>
                                 function selectTag(sel) {
                                    document.getElementById("tags").value = sel.value;
                                 }
    </script>
</html>
