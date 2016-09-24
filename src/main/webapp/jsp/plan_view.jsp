<%
String page_title = "Plan View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Planned Tasks</h3>
    </div>
    <div class="panel-body">
        <%
        List<Plan> plans = PlanManager.getInstance().getPlans();
        Collections.sort(plans, new Comparator<Plan>(){
            public int compare(Plan p1, Plan p2) {
                return p1.name.compareTo(p2.name);
            }
        });
        for(Plan plan : plans) {
            List<Task> tasks = PlanManager.getInstance().getTasksOfPlan(plan);
            Collections.sort(tasks, new Comparator<Task>(){
                public int compare(Task t1, Task t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
        %>
        <table class="table table-striped table-bordered">
            <caption><%=plan.name%></caption>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(Task task : tasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=task.id%>')"><%=task.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(task)%></td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="javascript:removeFromPlan(<%=plan.id%>, <%=task.id%>)">Remove</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <%
        }
        %>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Unplanned Tasks</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Task> tasks = PlanManager.getInstance().getUnplannedTasks();
                Collections.sort(tasks, new Comparator<Task>(){
                    public int compare(Task t1, Task t2) {
                        return t1.name.compareTo(t2.name);
                    }
                });
                for(Task task : tasks) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('task_edit.jsp?id=<%=task.id%>')"><%=task.id%></a></td>
                    <td><%=TaskManager.getInstance().getTaskDisplay(task)%></td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <%
                                for(Plan plan : plans) {
                                %>
                                <li><a href="javascript:addToPlan(<%=plan.id%>, <%=task.id%>)"><%=plan.name%></a></li>
                                <%
                                }
                                %>
                            </ul>
                        </div>
                    </td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            function addToPlan(plan_id, task_id) {
                $.post(getAPIURL("api/plan/add_to_plan"), { 'plan_id': plan_id, 'task_id': task_id }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpCurrent();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }

            function removeFromPlan(plan_id, task_id) {
                $.post(getAPIURL("api/plan/remove_from_plan"), { 'plan_id': plan_id, 'task_id': task_id }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpCurrent();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
</script>
<%@ include file="footer.jsp" %>
