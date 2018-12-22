package app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParamServlet  extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	         throws IOException, ServletException {
		
		String prvPg = request.getParameter("prvPg");
		
		if(prvPg.equalsIgnoreCase("lueck")){
			//Init Util based on the params received in REQUEST
			String lang = request.getParameter("language");
			Util.setLanguage(lang);
			String contextPath = request.getSession().getServletContext().getRealPath("/");
			//Util.init(contextPath + "/"+lang+".xml");
			Util.init(contextPath);
			//int subjnum = Util.getSubjects().size();
		} else if(prvPg.equalsIgnoreCase("results")){
			//destroy actual test
			ClozeTestServlet.finishTest();
		}
		
		// Set the response MIME type of the response message
		response.setContentType("text/html; charset=ISO-8859-1");
		// Allocate a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();
		// Write the response message, in an HTML page
		try {
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head><title>Lückentext</title><link href='lueck.css' rel='stylesheet' type='text/css' /></head>");
			out.println("<body>");
			out.println("<h3>"+Util.translateLabel("ChooseParameters")+"</h3>");
			// Form to start test
			//out.println("<form method='get' action='http://localhost:9999/lueckentext/cloze'>");
			out.println("<form method='get' action='cloze'>");
			
			out.println("<table><tr><td>");
			//left column: word types
			out.println("<span class='SUBJECT'>"+Util.translateLabel("WordTypes")+"</span><br/>");
			String[] wtps = WordType.getTypeArr();
			for(String s : wtps){
				String tpname = WordType.getTypeName(s);
				out.println("<label><input type='checkbox' name='wt' value='"+s+"' "+(s.equalsIgnoreCase("ZEICH") ? "" : "checked='checked' " )+"/><span class='SUBJECT'>"+tpname+"</span></label><br/>");
			}
			out.println("</td><td>");
			out.println("<span class='SUBJECT'>"+Util.translateLabel("Subjects")+"</span><br/>");
			//right column: subjects
			for(String sid : Util.getSubjects()){
				String sname = Util.getSubjectName(sid);
				int tcnt = Util.getSubjectSentences(sid).size();
				out.println("<label><input type='checkbox' name='subj' value='"+sid+"' /><span class='SUBJECT'>"+sname+" ("+tcnt+")</span></label><br/>");
			}
			
			out.println("</td></tr></table>");
			
			out.println("<label><span class='SUBJECT'>"+Util.translateLabel("BlankRatio")+"</span><input type='number' min='0' max='100' step='1' name='brat' /></label><br/>");
			
			out.println("<input type='submit' value='"+Util.translateLabel("NewTest")+"' />");
			out.println("<input type='hidden' name='prvPg' value='param'>");
			out.println("</form>");
			out.println("</body></html>");
		} finally {
			out.close();  // Always close the output writer
		}
	}

}
