<%@page import = "app.*" %>
<%@page import = "java.util.ArrayList" %>
<%@page import = "java.text.SimpleDateFormat" %>
<%@page import = "java.util.TimeZone" %>

<%-- useBean is not needed as stats.jsp is always called with @include and not with jsp:include -> so game is already there --%>
<%-- jsp:useBean id="game" class="app.Game" scope="session" --%>

<%	ArrayList<GameStatistics> stats = game.getGameStatistics();
	if(stats.size() > 0){
%>
<h3>Former results:</h3>
<table class='stats'>
	<tr><th class='stats'>#</th><th class='stats'>Time</th><th class='stats'>Moves</th><th class='stats'>Winner</th></tr>
	<%	int i=0;
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		for(GameStatistics s : stats){ 
			String dur = formatter.format(s.getDurationMillis());
			++i;
	%>
			<tr>
				<td class='stats'><%= i %>
				<td class='stats'><%= dur %></td>
				<td class='stats'><%= s.getTurns() %></td>
				<td class='stats'><%= s.getWinner()%></td>
			</tr>
	<%	}%>
</table>
<%	} %>