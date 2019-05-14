package test;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="setBean")
@SessionScoped
public class SettingsBean {

	public static List<String> CSS_FONT_FAMILIES = Arrays.asList("times","georgia","palatino","verdana","arial","comic","impact");
	public static List<String> CSS_FONT_STYLES = Arrays.asList("normal","italic","oblique");
	public static List<String> CSS_COLORS = Arrays.asList("black","red","green","blue");
	public static List<String> CSS_TEXT_TRANSFORMS = Arrays.asList("upper","lower","capital");

	private static String[] fontFamilies;
	private static String[] fontStyles;
	private static String[] colors;
	private static String[] textTransforms;

	public SettingsBean(){}

	public String[] getFontFamilies() {
		return fontFamilies;
	}
	public void setFontFamilies(String[] inFontFamilies) {
		SettingsBean.fontFamilies = inFontFamilies;
	}
	public List<String> getFontFamiliesList(){return SettingsBean.CSS_FONT_FAMILIES;}

	public String[] getFontStyles() {
		return fontStyles;
	}
	public void setFontStyles(String[] inFontStyles) {
		SettingsBean.fontStyles = inFontStyles;
	}
	public List<String> getFontStylesList(){return SettingsBean.CSS_FONT_STYLES;}

	public String[] getColors() {
		return colors;
	}
	public void setColors(String[] inColors) {
		/*String s = "";
		for(String q : inColors) s += q;
		System.out.println("setColors called -> "+s);*/
		SettingsBean.colors = inColors;
	}
	public List<String> getColorsList(){return SettingsBean.CSS_COLORS;}

	public String[] getTextTransforms() {
		return textTransforms;
	}
	public void setTextTransforms(String[] inTextTransforms) {
		SettingsBean.textTransforms = inTextTransforms;
	}
	public List<String> getTextTransformsList(){return SettingsBean.CSS_TEXT_TRANSFORMS;}

	public static String[] getFontFamilies2() {
		return fontFamilies;
	}
	public static String[] getFontStyles2() {
		return fontStyles;
	}
	public static String[] getColors2() {
		return colors;
	}
	public static String[] getTextTransforms2() {
		return textTransforms;
	}
}