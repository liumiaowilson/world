<%
String page_title = "Image List Edit";
%>
<%@ include file="header.jsp" %>
<%
ImageList image_list = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
image_list = ImageListManager.getInstance().getImageList(id);
if(image_list == null) {
    response.sendRedirect("image_list_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=image_list.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=image_list.name%>" required autofocus>
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
        <div class="form-control" id="content"><%=image_list.content%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="viewImageList()">View</a></li>
                <li><a href="javascript:void(0)" onclick="reorderImageList()">Reorder</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteImageList()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
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

            function deleteImageList() {
                bootbox.confirm("Are you sure to delete this image list?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/image_list/delete?id=" + id), function(data){
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
            function viewImageList() {
                jumpTo("image_list_view.jsp?id=" + $('#id').val());
            }
            function reorderImageList() {
                jumpTo("image_list_reorder.jsp?id=" + $('#id').val());
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
                        $.post(getAPIURL("api/image_list/update"), { id: $('#id').val(), name: $('#name').val(), content: editor.getValue() }, function(data) {
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
