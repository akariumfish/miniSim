package UI;

import java.util.ArrayList;

import RApplet.RConst;
import processing.core.PApplet;
import sData.nRunnable;





public class nBetterList extends nDrawer {
	
	class WidgetCheat {
		int index = 0; nWidget self;
		boolean bool = false;
		WidgetCheat(int i, nWidget w) { index = i; self = w; }
		WidgetCheat() {}
	}
	
	public class nListEntry { 
		Object builder;
		int index = 0; 
		boolean is_selectable = false, has_option = false, option_state;
		String title, info = "";
		nRunnable click_run = null, option_run = null, option_swtch_run = null;
		public nListEntry setBuilder(Object o) {
			builder = o; return this; }
		
		public nListEntry setSelectable() {
			is_selectable = true; return this; }
		public nListEntry setSelectable(nRunnable r) {
			is_selectable = true; click_run = r; return this; }
		public nListEntry setSelectable(String inf, nRunnable r) {
			is_selectable = true; click_run = r; info = inf; return this; }
		public nListEntry setOption() {
			has_option = true; return this; }
		public nListEntry setOption(nRunnable r) {
			has_option = true; option_run = r; return this; }
		public nListEntry setOption(boolean b, String inf, nRunnable r) {
			has_option = true; option_state = b; option_run = r; info = inf; return this; }
	}
	
	  ArrayList<nListEntry> complexe_entrys = new ArrayList<nListEntry>();

	  public nBetterList setEntrys(ArrayList<String> l) {
		  start_complexe_entry();
		  for (String s : l) 
			  new_comp_entry(RConst.copy(s)).setSelectable();
		  end_complexe_entry();
		  return this;
	  }
	  public nBetterList start_complexe_entry() {
		  complexe_entrys.clear();
		  return this;
	  }
	  public nListEntry new_comp_entry(String t) {
		  nListEntry le = new nListEntry();
		  le.title = RConst.copy(t);
		  le.index = complexe_entrys.size();
		  complexe_entrys.add(le);
		  return le;
	  }
	  public nBetterList end_complexe_entry() {
	    for (int i = 0 ; i < listoptions.size() ; i++) listoptions.get(i).clear();
	    listoptions.clear();
	    for (int i = 0 ; i < listwidgets.size() ; i++) listwidgets.get(i).clear();
	    listwidgets.clear();
	    for (int i = 0 ; i < list_widget_nb ; i++) {
	      nWidget ne = gui.theme.newWidget(gui, "List_Entry").setSize(larg - item_s, item_s)
	        .stackDown()
	        .setTextAlignment(align, PApplet.CENTER);
	      ne.addEventVisibilityChange(new nRunnable(ne) { public void run() {
	        	if (!ref.isHided()) 
	        		((nWidget)builder).show();
	        	else ((nWidget)builder).hide();
	      }}).addEventSwitchOn_Builder(new nRunnable() { public void run() {
	          if (last_choice_widget != null && last_choice_widget != ((nWidget)builder)) 
	            last_choice_widget.setLook(gui.theme, "List_Entry");
	          ((nWidget)builder).setLook(gui.theme, "List_Entry_Selected");
	          click();
	        }})
	        ;
	      if (listwidgets.size() > 0) ne.setParent(listwidgets.get(listwidgets.size()-1)); else ne.setParent(back);
	      listwidgets.add(ne);
	      nWidget ne_op = gui.theme.newWidget(gui, "Button_Check")
	    	.setSize(item_s*0.8, item_s*0.8).setPosition(larg - item_s*2, item_s*0.1);
	      WidgetCheat cheat = new WidgetCheat(i, ne_op);
	      ne_op.addEventSwitchOn(new nRunnable(cheat) { public void run() {
	        	int i = ((WidgetCheat)builder).index + entry_pos;
	        	if (!is_updating && i < complexe_entrys.size() && complexe_entrys.get(i).has_option && 
	        			complexe_entrys.get(i).option_run != null) {
	        		complexe_entrys.get(i).option_state = true;
	        		complexe_entrys.get(i).option_run.run(); 
	        		}
  	        }}).addEventSwitchOff(new nRunnable(cheat) { public void run() {
	        	int i = ((WidgetCheat)builder).index + entry_pos;
	        	if (!is_updating && i < complexe_entrys.size() && complexe_entrys.get(i).has_option && 
	        			complexe_entrys.get(i).option_run != null)  {
	        		complexe_entrys.get(i).option_state = false;
	        		complexe_entrys.get(i).option_run.run(); 
	        		}
  	        }}).addEventVisibilityChange(new nRunnable(cheat) { public void run() {
  	        	int i = ((WidgetCheat)builder).index + entry_pos;
  	        	if (i < complexe_entrys.size() && 
  	        		complexe_entrys.get(i).has_option && 
  	        		!ref.isHided()) 
  	        		((WidgetCheat)builder).self.show();
  	        	else ((WidgetCheat)builder).self.hide();
  	        }}).setSwitch().hide()
  	        ;
	      ne_op.setParent(ne);
	      if (!ref.isHided()) ne.show();
	      else ne.hide();
  	      listoptions.add(ne_op);
	    }
	    for (nWidget w : listwidgets) w.toLayerTop();
	    for (nWidget w : listoptions) w.toLayerTop();
	    
	    scroll.setPos(0);
	    scroll.setEntryNb(complexe_entrys.size());
	    scroll.setView(list_widget_nb);
	    entry_pos = 0;
	    update_list();
	    for (int i = 0 ; i < list_widget_nb ; i++) 
	      if (i < complexe_entrys.size()) listwidgets.get(i).setSwitch();
	      else listwidgets.get(i).setBackground();
	    unselect();
	    return this;
	  }
	  
