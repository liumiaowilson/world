<%
String page_title = "Novel Role Find Fragments";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="form-group">
    <label for="id">Role</label>
    <select class="combobox form-control" id="id">
        <option></option>
        <%
        List<NovelRole> roles = NovelRoleManager.getInstance().getNovelRoles();
        Collections.sort(roles, new Comparator<NovelRole>(){
            public int compare(NovelRole r1, NovelRole r2) {
                return r1.name.compareTo(r2.name);
            }
        });
        for(NovelRole role : roles) {
        %>
        <option value="<%=role.id%>"><%=role.name%></option>
        <%
        }
        %>
    </select>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary" id="search_btn" onclick="javascript:search()">Search</button>
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
            $('.combobox').combobox();

            function search() {
                $.get(getAPIURL("api/novel_role/find_fragments?id=" + $('#id').val()), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#item_table').show();
                        $('#item_table').DataTable().destroy();
                        $('#item_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('novel_fragment_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
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
