<%@ page import="org.wilson.world.entity.*" %>
<%@ include file="header.jsp" %>
<%
String type = request.getParameter("type");
EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
if(def == null) {
    response.sendRedirect("../index.jsp");
    return;
}

String page_title = type + " New";
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <%
    for(EntityProperty property : def.properties.values()) {
        if("_id".equals(property.name) || "name".equals(property.name)) {
            continue;
        }
        if("text".equals(property.field)) {
        %>
        <fieldset class="form-group">
            <label for="<%=property.name%>"><%=property.label%></label>
            <input type="text" class="form-control" id="<%=property.name%>" placeholder="Enter <%=property.name%>" required>
        </fieldset>
        <%
        }
        else if("textarea".equals(property.field)) {
        %>
        <fieldset class="form-group">
            <label for="<%=property.name%>"><%=property.label%></label>
            <textarea class="form-control" id="<%=property.name%>" rows="5" placeholder="Enter <%=property.name%>"></textarea>
        </fieldset>
        <%
        }
    }
    %>
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
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var obj = {};
                        <%
                        for(EntityProperty property : def.properties.values()) {
                            if("_id".equals(property.name)) {
                                continue;
                            }
                        %>
                        obj["<%=property.name%>"] = $("#<%=property.name%>").val();
                        <%
                        }
                        %>

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/entity/create"), { name: $('#name').val(), 'content': JSON.stringify(obj), 'type': "<%=type%>"}, function(data) {
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
