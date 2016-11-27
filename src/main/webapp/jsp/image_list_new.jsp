<%
String page_title = "Image List New";
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
    <div class="form-group">
        <label for="image">Image</label>
        <select class="combobox form-control" id="image">
            <option></option>
            <%
            List<String> imageRefNames = ImageManager.getInstance().getImageRefNames();
            Collections.sort(imageRefNames);
            for(String imageRefName : imageRefNames) {
            %>
            <option value="<%=imageRefName%>"><%=imageRefName%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="preview">Preview</label>
        <div id="preview">
            <img src=""/>
        </div>
    </div>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <div class="form-control" id="content"><%=ImageListManager.getInstance().getSampleContent()%></div>
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
            var editor = ace.edit("content");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/json");
            $("#content").css("width", "100%").css("height", "500");

            $('#image').change(function(){
                var image = $('#image').val();
                if(image) {
                    $.post(getAPIURL("api/image/get_url"), { 'name': $('#image').val(), 'width': 150, 'height': 150, 'adjust': true }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var url = data.result.data.$;
                            $('#preview img').attr("src", url);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                }
            });

            $(document).ready(function(){
                $('.combobox').combobox();

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
                        $.post(getAPIURL("api/image_list/create"), { name: $('#name').val(), 'content': editor.getValue() }, function(data) {
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
