<%@ page import="org.wilson.world.image.*" %>
<%
String page_title = "Image Set Image";
%>
<%@ include file="header.jsp" %>
<%
String refName = request.getParameter("refName");
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Image Set Image</h3>
    </div>
    <div class="panel-body">
        <div class="form-group">
            <label for="image">Image</label>
            <select class="combobox form-control" id="image">
                <option></option>
                <%
                List<String> imageRefNames = ImageManager.getInstance().getImageRefNames();
                Collections.sort(imageRefNames);
                for(String imageRefName : imageRefNames) {
                    String selectedStr = imageRefName.equals(refName) ? "selected" : "";
                %>
                <option value="<%=imageRefName%>" <%=selectedStr%>><%=imageRefName%></option>
                <%
                }
                %>
            </select>
        </div>
        <div class="form-group">
            <label for="preview">Preview</label>
            <div id="preview">
                <%
                String url = "";
                if(refName != null) {
                    ImageRef imageRef = ImageManager.getInstance().getImageRef(refName);
                    url = imageRef.getUrl(150, 150, true);
                }
                %>
                <img src="<%=url%>"/>
            </div>
        </div>
        <div class="form-group">
            <label for="sets">Image Sets</label>
            <div id="sets">
                <%
                List<ImageSet> sets = ImageSetManager.getInstance().getImageSets();
                Collections.sort(sets, new Comparator<ImageSet>(){
                    public int compare(ImageSet s1, ImageSet s2) {
                        return s1.name.compareTo(s2.name);
                    }
                });
                for(ImageSet set : sets) {
                    String checkedStr = set.refs.contains(refName) ? "checked" : "";
                %>
                <div class="checkbox">
                    <label><input type="checkbox" value="<%=set.name%>" <%=checkedStr%>><%=set.name%></label>
                </div>
                <%
                }
                %>
            </div>
        </div>
        <button type="button" class="btn btn-danger" id="save_btn">Save</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $('#image').change(function(){
                $.post(getAPIURL("api/image_set/get_image"), { 'name': $('#image').val(), 'width': 150, 'height': 150, 'adjust': true }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        var url = data.result.data.url;
                        $('#preview img').attr("src", url);
                        $('input[type=checkbox]').prop('checked', false);
                        var names = data.result.data.setNames.split(",");
                        for(var i in names) {
                            var name = names[i];
                            $('input[type=checkbox][value="' + name + '"]').prop('checked', true);
                        }
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });

            $(document).ready(function(){
                $('.combobox').combobox();

                $('#save_btn').click(function(){
                    var names = [];
                    $('input[type=checkbox]').each(function () {
                        if (this.checked) {
                            names.push(this.value);
                        }
                    });
                    $.post(getAPIURL("api/image_set/set_image_sets"), { 'names': names.join(','), 'image': $('#image').val() }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
