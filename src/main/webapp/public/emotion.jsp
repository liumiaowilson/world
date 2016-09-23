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

        <title>Emotion</title>
    </head>

    <body>
        <%
        Emotion emotion = EmotionManager.getInstance().getLastCreatedEmotion();
        String last = null;
        if(emotion == null) {
            last = "Last emotion not found.";
        }
        else {
            last = "Last emotion is [" + emotion.name + "].";
        }
        %>
        <%=last%>
        <form action="<%=basePath%>/api/emotion/create_public" method="post">
            <table>
                <tr>
                    <td>Name</td>
                    <td>
                        <input type="text" name="name"/>
                    </td>
                </tr>
                <tr>
                    <td>Ecstacy</td>
                    <td>
                        <select name="ecstacy">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Grief</td>
                    <td>
                        <select name="grief">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Admiration</td>
                    <td>
                        <select name="admiration">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Loathing</td>
                    <td>
                        <select name="loathing">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Rage</td>
                    <td>
                        <select name="rage">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Terror</td>
                    <td>
                        <select name="terror">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Vigilance</td>
                    <td>
                        <select name="vigilance">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Amazement</td>
                    <td>
                        <select name="amazement">
                            <%
                            for(int i = 0; i <= 100; i++) {
                            %>
                            <option value="<%=i%>"><%=i%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
            </table>
            <textarea name="description" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
