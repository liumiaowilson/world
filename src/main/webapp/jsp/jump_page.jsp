<%
String page_title = "Jump Page";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_typeahead.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Jump Page</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form">
            <fieldset class="form-group">
                <label for="page">Page</label>
                <input type="text" class="form-control typeahead" id="page" maxlength="50" placeholder="Enter page" required autofocus>
            </fieldset>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary" id="jump_btn">Jump</button>
            </div>
        </form>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_typeahead.jsp" %>
<script>
            var substringMatcher = function(strs) {
                return function findMatches(q, cb) {
                    var matches, substringRegex;

                    // an array that will be populated with substring matches
                    matches = [];

                    // regex used to determine if a string contains the substring `q`
                    substrRegex = new RegExp(q, 'i');

                    // iterate through the pool of strings and for any string that
                    // contains the substring `q`, add it to the `matches` array
                    $.each(strs, function(i, str) {
                        if (substrRegex.test(str)) {
                            matches.push(str);
                        }
                    });

                    cb(matches);
                };
            };

            var pages = [
            <%
            List<String> page_ids = MenuManager.getInstance().getSingleMenuIds();
            Collections.sort(page_ids);
            for(String page_id : page_ids) {
            %>
            "<%=page_id%>",
            <%
            }
            %>
            ];

            $('.typeahead').typeahead({
                hint: true,
                highlight: true,
                minLength: 1,
            },
            {
                name: 'pages',
                source: substringMatcher(pages)
            });
            $('.twitter-typeahead').css('width', '100%');
            $(document).ready(function(){
                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        $.get(getAPIURL("api/menu/get?id=" + $('#page').val()), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                var script = data.result.data.$;
                                eval(script);
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
