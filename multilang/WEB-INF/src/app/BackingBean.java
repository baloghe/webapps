package app;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

@ManagedBean(name="bBean")
@SessionScoped
public class BackingBean {
	
	private String[] selectedLangs;
	private String[] selectedThemes;
	
	private int cntSelectedLangs = 0;
	private int cntSelectedThemes = 0;
	
	private int               secondsRemaining;
	private QuestionVariant   actQuestion = null;
	private TagCloudModel     hintsCloud = new DefaultTagCloudModel();
	
	public BackingBean(){
		//load test
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");
		XMLProcessor.loadTest(realPath, "test.xml");
		this.secondsRemaining = -1;
	}
	
	//Language chooser
	public void setSelectedLangs(String[] inValues){
		this.selectedLangs = inValues;
	}
	
	public String[] getSelectedLangs(){
		return this.selectedLangs;
	}
	
	public Map<String, String> getLangsMap(){
		return XMLProcessor.getLangsMap();
	}
	
	public void languageChoiceChanged(){
		/*
		String sel = "";
		for(String s : selectedLangs){
			if(sel.length()==0)
				sel += s;
			else sel += (","+s);
		}
		System.out.println("Language choice changed, selected: " + (sel.length()==0 ? "Empty" : sel) );
		*/
		cntSelectedLangs = 0;
		for(String s : selectedLangs){
			if(s!=null && !s.equalsIgnoreCase(""))
				cntSelectedLangs++;
		}
	}
	
	//Theme chooser
	public void setSelectedThemes(String[] inValues){
		this.selectedThemes = inValues;
	}
	
	public String[] getSelectedThemes(){
		return this.selectedThemes;
	}
	
	public Map<String, String> getThemesMap(){
		return XMLProcessor.getThemesMap();
	}
	
	public void themeChoiceChanged(){
		/*
		String sel = "";
		for(String s : selectedThemes){
			if(sel.length()==0)
				sel += s;
			else sel += (","+s);
		}
		System.out.println("Theme choice changed, selected: " + (sel.length()==0 ? "Empty" : sel) );
		*/
		cntSelectedThemes = 0;
		for(String s : selectedThemes){
			if(s!=null && !s.equalsIgnoreCase(""))
				cntSelectedThemes++;
		}
	}
	
	public int getSecondsRemaining(){
		return secondsRemaining;
	}
	
	public void decrementTime(){
		if(secondsRemaining > 0){
			secondsRemaining--;
			//System.out.println("decrementTime :: " + secondsRemaining);
		} else {
			//System.out.println("decrementTime :: cntSelectedLangs=" + cntSelectedLangs + ", cntSelectedThemes=" + cntSelectedThemes);
			setNewQuestion();
		}
	}
	
	public String getActQuestion(){
		//System.out.println("getActQuestion called");
		if(actQuestion != null){
			return actQuestion.getText();
		} else return "";
	}
	
	public String getActHints(){
		System.out.println("getActHints called");
		if(actQuestion != null){
			return actQuestion.getHintsStr();
		} else return "";
	}
	
	private void refreshHintsCloud(){
		hintsCloud = new DefaultTagCloudModel();
		if(actQuestion!=null){
			Random r = new Random();
			for(String s : actQuestion.getHints()){
				hintsCloud.addTag(new DefaultTagCloudItem(s, r.nextInt(10)));
			}//next hint
		}//endif
	}
	
	public TagCloudModel getHintCloud(){return hintsCloud;}
	
	public void setNewQuestion(){
		if( cntSelectedLangs==0 || cntSelectedThemes==0 ){
			this.secondsRemaining = 0;
			this.actQuestion = null;
			return;
		} else {
			ArrayList<QuestionVariant> a = XMLProcessor.getQuestionVariants(selectedThemes, selectedLangs);
			if(a != null && a.size() > 0){
				Random r = new Random();
				this.actQuestion = a.get( r.nextInt( a.size() ) );
				this.secondsRemaining = this.actQuestion.getTimeSec();
			} else {
				this.actQuestion = null;
				this.secondsRemaining = 0;
			}
		}//lang and theme identified
		refreshHintsCloud();
	}
	
}
