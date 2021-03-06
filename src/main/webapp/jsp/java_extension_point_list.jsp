<%
String page_title = "Java Extension Point List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="java_class_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Class Name</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/java_class/list_extension_point"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#java_class_table').show();
                        $('#java_class_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        $(nTd).html(content);
                                        nTd.title = oData.name;
                                    }
                                },
                                {
                                    data: 'description',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.description;
                                        $(nTd).html(content);
                                    }
                                },
                                {
                                    data: 'clazz',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.clazz;
                                        $(nTd).html(content);
                                    }
                                }
                            ],
                            buttons: [
                            ]
                        });
                        $('#java_class_table').dataTable().$('tr').tooltip({
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
