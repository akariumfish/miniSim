package UI;

import java.util.ArrayList;

import sData.nRunnable;

public class nFrontPanel extends nWindowPanel {
	  
	  ArrayList<nRunnable> eventTab = new ArrayList<nRunnable>();
	  public nFrontPanel addEventTab(nRunnable r)       { eventTab.add(r); return this; }
	  
	  nFrontPanel setNonClosable() { closer.setText("").setBackground(); return this; }
	  public nFrontTab getTab(int n) { return tabs.get(n); }
	  ArrayList<nFrontTab> tabs = new ArrayList<nFrontTab>();
	  ArrayList<nWidget> tab_widgets = new ArrayList<nWidget>();
	  nFrontTab current_tab;
	  public int current_tab_id = 0;
	  public nFrontTab addTab(String n) {
	    nFrontTab tab = new nFrontTab(this, n);
	    tab.id = tabs.size();
	    tabs.add(tab);
	    tab.panel.setParent(panel)
	      .stackDown()
	      ;
	    float new_width = grabber.getLocalSX() / (tab_widgets.size() + 1);
	    nWidget tabbutton = addModel("Button-SS3");
	    tabbutton.setSwitch().setText(n)
	      .setSX(new_width)
	      .setFont((int)(ref_size/2))
	      .addEventSwitchOn(new nRunnable(tab) { public void run() {
	        for (nFrontTab t : tabs) t.hide();
	        current_tab = ((nFrontTab)builder);
	        current_tab.show();
	        current_tab.toLayerTop();
	        current_tab_id = current_tab.id;
	        runEvents(current_tab.eventOpen);
	        runEvents(eventTab);
	      } } )
	      ;
	    for (nWidget w : tab_widgets) { 
	      w.setSX(new_width); 
	      tabbutton.addExclude(w); w.addExclude(tabbutton); }
	    if (tab_widgets.size() > 0) tabbutton.setParent(tab_widgets.get(tab_widgets.size()-1)).stackRight();
	    else tabbutton.setParent(grabber).stackDown();
	    tab_widgets.add(tabbutton);
	    tab.tabbutton = tabbutton;
	    panel.setParent(tab_widgets.get(0));
	    
	    tabbutton.setOn();
	    
	    float moy_leng = 0;
	    for (nWidget w : tab_widgets) moy_leng += w.getText().length();
	    moy_leng /= tab_widgets.size();
	    for (nWidget w : tab_widgets) w.setSX(w.getLocalSX() * w.getText().length() / moy_leng);
	    
	    for (nFrontTab ot : tabs) ot.hide();
	    tab.show();
	    return tab;
	  }
	  
	  public nFrontPanel(nGUI _g, nTaskPanel _task, String _name) { 
	    super(_g, _task, _name); 
	    panel.setSY(0).setOutline(false);
	    gui.addEventSetup(new nRunnable() { public void run() {
	      if (tab_widgets.size() > 0) tab_widgets.get(0).setOn();
	    } });
	  } 
	  public void setTab(int i) { 
	    if (!collapsed && i < tab_widgets.size()) tab_widgets.get(i).setOn();
	  }
	  public void collapse() { 
	    super.collapse(); 
	  }
	  public void popUp() { 
	    boolean p = collapsed;
	    super.popUp(); 
	    for (nFrontTab t : tabs) t.hide();
	    if (current_tab != null) {
	      current_tab.show();
	      if (p) nRunnable.runEvents(current_tab.eventOpen); 
	    }
	  }
	  public nFrontPanel toLayerTop() { 
	    super.toLayerTop(); 
	    for (nFrontTab d : tabs) d.toLayerTop(); 
	    return this;
	  }
	  public nFrontPanel clear() { 
	    for (nFrontTab d : tabs) d.clear();
	    super.clear(); return this; }
	  public nFrontPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nFrontPanel updateWidth() { 
	    super.updateWidth(); 
	    if (current_tab != null && current_tab.panel.getLocalSX() != grabber.getLocalSX()) 
	    grabber.setSX(current_tab.panel.getLocalSX());
	    
	    //is tabs hhave different width verify tabs width follow correctly
	    if (grabber != null && tab_widgets != null) {
	      float new_width = grabber.getLocalSX() / (tab_widgets.size());
	      for (nWidget w : tab_widgets) w.setSX(new_width); 
	      float moy_leng = 0;
	      for (nWidget w : tab_widgets) moy_leng += w.getText().length();
	      moy_leng /= tab_widgets.size();
	      for (nWidget w : tab_widgets) w.setSX(w.getLocalSX() * w.getText().length() / moy_leng);
	    }
	    //current_tab.updateWidth(); 
	    //logln("frontpanel " + panel.getLocalSX()); 
	    
	    return this; }
	}
