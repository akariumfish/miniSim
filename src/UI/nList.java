package UI;

import java.util.ArrayList;

import RApplet.RConst;
import processing.core.PApplet;
import sData.nRunnable;

public class nList extends nDrawer {
	  
	  ArrayList<nWidget> listwidgets = new ArrayList<nWidget>();
	  ArrayList<String> entrys = new ArrayList<String>();
	  nWidget back, last_choice_widget;
	  nScroll scroll;
	  float item_s;
	  float larg;
	  int list_widget_nb = 5;
	  int entry_pos = 0;
	  boolean event_active = true;
	  public int last_choice_index = -1;
	  String last_choice_text = null;
	  
	  ArrayList<nRunnable> eventChangeRun = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventScrollRun = new ArrayList<nRunnable>();
	  public nList addEventScroll(nRunnable r)       { eventScrollRun.add(r); return this; }
	  public nList addEventChange(nRunnable r)       { eventChangeRun.add(r); return this; }
	  public nList addEventChange_Builder(nRunnable r) { eventChangeRun.add(r); r.builder = this; return this; }
	  
//	  public nWidget getRefWidget() { return back; }
	  
	  int layer = 0;
	  
//	  public nList setLayer(int l) {
//	    super.setLayer(l); 
//	    layer = l;
//	    scroll.setLayer(l);
//	    back.setLayer(l);
//	    for (nWidget w : listwidgets) w.setLayer(l);
//	    for (nWidget w : listoptions) w.setLayer(l);
//	    return this;
//	  }
	  public nList toLayerTop() {
	    super.toLayerTop();
	    back.toLayerTop();
	    for (nWidget w : listwidgets) w.toLayerTop();
	    scroll.toLayerTop();
	    return this;
	  }
	  public nList clear() {
	    super.clear();
	    scroll.clear();
	    for (nWidget w : listwidgets) w.clear();
	    back.clear();
	    return this;
	  }
	  int align = PApplet.CENTER;
	  public nList setTextAlign(int a) { align = a; setListLength(list_widget_nb); return this; }
	  nList(nShelf _sh, int _ent_nb, float _rs, float _lf, float _hf) {
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
	    setListLength(_ent_nb);
	    
	  }
	  
