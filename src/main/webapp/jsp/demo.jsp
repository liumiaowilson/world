<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_tag.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <fieldset class="form-group">
                <label for="content">Content</label>
                <input id="demo" class="form-control" type="text" data-role="tagsinput" value="Amsterdam,Washington,Sydney,Beijing,Cairo">
            </fieldset>
            <fieldset class="form-group">
                <label for="content">Content</label>
                <input type="text" class="form-control" value="Amsterdam,Washington,Sydney,Beijing,Cairo">
            </fieldset>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_typeahead.jsp" %>
<%@ include file="import_script_tag.jsp" %>
<script>
var citynames = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: {
        url: getAPIURL("api/demo/names"),
        filter: function(list) {
            return $.map(list, function(cityname) {
                return { name: cityname };
            });
        }
    }
});
citynames.initialize();

$('#demo').tagsinput({
    typeaheadjs: {
        name: 'citynames',
        displayKey: 'name',
        valueKey: 'name',
        source: citynames.ttAdapter()
    }
});

$('.bootstrap-tagsinput').css("width", "100%");
</script>
<%@ include file="footer.jsp" %>
