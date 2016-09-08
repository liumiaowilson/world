<%
String page_title = "Public";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Public Pages</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="javascript:jumpTo('../post.jsp')" class="list-group-item">Post</a>
            <a href="javascript:jumpTo('../check.jsp')" class="list-group-item">Check</a>
            <a href="javascript:jumpTo('../expense.jsp')" class="list-group-item">Expense</a>
            <a href="javascript:jumpTo('../notes.jsp')" class="list-group-item">Notes</a>
            <a href="javascript:jumpTo('../post_task.jsp')" class="list-group-item">Post Task</a>
            <a href="javascript:jumpTo('../behavior.jsp')" class="list-group-item">Behavior</a>
            <a href="javascript:jumpTo('../list_idea.jsp')" class="list-group-item">List Idea</a>
            <a href="javascript:jumpTo('../list_task.jsp')" class="list-group-item">List Task</a>
            <a href="javascript:jumpTo('../list_festival.jsp')" class="list-group-item">List Festival</a>
            <a href="javascript:jumpTo('../quest.jsp')" class="list-group-item">Quest</a>
            <a href="javascript:jumpTo('../view_porn.jsp')" class="list-group-item">Porn</a>
            <a href="javascript:jumpTo('../view_novel.jsp')" class="list-group-item">Novel</a>
            <a href="javascript:jumpTo('../finish_task.jsp')" class="list-group-item">Outdoor Tasks</a>
            <a href="javascript:jumpTo('../start_sleep.jsp')" class="list-group-item">Start Sleep</a>
            <a href="javascript:jumpTo('../end_sleep.jsp')" class="list-group-item">End Sleep</a>
        </div>
    </div>
</div>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="key">Key</label>
        <%
        String key = DataManager.getInstance().getValue("public.key");
        if(key == null) {
            key = "";
        }
        %>
        <input type="text" class="form-control" id="key" maxlength="20" placeholder="Enter key" value="<%=key%>" required autofocus>
        <small class="text-muted">The key is used to control access from outside.</small>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        $.post(getAPIURL("api/console/set_key"), { key: $('#key').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpCurrent();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
