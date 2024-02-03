package UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import RApplet.Rapp;
import RBase.RConst;
import sData.*;
import RApplet.sInput;
import processing.core.PApplet;
import processing.core.PVector;


















//manage look
//class sLook extends sValue {
//  String getString() { return ""; }
//  void clear() { }
//  nLook val = new nLook();
//  sLook(sValueBloc b, nLook v, String n, String s) { super(b, "look", n, s); val.copy(v); }
//  nLook get() { return val; }
//  void set(nLook v) { if (!v.ref.equals(val.ref)) has_changed = true; val.copy(v); }
//}


//colors n thickness
public class nLook {
  nLook copy(nLook l) {
    ref = l.ref.substring(0, l.ref.length());;
    standbyColor = l.standbyColor; pressColor = l.pressColor; 
    hoveredColor = l.hoveredColor; outlineColor = l.outlineColor;
    outlineSelectedColor = l.outlineSelectedColor; textColor = l.textColor;
    textFont = l.textFont; outlineWeight = l.outlineWeight;
    return this;
  }
  void clear() {}
  nLook(Rapp a) { 
	app = a; 
	standbyColor = app.color(80); hoveredColor = app.color(110); pressColor = app.color(130); 
	outlineColor = app.color(255); outlineSelectedColor = app.color(255, 255, 0); textColor = app.color(255);
  }
  public Rapp app;
  public String ref = "def";
  public int standbyColor, hoveredColor, pressColor, 
        outlineColor, outlineSelectedColor, textColor;
  public int textFont = 24; public float outlineWeight = 1;
}














/*
  Graph    > data structure and math objects
    Rect    axis aligned
      pvector pos, size
      collision to rect point ...
    Point, Circle, Line, Trig, Poly (multi trig)
    draw methods: rect(Rect), triangle(Trig), line(Line)
    special draw:
      different arrow, interupted circle (cible), 
      chainable outlined line witch articulation connectable to rect circle or trig
  
  

  Animation
    AnimationFrame     abstract void draw()
    list<animframe>
    draw() circle throug frame at each call
    
  
  
  Drawer
    abstract void draw()
    int layer
    DrawerPile
    bool show
    
    rect* view
    a Drawer can point to a rect that should contain the drawing. if the rect is out of a pre_selected rect, 
    or if he is too small he is passed. Maybe a Drawer can hold multiple methods for different level of zoom?
    This could allow large amount of small details. maybe passed and or far away from view drawer can 
    notify their creator for them to desactivate
  
  DrawerPile
    list<drawer>
    frame()
      run draw() for every drawer from the lowest layer so the top layer appear on top
      
  Hoverable
    point to a rect
    can be active pasif or background
    int layer    bool isfound
    
  HoverPile
    list<hover>
    hover founded
    event found, no find
    search(vector) 
      clear founded
      find the first hover under the point, search from the top layer to the down, set as founded
      stop if it found a background hover
*/






// systemes pour organiser l'ordre d'execution de different trucs en layer:

//drawing









//parmi une list de rect en layer lequel est en collision avec un point en premier








//execution ordonné en layer et timer
//abstract class Tickable {
//  Ticking_pile pile = null;
//  int layer;
//  boolean active = true;
//  Tickable() {}
//  Tickable(Ticking_pile p) {
//    layer = 0;
//    pile = p;
//    pile.tickables.add(this);
//  }
//  Tickable(Ticking_pile p, int l) {
//    layer = l;
//    pile = p;
//    pile.tickables.add(this);
//  }
//  void clear() { if (pile != null) pile.tickables.remove(this); }
//  Tickable setLayer(int l) {
//    layer = l;
//    return this;
//  }
//  abstract void tick(float time);
//}
//
//class Ticking_pile {
//  ArrayList<Tickable> tickables = new ArrayList<Tickable>();
//  float current_time = 0;
//  float prev_time = 0;
//  float frame_length = 0;
//  Ticking_pile() { }
//  void tick() {
//    current_time = millis();
//    frame_length = current_time - prev_time;
//    prev_time = current_time;
//    int layer = 0;
//    int run_count = 0;
//    while (run_count < tickables.size()) {
//      for (Tickable r : tickables) {
//        if (r.layer == layer) {
//          if (r.active) r.tick(frame_length);
//          run_count++;
//        }
//      }
//      layer++;
//    }
//  }
//}














 
