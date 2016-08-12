<%
String page_title = "Face";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<link href="<%=basePath%>/Upload/svgavatars/css/normalize.css" rel="stylesheet">
<link href="<%=basePath%>/Upload/svgavatars/css/spectrum.css" rel="stylesheet">
<link href="<%=basePath%>/Upload/svgavatars/css/svgavatars.css" rel="stylesheet">
<link href="http://fonts.googleapis.com/css?family=Roboto+Condensed:400,300,700|Roboto:400,300,500,700&amp;subset=latin,cyrillic-ext,cyrillic,latin-ext" rel="stylesheet" type="text/css">
<%@ include file="navbar.jsp" %>
<div style="width:960px; margin:0 auto;">
    <div id="svgAvatars"></div>
</div>
<%@ include file="import_script.jsp" %>
<script src="<%=basePath%>/Upload/svgavatars/js/svg.min.js"></script>
<script src='<%=basePath%>/Upload/svgavatars/js/spectrum.min.js'></script>
<script src="<%=basePath%>/Upload/svgavatars/js/jquery.scrollbar.min.js"></script>
<script src="<%=basePath%>/Upload/svgavatars/js/canvg/rgbcolor.js"></script> 
<script src="<%=basePath%>/Upload/svgavatars/js/canvg/StackBlur.js"></script>
<script src="<%=basePath%>/Upload/svgavatars/js/canvg/canvg.js"></script>
<script src="<%=basePath%>/Upload/svgavatars/js/svgavatars.en.js"></script>
<script src="<%=basePath%>/Upload/svgavatars/js/svgavatars.core.min.js"></script>
<script>
</script>
<%@ include file="footer.jsp" %>
