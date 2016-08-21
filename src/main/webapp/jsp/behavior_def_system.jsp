<%@ page import="org.wilson.world.behavior.*" %>
<%
String page_title = "Behavior Def System";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Behavior Def System</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Karma</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<IBehaviorDef> behavior_defs = BehaviorDefManager.getInstance().getIBehaviorDefs();
                List<IBehaviorDef> system_defs = new ArrayList<IBehaviorDef>();
                for(IBehaviorDef behavior_def : behavior_defs) {
                    if(behavior_def.getId() < 0) {
                        system_defs.add(behavior_def);
                    }
                }
                Collections.sort(system_defs, new Comparator<IBehaviorDef>(){
                    public int compare(IBehaviorDef d1, IBehaviorDef d2) {
                        return d1.getName().compareTo(d2.getName());
                    }
                });
                for(IBehaviorDef system_def : system_defs) {
                %>
                <tr>
                    <td><%=system_def.getId()%></td>
                    <td><%=system_def.getName()%></td>
                    <td><%=system_def.getDescription()%></td>
                    <td><%=system_def.getKarma()%></td>
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
