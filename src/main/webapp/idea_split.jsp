<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%
String from_url = "idea_split.jsp";
%>
<%@ include file="header.jsp" %>
<%
Idea idea = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    idea = new Idea();
}
idea = IdeaManager.getInstance().getIdea(id);
if(idea == null) {
    idea = new Idea();
}
%>
<table class="table table-striped table-bordered" id="split_table">
    <thead>
        <tr>
            <th>Name</th>
            <th>Content</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td id="name">aa</td>
            <td id="content">bb</td>
        </tr>
    </tbody>
</table>
<div class="form-group">
    <button type="button" class="btn btn-primary" id="save_btn">Save</button>
    <button type="button" class="btn btn-default" id="add_btn">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default" id="delete_btn">
        <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
    </button>
</div>
<%@ include file="import_scripts.jsp" %>
<script>
$('#add_btn').click(function(){
    $('#split_table tr:last').after('<tr><td id="name">Name</td><td id="content">Content</td></tr>');
    $('#split_table td').editable();
});

$('#delete_btn').click(function(){
    $('#split_table tr:last').remove();
    $('#split_table td').editable();
});

$(document).ready(function(){
    $.fn.editable.defaults.mode = 'inline';
    $('#split_table td').editable();

    $('#save_btn').click(function(){
        $('#split_table tbody tr').each(function(){
            $this = $(this);
            var name = $this.find("#name").text();
            var content = $this.find("#content").text();
            alert(name + " -> " + content);
        });
    });
});
</script>
<%@ include file="footer.jsp" %>
