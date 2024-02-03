package Specialize;

import java.util.ArrayList;

import Macro.Macro_Sheet;
import Macro.Sheet_Specialize;
import RBase.RConst;
import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nList;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sRun;
import sData.sStr;
import sData.sValueBloc;
import sData.sVec;



//#######################################################################
//##                              CANVAS                               ##
//#######################################################################




public class Canvas extends Macro_Sheet {
	  
	public static class CanvasPrint extends Sheet_Specialize {
		  Simulation sim;
		  public CanvasPrint(Simulation s) { super("Canvas"); sim = s; }
		  public Canvas get_new(Macro_Sheet s, String n, sValueBloc b) { return new Canvas(sim, b); }
		}
	
	  ArrayList<Face> faces = new ArrayList<Face>();
	  
	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Community");
	    tab.getShelf()
	      .addDrawer(10.25, 0.75)
	      .addModel("Label-S4", "-Canvas Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(val_show, val_show_bound, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(val_rst_run, val_show_grab, 10, 1)
	      .addSeparator(0.125)
	      ;
	      
	    selector_list = tab.getShelf(0)
	      .addSeparator(0.25)
	      .addList(4, 10, 1);
	    selector_list.addEventChange_Builder(new nRunnable() { public void run() {
	      nList sl = ((nList)builder); 
	      //logln("a "+sl.last_choice_index +"  "+ sim.list.size());
	      if (sl.last_choice_index < sim.list.size()) 
	        selected_comu(sim.list.get(sl.last_choice_index));
	        //selected_com.set(sim.list.get(sl.last_choice_index).name);
	    } } );
	    
	    selector_list.getShelf()
	      .addSeparator(0.125)
	      .addDrawer(10.25, 0.75)
	      .addWatcherModel("Label-S4", "Selected: ").setLinkedValue(selected_com).getShelf()
	      .addSeparator(0.125)
	      ;
	    
	    selector_entry = new ArrayList<String>(); // mmain().data.getCountOfType("flt")
	    selector_value = new ArrayList<Community>(); // mmain().data.getCountOfType("flt")
	    
	    update_com_selector_list();
	    
	    sheet_front.toLayerTop();
	  }
	  void update_com_selector_list() {
	    selector_entry.clear();
	    selector_value.clear();
	    for (Community v : sim.list) { 
	      selector_entry.add(v.name); 
	      selector_value.add(v);
	    }
	    if (selector_list != null) selector_list.setEntrys(selector_entry);
	  }
	  
	  void selected_comu(Community c) { 
//	    if (c != null && c.type_value.get().equals("floc")) { fcom = (FlocComu)c; selected_com.set(fcom.name); }
	  }
	  
	//  public FlocComu fcom;
	  
	  ArrayList<String> selector_entry;
	  ArrayList<Community> selector_value;
	  Community selected_value;
	  String selected_entry;
	  nList selector_list;
	  
	  Simulation sim;
	  
	  nRunnable tick_run, rst_run; Drawable cam_draw;
	  nCursor ref_cursor;
	  
	  sVec val_pos;
	  sInt val_w, val_h, can_div;
	  sFlt val_scale, color_keep_thresh, val_decay;
	  sBoo val_show, val_show_bound, val_show_grab;
	  sStr selected_com;
	  sCol val_col_back;
	  sRun val_rst_run;
	  
	  //nLinkedWidget canvas_grabber;
	  
	  PImage can1,can2;
	  int active_can = 0;
	  int can_st;
	  
