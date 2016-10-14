<%@ page import="org.wilson.world.profile.*" %>
<%
String page_title = "Profile View MBTI";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">MBTI Personality</h3>
    </div>
    <div class="panel-body">
        <div id="mbti">
        </div>
        <div class="well">
            <%
            MBTIProfile profile = ProfileManager.getInstance().getMBTIProfile();
            String name = profile.getType();
            MBTIType type = ProfileManager.getInstance().getMBTIType(name);
            %>
            <p><b><%=profile.getDisplay()%></b></p>
            <p><%=type.definition%></p>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
$(function () {
    $('#mbti').highcharts({
        chart: {
            polar: true,
            type: 'line'
        },
        title: {
            text: 'MBTI Personality',
            x: -80
        },
        pane: {
            size: '80%'
        },
        xAxis: {
            categories: ['Extraversion', 'Sensing', 'Thinking', 'Judging', 'Introversion', 'Intuition', 'Feeling', 'Perceiving'],
            tickmarkPlacement: 'on',
            lineWidth: 0
        },
        yAxis: {
            gridLineInterpolation: 'polygon',
            lineWidth: 0,
            min: 0
        },
        tooltip: {
            shared: true,
            pointFormat: '<span style="color:{series.color}">{series.name}: <b>{point.y:,.0f}</b><br/>'
        },
        legend: {
            align: 'right',
            verticalAlign: 'top',
            y: 70,
            layout: 'vertical'
        },
        series: [{
            name: 'MBTI Personality',
            data: [<%=profile.extraversion%>, <%=profile.sensing%>, <%=profile.thinking%>, <%=profile.judging%>, <%=profile.introversion%>, <%=profile.intuition%>, <%=profile.feeling%>, <%=profile.perceiving%>],
            pointPlacement: 'on'
        }]
    });
});
</script>
<%@ include file="footer.jsp" %>
