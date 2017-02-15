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
                    <th>Progress</th>
                    <th>Spent Time</th>
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
                    String progress = WebManager.getInstance().getProgressStatus(job);
                    String nextRunTime = WebManager.getInstance().getNextRunTime(job, tz);
                    boolean enabled = WebManager.getInstance().isEnabled(job);
                    String enabledStr = enabled ? "none" : "block";
                    String disabledStr = enabled ? "block" : "none";
                    boolean isInProgress = WebManager.getInstance().isWebJobInProgress(job);
                    String stopBtnStr = isInProgress ? "javascript:stopJob(" + job.getId() + ")" : "";
                    String spentTime = WebManager.getInstance().getSpentTimeDisplay(job);
                %>
                <tr>
                    <td><%=job.getId()%></td>
                    <td><%=job.getName()%></td>
                    <td><%="Error".equals(status) ? "<span style='color: red'>" + status + "</span>" : status%></td>
                    <td><%=progress%></td>
                    <td><%=spentTime%></td>
                    <td><%=nextRunTime%></td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-warning btn-xs" id="enable_btn" style="display:<%=enabledStr%>" onclick="javascript:enableJob(<%=job.getId()%>)">Enable</button>
                            <button type="button" class="btn btn-warning btn-xs" id="disable_btn" style="display:<%=disabledStr%>" onclick="javascript:disableJob(<%=job.getId()%>)">Disable</button>
                            <button type="button" class="btn btn-info btn-xs" id="debug_btn" onclick="javascript:debugJob(<%=job.getId()%>)">Debug</button>
                            <button type="button" class="btn btn-warning btn-xs <%=isInProgress ? "" : "disabled"%>" id="stop_btn" onclick="<%=stopBtnStr%>">Stop</button>
                        </div>
                    </td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <button type="button" class="btn btn-default" id="disable_all_btn" onclick="javascript:disableAll()">Disable All</button>
        <button type="button" class="btn btn-default" id="enable_all_btn" onclick="javascript:enableAll()">Enable All</button>
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
                        jumpCurrent();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function stopJob(id) {
                $.get(getAPIURL("api/web/stop?id=" + id), function(data){
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
            function disableAll() {
                $.get(getAPIURL("api/hopper/disable_all"), function(data){
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
            function enableAll() {
                $.get(getAPIURL("api/hopper/enable_all"), function(data){
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
</script>
<%@ include file="footer.jsp" %>
