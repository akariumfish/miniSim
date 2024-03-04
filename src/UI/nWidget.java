package UI;

import java.util.ArrayList;

import RApplet.Rapp;
import RApplet.RConst;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.nRunnable;

public class nWidget {
	
	public static ArrayList<nWidget> all_widgets = new ArrayList<nWidget>();

	private String identity = ""; 
	private boolean identity_flag = false; 
	public  nWidget setIdentity(String s) { identity = s; return this; }
	public  nWidget setIdentityFlag(boolean s) { identity_flag = s; return this; }
	public  String getIdentity() { boolean[] last = new boolean[0]; return getIdentity(0, last); }
	public  String getIdentity(int rec_count, boolean[] last) { 
		String id = this + " widgetid:" + identity + " childs:";
		if (identity_flag) {
			for (int i = 0 ; i < 10 - rec_count ; i++) id += "    ";
			id += ">>> FLAG <<<";
		}
		id += " "+'\n';
		for (nWidget w : childs) {
			for (int i = 0 ; i < rec_count - 1 ; i++) 
				if (last[i]) id += "    "; else id += "  | ";
			boolean[] last2 = new boolean[rec_count+1];
			for (int i = 0 ; i < rec_count ; i++) last2[i] = last[i];
			last2[rec_count] = childs.get(childs.size() - 1) == w;
			id += "  |_" + w.getIdentity(rec_count+1, last2);
			last2[rec_count] = false;
		}
		return id; 
	}
	  
  public  nWidget setDrawer(nDrawer d) { ndrawer = d; return this; }
  public  nDrawer getDrawer() { return ndrawer; }
  public  nShelf getShelf() { return ndrawer.shelf; }
  public  nShelfPanel getShelfPanel() { return ndrawer.shelf.shelfPanel; }
  
  public  nWidget addEventPositionChange(nRunnable r)   { eventPositionChange.add(r); return this; }
  public  nWidget addEventShapeChange(nRunnable r)      { eventShapeChange.add(r); return this; }
  public  nWidget addEventLayerChange(nRunnable r)      { eventLayerChange.add(r); return this; }
  public  nWidget addEventVisibilityChange(nRunnable r) { eventVisibilityChange.add(r); return this; }
  
  public nWidget addEventClear(nRunnable r)      { eventClear.add(r); return this; }
  
  public nWidget addEventFrame(nRunnable r)      { eventFrameRun.add(r); return this; }
  public nWidget addEventFrame_Builder(nRunnable r) { eventFrameRun.add(r); r.builder = this; return this; }
  
  public nWidget addEventGrab(nRunnable r)       { eventGrabRun.add(r); return this; }
  public nWidget addEventDrag(nRunnable r)       { eventDragRun.add(r); return this; }
  public nWidget removeEventDrag(nRunnable r)       { eventDragRun.remove(r); return this; }
  public  nWidget addEventLiberate(nRunnable r)   { eventLiberateRun.add(r); return this; }
  
  public nWidget addEventMouseEnter(nRunnable r) { eventMouseEnterRun.add(r); return this; }
  public nWidget addEventMouseLeave(nRunnable r) { eventMouseLeaveRun.add(r); return this; }

  public  nWidget addEventTriggerRight(nRunnable r)         { eventTriggerRightRun.add(r); return this; }
  
  public nWidget addEventPressed(nRunnable r)      { eventPressRun.add(r); return this; }
  public nWidget addEventRelease(nRunnable r)    { eventReleaseRun.add(r); return this; }
  
  public  nWidget addEventTrigger(nRunnable r)         { eventTriggerRun.add(r); return this; }
  public  nWidget removeEventTrigger(nRunnable r)      { eventTriggerRun.remove(r); return this; }
  public nWidget clearEventTrigger()                 { eventTriggerRun.clear(); return this; }
  public nWidget addEventTrigger_Builder(nRunnable r) { eventTriggerRun.add(r); r.builder = this; return this; }
  
  public nWidget addEventSwitchOn(nRunnable r)   { eventSwitchOnRun.add(r); return this; }
  public  nWidget addEventSwitchOn_Builder(nRunnable r)   { r.builder = this; eventSwitchOnRun.add(r); return this; }
  public nWidget addEventSwitchOff(nRunnable r)  { eventSwitchOffRun.add(r); return this; }
  public nWidget clearEventSwitchOn()   { eventSwitchOnRun.clear(); return this; }
  public nWidget clearEventSwitchOff()  { eventSwitchOffRun.clear(); return this; }
  
