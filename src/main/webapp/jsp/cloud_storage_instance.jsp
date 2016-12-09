<%@ page import="org.wilson.world.cloud.*" %>
<%
String page_title = "Cloud Storage Instance";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Cloud Storage Instance</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Service Name</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<CloudStorageInstance> instances = CloudStorageManager.getInstance().getCloudStorageInstances();
                Collections.sort(instances, new Comparator<CloudStorageInstance>(){
                    public int compare(CloudStorageInstance i1, CloudStorageInstance i2) {
                        return i1.getName().compareTo(i2.getName());
                    }
                });
                for(CloudStorageInstance instance : instances) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('cloud_storage_data_edit.jsp?id=<%=instance.getId()%>')"><%=instance.getId()%></a></td>
                    <td><%=instance.getName()%></td>
                    <td><%=instance.getService().getName()%></td>
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
