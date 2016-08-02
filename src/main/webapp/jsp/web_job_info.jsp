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
                for(WebJob job : jobs) {
                    HopperData data = HopperDataManager.getInstance().getHopperDataByHopperId(job.getId());
                    String status = "N/A";
                    if(data != null) {
                        status = data.status;
                    }
                %>
                <tr>
                    <td><%=job.getId()%></td>
                    <td><%=job.getName()%></td>
                    <td><%=status%></td>
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
