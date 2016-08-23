<%@ page import="java.lang.management.*" %>
<%
String page_title = "Memory Monitor";
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
                        <td><%=ConsoleManager.getInstance().getMemoryUsageDisplay(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage())%></td>
                    </tr>
                    <tr>
                        <td>Non-Heap Memory Usage</td>
                        <td><%=ConsoleManager.getInstance().getMemoryUsageDisplay(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage())%></td>
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
            <h3 class="panel-title">Memory Pool MXBean <%=item.getName()%></h3>
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
                        <td><%=ConsoleManager.getInstance().getMemoryUsageDisplay(item.getUsage())%></td>
                    </tr>
                    <tr>
                        <td>Peak Usage</td>
                        <td><%=ConsoleManager.getInstance().getMemoryUsageDisplay(item.getPeakUsage())%></td>
                    </tr>
                    <tr>
                        <td>Collection Usage</td>
                        <td><%=ConsoleManager.getInstance().getMemoryUsageDisplay(item.getCollectionUsage())%></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%
}
%>
<button type="button" class="btn btn-info" id="dump_heap_btn">Dump Heap</button>
<button type="button" class="btn btn-warning" id="object_graph_btn">Object Graph</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('#dump_heap_btn').click(function(){
                    window.location.href = getAPIURL("api/console/dump_heap");
                });

                $('#object_graph_btn').click(function(){
                    jumpTo("object_graph_list.jsp");
                });
            });
</script>
<%@ include file="footer.jsp" %>
