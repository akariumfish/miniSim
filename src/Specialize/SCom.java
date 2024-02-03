//
//
//
//
//
//
//class GrowerPrint extends Sheet_Specialize {
//  Simulation sim;
//  GrowerPrint(Simulation s) { super("Grower"); sim = s; }
//  GrowerComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new GrowerComu(sim, n, b); }
//}
//
//
//class GrowerComu extends Community {
//
//  sFlt DEVIATION; //drifting (rotation posible en portion de pi (PI/drift))
//  sFlt L_MIN; //longeur minimum de chaque section
//  sFlt L_MAX; //longeur max de chaque section MODIFIABLE PAR MENU MOVE minimum 1 , limité dans l'update de sont bp
//  sFlt L_DIFFICULTY;
//  sFlt OLD_AGE, TEEN_AGE;
//  //int TEEN_AGE = OLD_AGE / 20;
//
//  RandomTryParam growP;
//  RandomTryParam sproutP;
//  RandomTryParam stopP;
//  RandomTryParam leafP;
//  RandomTryParam dieP;
//  sFlt MAX_LINE_WIDTH; //epaisseur max des ligne, diminuer par l'age, un peut, se vois pas
//  sFlt MIN_LINE_WIDTH; //epaisseur min des ligne
//  sFlt leaf_size_fact; 
//  sFlt floc_prob;
//  
//  sBoo create_floc, use_organ, AGE_ON;
//  sInt activeGrower;
//  sRun srun_killg;
//
//  sCol val_col_live, val_col_leaf;
//  
//  FlocComu fcom;
//
//  void comPanelBuild(nFrontPanel sim_front) {
//    nFrontTab tab = sim_front.addTab(name);
//    tab.getShelf()
//      .addDrawerWatch(activeGrower, 10, 1)
//      .addDrawer(10.25, 0).getShelf()
//      .addSeparator(0.125)
//      .addDrawerActFactValue("floc", create_floc, floc_prob, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerButton(srun_killg, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(TEEN_AGE, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("age", AGE_ON, OLD_AGE, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("grow", growP.ON, growP.DIFFICULTY, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("Sprout", sproutP.ON, sproutP.DIFFICULTY, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("leaf", leafP.ON, leafP.DIFFICULTY, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("stop", stopP.ON, stopP.DIFFICULTY, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("die", dieP.ON, dieP.DIFFICULTY, 2, 10, 1)
//      .addSeparator(0.125)
//      ;
//    sim_front.getTab(2).getShelf()
//      .addDrawerFactValue(DEVIATION, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(L_MIN, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(L_MAX, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(L_DIFFICULTY, 2, 10, 1)
//      .addSeparator(0.125)
//      ;
//    sim_front.toLayerTop();
//  }
//  
//  void selected_comu(Community c) { 
//    //logln(value_bloc.ref + " got com " + c.name + " " + c.type_value.get());
//    if (c != null && c.type_value.get().equals("floc")) { fcom = (FlocComu)c; }
//  }
//
//  GrowerComu(Simulation _c, String n, sValueBloc t) { 
//    super(_c, n, "grow", 1000, t);
//    DEVIATION = newFlt(6, "dev", "dev");
//    L_MIN = newFlt(2.5, "lmin", "lmin");
//    L_MAX = newFlt(40, "lmax", "lmax");
//    L_DIFFICULTY = newFlt(1, "ldif", "ldif");
//    TEEN_AGE = newFlt(50, "young", "young");
//    OLD_AGE = newFlt(100, "age", "age");
//    floc_prob = newFlt(1, "floc_prob", "floc_prob");
//
//    growP = new RandomTryParam(this, 0.2, true, "grow");
//    sproutP = new RandomTryParam(this, 3000, true, "sprout");
//    stopP = new RandomTryParam(this, 2, true, "stop");
//    leafP = new RandomTryParam(this, 5000, true, "leaf");
//    dieP = new RandomTryParam(this, 40, true, "die");
//
//    create_floc = newBoo(true, "create_floc", "create floc");
//    use_organ = newBoo(true, "use_organ", "use_organ");
//    AGE_ON = newBoo(true, "AGE_ON", "AGE_ON");
//    activeGrower = newInt(0, "active_grower", "growers nb");
//    
//    val_col_live = menuColor(color(220), "val_col_live");
//    val_col_leaf = menuColor(color(0, 220, 0), "val_col_leaf");
//    
//    MAX_LINE_WIDTH = menuFltSlide(1.5, 0.1, 3, "max_line_width");
//    MIN_LINE_WIDTH = menuFltSlide(0.2, 0.1, 3, "min_line_width");
//    leaf_size_fact = menuFltSlide(1, 1, 4, "leaf_size_fact");
//    
//    srun_killg = newRun("kill_grower", "kill", new Runnable(list) { 
//      public void run() { 
//        for (Entity e : ((ArrayList<Entity>)builder)) {
//          Grower g = (Grower)e;
//          if (!g.end && g.sprouts == 0) { 
//            if (create_floc.get() && fcom != null && 
//                crandom(floc_prob.get()) > 0.9) {
//              Floc f = fcom.newEntity();
//              if (f != null) {
//                f.pos.x = g.pos.x;
//                f.pos.y = g.pos.y;
//              }
//            }
//            g.end = true;
//          }
//        }
//      }
//    }
//    );
//
//    //graph.init();
//  }
//  void custom_cam_draw_pre_entity() {
//  }
//  void custom_cam_draw_post_entity() {
//  }
//  void custom_pre_tick() {
//    activeGrower.set(grower_Nb());
//  }
//  void custom_post_tick() {
//  }
//
//  Grower build() { 
//    return new Grower(this);
//  }
//  boolean first_reset = true;
//  Grower addEntity() {
//    Grower ng = newEntity();
//    if (ng != null) ng.define(adding_cursor.pos(), adding_cursor.dir());
//    int cost = 5;
//    //if (ng != null && sim.organ != null) {
//      //if (sim.organ.active_entity.get() > cost - 1 && 
//      //    sim.organ.active_entity.get() <= sim.organ.list.size()) {
//      //  for (int i = 0 ; i < cost ; i ++) {
//      //    sim.organ.list.get(sim.organ.active_entity.get() - 1).destroy();
//      //    sim.organ.active_entity.add(-1);
//      //  }
//      //}
//      //if (first_reset && sim.organ.active_entity.get() <= cost + 1) sim.reset();
//      //if (!first_reset && sim.organ.active_entity.get() <= cost + 1) { mmain().no_save.set(true); sim.resetRng(); }
//      first_reset = false;
//    //}
//    return ng;
//  }
//  Grower newEntity() {
//    Grower ng = null;
//    for (Entity e : list) 
//      if (!e.active && ng == null) { 
//        ng = (Grower)e; 
//        e.activate();
//      }
//    return ng;
//  }
//  void custom_frame() {
//    //graph.update(activeEntity.get(), activeGrower.get());
//  }
//  void custom_screen_draw() {
//    //graph.draw();
//  }
//  int grower_Nb() {
//    int n = 0;
//    for (Entity e : list) if (e.active && !((Grower)e).end && ((Grower)e).sprouts == 0) n++;
//    return n;
//  }
//}
//
//
//
//
//
//class RandomTryParam {// extends Callable
//  sFlt DIFFICULTY;
//  sBoo ON;
//  //sFlt test_by_tick;
//  int count = 0;
//  RandomTryParam(Macro_Sheet sheet, float d, boolean b, String n) { 
//    DIFFICULTY = sheet.newFlt(d, n+"_dif", "dif");
//    ON = sheet.newBoo(b, n+"_on", "on");
//    //test_by_tick = new sFlt(sbloc, 0);
//    //DIFFICULTY.set(d); 
//    //ON.set(b); 
//    //addChannel(frameend_chan);
//  }
//  boolean test() { 
//    if (ON.get()) count++; 
//    //test_by_tick.set(count / sim.tick_by_frame.get()); 
//    return ON.get() && crandom(DIFFICULTY.get()) > 0.5;
//  }
//  //void answer(Channel chan, float v) { count = 0; test_by_tick.set(0); }
//}
//
//
//
//
//
//
//class Grower extends Entity {
//
//  PVector pos = new PVector();
//  PVector grows = new PVector();
//  PVector dir = new PVector();
//
//  float halo_size = 10;
//  float halo_density = 0.2;
//
//  // condition de croissance
//  boolean end = false;
//  int sprouts = 0;
//  float age = 0.0;
//  float start = 0.0;
//
//  Grower(GrowerComu c) { 
//    super(c);
//  }
//
//  Grower init() {
//    end = false;
//    sprouts = 0;
//    age = 0;
//    start = 0.0;
//    return this;
//  }
//  Grower define(PVector _p, PVector _d) {
//    pos = _p;
//    grows = new PVector(com().L_MIN.get() + crandom(com().L_DIFFICULTY.get())*(com().L_MAX.get() - com().L_MIN.get()), 0);
//    grows.rotate(_d.heading());
//    grows.rotate(random(PI / com().DEVIATION.get()) - ((PI / com().DEVIATION.get()) / 2));
//    dir = new PVector();
//    dir = grows;
//    grows = PVector.add(pos, grows);
//    return this;
//  }
//  Grower frame() { 
//    return this;
//  }
//  Grower tick() {
//    if (com().AGE_ON.get() || age <= com().TEEN_AGE.get()) age++;
//    if (age < com().TEEN_AGE.get()) {
//      start = (float)age / (float)com().TEEN_AGE.get();
//    } else start = 1;
//
//    //grow
//    if (start == 1 && !end && sprouts == 0 && com().growP.test()) {
//      Grower n = com().newEntity();
//      if (n != null) {
//        n.define(grows, dir);
//        sprouts++;
//      }
//    }
//
//    // sprout
//    if (start == 1 && !end && com().sproutP.test()) {
//      Grower n = com().newEntity();
//      if (n != null) {
//        PVector _p = new PVector(0, 0);
//        PVector _d = new PVector(0, 0);
//        _d.add(grows).sub(pos);
//        _d.setMag(random(1.0) * _d.mag());
//        _p.add(pos).add(_d);
//        n.define(_p, _d);
//        sprouts++;
//      }
//      //sprouts = (int[]) expand(sprouts, sprouts.length + 1);
//      //sprouts[sprouts.length - 1] = temp_b.id;
//      //temp_b.this_sprout_index = sprouts.length - 1;
//      //sprouts_nb++;
//    }
//
//    // leaf
//    if (start == 1 && !end && age < com().OLD_AGE.get() / 2 && com().leafP.test()) {
//      PVector _p = new PVector(0, 0);
//      PVector _d = new PVector(0, 0);
//      _d.add(grows).sub(pos);
//      _d.setMag(random(1.0) * _d.mag());
//      _p.add(pos).add(_d);
//      Grower n = com().newEntity();
//      if (n != null) {
//        n.define(_p, _d);
//        n.end = true;
//        sprouts++;
//      }
//    }
//
//    // stop growing
//    if (start == 1 && !end && sprouts == 0 && com().stopP.test()) {
//      if (com().create_floc.get() && com().fcom != null && 
//          crandom(com().floc_prob.get()) > 0.5) {
//        Floc f = com().fcom.newEntity();
//        if (f != null) {
//          f.pos.x = pos.x;
//          f.pos.y = pos.y;
//        }
//      }
//      end = true;
//    }
//
//    // die
//    float rng = crandom(com().dieP.DIFFICULTY.get());
//    if (com().dieP.ON.get() && start == 1 && !(!end && sprouts == 0) &&
//      (rng > ( (float)com().OLD_AGE.get() / (float)age ) //||
//      //rng / DIE_DIFFICULTY_DIVIDER > ((float)MAX_LIST_SIZE - (float)baseNb()) / (float)MAX_LIST_SIZE
//      )) {
//      this.destroy();
//    }
//    return this;
//  }
//  Grower draw() {
//    // aging color
//    int ca = 255;
//    if (age > com().OLD_AGE.get() / 2) ca = (int)constrain(255 + int(com().OLD_AGE.get()/2) - int(age/1.2), 90, 255);
//    //if (!end && sprouts == 0) { stroke(255, 0, 0); strokeWeight(param.MAX_LINE_WIDTH+1 / cam_scale); } //BIG red head
//    if (!end && sprouts == 0) { 
//      stroke(com().val_col_live.get()); 
//      strokeWeight((com().MAX_LINE_WIDTH.get()+1) / com.sim.inter.cam.cam_scale.get());
//    } else if (end) { 
//      color res = color(com().val_col_leaf.getred() * (float(ca) / 255.0), 
//                        com().val_col_leaf.getgreen() * (float(ca) / 255.0), 
//                        com().val_col_leaf.getblue() * (float(ca) / 255.0) );
//      stroke(res); 
//      strokeWeight((com().MAX_LINE_WIDTH.get()+1) / com.sim.inter.cam.cam_scale.get());
//    } else { 
//      color res = color(com().val_col_live.getred() * (float(ca) / 255.0), 
//                        com().val_col_live.getgreen() * (float(ca) / 255.0), 
//                        com().val_col_live.getblue() * (float(ca) / 255.0) );
//      stroke(res); 
//      strokeWeight(((float)com().MIN_LINE_WIDTH.get() + ((float)com().MAX_LINE_WIDTH.get() * (float)ca / 255.0)) / com.sim.inter.cam.cam_scale.get());
//    }              
//
//    PVector e = new PVector(dir.x, dir.y);
//    if (start < 1) e = e.setMag(e.mag() * start);
//    //e = e.add(pos);
//    //line(pos.x,pos.y,e.x,e.y);
//    pushMatrix();
//    translate(pos.x, pos.y);
//    if (end) {
//      scale(com().leaf_size_fact.get());
//      strokeWeight( (com().MAX_LINE_WIDTH.get()+1) / 
//                    (com.sim.inter.cam.cam_scale.get() * com().leaf_size_fact.get()) );
//      PVector e2 = new PVector(e.x, e.y);
//      e.div(2);
//      e.rotate(-PI/16);
//      line(0, 0, e.x, e.y);
//      line(e2.x, e2.y, e.x, e.y);
//      e.rotate(PI/8);
//      line(0, 0, e.x, e.y);
//      line(e2.x, e2.y, e.x, e.y);
//    } else line(0, 0, e.x, e.y);
//    popMatrix();
//
//    //line(pos.x,pos.y,grows.x,grows.y);
//
//    //DEBUG
//    //fill(255); ellipseMode(CENTER);
//    //ellipse(pos.x, pos.y, 2, 2);
//    //strokeWeight(MAX_LINE_WIDTH+1 / cam_scale);
//    //point(grows.x,grows.y);
//    return this;
//  }
//  Grower clear() { 
//    return this;
//  }
//  GrowerComu com() { 
//    return ((GrowerComu)com);
//  }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//         
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//class Floc extends Entity {
//  PVector pos = new PVector(0, 0);
//  PVector mov = new PVector(0, 0);
//  float speed = 0;
//  
//  float halo_size = 0;
//  float halo_density = 0;
//  
//  int age = 0;
//  int max_age = 2000;
//  
//  Floc(FlocComu c) { super(c); }
//  
//  void draw_halo(Canvas canvas) {
//    canvas.draw_halo(pos, halo_size, halo_density, com().val_col_halo.get());
//  }
//  
//  void headTo(PVector c, float s) {
//    PVector l = new PVector(c.x, c.y);
//    l.add(-pos.x, -pos.y);
//    float r1 = mapToCircularValues(mov.heading(), l.heading(), s, -PI, PI);
//    mov.x = speed; mov.y = 0;
//    mov.rotate(r1);
//  }
//  void headTo(float l, float s) {
//    float r1 = mapToCircularValues(mov.heading(), l, s, -PI, PI);
//    mov.x = speed; mov.y = 0;
//    mov.rotate(r1);
//  }
//  
//  void pair(Floc b2) {
//    float d = dist(pos.x, pos.y, b2.pos.x, b2.pos.y);
//    if (d < com().SPACING.get()) {
//      headTo(b2.mov.heading(), com().FOLLOW.get() / ((com().SPACING.get() - d) / com().SPACING.get()) );
//      b2.headTo(mov.heading(), com().FOLLOW.get() / ((com().SPACING.get() - d) / com().SPACING.get()) );
//    } else {
//      headTo(b2.pos, com().POURSUITE.get() / d);
//      b2.headTo(pos, com().POURSUITE.get() / d);
//    }
//  }
//  
//  Floc init() {
//    age = 0;
//    max_age = int(random(0.3, 1.7) * com().AGE.get());
//    halo_size = com().HALO_SIZE.get();
//    halo_density = com().HALO_DENS.get();
//    halo_size += random(com().HALO_SIZE.get());
//    halo_density += random(com().HALO_DENS.get());
//    pos = com().adding_cursor.pos();
//    //pos.x = random(-com().startbox, com().startbox);
//    //pos.y = random(-com().startbox, com().startbox);
//    speed = random(0.3, 1.7) * com().SPEED.get();
//    mov.x = speed; mov.y = 0;
//    mov.rotate(random(PI * 2.0));
//    return this;
//  }
//  Floc frame() { return this; }
//  Floc tick() {
//    age++;
//    if (age > max_age) {
//      if (com().create_grower.get() && com().gcom != null && crandom(com().grow_prob.get()) > 0.5) {
//        Grower ng = com().gcom.newEntity();
//        if (ng != null) ng.define(new PVector(pos.x, pos.y), new PVector(1, 0).rotate(mov.heading()));
//      }
//      destroy();
//    }
//    int cible_cnt = 0;
//    if (com().point_to_mouse.get()) cible_cnt++;
//    if (com().point_to_center.get()) cible_cnt++;
//    if (com().point_to_cursor.get()) cible_cnt++;
//    //point toward mouse
//    if (com().point_to_mouse.get()) headTo(com().sim.inter.cam.screen_to_cam(new PVector(mouseX, mouseY)), 
//                                           com().POINT_FORCE.get() / cible_cnt);
//    //point toward center
//    if (com().point_to_center.get()) headTo(new PVector(0, 0), com().POINT_FORCE.get() / cible_cnt);
//    //point toward cursor
//    if (com().point_to_cursor.get()) headTo(com().adding_cursor.pos(), com().POINT_FORCE.get() / cible_cnt);
//    pos.add(mov);
//    return this;
//  }
//  Floc draw() {
//    fill(com().val_col_def.get());
//    stroke(com().val_col_def.get());
//    strokeWeight(4/com.sim.cam_gui.scale);
//    pushMatrix();
//    translate(pos.x, pos.y);
//    rotate(mov.heading());
//    if (com().DRAWMODE_DEF.get()) {
//      line(0, 0, -com().scale.get(), -com().scale.get());
//      line(2, 0, -com().scale.get(), 0);
//      line(0, 0, -com().scale.get(), com().scale.get());
//    }
//    fill(com().val_col_deb.get());
//    //stroke(com().val_col_deb.get());
//    noStroke();
//    float t = (com().scale.get() * (com().ref_size / 10)) / (com.sim.cam_gui.scale * 6);
//    if (com().DRAWMODE_DEBUG.get()) ellipse(0, 0, t, t);
//    popMatrix();
//    return this;
//  }
//  Floc clear() { return this; }
//  FlocComu com() { return ((FlocComu)com); }
//}
//
////#######################################################################
////##          ROTATING TO ANGLE CIBLE BY SHORTEST DIRECTION            ##
////#######################################################################
//
//
//float mapToCircularValues(float current, float cible, float increment, float start, float stop) {
//  if (start > stop) {float i = start; start = stop; stop = i;}
//  increment = abs(increment);
//  
//  while (cible > stop) {cible -= (stop - start);}
//  while (current > stop) {current -= (stop - start);}
//  while (cible < start) {cible += (stop - start);}
//  while (current < start) {current += (stop - start);}
//  
//  if (cible < current) {
//    if ( (current - cible) <= (stop - current + cible - start) ) {
//      if (increment >= current - cible) {return cible;}
//      else                              {return current - increment;}
//    } else {
//      if (increment >= stop - current + cible - start) {return cible;}
//      else if (current + increment < stop)             {return current + increment;}
//      else                                             {return start + (increment - (stop - current));}
//    }
//  } else if (cible > current) {
//    if ( (cible - current) <= (stop - cible + current - start) ) {
//      if (increment >= cible - current) {return cible;}
//      else                              {return current + increment;}
//    } else { 
//      if (increment >= stop - cible + current - start) {return cible;}
//      else if (current - increment > start)            {return current - increment;}
//      else                                             {return stop - (increment - (current - start));}
//    }
//  }
//  return cible;
//}
//
//class FlocPrint extends Sheet_Specialize {
//  Simulation sim;
//  FlocPrint(Simulation s) { super("Floc"); sim = s; }
//  FlocComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new FlocComu(sim, n, b); }
//}
//
//class FlocComu extends Community {
//  
//  void comPanelBuild(nFrontPanel sim_front) {
//    nFrontTab tab = sim_front.addTab(name);
//    tab.getShelf()
//      .addDrawerDoubleButton(DRAWMODE_DEF, DRAWMODE_DEBUG, 10.25, 1)
//      .addSeparator(0.125)
//      .addDrawerTripleButton(point_to_mouse, point_to_center, point_to_cursor, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(POURSUITE, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(FOLLOW, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(SPACING, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(SPEED, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(LIMIT, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(AGE, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(HALO_SIZE, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(HALO_DENS, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(POINT_FORCE, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerActFactValue("grower", create_grower, grow_prob, 2, 10, 1)
//      .addSeparator(0.125)
//      ;
//    
//    sim_front.toLayerTop();
//  }
//  
//  void selected_comu(Community c) { 
//    if (c != null && c.type_value.get().equals("grow")) gcom = (GrowerComu)c;
//  }
//  
//  sFlt POURSUITE, FOLLOW, SPACING, SPEED, HALO_SIZE, HALO_DENS, POINT_FORCE, grow_prob ;
//  sInt LIMIT, AGE ;
//  sBoo DRAWMODE_DEF, DRAWMODE_DEBUG, create_grower, point_to_mouse, point_to_center, point_to_cursor;
//  
//  sCol val_col_def, val_col_deb, val_col_halo;
//  sFlt scale;
//  
//  int startbox = 400;
//  
//  GrowerComu gcom;
//  
//  FlocComu(Simulation _c, String n, sValueBloc b) { super(_c, n, "floc", 50, b); 
//    POURSUITE = newFlt(0.3, "POURSUITE", "poursuite");
//    FOLLOW = newFlt(0.0036, "FOLLOW", "follox");
//    SPACING = newFlt(95, "SPACING", "space");
//    SPEED = newFlt(2, "SPEED", "speed");
//    grow_prob = newFlt(1, "grow_prob", "grow_prob");
//    LIMIT = newInt(1600, "limit", "limit");
//    AGE = newInt(2000, "age", "age");
//    HALO_SIZE = newFlt(80, "HALO_SIZE", "Size");
//    HALO_DENS = newFlt(0.15, "HALO_DENS", "Dens");
//    POINT_FORCE = newFlt(0.01, "POINT_FORCE", "point");
//    
//    DRAWMODE_DEF = newBoo(true, "DRAWMODE_DEF", "draw1");
//    DRAWMODE_DEBUG = newBoo(false, "DRAWMODE_DEBUG", "draw2");
//    
//    create_grower = newBoo(true, "create_grower", "create grow");
//    point_to_mouse = newBoo(false, "point_to_mouse", "to mouse");
//    point_to_center = newBoo(false, "point_to_center", "to center");
//    point_to_cursor = newBoo(false, "point_to_cursor", "to cursor");
//    //init_canvas();
//    
//    val_col_def = menuColor(color(220), "val_col_def");
//    val_col_deb = menuColor(color(255, 0, 0), "val_col_deb");
//    val_col_halo = menuColor(color(255, 0, 0), "val_col_halo");
//    scale = menuFltSlide(10, 5, 100, "length");
//  }
//  
//  void custom_pre_tick() {
//    for (Entity e1 : list)
//      for (Entity e2 : list)
//        if (e1.id < e2.id && e1 != e2 && e1.active && e2.active)
//            ((Floc)e1).pair(((Floc)e2));
//          
//  }
//  void custom_post_tick() {}
//  void custom_frame() {
//    //can.drawHalo(this);
//  }
//  void custom_cam_draw_post_entity() {}
//  void custom_cam_draw_pre_entity() {
//    //can.drawCanvas();
//  }
//  
//  Floc build() { return new Floc(this); }
//  Floc addEntity() { return newEntity(); }
//  Floc newEntity() {
//    for (Entity e : list) if (!e.active) { e.activate(); return (Floc)e; } return null; }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//class BoxPrint extends Sheet_Specialize {
//  Simulation sim;
//  BoxPrint(Simulation s) { super("Box"); sim = s; }
//  BoxComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new BoxComu(sim, n, b); }
//}
//
//
//
//
//class BoxComu extends Community {
//  
//  void comPanelBuild(nFrontPanel sim_front) {
//    nFrontTab tab = sim_front.addTab(name);
//    tab.getShelf()
//      .addSeparator(0.125)
//      .addDrawerDoubleButton(draw_dot, val_const_line, 10.25, 1)
//      .addSeparator(0.25)
//      .addDrawerDoubleButton(kill_grow, floc_kill, 10, 1)
//      .addSeparator(0.25)
//      .addDrawerFactValue(spacing_min, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(spacing_max, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(spacing_diff, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(spacing_max_dist, 2, 10, 1)
//      .addSeparator(0.25)
//      .addDrawerFactValue(box_size_min, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(box_size_max, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(corner_space, 2, 10, 1)
//      .addSeparator(0.25)
//      .addDrawerFactValue(duplicate_prob, 2, 10, 1)
//      .addSeparator(0.125)
//      .addDrawerFactValue(max_age, 2, 10, 1)
//      .addSeparator(0.25)
//      .addDrawer(10.25, 0.75)
//      .addWatcherModel("Label-S4", "Select Grower: ").setLinkedValue(selected_com2).getShelf()
//      .addSeparator(0.125)
//      ;
//      
//    selector_list2 = tab.getShelf()
//      .addSeparator(0.25)
//      .addList(4, 10, 1);
//    selector_list2.addEventChange_Builder(new Runnable() { public void run() {
//      nList sl = ((nList)builder); 
//      //logln("a "+sl.last_choice_index +"  "+ sim.list.size());
//      if (sl.last_choice_index < sim.list.size()) 
//        selected_comu(sim.list.get(sl.last_choice_index));
//        selected_com2.set(sim.list.get(sl.last_choice_index).name);
//    } } );
//        
//    selector_list2.getShelf()
//      .addSeparator(0.125)
//      .addDrawer(10.25, 0.75)
//      .addWatcherModel("Label-S4", "Selected: ").setLinkedValue(selected_com2).getShelf()
//      .addSeparator(0.125)
//      ;
//    
//    selector_entry2 = new ArrayList<String>(); // mmain().data.getCountOfType("flt")
//    selector_value2 = new ArrayList<Community>(); // mmain().data.getCountOfType("flt")
//    
//    update_com_selector_list2();
//    
//    sim_front.toLayerTop();
//  }
//  void update_com_selector_list2() {
//    selector_entry2.clear();
//    selector_value2.clear();
//    for (Community v : sim.list) { 
//      selector_entry2.add(v.name); 
//      selector_value2.add(v);
//    }
//    if (selector_list2 != null) selector_list2.setEntrys(selector_entry2);
//  }
//  ArrayList<String> selector_entry2;
//  ArrayList<Community> selector_value2;
//  Community selected_value2;
//  String selected_entry2;
//  nList selector_list2;
//  
//  sFlt spacing_min , spacing_max, spacing_diff, spacing_max_dist, box_size_min, 
//    box_size_max, duplicate_prob, corner_space, box_point_size, val_line_w;
//  
//  sInt max_age;
//  
//  sCol val_col1, val_col2, val_col3;
//  sStr selected_com2;
//  
//  sBoo draw_dot, val_const_line, kill_grow, floc_kill;
//  
//  int cnt = 0;
//  FlocComu fcom;
//  GrowerComu gcom;
//  
//  void selected_comu(Community c) { 
//    //logln(c.name + c.type_value.get());
//    if (c != null && c.type_value.get().equals("floc")) fcom = (FlocComu)c;
//    if (c != null && c.type_value.get().equals("grow")) gcom = (GrowerComu)c;
//  }
//
//  BoxComu(Simulation _c, String n, sValueBloc b) { super(_c, n, "bloc", 0, b);
//    spacing_min = newFlt(50, "box_spacing_min", "sp min");
//    spacing_max = newFlt(200, "box_spacing_max", "sp max");
//    spacing_diff = newFlt(1, "box_spacing_diff", "sp dif");
//    val_line_w = menuFltSlide(5, 0.5, 20, "line_w");
//    spacing_max_dist = newFlt(10000, "normal_spacing_dist", "norm sp");
//    box_size_min = newFlt(100, "box_size_min", "sz min");
//    box_size_max = newFlt(400, "box_size_max", "sz max");
//    duplicate_prob = newFlt(5.0, "duplicate_prob", "duplic");
//    corner_space = newFlt(40, "box_corner_space", "corner");
//    max_age = newInt(2000, "max_age", "age");
//    draw_dot = newBoo(true, "draw_dot", "draw_dot");
//    val_const_line = newBoo(true, "val_const_line", "val_const_line");
//    kill_grow = newBoo(true, "kill_grow", "kill_grow");
//    floc_kill = newBoo(true, "floc_kill", "floc_kill");
//    selected_com2 = newStr("selected_com2", "scom2", "");
//    
//    val_col1 = menuColor(color(10, 190, 40), "val_fill");
//    val_col2 = menuColor(color(30, 220, 20), "val_line");
//    val_col3 = menuColor(color(0, 255, 0), "val_dot");
//    box_point_size = menuFltSlide(32, 10, 200, "box_point_size");
//    
//    addEventSetupLoad(new Runnable() { public void run() { 
//      sim.inter.addEventNextFrame(new Runnable() {public void run() { 
//        for (Community c : sim.list) if (c.name.equals(selected_com2.get())) selected_comu(c);
//      }}); } } );
//    
//  }
//  void custom_pre_tick() {}
//  void custom_build() {}
//  
//  
//  void custom_post_tick() { 
//    cnt+=2;
//    if (cnt > 2400) cnt -= 2400;
//  }
//  void custom_cam_draw_pre_entity() {}
//  void custom_reset() { cnt = 0; }
//  void custom_cam_draw_post_entity() { 
//    //float r = spacing_max_dist.get();  
//    //noFill();
//    //stroke(255);
//    ////ellipse(0, 0, r, r);
//    
//  }//
//  
//  Box build() { return new Box(this); }
//  Box addEntity() { return newEntity(); }
//  Box newEntity() { 
//    for (Entity e : list) if (!e.active) { e.activate(); return (Box)e; } return null; }
//}
//
//
//class Box extends Entity {
//  Rect rect = new Rect();
//  Box origin;
//  int generation = 1;
//  PVector connect1 = new PVector(0, 0);
//  PVector connect2 = new PVector(0, 0);
//  PVector origin_co = new PVector(0, 0); //origin box to ext co
//  float space = 0;
//  int age = 0;
//  
//  Box(BoxComu c) { super(c); }
//  
//  //void draw_halo(Canvas canvas, PImage i) {}
//  
//  void pair(Box b2) {}
//  
//  Box init() {
//    rect.size.x = random(com().box_size_min.get(), com().box_size_max.get()); 
//    rect.size.y = random(com().box_size_min.get(), com().box_size_max.get());
//    rect.pos.x = com().adding_cursor.pos().x;
//    rect.pos.y = com().adding_cursor.pos().y;
//    rect.pos.x -= rect.size.x/2; 
//    rect.pos.y -= rect.size.y/2;
//    connect1.x = rect.pos.x; connect1.y = rect.pos.y;
//    connect2.x = rect.pos.x; connect2.y = rect.pos.y;
//    origin = null;
//    origin_co.x = 0;
//    origin_co.y = 0;
//    generation = 1;
//    space = com().spacing_min.get();
//    rotation = -0.008;
//    col = 0;
//    age = 0;
//    return this;
//  }
//  void define_bis(Box b2, float x, float y, String dir) {
//    rect.pos.x = x; rect.pos.y = y;
//    for (Entity e : com().list) if (e.active) {
//      Box b = (Box)e;
//      if (b != this && rectCollide(rect, b.rect, com().spacing_min.get()/2)) {//-2
//        this.destroy(); return; } }
//    origin = b2;
//    generation = b2.generation + 1;
//    float corner_space = com().corner_space.get();
//    if (dir.charAt(0) == 'v') {
//      if (dir.charAt(1) == 'u') {
//        connect1.x = random(rect.pos.x + corner_space, rect.pos.x + rect.size.x - (2*corner_space));
//        connect1.y = rect.pos.y + rect.size.y;
//        connect2.x = random(b2.rect.pos.x + corner_space, b2.rect.pos.x + b2.rect.size.x - (2*corner_space));
//        connect2.y = b2.rect.pos.y;
//      } else {
//        connect1.x = random(rect.pos.x + corner_space, rect.pos.x + rect.size.x - (2*corner_space));
//        connect1.y = rect.pos.y;
//        connect2.x = random(b2.rect.pos.x + corner_space, b2.rect.pos.x + b2.rect.size.x - (2*corner_space));
//        connect2.y = b2.rect.pos.y + b2.rect.size.y;
//      }
//    } else {
//      if (dir.charAt(1) == 'l') {
//        connect1.y = random(rect.pos.y + corner_space, rect.pos.y + rect.size.y - (2*corner_space));
//        connect1.x = rect.pos.x + rect.size.x;
//        connect2.y = random(b2.rect.pos.y + corner_space, b2.rect.pos.y + b2.rect.size.y - (2*corner_space));
//        connect2.x = b2.rect.pos.x;
//      } else {
//        connect1.y = random(rect.pos.y + corner_space, rect.pos.y + rect.size.y - (2*corner_space));
//        connect1.x = rect.pos.x;
//        connect2.y = random(b2.rect.pos.y + corner_space, b2.rect.pos.y + b2.rect.size.y - (2*corner_space));
//        connect2.x = b2.rect.pos.x + b2.rect.size.x;
//      }
//    }
//    origin_co.x = connect2.x - origin.rect.pos.x;
//    origin_co.y = connect2.y - origin.rect.pos.y; //origin box to ext co
//    //PVector connect_line = new PVector(connect1.x - connect2.x, connect1.y - connect2.y); //ext co to self co
//    
//    rotation = 0;//.008 * (6000 - connect_line.mag()) / 6000;
//    //PVector box_local = new PVector(rect.pos.x - connect1.x, rect.pos.y - connect1.y); //self co to box pos
//    //connect_line.rotate(rotation + burst);
//    //connect1.x = connect_line.x + connect2.x;
//    //connect1.y = connect_line.y + connect2.y;
//    //rect.pos.x = box_local.x + connect1.x;
//    //rect.pos.y = box_local.y + connect1.y;
//  }
//  
//  Box define(Box b2) {
//    space = com().spacing_min.get() + 
//            ( 2 * com().spacing_max.get() * min(1, b2.rect.pos.mag()
//            / com().spacing_max_dist.get()) ) * crandom(com().spacing_diff.get());
//    //space = crandom( com().spacing_min.get(), 
//    //                 com().spacing_max.get(), 
//    //                 ( min(0, com().spacing_max_dist.get() - b2.rect.pos.mag()) / com().spacing_max_dist.get()) * com().spacing_diff.get() );
//    rect.size.x = random(com().box_size_min.get(), com().box_size_max.get()); 
//    rect.size.y = random(com().box_size_min.get(), com().box_size_max.get());
//    boolean axe = random(10) < 5;
//    float dir_mod = 0;
//    if (axe && b2.rect.pos.y > 0) dir_mod = -2.5;
//    if (axe && b2.rect.pos.y < 0) dir_mod = 2.5;
//    if (!axe && b2.rect.pos.x > 0) dir_mod = -2.5;
//    if (!axe && b2.rect.pos.x < 0) dir_mod = 2.5;
//    boolean side = random(10) < 5 + dir_mod;
//    if (axe) {
//      if (side) {
//        define_bis(b2, b2.rect.pos.x - rect.size.x - space + random(b2.rect.size.x + rect.size.x + 2*space), 
//                       b2.rect.pos.y - (rect.size.y + space), "vu"); }
//      else {
//        define_bis(b2, b2.rect.pos.x - rect.size.x - space + random(b2.rect.size.x + rect.size.x + 2*space),
//                       b2.rect.pos.y + b2.rect.size.y + space, "vd"); } }
//    else {
//      if (side) {  
//        define_bis(b2, b2.rect.pos.x - (rect.size.x + space),
//                       b2.rect.pos.y - rect.size.y - space + random(b2.rect.size.y + rect.size.y + 2*space), "hl"); }
//      else {                 
//        define_bis(b2, b2.rect.pos.x + b2.rect.size.x + space,
//                       b2.rect.pos.y - rect.size.y - space + random(b2.rect.size.y + rect.size.y + 2*space), "hr"); } }
//    return this;
//  }
//  
//  float rotation = -0.008;
//  int col = 0;
//  float burst = 0;
//  boolean blocked = false;
//  
//  Box frame() { return this; }
//  Box tick() {
//    age++;
//    if (age > com().max_age.get()) this.destroy();
//    
//    if (com().floc_kill.get() && com().fcom != null) for (Entity e : com().fcom.list) if (e.active) {
//      Floc f = (Floc)e;
//      if (rectCollide(f.pos, rect)) {
//        this.destroy();
//      }
//    }
//    if (com().kill_grow.get() && com().gcom != null) for (Entity e : com().gcom.list) if (e.active) {
//      Grower f = (Grower)e;
//      if (rectCollide(f.pos, rect)) {
//        f.destroy();
//        //lose hp ^^
//        generation--;
//      }
//    }
//    
//    if (random(100) < com().duplicate_prob.get()) {
//      Box nb = com().newEntity();
//      if (nb != null) {
//        nb.define(this); } }
//    
//    float rspeed = 0.008 / generation;
//    int pcol = col;
//    col = 0;
//    for (Entity e : com().list) if (e.active) {
//      Box b = (Box)e;
//      //if (col >= 1) { rotation = 0; }
//      if (b != this && rectCollide(rect, b.rect, com().spacing_min.get()/2)) {//-2
//        //if (col > 0 && !blocked) rotation *= 1.01;
//        if (col == 0 && !blocked) rotation *= -1;
//        col += 1;
//        //if (col == 0 && abs(rotation) > rspeed*2) rotation = 0;
//      } }
//    //if (blocked) rotation -= 0.00001;
//    //if (abs(rotation) > rspeed*2) { blocked = true; burst = 0.1; if (rotation < 0) burst *= -1; rotation = 0;  }
//    //if (col == 0 && abs(rotation) > rspeed) rotation /= 1.01;
//    if (pcol == 0) blocked = false;
//    //if (blocked && rotation == 0) rotation = rspeed;
//    //println(com().comList.tick.get() + " " + col + " " + rotation);
//    
//    PVector connect_line = new PVector(connect1.x - connect2.x, connect1.y - connect2.y); //ext co to self co
//    if (origin != null && origin.active) {
//      //connect2.x = origin.rect.pos.x + origin_co.x;
//      //connect2.y = origin.rect.pos.y + origin_co.y;
//      //PVector box_local = new PVector(rect.pos.x - connect1.x, rect.pos.y - connect1.y); //self co to box pos
//      ////connect_line.rotate(rotation + burst);
//      //connect1.x = connect_line.x + connect2.x;
//      //connect1.y = connect_line.y + connect2.y;
//      //rect.pos.x = box_local.x + connect1.x;
//      //rect.pos.y = box_local.y + connect1.y;
//      
//      //burst /= 1.01;
//    }
//    return this; }
//  
//  Box draw() {
//    float connect_bubble_size = com().corner_space.get();
//    
//    
//    float rd = 255.0 * (float)((10.0 - float(abs(generation - int(com().cnt/60.0)))) / 10.0);
//    float stroke_limit = 1;
//    if (rd <= stroke_limit) rd = 255.0 * (float)((10.0 - float(abs(generation - int((com().cnt+1200)/60.0)))) / 10.0);
//    if (rd <= stroke_limit) rd = 255.0 * (float)((10.0 - float(abs(generation - int((com().cnt-1200)/60.0)))) / 10.0);
//    if (rd <= stroke_limit) rd = 255.0 * (float)((10.0 - float(abs(generation - int((com().cnt+2400)/60.0)))) / 10.0);
//    if (rd <= stroke_limit) rd = 255.0 * (float)((10.0 - float(abs(generation - int((com().cnt-2400)/60.0)))) / 10.0);
//    //if (abs(generation - int(com().cnt/60)) < 10) 
//    float c1f = float(max(100, int(rd-20)))/255.0;
//    color filling = color( com().val_col1.getred()*c1f, 
//                           com().val_col1.getgreen()*c1f, 
//                           com().val_col1.getblue()*c1f );
//    float fc = max( 150, 255 - max(0, int(rd)) ) / 255.0;
//    color lining = color( com().val_col2.getred()*fc, 
//                           com().val_col2.getgreen()*fc, 
//                           com().val_col2.getblue()*fc );
//    //println(lining);
//    boolean constent_w = com().val_const_line.get();
//    float line_factor = com().val_line_w.get();
//    
//    noFill();
//    stroke(lining);
//    if (constent_w) strokeWeight(max(2/com.sim.cam_gui.scale, connect_bubble_size/1.3));
//    else strokeWeight(max(line_factor*2, connect_bubble_size/1.3));
//    line(connect1.x, connect1.y, connect2.x, connect2.y);
//    if (connect_bubble_size*com.sim.cam_gui.scale > 3) {
//      fill(filling);
//      stroke(lining);
//      if (constent_w) strokeWeight(4/com.sim.cam_gui.scale);
//      else strokeWeight(4*line_factor);
//      ellipse(connect1.x, connect1.y, connect_bubble_size, connect_bubble_size);
//      ellipse(connect2.x, connect2.y, connect_bubble_size, connect_bubble_size); }
//    fill(filling);
//    stroke(lining);
//    if (constent_w) strokeWeight(2/com.sim.cam_gui.scale);
//    else strokeWeight(2*line_factor);
//    rect.draw();
//    noFill();
//    stroke(0, 255, 0);
//    if (constent_w) strokeWeight(3/com.sim.cam_gui.scale);
//    else strokeWeight(3*line_factor);
//    //rect(rect.pos.x - space/2, rect.pos.y - space/2, rect.size.x + space, rect.size.y + space);
//    if (connect_bubble_size*com.sim.cam_gui.scale > 3) {
//      fill(filling);
//      noStroke();
//      ellipse(connect1.x, connect1.y, connect_bubble_size, connect_bubble_size);
//      ellipse(connect2.x, connect2.y, connect_bubble_size, connect_bubble_size); }
//    noFill();
//    stroke(filling);
//    if (constent_w) strokeWeight(max(0, connect_bubble_size/1.3 - 4/com.sim.cam_gui.scale));
//    else strokeWeight(max(0, connect_bubble_size/1.3 - 4*line_factor));
//    line(connect1.x, connect1.y, connect2.x, connect2.y);
//    if (com().draw_dot.get()) {
//      int point_size = int(com().box_point_size.get());
//      int c = 0;
//      color c3 = com().val_col3.get();
//      //strokeWeight(point_size);
//      for (float i = rect.pos.x + (rect.size.x%point_size)/2 + point_size/2; i < rect.pos.x + rect.size.x ; i += point_size) 
//        for (float j = rect.pos.y + (rect.size.y%point_size)/2 + point_size/2; j < rect.pos.y + rect.size.y ; j += point_size) {
//          noStroke();
//          fill(red(c3), green(c3), blue(c3), c);
//          ellipseMode(CENTER);
//          ellipse(i, j, point_size/2, point_size/2);
//          c+=(generation*int(point_size));
//          c %= 255;
//        }
//    }
//    fill(lining);
//    textFont(getFont(int(rect.size.y/3)));
//    text(""+generation, rect.pos.x + rect.size.x/3, rect.pos.y + rect.size.y/1.41);
//    return this; }
//  Box clear() { return this; }
//  BoxComu com() { return ((BoxComu)com); }
//}
//
//
//
//
//
//
//
//
//
//
//   
