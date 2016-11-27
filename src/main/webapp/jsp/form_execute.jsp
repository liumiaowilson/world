<%@ page import="org.wilson.world.form.*" %>
<%
String page_title = "Form";
%>
<%@ include file="header.jsp" %>
<%
Form form = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
form = FormManager.getInstance().getForm(id);
if(form == null) {
    response.sendRedirect("form_list.jsp");
    return;
}
Inputs inputs = form.getInputs();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_fileinput.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form" method="POST" action="<%=basePath%>/servlet/form?id=<%=form.getId()%>" <%=inputs.hasFileInput() ? "enctype=\"multipart/form-data\"" : ""%>>
    <%
    for(Input input : inputs.getAll()) {
        if(InputType.String == input.getType()) {
    %>
    <fieldset class="form-group">
        <label for="<%=input.getName()%>"><%=input.getLabel()%></label>
        <input type="text" class="form-control" id="<%=input.getName()%>" name="<%=input.getName()%>" placeholder="<%=input.getDescription()%>" <%=input.isMandatory() ? "required" : ""%>>
    </fieldset>
    <%
        }
        else if(InputType.Text == input.getType()) {
    %>
    <fieldset class="form-group">
        <label for="<%=input.getName()%>"><%=input.getLabel()%></label>
        <textarea class="form-control" id="<%=input.getName()%>" name="<%=input.getName()%>" rows="5" placeholder="<%=input.getDescription()%>" <%=input.isMandatory() ? "required" : ""%>></textarea>
    </fieldset>
    <%
        }
        else if(InputType.Number == input.getType()) {
    %>
    <fieldset class="form-group">
        <label for="<%=input.getName()%>"><%=input.getLabel()%></label>
        <input type="number" class="form-control" id="<%=input.getName()%>" name="<%=input.getName()%>" placeholder="<%=input.getDescription()%>" <%=input.isMandatory() ? "required" : ""%>>
    </fieldset>
    <%
        }
        else if(InputType.Boolean == input.getType()) {
    %>
    <div class="form-group">
        <label for="<%=input.getName()%>"><%=input.getLabel()%></label>
        <select class="combobox form-control" id="<%=input.getName()%>" name="<%=input.getName()%>">
            <option></option>
            <option value="true">true</option>
            <option value="false">false</option>
        </select>
    </div>
    <%
        }
        else if(InputType.Enum == input.getType()) {
    %>
    <div class="form-group">
        <label for="<%=input.getName()%>"><%=input.getLabel()%></label>
        <select class="combobox form-control" id="<%=input.getName()%>" name="<%=input.getName()%>">
            <option></option>
            <%
            String [] values = input.getValues();
            if(values != null) {
                for(String value : values) {
            %>
            <option value="<%=value%>"><%=value%></option>
            <%
                }
            }
            %>
        </select>
    </div>
    <%
        }
        else if(InputType.File == input.getType()) {
    %>
    <div class="form-group">
        <label class="control-label" for="<%=input.getName()%>"><%=input.getLabel()%></label>
        <input id="<%=input.getName()%>" name="<%=input.getName()%>" type="file" class="file">
    </div>
    <%
        }
    }
    %>
    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="execute_btn">Execute</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_fileinput.jsp" %>
<script>
            $('.combobox').combobox();
</script>
<%@ include file="footer.jsp" %>
