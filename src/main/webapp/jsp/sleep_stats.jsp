<%@ page import="org.wilson.world.sleep.*" %>
<%
String page_title = "Sleep Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Sleep Statistics</h3>
        </div>
        <div class="panel-body">
            <div id="container"/>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
$(function () {
    $('#container').highcharts({
        title: {
            text: 'Sleep Statistics',
            x: -20 //center
        },
        xAxis: {
            categories: [
            <%
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            List<SleepInfo> infos = SleepManager.getInstance().getSleepInfos(tz);
            for(SleepInfo info : infos) {
            %>
            "<%=info.timeStr%>",
            <%
            }
            %>
            ]
        },
        yAxis: {
            title: {
                text: 'Hours/Count'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: ''
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [
            {
                name: 'Sleep Time(Hours)',
                data: [
                <%
                for(SleepInfo info : infos) {
                %>
                <%=info.hours%>,
                <%
                }
                %>
                ]
            },
            {
                name: 'Average Quality',
                data: [
                <%
                for(SleepInfo info : infos) {
                %>
                <%=info.quality%>,
                <%
                }
                %>
                ]
            },
            {
                name: 'Sleep Dreams',
                data: [
                <%
                for(SleepInfo info : infos) {
                %>
                <%=info.dreams%>,
                <%
                }
                %>
                ]
            },
        ]
    });
});
</script>
<%@ include file="footer.jsp" %>
