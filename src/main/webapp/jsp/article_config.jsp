<%@ page import="org.wilson.world.article.*" %>
<%
String page_title = "Article Config";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Article Config</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Article Source</th>
                    <th>Number of Resources</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, List<ArticleInfo>> articles = ArticleManager.getInstance().getArticleInfoMap();
                if(articles == null) {
                    articles = new HashMap<String, List<ArticleInfo>>();
                }
                List<String> keys = new ArrayList<String>(articles.keySet());
                Collections.sort(keys);
                for(String key : keys) {
                %>
                <tr>
                    <td><%=key%></td>
                    <%
                    int num = 0;
                    List<ArticleInfo> infos = articles.get(key);
                    if(infos != null) {
                        num = infos.size();
                    }
                    %>
                    <td><%=num%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <div class="form-group">
            <label for="source">Article Source</label>
            <select class="combobox form-control" id="source">
                <option></option>
                <%
                for(String key : keys) {
                    String selectedStr = key.equals(ArticleManager.getInstance().getCurrent()) ? "selected" : "";
                %>
                <option value="<%=key%>" <%=selectedStr%>><%=key%></option>
                <%
                }
                %>
            </select>
        </div>
        <div class="form-group">
            <button type="button" class="btn btn-primary" id="set_source_btn">Set Source</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                $('#set_source_btn').click(function(){
                    $.get(getAPIURL("api/article/set_source?source=" + $('#source').val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            jumpCurrent();
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
