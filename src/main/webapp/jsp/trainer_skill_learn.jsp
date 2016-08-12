<%
String page_title = "Trainer Skill List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="alert alert-success" role="alert">
    <%
    int skillPoints = CharManager.getInstance().getSkillPoints();
    %>
    You have <%=skillPoints%> skill point(s) now.
</div>
<table id="trainer_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Scope</th>
            <th>Target</th>
            <th>Cost</th>
            <th>Cooldown</th>
            <th>Skill Points</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            function rotate() {
                bootbox.confirm("This will cost 1 skill point. Continue?", function(result){
                    if(result) {
                        $.get(getAPIURL("api/trainer/rotate"), function(data){
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

            function learn(id, price) {
                bootbox.confirm("This will cost " + price + " skill point(s). Continue?", function(result){
                    if(result) {
                        $.get(getAPIURL("api/trainer/learn?id=" + id + "&price=" + price), function(data){
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
                $.get(getAPIURL("api/trainer/list_learn"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#trainer_table').show();
                        $('#trainer_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html(oData.id);
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
                                    data: 'type',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.type;
                                        $(nTd).html(content);
                                        nTd.title = oData.type;
                                    }
                                },
                                {
                                    data: 'scope',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.scope;
                                        $(nTd).html(content);
                                        nTd.title = oData.scope;
                                    }
                                },
                                {
                                    data: 'target',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.target;
                                        $(nTd).html(content);
                                        nTd.title = oData.target;
                                    }
                                },
                                {
                                    data: 'cost',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.cost;
                                        $(nTd).html(content);
                                        nTd.title = oData.cost;
                                    }
                                },
                                {
                                    data: 'cooldown',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.cooldown;
                                        $(nTd).html(content);
                                        nTd.title = oData.cooldown;
                                    }
                                },
                                {
                                    data: 'price',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.price;
                                        content = "<strong>" + content + "</strong>";
                                        $(nTd).html(content);
                                        nTd.title = oData.price;
                                    }
                                },
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = '<div class="btn-group"><button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Action <span class="caret"></span></button>';
                                        content = content + '<ul class="dropdown-menu">';
                                        content = content + '<li><a href="javascript:learn(';
                                        content = content + oData.id + "," + oData.price;
                                        content = content + ')">Learn</a></li>';
                                        content = content + '</ul></div>';
                                        $(nTd).html(content);
                                    }
                                },
                            ],
                            buttons: [
                                {
                                    text: 'Rotate',
                                    action: function (e, dt, node, config) {
                                        rotate();
                                    }
                                }
                            ]
                        });
                        $('#trainer_table').dataTable().$('tr').tooltip({
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
