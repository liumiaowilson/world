<%
String page_title = "Novel Role Validate";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="novel_role_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Message</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/novel_role/validate"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#novel_role_table').show();
                        $('#novel_role_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('novel_role_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        $(nTd).html(content);
                                        nTd.title = oData.display;
                                    }
                                },
                                {
                                    data: 'message',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.message;
                                        $(nTd).html(content);
                                    }
                                },
                            ],
                            buttons: [
                            ]
                        });
                        $('#novel_role_table').dataTable().$('tr').tooltip({
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
