<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<table id="merge_table" class="table table-striped table-bordered">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
        </tr>
    </thead>
    <tbody>
        <%
        String id = request.getParameter("id");
        List<String> markedIds = MarkManager.getInstance().getMarked("idea");
        markedIds.add(id);
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < markedIds.size(); i++) {
            String markedId = markedIds.get(i);
            Idea idea = IdeaManager.getInstance().getIdea(Integer.parseInt(markedId));
            sb.append(markedId);
            if(i != markedIds.size() - 1) {
                sb.append(",");
            }
        %>
        <tr>
            <td><a href="javascript:jumpTo('idea_edit.jsp?id=<%=idea.id%>')"><%=idea.id%></a></td>
            <td><%=idea.name%></td>
        </tr>
        <%
        }
        %>
    </tbody>
</table>
<h3>
    <span class="label label-info">Merge ideas above into below new idea</span>
</h3>
<hr/>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholde="Enter detailed description"></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/idea/merge"), { name: $('#name').val(), 'content': content, 'id': <%=id%>, 'ids': "<%=sb.toString()%>"}, function(data) {
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
</script>
<%@ include file="footer.jsp" %>
