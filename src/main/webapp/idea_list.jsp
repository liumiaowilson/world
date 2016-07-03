<%
String from_url = "idea_list.jsp";
%>
<%@ include file="header.jsp" %>
<table id="idea_table" class="display">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_scripts.jsp" %>
<script>
            $(document).ready(function(){
                $('#idea_table').hide();
                $.get("api/idea/list", function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#idea_table').show();
                        $('#idea_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href='#test'>" + oData.id + "</a>");
                                    }
                                },
                                { data: 'name' },
                            ],
                            buttons: [
                                {
                                    text: 'New',
                                    action: function (e, dt, node, config) {
                                        alert("Good");
                                    }
                                }
                            ]
                        });
                    }
                    else {
                        var msg = data.result.message;
                        $('#alert_danger').text(msg);
                        $('#alert_danger').show();
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
