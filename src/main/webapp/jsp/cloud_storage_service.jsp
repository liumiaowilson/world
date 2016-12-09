<%@ page import="org.wilson.world.cloud.*" %>
<%
String page_title = "Cloud Storage Service";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Cloud Storage Service</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Class Name</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<CloudStorageService> services = CloudStorageManager.getInstance().getCloudStorageServices();
                Collections.sort(services, new Comparator<CloudStorageService>(){
                    public int compare(CloudStorageService s1, CloudStorageService s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(CloudStorageService service : services) {
                %>
                <tr>
                    <td><a href="<%=service.getServiceUrl()%>"><%=service.getName()%></a></td>
                    <td><%=service.getClass().getCanonicalName()%></td>
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
