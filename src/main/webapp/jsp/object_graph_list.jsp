<%
String page_title = "Object Graph List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="object_graph_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>Name</th>
            <th>Num of Objects</th>
            <th>Num of References</th>
            <th>Num of Primitives</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/console/list_object_graph"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#object_graph_table').show();
                        $('#object_graph_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'name',
                                },
                                {
                                    data: 'objects',
                                },
                                {
                                    data: 'refs',
                                },
                                {
                                    data: 'primitives',
                                },
                            ],
                            buttons: [
                            ]
                        });
                        $('#object_graph_table').dataTable().$('tr').tooltip({
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
