package UI;

import java.util.ArrayList;

import Macro.Macro_Main;
import Macro.Macro_Sheet;
import RApplet.Camera;
import RBase.RConst;
import processing.core.PConstants;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sValueBloc;
import sData.sVec;

public class nCursor extends nWidget {
	  float x() { if (pval != null) return pval.x(); else return 0; }
	  float y() { if (pval != null) return pval.y(); else return 0; }
	  public PVector dir() { if (dval != null && dval.get().mag() > ref_size) 
	                    return new PVector(dval.x(), dval.y()).setMag(1); 
	                  else return new PVector(1, 0).rotate(gui.app.random(2*RConst.PI)); }
	  public PVector pos() { 
	    if (pval != null) return new PVector(pval.x(), pval.y()); 
	    else return new PVector(); }
	  nGUI gui;
	  float ref_size;
	  public sVec pval = null;
	  public sVec dval = null;
	  public sBoo show = null;
	  sValueBloc s_bloc = null;
	  public String ref; 
	  String shr;
	  nWidget refwidget, thiswidget, pointwidget;
	  nWidget screen_widget, screenpoint_widget;
	  Macro_Sheet sheet;
	  Camera cam;
	  nRunnable move_run, zoom_run;
	  boolean show_dir = true;
	  
	  ArrayList<Runnable> eventClearRuns = new ArrayList<Runnable>();
	  nCursor addEventClear(Runnable r) { eventClearRuns.add(r); return this; }
	  
	  nCursor(Macro_Main mm, sValueBloc blc, String r, boolean sd) {
	    super(mm.gui);
	    show_dir = sd;
	    sheet = mm;
	    cam = mm.inter.cam;
	    new nConstructor(sheet.gui.theme, sheet.gui.theme.ref_size);
	    thiswidget = this;
	    gui = sheet.gui; ref_size = sheet.gui.theme.ref_size; 
	    
	    build_ui();
	    
	    int c = 0;
	    while (blc.getBloc(r+"_"+c) != null) { c++; }
	    s_bloc = blc.newBloc(r+"_"+c);
	    ref = r+"_"+c; shr = ref;
	    pval = s_bloc.newVec("pos", "pos");
	    show = s_bloc.newBoo(false, "show", "show"); //!!!!! is hided by default
	    dval = s_bloc.newVec("dir", "dir");
	    
	    pval.addEventDelete(new nRunnable() { public void run() { clear(); } } );
	    pval.addEventChange(new nRunnable() { public void run() {
	      thiswidget.setPosition(pval.x()-ref_size, pval.y()-ref_size);
	      PVector p = cam.cam_to_screen(pval.get());
	      screen_widget.setPosition(p.x-ref_size/4, p.y-ref_size/4); }});
	    show.addEventChange(new nRunnable(show) {public void run() { update_view(); }});
	    dval.addEventChange(new nRunnable() {public void run() {
	      if (dval.get().mag() > ref_size*2) dval.set(dval.get().setMag(ref_size*2));
	      pointwidget.setPosition(dval.x()-ref_size/4, dval.y()-ref_size/4); 
	      screenpoint_widget.setPosition(dval.x()-ref_size/4, dval.y()-ref_size/4); }});
	  }
	  nCursor(Macro_Main mm, sValueBloc blc, boolean sd) {
	    super(mm.gui);
	    show_dir = sd;
	    sheet = mm;
	    cam = mm.inter.cam;
	    new nConstructor(sheet.gui.theme, sheet.gui.theme.ref_size);
	    thiswidget = this;
	    gui = sheet.gui; ref_size = sheet.gui.theme.ref_size; 
	    
	    build_ui();
	    
	    if (blc.getValue("pos") != null && blc.getValue("show") != null && blc.getValue("dir") != null) {
	      s_bloc = blc;
	      pval = (sVec)blc.getValue("pos");
	      show = (sBoo)blc.getValue("show"); //!!!!! is hided by default
	      dval = (sVec)blc.getValue("dir");
	      ref = blc.ref; shr = ref;
	      
	      pval.addEventDelete(new nRunnable() { public void run() { clear(); } } );
	      pval.addEventChange(new nRunnable() { public void run() {
	        thiswidget.setPosition(pval.x()-ref_size, pval.y()-ref_size);
	        PVector p = cam.cam_to_screen(pval.get());
	        screen_widget.setPosition(p.x-ref_size/4, p.y-ref_size/4); }});
	      show.addEventChange(new nRunnable(show) {public void run() { update_view(); }});
	      dval.addEventChange(new nRunnable() {public void run() {
	        if (dval.get().mag() > ref_size*2) dval.set(dval.get().setMag(ref_size*2));
	        pointwidget.setPosition(dval.x()-ref_size/4, dval.y()-ref_size/4); 
	        screenpoint_widget.setPosition(dval.x()-ref_size/4, dval.y()-ref_size/4); }});
	    }
	  }
	  
