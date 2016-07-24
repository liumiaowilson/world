<%@ page import="org.wilson.world.query.*" %>
<%
String page_title = "Query Processor";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Query Processor</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Source</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<QueryProcessor> processors = QueryManager.getInstance().getQueryProcessors();
                Collections.sort(processors, new Comparator<QueryProcessor>(){
                    public int compare(QueryProcessor p1, QueryProcessor p2) {
                        int id1 = p1.getID();
                        int id2 = p2.getID();
                        if(id1 < 0 && id2 > 0) {
                            return -1;
                        }
                        else if(id1 > 0 && id2 < 0) {
                            return 1;
                        }
                        else {
                            return p1.getName().compareTo(p2.getName());
                        }
                    }
                });
                for(QueryProcessor processor : processors) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('query_execute.jsp?id=<%=processor.getID()%>')"><%=processor.getID()%></a></td>
                    <td><%=processor.getName()%></td>
                    <%
                    if(processor instanceof DefaultQueryProcessor) {
                    %>
                    <td><a href="javascript:jumpTo('query_edit.jsp?id=<%=processor.getID()%>')"><%=processor.getName()%></a></td>
                    <%
                    }
                    else {
                    %>
                    <td><%=processor.getClass().getCanonicalName()%></td>
                    <%
                    }
                    %>
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
