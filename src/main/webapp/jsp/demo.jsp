<%@ page import="org.wilson.world.image.*" %>
<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String url = "https://cdn.pixabay.com/photo/2014/12/24/05/02/drops-of-water-578897_960_720.jpg";
%>
<img src="<%=url%>" width="150px" height="150px"/>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
