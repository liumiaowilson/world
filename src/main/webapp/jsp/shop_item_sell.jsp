<%
String page_title = "Inventory Item List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="alert alert-success" role="alert">
    <%
    int coins = CharManager.getInstance().getCoins();
    %>
    You have <%=coins%> coin(s) now.
</div>
<table id="shop_item_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Price</th>
            <th>Inv Price</th>
            <th>Inv Amount</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            function sell(id, price) {
                bootbox.prompt({
                    title: "How much do you want to sell?",
                    value: "1",
                    callback: function(result) {
                        if(result) {
                            $.get(getAPIURL("api/shop/sell?id=" + id + "&amount=" + result + "&price=" + price), function(data){
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
                    }
                });
            }
            $(document).ready(function(){
                $.get(getAPIURL("api/shop/list_sell"), function(data){
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
                                        nTd.title = oData.description;
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
                                        content = "<strong>" + content + "</strong>";
                                        $(nTd).html(content);
                                        nTd.title = oData.price;
                                    }
                                },
                                {
                                    data: 'invPrice',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.invPrice;
                                        $(nTd).html(content);
                                        nTd.title = oData.invPrice;
                                    }
                                },
                                {
                                    data: 'amount',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.amount;
                                        content = "<strong>" + content + "</strong>";
                                        $(nTd).html(content);
                                        nTd.title = oData.amount;
                                    }
                                },
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = '<div class="btn-group"><button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action <span class="caret"></span></button>';
                                        content = content + '<ul class="dropdown-menu">';
                                        content = content + '<li><a href="javascript:sell(';
                                        content = content + oData.id;
                                        content = content + ',';
                                        content = content + oData.price;
                                        content = content + ')">Sell</a></li>';
                                        content = content + '</ul></div>';
                                        $(nTd).html(content);
                                    }
                                },
                            ],
                            buttons: [
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
