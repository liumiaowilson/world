<%
String page_title = "Hoop List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="hoop_table" class="display" style="display:none">
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
                $.get(getAPIURL("api/hoop/list"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#hoop_table').show();
                        $('#hoop_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('hoop_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        $(nTd).html(content);
                                        nTd.title = oData.definition;
                                    }
                                },
                            ],
                            buttons: [
                            ]
                        });
                        $('#hoop_table').dataTable().$('tr').tooltip({
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