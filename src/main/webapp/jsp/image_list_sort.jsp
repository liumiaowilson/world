<%@ page import="org.wilson.world.image.*" %>
<%
String page_title = "Image List Sort";
%>
<%@ include file="header.jsp" %>
<%
ImageList image_list = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
image_list = ImageListManager.getInstance().getImageList(id);
if(image_list == null) {
    response.sendRedirect("image_list_list.jsp");
    return;
}

List<ImageRef> refs = ImageListManager.getInstance().getImageRefs(image_list);
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_jquery_ui.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Image List Sort</h3>
    </div>
    <div class="panel-body">
        <ul id="sortable">
            <%
            for(ImageRef ref : refs) {
            %>
            <li class="ui-state-default"><img src="<%=ref.getUrl(null, 150, 150, true)%>" name="<%=ref.getName()%>"/></li>
            <%
            }
            %>
        </ul>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_jquery_ui.jsp" %>
<script>
$(document).ready(function(){
    $( "#sortable" ).sortable();
    $( "#sortable" ).disableSelection();

    var l = $('#save_btn').ladda();

    $("#save_btn").click(function(){
        var names = [];
        $("ul#sortable li img").each(function(){
            names.push($(this).attr("name"));
        });

        l.ladda('start');
        $.post(getAPIURL("api/image_list/sort"), { 'names': names.join(","), 'id': <%=image_list.id%> }, function(data) {
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                showSuccess(msg);
                l.ladda('stop');
                jumpBack();
            }
            else {
                showDanger(msg);
                l.ladda('stop');
            }
        }, "json");
    });
});
</script>
<%@ include file="footer.jsp" %>
