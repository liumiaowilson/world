<%@ page import="org.wilson.world.cloud.*" %>
<%
String page_title = "Cloud Storage Data New";
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
        <label for="service">Service</label>
        <select class="combobox form-control" id="service" required>
            <option></option>
            <%
            List<CloudStorageService> services = CloudStorageManager.getInstance().getCloudStorageServices();
            Collections.sort(services, new Comparator<CloudStorageService>(){
                public int compare(CloudStorageService s1, CloudStorageService s2) {
                    return s1.getName().compareTo(s2.getName());
                }
            });
            for(CloudStorageService service : services) {
            %>
            <option value="<%=service.getName()%>"><%=service.getName()%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <div class="form-control" id="content" required></div>
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
            var content = ace.edit("content");
            content.setTheme("ace/theme/monokai");
            content.getSession().setMode("ace/mode/json");
            $("#content").css("width", "100%").css("height", "400");

            $('#service').change(function(){
                var service = $('#service').val();
                if(service) {
                    $.post(getAPIURL("api/cloud_storage/get_sample_config_data"), { 'name': service }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var code = data.result.data.$;
                            content.setValue(code, -1);
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
                        $.post(getAPIURL("api/cloud_storage_data/create"), { name: $('#name').val(), 'content': content.getValue(), 'service': $('#service').val() }, function(data) {
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
