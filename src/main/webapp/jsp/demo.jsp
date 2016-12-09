<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_viewer.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
  <ul class="images">
    <li style="display:none"><img src="https://cdn.pixabay.com/photo/2014/12/24/05/02/drops-of-water-578897_960_720.jpg" alt="Picture"></li>
  </ul>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_viewer.jsp" %>
<script>
            $(document).ready(function(){
                $(".images").viewer({
                    inline: true,
                    minWidth: 400,
                    minHeight: 400,
                    interval: 60000,
                });
            });
</script>
<%@ include file="footer.jsp" %>
