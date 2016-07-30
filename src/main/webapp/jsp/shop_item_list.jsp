<%
String page_title = "Shop Item List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="shop_item_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Price</th>
            <th>Amount</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/shop/list"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#shop_item_table').show();
                        $('#shop_item_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.id;
                                        $(nTd).html(content);
                                        nTd.title = oData.id;
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        $(nTd).html(content);
                                        nTd.title = oData.name;
                                    }
                                },
                                {
                                    data: 'type',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.type;
                                        $(nTd).html(content);
                                        nTd.title = oData.type;
                                    }
                                },
                                {
                                    data: 'price',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.price;
                                        $(nTd).html(content);
                                        nTd.title = oData.price;
                                    }
                                },
                                {
                                    data: 'amount',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.amount;
                                        $(nTd).html(content);
                                        nTd.title = oData.amount;
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'Restock',
                                    action: function (e, dt, node, config) {
                                        $.get(getAPIURL("api/shop/restock"), function(data){
                                            var status = data.result.status;
                                            var msg = data.result.message;
                                            if("OK" == status) {
                                                showSuccess(msg);
                                                jumpCurrent();
                                            }
                                            else {
                                                showDanger(msg);
                                            }
                                        });
                                    }
                                },
                            ]
                        });
                        $('#shop_item_table').dataTable().$('tr').tooltip({
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