  public nWidget addEventFieldChange(nRunnable r) { eventFieldChangeRun.add(r); return this; }
  public nWidget removeEventFieldChange(nRunnable r) { eventFieldChangeRun.remove(r); return this; }
  public nWidget clearEventFieldChange() { eventFieldChangeRun.clear(); return this; }
  
  public nWidget setDrawable(Drawable d) { 
    gui.drawing_pile.drawables.remove(drawer); 
    drawer.clear();
    drawer = d; 
    if (drawer != null) {
      drawer.setLayer(layer); 
//      gui.drawing_pile.drawables.add(d); 
    }
    return this; 
  }

  public Drawable getDrawable() { 
    return drawer; 
  }
  public nWidget setLayer(int l) { 
    layer = l; 
    if (drawer != null) drawer.setLayer(layer); 
    if (hover != null) hover.setLayer(layer); 
    nRunnable.runEvents(eventLayerChange); 
    return this; 
  }

  
  public nWidget toLayerTop() {
    if (drawer != null) drawer.toLayerTop();
    if (hover != null) hover.toLayerTop();
    return this;
  }
  
  public nWidget setParent(nWidget p) { 
    if (parent != null) parent.childs.remove(this); 
    if (p != null) { parent = p; p.childs.add(this); changePosition(); } return this; }
  public nWidget clearParent() { 
    if (parent != null) { parent.childs.remove(this); parent = null; changePosition(); } return this; }

  public nWidget setFineView(boolean d) { fine_view = d; return this; }
  
  public nWidget setText(String s) { if (s != null) { label = s; cursorPos = label.length(); } return this; }
  public nWidget changeText(String s) { label = s; if (cursorPos > label.length()) cursorPos = label.length(); return this; }
  public nWidget setFont(int s) { look.textFont = s; return this; }
  public nWidget setTextAlignment(int sx, int sy) { textAlignX = sx; textAlignY = sy; return this; }
  public nWidget setTextVisibility(boolean s) { show_text = s; return this; }
  public nWidget setTextAutoReturn(boolean s) { auto_line_return = s; return this; }
  public nWidget setTextLineLength(int s) { set_line_length = s; return this; }
  public nWidget setTextLineNoLength() { set_line_length = 0; return this; }
  
  
  public nWidget setInfo(String s) { if (s != null && s.length() > 0) { infoText = s; showInfo = true; } return this; }
  public nWidget setNoInfo() { showInfo = false; return this; }
  
  public nWidget setLook(nLook l) { look.copy(l); return this; }
  public nWidget setLook(nTheme t, String r) { look.copy(t.getLook(r)); return this; }
  public nWidget setLook(String r) { look.copy(gui.theme.getLook(r)); return this; }

  public nWidget tempPassif(boolean b) { 
    if (b != temp_passif) {
    		if (!b && hover != null) hover.active = hoverHideState; 
    		else if (hover != null) { hoverHideState = hover.active; hover.active = false; }
    }
    return this;
  }
  
  public nWidget hide() { 
    if (!hide) {
      hide = true; 
      changePosition(); 
      if (drawer != null) { drawerHideState = drawer.get_view(); drawer.hide(); }
      if (hover != null) { hoverHideState = hover.active; hover.active = false; }
      nRunnable.runEvents(eventVisibilityChange); 
      for (nWidget w : childs) w.hide(); 
    }
    return this; 
  }
  public nWidget show() { 
    if (hide) {
      hide = false; 
      changePosition(); 
      if (drawer != null) drawer.set_view(drawerHideState); 
      if (hover != null) hover.active = hoverHideState; 
      nRunnable.runEvents(eventVisibilityChange); 
      for (nWidget w : childs) w.show(); 
    }
    return this; 
  }
  public nWidget show_childs() { 
	    for (nWidget w : childs) w.show(); 
	    return this; 
	  }
  public nWidget hide_childs() { 
	    for (nWidget w : childs) w.hide(); 
	    return this; 
	  }
  
