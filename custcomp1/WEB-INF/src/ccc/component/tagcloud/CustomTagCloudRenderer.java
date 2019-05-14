package ccc.component.tagcloud;

/**
 * Code found at and copied from:
 * https://jar-download.com/artifacts/org.primefaces/primefaces/5.0/source-code/org/primefaces/component/tagcloud/TagCloudRenderer.java
 * on:
 * 08-MAY-2019
 * */

/* extends javax.faces.render.Renderer -> already out of Primefaces 
 * => need to include javax.faces.jar in the build path! */
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.tagcloud.TagCloud;
import org.primefaces.component.tagcloud.TagCloudRenderer;
import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;

@FacesRenderer(componentFamily = CustomTagCloud.COMPONENT_FAMILY, rendererType = CustomTagCloudRenderer.RENDERER_TYPE)
public class CustomTagCloudRenderer extends TagCloudRenderer {

	public static final String RENDERER_TYPE = "ccc.component.tagcloud.CustomTagCloudRenderer";

	@Override
	protected void encodeMarkup(FacesContext context, TagCloud tagCloud) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TagCloudModel model = tagCloud.getModel();
        String styleClass = tagCloud.getStyleClass();
        String style = tagCloud.getStyle();
        styleClass = styleClass == null ? TagCloud.STYLE_CLASS : TagCloud.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", tagCloud);
        writer.writeAttribute("id", tagCloud.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }


        writer.startElement("ul", null);

        for(TagCloudItem item : model.getTags()) {
            String url = item.getUrl();
            String href = url == null ? "#" :  getResourceURL(context, item.getUrl());

            writer.startElement("li", null);

            String classes = "ui-tagcloud-strength-" + item.getStrength();

            /* include Custom properties when possible */
            if( item instanceof CustomTagCloudItem){
            	CustomTagCloudItem c = (CustomTagCloudItem)item;            	
            	if(c.getFontFamilyAlias() != null) classes += (" ccc-font-family-" + c.getFontFamilyAlias());
            	if(c.getFontStyleAlias() != null) classes += (" ccc-font-style-" + c.getFontStyleAlias());
            	if(c.getColorAlias() != null) classes += (" ccc-color-" + c.getColorAlias());
            	if(c.getTextTransformAlias() != null) classes += (" ccc-text-transform-" + c.getTextTransformAlias());
            }

            //System.out.println("encodeMarkup :: class=" + classes);

            writer.startElement("a", null);
            writer.writeAttribute("href", href, null);

            //instead of writing the label directly into the link,
            //we surround it with a span in order to assign our classes directly
            writer.startElement("span", null);
            writer.writeAttribute("class", classes, null);
            writer.writeText(item.getLabel(), null);
            writer.endElement("span");

            writer.endElement("a");

            writer.endElement("li");
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

}