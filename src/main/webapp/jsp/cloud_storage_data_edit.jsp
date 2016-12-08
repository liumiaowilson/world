<%@ page import="org.wilson.world.cloud.*" %>
<%
String page_title = "Cloud Storage Data Edit";
%>
<%@ include file="header.jsp" %>
<%
CloudStorageData cloud_storage_data = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
cloud_storage_data = CloudStorageDataManager.getInstance().getCloudStorageData(id);
if(cloud_storage_data == null) {
    response.sendRedirect("cloud_storage_data_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=cloud_storage_data.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=cloud_storage_data.name%>" required autofocus>
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
                String selectedStr = service.getName().equals(cloud_storage_data.service) ? "selected" : "";
            %>
            <option value="<%=service.getName()%>" <%=selectedStr%>><%=service.getName()%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <div class="form-control" id="content" required><%=cloud_storage_data.content%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteCloudStorageData()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
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

            function deleteCloudStorageData() {
                bootbox.confirm("Are you sure to delete this cloud storage data?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/cloud_storage_data/delete?id=" + id), function(data){
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
                $('.combobox').combobox();

                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post(getAPIURL("api/cloud_storage_data/update"), { id: $('#id').val(), name: $('#name').val(), content: content.getValue(), 'service':$('#service').val() }, function(data) {
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
