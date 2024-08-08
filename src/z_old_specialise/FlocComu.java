package z_old_specialise;

import java.util.ArrayList;

import Macro.*;
import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

class Floc extends Entity {
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
  
  Floc(FlocComu c) { 
	super(c);
  	tail_list = new ArrayList<PVector>();
  }
  
  void draw_halo(Canvas canvas) {
    canvas.draw_halo(pos, halo_size, halo_density, halo_col);
  }

  void headTo(PVector c, float s) {
    PVector l = new PVector(c.x, c.y);
    l.add(-pos.x, -pos.y);
    float r1 = RConst.mapToCircularValues(mov.heading(), l.heading(), s, -PConstants.PI, PConstants.PI);
    mov.x = speed; mov.y = 0;
    mov.rotate(r1);
  }
  void headAway(PVector c, float s) {
    PVector l = new PVector(c.x, c.y);
    l.add(-pos.x, -pos.y);
    l.mult(-1);
    float r1 = RConst.mapToCircularValues(mov.heading(), l.heading(), s, -PConstants.PI, PConstants.PI);
    mov.x = speed; mov.y = 0;
    mov.rotate(r1);
  }
  void headTo(float l, float s) {
    float r1 = RConst.mapToCircularValues(mov.heading(), l, s, -PConstants.PI, PConstants.PI);
    mov.x = speed; mov.y = 0;
    mov.rotate(r1);
  }
  
  void pair(Floc b2) {
    float d = PApplet.dist(pos.x, pos.y, b2.pos.x, b2.pos.y);
    if (d > com().SPACING_MIN.get() && d < com().SPACING_MAX.get()) {
      headTo(b2.mov.heading(), 
    		  com().FOLLOW.get() / ((com().SPACING_MAX.get() - d) / com().SPACING_MAX.get()) );
      b2.headTo(mov.heading(), 
    		  com().FOLLOW.get() / ((com().SPACING_MAX.get() - d) / com().SPACING_MAX.get()) );
    } else if (d >= com().SPACING_MAX.get()) {
        headTo(b2.pos, com().POURSUITE.get() / d);
        b2.headTo(pos, com().POURSUITE.get() / d);
    } else if (d <= com().SPACING_MIN.get()) {
        headAway(b2.pos, com().POURSUITE.get() / d);
        b2.headAway(pos, com().POURSUITE.get() / d);
    }
  }
  
