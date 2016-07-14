<%@ page import="java.util.*" %>
<%
String from_url = "jobs.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Scheduled Jobs</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Next Run Time</th>
                </tr>
            </thead>
            <tbody>
                <%
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                List<JobInfo> infos = ScheduleManager.getInstance().getJobInfos(tz);
                for(JobInfo info : infos) {
                %>
                <tr>
                    <td><%=info.name%></td>
                    <td><%=info.nextTime%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
