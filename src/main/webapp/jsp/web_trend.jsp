<%
String page_title = "Web Trend";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Web Trend</h3>
    </div>
    <div class="panel-body">
        <form id="form" role="form">
            <div class="form-group">
                <label for="name">Name</label>
                <select class="combobox form-control" id="name">
                    <option></option>
                    <%
                    List<String> names = WebManager.getInstance().getDataSetNames();
                    Collections.sort(names);
                    for(String name : names) {
                    %>
                    <option value="<%=name%>"><%=name%></option>
                    <%
                    }
                    %>
                </select>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary" id="query_btn">Query</button>
            </div>
            <div id="trend">
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
$(document).ready(function(){
    $('.combobox').combobox();

    $('#query_btn').click(function(){
        var name = $('#name').val();
        if(name) {
            $.get(getAPIURL("api/web/trend?name=" + name), function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    $('#trend').highcharts({
                        chart: {
                            zoomType: 'x'
                        },
                        title: {
                            text: 'Trend'
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
                            name: 'Data Size',
                            data: eval(msg)
                        }]
                    });
                }
                else {
                    showDanger(msg);
                }
            }, "json");
        }
    });
});
</script>
<%@ include file="footer.jsp" %>
