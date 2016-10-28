<%@ page import="org.wilson.world.code.*" %>
<%
String page_title = "Code Snippet Report";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Code Snippet Report</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Coverage(%)</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<CodeSnippetReport> reports = CodeSnippetManager.getInstance().getCodeSnippetReports();
                Collections.sort(reports, new Comparator<CodeSnippetReport>(){
                    public int compare(CodeSnippetReport r1, CodeSnippetReport r2) {
                        return r1.name.compareTo(r2.name);
                    }
                });
                for(CodeSnippetReport report : reports) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('code_snippet_add.jsp?selectedCodeLangId=<%=report.id%>')"><%=report.id%></a></td>
                    <td><%=report.name%></td>
                    <td><%=report.coverage%></td>
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
