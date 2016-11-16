<%@ page import="org.wilson.world.graph.*" %>
<%
String page_title = "Novel Stage Graph";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Novel Stage Graph</h3>
    </div>
    <div class="panel-body">
        <div id="container" style="width: 100%; height: 500px;"/>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_cytoscape.jsp" %>
<script>
    $(function() {
        var cy = cytoscape({
            container: $('#container'),

            elements: [
            <%
            Map<Integer, Node> nodes = NovelStageManager.getInstance().getNovelStageNodes();
            for(Node node : nodes.values()) {
            %>
                {
                    data: { id: '<%=node.id%>', name: '<%=node.name%>' }
                },
            <%
            }
            List<Edge> edges = NovelStageManager.getInstance().getNovelStageEdges(nodes);
            for(Edge edge : edges) {
            %>
                {
                    data: { id: '<%=edge.id%>', source: '<%=edge.source.id%>', target: '<%=edge.target.id%>' }
                },
            <%
            }
            %>
            ],

            style: [ // the stylesheet for the graph
                {
                    selector: 'node',
                    style: {
                        'content': 'data(name)',
                        'text-opacity': 0.5,
                        'text-valign': 'center',
                        'text-halign': 'right',
                        'background-color': '#11479e'
                    }
                },
                {
                    selector: 'edge',
                    style: {
                        'width': 4,
                        'target-arrow-shape': 'triangle',
                        'line-color': '#9dbaea',
                        'target-arrow-color': '#9dbaea',
                        'curve-style': 'bezier'
                    }
                }
            ],

            layout: {
                name: 'dagre',
            }
        });
        cy.on('tap', 'node', function(){
            jumpTo('novel_stage_edit.jsp?id=' + this.data('id'));
        });
    });
</script>
<%@ include file="footer.jsp" %>
