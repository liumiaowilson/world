<%
String page_title = "Task Queue";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Queue</h3>
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
                List<Task> tasks = TaskManager.getInstance().getTodos(sortedTasks, true);
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
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