	  public nCursor(Macro_Sheet _sheet, String r, String s, boolean sd) {
	    super(_sheet.gui);
	    show_dir = sd;
	    sheet = _sheet;
	    cam = sheet.mmain().inter.cam;
	    new nConstructor(sheet.gui.theme, sheet.gui.theme.ref_size);
	    thiswidget = this;
	    gui = sheet.gui; ref_size = sheet.gui.theme.ref_size; 
	    ref = r; shr = s;
	    
	    build_ui();
	    
	    pval = sheet.newVec(s+"_cursor_pos", s+"_pos");
	    show = sheet.newBoo(false, s+"_cursor_show", s+"_show"); //!!!!! is hided by default
	    dval = sheet.newVec(s+"_cursor_dir", s+"_dir");
	    
	    dval.addEventChange(new nRunnable() {public void run() {
	      if (dval.get().mag() > ref_size*2) dval.set(dval.get().setMag(ref_size*2));
	      pointwidget.setPosition(dval.x()-ref_size/4, dval.y()-ref_size/4); 
	      screenpoint_widget.setPosition(dval.x()-ref_size/4, dval.y()-ref_size/4); }});
	    
	    pval.addEventDelete(new nRunnable() { public void run() { clear(); } } );
	    pval.addEventChange(new nRunnable() { public void run() {
	      thiswidget.setPosition(pval.x()-ref_size, pval.y()-ref_size);
	      PVector p = cam.cam_to_screen(pval.get());
	      screen_widget.setPosition(p.x-ref_size/4, p.y-ref_size/4); }});
	      
	    show.addEventChange(new nRunnable(show) {public void run() { update_view(); }});
	    
	  }
	  
