<%@ page import="org.wilson.world.task.*" %>
<%
String page_title = "Task Interactor Show";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Interactor Show</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Symbol</th>
                    <th>Source</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<TaskInteractor> interactors = TaskFollowerManager.getInstance().getTaskInteractors();
                Collections.sort(interactors, new Comparator<TaskInteractor>(){
                    public int compare(TaskInteractor i1, TaskInteractor i2) {
                        return i1.getSymbol().compareTo(i2.getSymbol());
                    }
                });
                for(TaskInteractor interactor : interactors) {
                %>
                <tr>
                    <td><%=interactor.getSymbol()%></td>
                    <%
                    if(interactor instanceof DefaultTaskInteractor) {
                        String name = ((DefaultTaskInteractor)interactor).getName();
                        int id = ((DefaultTaskInteractor)interactor).getID();
                    %>
                    <td><a href="javascript:jumpTo('task_follower_edit.jsp?id=<%=id%>')"><%=name%></a></td>
                    <%
                    }
                    else {
                    %>
                    <td><%=interactor.getClass().getCanonicalName()%></td>
                    <%
                    }
                    %>
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
