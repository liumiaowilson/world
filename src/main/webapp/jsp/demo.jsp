<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_tooltipster.jsp" %>
<%@ include file="navbar.jsp" %>
<div style="display:none">
    <span id="tooltip_content">
        <img src="https://cdn.pixabay.com/photo/2014/12/24/05/02/drops-of-water-578897_960_720.jpg" /> <strong>This is the content of my tooltip!</strong>
    </span>
</div>
<span class="tooltipster" data-tooltip-content="#tooltip_content">Some text</span>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_tooltipster.jsp" %>
<script>
            $(document).ready(function(){
                $('.tooltipster').tooltipster();
            });
</script>
<%@ include file="footer.jsp" %>
