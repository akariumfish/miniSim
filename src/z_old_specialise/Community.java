package z_old_specialise;

import java.util.ArrayList;

import Macro.Macro_Sheet;
import RApplet.Rapp;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nList;
import sData.nRunnable;
import sData.sBoo;
import sData.sInt;
import sData.sRun;
import sData.sStr;
import sData.sValueBloc;

public abstract class Community extends Macro_Sheet {
	  
	  public Community clear() {
	    sim.list.remove(this);
	    adding_cursor.clear();
	    super.clear();
	    return this;
	  }

	  abstract void comPanelBuild(nFrontPanel front);
	  
	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Community");
	    tab.getShelf()
	      .addDrawer(10.25, 0.75)
	      .addModel("Label-S4", "-"+name+" Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerWatch(active_entity, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(max_entity, 100, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(adding_entity_nb, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(adding_step, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(pulse_add, auto_add, srun_add, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(show_entity, adding_cursor.show, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(pulse_add_delay, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(pulse_add_delay, 1000, 10, 1)
	      .addSeparator(0.125)
	      ;
	      
	    selector_list = tab.getShelf(0)
	      .addSeparator(0.25)
	      .addList(3, 10, 1);
	    selector_list.addEventChange_Builder(new nRunnable() { public void run() {
	      nList sl = ((nList)builder); 
	      //logln("a "+sl.last_choice_index +"  "+ sim.list.size());
	      if (sl.last_choice_index < sim.list.size()) 
	        selected_comu(sim.list.get(sl.last_choice_index));
	        selected_com.set(sim.list.get(sl.last_choice_index).name);
	        search_com();
	    } } );
	        
	    selector_list.getShelf()
	      .addSeparator(0.125)
	      .addDrawer(10.25, 0.75)
	      .addWatcherModel("Label-S4", "Selected: ").setLinkedValue(selected_com).getShelf()
	      .addSeparator(0.125)
	      ;
	    
	    selector_entry = new ArrayList<String>(); // mmain().data.getCountOfType("flt")
	    selector_value = new ArrayList<Community>(); // mmain().data.getCountOfType("flt")
	    
	    update_com_selector_list();
	    
	    comPanelBuild(sheet_front);
	    if (sheet_front.collapsed) {
	    		sheet_front.popUp();
	    		sheet_front.collapse();
	    } else {
    			sheet_front.collapse();
    			sheet_front.popUp();
	    }
	    sheet_front.toLayerTop();
	  }
	  void update_com_selector_list() {
	    selector_entry.clear();
	    selector_value.clear();
	    for (Community v : sim.list) { 
	      selector_entry.add(v.name); 
	      selector_value.add(v);
	    }
	    if (selector_list != null) selector_list.setEntrys(selector_entry);
	  }
	  
	  void search_com() { 
	    sim.inter.addEventNextFrame(new nRunnable() {public void run() { 
	    sim.inter.addEventNextFrame(new nRunnable() {public void run() { 
	      //logln(value_bloc.ref + " search " + selected_com.get());
	      for (Community c : sim.list) {
	        //log(value_bloc.ref + " try " + c.value_bloc.ref);
	        if (c.name.equals(selected_com.get())) { 
	          //log(" found"); 
	        selected_comu(c); }
	        //logln("");
	      }
	    }});
	    }});
	  }
	  
	  void selected_comu(Community c) {}
	  
	  ArrayList<String> selector_entry;
	  ArrayList<Community> selector_value;
	  Community selected_value;
	  String selected_entry;
	  nList selector_list;


	  Simulation sim;
	  Rapp app;
	  String name = "";
	  String type;

	  public ArrayList<Entity> list = new ArrayList<Entity>(); //contien les objet

	  sInt max_entity; //longueur max de l'array d'objet
	  sInt active_entity, adding_entity_nb, adding_step; // add one new object each adding_step turn
	  int adding_pile = 0;
	  int adding_counter = 0;
	  
