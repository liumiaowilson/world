<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Run Command</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <fieldset class="form-group">
                <label for="command">Command</label>
                <textarea class="form-control" id="command" rows="5" maxlength="200" placeholde="Enter command to run" required autofocus></textarea>
            </fieldset>
            <div class="form-group">
                <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="run_btn"><span class="ladda-label">Run</span></button>
            </div>
            <div class="well" id="result">
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#run_btn').ladda();

                showWarning("This operation may cause damage to the host. Please execute with caution!");


                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post("api/console/run", { cmd: $('#command').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                l.ladda('stop');
                                msg = msg.replace(/\n/g, "<br/>");
                                $('#result').html(msg);
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
