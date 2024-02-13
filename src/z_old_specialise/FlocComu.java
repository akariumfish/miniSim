package z_old_specialise;

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
  
  Floc(FlocComu c) { super(c); }
  
  void draw_halo(Canvas canvas) {
    canvas.draw_halo(pos, halo_size, halo_density, com().val_col_halo.get());
  }
  
  void headTo(PVector c, float s) {
    PVector l = new PVector(c.x, c.y);
    l.add(-pos.x, -pos.y);
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
    if (d < com().SPACING.get()) {
      headTo(b2.mov.heading(), com().FOLLOW.get() / ((com().SPACING.get() - d) / com().SPACING.get()) );
      b2.headTo(mov.heading(), com().FOLLOW.get() / ((com().SPACING.get() - d) / com().SPACING.get()) );
    } else {
      headTo(b2.pos, com().POURSUITE.get() / d);
      b2.headTo(pos, com().POURSUITE.get() / d);
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
    //pos.x = random(-com().startbox, com().startbox);
    //pos.y = random(-com().startbox, com().startbox);
    speed = com().app.random(0.3F, 1.7F) * com().SPEED.get();
    mov.x = speed; mov.y = 0;
    mov.rotate(com().app.random(PConstants.PI * 2.0F));
    return this;
  }
  Floc frame() { return this; }
  Floc tick() {
    age++;
    if (age > max_age) {
      if (com().create_grower.get() && com().gcom != null && com().app.crandom(com().grow_prob.get()) > 0.5) {
        Grower ng = com().gcom.newEntity();
        if (ng != null) ng.define(new PVector(pos.x, pos.y), new PVector(1, 0).rotate(mov.heading()));
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
    pos.add(mov);
    return this;
  }
  Floc draw() {
	  com().app.fill(com().val_col_def.get());
	  com().app.stroke(com().val_col_def.get());
	  com().app.strokeWeight(4/com.sim.cam_gui.scale);
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
  public FlocPrint(Simulation s) { super("Floc"); sim = s; }
  public void default_build() { }
  public FlocComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new FlocComu(sim, n, b); }
}


  void comPanelBuild(nFrontPanel sim_front) {
    nFrontTab tab = sim_front.addTab("Control");
    tab.getShelf()
      .addDrawerDoubleButton(DRAWMODE_DEF, DRAWMODE_DEBUG, 10.25F, 1)
      .addSeparator(0.125)
      .addDrawerTripleButton(point_to_mouse, point_to_center, point_to_cursor, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(POURSUITE, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(FOLLOW, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(SPACING, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(SPEED, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(LIMIT, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(AGE, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(HALO_SIZE, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(HALO_DENS, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(POINT_FORCE, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerActFactValue("grower", create_grower, grow_prob, 2, 10, 1)
      .addSeparator(0.125)
      ;
    
    sim_front.toLayerTop();
  }
  
//  void selected_comu(Community c) { 
//    if (c != null && c.type_value.get().equals("grow")) gcom = (GrowerComu)c;
//  }
  
  sFlt POURSUITE, FOLLOW, SPACING, SPEED, HALO_SIZE, HALO_DENS, POINT_FORCE, grow_prob ;
  sInt LIMIT, AGE ;
  sBoo DRAWMODE_DEF, DRAWMODE_DEBUG, create_grower, point_to_mouse, point_to_center, point_to_cursor;
  
  sCol val_col_def, val_col_deb, val_col_halo;
  sFlt scale;
  
  int startbox = 400;
  
  sObj grow_obj;
  GrowerComu gcom;
  
  FlocComu(Simulation _c, String n, sValueBloc b) { super(_c, n, "floc", 50, b); 
    POURSUITE = newFlt(0.3F, "POURSUITE", "poursuite");
    FOLLOW = newFlt(0.0036F, "FOLLOW", "follox");
    SPACING = newFlt(95, "SPACING", "space");
    SPEED = newFlt(2, "SPEED", "speed");
    grow_prob = newFlt(1, "grow_prob", "grow_prob");
    LIMIT = newInt(1600, "limit", "limit");
    AGE = newInt(2000, "age", "age");
    HALO_SIZE = newFlt(80, "HALO_SIZE", "Size");
    HALO_DENS = newFlt(0.15F, "HALO_DENS", "Dens");
    POINT_FORCE = newFlt(0.01F, "POINT_FORCE", "point");
    
    DRAWMODE_DEF = newBoo(true, "DRAWMODE_DEF", "draw1");
    DRAWMODE_DEBUG = newBoo(false, "DRAWMODE_DEBUG", "draw2");
    
    create_grower = newBoo(true, "create_grower", "create grow");
    point_to_mouse = newBoo(false, "point_to_mouse", "to mouse");
    point_to_center = newBoo(false, "point_to_center", "to center");
    point_to_cursor = newBoo(false, "point_to_cursor", "to cursor");
    //init_canvas();
    
    val_col_def = menuColor(app.color(220), "val_col_def");
    val_col_deb = menuColor(app.color(255, 0, 0), "val_col_deb");
    val_col_halo = menuColor(app.color(255, 0, 0), "val_col_halo");
    scale = menuFltSlide(10, 5, 100, "length");
    
    grow_obj = newObj("grow_obj", "grow_obj");
    grow_obj.addEventChange(new nRunnable() { public void run() {
      if (grow_obj.isSheet()) {
        Macro_Sheet ms = grow_obj.asSheet();
        if (ms.specialize.get().equals("Grower")) gcom = (GrowerComu)ms;
      }
    }});
    
  }
  
  void custom_pre_tick() {
    for (Entity e1 : list)
      for (Entity e2 : list)
        if (e1.id < e2.id && e1 != e2 && e1.active && e2.active)
            ((Floc)e1).pair(((Floc)e2));
          
  }
  void custom_post_tick() {}
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