	  void click() {
	    if (event_active) {
	      int i = 0;
	      for (nWidget w : listwidgets) {
	        if (w.isOn()) {
	          w.setOff();
	          break; }
	        i++; }
	      last_choice_index = i+entry_pos;
	      last_choice_widget = listwidgets.get(i);
	  	  if (last_choice_index < complexe_entrys.size() && 
	  			  complexe_entrys.get(last_choice_index).click_run != null) 
	  		complexe_entrys.get(last_choice_index).click_run.run();
	  	
	      nRunnable.runEvents(eventChangeRun);
	    }
	  }
	  void unselect() { last_choice_index = -1; update_list(); }
	  
	  void update_list() {
	    last_choice_widget = null;
  	  	is_updating = true;
	    for (int i = 0 ; i < list_widget_nb ; i++) {
		      nWidget w = listwidgets.get(i);
		      nWidget wo = listoptions.get(i);
	      
	      if (i + entry_pos == last_choice_index) { 
	        w.setLook(gui.theme, "List_Entry_Selected"); 
	        last_choice_widget = w; }
	      else w.setLook(gui.theme, "List_Entry");
	      if (i + entry_pos < complexe_entrys.size()) {
	    	  nListEntry le = complexe_entrys.get(i + entry_pos);
	    	  w.setText(le.title); 
	    	  if (le.is_selectable) w.setSwitch();
		      else w.setBackground();
	    	  if (le.has_option) wo.show();
		      else wo.hide();
	    	  if (le.option_state) wo.setOn();
		      else wo.setOff();
	    	  w.setInfo(le.info);
	      } else {
	    	  w.setText("");
	      }
	    }
  	  	is_updating = false;
	  }
	  
	  boolean is_updating = false;
	  
	  ArrayList<nWidget> listwidgets = new ArrayList<nWidget>();
	  ArrayList<nWidget> listoptions = new ArrayList<nWidget>();
	  
	  nWidget back, last_choice_widget;
	  nScroll scroll;
	  float item_s;
	  float larg;
	  int list_widget_nb = 5;
	  int entry_pos = 0;
	  boolean event_active = true;
	  public int last_choice_index = -1;
	  
	  ArrayList<nRunnable> eventChangeRun = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventScrollRun = new ArrayList<nRunnable>();
	  public nBetterList addEventScroll(nRunnable r)       { eventScrollRun.add(r); return this; }
	  public nBetterList addEventChange(nRunnable r)       { eventChangeRun.add(r); return this; }
	  public nBetterList addEventChange_Builder(nRunnable r) { eventChangeRun.add(r); r.builder = this; return this; }
	  
	  int layer = 0;
	  
	  public nBetterList toLayerTop() {
	    super.toLayerTop();
	    back.toLayerTop();
	    for (nWidget w : listwidgets) w.toLayerTop();
	    for (nWidget w : listoptions) w.toLayerTop();
	    scroll.toLayerTop();
	    return this;
	  }
	  public nBetterList clear() {
	    super.clear();
	    scroll.clear();
	    for (nWidget w : listwidgets) w.clear();
	    for (nWidget w : listoptions) w.clear();
	    back.clear();
	    return this;
	  }
	  int align = PApplet.CENTER;
	  public nBetterList setTextAlign(int a) { align = a; end_complexe_entry(); return this; }
	  nBetterList(nShelf _sh, int _ent_nb, float _rs, float _lf, float _hf) {
	    super(_sh, _rs*_lf, _rs*_hf*_ent_nb);
	    list_widget_nb = _ent_nb;
	    back = new nWidget(gui, 0, 0);
	    back.setParent(ref)
	      .addEventFrame(new nRunnable() { public void run() {
	        if (!back.isHided()) {
	          for (nWidget w : listwidgets) { 
	            if (w.isHovered() && gui.in.mouseWheelUp) {
	              scroll.go_down();
	            }
	            if (w.isHovered() && gui.in.mouseWheelDown) {
	              scroll.go_up();
	            }
	          }
	        }
	      }});
	    item_s = ref_size*_hf; larg = ref_size*_lf;
	    
	    scroll = new nScroll(gui, larg - item_s, 0, item_s, item_s*list_widget_nb);
	    scroll.getRefWidget().setParent(back);
	    scroll.setView(list_widget_nb)
	      .addEventChange(new nRunnable() { public void run() {
	        entry_pos = scroll.entry_pos;
	        update_list();
	        runEvents(eventScrollRun);
	      }});
	    end_complexe_entry();
	  }
	  
	  nBetterList setItemSize(float l) {
	    item_s = l;
	    scroll.getRefWidget().setPosition(larg - item_s, 0);
	    scroll.setWidth(item_s); scroll.setHeight(item_s*list_widget_nb);
	    for (nWidget w : listwidgets) w.setSize(larg - item_s, item_s);
	    for (nWidget w : listoptions) 
	    	w.setSize(item_s/2, item_s/2).setPosition(larg - item_s*2, item_s/4);
	    return this;
	  }
	  nBetterList setWidth(float l) {
	    larg = l;
	    scroll.getRefWidget().setPosition(larg - item_s, 0);
	    scroll.setWidth(item_s); scroll.setHeight(item_s*list_widget_nb);
	    for (nWidget w : listwidgets) w.setSize(larg - item_s, item_s);
	    for (nWidget w : listoptions) 
	    	w.setSize(item_s*0.8, item_s*0.8).setPosition(larg - item_s*2, item_s*0.1);
	    return this;
	  }
	}
