package cc;

import java.util.Random;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.el.ELContext;
import javax.el.MethodExpression;

@FacesComponent("cc.FindZeroComponent2")
public class FindZeroComponent2 extends UIOutput
                               implements NamingContainer {

	public FindZeroComponent2(){
		super();
		setValue(1);
		getStateHelper().put("counter", 0);
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
		incCounter();
	}

	public void decrease(){
		int newVal = getValue() - getRandomInRange();
		setValue(newVal);
		incCounter();
	}

	public void newGame(){
		//call managed bean
		updateMB();
		//reset component
		setValue(getRandomInRange());
		getStateHelper().put("counter", 0);
	}

	private void updateMB(){
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();

		Object params[] = new Object[2];
		params[0] = getId();
		params[1] = (Integer) getStateHelper().eval("counter");
		System.out.println("updateMB :: params=(" + params[0] + ", " + params[1] + ")");

		MethodExpression me = (MethodExpression)this.getAttributes().get("gameFinished");
		me.invoke(elContext, params);
	}

	private void incCounter(){
		int cnt = 1 + (Integer) getStateHelper().eval("counter");
		getStateHelper().put("counter", cnt);
	}
}