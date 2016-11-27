<%@ page import="org.wilson.world.form.*" %>
<%
String page_title = "Form List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Form List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Form> forms = FormManager.getInstance().getForms();
                Collections.sort(forms, new Comparator<Form>(){
                    public int compare(Form f1, Form f2) {
                        return Integer.compare(f1.getId(), f2.getId());
                    }
                });
                for(Form form : forms) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('form_execute.jsp?id=<%=form.getId()%>')"><%=form.getId()%></a></td>
                    <td><%=form.getName()%></td>
                    <td><%=form.getDescription()%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
