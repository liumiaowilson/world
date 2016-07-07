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
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href='idea_edit.jsp?id=" + oData.id + "'>" + oData.id + "</a>");
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        if(true == oData.marked) {
                                            $(nTd).html("<span style=\"color:<%=ConfigManager.getInstance().getConfig("item.marked.color", "red")%>\">" + oData.name + "</span>");
                                        }
                                        else {
                                            $(nTd).html(oData.name);
                                        }
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'New',
                                    action: function (e, dt, node, config) {
                                        window.location.href = "idea_new.jsp";
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
