package UI;

import java.util.ArrayList;

import RApplet.Rapp;
import RApplet.sInput;
import processing.core.PVector;
import sData.nRunnable;

/*

Global GUI Theme 
 can be picked from by widgets 
 ? list of widgets to update when is changed ?
 map of color and name
 map of size and name
 map<name, widget> models
 methods to directly build a widget from models
 
 


*/










//drawing point
public class nGUI {
  public nGUI setMouse(PVector v) { mouseVector = v; return this; }
  public nGUI setpMouse(PVector v) { pmouseVector = v; return this; }
  public nGUI setView(Rect v) { view = v; updateView(); return this; }
  public nGUI updateView() { 
	  scale = app.screen_width / view.size.x; 
	  return this; }
  public nGUI setTheme(nTheme v) { theme = v; return this; }
  public nGUI addEventFrame(nRunnable r) { eventsFrame.add(r); return this; }
  public nGUI removeEventFrame(nRunnable r) { eventsFrame.remove(r); return this; }
  public nGUI addEventsFullScreen(nRunnable r) { eventsFullScreen.add(r); return this; }
  public nGUI addEventFound(nRunnable r) { hoverable_pile.addEventFound(r); return this; }
  public nGUI addEventNotFound(nRunnable r) { hoverable_pile.addEventNotFound(r); return this; }
  public nGUI addEventSetup(nRunnable r) { eventsSetup.add(r);  return this; }
  public Rapp app;
  public nGUI(sInput _i, nTheme _t, float _ref_size) {
    in = _i; theme = _t; if (theme == null) theme = new nTheme(in.app, _ref_size);
    app = in.app;
    mouseVector = in.mouse; pmouseVector = in.pmouse;
    ref_size = _ref_size;
//    view = new Rect(0, 0, app.width, app.height);
    hoverable_pile = new Hoverable_pile(app);
    info = new nInfo(this, ref_size*0.75F);
//    addEventsFullScreen(new nRunnable(this) { public void run() { 
//      view.size.set(app.width, app.height); updateView();
//    } } );
  }
  
  public sInput in;
  public nTheme theme;
  public nInfo info;
  public Rect view;
  public float scale = 1;
  public float ref_size = 30;
  public boolean isShown = true;
  public boolean field_used = false;
  
  public Drawing_pile drawing_pile = new Drawing_pile();
  public Hoverable_pile hoverable_pile;// = new Hoverable_pile();
  
  public ArrayList<nRunnable> eventsFrame = new ArrayList<nRunnable>();
  public ArrayList<nRunnable> eventsFullScreen = new ArrayList<nRunnable>();
  public ArrayList<nRunnable> eventsSetup = new ArrayList<nRunnable>();
  public boolean is_starting = true;
  public PVector mouseVector = null, pmouseVector = null;
  public boolean hoverpile_passif = false;
  
  public void frame() {
    hoverable_pile.search(mouseVector, hoverpile_passif);
    if (is_starting) { is_starting = false; nRunnable.runEvents(eventsSetup); }
    nRunnable.runEvents(eventsFrame); 
    nRunnable.clearEvents(eventsFrame); 
    //logln(""+eventsFrame.size());
  }
  public void draw() {
    if (isShown) drawing_pile.drawing(); 
    for (PVector p : debugpoint) app.ellipse(p.x, p.y, 20, 20);
    debugpoint.clear();
  }
  public ArrayList<PVector> debugpoint = new ArrayList<PVector>();
  
  public void point(float x, float y) {
	  debugpoint.add(new PVector(x, y));
  }
}
