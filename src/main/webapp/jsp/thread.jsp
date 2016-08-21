<%@ page import="java.lang.management.*" %>
<%
String page_title = "Thread";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Overview</h3>
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
                ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
                %>
                <tr>
                    <td>Thread Count</td>
                    <td><%=mbean.getThreadCount()%></td>
                </tr>
                <tr>
                    <td>Peak Thread Count</td>
                    <td><%=mbean.getPeakThreadCount()%></td>
                </tr>
                <tr>
                    <td>Total Started Thread Count</td>
                    <td><%=mbean.getTotalStartedThreadCount()%></td>
                </tr>
                <tr>
                    <td>Daemon Thread Count</td>
                    <td><%=mbean.getDaemonThreadCount()%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Threads</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>State</th>
                    <th>Stack Trace</th>
                </tr>
            </thead>
            <tbody>
                <%
                long [] ids = mbean.getAllThreadIds();
                for(long id : ids) {
                    ThreadInfo info = mbean.getThreadInfo(id);
                %>
                <tr>
                    <td><%=id%></td>
                    <td><%=info.getThreadName()%></td>
                    <td><%=info.getThreadState()%></td>
                    <%
                    StringBuffer traceSb = new StringBuffer();
                    StackTraceElement [] elements = info.getStackTrace();
                    if(elements != null) {
                        for(StackTraceElement element : elements) {
                            traceSb.append(element.toString());
                            traceSb.append("<br/>");
                        }
                    }
                    %>
                    <td><%=traceSb.toString()%></td>
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
