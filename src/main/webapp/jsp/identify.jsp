<%@ page import="org.wilson.world.script.*" %>
<%
String page_title = "Identify";
%>
<%@ include file="header.jsp" %>
<%
String expression = request.getParameter("content");
if(expression == null) {
    expression = "";
}

ObjectInfo obj = null;

if(!"".equals(expression)) {
    try {
        obj = ScriptManager.getInstance().describe(expression);
    }
    catch(Exception e) {}
}

if(obj == null) {
    obj = new ObjectInfo();
    obj.fields = new org.wilson.world.script.FieldInfo[0];
    obj.methods = new MethodInfo[0];
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Identify</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form" method="POST" action="/jsp/identify.jsp">
            <fieldset class="form-group">
                <label for="expression">Expression</label>
                <div class="form-control" id="expression" required autofocus><%=FormatUtils.escapeHtml(expression)%></div>
            </fieldset>
            <div class="form-group">
                <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="identify_btn"><span class="ladda-label">Identify</span></button>
            </div>
            <input name="content" type="hidden">
        </form>
        <hr/>
        <table class="table table-striped table-bordered">
            <caption>Field</caption>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                </tr>
            </thead>
            <tbody>
                <%
                Arrays.sort(obj.fields, new Comparator<org.wilson.world.script.FieldInfo>(){
                    public int compare(org.wilson.world.script.FieldInfo f1, org.wilson.world.script.FieldInfo f2) {
                        return f1.name.compareTo(f2.name);
                    }
                });
                for(org.wilson.world.script.FieldInfo field : obj.fields) {
                %>
                <tr>
                    <td><%=field.name%></td>
                    <td><%=field.type%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <table class="table table-striped table-bordered">
            <caption>Method</caption>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Arg Types</th>
                    <th>Return Type</th>
                </tr>
            </thead>
            <tbody>
                <%
                Arrays.sort(obj.methods, new Comparator<MethodInfo>() {
                    public int compare(MethodInfo m1, MethodInfo m2) {
                        int ret = m1.name.compareTo(m2.name);
                        if(ret != 0) {
                            return ret;
                        }
                        else {
                            return m1.argTypes.length - m2.argTypes.length;
                        }
                    }
                });
                for(MethodInfo method : obj.methods) {
                    StringBuilder argTypeSB = new StringBuilder("(");
                    for(int i = 0; i < method.argTypes.length; i++) {
                        argTypeSB.append(method.argTypes[i]);
                        if(i != method.argTypes.length - 1) {
                            argTypeSB.append(", ");
                        }
                    }
                    argTypeSB.append(")");
                %>
                <tr>
                    <td><%=method.name%></td>
                    <td><%=argTypeSB.toString()%></td>
                    <td><%=method.returnType%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var editor = ace.edit("expression");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/javascript");
            $("#expression").css("width", "100%").css("height", "500");

            $("#identify_btn").click(function() {
                $("input[name='content']").val(editor.getValue());
                $("#form").submit();
            });
</script>
<%@ include file="footer.jsp" %>
