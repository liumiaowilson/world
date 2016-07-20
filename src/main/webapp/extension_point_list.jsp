<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="extension_point_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>Name</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get("api/extension_point/list", function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#extension_point_table').show();
                        $('#extension_point_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('extension_point_edit.jsp?name=" + oData.name + "')\">" + oData.name + "</a>");
                                    }
                                },
                                {
                                    data: 'description',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        if(true == oData.marked) {
                                            $(nTd).html("<span style=\"color:<%=ConfigManager.getInstance().getConfig("item.marked.color", "red")%>\">" + oData.description + "</span>");
                                        }
                                        else {
                                            $(nTd).html(oData.description);
                                        }
                                    }
                                },
                            ],
                            buttons: []
                        });
                        $('#extension_point_table tbody tr').each(function(index){
                            var obj = array[index];
                            this.setAttribute('title', obj.description);
                        });
                        $('#extension_point_table').dataTable().$('tr').tooltip({
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
