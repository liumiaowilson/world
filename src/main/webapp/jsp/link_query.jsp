<%
String page_title = "Link Query";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="form-group">
    <label for="itemType">Item Type</label>
    <select class="combobox form-control" id="itemType" required>
        <option></option>
        <%
        List<String> itemTypes = LinkManager.getInstance().getSupportedItemTypes();
        Collections.sort(itemTypes);
        for(String itemType : itemTypes) {
        %>
        <option value="<%=itemType%>"><%=itemType%></option>
        <%
        }
        %>
    </select>
</div>
<div class="form-group">
    <label for="itemId">Item</label>
    <select class="combobox form-control" id="itemId" required>
        <option></option>
    </select>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary" id="query_btn">Query</button>
</div>
<hr/>
<table id="link_table" class="display" style="display:none">
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
                $('.combobox').combobox();

                $('#itemType').change(function(){
                    $.get(getAPIURL("api/item/list?itemType=" + $(this).val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            $('#itemId').empty();
                            $('#itemId').append("<option></option>");
                            for(var i in data.result.list) {
                                var info = data.result.list[i];
                                $('#itemId').append("<option value='" + info.id + "'>" + info.name + "</option>");
                            }
                            $('#itemId').combobox('refresh');
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#query_btn').click(function(){
                    $.get(getAPIURL("api/link/query?type=" + $('#itemType').val() + "&id=" + $('#itemId').val()), function(data){
                        var status = data.result.status;
                        if("OK" == status) {
                            var array = data.result.list;
                            $('#link_table').show();
                            $('#link_table').DataTable().destroy();
                            $('#link_table').DataTable({
                                dom: 'Bfrtip',
                                data: array,
                                //disable initial sorting
                                aaSorting: [],
                                columns: [
                                    {
                                        data: 'id',
                                        fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                            $(nTd).html("<a href=\"javascript:jumpTo('link_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
                                        }
                                    },
                                    {
                                        data: 'name',
                                        fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                            var content = oData.name;
                                            $(nTd).html(content);
                                            nTd.title = oData.label;
                                        }
                                    },
                                ],
                                buttons: [
                                    {
                                        text: 'New',
                                        action: function (e, dt, node, config) {
                                            jumpTo("link_new.jsp");
                                        }
                                    }
                                ]
                            });
                            $('#link_table').dataTable().$('tr').tooltip({
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
            });
</script>
<%@ include file="footer.jsp" %>
