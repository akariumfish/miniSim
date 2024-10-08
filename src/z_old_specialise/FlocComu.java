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
  
  ArrayList<Floc> neight_list;
  ArrayList<Floc> blob_list;
  boolean in_blob = false;
  
  int crazy_cnt = 0;
  boolean crazy_state = false;
  
  Floc(FlocComu c) { 
	super(c);
  	tail_list = new ArrayList<PVector>();
  	neight_list = new ArrayList<Floc>();
  	blob_list = new ArrayList<Floc>();
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
  
  void follow(Floc f, float to, float frm, float a) {
	  if (!crazy_state) { headTo(frm, a ); f.headTo(to, a );
	  } else { headAway(frm, a ); f.headAway(to, a ); } }
  void headTo(Floc f, float a) {
	  if (!crazy_state) { headTo(f.pos, a ); f.headTo(pos, a );
	  } else { headAway(f.pos, a ); f.headAway(pos, a ); } }
  void headAway(Floc f, float a) {
	  if (!crazy_state) { headAway(f.pos, a ); f.headTo(pos, a );
	  } else { headTo(f.pos, a ); f.headAway(pos, a ); } }
  
  void pair(Floc b2) {
    float d = PApplet.dist(pos.x, pos.y, b2.pos.x, b2.pos.y);
	if (d < com().VIEWING_DIST.get()) {
	  float purss_eff = com().POURSUITE.get() * (d/com().VIEWING_DIST.get());
	  if (d > com().SPACING_MIN.get() && d < com().SPACING_MAX.get()) {
		float dist_fact = (d - com().SPACING_MIN.get()) / 
				(com().SPACING_MAX.get() - com().SPACING_MIN.get());
//				((com().SPACING_MAX.get() - d) / com().SPACING_MAX.get());
		
	    //follow as much as close
	    	follow(b2, mov.heading(), b2.mov.heading(), com().FOLLOW.get() * (1.0F - dist_fact) );
		//pursue less and less as close
	    	headTo(b2, com().POURSUITE.get() * dist_fact);
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

  Floc frame() { return this; }
  int dev_cnt = 0;
  Floc tick() {
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

  void draw_halo(Canvas canvas) {
    canvas.draw_halo(pos, halo_size, halo_density, halo_col);
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
  
  Floc draw() {

//      com().app.fill(com().val_col_deb.get());
//      com().app.noStroke();
//	  com().app.stroke(255);
//	  com().app.strokeWeight(com().scale.get() * com.sim.cam_gui.scale / (2.0F));
//	  for (Floc n : neight_list) {
//		  com().app.line(pos.x, pos.y, n.pos.x, n.pos.y);
//	  }
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
    com().app.popMatrix();
    if (com().DRAWMODE_DEBUG.get()) {
	    float t = (com().scale.get() * (com().ref_size / 10)) / (com.sim.cam_gui.scale * 6);
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
	  for (Floc f : neight_list) add_to_blob(this, f);
  }
  void add_to_blob(Floc cible, Floc test) {
	  if (!cible.blob_list.contains(test)) {
		  cible.blob_list.add(test);
		  for (Floc n : test.neight_list) add_to_blob(this, n);
	  }
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
      .addDrawerDoubleButton(show_web, show_blob, 10.25F, 1)
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
  
  sFlt POURSUITE, FOLLOW, SPACING_MAX, SPACING_MIN, VIEWING_DIST, SPEED, 
  	HALO_SIZE, HALO_DENS, POINT_FORCE, grow_prob,
  	speed_rng;
  sInt AGE, deriv_speed;
  sBoo create_grower, aging;
  sBoo DRAWMODE_DEF, DRAWMODE_DEBUG, point_to_mouse, point_to_center, point_to_cursor;
  
  sCol val_col_def1, val_col_def2, val_col_deb, val_col_halo1, val_col_halo2, val_col_web;
  sFlt scale, halo_dev, strt_dev;
  sFlt startzone;
  
  sObj grow_obj;
  GrowerComu gcom;
  Canvas canv;
  
  sBoo draw_tail, oscillant, point_to_side, do_flocking, show_web, show_blob;
  sFlt side_dir, oscl_force, oscl_length, side_force, web_weight, web_alpha_min, web_alpha_max;
  sInt tail_long;
  
  sRun kill_all;
  
  sBoo crazy_run;//, crazy_invert, crazy_random;
  sInt crazy_tryrate;
  sFlt crazy_prob, crazy_medlength;//, crazy_lenrngfact;

  FlocComu(Simulation _c, Canvas c, String n, sValueBloc b) { super(_c, n, "floc", 50, b); 
  	canv = c;
    can_init();
  }
  void can_init() {

	  crazy_run = newBoo(true, "crazy_run");
	  crazy_tryrate = newInt(200, "crazy_tryrate");
	  crazy_prob = newFlt(0.01F, "crazy_prob");
	  crazy_medlength = newFlt(200, "crazy_medlength");
	    
	    do_flocking = newBoo(true, "do_flocking", "do_flocking");
	    POURSUITE = newFlt(0.3F, "POURSUITE", "poursuite");
	    FOLLOW = newFlt(0.0036F, "FOLLOW", "follow");
	    VIEWING_DIST = newFlt(95, "VIEWING_DIST", "viewing distence");
	    SPACING_MIN = newFlt(95, "SPACING_MIN", "space min");
	    SPACING_MAX = newFlt(95, "SPACING_MAX", "space max");
	    SPEED = newFlt(2, "SPEED", "speed");
	    speed_rng = newFlt(0.5F, "speed_rng", "speed_rng");
	    
	    startzone = newFlt(500F, "startzone", "startzone");
	    strt_dev = newFlt(4.0F, "strt_dev");
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
	    HALO_SIZE = menuFltFact(80, 2.0F, "HALO_SIZE");
	    HALO_DENS = menuFltFact(0.15F, 2.0F, "HALO_DENS");
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
	    
	    kill_all = newRun("kill_all", "kill_all", new nRunnable() { 
	      public void run() { 
	    	  	for (Entity e : list) if (e.active) e.destroy(); } } );
  }
  
  void custom_pre_tick() {
	  for (Entity e : list) {
  	    ((Floc)e).neight_list.clear();
	  }
	  if (do_flocking.get())
	    for (Entity e1 : list) for (Entity e2 : list)
	        if (e1.id < e2.id && e1 != e2 && e1.active && e2.active)
	            ((Floc)e1).pair(((Floc)e2));
	  if (show_blob.get()) {
		  for (Entity e : list) {
		    ((Floc)e).blob_list.clear();
	    	    ((Floc)e).in_blob = false;
		  }
	      for (Entity e : list) ((Floc)e).build_blob();
	      
	      for (int i_test = 0 ; i_test < list.size() ; i_test++) {
	  	    Floc f = ((Floc)list.get(i_test));
	  	    int lsize = f.blob_list.size();
		  	if (f.active && lsize > 3 && !f.in_blob) {
		  		for (int i_p1 = 1 ; i_p1 < lsize ; i_p1++)
		  		for (int i_p2 = 1 ; i_p2 < lsize ; i_p2++)
		  		for (int i_p3 = 1 ; i_p3 < lsize ; i_p3++) {
		  			if (i_p1 != i_p2 && i_p2 != i_p3 && i_p3 != i_p1) {
		  				Floc e1 = f.blob_list.get(i_p1);
		  				Floc e2 = f.blob_list.get(i_p2);
		  				Floc e3 = f.blob_list.get(i_p3);
		  				if (e1.active && e2.active && e3.active && 
		  					RConst.point_in_trig(e1.pos, e2.pos, e3.pos, f.pos))
		  					f.in_blob = true;
		  			}
		  		}
		  	}
	      }
	  }
//      for (Entity e : list) {
//    	    Floc f = ((Floc)e);
//    	  	if (f.blob_list.size() > 3) {
//    	  		for (Floc e1 : f.blob_list) for (Floc e2 : f.blob_list) for (Floc e3 : f.blob_list)
//  			if (e1 != f && e2 != f && e3 != f && e1 != e2 && e2 != e3 && e3 != e1) {
//  				if (RConst.point_in_trig(e1.pos, e2.pos, e3.pos, f.pos))
//  					f.in_blob = true;
//  			}
//    	  	}
//      }
  }
  void custom_post_tick() {
	  canv.floc_tick(this);
  }
  void custom_frame() {
    //can.drawHalo(this);
  }
  void custom_cam_draw_post_entity() {
	  if (show_web.get()) {
		  for (int i = 0 ; i < list.size() - 1 ; i++) 
		  for (int j = i+1 ; j < list.size() ; j++) {
			  Floc f1 = (Floc)list.get(i);
			  Floc f2 = (Floc)list.get(j); 
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
	  	  float t = (scale.get() * (ref_size / 10.0F)) / (sim.cam_gui.scale * 6.0F);
		  PVector m = new PVector(sim.inter.cam_gui.mouseVector.x, 
				  				 sim.inter.cam_gui.mouseVector.y);
		  app.noStroke();
		  app.fill(120);
	      for (Entity e : list) if (e.active)  {
	    	  	  Floc f = ((Floc)e);
	    	  	  float d = PApplet.dist(f.pos.x, f.pos.y, m.x, m.y);
	    	  	  if (d < t/2.0F) {
	  			app.pushMatrix();
	  			app.translate(f.pos.x, f.pos.y);
	  		    app.ellipse(-t/2, -t/2, t, t);
	  		    app.popMatrix();
	    	  		for (Floc b : f.blob_list) {
	    	  			app.pushMatrix();
	    	  			app.translate(b.pos.x, b.pos.y);
	    	  		    app.ellipse(-t/2, -t/2, t, t);
	    	  		    app.popMatrix();
	    	  		}
	    	  	  }
	    	  	  
	      }
//	      for (Entity e : list) if (e.active) {
//	    	  	  Floc f = ((Floc)e);
//    	  		  app.strokeWeight(scale.get() * (2.0F));
//    	  		  app.noFill();
//	    	  	  if (f.in_blob) {
//	    	  		  app.stroke(0, 255, 0);
//	    	  	  } else {
//	    	  		  app.stroke(0, 0, 255);
//	    	  	  }
//	    	  	  app.pushMatrix();
//	    	  	  app.translate(f.pos.x, f.pos.y);
//	    	  	  app.ellipse(-t/2, -t/2, t, t);
//	    	  	  app.popMatrix();
//	      }
	  }
	  
  }
  void custom_cam_draw_pre_entity() {
    //can.drawCanvas();
  }
  
  Floc build() { return new Floc(this); }
  Floc addEntity() { return newEntity(); }
  Floc newEntity() {
    for (Entity e : list) if (!e.active) { e.activate(); return (Floc)e; } return null; }
}



