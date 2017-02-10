<%@ page import="org.wilson.world.endpoint.*" %>
<%
String page_title = "End Point";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">End Point</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Method</th>
                    <th>URI</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<EndPointInfo> infos = EndPointManager.getInstance().getEndPointInfos();
                Collections.sort(infos, new Comparator<EndPointInfo>(){
                    public int compare(EndPointInfo i1, EndPointInfo i2) {
                        return i1.name.compareTo(i2.name);
                    }
                });
                for(EndPointInfo info : infos) {
                %>
                <tr>
                    <td><%=info.name%></td>
                    <td><%=info.httpMethod%></td>
                    <td><%=info.uri%></td>
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
