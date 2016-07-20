<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="action_table" class="display" style="display:none">
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
                $.get("api/action/list", function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#action_table').show();
                        $('#action_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('action_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
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
                                        jumpTo('action_new.jsp');
                                    }
                                }
                            ]
                        });
                        $('#action_table tbody tr').each(function(index){
                            var obj = array[index];
                            this.setAttribute('title', obj.name);
                        });
                        $('#action_table').dataTable().$('tr').tooltip({
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