  public nWidget copy(nWidget w) {
  //eventFrameRun.clear(); for (Runnable r : w.eventFrameRun) eventFrameRun.add(r);
  
  //ArrayList<Runnable> eventPositionChange = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventShapeChange = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventLayerChange = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventVisibilityChange = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventClear = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventFrameRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventGrabRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventDragRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventLiberateRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventMouseEnterRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventMouseLeaveRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventPressRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventReleaseRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventTriggerRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventSwitchOnRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventSwitchOffRun = new ArrayList<Runnable>();
  //ArrayList<Runnable> eventFieldChangeRun = new ArrayList<Runnable>();
    triggerMode = w.triggerMode; switchMode = w.switchMode;
    grabbable = w.grabbable; constrainX = w.constrainX; constrainY = w.constrainY;
    isSelectable = w.isSelectable; isField = w.isField; 
    showCursor = w.showCursor; hoverOutline = w.hoverOutline; showOutline = w.showOutline;
    alignX = w.alignX; stackX = w.stackX; alignY = w.alignY; stackY = w.stackY; centerX = w.centerX; centerY = w.centerY;
    placeLeft = w.placeLeft; placeRight = w.placeRight; placeUp = w.placeUp; placeDown = w.placeDown;
    hide = w.hide; drawerHideState = w.drawerHideState; hoverHideState = w.hoverHideState;
    constantOutlineWeight = w.constantOutlineWeight;
    textAlignX = w.textAlignX; textAlignY = w.textAlignY; show_text = w.show_text;
    shapeRound = w.shapeRound; shapeLosange = w.shapeLosange; 
    showInfo = w.showInfo; infoText = RConst.str_copy(w.infoText);
    auto_line_return = w.auto_line_return; set_line_length = w.set_line_length;
    constrainDlength = w.constrainDlength; constrainD = w.constrainD;
    fine_view = w.fine_view; always_view = w.always_view;
    look.copy(w.look);
    setLayer(w.layer);
    setPosition(w.localrect.pos.x, w.localrect.pos.y);
    setSize(w.localrect.size.x, w.localrect.size.y);
    changePosition();
    if (hover != null && w.hover != null) hover.active = w.hover.active;
    if (w.parent != null) setParent(w.parent);
    //if (hover != null && (isSelectable || grabbable || triggerMode || switchMode) && !hide) hover.active = true;
    return this;
  }
  boolean has_been_cleared = false;
  public void clear() {
	  all_widgets.remove(this);
    //if (ndrawer != null) ndrawer.widgets.remove(this);
    for (nWidget w : childs) w.clear();
    nRunnable.runEvents(eventClear);
    //if (gui != null) gui.removeEventFrame(frame_run);
    frame_run.to_clear = true;
    //if (look != null) look.clear(); 
    //look = null;
    if (drawer != null) drawer.clear(); if (hover != null) hover.clear();
    drawer = null; hover = null;
    eventPositionChange.clear(); eventShapeChange.clear(); eventLayerChange.clear(); 
    eventVisibilityChange.clear(); eventClear.clear(); eventFrameRun.clear(); 
    eventGrabRun.clear(); eventDragRun.clear(); eventLiberateRun.clear(); eventFieldChangeRun.clear();
    eventMouseEnterRun.clear(); eventMouseLeaveRun.clear(); 
    eventPressRun.clear(); eventReleaseRun.clear(); eventTriggerRun.clear(); 
    eventTriggerRightRun.clear(); 
    eventSwitchOnRun.clear(); eventSwitchOffRun.clear(); eventFieldChangeRun.clear();
    has_been_cleared = true;
  }
  
  public nGUI getGUI() { return gui; }
  public Rect getRect() { return globalrect; }
  public Rect getPhantomRect() { return phantomrect; } //rect exist enven when hided ; for hided collisions
  public int getLayer() { return layer; }
  public String getText() { return label.substring(0, label.length()); }
  
  public boolean isClicked() { return isClicked; }
  public boolean isHovered() { return isHovered; }
  public boolean isGrabbed() { return isGrabbed; }
  public boolean isField() { return isField; }
  public boolean isHided() { return hide; }
  public boolean isOn() { return switchState; }
  
  public nWidget setAlwaysView(boolean b) { 
	  always_view = b;
	  return this; }
  
  public nWidget setHoverablePhantomSpace(float f) { if (hover != null) hover.phantom_space = f; return this; }
  
  public nWidget setPassif() { 
    triggerMode = false; 
    switchMode = false; 
    switchState = false; 
    grabbable = false; 
    isField = false;
    isClicked = false;
    if (hover != null) { hover.active = false; hoverHideState = hover.active; }
    return this; }
  public nWidget setBackground() { 
    triggerMode = false; 
    switchMode = false; 
    switchState = false; 
    grabbable = false; 
    isField = false; 
    isClicked = false;
    if (hover != null) { hover.active = true; hoverHideState = hover.active; }
    return this; }
  public nWidget setTrigger() { 
    triggerMode = true; switchMode = false; switchState = false; 
    if (hover != null) hover.active = true; if (hover != null) hoverHideState = hover.active; return this; }
  public nWidget setSwitch() { 
    triggerMode = false; switchMode = true; switchState = false; 
    if (hover != null) hover.active = true; if (hover != null) hoverHideState = hover.active; return this; }
  
