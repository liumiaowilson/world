<%
String page_title = "Eval Template";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Eval Template</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <fieldset class="form-group">
                <label for="context">Context</label>
                <div class="form-control" id="context" required autofocus>//vars.put("key", "value");</div>
            </fieldset>
            <fieldset class="form-group">
                <label for="template">Template</label>
                <div class="form-control" id="template" required></div>
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
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var context = ace.edit("context");
            context.setTheme("ace/theme/monokai");
            context.getSession().setMode("ace/mode/javascript");
            $("#context").css("width", "100%").css("height", "300");

            var template = ace.edit("template");
            template.setTheme("ace/theme/monokai");
            template.getSession().setMode("ace/mode/velocity");
            $("#template").css("width", "100%").css("height", "500");

            $(document).ready(function(){
                var l = $('#eval_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post(getAPIURL("api/template/eval"), { 'context': context.getValue(), 'template': template.getValue() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                l.ladda('stop');
                                msg = msg.replace(/\n/g, "<br/>");
                                $('#result').html(msg);
                                scrollToBottom();
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
