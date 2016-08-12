<%
String page_title = "Task Dep";
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
        <h3 class="panel-title">Blocking Tasks</h3>
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
                List<Task> blockingTasks = TaskManager.getInstance().getBlockingTasks(task);
                for(Task blockingTask : blockingTasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=blockingTask.id%>')"><%=blockingTask.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(blockingTask)%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Dependent Tasks</h3>
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
                List<Task> dependentTasks = TaskManager.getInstance().getDependentTasks(task);
                for(Task dependentTask : dependentTasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=dependentTask.id%>')"><%=dependentTask.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(dependentTask)%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Depended Tasks</h3>
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
                List<Task> dependedTasks = TaskManager.getInstance().getDependedTasks(task);
                for(Task dependedTask : dependedTasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=dependedTask.id%>')"><%=dependedTask.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(dependedTask)%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<div class="btn-group">
    <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
