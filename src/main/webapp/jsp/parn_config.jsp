<%@ page import="org.wilson.world.parn.*" %>
<%
String page_title = "Parn Config";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Parn Config</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Parn Source</th>
                    <th>Number of Resources</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, List<ParnInfo>> parns = ParnManager.getInstance().getParns();
                if(parns == null) {
                    parns = new HashMap<String, List<ParnInfo>>();
                }
                List<String> keys = new ArrayList<String>(parns.keySet());
                Collections.sort(keys);
                for(String key : keys) {
                %>
                <tr>
                    <td><%=key%></td>
                    <%
                    int num = 0;
                    List<ParnInfo> infos = parns.get(key);
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
            <label for="source">Parn Source</label>
            <select class="combobox form-control" id="source">
                <option></option>
                <%
                for(String key : keys) {
                    String selectedStr = key.equals(ParnManager.getInstance().getSource()) ? "selected" : "";
                %>
                <option value="<%=key%>" <%=selectedStr%>><%=key%></option>
                <%
                }
                %>
            </select>
        </div>
        <div class="form-group">
            <button type="button" class="btn btn-primary" id="set_source_btn">Set Source</button>
            <button type="button" class="btn btn-default" id="clean_btn">Clean</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                $('#set_source_btn').click(function(){
                    $.get(getAPIURL("api/parn/set_source?source=" + $('#source').val()), function(data){
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

                $('#clean_btn').click(function(){
                    $.get(getAPIURL("api/parn/clean"), function(data){
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
