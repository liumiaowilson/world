<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Quest</title>
    </head>

    <body>
        <%=QuestManager.getInstance().getLastCreatedQuestDisplay()%>
        <form action="api/quest_def/achieve_public" method="post">
            Behavior: <select name="id">
                <option></option>
                <%
                List<QuestDef> quest_defs = QuestDefManager.getInstance().getQuestDefs();
                Collections.sort(quest_defs, new Comparator<QuestDef>(){
                    public int compare(QuestDef d1, QuestDef d2) {
                        return d1.name.compareTo(d2.name);
                    }
                });
                for(QuestDef quest_def : quest_defs) {
                %>
                <option value="<%=quest_def.id%>"><%=quest_def.name%></option>
                <%
                }
                %>
            </select><br/>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
