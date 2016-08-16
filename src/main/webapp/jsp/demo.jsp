<%@ page import="java.lang.management.*" %>
<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Memory MXBean</h3>
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
                    <tr>
                        <td>Heap Memory Usage</td>
                        <td><%=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()%></td>
                    </tr>
                    <tr>
                        <td>Non-Heap Memory Usage</td>
                        <td><%=ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()%></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%
Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
while (iter.hasNext()) {
    MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
%>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Memory Pool MXBeans</h3>
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
                    <tr>
                        <td>Type</td>
                        <td><%=item.getType()%></td>
                    </tr>
                    <tr>
                        <td>Usage</td>
                        <td><%=item.getUsage()%></td>
                    </tr>
                    <tr>
                        <td>Peak Usage</td>
                        <td><%=item.getPeakUsage()%></td>
                    </tr>
                    <tr>
                        <td>Collection Usage</td>
                        <td><%=item.getCollectionUsage()%></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%
}
%>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
