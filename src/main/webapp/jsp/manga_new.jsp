<%@ page import="org.wilson.world.manga.*" %>
<%
String page_title = "Manga New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <div class="form-group">
        <label for="creator">Creator</label>
        <select class="combobox form-control" id="creator" required>
            <option></option>
            <%
            List<MangaCreator> creators = MangaManager.getInstance().getMangaCreators();
            Collections.sort(creators, new Comparator<MangaCreator>(){
                public int compare(MangaCreator c1, MangaCreator c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });
            for(MangaCreator creator : creators) {
            %>
            <option value="<%=creator.getName()%>"><%=creator.getName()%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="parameters">Parameters</label>
        <textarea class="form-control" id="parameters" rows="5" placeholder="Enter parameters" required></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<script>
            $('#creator').change(function(){
                var creator = $('#creator').val();
                if(creator) {
                    $.get(getAPIURL("api/manga/get_parameters_hint?creator=" + $('#creator').val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            $('#parameters').attr('placeholder', msg);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                }
            });

            $(document).ready(function(){
                $('.combobox').combobox();

                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/manga/create"), { creator: $('#creator').val(), 'parameters': $('#parameters').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    jumpCurrent();
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                }
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#create_new').val("false");
                    $('#form').submit();
                });

                $('#save_new_btn').click(function(){
                    $('#create_new').val("true");
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
