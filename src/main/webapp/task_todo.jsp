<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Todos</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Task> sortedTasks = TaskManager.getInstance().getSortedTasks();
                List<Task> tasks = TaskManager.getInstance().getTodos(sortedTasks);
                for(Task task : tasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=task.id%>')"><%=task.id%></a></td>
                    <td><%=task.name%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