  Floc init() {
    age = 0;
    max_age = (int)(com().app.random(0.3F, 1.7F) * com().AGE.get());
    halo_size = com().HALO_SIZE.get();
    halo_density = com().HALO_DENS.get();
    halo_size += com().app.random(com().HALO_SIZE.get());
    halo_density += com().app.random(com().HALO_DENS.get());
    pos = com().adding_cursor.pos();
    pos.x = pos.x + com().app.random(2 * com().startzone.get()) - com().startzone.get();
    pos.y = pos.y + com().app.random(2 * com().startzone.get()) - com().startzone.get();
    
    speed = com().app.random(1 - com().speed_rng.get(), 1 + com().speed_rng.get())
    			* com().SPEED.get();
    mov.x = speed; mov.y = 0;
    if (com().adding_cursor.dir_set()) {
    		mov.rotate(com().adding_cursor.dir().heading());
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
  
  Floc frame() { return this; }
  int dev_cnt = 0;
  Floc tick() {
	  dev_cnt++;
	  if (dev_cnt > com().deriv_speed.get()) {
		  dev_cnt = 0;
		  halo_col = derivCol(halo_col, (int)com().halo_dev.get());
	  }
	  
    age++;
    if (age > max_age && com().aging.get()) {
      if (com().create_grower.get() && com().gcom != null && 
    		  com().app.crandom(com().grow_prob.get()) > 0.5) {
        Grower ng = com().gcom.newEntity();
        if (ng != null) ng.define(new PVector(pos.x, pos.y), new PVector(1, 0).rotate(mov.heading()), 0);
      }
      destroy();
    }
    int cible_cnt = 0;
    if (com().point_to_mouse.get()) cible_cnt++;
    if (com().point_to_center.get()) cible_cnt++;
    if (com().point_to_cursor.get()) cible_cnt++;
    //point toward mouse
    if (com().point_to_mouse.get()) headTo(com().sim.inter.cam.screen_to_cam(
    		new PVector(com().app.mouseX, com().app.mouseY)), 
                                           com().POINT_FORCE.get() / cible_cnt);
    //point toward center
    if (com().point_to_center.get()) headTo(new PVector(0, 0), com().POINT_FORCE.get() / cible_cnt);
    //point toward cursor
    if (com().point_to_cursor.get()) headTo(com().adding_cursor.pos(), com().POINT_FORCE.get() / cible_cnt);
    
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
  Floc draw() {

//      com().app.fill(com().val_col_deb.get());
//      com().app.noStroke();
	  com().app.stroke(col);
	  com().app.strokeWeight(com().scale.get() / (1.0F));// * com.sim.cam_gui.scale
      PVector p0 = new PVector();
	  if (com().draw_tail.get() && tail_list.size() > 1) 
		for (int i = 0 ; i < tail_list.size() - 1 ; i++) {
		  PVector p1 = new PVector(tail_list.get(i).x, tail_list.get(i).y);
		  PVector p2 = new PVector(tail_list.get(i+1).x, tail_list.get(i+1).y);
		  PVector l = new PVector(p2.x - p1.x, p2.y - p1.y);
		  l.mult(tail_space / 20.0F);
//		  com().app.pushMatrix();
//		  com().app.translate(p1.x + l.x, p1.y + l.y);
//	      float t = (com().scale.get() * (com().ref_size / 10)) / 5.0F;
		  if (i == tail_list.size()-2) com().app.line(pos.x, pos.y, p1.x + l.x, p1.y + l.y);
		  if (i > 0 && i < tail_list.size()-1) com().app.line(p0.x, p0.y, p1.x + l.x, p1.y + l.y);
//	      com().app.ellipse(0, 0, t, t);
//	      com().app.popMatrix();
		  p0.set(p1.x + l.x, p1.y + l.y);
	  }
	  
	  com().app.fill(col);
	  com().app.stroke(col);
//	  com().app.strokeWeight(4/com.sim.cam_gui.scale);
	  com().app.pushMatrix();
	  com().app.translate(pos.x, pos.y);
	  com().app.rotate(mov.heading());
    if (com().DRAWMODE_DEF.get()) {
    		com().app.line(0, 0, -com().scale.get(), -com().scale.get());
    		com().app.line(2, 0, -com().scale.get(), 0);
    		com().app.line(0, 0, -com().scale.get(), com().scale.get());
    }
    com().app. fill(com().val_col_deb.get());
    //stroke(com().val_col_deb.get());
    com().app. noStroke();
    float t = (com().scale.get() * (com().ref_size / 10)) / (com.sim.cam_gui.scale * 6);
    if (com().DRAWMODE_DEBUG.get()) com().app.ellipse(0, 0, t, t);
    com().app.popMatrix();
    return this;
  }
  Floc clear() { return this; }
  FlocComu com() { return ((FlocComu)com); }
  
}





public class FlocComu extends Community {
  

public static class FlocPrint extends Sheet_Specialize {
  Simulation sim;
  Canvas can;
  public FlocPrint(Simulation s) { super("Floc"); sim = s; }
  public FlocPrint(Simulation s, Canvas c) { super("Floc"); sim = s; can = c; }
  public void default_build() { }
  public FlocComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new FlocComu(sim, can, n, b); }
}


  void comPanelBuild(nFrontPanel sim_front) {
	sim_front.getTab(1).getShelf()
	    .addDrawerTripleButton(DRAWMODE_DEF, DRAWMODE_DEBUG, draw_tail, 10.25F, 1)
	    .addSeparator(0.125)
    ;

	sim_front.getTab(2).getShelf()
    		.addDrawerFactValue(startzone, 2, 10, 1)
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
      ;
    

    tab = sim_front.addTab("Comport");
    tab.getShelf()
    .addDrawerActFactValue("grower", create_grower, grow_prob, 2, 10, 1)
    .addSeparator(0.125)
    .addDrawerActFactValue("aging", aging, AGE, 2, 10, 1)
    .addSeparator(0.125)
    .addDrawerTripleButton(point_to_mouse, point_to_center, point_to_cursor, 10, 1)
    .addSeparator(0.125)
    .addDrawerFactValue(POINT_FORCE, 2, 10, 1)
    .addSeparator(0.125)
    .addDrawerDoubleButton(oscillant, point_to_side, 10.25F, 1)
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
  
  void selected_comu(Community c) { 
    if (c != null && c.type_value.get().equals("grow")) gcom = (GrowerComu)c;
  }
  
  sFlt POURSUITE, FOLLOW, SPACING_MAX, SPACING_MIN, SPEED, 
  	HALO_SIZE, HALO_DENS, POINT_FORCE, grow_prob,
  	speed_rng;
  sInt AGE, deriv_speed;
  sBoo create_grower, aging;
  sBoo DRAWMODE_DEF, DRAWMODE_DEBUG, point_to_mouse, point_to_center, point_to_cursor;
  
  sCol val_col_def1, val_col_def2, val_col_deb, val_col_halo1, val_col_halo2;
  sFlt scale, halo_dev, strt_dev;
  sFlt startzone;
  
  sObj grow_obj;
  GrowerComu gcom;
  Canvas canv;
  
  sBoo draw_tail, oscillant, point_to_side, do_flocking;
  sFlt side_dir, oscl_force, oscl_length, side_force;
  sInt tail_long;

  FlocComu(Simulation _c, Canvas c, String n, sValueBloc b) { super(_c, n, "floc", 50, b); 
  	canv = c;
    can_init();
  }
  void can_init() {

	    do_flocking = newBoo(true, "do_flocking", "do_flocking");
	    POURSUITE = newFlt(0.3F, "POURSUITE", "poursuite");
	    FOLLOW = newFlt(0.0036F, "FOLLOW", "follox");
	    SPACING_MIN = newFlt(95, "SPACING_MIN", "space min");
	    SPACING_MAX = newFlt(95, "SPACING_MAX", "space max");
	    SPEED = newFlt(2, "SPEED", "speed");
	    speed_rng = newFlt(0.5F, "speed_rng", "speed_rng");
	    
	    startzone = newFlt(500F, "startzone", "startzone");
	    AGE = newInt(2000, "age", "age");
	    aging = newBoo(true, "aging", "aging");

	    point_to_mouse = newBoo(false, "point_to_mouse", "to mouse");
	    point_to_center = newBoo(false, "point_to_center", "to center");
	    point_to_cursor = newBoo(false, "point_to_cursor", "to cursor");
	    POINT_FORCE = newFlt(0.01F, "POINT_FORCE");
	    oscillant = newBoo(false, "oscillant", "oscillant");
	    oscl_force = newFlt(0.01F, "oscl_force");
	    oscl_length = newFlt(30F, "oscl_length");
	    point_to_side = newBoo(false, "point_to_side", "point_to_side");
	    side_dir = newFlt(0, "side_dir")
	    		.set_limit(-PConstants.PI, PConstants.PI);
	    side_force = newFlt(0.01F, "side_force");
	    
	    create_grower = newBoo(true, "create_grower", "create grow");
	    grow_prob = newFlt(1, "grow_prob", "grow_prob");

	    DRAWMODE_DEF = newBoo(true, "DRAWMODE_DEF", "draw1");
	    DRAWMODE_DEBUG = newBoo(false, "DRAWMODE_DEBUG", "draw2");
	    draw_tail = newBoo(false, "draw_tail", "tail");
	    tail_long = menuIntIncr(20, 10, "tail_long");
	    HALO_SIZE = menuFltFact(80, 2.0F, "HALO_SIZE");
	    HALO_DENS = menuFltFact(0.15F, 2.0F, "HALO_DENS");
	    strt_dev = menuFltFact(4.0F, 2.0F, "strt_dev");
	    val_col_def1 = menuColor(app.color(220), "val_col_def1");
	    val_col_def2 = menuColor(app.color(220), "val_col_def2");
	    val_col_deb = menuColor(app.color(255, 0, 0), "val_col_deb");
	    val_col_halo1 = menuColor(app.color(255, 0, 0), "val_col_halo1");
	    val_col_halo2 = menuColor(app.color(255, 0, 0), "val_col_halo2");
	    halo_dev = menuFltFact(0.1F, 2.0F, "halo_dev");
	    deriv_speed = menuIntFact(5, 2.0F, "deriv_speed");
	    scale = menuFltSlide(10, 5, 100, "length");

	    //init_canvas();
	    
	    grow_obj = newObj("grow_obj", "grow_obj");
	    grow_obj.addEventChange(new nRunnable() { public void run() {
	      if (grow_obj.isSheet()) {
	        Macro_Sheet ms = grow_obj.asSheet();
	        if (ms.specialize.get().equals("Grower")) gcom = (GrowerComu)ms;
	      }
	    }});
	  
  }
  
  void custom_pre_tick() {
	  if (do_flocking.get())
	    for (Entity e1 : list)
	      for (Entity e2 : list)
	        if (e1.id < e2.id && e1 != e2 && e1.active && e2.active)
	            ((Floc)e1).pair(((Floc)e2));
          
  }
  void custom_post_tick() {
	  canv.floc_tick(this);
  }
  void custom_frame() {
    //can.drawHalo(this);
  }
  void custom_cam_draw_post_entity() {}
  void custom_cam_draw_pre_entity() {
    //can.drawCanvas();
  }
  
  Floc build() { return new Floc(this); }
  Floc addEntity() { return newEntity(); }
  Floc newEntity() {
    for (Entity e : list) if (!e.active) { e.activate(); return (Floc)e; } return null; }
}



