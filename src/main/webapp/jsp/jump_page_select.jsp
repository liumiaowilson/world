<%
String page_title = "Jump Page Select";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Jump Page Select</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <div class="form-group">
                <label for="page">Page</label>
                <select class="combobox form-control" id="page">
                    <option></option>
                    <%
                    List<String> menuIds = MenuManager.getInstance().getSingleMenuIds();
                    Collections.sort(menuIds);
                    for(String menuId : menuIds) {
                    %>
                    <option value="<%=menuId%>"><%=menuId%></option>
                    <%
                    }
                    %>
                </select>
            </div>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary" id="jump_btn">Jump</button>
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        $.get(getAPIURL("api/menu/get?id=" + $('#page').val()), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                var script = data.result.data.$;
                                eval(script);
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
