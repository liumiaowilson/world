<%@ page import="java.util.*" %>
<%
String from_url = "user.jsp";
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
                    <td><%=ExpManager.getInstance().getLevel()%></td>
                </tr>
                <tr>
                    <td>Level Experience</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="<%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>%"><span class="sr-only"><%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>% Complete</span></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>Next Level Experience</td>
                    <td><%=ExpManager.getInstance().getNextLevelExp()%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
