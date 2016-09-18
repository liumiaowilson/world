<%@ page import="org.wilson.world.etiquette.*" %>
<%
String page_title = "Etiquette Gallery View";
%>
<%@ include file="header.jsp" %>
<%
EtiquetteItem etiquette_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
etiquette_item = EtiquetteManager.getInstance().getEtiquetteItem(id);
if(etiquette_item == null) {
    response.sendRedirect("etiquette_gallery.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Etiquette Gallery View</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/etiquette/get?id=<%=etiquette_item.id%>"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        var title = data.result.data.title;
                        var html = data.result.data.html;
                        $('#content').append("<h3>" + title + "</h3>");
                        $('#content').append(html);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
