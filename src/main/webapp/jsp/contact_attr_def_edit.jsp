<%
String page_title = "Contact Attr Def Edit";
%>
<%
ContactAttrDef contact_attr_def = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
contact_attr_def = ContactAttrDefManager.getInstance().getContactAttrDef(id);
if(contact_attr_def == null) {
    response.sendRedirect("contact_attr_def_list.jsp");
    return;
}
String disabledStr = "";
if(contact_attr_def.isSystem) {
    disabledStr = "disabled";
}
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=contact_attr_def.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=contact_attr_def.name%>" required autofocus <%=disabledStr%>>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Type</label>
        <select class="combobox form-control" id="type" <%=disabledStr%>>
            <option></option>
            <%
            List<String> types = ContactAttrDefManager.getInstance().getSupportedTypes();
            Collections.sort(types);
            for(String type : types) {
                boolean selected = (type.equals(contact_attr_def.type));
                String selectedStr = (selected ? "selected" : "");
            %>
            <option value="<%=type%>" <%=selectedStr%>><%=type%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required <%=disabledStr%>><%=contact_attr_def.description%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn" <%=disabledStr%>><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li class="<%=disabledStr%>"><a href="javascript:void(0)" onclick="<%=!disabledStr.equals("") ? "" : "deleteContactAttrDef()"%>">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteContactAttrDef() {
                bootbox.confirm("Are you sure to delete this contact attr def?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/contact_attr_def/delete?id=" + id), function(data){
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
                        var type = $('#type').val();
                        if(!type) {
                            type = "String";
                        }

                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/contact_attr_def/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), type: $('#type').val()}, function(data) {
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
