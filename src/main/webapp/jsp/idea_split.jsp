<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
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
            <td id="name"><%=idea.name%></td>
            <td id="content"><%=FormatUtils.safeString(idea.content)%></td>
        </tr>
    </tbody>
</table>
<div class="form-group">
    <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
    <button type="button" class="btn btn-default" id="add_btn">
        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default" id="delete_btn">
        <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
    </button>
    <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
$(document).ready(function(){
    var l = $('#save_btn').ladda();
    $.fn.editable.defaults.mode = 'inline';
    configTable();

    $('#save_btn').click(function(){
        var id = <%=id%>;
        var ideas = [];
        var validation = "";
        $('#split_table tbody tr').each(function(){
            $this = $(this);
            var name = $this.find("#name").text();
            var content = $this.find("#content").text();
            if(!name) {
                validation = "New idea name should be provided.";
                return;
            }
            if(name.length > 20) {
                validation = "New idea name should be less than 20.";
                return;
            }
            if(content.length > 200) {
                validation = "New idea content should be less than 200.";
                return;
            }
            ideas.push({'name': name, 'content': content});
        });
        if(ideas.length == 0) {
            validation = "No new ideas are provided.";
        }
        if(validation) {
            showDanger(validation);
            return;
        }
        l.ladda('start');
        $.post(getAPIURL("api/idea/split"), { 'id': id, 'ideas': JSON.stringify(ideas) }, function(data){
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

    $('#add_btn').click(function(){
        $('#split_table tbody tr:last').after('<tr><td id="name"><%=idea.name%></td><td id="content"><%=FormatUtils.safeString(idea.content)%></td></tr>');
        configTable();
    });

    $('#delete_btn').click(function(){
        $('#split_table tbody tr:last').remove();
    });
});

function configTable() {
    $('#split_table tbody td[id="name"]').editable();
}
</script>
<%@ include file="footer.jsp" %>
