<%
String page_title = "Emotion New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_slider.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="50" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description"></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="ecstacy">Ecstacy</label>
        <input id="ecstacy" data-slider-id='ecstacySlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="grief">Grief</label>
        <input id="grief" data-slider-id='griefSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="admiration">Admiration</label>
        <input id="admiration" data-slider-id='admirationSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="loathing">Loathing</label>
        <input id="loathing" data-slider-id='loathingSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="rage">Rage</label>
        <input id="rage" data-slider-id='rageSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="terror">Terror</label>
        <input id="terror" data-slider-id='terrorSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="vigilance">Vigilance</label>
        <input id="vigilance" data-slider-id='vigilanceSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <fieldset class="form-group">
        <label for="amazement">Amazement</label>
        <input id="amazement" data-slider-id='amazementSlider' type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
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

            $(document).ready(function(){
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/emotion/create"), { name: $('#name').val(), 'description': description, 'ecstacy': ecstacySlider.getValue(), 'grief': griefSlider.getValue(), 'admiration': admirationSlider.getValue(), 'loathing': loathingSlider.getValue(), 'rage': rageSlider.getValue(), 'terror': terrorSlider.getValue(), 'vigilance': vigilanceSlider.getValue(), 'amazement': amazementSlider.getValue() }, function(data) {
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
