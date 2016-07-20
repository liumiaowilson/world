<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="idea_table" class="display" style="display:none">
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
                $.get("api/idea/list", function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#idea_table').show();
                        $('#idea_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('idea_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        if(true == oData.marked) {
                                            content = "<span style=\"color:<%=ConfigManager.getInstance().getConfig("item.marked.color", "red")%>\">" + content + "</span>";
                                        }
                                        if(true == oData.starred) {
                                            content = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>" + content;
                                        }
                                        $(nTd).html(content);
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'New',
                                    action: function (e, dt, node, config) {
                                        jumpTo("idea_new.jsp");
                                    }
                                },
                                {
                                    text: 'Batch',
                                    action: function (e, dt, node, config) {
                                        jumpTo("idea_new_batch.jsp");
                                    }
                                },
                                {
                                    text: 'Post',
                                    action: function (e, dt, node, config) {
                                        jumpTo("post_process.jsp");
                                    }
                                }
                            ]
                        });
                        $('#idea_table tbody tr').each(function(index){
                            var obj = array[index];
                            this.setAttribute('title', obj.content);
                        });
                        $('#idea_table').dataTable().$('tr').tooltip({
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