  //carefull!! dont work if excluded cleared before this
  private ArrayList<nWidget> excludes = new ArrayList<nWidget>();
  public nWidget addExclude(nWidget b) { excludes.add(b); return this; }
  public nWidget removeExclude(nWidget b) { excludes.remove(b); return this; }
  
  public nWidget setGrabbable() { triggerMode = true; grabbable = true; hover.active = true; hoverHideState = hover.active; return this; }
  public nWidget setFixed() { grabbable = false; hover.active = false; hoverHideState = hover.active; return this; }
  public nWidget setConstrainX(boolean b) { constrainX = b; return this; }
  public nWidget setConstrainY(boolean b) { constrainY = b; return this; }
  public nWidget setConstrainDistance(float b) { if (b == 0) constrainD = false; else { constrainDlength = b; constrainD = true; } return this; }
  public nWidget setSelectable(boolean o) { isSelectable = o; hoverOutline = o; hover.active = true; hoverHideState = hover.active; return this; }
  public nWidget setField(boolean o) { isField = o; setSelectable(o); return this; }
  
  public nWidget setOutline(boolean o) { showOutline = o; return this; }
  public nWidget setOutlineWeight(float l) { look.outlineWeight = l; return this; }
  public nWidget setOutlineWeight(double l) { look.outlineWeight = (float)l; return this; }
  public nWidget setOutlineConstant(boolean l) { constantOutlineWeight = l; return this; }
  
  public nWidget setHoveredOutline(boolean o) { hoverOutline = o; return this; }

  public nWidget setPosition(float x, float y) { setPX(x); setPY(y); return this; }
  public nWidget setPosition(double x, double y) { setPX((float)x); setPY((float)y); return this; }
  public nWidget setPosition(PVector p) { setPX(p.x); setPY(p.y); return this; }
  public nWidget setSize(float x, float y) { setSX(x); setSY(y); return this; }
  public nWidget setSize(double x, double y) { setSX((float)x); setSY((float)y); return this; }

  public nWidget setPX(double v) { return setPX((float)v); }
  public nWidget setPY(double v) { return setPY((float)v); }
  public nWidget setSX(double v) { return setSX((float)v); }
  public nWidget setSY(double v) { return setSY((float)v); }
  
  public nWidget setPX(float v) { 
    if (v != localrect.pos.x) { localrect.pos.x = v; changePosition(); return this; } return this; }
  public nWidget setPY(float v) { 
    if (v != localrect.pos.y) { localrect.pos.y = v; changePosition(); return this; } return this; }
  public nWidget setSX(float v) { 
    if (v != localrect.size.x) { 
    		localrect.size.x = v; 
//    		globalrect.size.x = getSX(); 
//    		if (stackX && placeLeft) globalrect.pos.x = getX(); 
		changePosition();
//		for (nWidget w : childs) 
//			if (((w.stackX || w.alignX) && w.placeRight) || ((stackX || alignX) && placeLeft)) w.changePosition(); 
      	nRunnable.runEvents(eventShapeChange); 
      	return this; 
    } 
    return this; 
  }
  public nWidget setSY(float v) { 
    if (v != localrect.size.y) { 
    		localrect.size.y = v; 
//    		globalrect.size.y = getSY(); 
//    		if (stackY && placeUp) globalrect.pos.y = getY(); 
    		changePosition();
//    		for (nWidget w : childs) 
//    			if (((w.stackY || w.alignY) && w.placeDown) || ((stackY || alignY) && placeUp)) w.changePosition(); 
    		nRunnable.runEvents(eventShapeChange); 
    		return this; 
    } 
    return this; 
  }
  
  public nWidget setRound(boolean c) { shapeRound = c; return this; }
  public nWidget setLosange(boolean c) { shapeLosange = c; return this; }
  
  public nWidget setStandbyColor(int c) { look.standbyColor = c; return this; }
  public nWidget setHoveredColor(int c) { look.hoveredColor = c; return this; }
  public nWidget setClickedColor(int c) { look.pressColor = c; return this; }
  public nWidget setLabelColor(int c)   { look.textColor = c; return this; }
  public nWidget setOutlineColor(int c) { look.outlineColor = c; return this; }
  public nWidget setOutlineSelectedColor(int c) { look.outlineSelectedColor = c; return this; }
  
