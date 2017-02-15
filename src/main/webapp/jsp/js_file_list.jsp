<%
String page_title = "Js File List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="js_file_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/js_file/list"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#js_file_table').show();
                        $('#js_file_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('js_file_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
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
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<button type=\"button\" class=\"btn btn-default\" onclick=\"javascript:validateJsFile(" + oData.id + ")\">Validate</button>");
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'New',
                                    action: function (e, dt, node, config) {
                                        jumpTo("js_file_new.jsp");
                                    }
                                }
                            ]
                        });
                        $('#js_file_table').dataTable().$('tr').tooltip({
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
            function validateJsFile(id) {
                $.get(getAPIURL("api/js_file/validate?id=" + id), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        msg = msg.replace(/\n/g, "<br/>");
                        showSuccess(msg, true);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
</script>
<%@ include file="footer.jsp" %>
