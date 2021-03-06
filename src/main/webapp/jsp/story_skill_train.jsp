<%
String page_title = "Story Skill Train";
%>
<%@ include file="header.jsp" %>
<%
StorySkill story_skill = StorySkillManager.getInstance().randomStorySkill();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Story Skill Train</h3>
    </div>
    <div class="panel-body">
        <%
        if(story_skill != null) {
        %>
        <div id="story_skill">
            <div class="well">
                <p><b><%=story_skill.name%></b></p>
                <p><i><%=story_skill.definition%></i></p>
                <p><i>Examples</i></p>
                <%
                for(String example : story_skill.examples) {
                %>
                <p><i><%=example%></i></p>
                <%
                }
                %>
            </div>
            <%
            Emotion emotion = EmotionManager.getInstance().randomEmotion();
            if(emotion != null) {
            %>
            <span class="label label-info"><%=emotion.name%></span><br/>
            <%
            }
            %>
            <hr/>
        </div>
        <fieldset class="form-group">
            <label for="examples">Make Examples</label>
            <textarea class="form-control" id="examples" rows="5" maxlength="400" placeholder="Enter detailed examples."></textarea>
            <small class="text-muted">Try your best to make more examples.</small>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
        <%
        }
        else {
        %>
        <div class="alert alert-danger" role="alert">
            No story skill could be found.
        </div>
        <%
        }
        %>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary disabled" id="done_btn">Done</button>
</div>
<%@ include file="import_script.jsp" %>
<script>
var debug = <%=ConfigManager.getInstance().isInDebugMode()%>;
var period = 1000;
if(debug) {
    period = 10;
}
function stop() {
    $('#examples').prop('disabled', true);
    $('#done_btn').removeClass('disabled');

    $('#done_btn').click(function(){
        $.post(getAPIURL("api/story_skill/train"), { 'examples': $('#examples').val() }, function(data){
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                showSuccess(msg);
                jumpBack();
            }
            else {
                showDanger(msg);
            }
        });
    });
}

$(document).ready(function(){
    var progress = setInterval(function() {
        var bar = $('.progress-bar');
        var value = parseInt(bar.attr("aria-valuenow"));
        if(value >= 101) {
            clearInterval(progress);
            stop();
        }
        else {
            value = value + 1;
            bar.attr("aria-valuenow", value);
            bar.css("width", value + "%");
        }
    }, period);
});
</script>
<%@ include file="footer.jsp" %>
