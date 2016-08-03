<%
String page_title = "Item Search";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="form-group">
    <label for="type">Item Type</label>
    <select class="combobox form-control" id="type">
        <option></option>
        <%
        List<String> types = SearchManager.getInstance().getSearchTypes();
        Collections.sort(types);
        for(String type : types) {
        %>
        <option value="<%=type%>"><%=type%></option>
        <%
        }
        %>
    </select>
</div>
<div class="form-group">
    <label for="text">Text</label>
    <input type="text" class="form-control" id="text" maxlength="20" placeholder="Enter text" required autofocus>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary" id="search_btn" onclick="javascript:search()">Search</button>
    <button type="button" class="btn btn-default" id="reset_btn" onclick="javascript:reset()">Reset</button>
</div>
<table id="item_table" class="display" style="display:none">
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
            function reset() {
                $('#type').val('');
                $('#text').val('');
            }

            function search() {
                var type = $('#type').val();
                var text = $('#text').val();
                $.get(getAPIURL("api/item/search?type=" + type + "&text=" + text), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#item_table').show();
                        $('#item_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('" + oData.link + "')\">" + oData.id + "</a>");
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
                            ],
                            buttons: [
                            ]
                        });
                        $('#item_table').dataTable().$('tr').tooltip({
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
            }
</script>
<%@ include file="footer.jsp" %>
