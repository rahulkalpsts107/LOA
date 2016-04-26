<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<% //To do check how to include in /js directory %>
 <%
 	Integer gameFormat = Integer.parseInt(request.getParameter("gameformat"));
 	Integer id = (Integer)request.getSession().getAttribute("gameId");
 	Integer ply = Integer.parseInt(request.getParameter("ply"));
 	System.out.println("game id is "+id);
 	int widthBlk = gameFormat;
 	System.out.println("game format is "+widthBlk);
 	int heightBlk = widthBlk;
 	int humanType = ply;
 %>

<link rel="stylesheet" type="text/css" href="css/style.css">
<script src="<c:url value="js/jquery-1.8.2.min.js" />" ></script>
<script src="<c:url value="js/snap.svg-min.js" />" ></script>
<script src="<c:url value="js/ai-loa.js" />" ></script>
<script src="<c:url value="js/json.js" />" ></script>
    <body>
    	<h4> Human is <%= humanType== 0?"Black":"White" %> CPU is <%= humanType == 0? "White":"Black" %></h4>
        <div id="svgout"></div>
        <div class="modal"><!-- Place at bottom of page --></div>
    </body>
<script>//moved it below body
$( document ).ready(function() {
	$body = $("body");
	$(document).on({
	    ajaxStart: function() { $body.addClass("loading");    },
	     ajaxStop: function() { $body.removeClass("loading"); }    
	});
	startGame(<%=widthBlk%>,<%=heightBlk%>,<%=id%>, <%=humanType%>);
});

</script>
</html>