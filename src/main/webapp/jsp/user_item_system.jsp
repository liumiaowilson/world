<%@ page import="org.wilson.world.useritem.*" %>
<%
String page_title = "System User Item";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">System User Item</h3>
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
                List<UserItem> items = UserItemDataManager.getInstance().getUserItems();
                List<UserItem> systemItems = new ArrayList<UserItem>();
                for(UserItem item : items) {
                    if(item instanceof SystemUserItem) {
                        systemItems.add(item);
                    }
                }
                Collections.sort(systemItems, new Comparator<UserItem>(){
                    public int compare(UserItem i1, UserItem i2) {
                        return i1.getName().compareTo(i2.getName());
                    }
                });
                for(UserItem item : systemItems) {
                %>
                <tr>
                    <td><%=item.getId()%></td>
                    <td><%=item.getName()%></td>
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
