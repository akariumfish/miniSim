package z_old_specialise;

import java.util.ArrayList;

import Macro.*;
import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

class Solid extends Entity {
  PVector pos = new PVector(0, 0);
  PVector prev_pos = new PVector(0, 0);
  PVector mov = new PVector(0, 0);
  PVector accel = new PVector(0, 0);
  float radius;

  float halo_size = 0;
  float halo_density = 0;
  int halo_col = 0;
  
  Solid(SolidComu c) { 
	super(c);
  }
  Solid init() {
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
    return this;
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

  void draw_halo(Canvas canvas) {
    canvas.draw_line_halo(prev_pos, pos, halo_size, halo_density, halo_col, 
    		com().fracture_halo.get());
  }
  
  Solid frame() { return this; }
  
  Solid tick() {
	mov.add(accel);
	prev_pos.set(pos);
	pos.add(mov);
	
	// friction
    mov.mult(1 - com().friction_fact.get());
    
    accel.set(0, 0);
    return this;
  }
  
  Solid draw() {
	  if (com().show_fill.get()) com().app.fill(com().fill_col.get()); 
	  else com().app.noFill();
	  if (com().show_stroke.get()) com().app.stroke(com().stroke_col.get()); 
	  else com().app.noStroke();
	  com().app.strokeWeight(com().stroke_width.get()/com.sim.cam_gui.scale);
	  com().app.pushMatrix();
	  com().app.translate(pos.x, pos.y);
	  com().app.ellipse(-radius/2, -radius/2, radius, radius);
      com().app.popMatrix();
      return this;
  }
  Solid clear() { com().solid_list.remove(this); return this; }
  SolidComu com() { return ((SolidComu)com); }
  
}





public class SolidComu extends Community {
  

public static class SolidPrint extends Sheet_Specialize {
  Simulation sim;
  Canvas can;
  public SolidPrint(Simulation s) { super("Solid"); sim = s; }
  public SolidPrint(Simulation s, Canvas c) { super("Solid"); sim = s; can = c; }
  public void default_build() { }
  public SolidComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new SolidComu(sim, can, n, b); }
}

  void comPanelBuild(nFrontPanel sim_front) {

	  sim_front.getTab(1).getShelf()
	  	.addDrawerTripleButton(show_fill, show_stroke, fracture_halo, 10.25F, 1)
	    .addSeparator(0.125)
	    ;
	
      nFrontTab tab = sim_front.addTab("Solid");
      tab.getShelf()
      	.addDrawerDoubleButton(build_as, del_all, 10.25F, 1)
      	.addSeparator(0.125)
      	.addDrawerButton(reset_build, 10.25F, 1)
      	.addSeparator(0.125)
      	.addDrawerDoubleButton(build_as_box, build_as_hexa, 10.25F, 1)
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
        .addDrawerFactValue(friction_fact, 2, 10, 1)
        .addSeparator(0.125)
        .addDrawerFactValue(collide_fact, 2, 10, 1)
        .addSeparator(0.125)
      	.addDrawerDoubleButton(use_wall, show_wall, 10.25F, 1)
      	.addSeparator(0.125)
        .addDrawerFactValue(wall_width, 2, 10, 1)
        .addSeparator(0.125)
      	.addDrawerButton(use_ball, 10.25F, 1)
      	.addSeparator(0.125)
        .addDrawerFactValue(ball_rad, 2, 10, 1)
        .addSeparator(0.125)
      	;
    	  sim_front.toLayerTop();
  }
  
  Canvas canv;
  
  ArrayList<Solid> solid_list = new ArrayList<Solid>(); //contien les objet
  Solid ball_obj;
  
  sInt strtbox_width;
  sFlt strtbox_space, solid_rad, wall_width;
  sBoo reset_build, build_as_box, build_as_hexa, use_wall, show_wall, use_ball;
  sRun build_as, del_all;

  sFlt friction_fact, collide_fact, ball_rad;
  
