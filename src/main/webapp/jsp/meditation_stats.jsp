<%
String page_title = "Meditation Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Meditation Statistics</h3>
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
                long [] stats = MeditationManager.getInstance().getMeditationStats();
                %>
                <tr>
                    <td>Count</td>
                    <td><%=stats[0]%></td>
                </tr>
                <tr>
                    <td>Average Duration</td>
                    <td><%=TimeUtils.getTimeReadableString(stats[1])%></td>
                </tr>
                <tr>
                    <td>Min Duration</td>
                    <td><%=TimeUtils.getTimeReadableString(stats[2])%></td>
                </tr>
                <tr>
                    <td>Max Duration</td>
                    <td><%=TimeUtils.getTimeReadableString(stats[3])%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
