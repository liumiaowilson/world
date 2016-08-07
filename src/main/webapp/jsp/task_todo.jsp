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
                    String seedStr = TaskManager.getInstance().getSeedHint(task);
                    if(seedStr == null) {
                        seedStr = "";
                    }
                    String followerStr = TaskManager.getInstance().getFollowerHint(task);
                    if(followerStr == null) {
                        followerStr = "";
                    }
                    String typeStr = TaskManager.getInstance().getTypeHint(task);
                    if(typeStr == null) {
                        typeStr = "";
                    }
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=task.id%>')"><%=task.id%></a></td>
                    <td><%=typeStr%> <%=starredStr%><%=task.name%> <%=contextStr%> <%=seedStr%> <%=followerStr%></td>
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
                    String starredStr = "";
                    if(StarManager.getInstance().isStarred(dueTask.task)) {
                        starredStr = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>";
                    }
                    String contextStr = TaskManager.getInstance().getContextHint(dueTask.task);
                    if(contextStr == null) {
                        contextStr = "";
                    }
                    String seedStr = TaskManager.getInstance().getSeedHint(dueTask.task);
                    if(seedStr == null) {
                        seedStr = "";
                    }
                    String followerStr = TaskManager.getInstance().getFollowerHint(dueTask.task);
                    if(followerStr == null) {
                        followerStr = "";
                    }
                    String typeStr = TaskManager.getInstance().getTypeHint(dueTask.task);
                    if(typeStr == null) {
                        typeStr = "";
                    }
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=dueTask.task.id%>')"><%=dueTask.task.id%></a></td>
                    <td><%=typeStr%> <%=starredStr%><%=dueTask.task.name%> <%=contextStr%> <%=seedStr%> <%=followerStr%></td>
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
                    String starredStr = "";
                    if(StarManager.getInstance().isStarred(waitingTask)) {
                        starredStr = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>";
                    }
                    String contextStr = TaskManager.getInstance().getContextHint(waitingTask);
                    if(contextStr == null) {
                        contextStr = "";
                    }
                    String seedStr = TaskManager.getInstance().getSeedHint(waitingTask);
                    if(seedStr == null) {
                        seedStr = "";
                    }
                    String followerStr = TaskManager.getInstance().getFollowerHint(waitingTask);
                    if(followerStr == null) {
                        followerStr = "";
                    }
                    String typeStr = TaskManager.getInstance().getTypeHint(waitingTask);
                    if(typeStr == null) {
                        typeStr = "";
                    }
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=waitingTask.id%>')"><%=waitingTask.id%></a></td>
                    <td><%=typeStr%> <%=starredStr%><%=waitingTask.name%> <%=contextStr%> <%=seedStr%> <%=followerStr%></td>
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