  public nWidget alignUp()    { alignY = true;  stackY = false; placeUp   = true;  placeDown = false;  centerY = false; changePosition(); return this; }
  public nWidget alignDown()  { alignY = true;  stackY = false; placeUp   = false; placeDown = true;   centerY = false; changePosition(); return this; }
  public nWidget alignLeft()  { alignX = true;  stackX = false; placeLeft = true;  placeRight = false; centerY = false; changePosition(); return this; }
  public nWidget alignRight() { alignX = true;  stackX = false; placeLeft = false; placeRight = true;  centerY = false; changePosition(); return this; }
  public nWidget stackUp()    { alignY = false; stackY = true;  placeUp   = true;  placeDown = false;  centerX = false; changePosition(); return this; }
  public nWidget stackDown()  { alignY = false; stackY = true;  placeUp   = false; placeDown = true;   centerX = false; changePosition(); return this; }
  public nWidget stackLeft()  { alignX = false; stackX = true;  placeLeft = true;  placeRight = false; centerX = false; changePosition(); return this; }
  public nWidget stackRight() { alignX = false; stackX = true;  placeLeft = false; placeRight = true;  centerX = false; changePosition(); return this; }
  public nWidget centerX()    { alignX = false; stackX = false; placeLeft = false; placeRight = false; centerX = true;  changePosition(); return this; }
  public nWidget centerY()    { alignY = false; stackY = false; placeUp   = false; placeDown  = false; centerY = true;  changePosition(); return this; }
  public nWidget center()     { centerX(); centerY(); return this; }
  
  public nWidget setSwitchState(boolean s) { if (s) setOn(); else setOff(); return this; }
  public nWidget setOn() {
    if (!switchState) {
      switchState = true;
      nRunnable.runEvents(eventSwitchOnRun);
      for (nWidget b : excludes) b.setOff(); }
    return this;
  }
  public void forceOn() {
    switchState = true;
    nRunnable.runEvents(eventSwitchOnRun);
    for (nWidget b : excludes) b.setOff(); }
    
  public nWidget setOff() {
    if (switchState) {
      switchState = false;
      nRunnable.runEvents(eventSwitchOffRun); } 
    return this; }
  public void forceOff() {
    switchState = false;
    nRunnable.runEvents(eventSwitchOffRun); }
  
  public float getX() { 
    if (parent != null) {
      if (alignX) {
        if (placeRight) return parent.getX() + parent.getSX() + localrect.pos.x - getSX();
        else if (placeLeft) return parent.getX() + localrect.pos.x;
      } else if (stackX) {
        if (placeRight) return parent.getX() + parent.getSX() + localrect.pos.x;
        else if (placeLeft) return parent.getX() + localrect.pos.x - getSX();
      } else if (centerX) return parent.getX() + localrect.pos.x - getSX()/2;
      else return localrect.pos.x + parent.getX();
    } 
    if (alignX) {
      if (placeRight) return localrect.pos.x - getSX();
      else if (placeLeft) return localrect.pos.x;
    } else if (stackX) {
      if (placeRight) return localrect.pos.x;
      else if (placeLeft) return localrect.pos.x - getSX();
    } else if (centerX) return localrect.pos.x - getSX()/2;
    return localrect.pos.x;
  }
  public float getY() { 
    if (parent != null) {
      if (alignY) {
        if (placeDown) return parent.getY() + parent.getSY() + localrect.pos.y - getSY();
        else if (placeUp) return parent.getY() + localrect.pos.y;
      } else if (stackY) {
        if (placeDown) return parent.getY() + parent.getSY() + localrect.pos.y;
        else if (placeUp) return parent.getY() + localrect.pos.y - getSY();
      } else if (centerY) return parent.getY() + localrect.pos.y - getSY()/2;
      else return localrect.pos.y + parent.getY();
    } 
    if (alignY) {
      if (placeDown) return localrect.pos.y - getSY();
      else if (placeUp) return localrect.pos.y;
    } else if (stackY) {
      if (placeDown) return localrect.pos.y;
      else if (placeUp) return localrect.pos.y - getSY();
    } else if (centerY) return localrect.pos.y - getSY()/2;
    return localrect.pos.y;
  }
  public float getLocalX() { return localrect.pos.x; }
  public float getLocalY() { return localrect.pos.y; }
  public float getSX() { if (!hide) return localrect.size.x; else return 0; }
  public float getSY() { if (!hide) return localrect.size.y; else return 0; }
  public float getLocalSX() { return localrect.size.x; }
  public float getLocalSY() { return localrect.size.y; }
  
