package cc;
 
import java.io.IOException;
 
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
 
@FacesComponent("cc.FlipCardComponent2")
public class FlipCardComponent2 extends UIOutput
                                implements NamingContainer {
              
               @Override
    public void encodeAll(FacesContext context) throws IOException {
                              super.encodeAll(context);
                              register();
               }
              
               @Override
               public String getFamily() {
                              return("javax.faces.NamingContainer");
               }
              
               private void register(){
                              FacesContext context = FacesContext.getCurrentInstance();
                              ELContext elContext = context.getELContext();
                             
                              Object params[] = new Object[1];
                              params[0] = getId();
                              System.out.println("register :: params=(" + params[0] + ")");
                             
                              MethodExpression me = (MethodExpression)this.getAttributes().get("registerTo");
                              //if(me==null) System.out.println("me==null for id=" + params[0]);
                              me.invoke(elContext, params);                 
               }
              
}