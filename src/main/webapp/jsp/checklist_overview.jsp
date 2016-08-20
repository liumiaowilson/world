<%@ page import="org.wilson.world.checklist.*" %>
<%
String page_title = "Checklist Overview";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Checklist Overview</h3>
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
                List<Checklist> checklists = ChecklistManager.getInstance().getChecklists();
                Collections.sort(checklists, new Comparator<Checklist>(){
                    public int compare(Checklist c1, Checklist c2) {
                        return c1.name.compareTo(c2.name);
                    }
                });
                for(Checklist checklist : checklists) {
                %>
                <tr>
                    <td><a href="checklist_track.jsp?id=<%=checklist.id%>"><%=checklist.id%></a></td>
                    <td><%=checklist.name%></td>
                    <td><%=ChecklistManager.getInstance().getChecklistProgressDisplay(checklist)%></td>
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
