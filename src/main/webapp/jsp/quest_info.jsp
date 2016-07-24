<%
String page_title = "Quest Info";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quest Info</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Base Pay</th>
                    <th>Count</th>
                    <th>Real Pay</th>
                    <th>Remain(%)</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<QuestInfo> infos = QuestManager.getInstance().getAllQuestInfos();
                Collections.sort(infos, new Comparator<QuestInfo>(){
                    public int compare(QuestInfo i1, QuestInfo i2) {
                        return i1.name.compareTo(i2.name);
                    }
                });
                for(QuestInfo info : infos) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('quest_def_edit.jsp?id=<%=info.def.id%>')"><%=info.name%></a></td>
                    <td><strong><%=info.def.pay%></strong></td>
                    <td><%=info.count%></td>
                    <td><strong><%=info.pay >= 0 ? info.pay : "N/A"%></strong></td>
                    <%
                    int pct = (int)(info.pay * 100.0 / info.def.pay);
                    if(pct > 50) {
                    %>
                    <td><span style="color:green"><%=pct%></span></td>
                    <%
                    }
                    else if(pct >= 0) {
                    %>
                    <td><span style="color:red"><%=pct%></span></td>
                    <%
                    }
                    else {
                    %>
                    <td><span style="color:blue">N/A</span></td>
                    <%
                    }
                    %>
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
