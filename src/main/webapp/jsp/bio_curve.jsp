<%
String page_title = "Bio Curves";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Bio Curves</h3>
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
            text: 'Bio Curves',
            x: -20 //center
        },
        xAxis: {
            categories: ['-10', '-9', '-8', '-7', '-6', '-5', '-4', '-3', '-2', '-1', 'Today', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
        },
        yAxis: {
            title: {
                text: 'Power(%)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '%'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [
            {
                name: 'Energy',
                data: [
                <%
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                int [] energy_powers = HealthManager.getInstance().getEnergyPowers(tz);
                for(int energy_power : energy_powers) {
                %>
                <%=energy_power%>,
                <%
                }
                %>
                ]
            },
            {
                name: 'Emotion',
                data: [
                <%
                int [] emotion_powers = HealthManager.getInstance().getEmotionPowers(tz);
                for(int emotion_power : emotion_powers) {
                %>
                <%=emotion_power%>,
                <%
                }
                %>
                ]
            },
            {
                name: 'Intelligence',
                data: [
                <%
                int [] intelligence_powers = HealthManager.getInstance().getIntelligencePowers(tz);
                for(int intelligence_power : intelligence_powers) {
                %>
                <%=intelligence_power%>,
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
