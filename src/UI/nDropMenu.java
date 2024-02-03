package UI;

import java.util.ArrayList;

import processing.core.PApplet;
import sData.nRunnable;

public class nDropMenu extends nBuilder {
	  
	  nDropMenu drop(nWidget op, float x, float y) { 
	    opener = op; 
	    ref.setPosition(x, y).show(); 
	    for (nWidget w : menu_widgets) w.toLayerTop();
	    toLayerTop();
	    return this; }
	  nDropMenu drop(nGUI g) { 
	    float p_x = g.mouseVector.x - larg/2;
	    float p_y = g.mouseVector.y;
	    if (!down) p_y += haut/2; else p_y -= haut/4; 
	    float total_haut = haut*menu_widgets.size();
	    
	    if (p_x + larg > g.view.pos.x + g.view.size.x) p_x = g.view.pos.x + g.view.size.x - larg;
	    if (p_x < g.view.pos.x) p_x = g.view.pos.x;
	    if (down && p_y + total_haut > g.view.pos.y + g.view.size.y) 
	      p_y = g.view.pos.y + g.view.size.y - total_haut;
	    if (!down && p_y - total_haut < g.view.pos.y) p_y += g.view.pos.y - (p_y - total_haut);
	    
	    ref.setPosition(p_x, p_y).show(); 
	    for (nWidget w : menu_widgets) w.toLayerTop();
	    toLayerTop(); return this; }
	  nDropMenu close() { 
	    ref.hide();
	    return this; }
	  public nDropMenu clear() { super.clear(); events.clear(); return this; }
	  nWidget ref, opener;
	  ArrayList<nWidget> menu_widgets = new ArrayList<nWidget>();
	  ArrayList<Runnable> events = new ArrayList<Runnable>();
	  int layer = 20;  float haut, larg;  boolean down, ephemere = false;
	  
	  nDropMenu(nGUI _gui, float ref_size, float width_factor, boolean _down, boolean _ephemere) {
	    super(_gui, ref_size);
	    haut = ref_size; larg = haut*width_factor; down = _down; ephemere = _ephemere;
	    ref = addModel("ref").stackRight()
	      .addEventFrame(new nRunnable() { public void run() { 
	        boolean t = false;
	        for (nWidget w : menu_widgets) t = t || w.isHovered();
	        if (opener != null) t = t || opener.isHovered();
	        if ((gui.in.getClick("MouseLeft") || ephemere) && !t) close();
	      } });
	    if (!down) ref.stackUp(); 
	  }
	  void click() {
	    int i = 0;
	    for (nWidget w : menu_widgets) {
	      if (w.isOn()) { w.setOff(); break; }
	      i++; }
	    events.get(i).run();
	    ref.hide();
	  }
	  nWidget addEntry(String l, Runnable r) {
	    nWidget ne = new nWidget(gui, l, (int)(haut/1.5F), 0, 0, larg, haut)
	      .setSwitch() 
	      .setLayer(layer)
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      .setHoverablePhantomSpace(ref_size / 4)
	      .addEventSwitchOn(new nRunnable() { public void run() { click(); }}) 
	      ;
	     if (!down) ne.stackUp(); else ne.stackDown();
	    if (menu_widgets.size() > 0) ne.setParent(menu_widgets.get(menu_widgets.size()-1)); 
	    else ne.setParent(ref);
	    menu_widgets.add(ne);
	    events.add(r);
	    return ne;
	  }
	  nCtrlWidget addEntry(String l) {
	    nCtrlWidget ne = new nCtrlWidget(gui);
	    ne.setText(l)
	      .setFont((int)(haut/1.5))
	      .setSize(larg, haut)
	      .setHoverablePhantomSpace(ref_size / 4)
	      //.addEventSwitchOn(new Runnable() { public void run() { click(); }}) 
	      ;
	    if (!down) ne.stackUp(); else ne.stackDown();
	    if (menu_widgets.size() > 0) ne.setParent(menu_widgets.get(menu_widgets.size()-1)); 
	    else ne.setParent(ref);
	    menu_widgets.add(ne);
	    events.add(new Runnable() { public void run() { }});
	    return ne;
	  }
	}
