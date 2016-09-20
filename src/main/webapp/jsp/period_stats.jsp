<%@ page import="org.wilson.world.period.*" %>
<%
String page_title = "Period Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Period Statistics</h3>
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
                PeriodReport report = PeriodManager.getInstance().getPeriodReport();
                %>
                <tr>
                    <td>Average Cycle(Days)</td>
                    <td><%=report.cycleDays%></td>
                </tr>
                <tr>
                    <td>Average Duration(Days)</td>
                    <td><%=report.durationDays%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
