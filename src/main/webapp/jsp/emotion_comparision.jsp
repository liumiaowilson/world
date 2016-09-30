<%@ page import="org.wilson.world.emotion.*" %>
<%
String page_title = "Emotion Comparision";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datatable.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String typesStr = request.getParameter("types");
if(typesStr == null) {
    typesStr = "";
}
List<String> typeList = new ArrayList<String>();
for(String type : typesStr.trim().split(",")) {
    if(type != null && !"".equals(type.trim())) {
        typeList.add(type);
    }
}

for(EmotionType type : EmotionType.values()) {
    String selectedStr = typesStr.contains(type.name()) ? "checked" : "";
%>
<label><input type="checkbox" id="type" name="type" value="<%=type.name()%>" <%=selectedStr%>/><%=type.name()%></label><br/>
<%
}
%>
<button type="submit" class="btn btn-primary" id="query_btn">Query</button>
<hr/>
<table id="emotion_table" class="display" style="display:none">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <%
            for(String type : typeList) {
            %>
            <th><%=type%></th>
            <%
            }
            %>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datatable.jsp" %>
<script>
            $('#query_btn').click(function(){
                var types = [];
                $('input[type=checkbox]').each(function () {
                    if (this.checked) {
                        types.push(this.value);
                    }
                });
                jumpTo('emotion_comparision.jsp?types=' + types.join(","));
            });

            $(document).ready(function(){
                $.get(getAPIURL("api/emotion/comparision?types=<%=typesStr%>"), function(data){
                    var status = data.result.status;
                    if("OK" == status) {
                        var array = data.result.list;
                        $('#emotion_table').show();
                        $('#emotion_table').DataTable({
                            dom: 'Bfrtip',
                            data: array,
                            //disable initial sorting
                            aaSorting: [],
                            columns: [
                                {
                                    data: 'id',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        $(nTd).html("<a href=\"javascript:jumpTo('emotion_edit.jsp?id=" + oData.id + "')\">" + oData.id + "</a>");
                                    }
                                },
                                {
                                    data: 'name',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.name;
                                        $(nTd).html(content);
                                        nTd.title = oData.description;
                                    }
                                },
                                <%
                                for(String type : typeList) {
                                    String typename = Emotion.getName(EmotionType.valueOf(type));
                                %>
                                {
                                    data: '<%=typename%>',
                                    fnCreatedCell: function (nTd, sData, oData, iRow, iCol) {
                                        var content = oData.<%=typename%>;
                                        $(nTd).html(content);
                                    }
                                },
                                <%
                                }
                                %>
                            ],
                            buttons: [
                                {
                                    text: 'New',
                                    action: function (e, dt, node, config) {
                                        jumpTo("emotion_new.jsp");
                                    }
                                }
                            ]
                        });
                        $('#emotion_table').dataTable().$('tr').tooltip({
                            "delay": 0,
                            "track": true,
                            "fade": 250
                        });
                    }
                    else {
                        var msg = data.result.message;
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
