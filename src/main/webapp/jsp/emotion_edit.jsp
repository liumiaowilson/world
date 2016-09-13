<%
String page_title = "Emotion Edit";
%>
<%@ include file="header.jsp" %>
<%
Emotion emotion = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
emotion = EmotionManager.getInstance().getEmotion(id);
if(emotion == null) {
    response.sendRedirect("emotion_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_slider.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=emotion.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=emotion.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=emotion.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="ecstacy">Ecstacy</label>
        <input id="ecstacy" data-slider-id='ecstacySlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.ecstacy%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="grief">Grief</label>
        <input id="grief" data-slider-id='griefSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.grief%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="admiration">Admiration</label>
        <input id="admiration" data-slider-id='admirationSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.admiration%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="loathing">Loathing</label>
        <input id="loathing" data-slider-id='loathingSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.loathing%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="rage">Rage</label>
        <input id="rage" data-slider-id='rageSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.rage%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="terror">Terror</label>
        <input id="terror" data-slider-id='terrorSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.terror%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="vigilance">Vigilance</label>
        <input id="vigilance" data-slider-id='vigilanceSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.vigilance%>"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="amazement">Amazement</label>
        <input id="amazement" data-slider-id='amazementSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="<%=emotion.amazement%>"/>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="viewEmotion()">View</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteEmotion()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_slider.jsp" %>
<script>
            var ecstacySlider = $('#ecstacy').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#ecstacySlider').css("width", "100%");
            var griefSlider = $('#grief').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#griefSlider').css("width", "100%");
            var admirationSlider = $('#admiration').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#admirationSlider').css("width", "100%");
            var loathingSlider = $('#loathing').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#loathingSlider').css("width", "100%");
            var rageSlider = $('#rage').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#rageSlider').css("width", "100%");
            var terrorSlider = $('#terror').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#terrorSlider').css("width", "100%");
            var vigilanceSlider = $('#vigilance').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#vigilanceSlider').css("width", "100%");
            var amazementSlider = $('#amazement').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).data('slider');
            $('#amazementSlider').css("width", "100%");

            function viewEmotion() {
                jumpTo('emotion_view.jsp?id=<%=id%>');
            }
            function deleteEmotion() {
                bootbox.confirm("Are you sure to delete this emotion?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/emotion/delete?id=" + id), function(data){
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
                    }
                });
            }
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/emotion/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'ecstacy': ecstacySlider.getValue(), 'grief': griefSlider.getValue(), 'admiration': admirationSlider.getValue(), 'loathing': loathingSlider.getValue(), 'rage': rageSlider.getValue(), 'terror': terrorSlider.getValue(), 'vigilance': vigilanceSlider.getValue(), 'amazement': amazementSlider.getValue() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpBack();
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
