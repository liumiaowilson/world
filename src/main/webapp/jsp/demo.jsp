<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <div id="container" style="width: 300px; height: 300px;"/>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_cytoscape.jsp" %>
<script>
    $(function() {
        var cy = cytoscape({
            container: $('#container'),

            elements: [ // list of graph elements to start with
                { // node a
                    data: { id: 'a' }
                },
                { // node b
                    data: { id: 'b' }
                },
                { // edge ab
                    data: { id: 'ab', source: 'a', target: 'b' }
                }
            ],

            style: [ // the stylesheet for the graph
                {
                    selector: 'node',
                    style: {
                        'content': 'data(id)',
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
    });
</script>
<%@ include file="footer.jsp" %>
