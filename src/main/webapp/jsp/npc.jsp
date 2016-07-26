<%@ page import="org.wilson.world.tick.*" %>
<%
String page_title = "NPC";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">NPC</h3>
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
                List<Attacker> npcs = NPCManager.getInstance().getNPCs();
                Collections.sort(npcs, new Comparator<Attacker>(){
                    public int compare(Attacker a1, Attacker a2) {
                        return a1.getId() - a2.getId();
                    }
                });
                for(Attacker npc : npcs) {
                %>
                <tr>
                    <td><%=npc.getId()%></td>
                    <td><%=npc.getName()%></td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="javascript:jumpTo('game.jsp?id=<%=npc.getId()%>')">Try</a></li>
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
<%@ include file="footer.jsp" %>