  public boolean isViewable() {
	  return always_view || (Rect.rectCollide(getRect(), gui.view) && 
    		  !(fine_view && getSX()*gui.scale < 1 && getSY()*gui.scale < 1 ) && 
    		  !(!fine_view && getSX()*gui.scale < 5 && getSY()*gui.scale < 5));
  }
  
  public nWidget(Rapp a) {   //only for theme model saving !!
    localrect = new Rect();
    globalrect = new Rect();
    phantomrect = new Rect();
    hover = new Hoverable(null, null);
    hover.active = true;
    hoverHideState = hover.active; 
    changePosition();
    look = new nLook(a);
    label = new String();
  }
  public nWidget(nGUI g) { init(g); }
  public nWidget(nGUI g, float x, float y) {
    init(g);
    setPosition(x, y);
  }
  public nWidget(nGUI g, float x, float y, float w, float h) {
    init(g);
    setPosition(x, y);
    setSize(w, h);
  }
  public nWidget(nGUI g, String _label, int _text_font, float x, float y) {
    init(g);
    label = _label; look.textFont = _text_font;
    setPosition(x, y);
    setSize(label.length() * _text_font, _text_font);
  }
  public nWidget(nGUI g, String _label, int _text_font, float x, float y, float w, float h) {
    init(g);
    label = _label; look.textFont = _text_font;
    setPosition(x, y);
    setSize(w, h);
  }
  
  protected nGUI gui;
  Drawable drawer;
  private Hoverable hover;
  private Rect globalrect, localrect, phantomrect;
  private nWidget parent = null;
  private ArrayList<nWidget> childs = new ArrayList<nWidget>();
  public nLook look;
  

  
  private nDrawer ndrawer = null;
  
  private String label, infoText;
  private boolean auto_line_return = false;
  private int set_line_length = 0;
  private float mx = 0, my = 0;
//		  float pmx = 0, pmy = 0;
  private int cursorPos = 0;
  private int cursorCount = 0;
  private int cursorCycle = 80;

  private boolean fine_view = false;
  private boolean always_view = false;

  private boolean temp_passif = false;
  private boolean switchState = false;
  public boolean isClicked = false;
  public boolean isHovered = false;
  private boolean isGrabbed = false;
  private boolean isSelected = false;
  
  private boolean triggerMode = false, switchMode = false;
  private boolean grabbable = false, constrainX = false, constrainY = false, constrainD = false;
  private float constrainDlength = 0;
  private boolean isSelectable = false, isField = false, showCursor = false;
  boolean showOutline = false;
private boolean hoverOutline = false;
boolean constantOutlineWeight = false;
  private boolean alignX = false, stackX = false, alignY = false, stackY = false;
  private boolean centerX = false, centerY = false;
  private boolean placeLeft = false, placeRight = false, placeUp = false, placeDown = false;
  private boolean hide = false, drawerHideState = true, hoverHideState = true, show_text = true;
  private boolean shapeRound = false, shapeLosange = false, showInfo = false;
  private int layer = 0, textAlignX = PApplet.CENTER, textAlignY = PApplet.CENTER;
 
  ArrayList<nRunnable> eventPositionChange = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventShapeChange = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventLayerChange = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventVisibilityChange = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventClear = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventFrameRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventGrabRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventDragRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventLiberateRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventMouseEnterRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventMouseLeaveRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventPressRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventReleaseRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventTriggerRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventTriggerRightRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventSwitchOnRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventSwitchOffRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventFieldChangeRun = new ArrayList<nRunnable>();
  
