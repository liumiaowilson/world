<%
String page_title = "Word Lookup";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Word Lookup</h3>
    </div>
    <div class="panel-body">
        <fieldset class="form-group">
            <label for="word">Word</label>
            <input type="text" class="form-control" id="word" maxlength="50" placeholder="Enter word" required autofocus>
        </fieldset>
        <div class="btn-group">
            <button type="button" class="btn btn-primary" id="lookup_btn">Look Up</button>
        </div>
        <hr/>
        <div id="explanation" class="well">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('#lookup_btn').click(function(){
                    $.post(getAPIURL("api/web/lookup"), { 'word': $('#word').val() }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            $('#explanation').empty();
                            var wordInfo = data.result.data;
                            $('#explanation').append("<span><strong>" + wordInfo.type + "</strong></span><br/>");
                            $('#explanation').append("<span>" + wordInfo.pronunciation + "</span><br/>");
                            for(var i in wordInfo.explanations) {
                                $('#explanation').append("<span><span class='glyphicon glyphicon-unchecked' aria-hidden='true'></span>" + wordInfo.explanations[i] + "</span><br/>");
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
