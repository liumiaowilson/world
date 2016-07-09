<%
String from_url = "eval.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Eval Script</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <fieldset class="form-group">
                <label for="script">Command</label>
                <textarea class="form-control" id="script" rows="5" maxlength="200" placeholde="Enter script to evaluate"></textarea>
            </fieldset>
            <div class="form-group">
                <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="eval_btn"><span class="ladda-label">Evaluate</span></button>
            </div>
            <div class="well" id="result">
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#eval_btn').ladda();

                showWarning("Evaluate JavaScript here. This operation may cause damage to the host. Please execute with caution!");


                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post("api/console/eval", { script: $('#script').val() }, function(data) {
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
