package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
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
	      .addDrawerButton(show_entity, val_show_grab, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(pulse_add_delay, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(pulse_add_delay, 1000, 10, 1)
	      .addSeparator(0.125)
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
	  
	  Macro_Connexion link_in,link_out;
	
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
	  link_out = addOutput(1, "Link_out").set_link();
	  link_in = addInput(0, "Link_in").set_link();
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
		for (Entity e : entity_list) e.clear();
		entity_list.clear();
		return this; }
	public MGroup toLayerTop() {
		super.toLayerTop(); 
		return this;
	}

	  ArrayList<Entity> entity_list; //contien les objet

	  sInt max_entity; //longueur max de l'array d'objet
	  sInt active_entity, adding_entity_nb, adding_step; // add one new object each adding_step turn
	  int adding_pile = 0;
	  int adding_counter = 0;
	  
	  sInt pulse_add_delay;
	  sBoo auto_add, pulse_add;
	  int pulse_add_counter = 0;

	  sBoo show_entity;
	  sRun srun_add;
	  sStr type_value;
	  
	  public sInt val_draw_layer;

	  void init_group() {
		  
		  entity_list = new ArrayList<Entity>();
	    
	    max_entity = newInt(100, "max_entity", "max_entity");
	    active_entity = newInt(0, "active_entity ", "active_pop");
	    adding_entity_nb = newInt(1, "adding_entity_nb ", "add nb");
	    adding_step = newInt(0, "adding_step ", "add stp");
	    show_entity = newBoo(true, "show_entity ", "show");
	    auto_add = newBoo(true, "auto_add ", "auto_add");
	    pulse_add = newBoo(false, "pulse_add ", "pulse");
	    pulse_add_delay = newInt(100, "pulse_add_delay ", "pulseT");
	    
	    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");

	    srun_add = newRun("add_entity", "add_pop", new nRunnable() { 
	      public void run() { 
	        adding_pile += adding_entity_nb.get(); } } );
	    
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
	    for (int i = 0; i < max_entity.get(); i++)
    			entity_list.get(i).id = i;
	  }

	  void reset() { //deactivate all then create starting situation from parameters
	    this.destroy_All();
	    if (max_entity.get() != entity_list.size()) init_array();
	    if (auto_add.get()) adding_pile += adding_entity_nb.get();
	    custom_reset();
	  }

	  void frame() {
	    custom_frame();
	    for (Entity e : entity_list) if (e.active) e.frame();
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
    int id = 0;
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


class ESolid extends Entity {
	  PVector pos = new PVector(0, 0);
	  PVector mov = new PVector(0, 0);
	  PVector accel = new PVector(0, 0);
	  float radius;

	  PVector prev_pos = new PVector(0, 0);
	  float squared_speed = 0;

	  float halo_size = 0;
	  float halo_density = 0;
	  int halo_col = 0, fill_col = 0;
	  
	  ArrayList<ESolid> close_list = new ArrayList<ESolid>();
	  
	  ESolid(MSolidGroup c) { 
		super(c);
	  }
	  ESolid init() {
	    pos.set(0, 0);
	    prev_pos.set(0, 0);
	    mov.set(0, 0);
	    accel.set(0, 0);
	    radius = 10;
	    com().solid_list.add(this); 
	    halo_size = com().halo_size.get();
	    halo_density = com().halo_dens.get();
	    halo_size += com().app.random(com().halo_size.get());
	    halo_density += com().app.random(com().halo_dens.get());
	    halo_col = rngCol(com().val_col_halo1.get(), com().val_col_halo2.get());
	    fill_col = rngCol(com().fill_col1.get(), com().fill_col2.get());
	    return this;
	  }
	  
	  boolean collision(ESolid s) {
		  if (s == this) return true;
		  if (s.radius + radius > PApplet.dist(pos.x, pos.y, s.pos.x, s.pos.y)) return true;
		  return false;
	  }
	  
	  int rngCol(int c1, int c2) {
		int r = (int)com().app.random(com().app.red(c1) -
	    		com().app.red(c2));
	    int g = (int)com().app.random(com().app.green(c1) -
	    		com().app.green(c2));
	    int b = (int)com().app.random(com().app.blue(c1) -
	    		com().app.blue(c2));
	    r += com().app.red(c2);
	    g += com().app.green(c2);
	    b += com().app.blue(c2);
	    return com().app.color(r, g, b);
	  }

	  int gradedCol(int c1, int c2, float p) {
		int r = (int)(p * (com().app.red(c1) -
	    		com().app.red(c2)));
	    int g = (int)(p * (com().app.green(c1) -
	    		com().app.green(c2)));
	    int b = (int)(p * (com().app.blue(c1) -
	    		com().app.blue(c2)));
	    r += com().app.red(c2);
	    g += com().app.green(c2);
	    b += com().app.blue(c2);
	    return com().app.color(r, g, b);
	  }
	  
	  void draw_halo(MCanvas canvas) {
		  float speed_factor = ( squared_speed - com().val_sqspeed_min.get() ) / 
				  	( com().val_sqspeed_max.get() - com().val_sqspeed_min.get() );
		  halo_col = gradedCol(com().val_col_halo1.get(), com().val_col_halo2.get(), speed_factor);
	      
//		  if (canvas != null) 
//			  canvas.draw_line_halo(prev_pos, pos, halo_size, halo_density, halo_col, 
//					  				com().fracture_halo.get() );
	  }
	  
	  ESolid frame() { return this; }
	  
	  ESolid tick() {
		mov.add(accel);
		prev_pos.set(pos);
		pos.add(mov);
		
		squared_speed = com().app.squared_mag(mov);
//		if (com().sq_speed_min == 0) com().sq_speed_min = squared_speed;
//		if (com().sq_speed_min > squared_speed) com().sq_speed_min = squared_speed;
//		if (com().sq_speed_max < squared_speed) com().sq_speed_max = squared_speed;
		
		// friction
	    mov.mult(1 - com().friction_fact.get());
	    
	    accel.set(0, 0);
	    
//	    if (com().use_merge.get()) {
//		    cnt++;
//		    if (cnt > 40 && this != com().ball_obj && radius < com().ball_rad.get() * 0.9F) {
//		    		cnt = 0;
//			    for (Entity e : com().entity_list) if (e.active) {
//			    		ESolid s = (ESolid)e;
//			    		if (s != com().ball_obj && s.radius < com().ball_rad.get() * 0.9F) {
//				    		float d = PApplet.dist(pos.x, pos.y, s.pos.x, s.pos.y);
//				    		if (s.radius < radius && d < radius + s.radius) {
//				    			if (close_list.contains(s)) {
//				    				close_list.remove(s);
//				    				radius += s.radius / 1.0F;
//		//		    				mov.add(s.mov);
//				    				s.destroy();
//				    			} else close_list.add(s);
//				    		} else {
//				    			if (close_list.contains(s)) close_list.remove(s);
//				    		}
//			    		}
//			    } 
//		    }
//	    }
	    return this;
	  }
	  
	  int close_cnt = 0;
	  
	  int cnt = 0;
	  ESolid draw() {
		  if (com().show_fill.get()) {
			  float close_fact = PApplet.min(1, close_cnt / com().full_pack_nb.get());
			  fill_col = gradedCol(com().fill_col1.get(), com().fill_col2.get(), close_fact);
		      
			  com().app.fill(fill_col); 
		  }
		  else com().app.noFill();
		  if (com().show_stroke.get()) com().app.stroke(com().stroke_col.get()); 
		  else com().app.noStroke();
		  com().app.strokeWeight(com().stroke_width.get()/com.cam_gui.scale);
		  com().app.pushMatrix();
		  com().app.translate(pos.x, pos.y);
		  com().app.ellipse(-radius/2, -radius/2, radius, radius);
	      com().app.popMatrix();
	      return this;
	  }
	  ESolid clear() { com().solid_list.remove(this); return this; }
	  MSolidGroup com() { return ((MSolidGroup)com); }
	  
	}


	class MSolidGroup extends MGroup { 
		static class Builder extends MAbstract_Builder {
			Builder(Macro_Main m) { super("solids", "SolidGroup", "", "Work"); 
			first_start_show(m); }
			MSolidGroup build(Macro_Sheet s, sValueBloc b) { 
				MSolidGroup m = new MSolidGroup(s, b); return m; }
		}
		
		public void comPanelBuild(nFrontPanel sim_front) {

		  sim_front.getTab(2).getShelf()
		  	.addDrawerButton(show_fill, show_stroke, fracture_halo, 10.25F, 1)
		    .addSeparator(0.125)
		    ;
		
	      nFrontTab tab = sim_front.addTab("Create");
	      tab.getShelf()
	      	.addDrawerButton(build_as, del_all, 10.25F, 1)
	      	.addSeparator(0.125)
	      	.addDrawerButton(reset_build, 10.25F, 1)
	      	.addSeparator(0.125)
	      	.addDrawerButton(build_as_box, build_rng, 10.25F, 1)
	      	.addSeparator(0.125)
	        .addDrawerIncrValue(strtbox_width, 10, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(strtbox_space, 2, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(strtbox_space, 1.18F, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(solid_rad, 2, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(solid_rad, 1.18F, 10, 1)
	        .addSeparator(0.125)
	        ;
	      tab = sim_front.addTab("Add");
	      tab.getShelf()
	      	.addDrawerButton(show_addpoint, reset_add, 10.25F, 1)
	      	.addSeparator(0.125)
	      	.addDrawerButton(add_solid_run, 10.25F, 1)
	      	.addSeparator(0.125)
	      	.addDrawerButton(clear_solid_run, add_ball_run, 10.25F, 1)
	      	.addSeparator(0.125)
	        .addDrawerFltSlide(add_posx)
	        .addSeparator(0.125)
	        .addDrawerFltSlide(add_posy)
	        .addSeparator(0.125)
	        .addDrawerFltSlide(add_dir)
	        .addSeparator(0.125)
	        .addDrawerFactValue(add_mov, 2.0F, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(add_mov, 1.18F, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(add_rad, 2.0F, 10, 1)
	        .addSeparator(0.125)
	        ;
	      tab = sim_front.addTab("Pilote");
	      tab.getShelf()
	        .addDrawerFactValue(friction_fact, 2, 10, 1)
	        .addSeparator(0.125)
	        .addDrawerFactValue(collide_fact, 2, 10, 1)
	        .addSeparator(0.125)
	      	.addDrawerButton(use_grav, 10.25F, 1)
	      	.addSeparator(0.125)
	        .addDrawerFactValue(gravity_const, 2, 10, 1)
	        .addSeparator(0.125)
	      	.addDrawerButton(use_ball, ball_to_center, protect_ball, 10.25F, 1)
	      	.addSeparator(0.125)
	        .addDrawerFactValue(ball_rad, 2, 10, 1)
	        .addSeparator(0.125)
	      	.addDrawerButton(use_wall, show_wall, 10.25F, 1)
	      	.addSeparator(0.125)
	        .addDrawerFactValue(wall_width, 2, 10, 1)
	        .addSeparator(0.125)
	      	;
	    	  sim_front.toLayerTop();
	  }
		
		  ArrayList<ESolid> solid_list = new ArrayList<ESolid>(); //contien les objet
		  ESolid ball_obj;
		  
		  sInt strtbox_width;
		  sFlt strtbox_space, solid_rad, wall_width;
		  sBoo reset_build, build_as_box, build_rng, //build_as_hexa, build_spiral, 
		  	use_wall, show_wall, 
		  	use_ball, ball_to_center, protect_ball, use_grav;//, use_merge;
		  sRun build_as, del_all;

		  sFlt friction_fact, collide_fact, gravity_const, ball_rad;
		  
		  sBoo show_fill, show_stroke, fracture_halo;
		  sFlt stroke_width, halo_size, halo_dens, close_dist_factor, full_pack_nb;
		  sCol fill_col1, fill_col2, stroke_col, val_col_halo1, val_col_halo2;

//		  nRunnable ball_change;
		  
//		  float sq_speed_min = 0, sq_speed_max = 0; //squared values for efficiencies
		  sFlt val_sqspeed_min, val_sqspeed_max;
		  
		  sRun add_solid_run, clear_solid_run, add_ball_run;
		  sBoo show_addpoint, reset_add;
		  sFlt add_posx, add_posy, add_dir, add_mov, add_rad;
		  
		MSolidGroup(Macro_Sheet _sheet, sValueBloc _bloc) { 
			super(_sheet, "solids", _bloc); 
		}

		  public void init() {
			  super.init();
			  init_access();
			  
			  if (!loading_from_bloc) adding_entity_nb.set(0);
			  
			strtbox_width = newInt(12, "strtbox_width");
		    strtbox_space = newFlt(120, "strtbox_space");
		    solid_rad = newFlt(20, "solid_rad");

		    wall_width = newFlt(1000, "wall_width");
		    use_wall = newBoo(true, "use_wall");
		    show_wall = newBoo(true, "show_wall");
		    
		    reset_build = newBoo(true, "reset_build");
		    build_as_box = newBoo(true, "build_as_box");
//		    build_as_hexa = newBoo(false, "build_as_hexa");
		    build_rng = newBoo(false, "build_rng");
//		    build_spiral = newBoo(false, "build_spiral");
		    
		    friction_fact = newFlt(0.0125F, "friction_fact");
		    collide_fact = newFlt(0.1F, "collide_fact");
		    gravity_const = newFlt(0.5F, "gravity_const");
		    use_grav = newBoo(true, "use_grav");
//		    use_merge = newBoo(false, "use_merge");

		    use_ball = newBoo(true, "use_ball");
		    protect_ball = newBoo(true, "protect_ball");
		    ball_to_center = newBoo(true, "ball_to_center");
		    ball_rad = newFlt(80F, "ball_rad");

		    show_fill = newBoo(true, "show_fill");
		    show_stroke = newBoo(false, "show_stroke");
		    fracture_halo = newBoo(false, "fracture_halo");
		    close_dist_factor = menuFltFact(4.0F, 2.0F, "close_dist_factor");
		    full_pack_nb = menuFltFact(30.0F, 2.0F, "full_pack_nb");
		    fill_col1 = menuColor(app.color(0, 100, 255), "fill_col1", "fill solo");
		    fill_col2 = menuColor(app.color(0, 0, 170), "fill_col2", "fill packed");
		    stroke_col = menuColor(app.color(255), "stroke_col");
		    stroke_width = menuFltFact(2.0F, 2.0F, "stroke_width");
		    halo_size = menuFltFact(10, 2.0F, "halo_size");
		    halo_dens = menuFltFact(1.0F, 2.0F, "halo_dens");
		    val_col_halo1 = menuColor(app.color(255, 0, 0), "val_col_halo1", "halo quick");
		    val_col_halo2 = menuColor(app.color(0, 0, 255), "val_col_halo2", "halo slow");
		    val_sqspeed_min = menuFltFact(10.0F, 2.0F, "val_sqspeed_min");
		    val_sqspeed_max = menuFltFact(100.0F, 2.0F, "val_sqspeed_max");

		    show_addpoint = newBoo(false, "show_addpoint");
		    reset_add = newBoo(false, "reset_add");
		    add_posx = newFlt(0.0F, "add_posx");
			if (!loading_from_bloc) add_posx.set_limit(-1.0F, 1.0F);
		    add_posy = newFlt(0.0F, "add_posy");
			if (!loading_from_bloc) add_posy.set_limit(-1.0F, 1.0F);
		    add_dir = newFlt(0.0F, "add_dir");
			if (!loading_from_bloc) add_dir.set_limit(0.0F, 6.28F);
		    add_mov = newFlt(20.0F, "add_mov");
		    add_rad = newFlt(20, "add_rad");
		    nRunnable adds_run = new nRunnable() { public void run() {
		    		ESolid s = newEntity();
		    		if (s != null) {
				    	PVector p = new PVector(val_pos.get().x + 
		    					wall_width.get() * add_posx.get() / 2.0F, 
		    				val_pos.get().y - 
		    					wall_width.get() * add_posy.get() / 2.0F);
	        			s.pos.set(p.x, p.y);
	        			s.mov.set(add_mov.get(), 0);
	        			s.mov.rotate(add_dir.get());
	        			s.radius = add_rad.get();
	        		}
		    }};
		    add_solid_run = newRun("add_solid_run", adds_run);
		    nRunnable clears_run = new nRunnable() { public void run() {
		    		for (Entity e : entity_list) if (e.active) e.destroy(); 
		  		for (ESolid e : solid_list) if (e.active) e.destroy(); 
		  		if (ball_obj != null) ball_obj.destroy(); 
		  		ball_obj = null; 
		    }};
		    clear_solid_run = newRun("clear_solid_run", clears_run);
		    nRunnable addb_run = new nRunnable() { public void run() {
		    		if (ball_obj != null) ball_obj.destroy(); 
		  		ball_obj = null; 
				if (use_ball.get()) { ball_obj = newEntity(); }
		    		if (ball_obj != null) {
		    			ball_obj.pos.set(val_pos.get());
		    			ball_obj.radius = ball_rad.get(); }
		    }};
		    add_ball_run = newRun("add_ball_run", addb_run);
		    
		    build_as = newRun("build_as", "build_as", new nRunnable() { public void run() { 

	    	  		for (Entity e : entity_list) if (e.active) e.destroy(); 
	    	  		for (ESolid e : solid_list) if (e.active) e.destroy(); 
	    	  		
	    	  		if (ball_obj != null) ball_obj.destroy(); 
	    	  		ball_obj = null; 
	    			if (use_ball.get()) {
	    				ball_obj = newEntity();
	    			}
	    	    		if (ball_obj != null) {
	    	    			ball_obj.pos.set(val_pos.get());
	    	    			ball_obj.radius = ball_rad.get();
	    	    		}
		    		
		    		if (build_as_box.get()) {
			    	  	float side_length = (strtbox_width.get() - 1) * strtbox_space.get();
			    	  	for (int i = 0; i < strtbox_width.get() ; i++)
			        	for (int j = 0; j < strtbox_width.get() ; j++) {
			        		ESolid s = newEntity();
			        		if (s != null) {
			        			s.pos.set(val_pos.get().x - side_length / 2 + i * strtbox_space.get(), 
			        					 val_pos.get().y - side_length / 2 + j * strtbox_space.get());
			        			s.radius = solid_rad.get();
			        			if (build_rng.get()) {
			        				s.pos.add(app.random(- strtbox_space.get(), strtbox_space.get()),
			        						  app.random(- strtbox_space.get(), strtbox_space.get()));
			        				s.radius += app.random(- solid_rad.get() / 2, solid_rad.get() /2);
			        				s.mov.set(app.random(val_sqspeed_min.get(), val_sqspeed_max.get()), 0);
			        				s.mov.rotate(app.random(-3.14F, 3.14F));
			        			}
			        		}
			        	}
		    		}
//		    		if (build_as_hexa.get()) {
//			    	  	for (Entity e : entity_list) if (e.active) e.destroy(); 
////			    	  	ball_obj = null; 
//			    	  	float side_length = strtbox_width.get() * strtbox_space.get();
//			    	  	int flip = 0;
//			    	  	for (int i = 0; i < strtbox_width.get() ; i++) {
//			    	  		if (flip == 0) flip = 1; else flip = 0;
//				        	for (int j = 0; j < strtbox_width.get() ; j++) {
//				        		ESolid s = newEntity();
//				        		if (s != null) {
//				        			s.pos.set(val_pos.get().x
//				        					  - side_length / 2 + 
//				        					  i * (strtbox_space.get() - (strtbox_space.get() *0.1F)), 
//				        					  val_pos.get().y 
//				        					  - side_length / 2 + 
//				        					  j * strtbox_space.get() +
//				        					  (flip * strtbox_space.get() / 2));
//				        			s.radius = solid_rad.get();
//				        			if (build_rng.get()) {
//				        				s.pos.add(app.random(- strtbox_space.get() / 2, strtbox_space.get() /2),
//				        						  app.random(- strtbox_space.get() / 2, strtbox_space.get() /2));
//				        				s.radius += app.random(- solid_rad.get() / 2, solid_rad.get() / 1);
//				        				s.mov.set(300 * collide_fact.get() * solid_rad.get(), 0);
//				        				s.mov.rotate(app.random(-3.14F, 3.14F));
//				        			}
//				        		}
//				        	}
//			    	  	}
//		    		}

		    		if (protect_ball.get() && use_ball.get() && ball_obj != null) {
		    			for (Entity s : entity_list) 
		    				if (s.active && ((ESolid)s) != ball_obj &&
		    						((ESolid)s).collision(ball_obj)) s.destroy();
		    		}
		    		
		    	} } );
			
		    del_all = newRun("del_all", "del_all", new nRunnable() { 
		      public void run() { 
		    	  	for (Entity e : entity_list) if (e.active) e.destroy(); } } );
		  }
		  public void init_end() {
			  super.init_end();
			  is_init = true;
			  reset();
		  }
		  boolean is_init = false;

		  void custom_reset() {
			  if (is_init && reset_build.get()) {
				build_as.run();
			  }
			  if (is_init && reset_add.get()) add_solid_run.run();
		  }
		  void custom_pre_tick() {
			  for (ESolid s1 : solid_list) s1.close_cnt = 0;
			  
			  for (ESolid s1 : solid_list)
				for (ESolid s2 : solid_list) if (s1 != s2) {
				  float d = PApplet.dist(s1.pos.x, s1.pos.y, s2.pos.x, s2.pos.y);
				  if (d < s1.radius/2.0F + s2.radius/2.0F) {
					  PVector push = new PVector(s1.pos.x - s2.pos.x, s1.pos.y - s2.pos.y);
					  push.mult(collide_fact.get() * s2.radius / (s1.radius + s2.radius));
					  s1.accel.add(push);
					  push.mult(-(s1.radius / (s1.radius + s2.radius)) / (s2.radius / (s1.radius + s2.radius)));
					  s2.accel.add(push);
				  } else if (use_grav.get()) {
					  PVector push = new PVector(s1.pos.x - s2.pos.x, s1.pos.y - s2.pos.y);
					  push.setMag(s1.radius * gravity_const.get() / (d*d));
					  s2.accel.add(push);
					  push.mult(-s2.radius/s1.radius);
					  s1.accel.add(push);
				  }
				  if (d < (s1.radius/2.0F + s2.radius/2.0F) 
						  * close_dist_factor.get()) { s1.close_cnt++; }
			  }
			  if (use_wall.get()) {
				  float xmin = val_pos.get().x - wall_width.get() / 2.0F;
				  float xmax = val_pos.get().x + wall_width.get() / 2.0F;
				  float ymin = val_pos.get().y - wall_width.get() / 2.0F;
				  float ymax = val_pos.get().y + wall_width.get() / 2.0F;
				  for (ESolid s : solid_list) {
					  if (s.pos.x < xmin + s.radius/2) { //s.accel.add(-s.mov.x * 2, 0); 
					  	s.mov.add((xmin + s.radius/2) - s.pos.x, 0);
					  }
					  if (s.pos.y < ymin + s.radius/2) { //s.accel.add(0, -s.mov.y * 2);
					  	s.mov.add(0, (ymin + s.radius/2) - s.pos.y);
					  }
					  if (s.pos.x > xmax - s.radius/2) { //s.accel.add(-s.mov.x * 2, 0);
					  	s.mov.add((xmax - s.radius/2) - s.pos.x, 0);
					  }
					  if (s.pos.y > ymax - s.radius/2) { //s.accel.add(0, -s.mov.y * 2);
					  	s.mov.add(0, (ymax - s.radius/2) - s.pos.y);
					  }
				  }
			  }
//			  sq_speed_min = 0; sq_speed_max = 0;
		  }
		  void custom_post_tick() {
			  if (ball_to_center.get() && ball_obj != null) 
				  ball_obj.pos.set(val_pos.get());
		  }
		  void custom_frame() {
		  }
		  void custom_cam_draw_post_entity() {
			  if (show_wall.get()) {

			    	gui.app.stroke(180);
			    	gui.app.strokeWeight(ref_size / (10 * mmain().gui.scale) );
			    	gui.app.noFill();
			    	gui.app.rect(val_pos.get().x - wall_width.get() / 2.0F, 
			    				val_pos.get().y - wall_width.get() / 2.0F,
			    				wall_width.get(),
			    				wall_width.get());
			  }
			  if (show_addpoint.get()) {
				  gui.app.stroke(255);
			    	gui.app.strokeWeight(3);
			    	gui.app.noFill();
			    	PVector p = new PVector(
			    		val_pos.get().x + wall_width.get() * add_posx.get() / 2.0F, 
	    				val_pos.get().y - wall_width.get() * add_posy.get() / 2.0F);
			    	gui.app.pushMatrix();
			    	gui.app.translate(p.x, p.y);
			    	gui.app.scale(10 * mmain().gui.scale);
			    	gui.app.rotate(add_dir.get());
			    	gui.app.triangle(4, 0, - 2, + 2, - 2, - 2);
			    	gui.app.popMatrix();
			  }
		  }
		  void custom_cam_draw_pre_entity() {
		  }
		  
		  ESolid build() { return new ESolid(this); }
		  ESolid addEntity() { return newEntity(); }
		  ESolid newEntity() {
		    for (Entity e : entity_list) if (!e.active) { e.activate(); return (ESolid)e; } return null; }
	}

	
	
	
	class EFloc extends Entity {
		PVector pos = new PVector(0, 0);
		  PVector mov = new PVector(0, 0);
		  float speed = 0;
		  
		  float halo_size = 0;
		  float halo_density = 0;
		  
		  int age = 0;
		  int max_age = 2000;

		  int col = 0;
		  int halo_col = 0;
		  
		  ArrayList<PVector> tail_list;
		  int tail_space = 0;
		  int osc_cnt = 0;
		  
		  ArrayList<EFloc> neight_list;
		  ArrayList<EFloc> blob_list;
		  boolean in_blob = false;
		  
		  int crazy_cnt = 0;
		  boolean crazy_state = false;
		  
		EFloc(MFlocGroup c) { 
			super(c);

		  	tail_list = new ArrayList<PVector>();
		  	neight_list = new ArrayList<EFloc>();
		  	blob_list = new ArrayList<EFloc>();
		  }
		EFloc init() {
		    age = 0;
		    max_age = (int)(com().app.random(0.3F, 1.7F) * com().AGE.get());
		    halo_size = com().HALO_SIZE.get();
		    halo_density = com().HALO_DENS.get();
		    halo_size += com().app.random(com().HALO_SIZE.get());
		    halo_density += com().app.random(com().HALO_DENS.get());
		    pos = com().ref_cursor.pos();
		    pos.x = pos.x + com().app.random(2 * com().startzone.get()) - com().startzone.get();
		    pos.y = pos.y + com().app.random(2 * com().startzone.get()) - com().startzone.get();
		    
		    speed = com().app.random(1 - com().speed_rng.get(), 1 + com().speed_rng.get())
		    			* com().SPEED.get();
		    mov.x = speed; mov.y = 0;
		    if (com().ref_cursor.dir_set()) {
		    		mov.rotate(com().ref_cursor.dir().heading());
		    		mov.rotate(com().app.random(-PConstants.PI / com().strt_dev.get(), 
		    				PConstants.PI / com().strt_dev.get()));
		    } else mov.rotate(com().app.random(PConstants.PI * 2.0F));
		    
		    tail_list.clear();
		    tail_space = 0;
		    osc_cnt = 0;
		    
		    col = rngCol(com().val_col_def1.get(), com().val_col_def2.get());
		    halo_col = rngCol(com().val_col_halo1.get(), com().val_col_halo2.get());
		    dev_cnt = 0;
		    
		    return this;
		  }

		  void headTo(PVector c, float s) {
		    PVector l = new PVector(c.x, c.y);
		    l.add(-pos.x, -pos.y);
		    float r1 = RConst.mapToCircularValues(mov.heading(), l.heading(), s, -RConst.PI, RConst.PI);
		    mov.x = speed; mov.y = 0;
		    mov.rotate(r1);
		  }
		  void headAway(PVector c, float s) {
		    PVector l = new PVector(c.x, c.y);
		    l.add(-pos.x, -pos.y);
		    l.mult(-1);
		    float r1 = RConst.mapToCircularValues(mov.heading(), l.heading(), s, -RConst.PI, RConst.PI);
		    mov.x = speed; mov.y = 0;
		    mov.rotate(r1);
		  }
		  void headTo(float l, float s) {
		    float r1 = RConst.mapToCircularValues(mov.heading(), l, s, -RConst.PI, RConst.PI);
		    mov.x = speed; mov.y = 0;
		    mov.rotate(r1);
		  }
		  void headAway(float l, float s) {
		    l = (l + RConst.PI) % (2 * RConst.PI);
		    float r1 = RConst.mapToCircularValues(mov.heading(), l, s, -RConst.PI, RConst.PI);
		    mov.x = speed; mov.y = 0;
		    mov.rotate(r1);
		  }
		  
		  void follow(EFloc f, float to, float frm, float a) {
			  if (!crazy_state) { headTo(frm, a ); f.headTo(to, a );
			  } else { headAway(frm, a ); f.headAway(to, a ); } }
		  void headTo(EFloc f, float a) {
			  if (!crazy_state) { headTo(f.pos, a ); f.headTo(pos, a );
			  } else { headAway(f.pos, a ); f.headAway(pos, a ); } }
		  void headAway(EFloc f, float a) {
			  if (!crazy_state) { headAway(f.pos, a ); f.headTo(pos, a );
			  } else { headTo(f.pos, a ); f.headAway(pos, a ); } }
		  
		  void pair(EFloc b2) {
		    float d = PApplet.dist(pos.x, pos.y, b2.pos.x, b2.pos.y);
			if (d < com().VIEWING_DIST.get()) {
			  float purss_eff = com().POURSUITE.get() * (d/com().VIEWING_DIST.get());
			  if (d > com().SPACING_MIN.get() && d < com().SPACING_MAX.get()) {
				float dist_fact = (d - com().SPACING_MIN.get()) / 
						(com().SPACING_MAX.get() - com().SPACING_MIN.get());
//						((com().SPACING_MAX.get() - d) / com().SPACING_MAX.get());
				
			    //follow as much as close
			    	follow(b2, mov.heading(), b2.mov.heading(), 
			    			com().FOLLOW.get() * (1.0F - dist_fact) );
			    neight_list.add(b2);
			    b2.neight_list.add(this);
			  } else if (d >= com().SPACING_MAX.get()) {
			    	headTo(b2, purss_eff);
			  } else if (d <= com().SPACING_MIN.get()) {
			    headAway(b2, purss_eff);
			    neight_list.add(b2);
			    b2.neight_list.add(this);
			  }
		    }
		  }

		  int dev_cnt = 0;
		EFloc frame() { return this; }
		  
		EFloc tick() {

			  dev_cnt++;
			  if (dev_cnt > com().deriv_speed.get()) {
				  dev_cnt = 0;
				  halo_col = derivCol(halo_col, (int)com().halo_dev.get());
			  }
			  
			if (com().crazy_run.get()) {
				crazy_cnt++;
				if (!crazy_state && crazy_cnt >= com().crazy_tryrate.get()) {
					crazy_cnt = 0;
					if (com().app.random(1.0F) < com().crazy_prob.get()) crazy_state = true;
				} else if (crazy_state && crazy_cnt >= com().crazy_tryrate.get()) {
					crazy_cnt = 0;
					if (com().app.random(1.0F) > (1.0F - com().crazy_prob.get()) / 2.0F) crazy_state = false;
				}
			}
			  
		    age++;
		    if (age > max_age && com().aging.get()) {
		    	  for (Macro_Connexion c : com().link_out.connected_inputs) {
	        	  	MGrowerGroup gcom = null;
	    			if (c.elem.bloc.val_type.get().equals("grows")) {
	    				gcom = (MGrowerGroup)c.elem.bloc;
	    			} 
		        if (com().create_grower.get() && gcom != null && 
		    		  com().app.crandom(com().grow_prob.get()) > 0.5) {
		          EGrower ng = gcom.newEntity();
		          if (ng != null) ng.define(new PVector(pos.x, pos.y), new PVector(1, 0).rotate(mov.heading()), 0);
		        }
		      }
		      destroy();
		    }
		    int cible_cnt = 0;
		    if (com().point_to_mouse.get()) cible_cnt++;
		    if (com().point_to_center.get()) cible_cnt++;
		    if (com().point_to_cursor.get()) cible_cnt++;
		    //point toward mouse
		    if (com().point_to_mouse.get()) headTo(com().inter.cam.screen_to_cam(
		    		new PVector(com().app.mouseX, com().app.mouseY)), 
		                                           com().POINT_FORCE.get() / cible_cnt);
		    //point toward center
		    if (com().point_to_center.get()) headTo(new PVector(0, 0), com().POINT_FORCE.get() / cible_cnt);
		    //point toward cursor
		    if (com().point_to_cursor.get()) headTo(com().ref_cursor.pos(), com().POINT_FORCE.get() / cible_cnt);
		    
		    if (com().oscillant.get()) {
		    		osc_cnt+=1;
		    		if (osc_cnt >= com().oscl_length.get()) osc_cnt = 0;
		    		headTo(mov.heading() + PConstants.PI * 1.0F * (osc_cnt - com().oscl_length.get() / 2)
		    				/ com().oscl_length.get(), com().oscl_force.get());
		    }
		    
		    if (com().point_to_side.get()) {
		    		headTo(com().side_dir.get(), com().side_force.get());
		    }
		    
		    pos.add(mov);
		    
		    tail_space++;
		    if (tail_space > 10) {
		    		tail_space = 0;
		    		PVector t = new PVector(pos.x, pos.y);
		    		if (tail_list.size() < com().tail_long.get()) tail_list.add(t);
		    		else {
		    			tail_list.remove(0);
		    			tail_list.add(t);
		    		}
		    }
		    
		    return this;
		  }

		  public void draw_halo(MCanvas canvas) {
//		    canvas.draw_halo(pos, halo_size, halo_density, halo_col);
		  }
		  
		  int rngCol(int c1, int c2) {
			int r = (int)com().app.random(com().app.red(c1) -
		    		com().app.red(c2));
		    int g = (int)com().app.random(com().app.green(c1) -
		    		com().app.green(c2));
		    int b = (int)com().app.random(com().app.blue(c1) -
		    		com().app.blue(c2));
		    r += com().app.red(c2);
		    g += com().app.green(c2);
		    b += com().app.blue(c2);
		    return com().app.color(r, g, b);
		  }

		  int derivCol(int c, float dev) {
			float r = com().app.red(c);
			float g = com().app.green(c);
			float b = com().app.blue(c);
		    
			r = r * com().app.random(1 - dev, 1 + dev);
		    g = g * com().app.random(1 - dev, 1 + dev);
		    b = b * com().app.random(1 - dev, 1 + dev);
		    return com().app.color(r, g, b);
		  }
		  
		EFloc draw() {

//		      com().app.fill(com().val_col_deb.get());
//		      com().app.noStroke();
//			  com().app.stroke(255);
//			  com().app.strokeWeight(com().scale.get() * com.sim.cam_gui.scale / (2.0F));
//			  for (Floc n : neight_list) {
//				  com().app.line(pos.x, pos.y, n.pos.x, n.pos.y);
//			  }
			  com().app.stroke(col);
			  com().app.strokeWeight(com().scale.get() / (1.0F));// * com.sim.cam_gui.scale
		      PVector p0 = new PVector();
			  if (com().draw_tail.get() && tail_list.size() > 1) 
				for (int i = 0 ; i < tail_list.size() - 1 ; i++) {
				  PVector p1 = new PVector(tail_list.get(i).x, tail_list.get(i).y);
				  PVector p2 = new PVector(tail_list.get(i+1).x, tail_list.get(i+1).y);
				  PVector l = new PVector(p2.x - p1.x, p2.y - p1.y);
				  l.mult(tail_space / 20.0F);
//				  com().app.pushMatrix();
//				  com().app.translate(p1.x + l.x, p1.y + l.y);
//			      float t = (com().scale.get() * (com().ref_size / 10)) / 5.0F;
				  if (i == tail_list.size()-2) com().app.line(pos.x, pos.y, p1.x + l.x, p1.y + l.y);
				  if (i > 0 && i < tail_list.size()-1) com().app.line(p0.x, p0.y, p1.x + l.x, p1.y + l.y);
//			      com().app.ellipse(0, 0, t, t);
//			      com().app.popMatrix();
				  p0.set(p1.x + l.x, p1.y + l.y);
			  }
			  
			  com().app.fill(col);
			  com().app.stroke(col);
//			  com().app.strokeWeight(4/com.sim.cam_gui.scale);
			  com().app.pushMatrix();
			  com().app.translate(pos.x, pos.y);
			  com().app.rotate(mov.heading());
		    if (com().DRAWMODE_DEF.get()) {
		    		com().app.line(0, 0, -com().scale.get(), -com().scale.get());
		    		com().app.line(2, 0, -com().scale.get(), 0);
		    		com().app.line(0, 0, -com().scale.get(), com().scale.get());
		    }
		    com().app.popMatrix();
		    if (com().DRAWMODE_DEBUG.get()) {
			    float t = (com().scale.get() * (com().ref_size / 10)) / (com.cam_gui.scale * 6);
				com().app.pushMatrix();
				com().app.translate(pos.x, pos.y);
				if (!com().show_blob.get()) {
				    com().app.fill(com().val_col_deb.get());
				    com().app.noStroke();
				} else {
					if (in_blob) {
						t /= 3.0F;
					    com().app.fill(com().val_col_deb.get());
					    com().app.noStroke();
					} else {
					    com().app.fill(com().val_col_deb.get());
					    com().app.noStroke();
					}
				}
			    com().app.ellipse(-t/2, -t/2, t, t);
			    com().app.popMatrix();
		    }
			  return this;
		  }

		  void build_blob() {
			  blob_list.clear();
			  blob_list.add(this);
			  for (EFloc f : neight_list) add_to_blob(this, f);
		  }
		  void add_to_blob(EFloc cible, EFloc test) {
			  if (!cible.blob_list.contains(test)) {
				  cible.blob_list.add(test);
				  for (EFloc n : test.neight_list) add_to_blob(this, n);
			  }
		  }
		  
		EFloc clear() { return this; }
		  MFlocGroup com() { return ((MFlocGroup)com); }
		  
	}







	class MFlocGroup extends MGroup { 
		static class Builder extends MAbstract_Builder {
			Builder(Macro_Main m) { super("flocs", "FlocGroup", "", "Work"); 
			first_start_show(m); }
			MFlocGroup build(Macro_Sheet s, sValueBloc b) { 
				MFlocGroup m = new MFlocGroup(s, b); return m; }
		}
		
		public void comPanelBuild(nFrontPanel sim_front) {
		sim_front.getTab(2).getShelf()
		    .addDrawerButton(DRAWMODE_DEF, DRAWMODE_DEBUG, draw_tail, 10.25F, 1)
		    .addSeparator(0.125)
	    ;

		sim_front.getTab(2).getShelf()
	    	    .addDrawerButton(kill_all, 10, 1)
	    	    .addSeparator(0.125)
	    		;
		
	    nFrontTab tab = sim_front.addTab("Floc");
	    tab.getShelf()
	      .addDrawerButton(do_flocking, 10.25F, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(POURSUITE, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(FOLLOW, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(SPACING_MIN, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(SPACING_MAX, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(SPEED, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(speed_rng, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(show_web, show_blob, 10.25F, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(web_weight, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(web_alpha_min, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(web_alpha_max, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerColor(val_col_web, 10.25F, 1, mmain().inter.taskpanel)
	      .addSeparator(0.125)
	      ;
	    
	    tab = sim_front.addTab("Comport");
	    tab.getShelf()
	    .addDrawerFactValue(startzone, 2, 10, 1)
	    .addSeparator(0.125)
	    .addDrawerFactValue(strt_dev, 2, 10, 1)
	    .addSeparator(0.125)
	    .addDrawerActFactValue("grower", create_grower, grow_prob, 2, 10, 1, false)
	    .addSeparator(0.125)
	    .addDrawerActFactValue("aging", aging, AGE, 2, 10, 1, false)
	    .addSeparator(0.125)
	    .addDrawerButton(point_to_mouse, point_to_center, point_to_cursor, 10, 1)
	    .addSeparator(0.125)
	    .addDrawerFactValue(POINT_FORCE, 2, 10, 1)
	    .addSeparator(0.125)
	    .addDrawerButton(oscillant, point_to_side, 10.25F, 1)
	    .addSeparator(0.125)
	    .addDrawerFltSlide(side_dir)
	    .addSeparator(0.125)
	    .addDrawerFactValue(side_force, 2, 10, 1)
	    .addSeparator(0.125)
	    .addDrawerFactValue(oscl_length, 2, 10, 1)
	    .addSeparator(0.125)
	    .addDrawerFactValue(oscl_force, 2, 10, 1)
	    .addSeparator(0.125)
	    ;
	    	  sim_front.toLayerTop();
	    }
		

		  sFlt POURSUITE, FOLLOW, SPACING_MAX, SPACING_MIN, VIEWING_DIST, SPEED, 
		  	HALO_SIZE, HALO_DENS, POINT_FORCE, grow_prob,
		  	speed_rng;
		  sInt AGE, deriv_speed;
		  sBoo create_grower, aging;
		  sBoo DRAWMODE_DEF, DRAWMODE_DEBUG, point_to_mouse, point_to_center, point_to_cursor;
		  
		  sCol val_col_def1, val_col_def2, val_col_deb, val_col_halo1, val_col_halo2, val_col_web;
		  sFlt scale, halo_dev, strt_dev;
		  sFlt startzone;
		  
		  sBoo draw_tail, oscillant, point_to_side, do_flocking, show_web, show_blob;
		  sFlt side_dir, oscl_force, oscl_length, side_force, web_weight, web_alpha_min, web_alpha_max;
		  sInt tail_long;
		  
		  sRun kill_all;
		  
		  sBoo crazy_run;//, crazy_invert, crazy_random;
		  sInt crazy_tryrate;
		  sFlt crazy_prob, crazy_medlength;//, crazy_lenrngfact;

		MFlocGroup(Macro_Sheet _sheet, sValueBloc _bloc) { 
			super(_sheet, "flocs", _bloc); 

			if (!loading_from_bloc) {
				max_entity.set(50);
				adding_entity_nb.set(50);
				adding_step.set(20);
				pulse_add_delay.set(1000);
				pulse_add.set(true);
				auto_add.set(true);
			}
		}

		  public void init() {
			  super.init();
			  init_access();

			  crazy_run = newBoo(false, "crazy_run");
			  crazy_tryrate = newInt(200, "crazy_tryrate");
			  crazy_prob = newFlt(0.01F, "crazy_prob");
			  crazy_medlength = newFlt(200, "crazy_medlength");
			    
		    do_flocking = newBoo(true, "do_flocking", "do_flocking");
		    POURSUITE = newFlt(0.01F, "POURSUITE", "poursuite");
		    FOLLOW = newFlt(0.005F, "FOLLOW", "follow");
		    VIEWING_DIST = newFlt(10000, "VIEWING_DIST", "viewing distence");
		    SPACING_MIN = newFlt(15, "SPACING_MIN", "space min");
		    SPACING_MAX = newFlt(120, "SPACING_MAX", "space max");
		    SPEED = newFlt(2, "SPEED", "speed");
		    speed_rng = newFlt(0.5F, "speed_rng", "speed_rng");
		    
		    startzone = newFlt(500F, "startzone", "startzone");
		    strt_dev = newFlt(4.0F, "strt_dev");
		    AGE = newInt(2000, "age", "age");
		    aging = newBoo(true, "aging", "aging");

		    point_to_mouse = newBoo(false, "point_to_mouse", "to mouse");
		    point_to_center = newBoo(false, "point_to_center", "to center");
		    point_to_cursor = newBoo(false, "point_to_cursor", "to cursor");
		    POINT_FORCE = newFlt(0.002F, "POINT_FORCE");
		    oscillant = newBoo(false, "oscillant", "oscillant");
		    oscl_force = newFlt(0.01F, "oscl_force");
		    oscl_length = newFlt(30F, "oscl_length");
		    point_to_side = newBoo(false, "point_to_side", "point_to_side");
		    side_dir = newFlt(0, "side_dir")
		    		.set_limit(-PConstants.PI, PConstants.PI);
		    side_force = newFlt(0.01F, "side_force");

		    web_weight = newFlt(10F, "web_weight");
		    web_alpha_min = newFlt(150F, "web_alpha_min")
		    		.set_limit(0, 255);
		    web_alpha_max = newFlt(150F, "web_alpha_max")
		    		.set_limit(0, 255);
		    show_web = newBoo(false, "show_web");
		    show_blob = newBoo(false, "show_blob");
		    val_col_web = newCol(app.color(255), "val_col_web");
		    
		    create_grower = newBoo(true, "create_grower", "create grow");
		    grow_prob = newFlt(1, "grow_prob", "grow_prob");

		    DRAWMODE_DEF = newBoo(true, "DRAWMODE_DEF", "draw1");
		    DRAWMODE_DEBUG = newBoo(false, "DRAWMODE_DEBUG", "draw2");
		    draw_tail = newBoo(false, "draw_tail", "tail");
		    tail_long = menuIntIncr(20, 10, "tail_long");
		    HALO_SIZE = menuFltFact(7, 2.0F, "HALO_SIZE");
		    HALO_DENS = menuFltFact(1.2F, 2.0F, "HALO_DENS");
		    val_col_def1 = menuColor(app.color(220), "val_col_def1");
		    val_col_def2 = menuColor(app.color(220), "val_col_def2");
		    val_col_deb = menuColor(app.color(255, 0, 0), "val_col_deb");
		    val_col_halo1 = menuColor(app.color(255, 120, 0), "val_col_halo1");
		    val_col_halo2 = menuColor(app.color(255, 0, 0), "val_col_halo2");
		    halo_dev = menuFltFact(0.1F, 2.0F, "halo_dev");
		    deriv_speed = menuIntFact(5, 2.0F, "deriv_speed");
		    scale = menuFltSlide(5, 5, 100, "length");
		    
		    kill_all = newRun("kill_all", "kill_all", new nRunnable() { 
		      public void run() { 
		    	  	for (Entity e : entity_list) if (e.active) e.destroy(); } } );
		  }
		  public void init_end() {
			  super.init_end();
			  is_init = true;
			  reset();
		  }
		  boolean is_init = false;

		  void custom_reset() {
		  }
		  void custom_pre_tick() {

			  for (Entity e : entity_list) {
		  	    ((EFloc)e).neight_list.clear();
			  }
			  if (do_flocking.get())
			    for (Entity e1 : entity_list) for (Entity e2 : entity_list)
			        if (e1.id < e2.id && e1 != e2 && e1.active && e2.active)
			            ((EFloc)e1).pair(((EFloc)e2));
			  if (show_blob.get()) {
				  for (Entity e : entity_list) {
				    ((EFloc)e).blob_list.clear();
			    	    ((EFloc)e).in_blob = false;
				  }
			      for (Entity e : entity_list) ((EFloc)e).build_blob();
			      
			      for (int i_test = 0 ; i_test < entity_list.size() ; i_test++) {
			    	    EFloc f = ((EFloc)entity_list.get(i_test));
			  	    int lsize = f.blob_list.size();
				  	if (f.active && lsize > 3 && !f.in_blob) {
				  		for (int i_p1 = 1 ; i_p1 < lsize ; i_p1++)
				  		for (int i_p2 = 1 ; i_p2 < lsize ; i_p2++)
				  		for (int i_p3 = 1 ; i_p3 < lsize ; i_p3++) {
				  			if (i_p1 != i_p2 && i_p2 != i_p3 && i_p3 != i_p1) {
				  				EFloc e1 = f.blob_list.get(i_p1);
				  				EFloc e2 = f.blob_list.get(i_p2);
				  				EFloc e3 = f.blob_list.get(i_p3);
				  				if (e1.active && e2.active && e3.active && 
				  					RConst.point_in_trig(e1.pos, e2.pos, e3.pos, f.pos))
				  					f.in_blob = true;
				  			}
				  		}
				  	}
			      }
			  }
		  }
		  void custom_post_tick() {
		  }
		  void custom_frame() {
		  }
		  void custom_cam_draw_post_entity() {
			  if (show_web.get()) {
				  for (int i = 0 ; i < entity_list.size() - 1 ; i++) 
				  for (int j = i+1 ; j < entity_list.size() ; j++) {
					  EFloc f1 = (EFloc)entity_list.get(i);
					  EFloc f2 = (EFloc)entity_list.get(j); 
					  if (f1.active && f2.active) {
					      float d = PApplet.dist(f1.pos.x, f1.pos.y, f2.pos.x, f2.pos.y);
				    	  	  int r = (int)app.red(val_col_web.get());
				    	  	  int g = (int)app.green(val_col_web.get());
				    	  	  int b = (int)app.blue(val_col_web.get());
					      if (d > SPACING_MIN.get() && d < SPACING_MAX.get()) {
					    	  	  float alpha = web_alpha_min.get() + 
					    	  			  		(web_alpha_max.get() - web_alpha_min.get()) * 
					    	  			  		(1.0F - (d - SPACING_MIN.get()) / 
					    	  			  	    (SPACING_MAX.get() - SPACING_MIN.get()));
						    	  app.stroke(r, g, b, alpha);
						    	  app.strokeWeight(web_weight.get());
						    	  app.line(f1.pos.x, f1.pos.y, f2.pos.x, f2.pos.y);
				          }
					      else if (d <= SPACING_MIN.get()) {
						    	  app.stroke(r, g, b, web_alpha_max.get());
						    	  app.strokeWeight(web_weight.get());
						    	  app.line(f1.pos.x, f1.pos.y, f2.pos.x, f2.pos.y);
				          }
					  }
				  }	
			  }
			  
			  if (DRAWMODE_DEBUG.get() && show_blob.get()) {
			  	  float t = (scale.get() * (ref_size / 10.0F)) / (cam_gui.scale * 6.0F);
				  PVector m = new PVector(inter.cam_gui.mouseVector.x, 
						  				 inter.cam_gui.mouseVector.y);
				  app.noStroke();
				  app.fill(120);
			      for (Entity e : entity_list) if (e.active)  {
			    	  	  EFloc f = ((EFloc)e);
			    	  	  float d = PApplet.dist(f.pos.x, f.pos.y, m.x, m.y);
			    	  	  if (d < t/2.0F) {
			  			app.pushMatrix();
			  			app.translate(f.pos.x, f.pos.y);
			  		    app.ellipse(-t/2, -t/2, t, t);
			  		    app.popMatrix();
			    	  		for (EFloc b : f.blob_list) {
			    	  			app.pushMatrix();
			    	  			app.translate(b.pos.x, b.pos.y);
			    	  		    app.ellipse(-t/2, -t/2, t, t);
			    	  		    app.popMatrix();
			    	  		}
			    	  	  }
			    	  	  
			      }
//			      for (Entity e : list) if (e.active) {
//			    	  	  Floc f = ((Floc)e);
//		    	  		  app.strokeWeight(scale.get() * (2.0F));
//		    	  		  app.noFill();
//			    	  	  if (f.in_blob) {
//			    	  		  app.stroke(0, 255, 0);
//			    	  	  } else {
//			    	  		  app.stroke(0, 0, 255);
//			    	  	  }
//			    	  	  app.pushMatrix();
//			    	  	  app.translate(f.pos.x, f.pos.y);
//			    	  	  app.ellipse(-t/2, -t/2, t, t);
//			    	  	  app.popMatrix();
//			      }
			  }
		  }
		  void custom_cam_draw_pre_entity() {
		  }
		  
		  EFloc build() { return new EFloc(this); }
		  EFloc addEntity() { return newEntity(); }
		  EFloc newEntity() {
		    for (Entity e : entity_list) 
		    		if (!e.active) { e.activate(); return (EFloc)e; } return null; }
	}