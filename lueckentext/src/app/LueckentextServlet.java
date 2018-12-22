package app;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class LueckentextServlet extends HttpServlet {

	@Override
	   public void doGet(HttpServletRequest request, HttpServletResponse response)
	         throws IOException, ServletException {
	 
	      // Set the response MIME type of the response message
	      response.setContentType("text/html; charset=UTF-8");
	      // Allocate a output writer to write the response message into the network socket
	      PrintWriter out = response.getWriter();
	 
	      // Write the response message, in an HTML page
	      try {
	    	 out.println("<!DOCTYPE html>");
			 out.println("<html>");
	         out.println("<head><title>Lückentext</title><link href='lueck.css' rel='stylesheet' type='text/css' /></head>");
	         out.println("<body>");
	         out.println("<h3>Lückentext Test  -- from Java</h3>");  // says Hello
	         // Form to start test
	         //out.println("<form method='get' action='http://localhost:9999/lueckentext/cloze'>");
	         out.println("<form method='get' action='cloze'>");
	         out.println("<input type='submit' value='"+Util.translateLabel("StartTest")+"' />");
	         out.println("</form>");
	         out.println("</body></html>");
	      } finally {
	         out.close();  // Always close the output writer
	      }
	   }
	
}
