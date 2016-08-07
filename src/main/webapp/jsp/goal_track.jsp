<%
String page_title = "Goal Track";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Goal Track</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Progress</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Goal> goals = GoalManager.getInstance().getAvailableGoals();
                Collections.sort(goals, new Comparator<Goal>(){
                    public int compare(Goal g1, Goal g2) {
                        return g1.name.compareTo(g2.name);
                    }
                });
                for(Goal goal : goals) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('goal_def_edit.jsp?id=<%=goal.defId%>')"><%=goal.defId%></a></td>
                    <td><%=goal.name%></td>
                    <td><%=GoalManager.getInstance().getDisplay(goal)%></td>
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
