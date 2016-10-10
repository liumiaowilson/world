<%@ page import="org.wilson.world.item.*" %>
<%
String page_title = "Link Edit";
%>
<%@ include file="header.jsp" %>
<%
Link link = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
link = LinkManager.getInstance().getLink(id);
if(link == null) {
    response.sendRedirect("link_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=link.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=link.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="label">Label</label>
        <input type="text" class="form-control" id="label" maxlength="20" placeholder="Enter label" value="<%=link.label%>" required>
    </fieldset>
    <div class="form-group">
        <label for="itemType">Item Type</label>
        <select class="combobox form-control" id="itemType" required>
            <option></option>
            <%
            List<String> itemTypes = LinkManager.getInstance().getSupportedItemTypes();
            Collections.sort(itemTypes);
            for(String itemType : itemTypes) {
                String selectedStr = itemType.equals(link.itemType) ? "selected" : "";
            %>
            <option value="<%=itemType%>" <%=selectedStr%>><%=itemType%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="itemId">Item</label>
        <select class="combobox form-control" id="itemId" required>
            <option></option>
            <%
            List<ItemInfo> itemInfos = ItemManager.getInstance().getItemInfos(link.itemType);
            Collections.sort(itemInfos, new Comparator<ItemInfo>(){
                public int compare(ItemInfo i1, ItemInfo i2) {
                    return i1.name.compareTo(i2.name);
                }
            });
            for(ItemInfo itemInfo : itemInfos) {
                String selectedStr = itemInfo.id == link.itemId ? "selected" : "";
            %>
            <option value="<%=itemInfo.id%>" <%=selectedStr%>><%=itemInfo.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="menuId">Menu</label>
        <select class="combobox form-control" id="menuId">
            <option></option>
            <%
            List<String> menuIds = MenuManager.getInstance().getSingleMenuIds();
            Collections.sort(menuIds);
            for(String menuId : menuIds) {
                String selectedStr = menuId.equals(link.menuId) ? "selected" : "";
            %>
            <option value="<%=menuId%>" <%=selectedStr%>><%=menuId%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="url">URL</label>
        <input type="text" class="form-control" id="url" maxlength="50" placeholder="Enter url" value="<%=link.menuId%>">
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteLink()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteLink() {
                bootbox.confirm("Are you sure to delete this link?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/link/delete?id=" + id), function(data){
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

                $('#itemType').change(function(){
                    $.get(getAPIURL("api/item/list?itemType=" + $(this).val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            $('#itemId').empty();
                            $('#itemId').append("<option></option>");
                            for(var i in data.result.list) {
                                var info = data.result.list[i];
                                $('#itemId').append("<option value='" + info.id + "'>" + info.name + "</option>");
                            }
                            $('#itemId').combobox('refresh');
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#menuId').change(function(){
                    $('#url').val("");
                });

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var label = $('#label').val();
                        if(!label) {
                            label = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/link/update"), { id: $('#id').val(), name: $('#name').val(), label: $('#label').val(), 'itemType': $('#itemType').val(), 'itemId': $('#itemId').val(), 'menuId': $('#menuId').val(), 'url': $('#url').val() }, function(data) {
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
