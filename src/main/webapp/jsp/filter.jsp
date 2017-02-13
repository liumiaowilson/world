<%@ page import="org.wilson.world.filter.*" %>
<%
String page_title = "Active Filter";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Active Filter</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Pattern</th>
                    <th>Class</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<ActiveFilter> filters = FilterManager.getInstance().getActiveFilters();
                Collections.sort(filters, new Comparator<ActiveFilter>() {
                    public int compare(ActiveFilter s1, ActiveFilter s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(ActiveFilter filter : filters) {
                %>
                <tr>
                    <td><%=filter.getName()%></td>
                    <td><%=filter.getPattern()%></td>
                    <td><%=filter.getClass().getCanonicalName()%></td>
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
