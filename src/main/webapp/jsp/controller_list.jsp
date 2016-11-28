<%@ page import="org.wilson.world.controller.*" %>
<%
String page_title = "Controller List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Controller List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>URI</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Controller> controllers = ControllerManager.getInstance().getControllers();
                Collections.sort(controllers, new Comparator<Controller>(){
                    public int compare(Controller c1, Controller c2) {
                        return c1.getName().compareTo(c2.getName());
                    }
                });
                for(Controller controller : controllers) {
                %>
                <tr>
                    <td><%=controller.getName()%></td>
                    <td><%=controller.getUri()%></td>
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