	  sInt pulse_add_delay;
	  sBoo auto_add, pulse_add;
	  int pulse_add_counter = 0;

	  sBoo show_entity;
	  sRun srun_add;
	  sStr type_value, selected_com;
	  
	  public sInt val_draw_layer;

	  nCursor adding_cursor;
	  
	  Community(Simulation _c, String n, String ty, int max, sValueBloc b) { 
	    super(_c.inter.macro_main, n, b);
	    sim = _c; 
	    app = gui.app;
	    name = value_bloc.ref;
	    sim.list.add(this);
	    type = ty;
	    
	    max_entity = newInt(max, "max_entity", "max_entity");
	    type_value = newStr("type", "type", ty);
	    selected_com = newStr("selected_com", "scom", "");
	    active_entity = newInt(0, "active_entity ", "active_pop");
	    adding_entity_nb = newInt(0, "adding_entity_nb ", "add nb");
	    adding_step = newInt(0, "adding_step ", "add stp");
	    show_entity = newBoo(true, "show_entity ", "show");
	    auto_add = newBoo(true, "auto_add ", "auto_add");
	    pulse_add = newBoo(true, "pulse_add ", "pulse");
	    pulse_add_delay = newInt(100, "pulse_add_delay ", "pulseT");
	    
	    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");

	    adding_cursor = newCursor(n, true);

	    srun_add = newRun("add_entity", "add_pop", new nRunnable() { 
	      public void run() { 
	        adding_pile += adding_entity_nb.get();
	      }
	    }
	    );
	    
	    
	    addEventSetupLoad(new nRunnable() { public void run() { 
	      search_com(); } } );
	      
	    search_com();

	    reset();
	  }

	  Community show_entity() { 
	    show_entity.set(true); 
	    return this;
	  }
	  Community hide_entity() { 
	    show_entity.set(false); 
	    return this;
	  }

	  void custom_reset() {
	  }
	  void custom_frame() {
	  }
	  abstract void custom_pre_tick();
	  abstract void custom_post_tick();
	  abstract void custom_cam_draw_pre_entity();
	  abstract void custom_cam_draw_post_entity();
	  void custom_screen_draw() {
	  }

	  void init_array() {
	    list.clear();
	    for (int i = 0; i < max_entity.get(); i++)
	      list.add(build());
	  }

	  void reset() { //deactivate all then create starting situation from parameters
	    this.destroy_All();
	    if (max_entity.get() != list.size()) init_array();
	    if (auto_add.get()) adding_pile += adding_entity_nb.get();
	    custom_reset();
	  }

	  void frame() {
	    custom_frame();
	    for (Entity e : list) if (e.active) e.frame();
	  }

	  void tick() {
	    if (auto_add.get() && pulse_add.get()) {
	      pulse_add_counter++;
	      if (pulse_add_counter > pulse_add_delay.get()) { pulse_add_counter = 0; srun_add.run(); }
	    }
	    if (auto_add.get() && adding_counter > 0) adding_counter--;
	    while (auto_add.get() && adding_counter == 0 && adding_pile > 0) {
	      adding_counter += adding_step.get();
	      adding_pile--;
	      addEntity();
	    }
	    active_entity.set(active_Entity_Nb());
	    custom_pre_tick();
	    for (Entity e : list) if (e.active) e.tick();
	    for (Entity e : list) if (e.active) e.age++;
	    custom_post_tick();
	  }

	  void draw_Cam() { 
	    if (show_entity.get()) for (Entity e : list) if (e.active) e.draw();
	  }
	  void draw_Screen() { 
	    custom_screen_draw();
	  }

	  void destroy_All() { 
	    for (Entity e : list) e.destroy();
	  }

	  int active_Entity_Nb() {
	    int n = 0;
	    for (Entity e : list) if (e.active) n++;
	    return n;
	  }

	  abstract Entity build();
	  abstract Entity addEntity();
	}






