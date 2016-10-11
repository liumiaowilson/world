<%@ page import="org.wilson.world.stats.*" %>
<%
String page_title = "Event Record";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Event Record</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Max Per Hour</th>
                    <th>Max Per Day</th>
                </tr>
            </thead>
            <tbody>
                <%
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                List<EventRecord> records = StatsManager.getInstance().getEventRecords(tz);
                Collections.sort(records, new Comparator<EventRecord>(){
                    public int compare(EventRecord r1, EventRecord r2) {
                        return -(r1.count - r2.count);
                    }
                });
                for(EventRecord record : records) {
                %>
                <tr>
                    <td><%=record.name%></td>
                    <td><%=record.maxPerHour%></td>
                    <td><%=record.maxPerDay%></td>
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
