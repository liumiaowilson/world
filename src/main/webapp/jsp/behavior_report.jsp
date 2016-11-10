<%@ page import="org.wilson.world.behavior.*" %>
<%
String page_title = "Behavior Report";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_multiselect.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Behavior Report</h3>
    </div>
    <div class="panel-body">
        <form id="form" role="form">
            <div class="form-group">
                <label for="behavior">Behavior</label>
                <select class="form-control" id="behavior" name="behavior" multiple="multiple">
                    <%
                    List<IBehaviorDef> behaviorDefs = BehaviorDefManager.getInstance().getIBehaviorDefs();
                    Collections.sort(behaviorDefs, new Comparator<IBehaviorDef>(){
                        public int compare(IBehaviorDef d1, IBehaviorDef d2) {
                            return d1.getName().compareTo(d2.getName());
                        }
                    });
                    for(IBehaviorDef behaviorDef : behaviorDefs) {
                    %>
                    <option value="<%=behaviorDef.getId()%>"><%=behaviorDef.getName()%></option>
                    <%
                    }
                    %>
                </select>
            </div>
            <div class="form-group">
                <label for="quest">Quest</label>
                <select class="form-control" id="quest" name="quest" multiple="multiple">
                    <%
                    List<QuestDef> questDefs = QuestDefManager.getInstance().getQuestDefs();
                    Collections.sort(questDefs, new Comparator<QuestDef>(){
                        public int compare(QuestDef d1, QuestDef d2) {
                            return d1.name.compareTo(d2.name);
                        }
                    });
                    for(QuestDef questDef : questDefs) {
                    %>
                    <option value="<%=questDef.id%>"><%=questDef.name%></option>
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
<%@ include file="import_script_multiselect.jsp" %>
<script>
$(document).ready(function(){
    $('#behavior').multiselect();
    $('#quest').multiselect();

    $('span.multiselect-native-select div.btn-group').css("width", "100%");

    $('#query_btn').click(function(){
        $.post(getAPIURL("api/behavior/report"), { 'behavior': $('#behavior').val().join(","), 'quest': $('#quest').val().join(",") }, function(data){
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                $('#trend').highcharts({
                    chart: {
                        zoomType: 'x'
                    },
                    title: {
                        text: 'Report'
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
                        name: 'Behavior Counts',
                        data: eval(msg)
                    }]
                });
            }
            else {
                showDanger(msg);
            }
        }, "json");
    });
});
</script>
<%@ include file="footer.jsp" %>
