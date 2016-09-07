<%
String page_title = "Link New";
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
    <fieldset class="form-group">
        <label for="label">Label</label>
        <input type="text" class="form-control" id="label" maxlength="20" placeholder="Enter label" required>
    </fieldset>
    <div class="form-group">
        <label for="itemType">Item Type</label>
        <select class="combobox form-control" id="itemType" required>
            <option></option>
            <%
            List<String> itemTypes = LinkManager.getInstance().getSupportedItemTypes();
            Collections.sort(itemTypes);
            for(String itemType : itemTypes) {
            %>
            <option value="<%=itemType%>"><%=itemType%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="itemId">Item</label>
        <select class="combobox form-control" id="itemId" required>
            <option></option>
        </select>
    </div>
    <div class="form-group">
        <label for="menuId">Menu</label>
        <select class="combobox form-control" id="menuId" required>
            <option></option>
            <%
            List<String> menuIds = MenuManager.getInstance().getSingleMenuIds();
            Collections.sort(menuIds);
            for(String menuId : menuIds) {
            %>
            <option value="<%=menuId%>"><%=menuId%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

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

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var label = $('#label').val();
                        if(!label) {
                            label = $('#name').val();
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/link/create"), { name: $('#name').val(), 'label': label, 'itemType': $('#itemType').val(), 'itemId': $('#itemId').val(), 'menuId': $('#menuId').val() }, function(data) {
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
