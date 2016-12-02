<%
String page_title = "Activity Track";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Activity Track</h3>
    </div>
    <div class="panel-body">
        <form id="form" role="form">
            <div class="form-group">
                <label for="id">Activity</label>
                <select class="combobox form-control" id="id">
                    <option></option>
                    <%
                    List<Activity> activities = ActivityManager.getInstance().getActivities();
                    Collections.sort(activities, new Comparator<Activity>(){
                        public int compare(Activity a1, Activity a2) {
                            return a1.name.compareTo(a2.name);
                        }
                    });
                    for(Activity activity : activities) {
                    %>
                    <option value="<%=activity.id%>"><%=activity.name%></option>
                    <%
                    }
                    %>
                </select>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary" id="save_btn">Save</button>
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function(){
    $('.combobox').combobox();

    $('#save_btn').click(function(){
        var id = $('#id').val();
        if(id) {
            $.get(getAPIURL("api/activity/track?id=" + id), function(data) {
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    showSuccess(msg);
                }
                else {
                    showDanger(msg);
                }
            }, "json");
        }
    });
});
</script>
<%@ include file="footer.jsp" %>
