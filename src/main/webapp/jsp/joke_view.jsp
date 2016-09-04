<%
String page_title = "Joke View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<input type="hidden" id="joke_id" value=""/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Joke View</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
        <div class="form-group">
            <label for="id">Humor Pattern</label>
            <select class="combobox form-control" id="id">
                <option></option>
                <%
                List<HumorPattern> patterns = HumorPatternManager.getInstance().getHumorPatterns();
                Collections.sort(patterns, new Comparator<HumorPattern>(){
                    public int compare(HumorPattern p1, HumorPattern p2) {
                        return p1.name.compareTo(p2.name);
                    }
                });
                for(HumorPattern pattern : patterns) {
                %>
                <option value="<%=pattern.id%>"><%=pattern.name%></option>
                <%
                }
                %>
                <option value="-1">--New--</option>
            </select>
        </div>
        <div id="new_humor_pattern" style="display:none">
            <fieldset class="form-group">
                <label for="name">Name</label>
                <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name">
                <small class="text-muted">Give a nice and distinct name!</small>
            </fieldset>
            <fieldset class="form-group">
                <label for="content">Content</label>
                <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description"></textarea>
            </fieldset>
        </div>
    </div>
</div>
<button type="button" class="btn btn-primary disabled" id="done_btn">Done</button>
<button type="button" class="btn btn-default disabled" id="save_btn">Save</button>
<%@ include file="import_script.jsp" %>
<script>
            $('#id').change(function(){
                var val = $(this).val();
                if("-1" == val) {
                    $('#new_humor_pattern').show();
                }
                else {
                    $('#new_humor_pattern').hide();
                }
            });

            $(document).ready(function(){
                $('.combobox').combobox();
                $.get(getAPIURL("api/joke/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        $('#joke_id').val(data.result.data.id);
                        var title = data.result.data.title;
                        var html = data.result.data.html;
                        $('#content').append("<h3>" + title + "</h3>");
                        $('#content').append(html);

                        $('#done_btn').removeClass("disabled");
                        $('#done_btn').click(function(){
                            var id = $('#id').val();
                            var name = $('#name').val();
                            var content = $('#name').val();
                            if("-1" == id) {
                                if(!name || !content) {
                                    showDanger("Name and content are required");
                                    return;
                                }
                            }
                            $.post(getAPIURL("api/joke/train"), { 'id': id, 'name': name, 'content': content }, function(data){
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
                        });

                        $('#save_btn').removeClass("disabled");
                        $('#save_btn').click(function(){
                            bootbox.prompt({
                                title: "Enter the name you want to save as.",
                                callback: function(result) {
                                    if(result) {
                                        $.post(getAPIURL("api/joke/save"), { 'id': $('#joke_id').val(), 'name': result }, function(data){
                                            var status = data.result.status;
                                            var msg = data.result.message;
                                            if("OK" == status) {
                                                showSuccess(msg);
                                            }
                                            else {
                                                showDanger(msg);
                                            }
                                        });
                                    }
                                }
                            });
                        });
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
