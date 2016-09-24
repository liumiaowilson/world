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
                    <th>Difficulty</th>
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
                Attacker user = CharManager.getInstance().getAttacker();
                for(Attacker npc : npcs) {
                %>
                <tr>
                    <td><%=npc.getId()%></td>
                    <td><%=npc.getDisplayName()%></td>
                    <%
                    int numOfAdv = Attacker.compare(user, npc);
                    StringBuffer output = new StringBuffer();
                    for(int i = 0; i < 10 - numOfAdv; i++) {
                        output.append("<span class='glyphicon glyphicon-star' aria-hidden='true'></span>");
                    }
                    for(int i = 0; i < numOfAdv; i++) {
                        output.append("<span class='glyphicon glyphicon-star-empty' aria-hidden='true'></span>");
                    }
                    %>
                    <td><%=output.toString()%></td>
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="javascript:jumpTo('game.jsp?id=<%=npc.getId()%>&type=try')">Try</a></li>
                                <li><a href="javascript:jumpTo('npc_rate.jsp?id=<%=npc.getId()%>')">Rate</a></li>
                                <li role="separator" class="divider"></li>
                                <li><a href="javascript:jumpTo('game.jsp?id=<%=npc.getId()%>')">Attack</a></li>
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
