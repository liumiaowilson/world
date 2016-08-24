<%
String page_title = "Request Traffic";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Request Traffic</h3>
    </div>
    <div class="panel-body">
        <div id="trend">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/console/trend_traffic"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        $('#trend').highcharts({
                            chart: {
                                zoomType: 'x'
                            },
                            title: {
                                text: 'Request Traffic'
                            },
                            xAxis: {
                                type: 'datetime'
                            },
                            yAxis: {
                                title: {
                                    text: 'Count'
                                }
                            },
                            legend: {
                                enabled: false
                            },
                            plotOptions: {
                                area: {
                                    fillColor: {
                                        linearGradient: {
                                            x1: 0,
                                            y1: 0,
                                            x2: 0,
                                            y2: 1
                                        },
                                        stops: [
                                            [0, Highcharts.getOptions().colors[0]],
                                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                        ]
                                    },
                                    marker: {
                                        radius: 2
                                    },
                                    lineWidth: 1,
                                    states: {
                                        hover: {
                                            lineWidth: 1
                                        }
                                    },
                                    threshold: null
                                }
                            },

                            series: [{
                                type: 'area',
                                name: 'Request Traffic Count',
                                data: eval(msg)
                            }]
                        });
                    }
                    else {
                        showDanger(msg);
                    }
                }, "json");
            });
</script>
<%@ include file="footer.jsp" %>
