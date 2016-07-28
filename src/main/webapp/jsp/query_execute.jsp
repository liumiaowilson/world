<%@ page import="org.wilson.world.query.*" %>
<%@ include file="header.jsp" %>
<%
QueryProcessor processor = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
processor = QueryManager.getInstance().getQueryProcessor(id);
if(processor == null) {
    response.sendRedirect("query_list.jsp");
    return;
}
String page_title = processor.getName() + " List";
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="query_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/query/execute?<%=request.getQueryString()%>"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#query_table').show();
                        $('#query_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        <%=processor.getIDCellExpression()%>
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        <%=processor.getNameCellExpression()%>
                                    }
                                },
                            ],
                            buttons: [
                                <%
                                List<QueryButtonConfig> queryButtonConfigs = processor.getQueryButtonConfigs();
                                for(QueryButtonConfig queryButtonConfig : queryButtonConfigs) {
                                %>
                                {
                                    text: '<%=queryButtonConfig.name%>',
                                    action: function (e, dt, node, queryButtonConfig) {
                                        jumpTo("<%=queryButtonConfig.url%>");
                                    }
                                },
                                <%
                                }
                                %>
                            ]
                        });
                        $('#query_table').dataTable().$('tr').tooltip({
                            "delay": 0,
                            "track": true,
                            "fade": 250
                        });
                    }
                    else {
                        var msg = data.result.message;
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