	  void build_ui() {
	    
	    copy(gui.theme.getModel("Cursor"));
	    refwidget = gui.theme.newWidget(gui, "ref").setParent(this).setPosition(ref_size, ref_size);
	    setSize(ref_size*2, ref_size*2);
	    setPosition(-ref_size, -ref_size);
	    setText(ref).setFont((int)(ref_size/2.0F)).setTextAlignment(PConstants.LEFT, PConstants.CENTER);
	    setGrabbable();
	    
	    screen_widget = gui.theme.newWidget(sheet.mmain().screen_gui, "Cursor")
	      .setSize(ref_size/2, ref_size/2).setPosition(-ref_size/4, -ref_size/4)
	      .setGrabbable().setText(ref)
	      .setFont((int)(ref_size / 2.0)).setTextAlignment(PConstants.LEFT, PConstants.DOWN);
	    
	    screenpoint_widget = gui.theme.newWidget(sheet.mmain().screen_gui, "Pointer")
	      .setPosition(-ref_size/4, -ref_size/4).setSize(ref_size/2, ref_size/2).hide();
	    screenpoint_widget.setParent(screen_widget).setGrabbable().setConstrainDistance(ref_size*2).toLayerTop();
	    
	    pointwidget = gui.theme.newWidget(gui, "Pointer").setPosition(-ref_size/4, -ref_size/4).setSize(ref_size/2, ref_size/2);
	    pointwidget.setParent(refwidget).setGrabbable().setConstrainDistance(ref_size*2).hide().toLayerTop();
	    
	    addEventDrag(new nRunnable() {public void run() { 
	      if (pval != null) pval.set(refwidget.getX(), refwidget.getY()); }});
	    
	    screen_widget.addEventDrag(new nRunnable() {public void run() {
	        PVector p = new PVector(screen_widget.getX()+ref_size/4, screen_widget.getY()+ref_size/4);
	        p = cam.screen_to_cam(p);
	        if (pval != null) pval.set(p.x, p.y);
	      }});
	    pointwidget.addEventDrag(new nRunnable() {public void run() {
	      if (dval != null) dval.set(pointwidget.getLocalX() + ref_size/4, pointwidget.getLocalY() + ref_size/4);
	    }});
	    screenpoint_widget.addEventDrag(new nRunnable() {public void run() {
	      if (dval != null) dval.set(screenpoint_widget.getLocalX() + ref_size/4, screenpoint_widget.getLocalY() + ref_size/4);
	    }}); 
	    
	    pointwidget.addEventLiberate(new nRunnable() {public void run() {
	      if (dval != null && dval.get().mag() < ref_size) dval.set(0, 0); }});
	    screenpoint_widget.addEventLiberate(new nRunnable() {public void run() {
	      if (dval != null && dval.get().mag() < ref_size) dval.set(0, 0); }});
	    
	    move_run = new nRunnable() {public void run() { 
	      if (pval != null) {
	        PVector p = cam.cam_to_screen(pval.get());
	        screen_widget.setPosition(p.x-ref_size/4, p.y-ref_size/4); } }};
	    zoom_run = new nRunnable() {public void run() { 
	      update_view();
	      if (pval != null) {
	        PVector p = cam.cam_to_screen(pval.get());
	        screen_widget.setPosition(p.x-ref_size/4, p.y-ref_size/4); } }};
	    cam.addEventMove(move_run);
	    cam.addEventZoom(zoom_run);
	    
	    sheet.mmain().inter.addEventNextFrame(new nRunnable() {public void run() {
	      update_view();
	    }});
	  }
	  
	  void update_view() {
	    if (cam.cam_scale.get() < 0.25) { 
	      if (show != null && show.get()) { 
	        screen_widget.show(); 
	        if (show_dir) screenpoint_widget.show(); 
	        else screenpoint_widget.hide(); 
	        toLayerTop(); }
	      else { screen_widget.hide(); screenpoint_widget.hide(); }
	      
	      pointwidget.hide();
	      thiswidget.hide();
	    } else { 
	      screen_widget.hide();
	      screenpoint_widget.hide();
	      if (show != null && show.get()) { 
	        if (show_dir) pointwidget.show();
	        else pointwidget.hide(); 
	        thiswidget.show();
	        toLayerTop();
	      } else {
	        pointwidget.hide(); 
	        thiswidget.hide();
	      }
	    }
	  }
	  
	  public void clear() { 
	    nRunnable.runEvents(eventClear);
	    cam.removeEventMove(move_run);
	    cam.removeEventZoom(zoom_run);
	    refwidget.clear(); pointwidget.clear(); screen_widget.clear(); screenpoint_widget.clear(); super.clear(); 
	    show.clear(); dval.clear(); pval.doEvent(false); pval.clear(); 
	  }
	  public nCursor toLayerTop() { 
	    super.toLayerTop(); refwidget.toLayerTop(); pointwidget.toLayerTop(); 
	    screen_widget.toLayerTop(); screenpoint_widget.toLayerTop(); 
	    return this; }
	}
