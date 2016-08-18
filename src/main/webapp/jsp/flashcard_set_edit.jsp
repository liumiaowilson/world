<%
String page_title = "FlashCard Set Edit";
%>
<%@ include file="header.jsp" %>
<%
FlashCardSet flashcard_set = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
flashcard_set = FlashCardSetManager.getInstance().getFlashCardSet(id);
if(flashcard_set == null) {
    response.sendRedirect("flashcard_set_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=flashcard_set.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=flashcard_set.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=flashcard_set.description%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteFlashCardSet()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteFlashCardSet() {
                bootbox.confirm("Are you sure to delete this flashcard set?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/flashcard_set/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/flashcard_set/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val()}, function(data) {
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

                $('#left_btn').click(function(){
                    $.get(getAPIURL("api/flashcard_set/prev?id=<%=flashcard_set.id%>"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var prev = parseInt(data.result.data.$);
                            if(prev < 0) {
                                showDanger("Previous flashcard_set does not exist.");
                            }
                            else {
                                jumpTo("flashcard_set_edit.jsp?id=" + prev);
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#right_btn').click(function(){
                    $.get(getAPIURL("api/flashcard_set/next?id=<%=flashcard_set.id%>"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var next = parseInt(data.result.data.$);
                            if(next < 0) {
                                showDanger("Next flashcard_set does not exist.");
                            }
                            else {
                                jumpTo("flashcard_set_edit.jsp?id=" + next);
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
