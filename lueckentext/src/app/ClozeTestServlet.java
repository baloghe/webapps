package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClozeTestServlet extends HttpServlet {

	private static ClozeTest test;
	
	public static void setTest(ClozeTest inTest){
		test = inTest;
	}
	
	public static ClozeTest getTest(){
		return test;
	}
	
	public static void finishTest(){
		test = null;
	}
	
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException {
 
	  String prvPg = request.getParameter("prvPg");
	  
	  if(test == null || prvPg.equalsIgnoreCase("param")){
		  
		  //new ClozeTest object must be created and run 
		  
		  //read params
		  HashSet<String> wts = new HashSet<String>();
		  ArrayList<String> sbs = new ArrayList<String>();
		  
		  String[] rqWordTypes = request.getParameterValues("wt");
		  String[] rqSubjects = request.getParameterValues("subj");
		  for(String s : rqWordTypes) wts.add(s);
		  for(String s : rqSubjects) sbs.add(s);
		  
		  double brat = 0.3;
		  try{
			  Double rqBrat = Double.parseDouble(request.getParameter("brat"));
			  brat = rqBrat / 100.0;
		  } catch (NumberFormatException e){
		  }
		  
		  //create TEST object
		  test = new ClozeTest(wts, sbs, brat);
		  //test.start();
	  } else if(test != null && prvPg.equalsIgnoreCase("results")){
		  //existing ClozeTest object is OK but the test run should be repeated
		  test.start();
	  } else if(test != null && prvPg.equalsIgnoreCase("cloze")){
		  //first we have to evaluate the answers given in the previous sentence...
		  test.evaluateAnswer(request);
	  }
	   
	  TestSentence ts = test.next();
	   
      // Set the response MIME type of the response message
      response.setContentType("text/html; charset=ISO-8859-1");
      // Allocate a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      
      //String tmpactionurl = (test.isFinished() ? "http://localhost:9999/lueckentext/results" : "http://localhost:9999/lueckentext/cloze" );
      String tmpactionurl = (test.isFinished() ? "results" : "cloze" );
      String tmpbuttonlab = (test.isFinished() ? Util.translateLabel("Stat") : Util.translateLabel("Next") );
      
      
      // Write the response message, in an HTML page
      try {
    	 out.println("<!DOCTYPE html>");
		 out.println("<html>");
         out.println("<head><title>Lückentext</title><link href='lueck.css' rel='stylesheet' type='text/css' /></head>");
         out.println("<body>");
         out.println("<h3>Frage #"+test.getPointer()+"/"+test.getLength()+"</h3>");  // says Hello
         // Form to start test
         out.println("<form method='post' action='"+tmpactionurl+"'>");
         
         //sentence
         out.println(ts.testToString());

         out.println("<input type='submit' value='"+tmpbuttonlab+"' />");
		 out.println("<input type='hidden' name='prvPg' value='cloze'>");
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
