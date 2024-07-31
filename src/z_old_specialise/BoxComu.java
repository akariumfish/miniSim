package z_old_specialise;

import Macro.*;
import RApplet.RConst;
import RApplet.Rapp;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;
import java.util.ArrayList;





public class BoxComu extends Community { 
	public static class BoxPrint extends Sheet_Specialize {
		  Simulation sim;
		  public BoxPrint(Simulation s) { super("Box"); sim = s; }
		  public BoxComu get_new(Macro_Sheet s, String n, sValueBloc b) { return new BoxComu(sim, n, b); }
		}
  void comPanelBuild(nFrontPanel sim_front) {
    nFrontTab tab = sim_front.addTab(name);
    tab.getShelf()
      .addSeparator(0.125)
      .addDrawerDoubleButton(draw_dot, val_const_line, 10.25F, 1.0F)
      .addSeparator(0.25)
      .addDrawerDoubleButton(kill_grow, floc_kill, 10, 1)
      .addSeparator(0.25)
      .addDrawerFactValue(spacing_min, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(spacing_max, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(spacing_diff, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(spacing_max_dist, 2, 10, 1)
      .addSeparator(0.25)
      .addDrawerFactValue(box_size_min, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(box_size_max, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(corner_space, 2, 10, 1)
      .addSeparator(0.25)
      .addDrawerFactValue(duplicate_prob, 2, 10, 1)
      .addSeparator(0.125)
      .addDrawerFactValue(max_age, 2, 10, 1)
      .addSeparator(0.25)
      .addDrawer(10.25, 0.75)
      .addWatcherModel("Label-S4", "Select Grower: ").setLinkedValue(selected_com2).getShelf()
      .addSeparator(0.125)
      ;
      
    selector_list2 = tab.getShelf()
      .addSeparator(0.25)
      .addList(4, 10, 1);
    selector_list2.addEventChange_Builder(new nRunnable() { public void run() {
      nList sl = ((nList)builder); 
      //logln("a "+sl.last_choice_index +"  "+ sim.list.size());
      if (sl.last_choice_index < sim.list.size()) 
        selected_comu(sim.list.get(sl.last_choice_index));
        selected_com2.set(sim.list.get(sl.last_choice_index).name);
    } } );
        
    selector_list2.getShelf()
      .addSeparator(0.125)
//      .addDrawer(10.25, 0.75)
//      .addWatcherModel("Label-S4", "Selected Floc: ").setLinkedValue(selected_com2).getShelf()
      .addSeparator(0.125)
      ;
    
    selector_entry2 = new ArrayList<String>(); // mmain().data.getCountOfType("flt")
    selector_value2 = new ArrayList<Community>(); // mmain().data.getCountOfType("flt")
    
    update_com_selector_list2();
    
    sim_front.toLayerTop();
  }
  void update_com_selector_list2() {
    selector_entry2.clear();
    selector_value2.clear();
    for (Community v : sim.list) { 
      selector_entry2.add(v.name); 
      selector_value2.add(v);
    }
    if (selector_list2 != null) selector_list2.setEntrys(selector_entry2);
  }
  ArrayList<String> selector_entry2;
  ArrayList<Community> selector_value2;
  Community selected_value2;
  String selected_entry2;
  nList selector_list2;
  
  sFlt spacing_min , spacing_max, spacing_diff, spacing_max_dist, box_size_min, 
    box_size_max, duplicate_prob, corner_space, box_point_size, val_line_w;
  
  sInt max_age;
  
  sCol val_col1, val_col2, val_col3;
  sStr selected_com2;
  
  sBoo draw_dot, val_const_line, kill_grow, floc_kill;
  
  int cnt = 0;
  FlocComu fcom;
  GrowerComu gcom;
  
  void selected_comu(Community c) { 
    //logln(c.name + c.type_value.get());
    if (c != null && c.type_value.get().equals("floc")) fcom = (FlocComu)c;
    if (c != null && c.type_value.get().equals("grow")) gcom = (GrowerComu)c;
  }

  BoxComu(Simulation _c, String n, sValueBloc b) { super(_c, n, "bloc", 0, b);
    spacing_min = newFlt(50, "box_spacing_min", "sp min");
    spacing_max = newFlt(200, "box_spacing_max", "sp max");
    spacing_diff = newFlt(1, "box_spacing_diff", "sp dif");
    val_line_w = menuFltSlide(5, 0.5F, 20, "line_w");
    spacing_max_dist = newFlt(10000, "normal_spacing_dist", "norm sp");
    box_size_min = newFlt(100, "box_size_min", "sz min");
    box_size_max = newFlt(400, "box_size_max", "sz max");
    duplicate_prob = newFlt(5.0F, "duplicate_prob", "duplic");
    corner_space = newFlt(40, "box_corner_space", "corner");
    max_age = newInt(2000, "max_age", "age");
    draw_dot = newBoo(true, "draw_dot", "draw_dot");
    val_const_line = newBoo(true, "val_const_line", "val_const_line");
    kill_grow = newBoo(true, "kill_grow", "kill_grow");
    floc_kill = newBoo(true, "floc_kill", "floc_kill");
    selected_com2 = newStr("selected_com2", "scom2", "");
    
    val_col1 = menuColor(app.color(10, 190, 40), "val_fill");
    val_col2 = menuColor(app.color(30, 220, 20), "val_line");
    val_col3 = menuColor(app.color(0, 255, 0), "val_dot");
    box_point_size = menuFltSlide(32, 10, 200, "box_point_size");
    
    addEventSetupLoad(new nRunnable() { public void run() { 
      sim.inter.addEventNextFrame(new nRunnable() {public void run() { 
        for (Community c : sim.list) if (c.name.equals(selected_com2.get())) selected_comu(c);
      }}); } } );
    
  }
  void custom_pre_tick() {}
  void custom_build() {}
  
  
  void custom_post_tick() { 
    cnt+=2;
    if (cnt > 2400) cnt -= 2400;
  }
  void custom_cam_draw_pre_entity() {}
  void custom_reset() { cnt = 0; }
  void custom_cam_draw_post_entity() { 
    //float r = spacing_max_dist.get();  
    //noFill();
    //stroke(255);
    ////ellipse(0, 0, r, r);
    
  }//
  
  Box build() { return new Box(this); }
  Box addEntity() { return newEntity(); }
  Box newEntity() { 
    for (Entity e : list) if (!e.active) { e.activate(); return (Box)e; } return null; }
}


class Box extends Entity {
  Rect rect = new Rect();
  Box origin;
  int generation = 1;
  PVector connect1 = new PVector(0, 0);
  PVector connect2 = new PVector(0, 0);
  PVector origin_co = new PVector(0, 0); //origin box to ext co
  float space = 0;
  int age = 0;
  
  Box(BoxComu c) { super(c); }
  
  //void draw_halo(Canvas canvas, PImage i) {}
  
  void pair(Box b2) {}
  
  Box init() {
    rect.size.x = com().app.random(com().box_size_min.get(), com().box_size_max.get()); 
    rect.size.y = com().app.random(com().box_size_min.get(), com().box_size_max.get());
    rect.pos.x = com().adding_cursor.pos().x;
    rect.pos.y = com().adding_cursor.pos().y;
    rect.pos.x -= rect.size.x/2; 
    rect.pos.y -= rect.size.y/2;
    connect1.x = rect.pos.x; connect1.y = rect.pos.y;
    connect2.x = rect.pos.x; connect2.y = rect.pos.y;
    origin = null;
    origin_co.x = 0;
    origin_co.y = 0;
    generation = 1;
    space = com().spacing_min.get();
    rotation = -0.008F;
    col = 0;
    age = 0;
    return this;
  }
  void define_bis(Box b2, float x, float y, String dir) {
    rect.pos.x = x; rect.pos.y = y;
    for (Entity e : com().list) if (e.active) {
      Box b = (Box)e;
      if (b != this && Rect.rectCollide(rect, b.rect, com().spacing_min.get()/2)) {//-2
        this.destroy(); return; } }
    origin = b2;
    generation = b2.generation + 1;
    float corner_space = com().corner_space.get();
    if (dir.charAt(0) == 'v') {
      if (dir.charAt(1) == 'u') {
        connect1.x = com().app.random(rect.pos.x + corner_space, rect.pos.x + rect.size.x - (2*corner_space));
        connect1.y = rect.pos.y + rect.size.y;
        connect2.x = com().app.random(b2.rect.pos.x + corner_space, b2.rect.pos.x + b2.rect.size.x - (2*corner_space));
        connect2.y = b2.rect.pos.y;
      } else {
        connect1.x = com().app.random(rect.pos.x + corner_space, rect.pos.x + rect.size.x - (2*corner_space));
        connect1.y = rect.pos.y;
        connect2.x = com().app.random(b2.rect.pos.x + corner_space, b2.rect.pos.x + b2.rect.size.x - (2*corner_space));
        connect2.y = b2.rect.pos.y + b2.rect.size.y;
      }
    } else {
      if (dir.charAt(1) == 'l') {
        connect1.y = com().app.random(rect.pos.y + corner_space, rect.pos.y + rect.size.y - (2*corner_space));
        connect1.x = rect.pos.x + rect.size.x;
        connect2.y = com().app.random(b2.rect.pos.y + corner_space, b2.rect.pos.y + b2.rect.size.y - (2*corner_space));
        connect2.x = b2.rect.pos.x;
      } else {
        connect1.y = com().app.random(rect.pos.y + corner_space, rect.pos.y + rect.size.y - (2*corner_space));
        connect1.x = rect.pos.x;
        connect2.y = com().app.random(b2.rect.pos.y + corner_space, b2.rect.pos.y + b2.rect.size.y - (2*corner_space));
        connect2.x = b2.rect.pos.x + b2.rect.size.x;
      }
    }
    origin_co.x = connect2.x - origin.rect.pos.x;
    origin_co.y = connect2.y - origin.rect.pos.y; //origin box to ext co
    //PVector connect_line = new PVector(connect1.x - connect2.x, connect1.y - connect2.y); //ext co to self co
    
    rotation = 0;//.008 * (6000 - connect_line.mag()) / 6000;
    //PVector box_local = new PVector(rect.pos.x - connect1.x, rect.pos.y - connect1.y); //self co to box pos
    //connect_line.rotate(rotation + burst);
    //connect1.x = connect_line.x + connect2.x;
    //connect1.y = connect_line.y + connect2.y;
    //rect.pos.x = box_local.x + connect1.x;
    //rect.pos.y = box_local.y + connect1.y;
  }
  
  Box define(Box b2) {
    space = com().spacing_min.get() + 
            ( 2 * com().spacing_max.get() * PApplet.min(1, b2.rect.pos.mag()
            / com().spacing_max_dist.get()) ) * com().app.crandom(com().spacing_diff.get());
    //space = crandom( com().spacing_min.get(), 
    //                 com().spacing_max.get(), 
    //                 ( min(0, com().spacing_max_dist.get() - b2.rect.pos.mag()) / com().spacing_max_dist.get()) * com().spacing_diff.get() );
    rect.size.x = com().app.random(com().box_size_min.get(), com().box_size_max.get()); 
    rect.size.y = com().app.random(com().box_size_min.get(), com().box_size_max.get());
    boolean axe = com().app.random(10) < 5;
    float dir_mod = 0;
    if (axe && b2.rect.pos.y > 0) dir_mod = (float) -2.5;
    if (axe && b2.rect.pos.y < 0) dir_mod = (float) 2.5;
    if (!axe && b2.rect.pos.x > 0) dir_mod = (float) -2.5;
    if (!axe && b2.rect.pos.x < 0) dir_mod = (float) 2.5;
    boolean side = com().app.random(10) < 5 + dir_mod;
    if (axe) {
      if (side) {
        define_bis(b2, b2.rect.pos.x - rect.size.x - space + com().app.random(b2.rect.size.x + rect.size.x + 2*space), 
                       b2.rect.pos.y - (rect.size.y + space), "vu"); }
      else {
        define_bis(b2, b2.rect.pos.x - rect.size.x - space + com().app.random(b2.rect.size.x + rect.size.x + 2*space),
                       b2.rect.pos.y + b2.rect.size.y + space, "vd"); } }
    else {
      if (side) {  
        define_bis(b2, b2.rect.pos.x - (rect.size.x + space),
                       b2.rect.pos.y - rect.size.y - space + com().app.random(b2.rect.size.y + rect.size.y + 2*space), "hl"); }
      else {                 
        define_bis(b2, b2.rect.pos.x + b2.rect.size.x + space,
                       b2.rect.pos.y - rect.size.y - space + com().app.random(b2.rect.size.y + rect.size.y + 2*space), "hr"); } }
    return this;
  }
  
  float rotation = (float) -0.008;
  int col = 0;
  float burst = 0;
  boolean blocked = false;
  
  Box frame() { return this; }
  Box tick() {
    age++;
    if (age > com().max_age.get()) this.destroy();
    
    if (com().floc_kill.get() && com().fcom != null) for (Entity e : com().fcom.list) if (e.active) {
      Floc f = (Floc)e;
      if (Rect.rectCollide(f.pos, rect)) {
        this.destroy();
      }
    }
    if (com().kill_grow.get() && com().gcom != null) for (Entity e : com().gcom.list) if (e.active) {
      Grower f = (Grower)e;
      if (Rect.rectCollide(f.pos, rect)) {
        f.destroy();
        //lose hp ^^
        generation--;
        if (generation < 0) this.destroy();
      }
    }
    
    if (com().app.random(100) < com().duplicate_prob.get()) {
      Box nb = com().newEntity();
      if (nb != null) {
        nb.define(this); } }
    
    float rspeed = (float) (0.008 / generation);
    int pcol = col;
    col = 0;
    for (Entity e : com().list) if (e.active) {
      Box b = (Box)e;
      //if (col >= 1) { rotation = 0; }
      if (b != this && Rect.rectCollide(rect, b.rect, com().spacing_min.get()/2)) {//-2
        //if (col > 0 && !blocked) rotation *= 1.01;
        if (col == 0 && !blocked) rotation *= -1;
        col += 1;
        //if (col == 0 && abs(rotation) > rspeed*2) rotation = 0;
      } }
    //if (blocked) rotation -= 0.00001;
    //if (abs(rotation) > rspeed*2) { blocked = true; burst = 0.1; if (rotation < 0) burst *= -1; rotation = 0;  }
    //if (col == 0 && abs(rotation) > rspeed) rotation /= 1.01;
    if (pcol == 0) blocked = false;
    //if (blocked && rotation == 0) rotation = rspeed;
    //println(com().comList.tick.get() + " " + col + " " + rotation);
    
    PVector connect_line = new PVector(connect1.x - connect2.x, connect1.y - connect2.y); //ext co to self co
    if (origin != null && origin.active) {
      //connect2.x = origin.rect.pos.x + origin_co.x;
      //connect2.y = origin.rect.pos.y + origin_co.y;
      //PVector box_local = new PVector(rect.pos.x - connect1.x, rect.pos.y - connect1.y); //self co to box pos
      ////connect_line.rotate(rotation + burst);
      //connect1.x = connect_line.x + connect2.x;
      //connect1.y = connect_line.y + connect2.y;
      //rect.pos.x = box_local.x + connect1.x;
      //rect.pos.y = box_local.y + connect1.y;
      
      //burst /= 1.01;
    }
    return this; }
  
  Box draw() {
    float connect_bubble_size = com().corner_space.get();
    
    float rd = (float) (255.0 * (float)((10.0 - 
    		(float)(PApplet.abs(generation - (int)(com().cnt/60.0)))) / 10.0));
    float stroke_limit = 1;
    if (rd <= stroke_limit) rd = (float) (255.0 * (float)((10.0 - 
    		(float)(PApplet.abs(generation - (int)((com().cnt+1200)/60.0)))) / 10.0));
    if (rd <= stroke_limit) rd = (float) (255.0 * (float)((10.0 - 
    		(float)(PApplet.abs(generation - (int)((com().cnt-1200)/60.0)))) / 10.0));
    if (rd <= stroke_limit) rd = (float) (255.0 * (float)((10.0 - 
    		(float)(PApplet.abs(generation - (int)((com().cnt+2400)/60.0)))) / 10.0));
    if (rd <= stroke_limit) rd = (float) (255.0 * (float)((10.0 - 
    		(float)(PApplet.abs(generation - (int)((com().cnt-2400)/60.0)))) / 10.0));
//    if (PApplet.abs(generation - (int)(com().cnt/60)) < 10) 
    float c1f = (float)(PApplet.max(100, (int)(rd-20)))/255.0F;
    int filling = com().app.color( com().val_col1.getred()*c1f, 
                           com().val_col1.getgreen()*c1f, 
                           com().val_col1.getblue()*c1f );
    float fc = PApplet.max( 150, 255 - PApplet.max(0, (int)(rd)) ) / 255.0F;
    int lining = com().app.color( com().val_col2.getred()*fc, 
                           com().val_col2.getgreen()*fc, 
                           com().val_col2.getblue()*fc );
    //println(lining);
    boolean constent_w = com().val_const_line.get();
    float line_factor = com().val_line_w.get();
    
    com().app.noFill();
    com().app.stroke(lining);
    if (constent_w) com().app.strokeWeight(PApplet.max(2/com.sim.cam_gui.scale, (float) (connect_bubble_size/1.3)));
    else com().app.strokeWeight(PApplet.max(line_factor*2, (float) (connect_bubble_size/1.3)));
    com().app.line(connect1.x, connect1.y, connect2.x, connect2.y);
    if (connect_bubble_size*com.sim.cam_gui.scale > 3) {
    	com().app.fill(filling);
    	com().app.stroke(lining);
      if (constent_w) com().app.strokeWeight(4/com.sim.cam_gui.scale);
      else com().app.strokeWeight(4*line_factor);
      com().app.ellipse(connect1.x, connect1.y, connect_bubble_size, connect_bubble_size);
      com().app.ellipse(connect2.x, connect2.y, connect_bubble_size, connect_bubble_size); }
    com().app.fill(filling);
    com().app.stroke(lining);
    if (constent_w) com().app.strokeWeight(2/com.sim.cam_gui.scale);
    else com().app.strokeWeight(2*line_factor);
    rect.draw(com().app);
    com().app.noFill();
    com().app.stroke(0, 255, 0);
    if (constent_w) com().app.strokeWeight(3/com.sim.cam_gui.scale);
    else com().app.strokeWeight(3*line_factor);
    //rect(rect.pos.x - space/2, rect.pos.y - space/2, rect.size.x + space, rect.size.y + space);
    if (connect_bubble_size*com.sim.cam_gui.scale > 3) {
    	com().app.fill(filling);
    	com().app.noStroke();
    	com().app.ellipse(connect1.x, connect1.y, connect_bubble_size, connect_bubble_size);
    	com().app.ellipse(connect2.x, connect2.y, connect_bubble_size, connect_bubble_size); }
    com().app.noFill();
    com().app.stroke(filling);
    if (constent_w) com().app.strokeWeight(com().app.max(0, (int) (connect_bubble_size/1.3 - 4/com.sim.cam_gui.scale)));
    else com().app.strokeWeight(com().app.max(0, (int) (connect_bubble_size/1.3 - 4*line_factor)));
    com().app.line(connect1.x, connect1.y, connect2.x, connect2.y);
    if (com().draw_dot.get()) {
      int point_size = (int)(com().box_point_size.get());
      int c = 0;
      int c3 = com().val_col3.get();
      com().app.strokeWeight(point_size);
      for (float i = rect.pos.x + (rect.size.x%point_size)/2 + point_size/2; i < rect.pos.x + rect.size.x ; i += point_size) 
        for (float j = rect.pos.y + (rect.size.y%point_size)/2 + point_size/2; j < rect.pos.y + rect.size.y ; j += point_size) {
        	com().app.noStroke();
        	  com().app.fill(com().app.red(c3), com().app.green(c3), com().app.blue(c3), c);
          com().app.ellipseMode(PApplet.CENTER);
          com().app.ellipse(i, j, point_size/2, point_size/2);
          c+=(generation*(int)(point_size));
          c %= 255;
        }
    }
    com().app.fill(lining);
    com().app.textFont(com().app.getFont((int)(rect.size.y/3)));
    com().app.text(""+generation, rect.pos.x + rect.size.x/3, rect.pos.y + rect.size.y/1.41F);
    return this; }
  Box clear() { return this; }
  BoxComu com() { return ((BoxComu)com); }
}










   
