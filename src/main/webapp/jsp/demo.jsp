<%@ page import="java.lang.management.*" %>
<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_slider.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Exchange</h3>
        </div>
        <div class="panel-body">
            <input id="coins" data-slider-id='coinsSlider' type="text" data-slider-min="0" data-slider-max="20" data-slider-step="1" data-slider-value="14"/>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_slider.jsp" %>
<script>
$('#coins').slider({
    formatter: function(value) {
        return 'Current value: ' + value;
    }
});
</script>
<%@ include file="footer.jsp" %>
