<%@ page import="org.wilson.world.profile.*" %>
<%
String page_title = "Profile View Big Five";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Big Five Personality</h3>
    </div>
    <div class="panel-body">
        <div id="bigfive">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
$(function () {
    $('#bigfive').highcharts({
        chart: {
            polar: true,
            type: 'line'
        },
        title: {
            text: 'Big Five Personality',
            x: -80
        },
        pane: {
            size: '80%'
        },
        xAxis: {
            categories: ['Extroversion', 'Agreeableness', 'Conscientiousness', 'Neuroticism', 'Openness To Experience'],
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
            name: 'Big Five Personality',
            <%
            BigFiveProfile bigfiveProfile = ProfileManager.getInstance().getBigFiveProfile();
            %>
            data: [<%=bigfiveProfile.extroversion%>, <%=bigfiveProfile.agreeableness%>, <%=bigfiveProfile.conscientiousness%>, <%=bigfiveProfile.neuroticism%>, <%=bigfiveProfile.opennessToExperience%> ],
            pointPlacement: 'on'
        }]
    });
});
</script>
<%@ include file="footer.jsp" %>
