package Macro;

import RApplet.RConst;
import RApplet.Rapp;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

class EGrower extends Entity {

	  PVector pos = new PVector();
	  PVector grows = new PVector();
	  PVector dir = new PVector();

	  float halo_size = 10;
	  float halo_density = 0.2F;

	  // condition de croissance
	  boolean end = false;
	  int sprouts = 0;
	  float age = 0.0F;
	  float start = 0.0F;
	  
	  EGrower parent = this;
	  float width = 0;
	  int leaf_nb = 1;

	  EGrower(MGrowerGroup c) { 
		super(c);
	  }
	  EGrower init() {
	    end = false;
	    sprouts = 0;
	    age = 0;
	    start = 0.0F;
	    flt_cnt = 0;
	    width = com().MIN_LINE_WIDTH.get();
	    parent = this;
	    leaf_nb = (int) com().app.random(1, com().leaf_nb.get());
	    if (com().app.random(1) > 0.5) side = true; else side = false;
	    return this;
	  }

	  PVector headTo(PVector p, PVector mov, PVector c, float s) {
	    PVector l = new PVector(c.x, c.y);
	    l.add(-p.x, -p.y);
	    float r1 = RConst.mapToCircularValues(mov.heading(), l.heading(), s, 
	    		-PConstants.PI, PConstants.PI);

	    PVector o = new PVector(mov.mag(), 0);
	    o.rotate(r1);
	    return o;
	  }
	  EGrower define(EGrower g) {
		  define(g.pos, g.dir, g.rang);
	      return this;
	  }
	  EGrower define(PVector _p, PVector _d, int r) {
		rang = r+1;
		
	    pos = _p;
	    grows = new PVector(com().L_MIN.get() + 
	    		com().app.crandom(com().L_DIFFICULTY.get())*(com().L_MAX.get() - com().L_MIN.get()), 0);
	    
		if (_d.mag() >= 1) {
	    		grows.rotate(_d.heading());
	    }
	    else {	
	    		grows.rotate(com().app.random(-3.14F, 3.14F));
	    }
	    grows.rotate(com().app.random(PConstants.PI / com().DEVIATION.get()) - 
	    			 ((PConstants.PI / com().DEVIATION.get()) / 2));

//	    PVector l = new PVector(com().obj_cur.pos().x, com().obj_cur.pos().y);
//	    l.add(-pos.x, -pos.y);
//	    if (l.mag()>com().cible_zone.get())
//	    		grows.set(headTo(pos, grows, com().obj_cur.pos(), com().cible_force.get()));
	    
	    dir = new PVector();
	    dir = grows;
	    grows = PVector.add(pos, grows);
	    return this;
	  }
	  void addWidth() { addWidth(0); }
	  void addWidth(int r) {
		  width += (float)(com().MAX_LINE_WIDTH.get() - com().MIN_LINE_WIDTH.get()) / 
				  (float)com().max_entity.get();
		  if ( parent != null && parent != this && parent.active && 
			  r < com().max_entity.get() / 4.0 ) parent.addWidth(r+1);
	  }
	  EGrower frame() { return this; }
	  
