<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
boolean isInMemoryMode = ConfigManager.getInstance().isInMemoryMode();
String disabled = (isInMemoryMode ? "disabled" : "");
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Execute SQL</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <fieldset class="form-group">
                <label for="sql">Command</label>
                <textarea class="form-control" id="sql" rows="5" maxlength="200" placeholde="Enter SQL to execute" required autofocus></textarea>
            </fieldset>
            <div class="form-group">
                <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="execute_btn" <%=disabled%>><span class="ladda-label">Execute</span></button>
            </div>
            <div class="well" id="result">
            </div>
            <table class="table table-striped table-bordered" id="table" style="display:none">
            </table>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#execute_btn').ladda();

                showWarning("This operation may cause damage to the data. Please execute with caution!");


                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post("api/console/execute", { sql: $('#sql').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                l.ladda('stop');
                                var d = data.result.data;
                                if(d.isQuery) {
                                    var content = "";
                                    if(d.heading.items) {
                                        content = content + "<thead><tr>";
                                        for(var i in d.heading.items) {
                                            content = content + "<td>" + d.heading.items[i] + "</td>";
                                        }
                                        content = content + "</tr></thead>";
                                    }
                                    if(d.rows) {
                                        content = content + "<tbody>";
                                        for(var i in d.rows) {
                                            var row = d.rows[i];
                                            content = content + "<tr>";
                                            for(var j in row.items) {
                                                content = content + "<td>" + row.items[j] + "</td>";
                                            }
                                            content = content + "</tr>";
                                        }
                                        content = content + "</tbody>";
                                    }
                                    $('#table').html(content);
                                    $('#result').hide();
                                    $('#table').show();
                                }
                                else {
                                    $('#result').text("Update count is " + d.updateCount);
                                    $('#result').show();
                                    $('#table').hide();
                                }
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
