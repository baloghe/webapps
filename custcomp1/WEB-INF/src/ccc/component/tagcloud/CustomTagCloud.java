package ccc.component.tagcloud;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;

import org.primefaces.component.tagcloud.TagCloud;


@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="components.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="core.js"),
	@ResourceDependency(library="primefaces", name="components.js")
})
//@FacesComponent(createTag = true, tagName = "customTagCloud", namespace = "http://custom.component/tags")
@FacesComponent(CustomTagCloud.COMPONENT_TYPE) //that's much enough combined with taglib.xml, see: https://stackoverrun.com/de/q/2796457
public class CustomTagCloud extends TagCloud {

	public static final String COMPONENT_TYPE = "ccc.component.tagcloud.CustomTagCloud";
	public static final String COMPONENT_FAMILY = "ccc.component";
	public static final String DEFAULT_RENDERER = "ccc.component.tagcloud.CustomTagCloudRenderer";

	public CustomTagCloud() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override 
    public String getFamily() { 
     return COMPONENT_FAMILY; 
    } 

    @Override 
    public String getRendererType() { 
     return DEFAULT_RENDERER; 
    } 
}