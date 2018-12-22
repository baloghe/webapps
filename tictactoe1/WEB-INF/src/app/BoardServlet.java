package app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BoardServlet  extends HttpServlet {

	public static String drawBoardHTML(boolean inBtnActive, Game inGame){
		String ret = "<table class='board'>";
		for(int r=0; r<Board.ROWNUM; r++){
			ret += "<tr>";
			for(int c=0; c<Board.COLNUM; c++){
				int cval = inGame.getCellValue(r,c);
				String cellContent = "";
				if(cval==0){
					//not yet occupied => empty button
					if(inBtnActive)
						cellContent = "<button class='emptyCell' type='submit' name='cell' value='"+(r*Board.COLNUM + c)+"'>&nbsp;</button>";
					else cellContent = "<div class='emptyCell'></div>";
				} else if(cval==Board.CIRCLE){
					//already a circle
					cellContent = "<div class='circleCell'></div>";
				} else if(cval==Board.CROSS){
					//already a cross
					cellContent = "<div class='crossCell'></div>";
				}
				ret += ("<td class='boardCell'>"+cellContent+"</td>");
			}
			ret += "</tr>";
		}
		ret += "</table>";
		return ret;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	         throws IOException, ServletException {
		
		int cellReceived = -1;
		try{
			Integer x = Integer.parseInt(request.getParameter("cell"));
			cellReceived = x.intValue();
		} catch (NumberFormatException e){
		}
		if(cellReceived >=0 && cellReceived < Board.ROWNUM * Board.COLNUM){
			//User movement
			StartServlet.game.handleMove(Board.CROSS, cellReceived / Board.COLNUM, cellReceived % Board.ROWNUM);
			
			//check if game is not over => if not, Computer's turn
			if(!StartServlet.game.isGameFinished()){
				StartServlet.game.computerMove();
			}
		}
		
		String greeting;
		String tmpactionurl;
		boolean gameOver = StartServlet.game.isGameFinished();
		if( gameOver ){
			greeting = "Game over!";
			tmpactionurl = "start";
		} else {
			greeting = "Your turn!";
			tmpactionurl = "board";
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
			out.println("<h3>"+greeting+"</h3>");
			out.println("<form method='post' action='"+tmpactionurl+"'>");
			out.println("<input type='hidden' name='prvPg' value='board'/>");
			out.println( drawBoardHTML(!gameOver, StartServlet.game) );
			if( gameOver ) 
				out.println("<button type='submit' value='newgame'>New game</button>");
			out.println("</form>");
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
