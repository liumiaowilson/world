<%
String page_title = "Inventory Item Edit";
%>
<%@ include file="header.jsp" %>
<%
InventoryItem inventory_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
inventory_item = InventoryItemManager.getInstance().getInventoryItem(id);
if(inventory_item == null) {
    response.sendRedirect("inventory_item_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=inventory_item.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=inventory_item.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Type</label>
        <input type="text" class="form-control" id="type" maxlength="20" placeholder="Enter type" value="<%=inventory_item.type%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="price">Price</label>
        <input type="number" class="form-control" id="price" maxlength="20" placeholder="Enter price" value="<%=inventory_item.price%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="amount">Amount</label>
        <input type="number" class="form-control" id="amount" maxlength="20" placeholder="Enter amount" value="<%=inventory_item.amount%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="status">Status</label>
        <input type="text" class="form-control" id="status" maxlength="20" placeholder="Enter status" value="<%=inventory_item.status%>" disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="dropInventoryItem()">Drop</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function dropInventoryItem() {
                bootbox.confirm("Are you sure to drop this inventory item?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/inventory_item/delete?id=" + id), function(data){
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
