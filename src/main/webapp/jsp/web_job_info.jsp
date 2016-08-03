<%@ page import="org.wilson.world.web.*" %>
<%
String page_title = "Web Job Info";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Web Job Info</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Next Run Time</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<WebJob> jobs = WebManager.getInstance().getJobs();
                Collections.sort(jobs, new Comparator<WebJob>(){
                    public int compare(WebJob j1, WebJob j2) {
                        return j1.getName().compareTo(j2.getName());
                    }
                });
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                for(WebJob job : jobs) {
                    String status = WebManager.getInstance().getJobStatus(job);
                    String nextRunTime = WebManager.getInstance().getNextRunTime(job, tz);
                    boolean enabled = WebManager.getInstance().isEnabled(job);
                    String enabledStr = enabled ? "none" : "block";
                    String disabledStr = enabled ? "block" : "none";
                %>
                <tr>
                    <td><%=job.getId()%></td>
                    <td><%=job.getName()%></td>
                    <td><%=status%></td>
                    <td><%=nextRunTime%></td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-warning btn-xs" id="enable_btn" style="display:<%=enabledStr%>" onclick="javascript:enableJob(<%=job.getId()%>)">Enable</button>
                            <button type="button" class="btn btn-warning btn-xs" id="disable_btn" style="display:<%=disabledStr%>" onclick="javascript:disableJob(<%=job.getId()%>)">Disable</button>
                            <button type="button" class="btn btn-info btn-xs" id="debug_btn" onclick="javascript:debugJob(<%=job.getId()%>)">Debug</button>
                        </div>
                    </td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            function enableJob(id) {
                $.get(getAPIURL("api/hopper/enable?id=" + id), function(data){
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
            function disableJob(id) {
                $.get(getAPIURL("api/hopper/disable?id=" + id), function(data){
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
            function debugJob(id) {
                $.get(getAPIURL("api/hopper/debug?id=" + id), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
</script>
<%@ include file="footer.jsp" %>
