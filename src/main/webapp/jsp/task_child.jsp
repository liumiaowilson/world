<%
String page_title = "Task Child";
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
        <h3 class="panel-title">Children Tasks</h3>
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
                List<Task> childTasks = TaskManager.getInstance().getChildrenTasks(task);
                for(Task childTask : childTasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=childTask.id%>')"><%=childTask.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(childTask)%></td>
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
        <h3 class="panel-title">Parent Task</h3>
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
                Task parentTask = TaskManager.getInstance().getParentTask(task);
                if(parentTask != null) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=parentTask.id%>')"><%=parentTask.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(parentTask)%></td>
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
