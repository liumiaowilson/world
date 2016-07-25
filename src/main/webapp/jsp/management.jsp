<%
String page_title = "Management";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">System Info</h3>
    </div>
    <div class="panel-body">
        <table id="sys_info_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>System Up Time</td>
                    <%
                    long upTime = ConsoleManager.getInstance().getUpTime();
                    %>
                    <td><%=TimeUtils.getTimeReadableString(upTime)%></td>
                </tr>
                <tr>
                    <td>System Max Memory</td>
                    <%
                    long maxMemory = ConsoleManager.getInstance().maxMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(maxMemory)%></td>
                </tr>
                <tr>
                    <td>System Total Memory</td>
                    <%
                    long totalMemory = ConsoleManager.getInstance().totalMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(totalMemory)%></td>
                </tr>
                <tr>
                    <td>System Used Memory</td>
                    <%
                    long usedMemory = ConsoleManager.getInstance().usedMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(usedMemory)%></td>
                </tr>
                <tr>
                    <td>System Free Memory</td>
                    <%
                    long freeMemory = ConsoleManager.getInstance().freeMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(freeMemory)%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%
if(ConfigManager.getInstance().isOpenShiftApp()) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">OpenShift</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            String uuid = System.getenv("OPENSHIFT_APP_UUID");
            String name = System.getenv("OPENSHIFT_APP_NAME");
            %>
            <a href="https://openshift.redhat.com/app/console/application/<%=uuid%>-<%=name%>" class="list-group-item">OpenShift Console</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Jenkins</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            String jenkins_url = System.getenv("JENKINS_URL");
            String jenkins_username = System.getenv("JENKINS_USERNAME");
            String jenkins_password = System.getenv("JENKINS_PASSWORD");
            %>
            <a href="<%=jenkins_url%>" class="list-group-item">Jenkins Server</a>
        </div>
        <div class="alert alert-info" role="alert">
            Username is <strong><%=jenkins_username%></strong> and password is <strong><%=jenkins_password%></strong>.
        </div>
    </div>
</div>
<%
}
else {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">OpenShift</h3>
    </div>
    <div class="panel-body">
        <div class="alert alert-warning" role="alert">
            Management is not enabled unless run on OpenShift.
        </div>
    </div>
</div>
<%
}
%>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
