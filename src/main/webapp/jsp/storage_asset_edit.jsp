<%@ page import="org.wilson.world.storage.*" %>
<%
String page_title = "Storage Asset Edit";
%>
<%@ include file="header.jsp" %>
<%
StorageAsset storage_asset = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
storage_asset = StorageManager.getInstance().getStorageAsset(id);
if(storage_asset == null) {
    response.sendRedirect("storage_asset_list.jsp");
    return;
}
Storage storage = StorageManager.getInstance().getStorage(storage_asset.storageId);
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=storage_asset.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Path</label>
        <input type="text" class="form-control" id="name" maxlength="200" placeholder="Enter name" value="<%=storage_asset.name%>" required disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="storage">Storage Name</label>
        <input type="text" class="form-control" id="storage" maxlength="200" placeholder="Enter storage" value="<%=storage.name%>" required disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteStorageAsset()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteStorageAsset() {
                bootbox.confirm("Are you sure to delete this storage asset?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/storage_asset/delete?id=" + id), function(data){
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
            });
</script>
<%@ include file="footer.jsp" %>
