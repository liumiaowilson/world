<%
String page_title = "Request List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="request_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>Request URI</th>
            <th>Count</th>
            <th>Average Response Time(Client/Server)</th>
            <th>Min Response Time(Client/Server)</th>
            <th>Max Response Time(Client/Server)</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/console/listRequests"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#request_table').show();
                        $('#request_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'requestURI',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.requestURI;
                                        $(nTd).html(content);
                                    }
                                },
                                {
                                    data: 'count',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.count;
                                        $(nTd).html(content);
                                    }
                                },
                                {
                                    data: 'avg',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.avg;
                                        $(nTd).html(content);
                                    }
                                },
                                {
                                    data: 'min',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.min;
                                        $(nTd).html(content);
                                    }
                                },
                                {
                                    data: 'max',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.max;
                                        $(nTd).html(content);
                                    }
                                },
                            ],
                            buttons: [
                            ]
                        });
                        $('#request_table').dataTable().$('tr').tooltip({
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
