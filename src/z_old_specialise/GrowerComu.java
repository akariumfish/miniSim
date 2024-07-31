package z_old_specialise;

import Macro.*;
import RApplet.RConst;
import RApplet.Rapp;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;




public class GrowerComu extends Community {
	

public static class GrowerPrint extends Sheet_Specialize {
	  Simulation sim;
	  public GrowerPrint(Simulation s) { super("Grower"); sim = s; }
	  public void default_build() { }
	  public GrowerComu get_new(Macro_Sheet s, String n, sValueBloc b) { 
		  return new GrowerComu(sim, n, b); }
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
	  
	  sFlt cible_force, cible_zone, backfact;
	  
	  sBoo create_floc, AGE_ON, zone_view; //use_organ, 
	  sInt activeGrower;
	  sRun srun_killg;

	  sCol val_col_live, val_col_leaf, col_back;
	  
	  FlocComu fcom;
	  sObj floc_obj;
	  
	  nCursor obj_cur;

	  void comPanelBuild(nFrontPanel sim_front) {
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
	      .addDrawerFactValue(DEVIATION, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(L_MIN, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(L_MAX, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(L_DIFFICULTY, 2, 10, 1)
	      .addSeparator(0.125)
	      ;
	    sim_front.getTab(1).getShelf()
	      .addDrawerFactValue(cible_force, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(cible_zone, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(zone_view, 10, 1)
	      .addSeparator(0.125)
	      ;
	    sim_front.toLayerTop();
	  }
	  
	  void selected_comu(Community c) { 
	    //logln(value_bloc.ref + " got com " + c.name + " " + c.type_value.get());
	    if (c != null && c.type_value.get().equals("floc")) { fcom = (FlocComu)c; }
	  }

	  GrowerComu(Simulation _c, String n, sValueBloc t) { 
	    super(_c, n, "grow", 1000, t);
	    DEVIATION = newFlt(6, "dev", "dev");
	    L_MIN = newFlt(2.5F, "lmin", "lmin");
	    L_MAX = newFlt(40, "lmax", "lmax");
	    L_DIFFICULTY = newFlt(1, "ldif", "ldif");
	    TEEN_AGE = newFlt(50, "young", "young");
	    OLD_AGE = newFlt(100, "age", "age");
	    floc_prob = newFlt(1, "floc_prob", "floc_prob");
	    cible_force = newFlt(0.01F, "cible_force", "cible");
	    backfact = newFlt(1.2F, "backfact", "backfact");

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
	    
	    MAX_LINE_WIDTH = menuFltSlide(1.5F, 0.01F, 8, "max_line_width");
	    MIN_LINE_WIDTH = menuFltSlide(0.2F, 0.01F, 8, "min_line_width");
	    leaf_size_fact = menuFltSlide(1, 1, 8, "leaf_size_fact");
	    backfact = menuFltSlide(1, 1, 8, "backfact");
	    
	    srun_killg = newRun("kill_grower", "kill", new nRunnable() { public void run() { 
	        for (Entity e : list) {
	          Grower g = (Grower)e;
	          if (!g.end && g.sprouts == 0) { 
	            if (create_floc.get() && fcom != null && 
	                gui.app.crandom(floc_prob.get()) > 0.9) {
	              Floc f = fcom.newEntity();
	              if (f != null) {
	                f.pos.x = g.pos.x;
	                f.pos.y = g.pos.y;
	              }
	            }
	            g.end = true;
	          }
	        }
	    } } );


	    floc_obj = newObj("floc_obj", "floc_obj");
	    floc_obj.addEventChange(new nRunnable() { public void run() {
	      if (floc_obj.isSheet()) {
	        Macro_Sheet ms = floc_obj.asSheet();
	        if (ms.specialize.get().equals("Floc")) fcom = (FlocComu)ms;
	      }
	    }});
	    

	    obj_cur = menuCursor(""+n+"_objectif", false);
	    
	  }
	  void custom_cam_draw_pre_entity() {
//		  for (Entity g : list) if (g.active) ((Grower)g).backdraw();
	  }
	  void custom_cam_draw_post_entity() {
		  if (zone_view.get()) {
			  gui.app.stroke(220);
			  gui.app.strokeWeight(1);
			  gui.app.noFill();
			  gui.app.ellipse(obj_cur.x() - cible_zone.get(), obj_cur.y() - cible_zone.get(), 
					  cible_zone.get()*2, cible_zone.get()*2);
		  }
	  }
	  void custom_pre_tick() {
	    activeGrower.set(grower_Nb());
	  }
	  void custom_post_tick() {
	  }

	  Grower build() { 
	    return new Grower(this);
	  }
	  boolean first_reset = true;
	  Grower addEntity() {
	    Grower ng = newEntity();
	    if (ng != null) {
	    		if (adding_cursor.hasDir()) ng.define(adding_cursor.pos(), adding_cursor.dir());
	    		else ng.define(adding_cursor.pos(), new PVector(0,0));
	    }
//	    int cost = 5;
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
	    return ng;
	  }
	  Grower newEntity() {
	    Grower ng = null;
	    for (Entity e : list) 
	      if (!e.active && ng == null) { 
	        ng = (Grower)e; 
	        e.activate();
	      }
	    return ng;
	  }
	  void custom_frame() {
	    //graph.update(activeEntity.get(), activeGrower.get());
	  }
	  void custom_screen_draw() {
	    //graph.draw();
	  }
	  int grower_Nb() {
	    int n = 0;
	    for (Entity e : list) if (e.active && !((Grower)e).end && ((Grower)e).sprouts == 0) n++;
	    return n;
	  }
	}





	class RandomTryParam {// extends Callable
	  sFlt DIFFICULTY;
	  sBoo ON;
	  //sFlt test_by_tick;
	  int count = 0;
	  RandomTryParam(Macro_Sheet sheet, float d, boolean b, String n) { 
	    DIFFICULTY = sheet.newFlt(d, n+"_dif", "dif");
	    ON = sheet.newBoo(b, n+"_on", "on");
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






	class Grower extends Entity {

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

	  Grower(GrowerComu c) { 
	    super(c);
	  }

	  Grower init() {
	    end = false;
	    sprouts = 0;
	    age = 0;
	    start = 0.0F;
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
	  Grower define(PVector _p, PVector _d) {
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

	    PVector l = new PVector(com().obj_cur.pos().x, com().obj_cur.pos().y);
	    l.add(-pos.x, -pos.y);
	    if (l.mag()>com().cible_zone.get())
	    		grows.set(headTo(pos, grows, com().obj_cur.pos(), com().cible_force.get()));
	    
	    dir = new PVector();
	    dir = grows;
	    grows = PVector.add(pos, grows);
	    return this;
	  }
	  Grower frame() { 
	    return this;
	  }
	  Grower tick() {
	    if (com().AGE_ON.get() || age <= com().TEEN_AGE.get()) age++;
	    if (age < com().TEEN_AGE.get()) {
	      start = (float)age / (float)com().TEEN_AGE.get();
	    } else start = 1;

	    //grow
	    if (start == 1 && !end && sprouts == 0 && com().growP.test(com().app)) {
	      Grower n = com().newEntity();
	      if (n != null) {
	        n.define(grows, dir);
	        sprouts++;
	      }
	    }

	    // sprout
	    if (start == 1 && !end && com().sproutP.test(com().app)) {
	      Grower n = com().newEntity();
	      if (n != null) {
	        PVector _p = new PVector(0, 0);
	        PVector _d = new PVector(0, 0);
	        _d.add(grows).sub(pos);
	        _d.setMag(com().app.random(1.0F) * _d.mag());
	        _p.add(pos).add(_d);
	        n.define(_p, _d);
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
	      _p.add(pos).add(_d);
	      Grower n = com().newEntity();
	      if (n != null) {
	        n.define(_p, _d);
	        n.end = true;
	        sprouts++;
	      }
	    }

	    // stop growing
	    if (start == 1 && !end && sprouts == 0 && com().stopP.test(com().app)) {
	      if (com().create_floc.get() && com().fcom != null && 
	    		  com().app.crandom(com().floc_prob.get()) > 0.5F) {
	        Floc f = com().fcom.newEntity();
	        if (f != null) {
	          f.pos.x = pos.x;
	          f.pos.y = pos.y;
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
	  Grower draw() {
		  //backdraw();
		    // aging color
		    int ca = 255;
		    if (age > com().OLD_AGE.get() / 2) ca = PApplet.constrain(255 + (int)(com().OLD_AGE.get()/2) - (int)(age/1.2), 90, 255);
		    //if (!end && sprouts == 0) { stroke(255, 0, 0); strokeWeight(param.MAX_LINE_WIDTH+1 / cam_scale); } //BIG red head
		    if (!end && sprouts == 0) { 
		    	com().app.stroke(com().val_col_live.get()); 
		    	com().app.strokeWeight((com().MAX_LINE_WIDTH.get()+1));// / com.sim.inter.cam.cam_scale.get());
		    } else if (end) { 
		      int res = com().app.color(com().val_col_leaf.getred() * ((float)(ca) / 255.0F), 
		                        com().val_col_leaf.getgreen() * ((float)(ca) / 255.0F), 
		                        com().val_col_leaf.getblue() * ((float)(ca) / 255.0F) );
		      com().app.stroke(res); 
		      com().app.strokeWeight((com().MAX_LINE_WIDTH.get()+1));// / com.sim.inter.cam.cam_scale.get());
		    } else { 
		      int res = com().app.color(com().val_col_live.getred() * ((float)(ca) / 255.0F), 
		                        com().val_col_live.getgreen() * ((float)(ca) / 255.0F), 
		                        com().val_col_live.getblue() * ((float)(ca) / 255.0F) );
		      com().app.stroke(res); 
		      com().app.strokeWeight( ( (float)com().MIN_LINE_WIDTH.get() + (float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0F ));// / com.sim.inter.cam.cam_scale.get() );
		    }              

		    PVector e = new PVector(dir.x, dir.y);
		    if (start < 1) e = e.setMag(e.mag() * start);
		    //e = e.add(pos);
		    //line(pos.x,pos.y,e.x,e.y);
		    com().app.pushMatrix();
		    com().app.translate(pos.x, pos.y);
		    if (end) {
		    	com().app.scale(com().leaf_size_fact.get());
		    	com().app.strokeWeight( (com().MAX_LINE_WIDTH.get()+1) );//* com().leaf_size_fact.get() );// / 
		                    //(com.sim.inter.cam.cam_scale.get());
		      PVector e2 = new PVector(e.x, e.y);
		      e.div(2);
		      e.rotate(-PConstants.PI/16);
		      com().app.line(0, 0, e.x, e.y);
		      com().app.line(e2.x, e2.y, e.x, e.y);
		      e.rotate(PConstants.PI/8);
		      com().app.line(0, 0, e.x, e.y);
		      com().app.line(e2.x, e2.y, e.x, e.y);
		    } else com().app.line(0, 0, e.x, e.y);
		    com().app.popMatrix();

		    //line(pos.x,pos.y,grows.x,grows.y);

		    //DEBUG
		    //fill(255); ellipseMode(CENTER);
		    //ellipse(pos.x, pos.y, 2, 2);
		    //strokeWeight(MAX_LINE_WIDTH+1 / cam_scale);
		    //point(grows.x,grows.y);
		    return this;
		  }
	  Grower backdraw() {
//		     aging color
		    int ca = 255;
		    if (age > com().OLD_AGE.get() / 2) ca = PApplet.constrain(255 + (int)(com().OLD_AGE.get()/2) - (int)(age/1.2), 90, 255);
		    //if (!end && sprouts == 0) { stroke(255, 0, 0); strokeWeight(param.MAX_LINE_WIDTH+1 / cam_scale); } //BIG red head
//		    if (!end && sprouts == 0) { 
//		    	com().app.stroke(com().val_col_live.get()); 
//		    	com().app.strokeWeight((com().MAX_LINE_WIDTH.get()+1) / com.sim.inter.cam.cam_scale.get());
//		    } else if (end) { 
//		      int res = com().app.color(com().val_col_leaf.getred() * ((float)(ca) / 255.0F), 
//		                        com().val_col_leaf.getgreen() * ((float)(ca) / 255.0F), 
//		                        com().val_col_leaf.getblue() * ((float)(ca) / 255.0F) );
//		      com().app.stroke(res); 
//		      com().app.strokeWeight((com().MAX_LINE_WIDTH.get()+1) / com.sim.inter.cam.cam_scale.get());
//		    } else { 
//		      int res = com().app.color(com().val_col_live.getred() * ((float)(ca) / 255.0F), 
//		                        com().val_col_live.getgreen() * ((float)(ca) / 255.0F), 
//		                        com().val_col_live.getblue() * ((float)(ca) / 255.0F) );
//		      com().app.stroke(res); 
//		      com().app.strokeWeight( ( (float)com().MIN_LINE_WIDTH.get() + (float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0F ) / com.sim.inter.cam.cam_scale.get() );
//		    }              
		    com().app.stroke(com().col_back.get()); 
		    com().app.strokeWeight( ( ( (float)com().MIN_LINE_WIDTH.get() + 
		    		(float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0F )// / com.sim.inter.cam.cam_scale.get() 
		    		) * com().backfact.get());
		    
		    PVector e = new PVector(dir.x, dir.y);
		    if (start < 1) e = e.setMag(e.mag() * start);
		    //e = e.add(pos);
		    //line(pos.x,pos.y,e.x,e.y);
		    com().app.pushMatrix();
		    com().app.translate(pos.x, pos.y);
//		    if (end) {
//		    	com().app.scale(com().leaf_size_fact.get());
//		    	com().app.strokeWeight( (com().MAX_LINE_WIDTH.get()+1) / 
//		                    (com.sim.inter.cam.cam_scale.get() * com().leaf_size_fact.get()) );
//		      PVector e2 = new PVector(e.x, e.y);
//		      e.div(2);
//		      e.rotate(-PConstants.PI/16);
//		      com().app.line(0, 0, e.x, e.y);
//		      com().app.line(e2.x, e2.y, e.x, e.y);
//		      e.rotate(PConstants.PI/8);
//		      com().app.line(0, 0, e.x, e.y);
//		      com().app.line(e2.x, e2.y, e.x, e.y);
//		    } else 
		    com().app.line(0, 0, e.x, e.y);
		    com().app.popMatrix();

		    //line(pos.x,pos.y,grows.x,grows.y);

		    //DEBUG
		    //fill(255); ellipseMode(CENTER);
		    //ellipse(pos.x, pos.y, 2, 2);
		    //strokeWeight(MAX_LINE_WIDTH+1 / cam_scale);
		    //point(grows.x,grows.y);
		    return this;
		  }
	  Grower clear() { 
	    return this;
	  }
	  GrowerComu com() { 
	    return ((GrowerComu)com);
	  }
	}



   
