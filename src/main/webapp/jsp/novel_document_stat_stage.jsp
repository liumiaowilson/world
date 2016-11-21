<%@ page import="org.wilson.world.novel.*" %>
<%
String page_title = "Novel Fragments Per Stage";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Novel Fragments Per Stage</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Count</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<NovelFragmentPerStage> nfpsList = NovelDocumentManager.getInstance().getNovelFragmentPerStageStats();
                for(NovelFragmentPerStage nfps : nfpsList) {
                %>
                <tr>
                    <td><a href="novel_stage_edit.jsp?id=<%=nfps.id%>"><%=nfps.id%></a></td>
                    <td><%=nfps.name%></td>
                    <td><%=nfps.count%></td>
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
