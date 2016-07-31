<%@ page import="org.wilson.world.tick.*" %>
<%
String page_title = "NPC Rate";
%>
<%@ include file="header.jsp" %>
<%
Attacker npc = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
npc = NPCManager.getInstance().getNPC(id);
if(npc == null) {
    response.sendRedirect("npc.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">NPC Rate</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                RateInfo info = NPCManager.getInstance().rate(npc);
                %>
                <tr>
                    <td>Win Rate</td>
                    <td><%=info.win_rate%>%</td>
                </tr>
                <tr>
                    <td>Tie Rate</td>
                    <td><%=info.tie_rate%>%</td>
                </tr>
                <tr>
                    <td>Average Lost HP</td>
                    <td><%=info.avg_lost_hp%></td>
                </tr>
                <tr>
                    <td>Average Steps</td>
                    <td><%=info.avg_steps%></td>
                </tr>
            </tbody>
        </table>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
