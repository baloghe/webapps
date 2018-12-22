package app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResultsServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	         throws IOException, ServletException {
	 
		// Set the response MIME type of the response message
		response.setContentType("text/html; charset=UTF-8");
		// Allocate a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();
		
		ClozeTest test = ClozeTestServlet.getTest();
		String prvPg = request.getParameter("prvPg");
		if(test != null && prvPg.equalsIgnoreCase("cloze")){
			  //the last sentence in the actual test still needs an evaluation...
			  test.evaluateAnswer(request);
		}
		
		//close test
		Statistics stat = test.calcStatistics();
		Util.addStatistics(stat);
		 
		// Write the response message, in an HTML page
		try {
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head><title>Lückentext</title><link href='lueck.css' rel='stylesheet' type='text/css' /></head>");
			out.println("<body>");
			out.println("<h3>Ergebnisse</h3>");
			
			//actual test results
			out.println("<br/>");
			out.println( test.resultsToString() );
			
			//statistics
			out.println("<br/>");
			out.println( Util.statisticsToHtmlString() );
			
			// Form to start a new test
			//out.println("<form method='get' action='http://localhost:9999/lueckentext/params'>");
			out.println("<form method='get' action='params'>");
			out.println("<input type='hidden' name='prvPg' value='results'>");
			out.println("<input type='submit' value='"+Util.translateLabel("NewTest")+"' />");
			out.println("</form>");
			//out.println("<form method='get' action='http://localhost:9999/lueckentext/cloze'>");
			out.println("<form method='get' action='cloze'>");
			out.println("<input type='submit' value='"+Util.translateLabel("Repeat")+"' />");
			out.println("<input type='hidden' name='prvPg' value='results'>");
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