	  EGrower tick() {
	    if (com().AGE_ON.get() || age <= com().TEEN_AGE.get()) age++;
	    if (age < com().TEEN_AGE.get()) {
	      start = (float)age / (float)com().TEEN_AGE.get();
	    } else start = 1;

	    //grow
	    if (start == 1 && !end && sprouts == 0 && com().growP.test(com().app)) {
	      EGrower n = com().newEntity();
	      if (n != null) {
	        n.define(grows, dir, rang);
	        n.parent = this;
	        if (com().growing_width.get()) n.addWidth();
	        sprouts++;
	      }
	    }

	    // sprout
	    if (start == 1 && !end && com().sproutP.test(com().app)) {
	      EGrower n = com().newEntity();
	      if (n != null) {
	        PVector _p = new PVector(0, 0);
	        PVector _d = new PVector(0, 0);
	        _d.add(grows).sub(pos);
	        _d.setMag(com().app.random(1.0F) * _d.mag());
	        _p.add(pos).add(_d);
	        n.define(_p, _d, rang);
//		        n.parent = n;
//		        n.addWidth();
	        sprouts++;
	      }
	      //sprouts = (int[]) expand(sprouts, sprouts.length + 1);
	      //sprouts[sprouts.length - 1] = temp_b.id;
	      //temp_b.this_sprout_index = sprouts.length - 1;
	      //sprouts_nb++;
	    }

	    // leaf
	    if (start == 1 && !end && age < com().OLD_AGE.get() / 2 && com().leafP.test(com().app)) {
	      PVector _p = new PVector(0, 0);
	      PVector _d = new PVector(0, 0);
	      _d.add(grows).sub(pos);
	      _d.setMag(com().app.random(1.0F) * _d.mag());
	  	  if (com().app.random(1.0F) > 0.5)
		  	_d.rotate(PConstants.PI / 3);
	  	  else _d.rotate(-PConstants.PI / 3);
	      _p.add(pos).add(_d);
	      EGrower n = com().newEntity();
	      if (n != null) {
	        n.define(_p, _d, rang);
	        n.parent = this;
	        n.addWidth();
	        n.end = true;
	        sprouts++;
	      }
	    }

	    // stop growing
	    if (start == 1 && !end && sprouts == 0 && com().stopP.test(com().app)) {
	    	for (Macro_Connexion c : com().link_out.connected_inputs) {
        	  	MFlocGroup fcom = null;
    			if (c.elem.bloc.val_type.get().equals("flocs")) {
    				fcom = (MFlocGroup)c.elem.bloc;
    			} 
            if (com().create_floc.get() && fcom != null && 
            		com().gui.app.crandom(com().floc_prob.get()) > 0.9) {
              EFloc f = fcom.newEntity();
              if (f != null) {
                f.pos.x = pos.x;
                f.pos.y = pos.y;
              }
            }
    	  	  }
	      end = true;
	    }

	    // die
	    float rng = com().app.crandom(com().dieP.DIFFICULTY.get());
	    if (com().dieP.ON.get() && start == 1 && !(!end && sprouts == 0) &&
	      (rng > ( (float)com().OLD_AGE.get() / (float)age ) //||
	      //rng / DIE_DIFFICULTY_DIVIDER > ((float)MAX_LIST_SIZE - (float)baseNb()) / (float)MAX_LIST_SIZE
	      )) {
	      this.destroy();
	    }
	    return this;
	  }
	  EGrower draw() {
	  	if (!end) backdraw();
	  	
	    // aging color
	    int ca = 255;
	    if (age > com().OLD_AGE.get() / 2) ca = PApplet.constrain(255 + (int)(com().OLD_AGE.get()/2) - (int)(age/1.2), 90, 255);
	    //if (!end && sprouts == 0) { stroke(255, 0, 0); strokeWeight(param.MAX_LINE_WIDTH+1 / cam_scale); } //BIG red head
	    if (!end && sprouts == 0) { 
	    		com().app.stroke(com().val_col_live.get()); 
	    		if (com().abs_width.get()) com().app.strokeWeight(com().MAX_LINE_WIDTH.get() / com().inter.cam.cam_scale.get());
	    		else com().app.strokeWeight(width);
	    } else if (end) { 
	      int res = com().app.color(com().val_col_leaf.getred() * ((float)(ca) / 255.0F), 
	                        com().val_col_leaf.getgreen() * ((float)(ca) / 255.0F), 
	                        com().val_col_leaf.getblue() * ((float)(ca) / 255.0F) );
	      if (com().abs_width.get()) {
		      com().app.stroke(res); 
		      com().app.strokeWeight((com().MAX_LINE_WIDTH.get()+1) / com().inter.cam.cam_scale.get());
	      } else {
	    	  	  com().app.fill(res); 
		      com().app.noStroke(); 
	      }
	    } else { 
	      int res = com().app.color(com().val_col_live.getred() * ((float)(ca) / 255.0F), 
	                        com().val_col_live.getgreen() * ((float)(ca) / 255.0F), 
	                        com().val_col_live.getblue() * ((float)(ca) / 255.0F) );

	  	  com().app.stroke(res); 
	      if (com().abs_width.get()) {
	    	  	  com().app.strokeWeight(( (float)com().MIN_LINE_WIDTH.get() + 
	    		  (float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0F ) / com().inter.cam.cam_scale.get() );
	      } else {
		      com().app.strokeWeight(width);
	      }
	    }              

	    PVector e = new PVector(dir.x, dir.y);
	    if (start < 1) e = e.setMag(e.mag() * start);
	    //e = e.add(pos);
	    //line(pos.x,pos.y,e.x,e.y);
	    com().app.pushMatrix();
	    com().app.translate(pos.x, pos.y);
	    if (end) {
	    	  com().app.scale(com().leaf_size_fact.get());
	    	  if (com().abs_width.get()) {
	    		  com().app.strokeWeight( (com().MAX_LINE_WIDTH.get()+1) / com().leaf_size_fact.get() /
	                    com().inter.cam.cam_scale.get());
	    	  } else {
	    		  com().app.strokeWeight( (com().MAX_LINE_WIDTH.get()+1) / com().leaf_size_fact.get() );
	    	  }
	      PVector e2 = new PVector(e.x, e.y);
	      if (flt_cnt < leaf_nb * 50) flt_cnt++;
	      else r_cnt += com().leaf_rot.get();
	      for (int i = 1 ; i < 1 + flt_cnt / 50 ; i++) {
	    	    com().app.pushMatrix();
	    	    com().app.rotate(-2*PConstants.PI * r_cnt / 500);
	    	    com().app.rotate(2*PConstants.PI * 50 * i / flt_cnt);
	    	    e.set(dir);
	    	    e2 = new PVector(e.x * (40+rang) / 200.0F, e.y * (40+rang) / 200.0F);
	    	    e.div(2);
	    	    e.rotate(-PConstants.PI/12.0F);
	    	    e.mult(rang / 100.0F);
	    	    if (side) {
	    	    		com().app.triangle(0F, 0F, e2.x, e2.y, e.x, e.y);
	    	    } else {
		    	    e.rotate(PConstants.PI/8.0F);
		    	    com().app.triangle(0F, 0F, e2.x, e2.y, e.x, e.y);
	    	    }
			com().app.popMatrix();
	      }
	    } 
	    else com().app.line(0, 0, e.x, e.y);
	    com().app.popMatrix();

	    //line(pos.x,pos.y,grows.x,grows.y);

	    //DEBUG
	    //fill(255); ellipseMode(CENTER);
	    //ellipse(pos.x, pos.y, 2, 2);
	    //strokeWeight(MAX_LINE_WIDTH+1 / cam_scale);
	    //point(grows.x,grows.y);
		  return this;
	  }
	  int flt_cnt = 0;
	  int r_cnt = 0;
	  boolean side = false;
	  int rang = 0;
	  EGrower backdraw() {
//		     aging color
	    int ca = 255;
	    if (age > com().OLD_AGE.get() / 2) ca = PApplet.constrain(255 + (int)(com().OLD_AGE.get()/2) - (int)(age/1.2), 90, 255);
	             
	    com().app.stroke(com().col_back.get()); 
//		    float bd = (float)com().MIN_LINE_WIDTH.get() * com().backfact.get();
//		    float bd = ( ( (float)com().MIN_LINE_WIDTH.get() + 
//		    		(float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0F )// / com.sim.inter.cam.cam_scale.get() 
//		    		) * com().backfact.get();
//		    com().app.strokeWeight(bd);
	    com().app.strokeWeight(width * com().backfact.get());
//		    com().app.strokeWeight( ( ( (float)com().MIN_LINE_WIDTH.get() + 
//		    		(float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0F )// / com.sim.inter.cam.cam_scale.get() 
//		    		) * com().backfact.get());

	    PVector e = new PVector(dir.x, dir.y);
	    PVector m = new PVector(dir.x, dir.y);
	    m.rotate(PConstants.PI / 2);
	    m.setMag(width/2F);
	    if (start < 1) e = e.setMag(e.mag() * start);
	    e = e.add(pos);
	    com().app.line(pos.x+m.x,pos.y+m.y,e.x+m.x,e.y+m.y);
	    com().app.line(pos.x-m.x,pos.y-m.y,e.x-m.x,e.y-m.y);
//		    com().app.pushMatrix();
//		    com().app.translate(pos.x, pos.y);
//		    com().app.line(0, 0, e.x, e.y);
//		    com().app.popMatrix();
	    return this;
	  }
	  EGrower clear() { return this; }
	  MGrowerGroup com() { return ((MGrowerGroup)com); }
	  
}







public class MGrowerGroup extends MGroup { 
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("grows", "GrowerGroup", "", "Work"); 
		first_start_show(m); }
		MGrowerGroup build(Macro_Sheet s, sValueBloc b) { 
			MGrowerGroup m = new MGrowerGroup(s, b); return m; }
	}
	
	public void comPanelBuild(nFrontPanel sim_front) {
	    nFrontTab tab = sim_front.addTab("Control");
	    tab.getShelf()
	      .addDrawerWatch(activeGrower, 10, 1)
	      .addDrawer(10.25, 0).getShelf()
	      .addSeparator(0.125)
	      .addDrawerActFactValue("floc", create_floc, floc_prob, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(srun_killg, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(TEEN_AGE, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerActFactValue("age", AGE_ON, OLD_AGE, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerActFactValue("grow", growP.ON, growP.DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerActFactValue("Sprout", sproutP.ON, sproutP.DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerActFactValue("leaf", leafP.ON, leafP.DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerActFactValue("stop", stopP.ON, stopP.DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerActFactValue("die", dieP.ON, dieP.DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      ;
	    sim_front.addTab("Comport").getShelf()
	      .addDrawerFactValue(DEVIATION, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(L_MIN, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(L_MAX, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(L_DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(cible_force, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(cible_zone, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(cible_linew, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(zone_view, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(leaf_rot, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(leaf_nb, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(growing_width, abs_width, 10, 1)
	      .addSeparator(0.125)
	      ;
    	  sim_front.toLayerTop();
    }
	

	  sFlt DEVIATION; //drifting (rotation posible en portion de pi (PI/drift))
	  sFlt L_MIN; //longeur minimum de chaque section
	  sFlt L_MAX; //longeur max de chaque section MODIFIABLE PAR MENU MOVE minimum 1 , limité dans l'update de sont bp
	  sFlt L_DIFFICULTY;
	  sFlt OLD_AGE, TEEN_AGE;
	  //int TEEN_AGE = OLD_AGE / 20;

	  RandomTryParam growP;
	  RandomTryParam sproutP;
	  RandomTryParam stopP;
	  RandomTryParam leafP;
	  RandomTryParam dieP;
	  sFlt MAX_LINE_WIDTH; //epaisseur max des ligne, diminuer par l'age, un peut, se vois pas
	  sFlt MIN_LINE_WIDTH; //epaisseur min des ligne
	  sFlt leaf_size_fact; 
	  sFlt floc_prob;
	  
	  sFlt cible_force, cible_zone, backfact, cible_linew;
	  
	  sBoo create_floc, AGE_ON, zone_view; //use_organ, 
	  sInt activeGrower;
	  sRun srun_killg;

	  sCol val_col_live, val_col_leaf, col_back;
	  
//	  nCursor obj_cur;
	  
	  sInt leaf_nb;
	  sFlt leaf_rot;
	  
	  sBoo growing_width, abs_width;
	
	MGrowerGroup(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "grows", _bloc); 
		if (!loading_from_bloc) max_entity.set(1000);
	    DEVIATION = newFlt(6, "dev", "dev");
	    L_MIN = newFlt(5F, "lmin", "lmin");
	    L_MAX = newFlt(20, "lmax", "lmax");
	    L_DIFFICULTY = newFlt(1, "ldif", "ldif");
	    TEEN_AGE = newFlt(6.25F, "young", "young");
	    OLD_AGE = newFlt(200, "age", "age");
	    floc_prob = newFlt(1, "floc_prob", "floc_prob");
	    cible_force = newFlt(0.01F, "cible_force", "cible");
	    cible_linew = newFlt(3.0F, "cible_linew", "cible_linew");
	    backfact = newFlt(1.2F, "backfact", "backfact");
	    leaf_nb = newInt(6, "leaf_nb", "leaf_nb");
	    leaf_rot = newFlt(1, "leaf_rot", "leaf_rot");

	    cible_zone = newFlt(1000F, "cible_zone", "zone");
	    zone_view = newBoo(false, "zone_view", "zone_view");
	    
	    growP = new RandomTryParam(this, 0.2F, true, "grow");
	    sproutP = new RandomTryParam(this, 3000, true, "sprout");
	    stopP = new RandomTryParam(this, 2, true, "stop");
	    leafP = new RandomTryParam(this, 5000, true, "leaf");
	    dieP = new RandomTryParam(this, 40, true, "die");

	    create_floc = newBoo(true, "create_floc", "create floc");
//	    use_organ = newBoo(true, "use_organ", "use_organ");
	    AGE_ON = newBoo(true, "AGE_ON", "AGE_ON");
	    activeGrower = newInt(0, "active_grower", "growers nb");
	    
	    val_col_live = menuColor(gui.app.color(220), "val_col_live");
	    val_col_leaf = menuColor(gui.app.color(0, 220, 0), "val_col_leaf");
	    col_back = menuColor(gui.app.color(0, 220, 0), "col_back");
	    
	    MAX_LINE_WIDTH = menuFltSlide(25F, 1, 300, "max_line_width");
	    MIN_LINE_WIDTH = menuFltSlide(3F, 1, 50, "min_line_width");
	    leaf_size_fact = menuFltSlide(1, 1, 8, "leaf_size_fact");
	    backfact = menuFltSlide(1, 0.1F, 8, "backfact");
	    growing_width = newBoo(true, "growing_width");
	    abs_width = newBoo(false, "abs_width");
	    
	    srun_killg = newRun("kill_grower", "kill", new nRunnable() { public void run() { 
	        for (Entity e : entity_list) {
	          EGrower g = (EGrower)e;
	          if (!g.end && g.sprouts == 0) { 
	        	  	for (Macro_Connexion c : link_out.connected_inputs) {
		        	  	MFlocGroup fcom = null;
		    			if (c.elem.bloc.val_type.get().equals("flocs")) {
		    				fcom = (MFlocGroup)c.elem.bloc;
		    			} 
		            if (create_floc.get() && fcom != null && 
		                gui.app.crandom(floc_prob.get()) > 0.9) {
		              EFloc f = fcom.newEntity();
		              if (f != null) {
		                f.pos.x = g.pos.x;
		                f.pos.y = g.pos.y;
		              }
		            }
	        	  	}
	            g.end = true;
	          }
	        }
	    } } );
	    
//	    obj_cur = menuCursor(""+value_bloc.ref+"_objectif", false);
	}

	  public void init() {
		  super.init();
		  init_access();
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
		    activeGrower.set(grower_Nb());
	  }
	  void custom_post_tick() {
	  }
	  void custom_frame() {
	  }
	  void custom_cam_draw_post_entity() {
	  }
	  void custom_cam_draw_pre_entity() {
		  if (zone_view.get() && show_entity.get()) {
			  gui.app.stroke(200);
			  gui.app.strokeWeight(cible_linew.get());
			  gui.app.noFill();
//			  gui.app.ellipse(obj_cur.x() - cible_zone.get(), obj_cur.y() - cible_zone.get(), 
//					  cible_zone.get()*2, cible_zone.get()*2);
		  }
	  }

	  int grower_Nb() {
	    int n = 0;
	    for (Entity e : entity_list) 
	    		if (e.active && !((EGrower)e).end && ((EGrower)e).sprouts == 0) n++;
	    return n;
	  }
	  EGrower build() { return new EGrower(this); }

	  boolean first_reset = true;
	  EGrower addEntity() { 
		    EGrower ng = newEntity();
		    if (ng != null) {
		    		if (ref_cursor.hasDir()) 
		    			ng.define(ref_cursor.pos(), ref_cursor.dir(), 0);
		    		else ng.define(ref_cursor.pos(), new PVector(0,0), 0);
		    }
//		    int cost = 5;
		    //if (ng != null && sim.organ != null) {
		      //if (sim.organ.active_entity.get() > cost - 1 && 
		      //    sim.organ.active_entity.get() <= sim.organ.list.size()) {
		      //  for (int i = 0 ; i < cost ; i ++) {
		      //    sim.organ.list.get(sim.organ.active_entity.get() - 1).destroy();
		      //    sim.organ.active_entity.add(-1);
		      //  }
		      //}
		      //if (first_reset && sim.organ.active_entity.get() <= cost + 1) sim.reset();
		      //if (!first_reset && sim.organ.active_entity.get() <= cost + 1) { mmain().no_save.set(true); sim.resetRng(); }
		      first_reset = false;
		    //}
		    return ng;}
	  EGrower newEntity() {
	    for (Entity e : entity_list) 
	    		if (!e.active) { e.activate(); return (EGrower)e; } return null; }
	  
	  class RandomTryParam {// extends Callable
		  sFlt DIFFICULTY;
		  sBoo ON;
		  //sFlt test_by_tick;
		  int count = 0;
		  RandomTryParam(Macro_Bloc bloc, float d, boolean b, String n) { 
		    DIFFICULTY = bloc.newFlt(d, n+"_dif", "dif");
		    ON = bloc.newBoo(b, n+"_on", "on");
		    //test_by_tick = new sFlt(sbloc, 0);
		    //DIFFICULTY.set(d); 
		    //ON.set(b); 
		    //addChannel(frameend_chan);
		  }
		  boolean test(Rapp app) { 
		    if (ON.get()) count++; 
		    //test_by_tick.set(count / sim.tick_by_frame.get()); 
		    return ON.get() && app.crandom(DIFFICULTY.get()) > 0.5;
		  }
		  //void answer(Channel chan, float v) { count = 0; test_by_tick.set(0); }
	  }
}



