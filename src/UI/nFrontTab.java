package UI;

import java.util.ArrayList;

import sData.nRunnable;

public class nFrontTab extends nShelfPanel {
	  public nFrontPanel getFront() { return front; }
	  
	  ArrayList<nRunnable> eventOpen = new ArrayList<nRunnable>();
	  nFrontTab addEventOpen(nRunnable r)       { eventOpen.add(r); return this; }
	  
	  public nFrontTab toLayerTop() { 
	    super.toLayerTop(); 
	    
	    return this;
	  }
	  nFrontTab show() {
	    panel.show();
	    front.grabber.setSX(panel.getLocalSX()); 
	    toLayerTop();
	    return this; }
	  
	  nFrontTab hide() {
	    panel.hide();
	    
	    return this; }
	  
	  nFrontPanel front;
	  String name;
	  nWidget tabbutton;
	  int id = 0;
	  nFrontTab(nFrontPanel _front, String ti) { 
	    super(_front.gui, _front.ref_size, _front.space_factor); 
	    front = _front;
	    name = ti;
	    fronttab = this;
	    addShelf().addDrawer((front.grabber.getLocalSX() / front.ref_size) - 2*front.space_factor, 0);
	  } 
	  public nFrontTab clear() { 
	    tabbutton.clear();
	    eventOpen.clear();
	    super.clear(); return this; }
	  public nFrontTab updateHeight() { 
	    
	    super.updateHeight(); return this; }
	  public nFrontTab updateWidth() { 
	    super.updateWidth(); 
	    front.grabber.setSX(max_width);
	    panel.setSX(max_width); front.updateWidth(); 
	    //logln("tab "+name+" : front.grab " + front.grabber.getLocalSX()); 
	    
	    
	    float new_width = front.grabber.getLocalSX() / (front.tab_widgets.size());
	    for (nWidget w : front.tab_widgets) w.setSX(new_width); 
	    float moy_leng = 0;
	    for (nWidget w : front.tab_widgets) moy_leng += w.getText().length();
	    moy_leng /= front.tab_widgets.size();
	    for (nWidget w : front.tab_widgets) w.setSX(w.getLocalSX() * w.getText().length() / moy_leng);
	    
	    
	    
	    return this; }
	}
