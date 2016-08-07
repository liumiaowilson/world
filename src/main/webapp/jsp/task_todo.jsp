<%
String page_title = "Task Todo";
%>
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
                    <td><%=TaskManager.getInstance().getTaskDisplay(task)%></td>
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
        <h3 class="panel-title">Due Todos</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Due Time</th>
                    <th>Remaining Time</th>
                </tr>
            </thead>
            <tbody>
                <%
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                List<TaskInfo> dueTasks = TaskManager.getInstance().getDueTodos(sortedTasks, tz);
                for(TaskInfo dueTask : dueTasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=dueTask.task.id%>')"><%=dueTask.task.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(dueTask.task)%></td>
                    <td><%=dueTask.dueTime%></td>
                    <td><%=dueTask.remainTime%></td>
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
        <h3 class="panel-title">Waiting Tasks</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Waiting For</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Task> waitingTasks = TaskManager.getInstance().getWaitingTasks();
                for(Task waitingTask : waitingTasks) {
                    String waitFor = waitingTask.getValue("WaitFor");
                    if(waitFor == null) {
                        waitFor = "";
                    }
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=waitingTask.id%>')"><%=waitingTask.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(waitingTask)%></td>
                    <td><%=waitFor%></td>
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
