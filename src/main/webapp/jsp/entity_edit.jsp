<%@ page import="org.wilson.world.entity.*" %>
<%@ page import="org.wilson.world.pagelet.*" %>
<%@ include file="header.jsp" %>
<%
String type = request.getParameter("type");
EntityDefinition def = EntityManager.getInstance().getEntityDefinition(type);
if(def == null) {
    response.sendRedirect("../index.jsp");
    return;
}

String page_title = type + " Edit";

Entity entity = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
entity = EntityManager.getInstance().getEntity(type, id, true);
if(entity == null) {
    response.sendRedirect("entity_list.jsp?type=" + type);
    return;
}

List<FieldInfo> infos = new ArrayList<FieldInfo>();
for(EntityProperty property : def.properties.values()) {
    if("_id".equals(property.name) || "name".equals(property.name)) {
        continue;
    }
    infos.add(property.toFieldInfo());
}

FieldCreator creator = new FieldCreator(infos);

creator.executeServerCode(request, response);
%>
<%@ include file="import_css.jsp" %>
<%
creator.renderStyles(out);
%>
<style>
<%
creator.renderCSS(out);
%>
</style>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=entity.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=entity.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <%
    creator.renderHTML(out);
    %>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteEntity()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%
creator.renderScripts(out);
%>
<script>
            <%
            creator.renderClientScript(out);
            %>

            function deleteEntity() {
                bootbox.confirm("Are you sure to delete this <%=type%>?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/entity/delete?type=<%=type%>&id=" + id), function(data){
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
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var obj = {};
                        obj["_id"] = $("#id").val();
                        obj["name"] = $("#name").val();
                        fieldNames.forEach(function(name) {
                            obj[name] = C[name]();
                        });

                        l.ladda('start');
                        $.post(getAPIURL("api/entity/update"), { id: $('#id').val(), name: $('#name').val(), content: JSON.stringify(obj), type: "<%=type%>"}, function(data) {
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
