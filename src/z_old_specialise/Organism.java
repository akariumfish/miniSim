package z_old_specialise;

import UI.*;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;

import Macro.*;
import RApplet.Rapp;
import sData.*;










/*
organism
  cell group limited in size
  different etat influence les stat global de l'organisme > preset
  l'etat depand de la situation majoritaire des cells 
    > condition du type "+ de x% des cell sont dans tel etat"

cell
  shape
  spacialization
  different etat / situation constitue le cicle de vie
    condition de changement
    consequance sur les variables
      element graphique
      feedback ?
  ex:
    evenement : naissance
    etat : croissance
    evenement : produit une cell  /  stop croissance   /  fleurie
    etats :     static            /  static            /  bloom
    evenement : meur (rng)        /  produit une cell  /  stop croissance
    etats :     dead              /  static            /  static
    evenement :                   /  meur (age)        /  meur (age)
    etats :     dead              /  dead              /  dead

shape interaction
  slowed down, 

*/




public class Organism extends Macro_Sheet {
  

	public static class OrganismPrint extends Sheet_Specialize {
	  Simulation sim;
		  public OrganismPrint(Simulation s) { super("Organism"); sim = s; }
		  public OrganismPrint() { super("Organism"); }
	  public void default_build() { }
	  public Organism get_new(Macro_Sheet s, String n, sValueBloc b) { return new Organism(sim, n, b); }
//	  Organism get_new(Macro_Sheet s, String n, Organism b) { return new Organism(mmain, n, b); }
	}

  public void build_custom_menu(nFrontPanel sheet_front) {
    if (sheet_front != null) {
      
      sheet_front.getTab(2).getShelf()
        .addSeparator(0.125)
        .addDrawerButton(adding_cursor.show, 10, 1)
        .addSeparator(0.125);
        
      sheet_front.toLayerTop();
    }
  }

  sRun srun_duplic;
  
  Simulation sim;

  nRunnable tick_run, rst_run; //Drawable cam_draw;
  
  ArrayList<Cell> list = new ArrayList<Cell>(); //contien les objet

  sInt max_entity, active_entity;
  
  sFlt blarg, larg, lon, dev, shrt, branch;
  
  sCol val_fill1, val_fill2, val_stroke;
  
  public sInt val_draw_layer;
  
  nCursor adding_cursor;
  
  sRun srun_reset;
  
  sObj face_obj;
  
  Face face_source;
  
  Organism(Simulation _s, String n, sValueBloc b) { 
    super(_s.mmain(), n, b);
    sim = _s;
    sim.organs.add(this);
    
    branch = menuFltFact(10, 2, "branch");
    shrt = menuFltFact(0.95F, 1.02F, "shortening");
    dev = menuFltFact(4, 2, "deviation");
    lon = menuFltSlide(40, 5, 400, "length");
    blarg = menuFltSlide(0.3F, 0.05F, 3, "base larg");
    larg = menuFltFact(1, 1.02F, "large");
    
    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");
    
    val_stroke = menuColor(gui.app.color(10, 190, 40), "val_stroke");
    val_fill2 = menuColor(gui.app.color(30, 90, 20), "val_fill2");
    val_fill1 = menuColor(gui.app.color(20, 130, 40), "val_fill1");
    
    max_entity = menuIntIncr(40, 100, "max_entity");
    
    organ_init();
    
    adding_cursor = menuCursor("add", true);
    
  }
//  Organism(Macro_Main _m, String n, Organism b) { 
//    super(_m, n, null);
//    
////    sim = _s;
//    branch = menuFltFact(b.branch.get(), 2, "branch");
//    shrt = menuFltFact(b.shrt.get(), 1.02F, "shortening");
//    dev = menuFltFact(b.dev.get(), 2, "deviation");
//    lon = menuFltSlide(b.lon.get(), 5, 400, "length");
//    blarg = menuFltSlide(b.blarg.get(), 5, 400, "base larg");
//    larg = menuFltFact(b.larg.get(), 1.02F, "large");
//    
//    val_stroke = menuColor(b.val_stroke.get(), "val_stroke");
//    val_fill2 = menuColor(b.val_fill2.get(), "val_fill2");
//    val_fill1 = menuColor(b.val_fill1.get(), "val_fill1");
//    
//    max_entity = menuIntIncr(b.max_entity.get(), 100, "max_entity");
//    
//    organ_init();
//    
//    adding_cursor = menuCursor("add", true);
//    adding_cursor.pval.set(b.adding_cursor.pval.get());
//    adding_cursor.pval.add(ref_size * 4, 0);
//  }
  
