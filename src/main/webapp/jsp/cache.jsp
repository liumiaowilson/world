<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Cache</h3>
    </div>
    <div class="panel-body">
        <%
        List<String> names = CacheManager.getInstance().getCacheNames();
        for(String name : names) {
        %>
        <div class="checkbox">
            <label><input type="checkbox" value="<%=name%>"><%=name%></label>
        </div>
        <%
        }
        %>
        <button type="button" class="btn btn-danger ladda-button" id="reload_btn"><span class="ladda-label">Reload</span></button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#reload_btn').ladda();

                $('#reload_btn').click(function(){
                    var names = [];
                    $('input[type=checkbox]').each(function () {
                        if (this.checked) {
                            names.push(this.value);
                        }
                    });
                    if(names.length > 0) {
                        bootbox.confirm("Are you sure to reload the caches?", function(result){
                            if(result) {
                                l.ladda('start');
                                $.get(getAPIURL("api/item/reload_cache?names=" + names), function(data){
                                    var status = data.result.status;
                                    var msg = data.result.message;
                                    if("OK" == status) {
                                        showSuccess(msg);
                                        l.ladda('stop');
                                    }
                                    else {
                                        showDanger(msg);
                                        l.ladda('stop');
                                    }
                                });
                            }
                        });
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
