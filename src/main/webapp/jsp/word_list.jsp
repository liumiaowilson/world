<%
String page_title = "Word List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="word_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Familiarity</th>
            <th>Last Reviewed</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/word/list"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#word_table').show();
                        $('#word_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('word_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        $(nTd).html(content);
                                        nTd.title = oData.meaning;
                                    }
                                },
                                {
                                    data: 'step',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = "";
                                        for(var i = 1; i <= oData.step; i++) {
                                            content += "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>";
                                        }
                                        for(var i = oData.step + 1; i <= 8; i++) {
                                            content += "<span class='glyphicon glyphicon-star-empty' aria-hidden='true'></span>";
                                        }
                                        $(nTd).html(content);
                                    }
                                },
                                {
                                    data: 'lastReviewed',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.lastReviewed;
                                        $(nTd).html(content);
                                        nTd.title = oData.lastReviewed;
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'New',
                                    action: function (e, dt, node, config) {
                                        jumpTo("word_new.jsp");
                                    }
                                },
                                {
                                    text: 'Train',
                                    action: function (e, dt, node, config) {
                                        trainWord();
                                    }
                                }
                            ]
                        });
                        $('#word_table').dataTable().$('tr').tooltip({
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
