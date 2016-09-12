<%@ page import="org.wilson.world.thread.*" %>
<%
String page_title = "Worker";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Worker</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Periods</th>
                    <th>Time Per Period</th>
                    <th>Working Time Per Period</th>
                    <th>Working Percentage(%)</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<WorkerInfo> infos = ThreadPoolManager.getInstance().getWorkerInfos();
                Collections.sort(infos, new Comparator<WorkerInfo>(){
                    public int compare(WorkerInfo i1, WorkerInfo i2) {
                        return i1.name.compareTo(i2.name);
                    }
                });
                for(WorkerInfo info : infos) {
                %>
                <tr>
                    <td><%=info.name%></td>
                    <td><%=info.periods%></td>
                    <td><%=TimeUtils.getTimeReadableString(info.timePerPeriod)%></td>
                    <td><%=TimeUtils.getTimeReadableString(info.workingTimePerPeriod)%></td>
                    <td><%=info.workingPercent%></td>
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
