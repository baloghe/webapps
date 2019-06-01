package cc;

import java.util.Random;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIOutput;

@FacesComponent("cc.FindZeroComponent")
public class FindZeroComponent extends UIOutput
                               implements NamingContainer {

	public FindZeroComponent(){
		super();
		setValue(1);
	}

	@Override
	public String getFamily() {
		return("javax.faces.NamingContainer");
	}

	public Integer getValue(){
		return (Integer) getStateHelper().eval("value");
	}

	public void setValue(Integer value){
		getStateHelper().put("value", value);
	}

	public Integer getRandomRange(){
		return (Integer) getStateHelper().eval("randomRange");
	}

	public void setRandomRange(Integer value){
		getStateHelper().put("randomRange", value);
	}

	private int getRandomInRange(){
		int rr = getRandomRange();
		return 1 + (new Random()).nextInt(rr);
	}

	public void increase(){
		int newVal = getValue() + getRandomInRange();
		setValue(newVal);
	}

	public void decrease(){
		int newVal = getValue() - getRandomInRange();
		setValue(newVal);
	}

	public void newGame(){
		setValue(getRandomInRange());
	}
}