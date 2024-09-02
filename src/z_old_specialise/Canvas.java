package z_old_specialise;

import java.util.ArrayList;

import Macro.Macro_Main;
import Macro.Macro_Sheet;
import RApplet.RConst;
import RApplet.Rapp;
import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nList;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import sData.*;



//#######################################################################
//##                              CANVAS                               ##
//#######################################################################




public class Canvas extends Macro_Sheet {
	  
	public static class CanvasPrint extends Sheet_Specialize {
		  Simulation sim;
		  public CanvasPrint(Simulation s) { super("Canvas"); sim = s; }
		  public CanvasPrint() { super("Canvas"); }
		  public void default_build() { }
		  public Canvas get_new(Macro_Sheet s, String n, sValueBloc b) { return new Canvas(sim, b); }
		}
	
	  ArrayList<Face> faces = new ArrayList<Face>();
	  
	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Community");
	    tab.getShelf()
	      .addDrawer(10.25, 0.75)
	      .addModel("Label-S4", "-Canvas Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      ;
	    sheet_front.getTab(1).getShelf()
	      .addDrawerTripleButton(back_clear, back_fill, back_add, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(back_save, back_load, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFieldCtrl(back_file, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(val_show, val_show_back, val_show_bound, 10, 1)
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
	        selected_com.set(sim.list.get(sl.last_choice_index).name);
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
	    
	    tab = sheet_front.addTab("Brush");
	    tab.getShelf()
	      .addDrawer(10.25, 0.75)
	      .addModel("Label-S4", "-Brush Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerTripleButton(do_rain1, do_rain2, do_rain3, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(rain_strength, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(rain_dir, 1, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(do_brush, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(brush_size, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(brush_dens, 2, 10, 1)
	      .addSeparator(0.125)
	      ;

	    if (sheet_front.collapsed) {
	    		sheet_front.popUp();
	    		sheet_front.collapse();
	    } else {
    			sheet_front.collapse();
    			sheet_front.popUp();
	    }
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
	    if (c != null && c.type_value.get().equals("floc")) { fcom = (FlocComu)c; selected_com.set(fcom.name); }
	  }
	  
	  public FlocComu fcom;
	  
	  ArrayList<String> selector_entry;
	  ArrayList<Community> selector_value;
	  Community selected_value;
	  String selected_entry;
	  nList selector_list;
	  
	  nRunnable tick_run, rst_run; Drawable cam_draw;
	  nCursor ref_cursor;
	  
	  sVec val_pos;
	  sInt val_w, val_h, can_div, rate_decay;
	  sFlt val_scale, color_keep_thresh, decay_fact;
	  sBoo val_show, val_show_back, val_show_bound, val_show_grab;
	  sStr selected_com;
	  sCol val_col_back;
	  sRun val_rst_run;

	  sObj floc_obj;
	  
	  //nLinkedWidget canvas_grabber;
	  
	  PImage can1,can2;
	  int active_can = 0;
	  int can_st;
	  
	  Simulation sim;
	  
	  PImage back;
	  sRun back_clear, back_add, back_fill, back_save, back_load;
	  sStr back_file;
	  
	  sBoo do_rain1, do_rain2, do_rain3, do_brush;
	  sFlt rain_strength, brush_size, brush_dens;
	  sInt rain_dir;
	  sCol val_col_brush;
	  
	  
	  Canvas(Simulation m, sValueBloc b) { 
	    super(m.mmain(), "Canvas", b);
	    sim = m;
	    int def_pix_size = 10;
	    val_pos = newVec("val_pos", "val_pos");
	    val_w = menuIntIncr(gui.app.width / def_pix_size, 100, "val_w");
	    val_h = menuIntIncr(gui.app.height / def_pix_size, 100, "val_h");
	    can_div = menuIntIncr(4, 1, "can_div");
	    val_scale = menuFltSlide(def_pix_size, 2, 80, "val_scale");
	    color_keep_thresh = menuFltSlide(200, 0, 260, "clrkeep_thresh");
	    decay_fact = menuFltSlide(1, 0.9F, 1.0F, "decay_fact");
	    rate_decay = menuIntIncr(100, 10, "rate_decay");
	    val_show = newBoo(false, "val_show", "show_canvas");
	    val_show_back = newBoo(false, "val_show_back", "val_show_back");
	    val_show_bound = newBoo(false, "val_show_bound", "show_bound");
	    val_show_grab = newBoo(false, "val_show_grab", "show_grab");
	    selected_com = newStr("selected_com", "scom", "");
	    val_col_back = menuColor(gui.app.color(0), "background");
	    val_col_back.addEventChange(new nRunnable() { public void run() { reset(); } });
	    do_rain1 = newBoo(false, "do_rain1", "do_rain1");
	    do_rain2 = newBoo(false, "do_rain2", "do_rain2");
	    do_rain3 = newBoo(false, "do_rain3", "do_rain3");
	    rain_strength = newFlt(1, "rain_strength", "rain_strength");
	    rain_dir = newInt(0, "rain_dir", "rain_dir");
	    do_brush = newBoo(false, "do_brush", "do_brush");
	    brush_size = newFlt(30, "brush_size", "brush_size");
	    brush_dens = newFlt(1, "brush_dens", "brush_dens");
	    val_col_brush = menuColor(gui.app.color(255, 0, 0), "val_col_brush");
	    
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
	    
	    ref_cursor = menuSheetCursor("Canvas", false);
	    
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

	    back_clear = newRun("back_clear", "back_clear", 
	    		new nRunnable() { public void run() { clearBack(); } });
	    back_fill = newRun("back_fill", "back_fill", 
	    		new nRunnable() { public void run() { fillBack(); } });
	    back_add = newRun("back_add", "back_add", 
	    		new nRunnable() { public void run() { addToBack(); } });

	    back_save = newRun("back_save", "back_save", 
	    		new nRunnable() { public void run() { saveBack(); } });
	    back_load = newRun("back_load", "back_load", 
	    		new nRunnable() { public void run() { loadBack(); } });
	    back_file = newStr("back_file", "back_file", "canvas");

	    val_w.addEventChange(back_clear.get());
	    val_h.addEventChange(back_clear.get());
	    val_w.addEventChange(rst_run);
	    val_h.addEventChange(rst_run);
	    
	    if (sim != null) sim.addEventTick2(tick_run);
	    if (sim != null) sim.inter.addToCamDrawerPile(cam_draw);
	    if (sim != null) sim.addEventReset(rst_run);
//	    if (sim != null) sim.reset();
	    
	    mmain().inter.addToCamDrawerPile(cam_draw);

	    floc_obj = newObj("floc_obj", "floc_obj");
	    floc_obj.addEventChange(new nRunnable() { public void run() {
	      if (floc_obj.isSheet()) {
	        Macro_Sheet ms = floc_obj.asSheet();
	        if (ms.specialize.get().equals("Floc")) fcom = (FlocComu)ms;
	      }
	    }});
	    
	    clearBack();
	    reset();
	    
	    addEventSetupLoad(new nRunnable() { public void run() { 
	      mmain().inter.addEventNextFrame(new nRunnable() {public void run() { 
	  	    clearBack();
	    	    reset();
	        for (Community c : sim.list) if (c.name.equals(selected_com.get())) selected_comu(c);
	        cam_draw.toLayerBottom();
	      }}); } } );
	  }
	  
	  void reset() {
	    can1 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.ARGB);
	    init_pim(can1);
	    can2 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.ARGB);
	    init_pim(can2);
	    can_st = can_div.get();
	    active_can = 0;
	  }
	  
	  private void init_pim(PImage canvas) {
	    for(int i = 0; i < canvas.pixels.length; i++) {
	      canvas.pixels[i] = val_col_back.get(); 
	    }
	  }

	  void saveBack() {
	    back.save("image/"+back_file.get()+".tif");
	  }
	  void loadBack() {
		  PImage t = gui.app.loadImage("image/"+back_file.get()+".tif");
		  if (t != null)
		  	for(int i = 0; i < back.pixels.length && i < t.pixels.length ; i++) {
			  back.pixels[i] = t.pixels[i];
		  }
	  }
	  void clearBack() {
	    back = gui.app.createImage(val_w.get(), val_h.get(), PConstants.ARGB);
	    for(int i = 0; i < back.pixels.length; i++) {
	    		back.pixels[i] = gui.app.color(0); 
	    }
	  }
	  void fillBack() {
	    back = gui.app.createImage(val_w.get(), val_h.get(), PConstants.ARGB);
	    for(int i = 0; i < back.pixels.length; i++) {
	    		back.pixels[i] = gui.app.color(0);  
	    }
	  }
	  void addToBack() {
		  PImage t = null;
	      if (active_can == 0) t = can1;
	      else if (active_can == 1) t = can2;
		  for(int i = 0; i < back.pixels.length; i++) {
			  float a = gui.app.alpha(t.pixels[i]) / 255.0F;
			  back.pixels[i] = gui.app.color(
					  			gui.app.red(back.pixels[i]) + gui.app.red(t.pixels[i]) * a, 
 		             			gui.app.green(back.pixels[i]) + gui.app.green(t.pixels[i]) * a, 
 		             			gui.app.blue(back.pixels[i]) + gui.app.blue(t.pixels[i]) * a
 		             		); 
		  }
	  }
	  
	  public Canvas clear() {
//	    sim.removeEventTick(tick_run);
//	    sim.removeEventReset(rst_run);
//	    cam_draw.clear();
//		fcom = null;
	    super.clear();
	    return this;
	  }
	  
	  float sat(int c) {
	    return (gui.app.alpha(c) / 255.0F) * 
	    		(gui.app.red(c) + gui.app.green(c) + gui.app.blue(c)) / 3.0F; 
	  }
	  
	  int decay(int c) {
	    return gui.app.color(1.0F*gui.app.red(c)*decay_fact.get(), 
	    					1.0F*gui.app.green(c)*decay_fact.get(), 
	    					1.0F*gui.app.blue(c)*decay_fact.get(), 
	    		             gui.app.alpha(c));//*val_decay.get()); 
	  }
//	  int slowdecay(int c) {
//	    return gui.app.color(gui.app.red(c)*slow_decay.get(), 
//	    		             gui.app.green(c)*slow_decay.get(), 
//	    		             gui.app.blue(c)*slow_decay.get(), 
//	    		             gui.app.alpha(c)*slow_decay.get()); 
//	  }
	  int dc_cnt = 0;
	  private void clear_pim(PImage canvas) {
  	  	dc_cnt++;
  	  	if (dc_cnt >= rate_decay.get()) {
  	  		dc_cnt = 0;
  	  	}
	    for (int i = 0 ; i < canvas.pixels.length ; i++) {
	      if (sat(canvas.pixels[i]) < color_keep_thresh.get()) canvas.pixels[i] = val_col_back.get(); 
	      else {
	    	  	if (dc_cnt == 0) {
	    	  		canvas.pixels[i] = decay(canvas.pixels[i]);
	    	  	}
	      }
	    }
	  }
	  private void med_pim(PImage canvas1, PImage canvas2) {
	    for (int i = 0 ; i < canvas1.pixels.length ; i++) {
	      int c = gui.app.color( (gui.app.red(canvas1.pixels[i]) + 
	    		  						gui.app.red(canvas2.pixels[i])) / 2.0F, 
	                       (gui.app.green(canvas1.pixels[i]) + 
	                    		   		gui.app.green(canvas2.pixels[i])) / 2.0F, 
	                       (gui.app.blue(canvas1.pixels[i]) + 
                   		   		gui.app.blue(canvas2.pixels[i])) / 2.0F , 
	                       (gui.app.alpha(canvas1.pixels[i]) + 
                   		   		gui.app.alpha(canvas2.pixels[i])) / 2.0F );
	      canvas1.pixels[i] = c;
	      canvas2.pixels[i] = c;
	    }
	  }
	  
	  void floc_tick(FlocComu f) {
	    if (f != null) {
	      for (int i = can_st ; i < f.list.size() ; i += Math.max(1, can_div.get()) )
	        if (f.list.get(i).active) {
	          ((Floc)f.list.get(i)).draw_halo(this);
	      }
	    }
	  }
	  
	  void rain(PImage can) {
		  for (int i = 0 ; i < can.pixels.length ; i++) {
			  if (do_rain1.get()) {
			  if (i+val_w.get() < can.pixels.length) {
				  if (sat(can.pixels[i]) - rain_strength.get() > 
			  	  sat(can.pixels[i+val_w.get()])) {
				  can.pixels[i] = gui.app.color(
						  gui.app.red(can.pixels[i]) - rain_strength.get()/3.0F, 
						  gui.app.green(can.pixels[i]) - rain_strength.get()/3.0F, 
						  gui.app.blue(can.pixels[i]) - rain_strength.get()/3.0F 
//						  gui.app.alpha(can.pixels[i]) - rain_strength.get()/4
						  );
				  int j = i+val_w.get();
				  can.pixels[j] = gui.app.color(
						  gui.app.red(can.pixels[j]) + rain_strength.get()/3.0F, 
						  gui.app.green(can.pixels[j]) + rain_strength.get()/3.0F, 
						  gui.app.blue(can.pixels[j]) + rain_strength.get()/3.0F 
//						  gui.app.alpha(can.pixels[j]) + rain_strength.get()/4
						  );
				  }
			  } 
			}
			if (do_rain2.get()) {
				if (i+val_w.get() < can.pixels.length) {
					if (gui.app.red(can.pixels[i]) - rain_strength.get() >=
						gui.app.red(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() && 
						gui.app.red(can.pixels[i]) - rain_strength.get() >= 0 && 
						gui.app.red(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() <= 255) {
					  can.pixels[i] = gui.app.color(
							  gui.app.red(can.pixels[i]) - rain_strength.get(), 
							  gui.app.green(can.pixels[i]), 
							  gui.app.blue(can.pixels[i]), 
							  gui.app.alpha(can.pixels[i]) );
					  int j = i+val_w.get();
					  can.pixels[j] = gui.app.color(
							  gui.app.red(can.pixels[j]) + rain_strength.get(), 
							  gui.app.green(can.pixels[j]), 
							  gui.app.blue(can.pixels[j]), 
							  gui.app.alpha(can.pixels[j]) );
					}  
					if (gui.app.green(can.pixels[i]) - rain_strength.get() >=
						gui.app.green(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() && 
						gui.app.green(can.pixels[i]) - rain_strength.get() >= 0 && 
						gui.app.green(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() <= 255) {
					  can.pixels[i] = gui.app.color(
							  gui.app.red(can.pixels[i]), 
							  gui.app.green(can.pixels[i]) - rain_strength.get(), 
							  gui.app.blue(can.pixels[i]), 
							  gui.app.alpha(can.pixels[i]) );
					  int j = i+val_w.get();
					  can.pixels[j] = gui.app.color(
							  gui.app.red(can.pixels[j]), 
							  gui.app.green(can.pixels[j]) + rain_strength.get(), 
							  gui.app.blue(can.pixels[j]), 
							  gui.app.alpha(can.pixels[j]) );
					}  
					if (gui.app.blue(can.pixels[i]) - rain_strength.get() >=
						gui.app.blue(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() && 
						gui.app.blue(can.pixels[i]) - rain_strength.get() >= 0 && 
						gui.app.blue(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() <= 255) {
					  can.pixels[i] = gui.app.color(
							  gui.app.red(can.pixels[i]), 
							  gui.app.green(can.pixels[i]), 
							  gui.app.blue(can.pixels[i]) - rain_strength.get(), 
							  gui.app.alpha(can.pixels[i]) );
					  int j = i+val_w.get();
					  can.pixels[j] = gui.app.color(
							  gui.app.red(can.pixels[j]), 
							  gui.app.green(can.pixels[j]), 
							  gui.app.blue(can.pixels[j]) + rain_strength.get(), 
							  gui.app.alpha(can.pixels[j]) );
					}  
				}
			}
		  }
		if (do_rain3.get()) {
			for (int i = 0 ; i < can.pixels.length ; i++) {
				if (i+val_w.get() < can.pixels.length) {
					transfer_pixel(can, can, i, i+val_w.get(), (int)rain_strength.get());
				}
			}
		}
	  }
	  void transfer_pixel(PImage from_can, PImage to_can, int from, int to, int amount) {
		  if (from_can.pixels.length != to_can.pixels.length) return;
		  if (from < 0 || from > from_can.pixels.length) return;
		  if (to < 0 || to > from_can.pixels.length) return;
		  Rapp ap = gui.app;
		  int rf = (int)ap.red(from_can.pixels[from]);
		  int gf = (int)ap.green(from_can.pixels[from]);
		  int bf = (int)ap.blue(from_can.pixels[from]);
		  int af = (int)ap.alpha(from_can.pixels[from]);
		  int rt = (int)ap.red(from_can.pixels[to]);
		  int gt = (int)ap.green(from_can.pixels[to]);
		  int bt = (int)ap.blue(from_can.pixels[to]);
		  int at = (int)ap.alpha(from_can.pixels[to]);
		  int[] result = transfer_color(rf, rt, amount);
		  rf = result[0];
		  rt = result[1];
		  result = transfer_color(gf, gt, amount);
		  gf = result[0];
		  gt = result[1];
		  result = transfer_color(bf, bt, amount);
		  bf = result[0];
		  bt = result[1];
		  to_can.pixels[from] = ap.color(rf,gf,bf,af);
		  to_can.pixels[to] = ap.color(rt,gt,bt,at);
	  }
	  int[] transfer_color(int from, int to, int amount) {
		  if (from - amount < 0) amount = from;
		  if (to + amount > 255) amount = 255 - to;
		  int[] result = new int[3];
		  result[0] = from - amount;
		  result[1] = to + amount;
		  result[2] = amount;
		  return result;
	  }
	  
	  void tick() {
//	    if (fcom != null) {
//	      for (int i = can_st ; i < fcom.list.size() ; i += Math.max(1, can_div.get()) )
//	        if (fcom.list.get(i).active) {
//	          ((Floc)fcom.list.get(i)).draw_halo(this);
//	      }
//	    }
		
		if (active_can == 0) rain(can2);
		else if (active_can == 1) rain(can1);
		
		if (do_brush.get() && gui.in.getState("MouseRight")) {
			draw_halo(gui.mouseVector, brush_size.get(), brush_dens.get(), val_col_brush.get());
		}
	    
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
	    	gui.app.rect(val_pos.get().x - val_w.get() * val_scale.get() / 2, 
	    				 val_pos.get().y - val_h.get() * val_scale.get() / 2, 
	    				 val_w.get() * val_scale.get(), val_h.get() * val_scale.get());
	    }

  	    if (val_show_back.get()) draw(back);
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
	    gui.app.translate(val_pos.get().x - val_w.get() * val_scale.get() / 2, 
	    				  val_pos.get().y - val_h.get() * val_scale.get() / 2);
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

	  public void draw_halo(PVector pos, float halo_size, float halo_density, int c) {
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
	  public void draw_line_halo(PVector p1, PVector p2, float halo_size, float halo_density, int c, boolean fracture) {
		  float xmin = PApplet.min(p1.x, p2.x);
		  float xmax = PApplet.max(p1.x, p2.x);
		  float ymin = PApplet.min(p1.y, p2.y);
		  float ymax = PApplet.max(p1.y, p2.y);
		    for (float px = (int)(xmin - halo_size) ; px < (int)(xmax + halo_size) ; 
		    		 px += val_scale.get())
		      for (float py = (int)(ymin - halo_size) ; py < (int)(ymax + halo_size) ; 
		    		   py += val_scale.get()) {
		        float d = RConst.distancePointToLine(px, py, p1.x, p1.y, p2.x, p2.y);
		        float d1 = RConst.distancePointToPoint(px, py, p1.x, p1.y);
//		        float d2 = RConst.distancePointToPoint(px, py, p2.x, p2.y);
//		        float l = RConst.distancePointToPoint(p1.x, p1.y, p2.x, p2.y);
		        if (d < halo_size && (d1 > halo_size || !fracture)
		        		//&& (l < halo_size || !fracture)
		        		) { 
		          float a = (halo_density) * RConst.soothedcurve(1.0F, d / halo_size);
		          if (active_can == 0) addpix(can2, px, py, gui.app.color(gui.app.red(c)*a, 
		        		  													gui.app.green(c)*a, 
		        		  													gui.app.blue(c)*a));
		          if (active_can == 1) addpix(can1, px, py, gui.app.color(gui.app.red(c)*a, 
		        		  													gui.app.green(c)*a, 
		        		  													gui.app.blue(c)*a));
		        }
		    }
	  }

	  void addpix(PImage canvas, float x, float y, int nc) {
	    //x -= int(val_scale.get() / 2);
	    //y -= int(val_scale.get() / 2);
	    x -= val_pos.get().x - val_w.get() * val_scale.get() / 2;
	    y -= val_pos.get().y - val_h.get() * val_scale.get() / 2;
	    x /= val_scale.get();
	    y /= val_scale.get();
	    //x += 1 / val_scale.get();
	    //y += 1 / val_scale.get();
	    if (x < 0 || y < 0 || x > canvas.width || y > canvas.height) return;
	    int pi = canvas.width * (int)(y) + (int)(x);
	    if (pi >= 0 && pi < canvas.pixels.length) {
	      int oc = canvas.pixels[pi];
	      canvas.pixels[pi] = gui.app.color(PApplet.min(255, PApplet.max(gui.app.red(oc), gui.app.red(nc))), 
	    		  							PApplet.min(255, PApplet.max(gui.app.green(oc), gui.app.green(nc))), 
	    		  							PApplet.min(255, PApplet.max(gui.app.blue(oc), gui.app.blue(nc))) );
	    }
	  }

	  void shape_transform(nBase sh, PixelTransform rn) {
		  for (float px = (int)(sh.pos.x - sh.rad()) ; 
				  px < (int)(sh.pos.x + sh.rad()) ; px+=val_scale.get())
			  for (float py = (int)(sh.pos.y - sh.rad()) ; 
					  py < (int)(sh.pos.y + sh.rad()) ; py+=val_scale.get()) {
			  PVector p = new PVector(px, py);
			  if (RConst.point_in_trig(sh.p1(), sh.p2(), sh.p3(), p))
				  if (active_can == 0) transformpix(can2, px, py, rn);
				  else if (active_can == 1) transformpix(can1, px, py, rn);
		  }
      }
	  
	  void transformpix(PImage canvas, float x, float y, PixelTransform rn) {
		    //x -= int(val_scale.get() / 2);
		    //y -= int(val_scale.get() / 2);
		    x -= val_pos.get().x - val_w.get() * val_scale.get() / 2;
		    y -= val_pos.get().y - val_h.get() * val_scale.get() / 2;
		    x /= val_scale.get();
		    y /= val_scale.get();
		    //x += 1 / val_scale.get();
		    //y += 1 / val_scale.get();
		    if (x < 0 || y < 0 || x > canvas.width || y > canvas.height) return;
		    int pi = canvas.width * (int)(y) + (int)(x);
		    if (pi >= 0 && pi < canvas.pixels.length) {
		      int oc = canvas.pixels[pi];
		      canvas.pixels[pi] = rn.result(oc);
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

	  abstract class PixelTransform {
		  public abstract int result(int v); }
	  
	  