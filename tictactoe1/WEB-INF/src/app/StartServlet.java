package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StartServlet extends HttpServlet {
	
	public static Game game = null;
	private static ArrayList<GameStatistics> stats;
	
	public String printStatsHTML(){
		String ret = "<table class='stats'><tr><th class='stats'>#</th><th class='stats'>Time</th><th class='stats'>Moves</th><th class='stats'>Winner</th></tr>";
		int i=0;
		for(GameStatistics s : stats){
			String dur = GameStatistics.formatter.format(s.getDurationMillis());
			ret += ("<tr><td class='stats'>"+(++i)+"</th><td class='stats'>"+dur+"</th><td class='stats'>"+s.getTurns()+"</th><td class='stats'>"+s.getWinner()+"</th></tr>");
		}
		ret += "</table>";
		return ret;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	         throws IOException, ServletException {
		
		String prvPg = request.getParameter("prvPg");
		String boardTbl;
		String gameStats;
		boolean firstGame = false;
		if( game == null ){
			game = new Game();
			boardTbl = "";
			gameStats = "";
			stats = new ArrayList<GameStatistics>();
			firstGame = true;
		} else {
			if(prvPg != null && prvPg.equalsIgnoreCase("board") && game.isGameFinished())
				stats.add(game.getStatistics());
			boardTbl = BoardServlet.drawBoardHTML(false, game);
			gameStats = printStatsHTML();
			game = new Game();
		}

		
		// Set the response MIME type of the response message
		response.setContentType("text/html; charset=ISO-8859-1");
		// Allocate a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();
		// Write the response message, in an HTML page
		try {
			// Write HTML
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head><title>Tic-Tac-Toe Servlet</title><link href='resources/ttt.css' rel='stylesheet' type='text/css' /></head>");
			out.println("<body>");
			out.println("<h3>Start a new game!</h3>");
			out.println("<form method='post' action='board'>");
			out.println("<input type='hidden' name='prvPg' value='start'/>");
			out.println("<button type='submit' value='newgame'>Start</button>");
			out.println("</form>");
			if(!firstGame){
				out.println("<table class='startTbl'><tr><td class='lastGame'>");
				out.println("<h3>Last game:</h3>");
				out.println(boardTbl);
				out.println("</td><td>");
				out.println("<h3>Games played so far:</h3>");
				out.println(gameStats);
				out.println("</td></tr></table>");
			}
			out.println("</body></html>");
		} finally {
			out.close();  // Always close the output writer
		}
	}
	   
   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
      doGet(request, response);  // call doGet()
   }
}
