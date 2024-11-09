package Macro;

import java.util.ArrayList;

import UI.nFrontPanel;
import UI.nFrontTab;
import processing.core.PApplet;
import processing.core.PVector;
import sData.*;
import z_old_specialise.Canvas;
import z_old_specialise.Floc;
import z_old_specialise.FlocComu;

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
      
	  if (canvas != null) 
		  canvas.draw_line_halo(prev_pos, pos, halo_size, halo_density, halo_col, 
				  				com().fracture_halo.get() );
  }
  
  ESolid frame() { return this; }
  
  ESolid tick() {
	mov.add(accel);
	prev_pos.set(pos);
	pos.add(mov);
	
	squared_speed = com().app.squared_mag(mov);
//	if (com().sq_speed_min == 0) com().sq_speed_min = squared_speed;
//	if (com().sq_speed_min > squared_speed) com().sq_speed_min = squared_speed;
//	if (com().sq_speed_max < squared_speed) com().sq_speed_max = squared_speed;
	
	// friction
    mov.mult(1 - com().friction_fact.get());
    
    accel.set(0, 0);
    
//    if (com().use_merge.get()) {
//	    cnt++;
//	    if (cnt > 40 && this != com().ball_obj && radius < com().ball_rad.get() * 0.9F) {
//	    		cnt = 0;
//		    for (Entity e : com().entity_list) if (e.active) {
//		    		ESolid s = (ESolid)e;
//		    		if (s != com().ball_obj && s.radius < com().ball_rad.get() * 0.9F) {
//			    		float d = PApplet.dist(pos.x, pos.y, s.pos.x, s.pos.y);
//			    		if (s.radius < radius && d < radius + s.radius) {
//			    			if (close_list.contains(s)) {
//			    				close_list.remove(s);
//			    				radius += s.radius / 1.0F;
//	//		    				mov.add(s.mov);
//			    				s.destroy();
//			    			} else close_list.add(s);
//			    		} else {
//			    			if (close_list.contains(s)) close_list.remove(s);
//			    		}
//		    		}
//		    } 
//	    }
//    }
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


public class MSolidGroup extends MGroup { 
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

//	  nRunnable ball_change;
	  
//	  float sq_speed_min = 0, sq_speed_max = 0; //squared values for efficiencies
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
//	    build_as_hexa = newBoo(false, "build_as_hexa");
	    build_rng = newBoo(false, "build_rng");
//	    build_spiral = newBoo(false, "build_spiral");
	    
	    friction_fact = newFlt(0.0125F, "friction_fact");
	    collide_fact = newFlt(0.1F, "collide_fact");
	    gravity_const = newFlt(0.5F, "gravity_const");
	    use_grav = newBoo(true, "use_grav");
//	    use_merge = newBoo(false, "use_merge");

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
//	    		if (build_as_hexa.get()) {
//		    	  	for (Entity e : entity_list) if (e.active) e.destroy(); 
////		    	  	ball_obj = null; 
//		    	  	float side_length = strtbox_width.get() * strtbox_space.get();
//		    	  	int flip = 0;
//		    	  	for (int i = 0; i < strtbox_width.get() ; i++) {
//		    	  		if (flip == 0) flip = 1; else flip = 0;
//			        	for (int j = 0; j < strtbox_width.get() ; j++) {
//			        		ESolid s = newEntity();
//			        		if (s != null) {
//			        			s.pos.set(val_pos.get().x
//			        					  - side_length / 2 + 
//			        					  i * (strtbox_space.get() - (strtbox_space.get() *0.1F)), 
//			        					  val_pos.get().y 
//			        					  - side_length / 2 + 
//			        					  j * strtbox_space.get() +
//			        					  (flip * strtbox_space.get() / 2));
//			        			s.radius = solid_rad.get();
//			        			if (build_rng.get()) {
//			        				s.pos.add(app.random(- strtbox_space.get() / 2, strtbox_space.get() /2),
//			        						  app.random(- strtbox_space.get() / 2, strtbox_space.get() /2));
//			        				s.radius += app.random(- solid_rad.get() / 2, solid_rad.get() / 1);
//			        				s.mov.set(300 * collide_fact.get() * solid_rad.get(), 0);
//			        				s.mov.rotate(app.random(-3.14F, 3.14F));
//			        			}
//			        		}
//			        	}
//		    	  	}
//	    		}

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
//		  sq_speed_min = 0; sq_speed_max = 0;
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
