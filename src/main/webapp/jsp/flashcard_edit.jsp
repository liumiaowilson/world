<%
String page_title = "FlashCard Edit";
%>
<%@ include file="header.jsp" %>
<%
FlashCard flashcard = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
flashcard = FlashCardManager.getInstance().getFlashCard(id);
if(flashcard == null) {
    response.sendRedirect("flashcard_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=flashcard.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=flashcard.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="setId">Set</label>
        <select class="combobox form-control" id="setId" required>
            <option></option>
            <%
            List<FlashCardSet> sets = FlashCardSetManager.getInstance().getFlashCardSets();
            Collections.sort(sets, new Comparator<FlashCardSet>(){
                public int compare(FlashCardSet s1, FlashCardSet s2) {
                    return s1.name.compareTo(s2.name);
                }
            });
            for(FlashCardSet set : sets) {
                String selectedStr = flashcard.setId == set.id ? "selected" : "";
            %>
            <option value="<%=set.id%>" <%=selectedStr%>><%=set.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="top">Top</label>
        <textarea class="form-control" id="top" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=flashcard.top%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="bottom">Bottom</label>
        <textarea class="form-control" id="bottom" rows="5" maxlength="400" placeholder="Enter detailed description" required><%=flashcard.bottom%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteFlashCard()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteFlashCard() {
                bootbox.confirm("Are you sure to delete this flashcard?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/flashcard/delete?id=" + id), function(data){
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
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var top = $('#top').val();
                        if(!top) {
                            top = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/flashcard/update"), { id: $('#id').val(), name: $('#name').val(), top: $('#top').val(), 'setId': $('#setId').val(), 'bottom': $('#bottom').val() }, function(data) {
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
