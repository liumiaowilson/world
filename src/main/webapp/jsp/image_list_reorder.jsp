<%@ page import="org.wilson.world.image.*" %>
<%
String page_title = "Image List Reorder";
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
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Image List Reorder</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered" id="sort_table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(ImageRef ref : refs) {
                %>
                <tr>
                    <td id="name"><%=ref.getName()%></td>
                    <td>
                        <button type="button" class="btn btn-xs" id="up_btn">
                            <span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>
                        </button>
                        <button type="button" class="btn btn-xs" id="down_btn">
                            <span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span>
                        </button>
                    </td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function(){
    var l = $('#save_btn').ladda();

    $("#up_btn, #down_btn").click(function(){
        var row = $(this).parents("tr:first");
        if ($(this).is("#up_btn")) {
            row.insertBefore(row.prev());
        } else {
            row.insertAfter(row.next());
        }
    });

    $("#save_btn").click(function(){
        var names = [];
        $("#sort_table tbody tr").each(function(){
            names.push($(this).find("#name").text());
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
