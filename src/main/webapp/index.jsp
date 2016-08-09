<%@ page import="org.wilson.world.query.*" %>
<%
String page_title = "World";
%>
<%@ include file="jsp/header.jsp" %>
<%@ include file="jsp/import_css.jsp" %>
<%@ include file="jsp/navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Tasks</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('task_todo.jsp')" class="list-group-item">Todos</a>
            <a href="javascript:jumpTo('task_new.jsp')" class="list-group-item">New Task</a>
            <a href="javascript:jumpTo('task_list.jsp')" class="list-group-item">List Tasks</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Ideas</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('idea_new_batch.jsp')" class="list-group-item">New Idea</a>
            <a href="javascript:jumpTo('idea_list.jsp')" class="list-group-item">List Ideas</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Queries</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            List<QueryProcessor> processors = QueryManager.getInstance().getQueryProcessors();
            Collections.sort(processors, new Comparator<QueryProcessor>(){
                public int compare(QueryProcessor p1, QueryProcessor p2) {
                    return p1.getName().compareTo(p2.getName());
                }
            });
            for(QueryProcessor processor : processors) {
                if(!processor.isQuickLink()) {
                    continue;
                }
            %>
            <a href="javascript:jumpTo('query_execute.jsp?id=<%=processor.getID()%>')" class="list-group-item"><%=processor.getName()%></a>
            <%
            }
            %>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Habits</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('habit_trace_check.jsp')" class="list-group-item">Habit Check</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Journals</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('journal_new.jsp')" class="list-group-item">New Journal</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quests</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('quest_info.jsp')" class="list-group-item">Quest Info</a>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Scenarios</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('scenario_read.jsp')" class="list-group-item">Scenario Train</a>
        </div>
    </div>
</div>
<%@ include file="jsp/import_script.jsp" %>
<script>
</script>
<%@ include file="jsp/footer.jsp" %>