  sBoo show_fill, show_stroke, fracture_halo;
  sFlt stroke_width, halo_size, halo_dens;
  sCol fill_col, stroke_col, val_col_halo1, val_col_halo2;

  nRunnable ball_change;
  
  SolidComu(Simulation _c, Canvas c, String n, sValueBloc b) { super(_c, n, "floc", 50, b); 
  	canv = c;
    can_init();
  }
  void can_init() {
	strtbox_width = newInt(20, "strtbox_width");
    strtbox_space = newFlt(80, "strtbox_space");
    solid_rad = newFlt(40, "solid_rad");

    wall_width = newFlt(2000, "wall_width");
    use_wall = newBoo(true, "use_wall");
    show_wall = newBoo(true, "show_wall");
    
    reset_build = newBoo(true, "reset_build");
    build_as_box = newBoo(true, "build_as_box");
    build_as_hexa = newBoo(false, "build_as_hexa");

    friction_fact = newFlt(0.1F, "friction_fact");
    collide_fact = newFlt(0.1F, "collide_fact");
    
    use_ball = newBoo(false, "use_ball");
    ball_rad = newFlt(40F, "ball_rad");

    show_fill = newBoo(true, "show_fill");
    show_stroke = newBoo(true, "show_stroke");
    fracture_halo = newBoo(true, "fracture_halo");
    fill_col = menuColor(app.color(255, 0, 0), "fill_col");
    stroke_col = menuColor(app.color(255), "stroke_col");
    stroke_width = menuFltFact(2.0F, 2.0F, "stroke_width");
    halo_size = menuFltFact(80, 2.0F, "halo_size");
    halo_dens = menuFltFact(0.15F, 2.0F, "halo_dens");
    val_col_halo1 = menuColor(app.color(255, 0, 0), "val_col_halo1");
    val_col_halo2 = menuColor(app.color(255, 0, 0), "val_col_halo2");
    
    ball_change = new nRunnable() { public void run() {
		if (!use_ball.get() && ball_obj != null) { 
			ball_obj.destroy(); 
			ball_obj = null; 
		}
		if (use_ball.get() && (ball_obj == null || !ball_obj.active)) {
			ball_obj = newEntity();
	    		if (ball_obj != null) {
	    			ball_obj.pos.set(adding_cursor.pos().x, adding_cursor.pos().y);
	    			ball_obj.radius = ball_rad.get();
	    		}
		}
    }};
    
    use_ball.addEventChange(ball_change);
    
    build_as = newRun("build_as", "build_as", new nRunnable() { public void run() { 
    		if (build_as_box.get()) {
	    	  	for (Entity e : list) if (e.active) e.destroy(); 
	    	  	ball_obj = null; 
	    	  	float side_length = strtbox_width.get() * strtbox_space.get();
	    	  	for (int i = 0; i < strtbox_width.get() ; i++)
	        	for (int j = 0; j < strtbox_width.get() ; j++) {
	        		Solid s = newEntity();
	        		if (s != null) {
	        			s.pos.set(adding_cursor.pos().x - side_length / 2 + i * strtbox_space.get(), 
	        					  adding_cursor.pos().y - side_length / 2 + j * strtbox_space.get());
	        			s.radius = solid_rad.get();
	        		}
	        	}
    		}
    		if (build_as_hexa.get()) {
	    	  	for (Entity e : list) if (e.active) e.destroy(); 
	    	  	ball_obj = null; 
	    	  	float side_length = strtbox_width.get() * strtbox_space.get();
	    	  	int flip = 0;
	    	  	for (int i = 0; i < strtbox_width.get() ; i++) {
	    	  		if (flip == 0) flip = 1; else flip = 0;
		        	for (int j = 0; j < strtbox_width.get() ; j++) {
		        		Solid s = newEntity();
		        		if (s != null) {
		        			s.pos.set(adding_cursor.pos().x - 
		        					  side_length / 2 + 
		        					  i * (strtbox_space.get() - (strtbox_space.get() *0.1F)), 
		        					  adding_cursor.pos().y - 
		        					  side_length / 2 + 
		        					  j * strtbox_space.get() +
		        					  (flip * strtbox_space.get() / 2));
		        			s.radius = solid_rad.get();
		        		}
		        	}
	    	  	}
    		}
    		ball_change.run();
    	} } );
	
    del_all = newRun("del_all", "del_all", new nRunnable() { 
      public void run() { 
    	  	for (Entity e : list) if (e.active) e.destroy(); } } );
    is_init = true;
  }
  boolean is_init = false;

