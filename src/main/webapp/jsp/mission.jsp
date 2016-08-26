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
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <%
        List<Mission> missions = MissionManager.getInstance().getMissions();
        Mission accepted = MissionManager.getInstance().getAcceptedMission();
        for(Mission mission : missions) {
        %>
        <tr>
            <td><%=mission.id%></td>
            <td><%=mission.name%></td>
            <td><%=MissionManager.getInstance().getContent(mission)%></td>
            <td><%=mission.reward.getName()%></td>
            <td><%=mission.status.name()%></td>
            <td>
                <div class="btn-group">
                    <%
                    if(accepted != null && accepted == mission) {
                    %>
                    <button type="button" class="btn btn-warning btn-xs" id="abandon_btn" onclick="javascript:abandonMission(<%=mission.id%>)">Abandon</button>
                    <%
                    }
                    else if(accepted != null && accepted != mission) {
                    }
                    else {
                    %>
                    <button type="button" class="btn btn-info btn-xs" id="accept_btn" onclick="javascript:acceptMission(<%=mission.id%>)">Accept</button>
                    <%
                    }
                    %>
                </div>
            </td>
        </tr>
        <%
        }
        %>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<script>
            function acceptMission(id) {
                bootbox.confirm("Are you sure to accept this mission?", function(result){
                    if(result) {
                        $.get(getAPIURL("api/mission/accept?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpCurrent();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            function abandonMission(id) {
                bootbox.confirm("Are you sure to abandon this mission?", function(result){
                    if(result) {
                        $.get(getAPIURL("api/mission/abandon?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpCurrent();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
</script>
<%@ include file="footer.jsp" %>
