package z_old_specialise;


import java.util.ArrayList;

import Macro.Macro_Sheet;
import RApplet.sInterface;
import UI.*;
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
  
  public Simulation clear() {
    for (int i = list.size() - 1 ; i >= 0 ; i--) list.get(i).clear();
    super.clear();
    return this;
  }
  
  Simulation(sInterface _int, sValueBloc b) {
    super(_int.macro_main, "Sim", b);
    inter = _int;
    ref_size = inter.ref_size;
    cam_gui = inter.cam_gui;
    
    //setPosition(0, -ref_size*8);
    val_descr.set("Control time, reset, random...");
    tick_counter = newInt(0, "tick_counter", "tick");
    tick_by_frame = newFlt(2, "tick by frame", "tck/frm");
    tick_sec = newFlt(0, "tick seconde", "tps");
    pause = newBoo(false, "pause", "pause");
    force_next_tick = newInt(0, "force_next_tick", "nxt tick");
    auto_reset = newBoo(true, "auto_reset", "auto reset");
    auto_reset_rng_seed = newBoo(true, "auto_reset_rng_seed", "auto rng");
    auto_reset_screenshot = newBoo(false, "auto_rest_screenshot", "auto shot");
    show_com = newBoo(false, "show_com", "show");
    auto_reset_turn = newInt(4000, "auto_reset_turn", "auto turn");
    SEED = newInt(548651008, "SEED", "SEED");

    inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );
    inter.addToCamDrawerPile(new Drawable() { 
      public void drawing() { draw_to_cam(); } } );
    inter.addToScreenDrawerPile(new Drawable() { 
      public void drawing() { draw_to_screen(); } } );
    
    srun_tick = newRun("sim_tick", "tick", new nRunnable() { public void run() { } } );
    srun_reset = newRun("sim_reset", "reset", new nRunnable() { 
      public void run() { reset(); } } );
    srun_rngr = newRun("sim_rng_reset", "rst rng", new nRunnable() { 
      public void run() { resetRng(); } } );
    srun_nxtt = newRun("sim_next_tick", "nxt tck", new nRunnable() { 
      public void run() { force_next_tick.add(1); } } );
    srun_nxtf = newRun("sim_next_frame", "nxt frm", new nRunnable() { 
      public void run() { force_next_tick.set((int)(tick_by_frame.get())); } } );
    srun_scrsht = newRun("screen_shot", "impr", new nRunnable() { 
      public void run() { inter.cam.screenshot = true; } } );
    
    mmain().addEventSetupLoad(new nRunnable() { 
      public void run() { mmain().inter.addEventNextFrame(new nRunnable() { 
      public void run() { reset(); } } ); 
    } } );
      
    show_toolpanel = newBoo("show_toolpanel", "toolpanel", true);
    show_toolpanel.addEventChange(new nRunnable(this) { public void run() { 
      if (toolpanel != null && toolpanel.hide == show_toolpanel.get()) toolpanel.reduc();
    }});
    
