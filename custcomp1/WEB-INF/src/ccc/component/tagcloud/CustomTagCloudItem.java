package ccc.component.tagcloud;

import org.primefaces.model.tagcloud.DefaultTagCloudItem;

public class CustomTagCloudItem extends DefaultTagCloudItem {

	private String fontFamilyAlias;
	private String fontStyleAlias;
	private String colorAlias;
	private String textTransformAlias;

	public CustomTagCloudItem() {
		super();
	}

	public CustomTagCloudItem(String label, int strength) {
	    super(label, strength);
	}

	public CustomTagCloudItem(String label, String url, int strength) {
	    super(label, url, strength);
	}

	public String getFontFamilyAlias() {
		return fontFamilyAlias;
	}

	public void setFontFamilyAlias(String fontFamilyAlias) {
		this.fontFamilyAlias = fontFamilyAlias;
	}

	public String getFontStyleAlias() {
		return fontStyleAlias;
	}

	public void setFontStyleAlias(String fontStyleAlias) {
		this.fontStyleAlias = fontStyleAlias;
	}

	public String getColorAlias() {
		return colorAlias;
	}

	public void setColorAlias(String colorAlias) {
		this.colorAlias = colorAlias;
	}

	public String getTextTransformAlias() {
		return textTransformAlias;
	}

	public void setTextTransformAlias(String textTransformAlias) {
		this.textTransformAlias = textTransformAlias;
	}

	public String attributesToString(){
		return "(label="+getLabel()+",strength="+getStrength()+", fontFamily="+fontFamilyAlias+", fontStyle="+fontStyleAlias+", color="+colorAlias+", textTransform="+textTransformAlias+")";
	}
}