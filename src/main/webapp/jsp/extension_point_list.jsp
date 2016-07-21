<%
String page_title = "Extension Point List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="extension_point_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>Name</th>
            <th>Description</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/extension_point/list"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#extension_point_table').show();
                        $('#extension_point_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('extension_point_edit.jsp?name=" + oData.name + "')\">" + oData.name + "</a>");
                                    }
                                },
                                {
                                    data: 'description',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.description;
                                        if(true == oData.marked) {
                                            content = "<span style=\"color:<%=ConfigManager.getInstance().getConfig("item.marked.color", "red")%>\">" + content + "</span>";
                                        }
                                        if(true == oData.starred) {
                                            content = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>" + content;
                                        }
                                        $(nTd).html(content);
                                        nTd.title = oData.description;
                                    }
                                },
                            ],
                            buttons: []
                        });
                        $('#extension_point_table').dataTable().$('tr').tooltip({
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
