<%@ page import="org.wilson.world.mission.*" %>
<%
String page_title = "Mission";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<table class="table table-striped table-bordered">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Content</th>
            <th>Reward</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody>
        <%
        List<Mission> missions = MissionManager.getInstance().getMissions();
        for(Mission mission : missions) {
        %>
        <tr>
            <td><%=mission.id%></td>
            <td><%=mission.name%></td>
            <td><%=MissionManager.getInstance().toString(mission.target)%></td>
            <td><%=mission.reward.getName()%></td>
            <td><%=mission.status.name()%></td>
        </tr>
        <%
        }
        %>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