//    build_toolpanel();
    
  }

  sInt tick_counter; //conteur de tour depuis le dernier reset ou le debut
  sBoo pause; //permet d'interompre le defilement des tour
  sInt force_next_tick; 
  sFlt tick_by_frame; //nombre de tour a executé par frame
  sFlt tick_sec;
  sInt SEED; //seed pour l'aleatoire
  sBoo auto_reset, auto_reset_rng_seed, auto_reset_screenshot, show_com;
  sInt auto_reset_turn;
  sRun srun_reset, srun_rngr, srun_nxtt, srun_nxtf, srun_tick, srun_scrsht;
  sBoo show_toolpanel;

  float tick_pile = 0; //pile des tick a exec

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

  void resetRng() { 
    SEED.set((int)(gui.app.random(1000000000))); 
    reset();
  }
  void reset() {
	  gui.app.randomSeed(SEED.get());
    tick_counter.set(0);
    inter.framerate.reset();
    for (Community c : list) c.reset();
    nRunnable.runEvents(eventsReset);
  }

  void frame() {
    if (!pause.get()) {
      tick_sec.set(inter.framerate.median_framerate.get() * tick_by_frame.get());
      
      tick_pile += tick_by_frame.get();

      //auto screenshot before reset
      if (auto_reset.get() && auto_reset_screenshot.get() &&
        auto_reset_turn.get() == tick_counter.get() + tick_by_frame.get() + tick_by_frame.get()) {
        inter.cam.screenshot = true;
      }

      while (tick_pile >= 1) {
        tick();
        tick_pile--;
      }

      //run_each_unpaused_frame
      nRunnable.runEvents(eventsUnpausedFrame);
    } else tick_sec.set(0);

    // tick by tick control
    if (pause.get() && force_next_tick.get() > 0) { 
      for (int i = 0; i < force_next_tick.get(); i++) tick(); 
      force_next_tick.set(0);
    }
    if (!pause.get() && force_next_tick.get() > 0) { 
      force_next_tick.set(0);
    }

    //run custom frame methods
    for (Community c : list) c.frame();
    nRunnable.runEvents(eventsFrame);
  }

  void tick() {

    //auto reset
    if (auto_reset.get() && auto_reset_turn.get() <= tick_counter.get()) {
      if (auto_reset_rng_seed.get()) {
        SEED.set((int)(gui.app.random(1000000000)));
      }
      reset();
    }

    //ticking_pile.tick();
    srun_tick.run();

    //tick communitys
    for (Community c : list) c.tick();

    //tick call
    nRunnable.runEvents(eventsTick);
    nRunnable.runEvents(eventsTick2);

    tick_counter.set(tick_counter.get()+1);
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
  
  //void build_toolpanel() {
  //  toolpanel = new nToolPanel(inter.screen_gui, ref_size, 0.125, false, false);
  //  toolpanel.addShelf()
  //    .addDrawer(10, 0.75)
  //    .addModel("Label-S4", "-  GROWERS  -").setFont(int(ref_size)).getShelf()
  //    .addSeparator(0.25)
  //    .addDrawer(10, 1)
  //    .addCtrlModel("Button-S3-P1", "RESET")
  //    .setRunnable(new nRunnable() {  public void run() {  reset(); } } ).getDrawer()
  //    .addCtrlModel("Button-S3-P2", "RESET RNG")
  //    .setRunnable(new nRunnable() { public void run() {  resetRng(); } } )
  //    .getShelf()
  //    .addDrawer(10, 1)
  //    .addCtrlModel("Button-S3-P1", "Quick Save")
  //    .setRunnable(new nRunnable() { 
  //    public void run() { 
  //      inter.full_data_save();
  //    }
  //  }
  //  ).getDrawer()
  //    .addCtrlModel("Button-S3-P2", "Quick Load")
  //    .setRunnable(new nRunnable() { 
  //    public void run() { 
  //      inter.setup_load();
  //    }
  //  }
  //  ).getDrawer().getShelf()
  //    .addDrawer(10, 1)
  //    .addLinkedModel("Button-S3-P1", "grid")
  //    .setLinkedValue(inter.cam.grid).getDrawer()
  //    .addLinkedModel("Button-S3-P2", "auto load")
  //    .setLinkedValue(inter.auto_load).getDrawer()
  //    .getShelfPanel()
  //    .addShelf()
  //    .addDrawer(10, 1)
  //    .addCtrlModel("Button-S2-P1", "tick").setRunnable(new nRunnable() { 
  //    public void run() { 
  //      force_next_tick.set(1);
  //    }
  //  }
  //  ).getDrawer()
  //    .addLinkedModel("Button-S2-P2", "PAUSE").setLinkedValue(pause).getDrawer()
  //    .addCtrlModel("Button-S2-P3", "frame").setRunnable(new nRunnable() { 
  //    public void run() { 
  //      force_next_tick.set(int(tick_by_frame.get()));
  //    }
  //  }
  //  ).getShelf()
  //    .addDrawer(10, 1)
  //    .addCtrlModel("Button-S1-P2", "<<").setLinkedValue(tick_by_frame).setFactor(0.5).getDrawer()
  //    .addCtrlModel("Button-S1-P3", "<").setLinkedValue(tick_by_frame).setFactor(0.8).getDrawer()
  //    .addWatcherModel("Label_Back-S2-P2", "--").setLinkedValue(tick_by_frame).getDrawer()
  //    .addCtrlModel("Button-S1-P7", ">").setLinkedValue(tick_by_frame).setFactor(1.25).getDrawer()
  //    .addCtrlModel("Button-S1-P8", ">>").setLinkedValue(tick_by_frame).setFactor(2).getShelf()
  //    .addDrawer(10, 1)
  //    .addCtrlModel("Button-S3-P1", "Sim").setRunnable(new nRunnable() { public void run() { 
  //      build_sheet_menu(); } } )
  //    .getDrawer()
  //    .addCtrlModel("Button-S3-P2", "Files").setRunnable(new nRunnable() { public void run() { 
  //      inter.filesManagement(); } } ).getShelf()
  //    .addDrawer(10, 1)
  //    .addWatcherModel("Label_Back-S3-P1")
  //    .setLinkedValue(inter.framerate.median_framerate).getDrawer()
  //    .addWatcherModel("Label_Back-S3-P2").setLinkedValue(tick_counter).getDrawer()
  //    .addLinkedModel("Button-S1-P9", "S")
  //    .setLinkedValue(show_com)
  //    .getShelfPanel()
  //    ;
    
  //  inter.screen_gui.addEventSetup(new nRunnable() { 
  //    public void run() { 
  //      //for (Blueprint c : com_blueprint) c.simPanelBuild(sim_front);

  //      //if (list.size() > 0) {
  //      //  update_com_selector_list();
  //      //}
  //      //build_sim_frontpanel(inter.screen_gui);
  //    }
  //  } 
  //  );  
    
  //  if (!show_toolpanel.get()) toolpanel.reduc();
  //  toolpanel.addEventReduc(new nRunnable() { public void run() { 
  //    show_toolpanel.set(!toolpanel.hide); }});
  //}
  
  public void build_custom_menu(nFrontPanel sheet_front) {
    nFrontTab tab = sheet_front.addTab("Control");

    tab.getShelf()
      .addDrawer(10.25, 0.6)
      .addModel("Label-S4", "- Simulation Control -").setFont((int)(ref_size/1.4)).getShelf()
      .addSeparator(0.125)
      .addDrawerWatch(tick_counter, 10, 1)
      .addSeparator(0.125)
      .addDrawerLargeFieldCtrl(SEED, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(tick_by_frame, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerIncrValue(auto_reset_turn, 1000, 10, 1)
      .addSeparator(0.125)
      .addDrawerDoubleButton(auto_reset, auto_reset_rng_seed, 10, 1)
      .addSeparator(0.125)
      .addDrawerDoubleButton(srun_scrsht, auto_reset_screenshot, 10, 1)
      .addSeparator(0.125)
      .addDrawerTripleButton(srun_reset, srun_rngr, srun_nxtt, 10, 1)
      .addSeparator(0.125)
      .addDrawerTripleButton(pause, show_com, inter.cam.grid, 10, 1)
      .addSeparator(0.125)
      ;
    
//    tab.getShelf(0).addSeparator(0.25)
//      .addDrawer(10.25, 0.75)
//      .addModel("Label-SS4", "- Active Community -").setFont((int)(ref_size/1.5)).getShelf()
//      ;
//      
//    selector_list = tab.getShelf(0)
//      .addSeparator(0.25)
//      .addList(5, 10, 1);
//    selector_list.addEventChange_Builder(new nRunnable() { public void run() {
//      nList sl = ((nList)builder); 
//      if (sl.last_choice_index < list.size()) 
//        list.get(sl.last_choice_index).build_sheet_menu();
//    } } );
//    
//    selector_list.getShelf()
//      .addSeparator(0.0625)
//      ;
//    
//    selector_entry = new ArrayList<String>(); // mmain().data.getCountOfType("flt")
//    selector_value = new ArrayList<Community>(); // mmain().data.getCountOfType("flt")
//  
//    update_com_selector_list();
    sheet_front.toLayerTop();
  }
//  void update_com_selector_list() {
//    selector_entry.clear();
//    selector_value.clear();
//    for (Community v : list) { 
//      selector_entry.add(v.name); 
//      selector_value.add(v);
//    }
//    if (selector_list != null) selector_list.setEntrys(selector_entry);
//  }

//  ArrayList<String> selector_entry;
//  ArrayList<Community> selector_value;
//  Community selected_value;
//  String selected_entry;
//  nList selector_list;

  sInterface inter;
  sValueBloc sbloc;
  nGUI cam_gui;
  float ref_size;
//  Ticking_pile ticking_pile;
//  Tickable macromain_tickable;

  ArrayList<Community> list = new ArrayList<Community>();
  ArrayList<Face> faces = new ArrayList<Face>();
  ArrayList<Organism> organs = new ArrayList<Organism>();
  
}








