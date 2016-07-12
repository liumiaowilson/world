<%
String from_url = "extension_point_edit.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String name = request.getParameter("name");
ExtensionPoint ep = ExtManager.getInstance().getExtensionPoint(name);
if(ep == null) {
    ep = new ExtensionPoint();
}
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=ep.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="2" maxlength="200" placeholde="Enter detailed description" required disabled><%=ep.description%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="param_table">Parameters</label>
        <table id="param_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(String paramName : ep.paramNames) {
                    Class clazz = ep.params.get(paramName);
                %>
                <tr>
                    <td><%=paramName%></td>
                    <td><%=clazz.getCanonicalName()%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
    <fieldset class="form-group">
        <label for="returnType">Return Type</label>
        <input type="text" class="form-control" id="returnType" maxlength="20" placeholder="Enter return type" value="<%=ep.returnType%>" disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn" disabled><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="generateAction()">Gen Action</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function generateAction() {
                window.location.href = "action_gen.jsp?name=<%=name%>";
            }
            $(document).ready(function(){
                $('#view_all_btn').click(function(){
                    window.location.href = "extension_point_list.jsp";
                });
            });
</script>
<%@ include file="footer.jsp" %>
