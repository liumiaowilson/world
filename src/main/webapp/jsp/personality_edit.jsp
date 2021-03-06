<%
String page_title = "Personality Edit";
%>
<%@ include file="header.jsp" %>
<%
Personality personality = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
personality = PersonalityManager.getInstance().getPersonality(id);
if(personality == null) {
    response.sendRedirect("personality_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_tag.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=personality.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=personality.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="tags">Tags</label>
        <input type="text" class="form-control" data-role="tagsinput" id="tags" maxlength="400" placeholder="Enter tags" value="<%=personality.tags%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="400" placeholder="Enter detailed description"><%=personality.description%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deletePersonality()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_typeahead.jsp" %>
<%@ include file="import_script_tag.jsp" %>
<script>
            var tags = [
                <%
                List<String> tagnames = PersonalityManager.getInstance().getTags();
                for(String tagname : tagnames) {
                %>
                "<%=tagname%>",
                <%
                }
                %>
                ];
            var tagnames = new Bloodhound({
                datumTokenizer: Bloodhound.tokenizers.whitespace,
                queryTokenizer: Bloodhound.tokenizers.whitespace,
                local: tags
            });
            tagnames.initialize();

            $('#tags').tagsinput({
                typeaheadjs: {
                    name: 'tagnames',
                    source: tagnames
                }
            });

            $('.bootstrap-tagsinput').css("width", "100%");

            function deletePersonality() {
                bootbox.confirm("Are you sure to delete this personality?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/personality/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/personality/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'tags': $('#tags').val() }, function(data) {
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
            });
</script>
<%@ include file="footer.jsp" %>
