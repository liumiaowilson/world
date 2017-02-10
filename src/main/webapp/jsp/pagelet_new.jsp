<%
String page_title = "Pagelet New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="target">Target</label>
        <input type="text" class="form-control" id="target" maxlength="200" placeholder="Enter target">
    </fieldset>
    <fieldset class="form-group">
        <label for="serverCode">Server Code</label>
        <div class="form-control" id="serverCode"></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="css">CSS</label>
        <div class="form-control" id="css"></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="html">HTML</label>
        <div class="form-control" id="html"></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="clientCode">Client Code</label>
        <div class="form-control" id="clientCode"></div>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var serverCode = ace.edit("serverCode");
            serverCode.setTheme("ace/theme/monokai");
            serverCode.getSession().setMode("ace/mode/javascript");
            $("#serverCode").css("width", "100%").css("height", "400");

            var css = ace.edit("css");
            css.setTheme("ace/theme/monokai");
            css.getSession().setMode("ace/mode/css");
            $("#css").css("width", "100%").css("height", "400");

            var html = ace.edit("html");
            html.setTheme("ace/theme/monokai");
            html.getSession().setMode("ace/mode/html");
            $("#html").css("width", "100%").css("height", "400");

            var clientCode = ace.edit("clientCode");
            clientCode.setTheme("ace/theme/monokai");
            clientCode.getSession().setMode("ace/mode/javascript");
            $("#clientCode").css("width", "100%").css("height", "400");

            $(document).ready(function(){
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        var serverCodeValue = serverCode.getValue().replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                        var cssValue = css.getValue().replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                        var htmlValue = html.getValue().replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                        var clientCodeValue = clientCode.getValue().replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                        $.post(getAPIURL("api/pagelet/create"), { name: $('#name').val(), target: $('#target').val(), serverCode: serverCodeValue, css: cssValue, html: htmlValue, clientCode: clientCodeValue }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    jumpCurrent();
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                }
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#create_new').val("false");
                    $('#form').submit();
                });

                $('#save_new_btn').click(function(){
                    $('#create_new').val("true");
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