  void organ_init() {
    
    face_obj = newObj("face_obj", "face_obj");
    face_obj.addEventChange(new nRunnable() { public void run() {
//      if (face_obj.isSheet()) {
//        Macro_Sheet ms = face_obj.asSheet();
//        if (ms.specialize.get().equals("Face")) face_source = (Face)ms;
//      }
    }});
    
    
    active_entity = menuIntWatch(0, "active_entity");
    
    srun_reset = newRun("organ_reset", "reset", new nRunnable() { 
      public void run() { reset(); } } );
    srun_duplic = newRun("duplication", "duplic", new nRunnable() { public void run() { duplicate(); } } );
    
    tick_run = new nRunnable() { public void run() { tick(); } };
    rst_run = new nRunnable() { public void run() { reset(); } };
    //cam_draw = new Drawable() { public void drawing() { 
    //  draw_All(); } };
    
//    if (sim != null) sim.addEventTick(tick_run);
//    //if (sim != null) sim.inter.addToCamDrawerPile(cam_draw);
//    if (sim != null) sim.reset();
//    if (sim != null) sim.addEventReset(rst_run);
  }

  public Organism clear() {
//    sim.organs.remove(this);
    this.destroy_All();
//    sim.removeEventTick(tick_run);
//    sim.removeEventReset(rst_run);
    //cam_draw.clear();
    super.clear();
    return this;
  }
  
  void duplicate() {
    //if (selected_cursor != null) {
    //  Organism m = (Organism)sheet_specialize.add_new(sim, null, this);
    //  m.setPosition(selected_cursor.pos().x, selected_cursor.pos().y);
    //}
  }
  
  void init_array() {
    list.clear();
    for (int i = 0; i < max_entity.get(); i++)
      list.add(build());
  }

  void reset() { //deactivate all then create starting situation from parameters
    this.destroy_All();
    if (max_entity.get() != list.size()) init_array();
    
//    Cell c = 
    		newEntity(null);
    
  }

  void tick() {
    active_entity.set(active_Entity_Nb());
    for (Cell e : list) if (e.active) e.tick();
    
  }

  public void draw_All() { 
    for (Cell e : list) if (e.active) e.draw(gui.app); }
  void destroy_All() { 
    for (Cell e : list) e.destroy(); }

  int active_Entity_Nb() {
    int n = 0;
    for (Cell e : list) if (e.active) n++;
    return n;
  }
  Cell build() { 
    return new Cell(this);
  }
  Cell newEntity(Cell p) {
    Cell ng = null;
    for (Cell e : list) 
      if (!e.active && ng == null) { 
        ng = (Cell)e; 
        e.activate();
      }
    if (ng != null) ng.define(p);
    return ng;
  }
}




/*
class nface
  3 coordinate
  should all have the same surface!!
  
nShape spacialization:
  pos, dir, scale, mirroring
  
nBase 
  an exemple
*/





class Cell {
  
  nBase shape;

  Organism com;
  int age = 0;
  boolean active = false;
  
