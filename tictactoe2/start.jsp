<%@page import = "app.*" %>
<%@page import = "java.util.Map.Entry" %>
<jsp:useBean id="game" class="app.Game" scope="session" />
<!DOCTYPE html>
<html>
<head>
	<title>Tic-Tac-Toe JSP</title>
	<link href='resources/ttt.css' rel='stylesheet' type='text/css' />
</head>
<body>
<h3>Start a new game!</h3>

<!-- Form for NewGame button -->
<form method='post' action='board.jsp'>
	<p>Choose opponent strategy:<select name="computerStrategyType">
	<% 	int prevStrat = -1;
		if( request.getParameter("computerStrategyType") != null ){
			try{
				Integer x = Integer.parseInt(request.getParameter("computerStrategyType"));
				prevStrat = x.intValue();
			} catch (NumberFormatException e){
			}
		}
		for(java.util.Map.Entry<Integer, String> e : app.GameStrategyFactory.STRATEGIES.entrySet()){ %>
			<option value="<%= e.getKey() %>" <% if( e.getKey().intValue() == prevStrat ) %> selected <% ; %> > <%= e.getValue() %> </option>
	<%	} %></select>
	</p>
	<p>Would You start the game? Or leave the first move to the Computer?
	<input type="checkbox" name="humanStarts" value="human" <% if( request.getParameter("humanStarts") != null && !request.getParameter("humanStarts").equalsIgnoreCase("null") ) %> checked <% ; %> >Human starts</input>
	</p>
	<input type='hidden' name='humanSymbol' value='<%= Board.CIRCLE %>'/>
	<input type='hidden' name='usage' value='newgame'/>
	<button type='submit' value='newgame'>Start</button>
</form>
<!-- Previous game + statistics -->
<table class='startTbl'>
	<tr>
		<td class='startTbl'>
			<!-- Show board -->
			<%-- = game.toString() --%>
			<% if( game.getLastGame() != null ){ %>
			<jsp:include page="board.jsp">
				<jsp:param name="usage" value="showlast" />
			</jsp:include>
			<% } %>
		</td><td class='startTbl'>
			<!-- Show statistics -->
			<%@include file="stats.jsp" %>
		</td>
	</tr>
</table>
</body>
</html>