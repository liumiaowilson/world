<%@ page import="org.wilson.world.period.*" %>
<%
String page_title = "Period Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Period Statistics</h3>
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
                PeriodReport report = PeriodManager.getInstance().getPeriodReport();
                %>
                <tr>
                    <td>Average Cycle(Days)</td>
                    <td><%=report.cycleDays%></td>
                </tr>
                <tr>
                    <td>Average Duration(Days)</td>
                    <td><%=report.durationDays%></td>
                </tr>
            </tbody>
        </table>
        <div id="container">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
$(function () {
    $('#container').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: 'Period'
        },
        xAxis: {
            <%
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            List<PeriodItem> items = PeriodManager.getInstance().getPeriodStats(tz);
            %>
            categories: [
            <%
            for(PeriodItem item : items) {
            %>
            "<%=item.startTime%>",
            <%
            }
            %>
            ],
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Average Time(Days)'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f} days</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
            name: 'Partner',
            data: [
                    <%
                    for(PeriodItem item : items) {
                    %>
                    <%=item.days%>,
                    <%
                    }
                    %>
            ]
        }]
    });
});
</script>
<%@ include file="footer.jsp" %>