	  void click() {
	    if (event_active) {
	      int i = 0;
	      for (nWidget w : listwidgets) {
	        if (w.isOn()) {
	          w.setOff();
	          break;
	        }
	        i++;
	      }
	      last_choice_index = i+entry_pos;
	      last_choice_text = RConst.copy(listwidgets.get(i).getText());
	      last_choice_widget = listwidgets.get(i);
	      nRunnable.runEvents(eventChangeRun);
	    }
	  }
	  void unselect() { last_choice_index = -1; last_choice_text = ""; update_list(); }
	  void update_list() {
	    last_choice_widget = null;
	    for (int i = 0 ; i < list_widget_nb ; i++) {
	      nWidget w = listwidgets.get(i);
	      if (i + entry_pos == last_choice_index) { 
	        w.setLook(gui.theme, "List_Entry_Selected"); 
	        last_choice_widget = w; }
	      else w.setLook(gui.theme, "List_Entry");
	      if (i + entry_pos < entrys.size()) w.setText(entrys.get(i + entry_pos)); else w.setText("");
	    }
	  }
	  public nList setEntrys(ArrayList<String> l) {
	    entrys.clear();
	    for (String s : l) entrys.add(RConst.copy(s));
	    scroll.setPos(0);
	    scroll.setEntryNb(l.size());

	    scroll.setView(list_widget_nb);
	    entry_pos = 0; 
	    for (int i = 0 ; i < list_widget_nb ; i++) 
	      if (i < entrys.size()) listwidgets.get(i).setSwitch();
	      else listwidgets.get(i).setBackground();
	    unselect();
	    return this;
	  }
	  nList setListLength(int l) {
	    for (int i = 0 ; i < listwidgets.size() ; i++) listwidgets.get(i).clear();
	    listwidgets.clear();
	    list_widget_nb = l;
	    for (int i = 0 ; i < list_widget_nb ; i++) {
	      nWidget ne = gui.theme.newWidget(gui, "List_Entry").setSize(larg - item_s, item_s)
	        .stackDown()
	        .setTextAlignment(align, PApplet.CENTER)
	        .addEventSwitchOn_Builder(new nRunnable() { public void run() {
	          if (last_choice_widget != null && last_choice_widget != ((nWidget)builder)) 
	            last_choice_widget.setLook(gui.theme, "List_Entry");
	          ((nWidget)builder).setLook(gui.theme, "List_Entry_Selected");
	          click();
	        }})
	        ;
	      if (listwidgets.size() > 0) ne.setParent(listwidgets.get(listwidgets.size()-1)); else ne.setParent(back);
	      listwidgets.add(ne);
	    }
	    for (nWidget w : listwidgets) w.toLayerTop();
	    
	    scroll.setPos(0);
	    scroll.setEntryNb(entrys.size());
	    scroll.setView(list_widget_nb);
	    entry_pos = 0;
	    update_list();
	    return this;
	  }
	  nList setItemSize(float l) {
	    item_s = l;
	    scroll.getRefWidget().setPosition(larg - item_s, 0);
	    scroll.setWidth(item_s); scroll.setHeight(item_s*list_widget_nb);
	    for (nWidget w : listwidgets) w.setSize(larg - item_s, item_s);
	    return this;
	  }
	  nList setWidth(float l) {
	    larg = l;
	    scroll.getRefWidget().setPosition(larg - item_s, 0);
	    scroll.setWidth(item_s); scroll.setHeight(item_s*list_widget_nb);
	    for (nWidget w : listwidgets) w.setSize(larg - item_s, item_s);
	    return this;
	  }
	}






	class nScroll {
	  nGUI gui;
	  nWidget up, down, back, curs;
	  float larg = 60;
	  float haut = 200;
	  int entry_nb = 1;
	  int entry_pos = 0;
	  int entry_view = 1;
	  
	  ArrayList<nRunnable> eventChangeRun = new ArrayList<nRunnable>();
	  nScroll addEventChange(nRunnable r)       { eventChangeRun.add(r); return this; }
	  nScroll removeEventChange(nRunnable r)       { eventChangeRun.remove(r); return this; }
	  
	  nScroll setEntryNb(int v) { entry_nb = v; update_cursor(); return this; }
	  nScroll setView(int v) { entry_view = v; update_cursor(); return this; }
	  nScroll setPos(int v) { entry_pos = v; update_cursor(); return this; }
	  
	  nScroll setHeight(float h) { haut = h; back.setSY(h); update_cursor(); return this; }
	  nScroll setWidth(float w) { 
	    larg = w; back.setSX(w); up.setSize(w, w); down.setSize(w, w); curs.setSX(w);
	    up.setOutlineWeight(w / 16).setFont((int)(w/1.5));
	    down.setOutlineWeight(w / 16).setFont((int)(w/1.5));
	    curs.setOutlineWeight(w / 16).setFont((int)(w/1.5));
	    update_cursor(); return this; }
	  
	  nWidget getRefWidget() { return back; }
	  
	  int layer = 0;
	  
	  nScroll setLayer(int l) {
	    layer = l;
	    up.setLayer(l);
	    down.setLayer(l);
	    curs.setLayer(l);
	    back.setLayer(l);
	    return this;
	  }
	  nScroll toLayerTop() {
	    back.toLayerTop();
	    up.toLayerTop();
	    down.toLayerTop();
	    curs.toLayerTop();
	    return this;
	  }
	  nScroll clear() {
	    up.clear();
	    down.clear();
	    curs.clear();
	    back.clear();
	    return this;
	  }
	  
	  nScroll(nGUI _gui, float x, float y, float w, float h) {
	    gui = _gui;
	    larg = w; haut = h;
	    back = new nWidget(gui, x, y, w, h)
	        .setStandbyColor(gui.app.color(70))
	        .toLayerTop()
	        ;
	    up = new nWidget(gui, "^", (int)(w/1.5), 0, 0, w, w)
	        .setParent(back)
	        .toLayerTop()
	        .setOutlineColor(gui.app.color(100))
	        .setLabelColor(gui.app.color(180))
	        .setTextAlignment(PApplet.CENTER, PApplet.BOTTOM)
	        .setOutlineWeight(w / 16)
	        .setOutline(true)
	        .setTrigger()
	        .addEventFrame(new nRunnable() { public void run() {
	          if ((back.isHovered() || up.isHovered() || down.isHovered() || curs.isHovered()) && gui.in.mouseWheelUp) {
	            go_down();
	          }
	          if ((back.isHovered() || up.isHovered() || down.isHovered() || curs.isHovered()) && gui.in.mouseWheelDown) {
	            go_up();
	          }
	        }})
	        .addEventTrigger(new nRunnable() { public void run() {
	          go_up();
	        }})
	        ;
	    down = new nWidget(gui, "v", (int)(w/2.0), 0, 0, w, w)
	        .setParent(back)
	        .toLayerTop()
	        .setOutlineColor(gui.app.color(100))
	        .setLabelColor(gui.app.color(180))
	        .setOutlineWeight(w / 16)
	        .setOutline(true)
	        .alignDown()
	        .setTrigger()
	        .addEventTrigger(new nRunnable() { public void run() {
	          go_down();
	        }})
	        ; 
	    curs = new nWidget(gui, 0, 0, w, h-(w*2))
	        .setParent(up)
	        .toLayerTop()
	        .stackDown()
	        .setGrabbable()
	        .setConstrainX(true)
	        .addEventDrag(new nRunnable() {public void run() {
	        		float h = haut - (larg*2);
	        		float d = h / entry_nb;
	        		if (curs.getLocalY() < 0) curs.setPY(0);
	        		if (curs.getLocalY() > h - d*entry_view) curs.setPY(h - d*entry_view);
	        		entry_pos = (int) PApplet.abs(curs.getLocalY() / d);
	        		if (entry_pos > PApplet.max(0, entry_nb - entry_view)) 
	        			entry_pos = PApplet.max(0, entry_nb - entry_view);
	        	    update_cursor();
	        	    nRunnable.runEvents(eventChangeRun);
	        }})
	        .setStandbyColor(gui.app.color(100))
	        ;

	    toLayerTop();
	  }
	  void go_up() {
	    if (entry_pos > 0) entry_pos--;
	    update_cursor();
	    nRunnable.runEvents(eventChangeRun);
	  }
	  void go_down() {
	    if (entry_pos < entry_nb - entry_view) entry_pos++;
	    update_cursor();
	    nRunnable.runEvents(eventChangeRun);
	  }
	  void update_cursor() {
	    if (entry_view <= entry_nb) {
	      float h = haut - (larg*2);
	      float d = h / entry_nb;
	      curs.setSY(d*entry_view)
	        .setPY(d*entry_pos);
	    } else {
	      curs.setSY(haut - (larg*2))
	        .setPY(0);
	    }
	  }
	}