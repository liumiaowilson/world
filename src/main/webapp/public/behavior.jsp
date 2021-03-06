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

        <title>Behavior</title>
    </head>

    <body>
        <%=BehaviorManager.getInstance().getLastBehaviorDisplay()%>
        <form action="<%=basePath%>/api/behavior/create_public" method="post">
            <table>
                <tr>
                    <td>Behavior</td>
                    <td>
                        <select name="defId">
                            <option></option>
                            <%
                            List<BehaviorDef> defs = BehaviorDefManager.getInstance().getBehaviorDefs();
                            Collections.sort(defs, new Comparator<BehaviorDef>(){
                                public int compare(BehaviorDef d1, BehaviorDef d2) {
                                    return d1.name.compareTo(d2.name);
                                }
                            });
                            for(BehaviorDef def : defs) {
                            %>
                            <option value="<%=def.id%>"><%=def.name%></option>
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
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