  private nRunnable frame_run;
  public Rapp app;
  void init(nGUI g) {
	  all_widgets.add(this);
    gui = g; app = g.app;
    frame_run = new nRunnable() { public void run() { frame(); } };
    gui.addEventFrame(frame_run);
    localrect = new Rect();
    globalrect = new Rect();
    phantomrect = new Rect();
    changePosition();
    hover = new Hoverable(g.hoverable_pile, globalrect);
    hover.active = true;
    hoverHideState = hover.active; 
    label = new String();
    look = new nLook(gui.app);
    drawer = new Drawable(g.drawing_pile) { public void drawing() {
      if (isViewable()) {
        if (((triggerMode || switchMode) && isClicked) || switchState) { app.fill(look.pressColor); } 
        else if (isHovered && (triggerMode || switchMode))             { app.fill(look.hoveredColor); } 
        else                                                           { app.fill(look.standbyColor); }
        app.noStroke();
        app.ellipseMode(PConstants.CORNER);
        if (shapeRound) app.ellipse(getX(), getY(), getSX(), getSY());
        else if (shapeLosange) {app.quad(getX() + getSX()/2, getY(), 
                                     getX() + getSX()  , getY() + getSY()/2, 
                                     getX() + getSX()/2, getY() + getSY(), 
                                     getX()            , getY() + getSY()/2  );}
        else if (!app.DEBUG_NOFILL) app.rect(getX(), getY(), getSX(), getSY());
        
        app.noFill();
        if (isField && isSelected) app.stroke(look.outlineSelectedColor);
        else if (showOutline || (hoverOutline && isHovered)) app.stroke(look.outlineColor);
        else app.noStroke();
        float wf = 1.0F;
        if (constantOutlineWeight) { wf = 1 / gui.scale; app.strokeWeight(look.outlineWeight / gui.scale); }
        else app.strokeWeight(look.outlineWeight);
        
        
        if (shapeRound) app.ellipse(getX() + wf*look.outlineWeight/2, getY() + wf*look.outlineWeight/2, 
             getSX() - wf*look.outlineWeight, getSY() - wf*look.outlineWeight);
        else if (shapeLosange) {app.quad(getX() + getSX()/2, getY() + wf*look.outlineWeight/2, 
                                     getX() + getSX() - wf*look.outlineWeight/2, getY() + getSY()/2, 
                                     getX() + getSX()/2, getY() + getSY() - wf*look.outlineWeight/2, 
                                     getX() + wf*look.outlineWeight/2, getY() + getSY()/2  );}
        else app.rect(getX() + wf*look.outlineWeight/2, getY() + wf*look.outlineWeight/2, 
             getSX() - wf*look.outlineWeight, getSY() - wf*look.outlineWeight);
        
        if (show_text && gui.scale >= 0.3) {
          String l = label;
          if (showCursor) {
            String str = label.substring(0, cursorPos);
            String end = label.substring(cursorPos, label.length());
            if (cursorCount < cursorCycle / 2) l = str + "|" + end;
            else l = str + " " + end;
            cursorCount++;
            if (cursorCount > cursorCycle) cursorCount = 0;
          }
          if (l.length() > 0) {
	          app.fill(look.textColor); 
	          app.textAlign(textAlignX, textAlignY);
//	          app.textFont(app.getFont(look.textFont));
	          //int line = 0;
	          //for (int i = 0 ; i < l.length() ; i++) if (l.charAt(i) == '\n') line+=1;
	          float tx = getX();
	          float ty = getY();
	          if (textAlignY == PConstants.CENTER)         
	            ty += (getLocalSY() / 2.0)
	                  - (look.textFont / 6.0)
	                    //- (line * look.textFont / 3)
	                  ;
	          else if (textAlignY == PConstants.BOTTOM) 
	            ty += getLocalSY() - (look.textFont / 10);
	          if (textAlignX == PConstants.LEFT)        tx += look.textFont / 4.0;
	          else if (textAlignX == PConstants.CENTER) tx += getSX() / 2;
	          else if (textAlignX == PConstants.BOTTOM) //ref to opposite side
	        	  	tx += getSX() - gui.app.textWidth('n')*l.length();
	
	  	      float line_max_char = (getSX() / gui.app.textWidth('m'));
	  	      if (set_line_length > 0) line_max_char = set_line_length;
	          if (!auto_line_return || l.length() < line_max_char) app.text(l, tx, ty);
	          else {
	            int printed_char = 0;
	            int line_cnt = 0;
	            while (printed_char < l.length()) {
	                int line_end = 0;
	              String line_string = l.substring(printed_char, l.length());
	              float tw = app.textWidth(line_string);
	              while (tw > getLocalSX() - look.textFont / 2.0F) {
		              line_end++;
		              line_string = l.substring(printed_char, l.length() - line_end);
		              tw = app.textWidth(line_string);
	              }
	              printed_char += line_string.length();
	              app.text(line_string, tx, ty + (line_cnt*look.textFont));
	              line_cnt++;
	            }
	        	  
	//            app.logln(l + "      font" + look.textFont + " line" + line_cnt);
	          }
          }
        }
      }
    } } ;
  }
  
  private void changePosition() { 
    globalrect.pos.x = getX(); 
    globalrect.pos.y = getY(); 
    globalrect.size.x = getSX(); 
    globalrect.size.y = getSY(); 
    phantomrect.pos.x = getX(); 
    phantomrect.pos.y = getY(); 
    phantomrect.size.x = getLocalSX(); 
    phantomrect.size.y = getLocalSY(); 
    nRunnable.runEvents(eventPositionChange); 
    for (nWidget w : childs) w.changePosition(); 
  }
  