  void custom_reset() {
	  if (is_init && reset_build.get()) {
		build_as.run();
  		if (!use_ball.get() && ball_obj != null) { 
			ball_obj.destroy(); 
			ball_obj = null; 
		}
		if (use_ball.get()) {
			ball_obj = newEntity();
	    		if (ball_obj != null) {
	    			ball_obj.pos.set(adding_cursor.pos().x, adding_cursor.pos().y);
	    			ball_obj.radius = ball_rad.get();
	    		}
		}
	  }
  }
  void custom_pre_tick() {
	  for (Solid s1 : solid_list)
	  for (Solid s2 : solid_list) if (s1 != s2) {
		  float d = PApplet.dist(s1.pos.x, s1.pos.y, s2.pos.x, s2.pos.y);
		  if (d < s1.radius/2.0F + s2.radius/2.0F) {
			  PVector push = new PVector(s1.pos.x - s2.pos.x, s1.pos.y - s2.pos.y);
			  push.mult(collide_fact.get());
			  s1.accel.add(push);
			  push.mult(-1);
			  s2.accel.add(push);
		  }
	  }
	  if (use_wall.get()) {
		  float xmin = adding_cursor.pos().x - wall_width.get() / 2.0F;
		  float xmax = adding_cursor.pos().x + wall_width.get() / 2.0F;
		  float ymin = adding_cursor.pos().y - wall_width.get() / 2.0F;
		  float ymax = adding_cursor.pos().y + wall_width.get() / 2.0F;
		  for (Solid s : solid_list) {
			  if (s.pos.x < xmin + s.radius) { //s.accel.add(-s.mov.x * 2, 0); 
			  	s.mov.add((xmin + s.radius) - s.pos.x, 0);
			  }
			  if (s.pos.y < ymin + s.radius) { //s.accel.add(0, -s.mov.y * 2);
			  	s.mov.add(0, (ymin + s.radius) - s.pos.y);
			  }
			  if (s.pos.x > xmax - s.radius) { //s.accel.add(-s.mov.x * 2, 0);
			  	s.mov.add((xmax - s.radius) - s.pos.x, 0);
			  }
			  if (s.pos.y > ymax - s.radius) { //s.accel.add(0, -s.mov.y * 2);
			  	s.mov.add(0, (ymax - s.radius) - s.pos.y);
			  }
		  }
	  }
  }
  void custom_post_tick() {
	  for (Solid s : solid_list) s.draw_halo(canv);
  }
  void custom_frame() {
  }
  void custom_cam_draw_post_entity() {
	  if (show_wall.get()) {

	    	gui.app.stroke(180);
	    	gui.app.strokeWeight(ref_size / (10 * mmain().gui.scale) );
	    	gui.app.noFill();
	    	gui.app.rect(adding_cursor.pos().x - wall_width.get() / 2.0F, 
	    				adding_cursor.pos().y - wall_width.get() / 2.0F,
	    				wall_width.get(),
	    				wall_width.get());
	  }
  }
  void custom_cam_draw_pre_entity() {
  }
  
  Solid build() { return new Solid(this); }
  Solid addEntity() { return newEntity(); }
  Solid newEntity() {
    for (Entity e : list) if (!e.active) { e.activate(); return (Solid)e; } return null; }
}


