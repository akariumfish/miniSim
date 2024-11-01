package z_old_specialise;


import java.util.ArrayList;

import Macro.Macro_Sheet;
import RApplet.sInterface;
import UI.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import sData.*;
import z_old_specialise.Community;

/*                          RENAME IT " TIME "

 Simulation(Input, Data, Interface)
 Build with interface
 toolPanel down left to down center with main function
 right
 next tick,  pause,  next frame
 tick/frame            5 widget
 time counter,     tick counter
 framerate,            tickrate
 
 down left
 Hide all
 
 left
 open menus      <align to panel top
 title
 Quick save, load
 restart,    RNG
 
 time control (tick by frame, pause, trigger tick by tick or frame by frame)
 restart control and RNG
 Quick save / load
 button to hide all guis (toolpanel reducted)
 Openning main dropdown menu to open main panels
 file selection panel
 communitys panel
 open save / load parameters panel
 basic info n param
 completed by each community type
 shortcut panel
 can link key to preselected button
 taskBar on down right side
 SelectZone working in camera
 Info
 TickPile
 one Drawer for all communitys in camera drawerpile
 simpler before more coding
 community
 has an adding point as an svalue and grabbable
 grower as also an adding direction
 floc an adding radius
 Entity
 position, direction, size
 custom parameters
 list of geometrical shapes and colors
 shapes contain energy???                            <<<<<< THE GAME MECHANIC MAKE HER ENTRY
 to excenge energy throug macro link output need a received method called by receiving input to
 confirm transfer 
 screen width 1200
 draw : invisible, particle 1px, pebble 5px, small 25px, med 100px, 
 big 400px, fullscreen 1100px, zoom in 3000px, micro 10 000px, too big 100 000px
 frame()
 drive ticking
 macro_main
 each community have her sheet inside whom community param and runnable can be acsessed
 maybe for each community her is an independent macro space who can acsess an entity
 property and who can be applyed to each entity of this commu
 there can be plane who take entity from different commu to make them interact
 */



public class Simulation extends Macro_Sheet {
	
	public static class SimPrint extends Sheet_Specialize {
		public SimPrint() { super("Sim"); }
		  public void default_build() { }
	  public Simulation get_new(Macro_Sheet s, String n, sValueBloc b) { return new Simulation(mmain.inter, b); }
	}

  sInterface inter;
  sValueBloc sbloc;
  nGUI cam_gui;
  float ref_size;

  ArrayList<Community> list = new ArrayList<Community>();
  ArrayList<Face> faces = new ArrayList<Face>();
  ArrayList<Organism> organs = new ArrayList<Organism>();

  sBoo show_com;
  
  ArrayList<nRunnable> eventsReset = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsFrame = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsUnpausedFrame = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsTick = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsTick2 = new ArrayList<nRunnable>();
  Simulation addEventReset(nRunnable r) { eventsReset.add(r); return this; }
  Simulation removeEventReset(nRunnable r) { eventsReset.remove(r); return this; }
  Simulation addEventFrame(nRunnable r) { eventsFrame.add(r); return this; }
  Simulation addEventUnpausedFrame(nRunnable r) { eventsUnpausedFrame.add(r); return this; }
  Simulation addEventTick(nRunnable r) { eventsTick.add(r); return this; }
  Simulation removeEventTick(nRunnable r) { eventsTick.remove(r); return this; }
  Simulation addEventTick2(nRunnable r) { eventsTick2.add(r); return this; }
  Simulation removeEventTick2(nRunnable r) { eventsTick2.remove(r); return this; }

  Simulation(sInterface _int, sValueBloc b) {
    super(_int.macro_main, "Sim", b);
    inter = _int;
    ref_size = inter.ref_size;
    cam_gui = inter.cam_gui;
    
    val_descr.set("Control time, reset, random...");
    show_com = newBoo(false, "show_com", "show");

    inter.addToCamDrawerPile(new Drawable() { 
      public void drawing() { draw_to_cam(); } } );
    inter.addToScreenDrawerPile(new Drawable() { 
      public void drawing() { draw_to_screen(); } } );

    inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );
    mmain().addEventReset(new nRunnable() { public void run() { reset(); } } );
    mmain().addEventTick(new nRunnable() { public void run() { tick(); } } );
    
  }

  public Simulation clear() {
    for (int i = list.size() - 1 ; i >= 0 ; i--) list.get(i).clear();
    super.clear();
    return this;
  }
  
  void reset() {
	  
    for (Community c : list) c.reset();
    nRunnable.runEvents(eventsReset);
  }

  void frame() {
    if (!mmain().pause.get()) {
    	
      //run_each_unpaused_frame
      nRunnable.runEvents(eventsUnpausedFrame);
    } 

    //run custom frame methods
    for (Community c : list) c.frame();
    nRunnable.runEvents(eventsFrame);
  }

  void tick() {

    //tick communitys
    for (Community c : list) c.tick();

    //tick call
    nRunnable.runEvents(eventsTick);
    nRunnable.runEvents(eventsTick2);

  }

  void draw_to_cam() { 
	  
    if (show_com.get()) {
      
      if (faces.size() > 0 || list.size() > 0 || organs.size() > 0) {
        int min = 0; 
        if (faces.size() > 0) min = faces.get(0).val_draw_layer.get();
        else 
        	if (list.size() > 0) min = list.get(0).val_draw_layer.get();
        else if (organs.size() > 0) min = organs.get(0).val_draw_layer.get();
        int max = min;
        for (Face f : faces) {
          min = Math.min(min, f.val_draw_layer.get()); max = Math.max(max, f.val_draw_layer.get()); }
        for (Community f : list) {
          min = Math.min(min, f.val_draw_layer.get()); max = Math.max(max, f.val_draw_layer.get()); }
        for (Organism f : organs) {
          min = Math.min(min, f.val_draw_layer.get()); max = Math.max(max, f.val_draw_layer.get()); }
        for (int i = min ; i <= max ; i++) {
          for (Face f : faces) if (f.val_draw_layer.get() == i) f.draw(gui.app);
          for (Organism f : organs) if (f.val_draw_layer.get() == i) f.draw_All();
          for (Community f : list) if (f.val_draw_layer.get() == i) f.custom_cam_draw_pre_entity();
          for (Community f : list) if (f.val_draw_layer.get() == i) f.draw_Cam();
          for (Community f : list) if (f.val_draw_layer.get() == i) f.custom_cam_draw_post_entity();
        }
      }
    }
    
    
  }
  void draw_to_screen() { 
    for (Community c : list) if (c.show_entity.get()) c.draw_Screen();
  }
  
  
  nToolPanel toolpanel;
  
  public void build_custom_menu(nFrontPanel sheet_front) {
    nFrontTab tab = sheet_front.addTab("Control");

    tab.getShelf()
      .addDrawer(10.25, 0.6)
      .addModel("Label-S4", "- Simulation Control -").setFont((int)(ref_size/1.4)).getShelf()
      .addSeparator(0.125)
      .addDrawerDoubleButton(show_com, inter.cam.grid, 10, 1)
      .addSeparator(0.125)
      ;
    
    sheet_front.toLayerTop();
  }
  
}








