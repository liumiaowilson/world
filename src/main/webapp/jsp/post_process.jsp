<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="post_table" class="table table-striped table-bordered">
    <thead>
        <tr>
            <th>Name</th>
            <th>Content</th>
        </tr>
    </thead>
    <tbody>
        <%
        List<String> posts = PostManager.getInstance().getPosts();
        for(String post : posts) {
            String name = post;
            if(name.length() >20) {
                name = name.substring(0, 20);
            }
        %>
        <tr>
            <td id="name"><%=name%></td>
            <td id="content"><%=post%></td>
        </tr>
        <%
        }
        %>
    </tbody>
</table>
<h3>
    <span class="label label-info">Process these posts into ideas.</span>
</h3>
<hr/>
<form id="form" data-toggle="validator" role="form">
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();
                $.fn.editable.defaults.mode = 'inline';
                configTable();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var ideas = [];
                        var validation = "";
                        $('#post_table tbody tr').each(function(){
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
                        $.post(getAPIURL("api/post/process"), { 'ideas': JSON.stringify(ideas) }, function(data) {
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
                    }
                });

                $('#save_btn').click(function(){
                    $('#form').submit();
                });
            });

function configTable() {
    $('#post_table tbody td[id="name"]').editable();
}
</script>
<%@ include file="footer.jsp" %>
