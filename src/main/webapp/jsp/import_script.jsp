        </div>
    </div>
</div>
<%
interceptor.renderHTML(out);
%>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../js/ie10-viewport-bug-workaround.js"></script>
<script src="<%=cm.getConfig("js.jquery.url", "../js/jquery-2.2.4.min.js")%>"></script>
<script src="<%=cm.getConfig("js.bootstrap.url", "../js/bootstrap.min.js")%>"></script>
<script src="../js/validator.min.js"></script>
<script src="../js/spin.min.js"></script>
<script src="../js/ladda.min.js"></script>
<script src="../js/ladda.jquery.min.js"></script>
<script src="../js/bootbox.min.js"></script>
<script src="../js/bootstrap-combobox.js"></script>
<script src="../js/bootstrap-notify.min.js"></script>
<script src="../js/clipboard.min.js"></script>
<%
interceptor.renderScripts(out);
%>
<script>
        $.ajaxSetup({
            timeout: 30000,
            beforeSend: function(xhr) {
                $('#alert_success').hide();
                $('#alert_info').hide();
                $('#alert_warning').hide();
                $('#alert_danger').hide();
                $('#alert_ajax').text("Request is being processed. Please wait...");
                $('#alert_ajax').show();

                $('html, body').animate({ scrollTop: 0 });
            },
            error: function(xhr, status, error) {
                showDanger("Request failed: " + error);
            },
            complete: function(xhr, status) {
                $('#alert_ajax').hide();
                $('.ladda-button').ladda().ladda("stop");
                checkAlerts();
            }
        });

        function jumpToWebProxy() {
            window.location.href = "<%=ProxyManager.getInstance().getWebProxyUrl()%>";
        }

        function scrollToTop() {
            $('html, body').animate({ scrollTop: 0 });
        }

        function scrollToBottom() {
            $('html, body').animate({ scrollTop: $(document).height() });
        }

        function doQuiz(type, query, paper_url) {
            if(!query) {
                query = "";
            }
            if(!paper_url) {
                paper_url = "quiz_paper.jsp";
            }
            $.get(getAPIURL("api/" + type + "/do_quiz" + query), function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    var quiz_id = data.result.data.$;
                    jumpTo(paper_url + "?id=" + quiz_id);
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function trainWord() {
            doQuiz("word");
        }

        function doRhetoricQuiz() {
            doQuiz("rhetoric");
        }

        function doMetaModelQuiz() {
            doQuiz("meta_model");
        }

        function doZodiacSignQuiz(type) {
            doQuiz("zodiac_sign", "?type=" + type);
        }

        function doStrategyQuiz() {
            doQuiz("strategy");
        }

        function doSOMPQuiz() {
            doQuiz("somp");
        }

        function doPersonalityQuiz() {
            doQuiz("personality");
        }

        function doEmotionQuiz() {
            doQuiz("emotion", "", "emotion_quiz_paper.jsp");
        }

        function doColdReadQuiz() {
            doQuiz("cold_read");
        }

        function doStorySkillQuiz() {
            doQuiz("story_skill");
        }

        function doPushPullQuiz() {
            doQuiz("push_pull");
        }

        function doReactionQuiz() {
            doQuiz("reaction");
        }

        function doChatSkillQuiz() {
            doQuiz("chat_skill");
        }

        function doWritingSkillQuiz() {
            doQuiz("writing_skill");
        }

        function doDressQuiz() {
            doQuiz("dress");
        }

        function doTrickRuleQuiz() {
            doQuiz("trick_rule");
        }

        function doMicroExpressionQuiz() {
            doQuiz("micro_expression");
        }

        function doDesignPatternQuiz() {
            doQuiz("design_pattern");
        }

        function doBodyLanguageQuiz() {
            doQuiz("body_language");
        }

        function doMiltonModelQuiz() {
            doQuiz("milton_model");
        }

        function doOpenerQuiz() {
            doQuiz("opener");
        }

        function doFaceReadQuiz() {
            doQuiz("face_read");
        }

        function doZodiacSignComplexQuiz() {
            doQuiz("zodiac_sign");
        }

        function doInterviewQuiz() {
            doQuiz("interview");
        }

        function doHoopQuiz() {
            doQuiz("hoop");
        }

        function trainSign() {
            doQuiz("sign");
        }

        function doCodeRuleQuiz() {
            doQuiz("code_rule");
        }

        function doTechViewQuiz() {
            doQuiz("tech_view");
        }

        function startTaskIterator() {
            $.get(getAPIURL("api/task/start_iterator"), function(data){
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

        function stopTaskIterator() {
            $.get(getAPIURL("api/task/stop_iterator"), function(data){
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

        function startIdeaIterator() {
            $.get(getAPIURL("api/idea/start_iterator"), function(data){
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

        function stopIdeaIterator() {
            $.get(getAPIURL("api/idea/stop_iterator"), function(data){
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

        function openNotesDialog() {
            $('#notesDialog').modal();
        }

        function saveNotes() {
            var notes = $('#notesContent').val();
            $.post(getAPIURL("api/notes/set_notes"), { 'notes': notes }, function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    $('#notesDialog').modal('toggle');
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function resetNotes() {
            $('#notesContent').val("");
        }

        function openWordLookupDialog(word) {
            if(word) {
                $('#dialog_word').val(word);
            }
            $('#wordLookupDialog').modal();
        }

        function lookupWord() {
            var word = $('#dialog_word').val();
            $.post(getAPIURL("api/web/lookup"), { 'word': word }, function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    $('#dialog_explanation').empty();
                    var wordInfo = data.result.data;
                    $('#dialog_explanation').append(wordInfo.explanation);
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function openObjectDescribeDialog(name) {
            if(name) {
                $('#dialog_describe').val(name);
            }
            $('#describeDialog').modal();
        }

        function describeObject() {
            var name = $('#dialog_describe').val();
            $.post(getAPIURL("api/console/describe"), { 'name': name }, function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    $('#dialog_description').empty();
                    $('#dialog_description').append(msg);
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function resetAlerts() {
            $('#alert_success').hide();
            $('#alert_info').hide();
            $('#alert_warning').hide();
            $('#alert_danger').hide();
        }

        function showSuccess(msg, html) {
            if(html) {
                $('#alert_success').html(msg);
            }
            else {
                $('#alert_success').text(msg);
            }

            $('#alert_success').show();
        }

        function showInfo(msg, html) {
            if(html) {
                $('#alert_info').html(msg);
            }
            else {
                $('#alert_info').text(msg);
            }

            $('#alert_info').show();
        }

        function showWarning(msg, html) {
            if(html) {
                $('#alert_warning').html(msg);
            }
            else {
                $('#alert_warning').text(msg);
            }

            $('#alert_warning').show();
        }

        function showDanger(msg, html) {
            if(html) {
                $('#alert_danger').html(msg);
            }
            else {
                $('#alert_danger').text(msg);
            }

            $('#alert_danger').show();
        }

        function notifySuccess(msg) {
            notifyMessage(msg, 'success');
        }

        function notifyInfo(msg) {
            notifyMessage(msg, 'info');
        }

        function notifyWarning(msg) {
            notifyMessage(msg, 'warning');
        }

        function notifyDanger(msg) {
            notifyMessage(msg, 'danger');
        }

        function notifyReminder(msg) {
            bootbox.alert(msg);
        }

        function notifyMessage(msg, type) {
            $.notify({
                message: msg,
            },{
                type: type,
                allow_dismiss: true,
                placement: {
                    from: "top",
                    align: "right"
                },
                animate: {
                    enter: 'animated fadeInDown',
                    exit: 'animated fadeOutUp'
                },
                offset: 20,
                spacing: 10,
                z_index: 1031,
                delay: 5000,
                timer: 1000,
            });
        }

        function setCurrentContext(id) {
            var old_url = window.location.href;
            $.get(getAPIURL("api/context/set_current?id=" + id), function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    window.location.href = old_url;
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function randomQuote() {
            $.get(getAPIURL("api/quote/random"), function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    var content = data.result.data.content;
                    notifySuccess(content);
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function randomIdea() {
            $.get(getAPIURL("api/idea/random"), function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    var idea_id = data.result.data.id;
                    jumpTo("idea_edit.jsp?id=" + idea_id);
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function randomTask() {
            $.get(getAPIURL("api/task/random"), function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                    var idea_id = data.result.data.id;
                    jumpTo("task_edit.jsp?id=" + idea_id);
                }
                else {
                    showDanger(msg);
                }
            });
        }

        function checkAlerts() {
            var numOfAlerts = <%=MonitorManager.getInstance().getAlerts().size()%>;
            if(numOfAlerts != 0) {
                var alerts_str;
                if(numOfAlerts == 1) {
                    alerts_str = numOfAlerts + " alert is";
                }
                else {
                    alerts_str = numOfAlerts + " alerts are";
                }
                showWarning("<strong>" + alerts_str + "</strong> found. Please see <a href=\"javascript:jumpTo('alert.jsp')\">HERE</a>.", true);
            }
        }

        $(document).ready(function(){
            $('#request_end_time').val(new Date().getTime());

            <%
            List<String> msgs = NotifyManager.getInstance().take("notify_success");
            if(msgs != null) {
                for(String msg : msgs) {
            %>
                notifySuccess("<%=msg%>");
            <%
                }
            }
            %>
            <%
            msgs = NotifyManager.getInstance().take("notify_info");
            if(msgs != null) {
                for(String msg : msgs) {
            %>
                notifyInfo("<%=msg%>");
            <%
                }
            }
            %>
            <%
            msgs = NotifyManager.getInstance().take("notify_warning");
            if(msgs != null) {
                for(String msg : msgs) {
            %>
                notifyWarning("<%=msg%>");
            <%
                }
            }
            %>
            <%
            msgs = NotifyManager.getInstance().take("notify_danger");
            if(msgs != null) {
                for(String msg : msgs) {
            %>
                notifyDanger("<%=msg%>");
            <%
                }
            }
            %>

            <%
            msgs = NotifyManager.getInstance().take("notify_reminder");
            if(msgs != null) {
                StringBuilder reminderSb = new StringBuilder();
                for(String msg : msgs) {
                    reminderSb.append(msg);
                    reminderSb.append("<br/>");
                }
            %>
                notifyReminder("<%=reminderSb.toString()%>");
            <%
            }
            %>

            $('#url_back_btn').click(function(){
                jumpBack();
            });

            $('textarea.form-control').after('<div class="form-group"> <button type="button" class="btn btn-default btn-xs btn_textarea_copy"> <span class="glyphicon glyphicon-copy" aria-hidden="true"></span> </button> <button type="button" class="btn btn-default btn-xs btn_textarea_remove"> <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> </button> </div>');

            $('.btn_textarea_remove').click(function(){
                var textarea = $(this).parent().prev();
                if(textarea.attr("disabled")) {
                     return;
                }
                $('#tmp_value_holder').val(textarea.val());
                textarea.val('');
            });
            new Clipboard('.btn_textarea_copy', {
                text: function(trigger) {
                    var textarea = $(trigger).parent().prev();
                    return textarea.val();
                }
            });

            checkAlerts();
        });

    <%
        interceptor.renderClientScript(out);
    %>
</script>
