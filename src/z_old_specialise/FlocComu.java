package z_old_specialise;

import java.util.ArrayList;

import Macro.*;
import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;







public class FlocComu extends Community {
  

public static class FlocPrint extends Sheet_Specialize {
  Simulation sim;
  Canvas can;
  public FlocPrint(Simulation s) { super("Floc"); sim = s; can = null; }
  public FlocPrint(Simulation s, Canvas c) { super("Floc"); sim = s; can = c; }
  public void default_build() { }
  public FlocComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new FlocComu(sim, can, n, b); }
}


  void comPanelBuild(nFrontPanel sim_front) {
	sim_front.getTab(1).getShelf()
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
    .addDrawerActFactValue("grower", create_grower, grow_prob, 2, 10, 1)
    .addSeparator(0.125)
    .addDrawerActFactValue("aging", aging, AGE, 2, 10, 1)
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
	  if (canv != null) canv.floc_tick(this);
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
  public Floc newEntity() {
    for (Entity e : list) if (!e.active) { e.activate(); return (Floc)e; } return null; }
}



