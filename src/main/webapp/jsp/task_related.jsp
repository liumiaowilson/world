<%
String page_title = "Task Related";
%>
<%
Task task = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
task = TaskManager.getInstance().getTask(id);
if(task == null) {
    response.sendRedirect("task_list.jsp");
    return;
}
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Related</h3>
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
                {
                    data: { id: '<%=task.id%>', name: '<%=task.name%>' }
                },
            <%
            List<Task> relatedTasks = TaskManager.getInstance().getRelatedTasks(task);
            for(Task relatedTask : relatedTasks) {
            %>
                {
                    data: { id: '<%=relatedTask.id%>', name: '<%=relatedTask.name%>' }
                },
            <%
            }
            for(Task relatedTask : relatedTasks) {
            %>
                {
                    data: { id: '<%=relatedTask.id%>_<%=task.id%>', source: '<%=relatedTask.id%>', target: '<%=task.id%>' }
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
                name: 'concentric',
                concentric: function( node ){
                    return node.degree();
                },
                levelWidth: function( nodes ){
                    return 2;
                }
            },
        });
        cy.on('tap', 'node', function(){
            jumpTo('task_edit.jsp?id=' + this.data('id'));
        });
    });
</script>
<%@ include file="footer.jsp" %>