	  Canvas(Simulation m, sValueBloc b) { 
	    super(m.inter.macro_main, "Canvas", b);
	    sim = m;
	    
	    int def_pix_size = 10;
	    val_pos = newVec("val_pos", "val_pos");
	    val_w = menuIntIncr(gui.app.width / def_pix_size, 100, "val_w");
	    val_h = menuIntIncr(gui.app.height / def_pix_size, 100, "val_h");
	    can_div = menuIntIncr(4, 1, "can_div");
	    val_scale = menuFltSlide(def_pix_size, 10, 500, "val_scale");
	    color_keep_thresh = menuFltSlide(200, 10, 260, "clrkeep_thresh");
	    val_decay = menuFltSlide(1, 0.99F, 1.01F, "decay");
	    val_show = newBoo(true, "val_show", "show_canvas");
	    val_show_bound = newBoo(true, "val_show_bound", "show_bound");
	    val_show_grab = newBoo(true, "val_show_grab", "show_grab");
	    selected_com = newStr("selected_com", "scom", "");
	    val_col_back = menuColor(gui.app.color(0), "background");
	    val_col_back.addEventChange(new nRunnable() { public void run() { 
	      reset();
	    } });
	    
	    can_st = can_div.get()-1;
	    can_div.addEventChange(new nRunnable() { public void run() { 
	      reset();
	    } });
	    
	    val_show_grab.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.show != null) ref_cursor.show.set(val_show.get() && val_show_grab.get());
	    } });
	    val_show.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.show != null) ref_cursor.show.set(val_show.get() && val_show_grab.get());
	    } });
	    
	    ref_cursor = menuCursor("Canvas", false);
	    if (ref_cursor.show != null) ref_cursor.show.set(val_show.get() && val_show_grab.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.addEventChange(new nRunnable() { public void run() { 
	      val_pos.set(ref_cursor.pval.get()); }});
	    val_pos.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get()); }});
	    
	    tick_run = new nRunnable() { public void run() { tick(); } };
	    rst_run = new nRunnable() { public void run() { reset(); } };
	    cam_draw = new Drawable() { public void drawing() { 
	      drawCanvas(); } };
	    val_rst_run = newRun("val_rst_run", "rst_run", rst_run);
	    
	    val_w.addEventChange(rst_run);
	    val_h.addEventChange(rst_run);
	    
	    if (sim != null) sim.addEventTick2(tick_run);
	    if (sim != null) sim.inter.addToCamDrawerPile(cam_draw);
	    if (sim != null) sim.addEventReset(rst_run);
	    //if (sim != null) sim.reset();
	    reset();
	    
	    addEventSetupLoad(new nRunnable() { public void run() { 
	      sim.inter.addEventNextFrame(new nRunnable() {public void run() { 
	        for (Community c : sim.list) if (c.name.equals(selected_com.get())) selected_comu(c);
	        cam_draw.toLayerBottom();
	      }}); } } );
	  }
	  
	  void reset() {
	    can1 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.RGB);
	    init_pim(can1);
	    can2 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.RGB);
	    init_pim(can2);
	    can_st = can_div.get();
	    active_can = 0;
	  }
	  
	  public Canvas clear() {
	    //sim.removeEventTick(tick_run);
	    //sim.removeEventReset(rst_run);
	    //cam_draw.clear();
	    super.clear();
	    return this;
	  }
	  
	  private void init_pim(PImage canvas) {
	    for(int i = 0; i < canvas.pixels.length; i++) {
	      canvas.pixels[i] = val_col_back.get(); 
	    }
	  }
	  
	  float sat(int c) {
	    return (gui.app.red(c) + gui.app.green(c) + gui.app.blue(c)) / 3; 
	  }
	  
	  int decay(int c) {
	    return gui.app.color(gui.app.red(c)*val_decay.get(), 
	    		             gui.app.green(c)*val_decay.get(), 
	    		             gui.app.blue(c)*val_decay.get()); 
	  }
	  
	  private void clear_pim(PImage canvas) {
	    for (int i = 0 ; i < canvas.pixels.length ; i++) {
	      if (sat(canvas.pixels[i]) < color_keep_thresh.get()) canvas.pixels[i] = val_col_back.get(); 
	      else canvas.pixels[i] = decay(canvas.pixels[i]);
	    }
	  }
	  private void med_pim(PImage canvas1, PImage canvas2) {
	    for (int i = 0 ; i < canvas1.pixels.length ; i++) {
	      int c = gui.app.color( (gui.app.red(canvas1.pixels[i]) + gui.app.red(canvas2.pixels[i])) / 2.0F, 
	                       (gui.app.green(canvas1.pixels[i]) + gui.app.green(canvas2.pixels[i])) / 2.0F, 
	                       (gui.app.blue(canvas1.pixels[i]) + gui.app.blue(canvas2.pixels[i])) / 2.0F );
	      canvas1.pixels[i] = c;
	      canvas2.pixels[i] = c;
	    }
	  }
	  
	  void tick() {
//	    if (fcom != null) {
//	      for (int i = can_st ; i < fcom.list.size() ; i += Math.max(1, can_div.get()) )
//	        if (fcom.list.get(i).active) {
//	          ((Floc)fcom.list.get(i)).draw_halo(this);
//	      }
//	    }
	    
	    for (Face f : faces) f.tick();
	    
	    if (active_can == 0) {
	      if (can_st <= 0) {
	        active_can = 1;
	        med_pim(can1, can2);
	        clear_pim(can1);
	        can_st = can_div.get();
	      } else can_st--;
	    }
	    else if (active_can == 1) {
	      if (can_st <= 0) {
	        active_can = 0;
	        med_pim(can1, can2);
	        clear_pim(can2);
	        can_st = can_div.get();
	      } else can_st--;
	    }
	  }
	  
	  void drawCanvas() {
	    if (val_show_bound.get()) {

	    	gui.app.stroke(180);
	    	gui.app.strokeWeight(ref_size / (10 * mmain().gui.scale) );
	    	gui.app.noFill();
	    	gui.app.rect(val_pos.get().x, val_pos.get().y, val_w.get() * val_scale.get(), val_h.get() * val_scale.get());
	    }
	    if (val_show.get()) {
	      if (active_can == 0) draw(can1);
	      else if (active_can == 1) draw(can2);
	    }
	    
	    //done in sim
	    //if (faces.size() > 0) {
	    //  int min = faces.get(0).val_draw_layer.get(), max = min;
	    //  for (Face f : faces) {
	    //    min = min(min, f.val_draw_layer.get()); max = max(max, f.val_draw_layer.get()); }
	    //  for (int i = min ; i <= max ; i++)
	    //  for (Face f : faces) if (f.val_draw_layer.get() == i) f.draw();
	    //}
	  }
	  
	  void draw(PImage canvas) {
	    canvas.updatePixels();
	    gui.app.pushMatrix();
	    gui.app.translate(val_pos.get().x, val_pos.get().y);
	    gui.app.scale(val_scale.get());
	    gui.app.image(canvas, 0, 0);
	    gui.app.popMatrix();
	  }
	  
	  void draw_shape_fill(nBase sh, float halo_size, float halo_density, int c) {
	    for (float px = (int)(sh.pos.x - sh.rad() - halo_size) ; 
	         px < (int)(sh.pos.x + sh.rad() + halo_size) ; px+=val_scale.get())
	      for (float py = (int)(sh.pos.y - sh.rad() - halo_size) ; 
	           py < (int)(sh.pos.y + sh.rad() + halo_size) ; py+=val_scale.get()) {
	      PVector p = new PVector(px, py);
	      float l1 = RConst.distancePointToLine(px, py, sh.p1().x, sh.p1().y, sh.p2().x, sh.p2().y);
	      float l2 = RConst.distancePointToLine(px, py, sh.p3().x, sh.p3().y, sh.p2().x, sh.p2().y);
	      float l3 = RConst.distancePointToLine(px, py, sh.p1().x, sh.p1().y, sh.p3().x, sh.p3().y);
	      float m = Math.min(Math.min(l1, l2), l3);
	      if (RConst.point_in_trig(sh.p1(), sh.p2(), sh.p3(), p)) m = 0;
	      if (m < halo_size) { //get and try distence of current pix
	        //the color to add to the current pix is function of his distence to the center
	        //the decreasing of the quantity of color to add is soothed
	        float a = (halo_density) * RConst.soothedcurve(1.0F, m / halo_size);
	        if (active_can == 0) addpix(can2, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	        if (active_can == 1) addpix(can1, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	      }
	    }
	  }
	  void draw_shape_line(nBase sh, float halo_size, float halo_density, int c) {
	    for (float px = (int)(sh.pos.x - sh.rad() - halo_size) ; 
	         px < (int)(sh.pos.x + sh.rad() + halo_size) ; px+=val_scale.get())
	      for (float py = (int)(sh.pos.y - sh.rad() - halo_size) ; 
	           py < (int)(sh.pos.y + sh.rad() + halo_size) ; py+=val_scale.get()) {
	      
	      float l1 = RConst.distancePointToLine(px, py, sh.p1().x, sh.p1().y, sh.p2().x, sh.p2().y);
	      float l2 = RConst.distancePointToLine(px, py, sh.p3().x, sh.p3().y, sh.p2().x, sh.p2().y);
	      float l3 = RConst.distancePointToLine(px, py, sh.p1().x, sh.p1().y, sh.p3().x, sh.p3().y);
	      float m = Math.min(Math.min(l1, l2), l3);
	      if (m < halo_size) { //get and try distence of current pix
	        //the color to add to the current pix is function of his distence to the center
	        //the decreasing of the quantity of color to add is soothed
	        float a = (halo_density) * RConst.soothedcurve(1.0F, m / halo_size);
	        if (active_can == 0) addpix(can2, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	        if (active_can == 1) addpix(can1, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	      }
	    }
	  }
	  
	  void draw_halo(PVector pos, float halo_size, float halo_density, int c) {
	    //walk a box of pix around entity containing the halo (pos +/- halo radius)
	    for (float px = (int)(pos.x - halo_size) ; px < (int)(pos.x + halo_size) ; px+=val_scale.get())
	      for (float py = (int)(pos.y - halo_size) ; py < (int)(pos.y + halo_size) ; py+=val_scale.get()) {
	        PVector m = new PVector(pos.x - px, pos.y - py);
	        if (m.mag() < halo_size) { //get and try distence of current pix
	          //the color to add to the current pix is function of his distence to the center
	          //the decreasing of the quantity of color to add is soothed
	          float a = (halo_density) * RConst.soothedcurve(1.0F, m.mag() / halo_size);
	          if (active_can == 0) addpix(can2, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	          if (active_can == 1) addpix(can1, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	        }
	    }
	  }
	  
	  void addpix(PImage canvas, float x, float y, int nc) {
	    //x -= int(val_scale.get() / 2);
	    //y -= int(val_scale.get() / 2);
	    x -= val_pos.get().x;
	    y -= val_pos.get().y;
	    x /= val_scale.get();
	    y /= val_scale.get();
	    //x += 1 / val_scale.get();
	    //y += 1 / val_scale.get();
	    if (x < 0 || y < 0 || x > canvas.width || y > canvas.height) return;
	    int pi = canvas.width * (int)(y) + (int)(x);
	    if (pi >= 0 && pi < canvas.pixels.length) {
	      int oc = canvas.pixels[pi];
	      canvas.pixels[pi] = gui.app.color(gui.app.min(255, gui.app.max(gui.app.red(oc), gui.app.red(nc))), 
					    		            gui.app.min(255, gui.app.max(gui.app.green(oc), gui.app.green(nc))), 
					    		            gui.app.min(255, gui.app.max(gui.app.blue(oc), gui.app.blue(nc))) );
	    }
	  }
	  //color getpix(PImage canvas, PVector v) { return getpix(canvas, v.x, v.y); }
	  //color getpix(PImage canvas, float x, float y) {
	  //  color co = 0;
	  //  int pi = canvas.width * int(y + canvas.height / 2) + int(x + canvas.width/2);
	  //  if (pi >= 0 && pi < canvas.pixels.length) {
	  //    co = canvas.pixels[pi];
	  //  }
	  //  return co;
	  //}
	  //void setpix(PImage canvas, PVector v, color c) { setpix(canvas, v.x, v.y, c); }
	  //void setpix(PImage canvas, float x, float y, color c) {
	  //  int pi = canvas.width * int(y + canvas.height / 2) + int(x + canvas.width/2);
	  //  if (pi >= 0 && pi < canvas.pixels.length) {
	  //    canvas.pixels[pi] = c;
	  //  }
	  //}
	  
	  //void canvas_croix(PImage canvas, float x, float y, int c) {
	  //  color co = getpix(canvas, x, y);
	  //  setpix(canvas, x, y, color(c + red(co)) );
	  //  setpix(canvas, x + 1, y, color(c/2 + red(co)) );
	  //  setpix(canvas, x - 1, y, color(c/2 + red(co)) );
	  //  setpix(canvas, x, y + 1, color(c/2 + red(co)) );
	  //  setpix(canvas, x, y - 1, color(c/2 + red(co)) );
	  //}
	  
	  //void canvas_line(PImage canvas, PVector v1, PVector v2, int c) {
	  //  PVector m = new PVector(v1.x - v2.x, v1.y - v2.y);
	  //  int l = int(m.mag());
	  //  m.setMag(-1);
	  //  PVector p = new PVector(v1.x, v1.y);
	  //  for (int i = 0 ; i < l ; i++) {
	  //    color co = getpix(canvas, p.x, p.y);
	  //    setpix(canvas, p.x, p.y, color(c + red(co)) );
	  //    p.add(m);
	  //  }
	  //}
	}