  void frame() {
    if (hover != null && hover.mouseOver && 
    		!(fine_view && getSX()*gui.scale < 2 && getSY()*gui.scale < 2) && 
    		!(!fine_view && getSX()*gui.scale < 10 && getSY()*gui.scale < 10)) {
      if (!isHovered) nRunnable.runEvents(eventMouseEnterRun);
      if (showInfo) gui.info.showText(infoText);
      isHovered = true;
    } else {
      if (isHovered) nRunnable.runEvents(eventMouseLeaveRun); 
      isHovered = false;
    }
    if (triggerMode || switchMode) {
      if (gui.in.getUnClick("MouseLeft")) {
        if (isClicked) nRunnable.runEvents(eventReleaseRun); 
        isClicked = false;
      }
      if (gui.in.getClick("MouseRight") && isHovered && !isClicked) 
    	  		nRunnable.runEvents(eventTriggerRightRun); 
      
      if (gui.in.getClick("MouseLeft") && isHovered && !isClicked) {
        isClicked = true;
        if (triggerMode) nRunnable.runEvents(eventTriggerRun); 
        if (switchMode) { if (switchState) { setOff(); } else { setOn(); } }
      }
      
    }
    if (isClicked) nRunnable.runEvents(eventPressRun);
    if (grabbable) {
      if (isHovered) {
        if (gui.in.getClick("MouseLeft")) {
          mx = getLocalX() - gui.mouseVector.x;
          my = getLocalY() - gui.mouseVector.y;
          //gui.in.cam.GRAB = false; //deactive le deplacement camera
          //gui.szone.ON = false;
          isGrabbed = true;
          nRunnable.runEvents(eventGrabRun);
        }
      }
      if (isGrabbed && gui.in.getUnClick("MouseLeft")) {
        isGrabbed = false;
        //gui.in.cam.GRAB = true;
        //gui.szone.ON = true;
        nRunnable.runEvents(eventLiberateRun);
      }
      if (isGrabbed && isClicked) {
        float nx = gui.mouseVector.x + mx, ny = gui.mouseVector.y + my;
        if (constrainD) {
          PVector p = new PVector(nx, ny);
          if (p.mag() > constrainDlength) p.setMag(constrainDlength);
          nx = p.x; ny = p.y;
        }
        if (!constrainX) setPX(nx);
        if (!constrainY) setPY(ny);
        nRunnable.runEvents(eventDragRun);
      }
    }
    if (isSelectable) {
      if (isHovered && gui.in.getClick("MouseLeft")) {
        isSelected = !isSelected;
        if (isSelected) {
          prev_select_outline = showOutline;
          showOutline = true;
          if (isField) showCursor = true;
          gui.field_used = true;
        } else {
          showOutline = prev_select_outline;
          if (isField) showCursor = false;
          gui.field_used = false;
        }
      } else if (!isHovered && gui.in.getClick("MouseLeft") && isSelected) {
        showOutline = prev_select_outline;
        if (isField) showCursor = false;
        isSelected = false;
        gui.field_used = false;
      }
    }
    if (isField && isSelected) {
      if (gui.in.getClick("Left")) cursorPos = Math.max(0, cursorPos-1);
      else if (gui.in.getClick("Right")) cursorPos = Math.min(cursorPos+1, label.length());
      else if (gui.in.getClick("Backspace") && cursorPos > 0) {
        String str = label.substring(0, cursorPos-1);
        String end = label.substring(cursorPos, label.length());
        label = str + end;
        cursorPos--;
        nRunnable.runEvents(eventFieldChangeRun);
      }
      else if (gui.in.getClick("Enter")) {
              // it break the saving!!!
        //String str = label.substring(0, cursorPos);
        //String end = label.substring(cursorPos, label.length());
        //label = str + '\n' + end;
        //cursorPos++;
        //runEvents(eventFieldChangeRun);
      }
      else if (gui.in.getClick("Backspace")) {}
      else if (gui.in.getClick("All")) {
        String str = label.substring(0, cursorPos);
        String end = label.substring(cursorPos, label.length());
        label = str + gui.in.getLastKey() + end;
        cursorPos++;
        nRunnable.runEvents(eventFieldChangeRun);
      }
    }
    nRunnable.runEvents(eventFrameRun);
  }
  private boolean prev_select_outline = false;
}

