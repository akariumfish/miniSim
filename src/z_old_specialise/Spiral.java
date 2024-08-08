package z_old_specialise;

import java.util.ArrayList;

import Macro.*;
import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

class Arc extends Entity {
  float rad, a_strt, a_end, global_rot, local_rot;
  
  Arc(Spiral c) { 
	super(c);
  }
  
  void draw_halo(Canvas canvas) {
//    canvas.draw_halo(pos, halo_size, halo_density, com().val_col_halo.get());
  }
  
  Arc init() {
	  rad = com().radius.get();
	  a_strt = 0;//-PConstants.PI / 2.0F;
	  a_end = PConstants.PI;
	  local_rot = 0;
	  global_rot = 0;
    return this;
  }
  Arc frame() { return this; }
  Arc tick() {
	  local_rot -= com().rot_speed.get();
    return this;
  }
  Arc draw() {
	  com().app.pushMatrix();
	  com().app.translate(com().adding_cursor.pos().x, com().adding_cursor.pos().y);
	  com().app.rotate(com().adding_cursor.dir().heading());
	  
	  com().app.pushMatrix();
	  com().app.translate(- com().spacing.get() / 2.0F, 0);
	  com().app.rotate(local_rot);
	  com().app.translate(-rad/2.0F, 0);
	  com().app.noFill();
	  com().app.stroke(255);
	  com().app.strokeWeight(2);
	  com().app.ellipseMode(PConstants.CENTER);
	  com().app.arc(0, 0, rad, rad, a_strt, a_end);
	  com().app.popMatrix();

	  com().app.pushMatrix();
	  com().app.translate(com().spacing.get() / 2.0F, 0);
	  com().app.rotate(-local_rot);
	  com().app.translate(rad/2.0F, 0);
	  com().app.noFill();
	  com().app.stroke(255);
	  com().app.strokeWeight(2);
	  com().app.ellipseMode(PConstants.CENTER);
	  com().app.arc(0, 0, rad, rad, a_strt, a_end);
	  com().app.popMatrix();

	  com().app.popMatrix();
      return this;
  }
  Arc clear() { return this; }
  Spiral com() { return ((Spiral)com); }
  
}





public class Spiral extends Community {
  

public static class SpiralPrint extends Sheet_Specialize {
  Simulation sim;
  Canvas can;
  public SpiralPrint(Simulation s) { super("Spiral"); sim = s; }
  public SpiralPrint(Simulation s, Canvas c) { super("Spiral"); sim = s; can = c; }
  public void default_build() { }
  public Spiral get_new(Macro_Sheet s, String n, sValueBloc b) { return new Spiral(sim, can, n, b); }
}


  void comPanelBuild(nFrontPanel sim_front) {
    sim_front.toLayerTop();
  }
  
  Canvas canv;
  
  sFlt radius, rot_speed, spacing;

  Spiral(Simulation _c, Canvas c, String n, sValueBloc b) { super(_c, n, "spiral", 50, b); 
  	canv = c;

  	radius = menuFltFact(2000, 2.0F, "radius");
  	spacing = menuFltFact(2000, 2.0F, "spacing");
  	rot_speed = menuFltFact(0.05F, 2.0F, "rot_speed");
	  
  }
  
  void custom_pre_tick() {
  }
  void custom_post_tick() {
	  canv.spiral_tick(this);
  }
  void custom_frame() {
    //can.drawHalo(this);
  }
  void custom_cam_draw_post_entity() {}
  void custom_cam_draw_pre_entity() {
    //can.drawCanvas();
  }
  
  Arc build() { return new Arc(this); }
  Arc addEntity() { return newEntity(); }
  Arc newEntity() {
    for (Entity e : list) if (!e.active) { e.activate(); return (Arc)e; } return null; }
}



