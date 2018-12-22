<%@page import = "app.NumberGuessBean" %>
<%@page import = "app.Guess" %>
<jsp:useBean id="numguess" class="app.NumberGuessBean" scope="session" />
<%-- This JSP has a form with request parameter "guess=xxx", which matches
     the setGuess() method of the bean  --%>
<jsp:setProperty name="numguess" property="*"/>

<html>
<head>
	<title>Number Guess - plain vanilla with JSP</title>
	<link href='resources/nguess.css' rel='stylesheet' type='text/css' />
</head>
<body>

<%
if (numguess.isSuccess()) {    // correct guess - updated by setGuess() bound earlier
%>
  <p>Congratulations!  You got it, after <%= numguess.getCntGuess() %> tries.</p>
  <% numguess.reset(); %>
  <p><a href="<%= request.getRequestURI() %>">Play again</a></p>
<%
} else { //start numguess.isSuccess()=false branch
	if (numguess.getCntGuess() == 0 ) {   // start a game
%>
		<h3>Welcome to the Number Guess game!</h3>
<%
	} else {   // in a game, wrong guess
%>
		<p>Good guess, but nope. Try a <strong>
			<% if( numguess.getHint() < 0 ){ %>
				smaller
			<% } else if( numguess.getHint() > 0 ) { %>
				bigger
			<% } else { %>
				valid integer
			<% } %>
			</strong> number!
		</p>
		<p>You have made <%= numguess.getCntGuess() %> guesses so far:</p>
		<!-- TBD: enumerate valid guesses with smaller and greater signs, respectively -->
		<p><% int i=0; for(Guess g : numguess.getPrvGuesses()){ %>
			<%if( i>0 ){ %> , <% } %>
			<%= g.getGuess() %>
			<% if( g.getHint() < 0 ) { %>
				&gt;
			<% } else if( g.getHint() > 0 ) { %>
				&lt;
			<% } %>
		<% i++;} %></p>
		
<%
	}
%>

	<%-- Putting up the form to get the user guess  --%>
	<% int numbase = numguess.getNumBase(); %>
	<p>I'm thinking of a number between 1 and <%= numbase %>.</p>
	<p>What's your guess?</p>
	<form method="get">
		<%	int sqr = (int) Math.sqrt( (double)numbase );				
			int numRow = ( sqr * sqr < numbase ? sqr+1 : sqr  );
			int remCol = numbase - sqr * sqr;
		%>
		<p>numRow=<%= numRow %>, remCol=<%= remCol %></p>
		<table class='guessable'>
			<%	int actNum = 1;
				for(int r = 0; r <= numRow; r++){ %>
					<tr>
					<%	for(int c = 0; c < (r==numRow-1 ? remCol : numRow); c++){ %>
							<td class='guessable'>
							<% int hint = numguess.guessedAlready(actNum);
							if( hint == 0 ){ %>
								<button class='guessable' type='submit' name='guess' value='<%=actNum%>'><%=actNum%></button>
							<%} else if( hint < 0 ) {%>
								<div class='hintSmaller'><%=actNum%></div>
							<%} else {%>
								<div class='hintBigger'><%=actNum%></div>
							<%}%>
							</td>
					<%		actNum++;
						}%>
					</tr>
			<%	}%>			
		</table>
	</form>

<%
  } //close numguess.isSuccess()=false branch...
%>
</body></html>
