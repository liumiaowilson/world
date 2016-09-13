<%
String page_title = "Emotion View";
%>
<%@ include file="header.jsp" %>
<%
Emotion emotion = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
emotion = EmotionManager.getInstance().getEmotion(id);
if(emotion == null) {
    response.sendRedirect("emotion_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Emotion View</h3>
        </div>
        <div class="panel-body">
            <div id="container">
            </div>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
$(function () {
    $('#container').highcharts({
        chart: {
            polar: true,
            type: 'line'
        },
        title: {
            text: 'Emotion Structure',
            x: -80
        },
        pane: {
            size: '80%'
        },
        xAxis: {
            categories: ['Ecstacy', 'Admiration', 'Rage', 'Vigilance', 'Grief', 'Loathing', 'Terror', 'Amazement'],
            tickmarkPlacement: 'on',
            lineWidth: 0
        },
        yAxis: {
            gridLineInterpolation: 'polygon',
            lineWidth: 0,
            min: -10
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
            name: '<%=emotion.name%>',
            data: [<%=emotion.ecstacy%>,<%=emotion.admiration%>,<%=emotion.rage%>,<%=emotion.vigilance%>,<%=emotion.grief%>,<%=emotion.loathing%>,<%=emotion.terror%>,<%=emotion.amazement%>],
            pointPlacement: 'on'
        }]
    });
});
</script>
<%@ include file="footer.jsp" %>
