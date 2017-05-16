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

String page_title = type + " Backup Edit";

Entity entity = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
EntityDelegator delegator = EntityManager.getInstance().getEntityDelegator(type);
Map<String, Entity> backupEntities = delegator.getBackupEntities();
for(Entity e : backupEntities.values()) {
    if(e.id == id) {
        entity = e;
        break;
    }
}
if(entity == null) {
    response.sendRedirect("entity_backup_list.jsp?type=" + type);
    return;
}

List<FieldInfo> infos = new ArrayList<FieldInfo>();
for(EntityProperty property : def.properties.values()) {
    if("_id".equals(property.name) || "name".equals(property.name)) {
        continue;
    }
    FieldInfo info = property.toFieldInfo();
    String value = entity.get(property.name);
    info.data.put("value", value);
    infos.add(info);
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
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=entity.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <%
    creator.renderHTML(out);
    %>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="save_btn" disabled>Save</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="restoreEntity()">Restore</a></li>
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

            function restoreEntity() {
                bootbox.confirm("Are you sure to restore this <%=type%>?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/entity/restore_backup?type=<%=type%>&id=" + id), function(data){
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
</script>
<%@ include file="footer.jsp" %>
