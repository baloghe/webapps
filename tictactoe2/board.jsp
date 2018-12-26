<%@page import = "app.*" %>
<jsp:useBean id="game" class="app.Game" scope="session" />

<% if(request.getParameter("usage").equalsIgnoreCase("showlast")){ %>
<h3>Last game:</h3>
<% } else {// endif usage=showlast %>
<!DOCTYPE html>
<html>
<head>
	<title>Tic-Tac-Toe JSP</title>
	<link href='resources/ttt.css' rel='stylesheet' type='text/css' />
</head>
<body>
<% if( request.getParameter("usage").equalsIgnoreCase("newgame") ){ %>
	<jsp:setProperty name="game" property="*" />
	<% game.reset(); %>
<% } %>

<%	if( (!game.isGameFinished()) && request.getParameter("usage").equalsIgnoreCase("playing") ){ 
		//consume previous user move
		int cellReceived = -1;
		try{
			Integer x = Integer.parseInt(request.getParameter("cell"));
			cellReceived = x.intValue();
		} catch (NumberFormatException e){
		}
		game.manageTurn( cellReceived );
	}
%>

<% 	String actAction = "";
	if( game.isGameFinished() ){ 
		actAction = "start.jsp"; %>
		<h3>Game over!</h3>
<%	} else { 
		actAction = "board.jsp"; %>
		<h3>It's Your turn!</h3>
<%	} %>

<%-- = game.toString() --%>

<form method='post' action='<%= actAction %>'>

<% } // endif usage!=showlast %>

<table class='board'>
	<% for(int r=0; r<app.Board.ROWNUM; r++){ %>
		<tr>
		<% for(int c=0; c<app.Board.COLNUM; c++){ 
			int cval = game.getCellValue(r,c);
			String cellContent = "";
			if(cval==0){ //empty
				if( request.getParameter("usage").equalsIgnoreCase("showlast") || ( game != null && game.isGameFinished() ) ){ //show only
					cellContent = "<div class='emptyCell'></div>";
				} else {
					cellContent = "<button class='emptyCell' type='submit' name='cell' value='"+(r*Board.COLNUM + c)+"'>&nbsp;</button>";
				}
			} else if(cval==Board.CIRCLE){ //already a circle
				cellContent = "<div class='circleCell'></div>";
			} else if(cval==Board.CROSS){  //already a cross
				cellContent = "<div class='crossCell'></div>";
			}
			%>
			<td class='boardCell'> <%=cellContent %> </td>
		<% } //next c %>
		</tr>
	<% } //next r %>
</table>

<% if( !request.getParameter("usage").equalsIgnoreCase("showlast") ){ //complete <form> %>

<input type='hidden' name='usage' value='playing'/>
<%	if( game.isGameFinished() ){ %>
		<button type='submit' value='start'>New game</button>
<%	} %>
</form>

<% } // endif usage!=showlast %>

<% if(request.getParameter("usage").equalsIgnoreCase("showlast")){ %>

<% } else {// endif usage=showlast %>
</body></html>
<% }// endif usage!=showlast %>