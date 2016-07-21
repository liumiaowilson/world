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
                    String starredStr = "";
                    if(StarManager.getInstance().isStarred(task)) {
                        starredStr = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>";
                    }
                    String contextStr = TaskManager.getInstance().getContextHint(task);
                    if(contextStr == null) {
                        contextStr = "";
                    }
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=task.id%>')"><%=task.id%></a></td>
                    <td><%=starredStr%><%=task.name%> <%=contextStr%></td>
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
                </tr>
            </thead>
            <tbody>
                <%
                List<TaskInfo> dueTasks = TaskManager.getInstance().getDueTodos(sortedTasks);
                for(TaskInfo dueTask : dueTasks) {
                    String starredStr = "";
                    if(StarManager.getInstance().isStarred(dueTask.task)) {
                        starredStr = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>";
                    }
                    String contextStr = TaskManager.getInstance().getContextHint(dueTask.task);
                    if(contextStr == null) {
                        contextStr = "";
                    }
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=dueTask.task.id%>')"><%=dueTask.task.id%></a></td>
                    <td><%=starredStr%><%=dueTask.task.name%> <%=contextStr%></td>
                    <td><%=dueTask.dueTime%></td>
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
