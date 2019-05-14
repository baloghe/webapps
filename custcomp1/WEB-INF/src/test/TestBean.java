package test;

import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

import ccc.component.tagcloud.CustomTagCloudItem;

@ManagedBean(name="bBean")
@SessionScoped
public class TestBean {

	public static String[] CLOUD_WORDS = new String[]{"What","would","You","make","out","of","this"};

	private DefaultTagCloudModel hintsCloud;

	public TestBean(){
		refreshHintsCloud();
	}

	public TagCloudModel getHintCloud(){
		//System.out.println("getHintCloud called"); 
		return hintsCloud;
	}

	public void refreshHintsCloud(){
		//System.out.println("refreshHintsCloud called");
		hintsCloud = new DefaultTagCloudModel();
		Random r = new Random();
		for(String s : CLOUD_WORDS){
			CustomTagCloudItem i = new CustomTagCloudItem(s, r.nextInt(5));
			i.setColorAlias( this.getSetting(SettingsBean.getColors2(), "black") );
			i.setFontStyleAlias( this.getSetting(SettingsBean.getFontStyles2(), "normal") );
			i.setFontFamilyAlias( this.getSetting(SettingsBean.getFontFamilies2(), null) );
			i.setTextTransformAlias( this.getSetting(SettingsBean.getTextTransforms2(), null) );
			hintsCloud.addTag(i);
			//System.out.println("item added: " + i.attributesToString());
		}
	}

	private String getSetting(String[] inOptions, String inDefault){
		if(inOptions==null || inOptions.length==0){
			//System.out.println("getSetting :: got null / 0 => default value=" + inDefault);
			return inDefault;
		} else if(inOptions.length==1){
			return inOptions[0];
		} else {
			Random r = new Random();
			return inOptions[r.nextInt(inOptions.length)];
		}
	}
}