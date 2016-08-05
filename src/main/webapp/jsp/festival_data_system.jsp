<%@ page import="org.wilson.world.festival.*" %>
<%
String page_title = "System Festival Data";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">System Festival Data</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Definition</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Festival> festivals = FestivalDataManager.getInstance().getSystemFestivals();
                for(Festival festival : festivals) {
                    SystemFestival sf = (SystemFestival)festival;
                %>
                <tr>
                    <td><%=sf.getId()%></td>
                    <td><%=sf.getName()%></td>
                    <td><%=sf.getDescription()%></td>
                    <td><%=sf.getDefinition()%></td>
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