  int state = 0;
  Cell(Organism c) { 
    com = c;
  }
  Cell clear() { 
    return this;
  }
  Cell activate() {
    if (!active) { 
      active = true; 
      age = 0; 
      state = 0;
//      shape = new nBase(com().gui.app);
//      if (com().face_source != null) {
//        nBase fb = com().face_source.shape;
//        shape.face.p1.set(fb.face.p1.x, fb.face.p1.y * com().blarg.get());
//        shape.face.p2.set(fb.face.p2.x, fb.face.p2.y * com().blarg.get());
//        shape.face.p3.set(fb.face.p3.x, fb.face.p3.y * com().blarg.get());
//      } else {
//        shape.face.p1.set(1, 0);
//        shape.face.p2.set(0, com().blarg.get());
//        shape.face.p3.set(-1, -com().blarg.get());
//      }
//      shape.dir.setMag(com().lon.get());
//      float inf = (float)(com().active_entity.get()) / (float)(com().max_entity.get());
//      float inf2 = ((float)(com().max_entity.get()) - (float)(com().active_entity.get())) / 
//    		  (float)(com().max_entity.get());
//      float re = (com().val_fill2.getred() * inf + com().val_fill1.getred() * inf2) / 1.0F;
//      float gr = (com().val_fill2.getgreen() * inf + com().val_fill1.getgreen()* inf2) / 1.0F;
//      float bl = (com().val_fill2.getblue() * inf + com().val_fill1.getblue() * inf2) / 1.0F;
//      shape.col_fill = com().gui.app.color(re, gr, bl);
//      shape.col_line = com().val_stroke.get();
    }
    return this;
  }
  Cell destroy() {
    if (active) { 
      active = false; 
      clear();
    }
    return this;
  }
  Cell define(Cell p) {
//    if (p != null) {
//      PVector _p = p.shape.pos;
//      PVector _d = p.shape.dir;
//      shape.pos.x = _p.x + _d.x;
//      shape.pos.y = _p.y + _d.y;
//      shape.dir.set(_d);
//      shape.dir.rotate(com().gui.app.random(-PConstants.HALF_PI/com().dev.get(), PConstants.HALF_PI/com().dev.get()));
//      shape.dir.setMag(shape.dir.mag() * com().gui.app.random(Math.min(com().shrt.get(), 1), Math.max(com().shrt.get(), 1)) );
//      
//      shape.face.p2.set(p.shape.face.p2.x, - p.shape.face.p2.y / com().larg.get());
//      shape.face.p3.set(p.shape.face.p3.x, - p.shape.face.p3.y / com().larg.get());
//    } else if (com().adding_cursor != null) {
//      shape.pos.x = com().adding_cursor.pos().x;
//      shape.pos.y = com().adding_cursor.pos().y;
//      float dm = shape.dir.mag();
//      shape.dir.set(com().adding_cursor.dir());
//      shape.dir.rotate(com().gui.app.random(-PConstants.HALF_PI/com().dev.get(), PConstants.HALF_PI/com().dev.get()));
//      shape.dir.setMag(dm * shape.dir.mag() * com().gui.app.random(Math.min(com().shrt.get(), 1), Math.max(com().shrt.get(), 1)) );
//    }
    return this;
  }
  Cell tick() {
    age++;
    if (state == 0) {
      if (age == 2) {
        com().newEntity(this);
        state = 1;
      }
    } else if (state == 1) {
      if (com().gui.app.crandom(com().branch.get()) > 0.5) {
        com().newEntity(this);
        //nCursor c = com().newCursor("branch_"+com().cursor_count, true);
        //c.pval.set(shape.pos.x + shape.dir.x, shape.pos.y + shape.dir.y);
        //c.dval.set(shape.dir.x, shape.dir.y);
        //c.show.set(true);
      }
      state = 2;
    } else if (state == 2) {
      
    }
    return this;
  }
  Cell draw(Rapp a) {
//    shape.draw(a);
    return this;
  }
  Organism com() { 
    return ((Organism)com);
  }
}








      
  
