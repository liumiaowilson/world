<%@ page import="org.wilson.world.gym.*" %>
<%
String page_title = "Gym Item List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Gym Item List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Type</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<GymItem> items = GymManager.getInstance().getGymItems();
                Collections.sort(items, new Comparator<GymItem>(){
                    public int compare(GymItem i1, GymItem i2) {
                        return i1.name.compareTo(i2.name);
                    }
                });
                for(GymItem item : items) {
                %>
                <tr>
                    <td><%=item.id%></td>
                    <td><a href="<%=item.menu.link%>"><%=item.name%></a></td>
                    <td><%=item.type.name()%></td>
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
