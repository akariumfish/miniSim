
package z_old_specialise;

import java.util.ArrayList;

import Macro.MCanvas;
import RApplet.RConst;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Floc extends Entity {
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
//					((com().SPACING_MAX.get() - d) / com().SPACING_MAX.get());
			
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
	  public void draw_halo(MCanvas canvas) {
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

//	      com().app.fill(com().val_col_deb.get());
//	      com().app.noStroke();
//		  com().app.stroke(255);
//		  com().app.strokeWeight(com().scale.get() * com.sim.cam_gui.scale / (2.0F));
//		  for (Floc n : neight_list) {
//			  com().app.line(pos.x, pos.y, n.pos.x, n.pos.y);
//		  }
		  com().app.stroke(col);
		  com().app.strokeWeight(com().scale.get() / (1.0F));// * com.sim.cam_gui.scale
	      PVector p0 = new PVector();
		  if (com().draw_tail.get() && tail_list.size() > 1) 
			for (int i = 0 ; i < tail_list.size() - 1 ; i++) {
			  PVector p1 = new PVector(tail_list.get(i).x, tail_list.get(i).y);
			  PVector p2 = new PVector(tail_list.get(i+1).x, tail_list.get(i+1).y);
			  PVector l = new PVector(p2.x - p1.x, p2.y - p1.y);
			  l.mult(tail_space / 20.0F);
//			  com().app.pushMatrix();
//			  com().app.translate(p1.x + l.x, p1.y + l.y);
//		      float t = (com().scale.get() * (com().ref_size / 10)) / 5.0F;
			  if (i == tail_list.size()-2) com().app.line(pos.x, pos.y, p1.x + l.x, p1.y + l.y);
			  if (i > 0 && i < tail_list.size()-1) com().app.line(p0.x, p0.y, p1.x + l.x, p1.y + l.y);
//		      com().app.ellipse(0, 0, t, t);
//		      com().app.popMatrix();
			  p0.set(p1.x + l.x, p1.y + l.y);
		  }
		  
		  com().app.fill(col);
		  com().app.stroke(col);
//		  com().app.strokeWeight(4/com.sim.cam_gui.scale);
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
