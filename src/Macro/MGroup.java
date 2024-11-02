package Macro;

import java.util.ArrayList;

import RApplet.Rapp;
import RApplet.sInterface;
import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import sData.*;

public abstract class MGroup extends MBaseMT { 

	abstract void comPanelBuild(nFrontPanel front);
	  
	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Community");
	    tab.getShelf()
	      .addDrawer(10.25, 0.75)
	      .addModel("Label-S4", "-"+value_bloc.ref+" Control-")
	      	.setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
//	      .addDrawerWatch(active_entity, 10, 1)
//	      .addSeparator(0.125)
	      .addDrawerIncrValue(max_entity, 100, 10, 1)
	      .addSeparator(0.125)
//	      .addDrawerIncrValue(adding_entity_nb, 10, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerIncrValue(adding_step, 10, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerButton(pulse_add, auto_add, srun_add, 10, 1)
//	      .addSeparator(0.125)
	      .addDrawerButton(show_entity, val_show_grab, 10, 1)
	      .addSeparator(0.125)
//	      .addDrawerIncrValue(pulse_add_delay, 10, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerIncrValue(pulse_add_delay, 1000, 10, 1)
//	      .addSeparator(0.125)
	      ;
	      
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
	  
	  sInterface inter;
	  nGUI cam_gui;
	  float ref_size;
	  Drawable drawable;
	  nCursor ref_cursor;
		
	  sVec val_pos;
	  sBoo val_show_grab;
	  
	  Macro_Connexion link_group;
	
	MGroup(Macro_Sheet _sheet, String typ, sValueBloc _bloc) { 
		super(_sheet, typ, _bloc); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		

	    val_pos = newVec("val_pos", "val_pos");
	    val_show_grab = newBoo(true, "val_show_grab", "show_grab");
	    ref_cursor = sheet.menuCursor(value_bloc.ref, false);

	    val_show_grab.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.show != null) ref_cursor.show.set(val_show_grab.get());
	    } });
	    if (ref_cursor.show != null) ref_cursor.show.set(val_show_grab.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.addEventChange(new nRunnable() { public void run() { 
	      val_pos.set(ref_cursor.pval.get()); }});
	    val_pos.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get()); }});
	    

		drawable = new Drawable() { public void drawing() { draw_Cam(); } } ;
		mmain().inter.addToCamDrawerPile(drawable);
		drawable.setLayer(priority.get());
		priority.addEventChange(new nRunnable() { public void run() { 
			drawable.setLayer(priority.get()); }});
		
		
		
	    inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );

	    init_group();
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	  super.build_normal(); 
	  link_group = addOutput(0, "Link").set_link();
	}
	void init_end() { 
		super.init_end(); 
	}
	void rebuild() { 
		super.rebuild(); 
	}
	public MGroup clear() {
		super.clear(); 
		drawable.clear();
		ref_cursor.clear();
//		for (Entity e : entity_list) e.clear();
//		entity_list.clear();
		return this; }
	public MGroup toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	
	
	

	  ArrayList<Entity> entity_list; //contien les objet

	  sInt max_entity; //longueur max de l'array d'objet
//	  sInt active_entity, adding_entity_nb, adding_step; // add one new object each adding_step turn
//	  int adding_pile = 0;
//	  int adding_counter = 0;
//	  
//	  sInt pulse_add_delay;
//	  sBoo auto_add, pulse_add;
//	  int pulse_add_counter = 0;

	  sBoo show_entity;
//	  sRun srun_add;
//	  sStr type_value;
	  
//	  public sInt val_draw_layer;

	  void init_group() {
		  
		  entity_list = new ArrayList<Entity>();
	    
	    max_entity = newInt(100, "max_entity", "max_entity");
//	    active_entity = newInt(0, "active_entity ", "active_pop");
//	    adding_entity_nb = newInt(0, "adding_entity_nb ", "add nb");
//	    adding_step = newInt(0, "adding_step ", "add stp");
	    show_entity = newBoo(true, "show_entity ", "show");
//	    auto_add = newBoo(false, "auto_add ", "auto_add");
//	    pulse_add = newBoo(false, "pulse_add ", "pulse");
//	    pulse_add_delay = newInt(100, "pulse_add_delay ", "pulseT");
	    
//	    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");

//	    srun_add = newRun("add_entity", "add_pop", new nRunnable() { 
//	      public void run() { 
//	        adding_pile += adding_entity_nb.get();
//	      }
//	    }
//	    );
	    
	    reset();
	  }

	  MGroup show_entity() { 
	    show_entity.set(true); 
	    return this;
	  }
	  MGroup hide_entity() { 
	    show_entity.set(false); 
	    return this;
	  }

	  void custom_reset() {}
	  void custom_frame() {}
	  abstract void custom_pre_tick();
	  abstract void custom_post_tick();
	  abstract void custom_cam_draw_pre_entity();
	  abstract void custom_cam_draw_post_entity();
//	  void custom_screen_draw() {}

	  void init_array() {
		entity_list.clear();
	    for (int i = 0; i < max_entity.get(); i++)
	    		entity_list.add(build());
	  }

	  void reset() { //deactivate all then create starting situation from parameters
	    this.destroy_All();
	    if (max_entity.get() != entity_list.size()) init_array();
//	    if (auto_add.get()) adding_pile += adding_entity_nb.get();
	    custom_reset();
	  }

	  void frame() {
	    custom_frame();
	    for (Entity e : entity_list) if (e.active) e.frame();
	  }

	  void tick() {
//	    if (auto_add.get() && pulse_add.get()) {
//	      pulse_add_counter++;
//	      if (pulse_add_counter > pulse_add_delay.get()) { pulse_add_counter = 0; srun_add.run(); }
//	    }
//	    if (auto_add.get() && adding_counter > 0) adding_counter--;
//	    while (auto_add.get() && adding_counter == 0 && adding_pile > 0) {
//	      adding_counter += adding_step.get();
//	      adding_pile--;
//	      addEntity();
//	    }
//	    active_entity.set(active_Entity_Nb());
	    custom_pre_tick();
	    for (Entity e : entity_list) if (e.active) e.tick();
//	    for (Entity e : list) if (e.active) e.age++;
	    custom_post_tick();
	  }

	  void draw_Cam() { 
		custom_cam_draw_pre_entity();
	    if (show_entity.get()) for (Entity e : entity_list) if (e.active) e.draw();
	    custom_cam_draw_post_entity();
	  }
//	  void draw_Screen() { 
//	    custom_screen_draw();
//	  }

	  void destroy_All() { 
	    for (Entity e : entity_list) e.destroy();
	  }

	  int active_Entity_Nb() {
	    int n = 0;
	    for (Entity e : entity_list) if (e.active) n++;
	    return n;
	  }

	  abstract Entity build();
	  abstract Entity addEntity();
}

abstract class Entity {
	MGroup com;
	Rapp app;
    boolean active = false;
    Entity(MGroup c) { 
    		com = c; 
    }
    Entity activate() {
    		if (!active) { 
    			active = true; 
    			init();
    		}
    		return this;
    }
    Entity destroy() {
    		if (active) { 
    			active = false; 
    			clear();
    		}
    		return this;
    }
    abstract Entity tick();     //exec by community 
    abstract Entity frame();    //exec by community 
    abstract Entity draw();    //exec by community 
    abstract Entity init();     //exec by activate and community.reset
    abstract Entity clear();    //exec by destroy
}
