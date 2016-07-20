<%
String page_title = "User";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">User Information</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th style="width:40%">Attribute</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>User Name</td>
                    <td><%=(String)session.getAttribute("world-user")%></td>
                </tr>
                <tr>
                    <td>User Level</td>
                    <td><span class="badge"><%=ExpManager.getInstance().getLevel()%></span></td>
                </tr>
                <tr>
                    <td>Level Experience</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="<%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>%"><%=ExpManager.getInstance().getLevelInfo()%></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>To Next Level Experience</td>
                    <td><%=ExpManager.getInstance().getToNextLevelExp()%></td>
                </tr>
                <tr>
                    <td>HP</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="<%=CharManager.getInstance().getCurrentHPPercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=CharManager.getInstance().getCurrentHPPercentage()%>%"><%=CharManager.getInstance().getHP()%>/<%=CharManager.getInstance().getMaxHP()%></div>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
