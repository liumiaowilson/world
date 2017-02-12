<%@ page import="org.wilson.world.pagelet.*" %>
<%
String page_title = "Field Test";
%>
<%@ include file="header.jsp" %>
<%
String content = request.getParameter("content");
if(content == null) {
    content = "[\n    {\n        \"name\": \"test\",\n        \"label\": \"Test\",\n        \"type\": \"text\"\n    }\n]";
}
FieldCreator creator = new FieldCreator(content);

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
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Field Test</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form" method="POST" action="/jsp/field.jsp">
            <fieldset class="form-group">
                <label for="content">Content</label>
                <div class="form-control" id="content" required autofocus><%=content%></div>
            </fieldset>
            <div class="form-group">
                <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="view_btn"><span class="ladda-label">View</span></button>
            </div>
            <input name="content" type="hidden">
        </form>
        <hr/>
        <form id="result">
            <%
            creator.renderHTML(out);
            %>
        </form>
        <div class="form-group">
            <button type="button" class="btn btn-default" id="send_btn">Send</button>
        </div>
        <hr/>
        <table class="table table-striped table-bordered" id="send_table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<%
creator.renderScripts(out);
%>
<script>
            var content = ace.edit("content");
            content.setTheme("ace/theme/monokai");
            content.getSession().setMode("ace/mode/json");
            $("#content").css("width", "100%").css("height", "400");

            $("#view_btn").click(function() {
                $('input[name="content"]').val(content.getValue());
                $("#form").submit();
            });

            <%
            creator.renderClientScript(out);
            %>

            $("#send_btn").click(function() {
                $("#send_table tbody").empty();
                fieldNames.forEach(function(name) {
                    $("#send_table").append("<tr><td>" + name + "</td><td>" + C[name]() + "</td></tr>");
                });
            });

</script>
<%@ include file="footer.jsp" %>
