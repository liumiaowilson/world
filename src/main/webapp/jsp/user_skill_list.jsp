<%
String page_title = "User Skill List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="user_skill_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Level</th>
            <th>Experience</th>
            <th>CD</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            function copy() {
                bootbox.confirm("This will cost 1 skill point. Continue?", function(result){
                    if(result) {
                        $.get(getAPIURL("api/user_skill/copy"), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpCurrent();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            $(document).ready(function(){
                $.get(getAPIURL("api/user_skill/list"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#user_skill_table').show();
                        $('#user_skill_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('user_skill_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
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
                                    data: 'level',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.level;
                                        $(nTd).html(content);
                                        nTd.title = oData.level;
                                    }
                                },
                                {
                                    data: 'exp',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = '<div class="progress"><div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="';
                                        content = content + oData.exp;
                                        content = content + '" aria-valuemin="0" aria-valuemax="100" style="width: ';
                                        content = content + oData.exp;
                                        content = content + '%">';
                                        content = content + oData.exp;
                                        content = content + '/100</div></div>';
                                        $(nTd).html(content);
                                        nTd.title = oData.exp + "/100";
                                    }
                                },
                                {
                                    data: 'cd',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.cd;
                                        $(nTd).html(content);
                                        nTd.title = oData.cd;
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'Copy',
                                    action: function (e, dt, node, config) {
                                        copy();
                                    }
                                }
                            ]
                        });
                        $('#user_skill_table').dataTable().$('tr').tooltip({
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
