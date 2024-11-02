package Macro;

import RApplet.RConst;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;
import sData.*;
import z_old_specialise.FlocComu;
import z_old_specialise.Floc;

public class MCanvas extends MBaseMT { 
	
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("mcanvas", "Canvas", "", "Work"); 
		first_start_show(m); }
		MCanvas build(Macro_Sheet s, sValueBloc b) { MCanvas m = new MCanvas(s, b); return m; }
	}

	public void build_custom_menu(nFrontPanel sheet_front) {
	    sheet_front.getTab(2).getShelf()
	      .addDrawerButton(val_show, val_show_back, val_show_bound, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(val_rst_run, val_show_grab, val_centered, 10, 1)
	      .addSeparator(0.125)
	      ;
	    
	    nFrontTab tab = sheet_front.addTab("Effect");
	    tab.getShelf()
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-Back Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(back_clear, back_fill, back_add, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(back_save, back_load, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFieldCtrl(back_file, 10, 1)
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-Rain Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(do_rain1, do_rain2, do_rain3, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(rain_strength, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(rain_dir, 1, 10, 1)
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-Brush Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(do_brush, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(brush_size, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(brush_dens, 2, 10, 1)
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-Shader Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(use_mask, use_blur, 10, 1)
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
	
	public MCanvas clear() {
		super.clear(); 
		ref_cursor.clear();
		cam_draw.clear();
		removeEventSetupLoad(setup_run);
		return this; }
	public MCanvas toLayerTop() {
		super.toLayerTop(); 
		return this; }
	  
	sInterface inter;
	nGUI cam_gui;
	float ref_size;
	Macro_Connexion link_cam, link_group;
	
	MCanvas(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "mcanvas", _bloc); 
	}
	void build_param() { /*super.build_param();*/ build_normal(); }
	void build_normal() { 
		super.build_normal(); 
		link_cam = addInput(0, "Masking Cams").set_link();
		link_group = addInput(0, "Groups").set_link();
		addEmptyS(1); addEmptyS(1);
	}
	void rebuild() { super.rebuild(); }
	void init_end() { super.init_end(); }
	public void init() {
		super.init();
		inter = mmain().inter;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		init_canvas();
	}

	  nRunnable rst_run, setup_run; Drawable cam_draw;
	  sRun val_rst_run;
	  nCursor ref_cursor;
	
	  sVec val_pos;
	  sInt val_w, val_h, can_div, rate_decay;
	  sFlt val_scale, val_scale_fine, color_keep_thresh, decay_fact, val_bound_thick;
	  sBoo val_show, val_show_back, val_show_bound, val_show_grab, val_centered;
	  sCol val_col_back, val_col_bound;

	  PImage can1,can2;
	  int active_can = 0;
	  int can_st;
	  
	  PImage back;
	  sRun back_clear, back_add, back_fill, back_save, back_load;
	  sStr back_file;
	  
	  sBoo do_rain1, do_rain2, do_rain3, do_brush;
	  sFlt rain_strength, brush_size, brush_dens;
	  sInt rain_dir;
	  sCol val_col_brush;

	  PShader shader_blur;
	  PShader shader_mask;
	  PGraphics mask_image;
	  sBoo use_blur, use_mask;
	
	void init_canvas() {
	    int def_pix_size = 10;
	    int def_size = PApplet.min(app.width, app.height) / def_pix_size;
	    def_size -= def_size % 10;
	    val_pos = newVec("val_pos", "val_pos");
	    val_w = menuIntIncr(def_size, 100, "val_w");
	    val_h = menuIntIncr(def_size, 100, "val_h");
	    can_div = menuIntIncr(1, 1, "can_div");
	    can_div.set_min(0);
	    val_scale = menuFltSlide(def_pix_size, 1, 150, "val_scale");
	    val_scale_fine = menuFltSlide(0, 0, 15, "fine_scale");
	    color_keep_thresh = menuFltSlide(200, 0, 260, "clrkeep_thresh");
	    decay_fact = menuFltSlide(1, 0.9F, 1.0F, "decay_fact");
	    rate_decay = menuIntIncr(100, 10, "rate_decay");
	    val_show = newBoo(true, "val_show", "show_canvas");
	    val_show_back = newBoo(false, "val_show_back", "val_show_back");
	    val_show_bound = newBoo(true, "val_show_bound", "show_bound");
	    val_show_grab = newBoo(true, "val_show_grab", "show_grab");
	    val_centered = newBoo(false, "val_centered", "val_centered");
	    val_col_back = menuColor(gui.app.color(0), "background");
	    val_col_back.addEventChange(new nRunnable() { public void run() { reset(); } });
	    val_col_bound = menuColor(gui.app.color(255), "col_bound");
	    val_bound_thick = menuFltSlide(0.5F, 0, 6.0F, "val_bound_thick");
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
	    
	    ref_cursor = sheet.menuCursor(value_bloc.ref, false);
	    
	    if (ref_cursor.show != null) ref_cursor.show.set(val_show.get() && val_show_grab.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.addEventChange(new nRunnable() { public void run() { 
	      val_pos.set(ref_cursor.pval.get()); }});
	    val_pos.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get()); }});
	    
	    rst_run = new nRunnable() { public void run() { reset(); } };
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


	    use_blur = newBoo(false, "use_blur");
	    use_mask = newBoo(false, "use_mask");
	    use_blur.addEventChange(rst_run);
	    use_mask.addEventChange(rst_run);
	    if (gui.app.USE_SHADERS) {
		    shader_blur = gui.app.loadShader("shaders\\shader_blur.glsl");
		    shader_mask = gui.app.loadShader("shaders\\shader_mask.glsl");
	    }

	    cam_draw = new Drawable() { public void drawing() { drawCanvas(); } };
	    inter.addToCamDrawerPile(cam_draw);
//	    cam_draw.setAlwaysBottom(true);
	    cam_draw.setLayer(priority.get());
		priority.addEventChange(new nRunnable() { public void run() { 
			cam_draw.setLayer(priority.get()); }});
	    
	    clearBack();
	    reset();
	    setup_run = new nRunnable() { public void run() { 
		      mmain().inter.addEventNextFrame(new nRunnable() {public void run() { 
			  	    clearBack();
			    	    reset();
//			        cam_draw.toLayerBottom();
			      }}); } } ;
	    addEventSetupLoad(setup_run);
	  }

	  private void init_pim(PImage canvas) {
	    for(int i = 0; i < canvas.pixels.length; i++) {
	      canvas.pixels[i] = val_col_back.get(); 
	    }
	  }

	  float getscale() { return val_scale.get() + val_scale_fine.get(); }  

	  float sat(int c) {
	    return (gui.app.alpha(c) / 255.0F) * 
	    		(gui.app.red(c) + gui.app.green(c) + gui.app.blue(c)) / 3.0F; }
	  
	  int decay(int c) {
	    return gui.app.color(1.0F*gui.app.red(c)*decay_fact.get(), 
	    					1.0F*gui.app.green(c)*decay_fact.get(), 
	    					1.0F*gui.app.blue(c)*decay_fact.get(), 
	    		             gui.app.alpha(c));//*val_decay.get()); 
	  }

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
	  
	  void reset() {
	    can1 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.ARGB);
	    init_pim(can1);
	    can2 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.ARGB);
	    init_pim(can2);

	    if (gui.app.USE_SHADERS) {
		    mask_image = gui.app.createGraphics(val_w.get(), val_h.get(), PApplet.P2D);
		    mask_image.noSmooth();
	    }
	    
	    can_st = can_div.get();
	    active_can = 0;
	  }
	
	void tick() {}
	void tick_end() {
		
		for (Macro_Connexion c : link_group.connected_outputs) {
			FlocComu fcom = null;
			MSolidGroup solidg = null;
			if (c.elem.bloc.val_type.get().equals("sheetbloc")) {
				MSheetBloc sb = (MSheetBloc)c.elem.bloc;
				sValueBloc b = sb.sheet.value_bloc;
				if (b.getBloc("settings").getValue("specialize") != null) {
			        String spe = ((sStr)b.getBloc("settings").getValue("specialize")).get();
			        
			        if (spe.equals("Floc")) {
			        		fcom = (FlocComu)sb.sheet;
			        }
				}
			} 
			if (c.elem.bloc.val_type.get().equals("solids")) {
				solidg = (MSolidGroup)c.elem.bloc;
			}
			
		    if (fcom != null) {
		      for (int i = can_st ; i < fcom.list.size() ; i += Math.max(1, can_div.get()) )
		        if (fcom.list.get(i).active) {
		          ((Floc)fcom.list.get(i)).draw_halo(this);
		      }
		    }
		    if (solidg != null) { 
		    		for (ESolid s : solidg.solid_list) s.draw_halo(this);
		    }
		}
		if (active_can == 0) rain(can2);
		else if (active_can == 1) rain(can1);
		
		if (do_brush.get() && gui.in.getState("MouseRight")) {
			draw_line_halo(gui.mouseVector, gui.pmouseVector, 
					brush_size.get(), brush_dens.get(), 
					val_col_brush.get(), false);
		}
	    
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
	  
	  void cam_to_mask(MCam s) {
		PVector p1 = new PVector(); PVector p2 = new PVector(); PVector p3 = new PVector();

		mask_image.pushMatrix();
		mask_image.scale( 1.0F / getscale() );
		if (val_centered.get()) 
			mask_image.translate(
				-val_pos.get().x + val_w.get() * getscale() / 2, 
    				-val_pos.get().y + val_h.get() * getscale() / 2); 
		else mask_image.translate(-val_pos.get().x, -val_pos.get().y);
		
		for (int j = 0 ; j+2 < s.mask_vert_stack.size() ; j += 3) {
			p1.set(s.mask_vert_stack.get(j));
			p2.set(s.mask_vert_stack.get(j+1));
			p3.set(s.mask_vert_stack.get(j+2));
			
			mask_image.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
		}
		mask_image.popMatrix();
	  }
	  
	void drawCanvas() {
	    if (val_show_bound.get()) {

		    	gui.app.stroke(val_col_bound.get());
		    	gui.app.strokeWeight(val_bound_thick.get() * ref_size / (10 * mmain().gui.scale) );
		    	gui.app.noFill();
		    	if (val_centered.get())
		    		gui.app.rect(val_pos.get().x - val_w.get() * getscale() / 2, 
		    				 val_pos.get().y - val_h.get() * getscale() / 2, 
		    				 val_w.get() * getscale(), val_h.get() * getscale());
		    	else gui.app.rect(val_pos.get().x, val_pos.get().y, 
	    				 val_w.get() * getscale(), val_h.get() * getscale());
	    }

  	    if (val_show_back.get()) draw(back, false);
	    if (val_show.get()) {
	      if (active_can == 0) draw(can1, true);
	      else if (active_can == 1) draw(can2, true);
	    }
	    
    		if (gui.app.USE_SHADERS && use_mask.get()) {
    		    mask_image.beginDraw();
    	  	  	mask_image.background(0);
    	  	  	mask_image.noStroke();
    	  	  	mask_image.fill(255, 0, 0);
  	  	
			for (Macro_Connexion i : link_cam.connected_outputs) 
				if (i.elem.bloc.val_type.get().equals("cam")) 
					cam_to_mask((MCam)i.elem.bloc);
			for (Macro_Connexion dc : link_cam.direct_cos) 
				for (Macro_Connexion i : dc.connected_outputs)
					if (i.elem.bloc.val_type.get().equals("cam")) 
						cam_to_mask((MCam)i.elem.bloc);
			
	  	  	mask_image.endDraw();
	    }
	  }

	  void draw(PImage canvas, boolean shadering) {
	    canvas.updatePixels();
	    gui.app.pushMatrix();
	    if (val_centered.get()) 
	    		gui.app.translate(val_pos.get().x - val_w.get() * getscale() / 2, 
	    				  val_pos.get().y - val_h.get() * getscale() / 2);
	    else gui.app.translate(val_pos.get().x, val_pos.get().y);
	    gui.app.scale(getscale());

	    if (gui.app.USE_SHADERS) {
		    if (use_mask.get() && shadering) {
			    shader_mask.set("mask", mask_image);
		    	    gui.app.shader(shader_mask);
		    }
		    else if (use_blur.get() && shadering) gui.app.shader(shader_blur);
		    else gui.app.resetShader();
	    }
	    gui.app.image(canvas, 0, 0);
	    
	    if (gui.app.USE_SHADERS && (use_blur.get() || use_mask.get())) gui.app.resetShader();
	    
	    gui.app.popMatrix();
	  }
	  
	  public void draw_halo(PVector pos, float halo_size, float halo_density, int c) {
	    //walk a box of pix around entity containing the halo (pos +/- halo radius)
	    for (float px = (int)(pos.x - halo_size) ; px < (int)(pos.x + halo_size) ; px+=getscale())
	      for (float py = (int)(pos.y - halo_size) ; py < (int)(pos.y + halo_size) ; py+=getscale()) {
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
	  public void draw_line_halo(PVector p1, PVector p2, 
			  					float halo_size, float halo_density, int c, boolean fracture) {
		  float xmin = PApplet.min(p1.x, p2.x);
		  float xmax = PApplet.max(p1.x, p2.x);
		  float ymin = PApplet.min(p1.y, p2.y);
		  float ymax = PApplet.max(p1.y, p2.y);
		    for (float px = (int)(xmin - halo_size) ; px < (int)(xmax + halo_size) ; 
		    		 px += getscale())
		      for (float py = (int)(ymin - halo_size) ; py < (int)(ymax + halo_size) ; 
		    		   py += getscale()) {
		        float d = RConst.distancePointToLine(px, py, p1.x, p1.y, p2.x, p2.y);
		        float d1 = RConst.distancePointToPoint(px, py, p1.x, p1.y);
//			        float d2 = RConst.distancePointToPoint(px, py, p2.x, p2.y);
//			        float l = RConst.distancePointToPoint(p1.x, p1.y, p2.x, p2.y);
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
	    //x -= int(getscale() / 2);
	    //y -= int(getscale() / 2);
		  if (val_centered.get()) {
	    x -= val_pos.get().x - val_w.get() * getscale() / 2;
	    y -= val_pos.get().y - val_h.get() * getscale() / 2;
		  } else {
	    x -= val_pos.get().x;
	    y -= val_pos.get().y;
		  }
	    x /= getscale();
	    y /= getscale();
	    //x += 1 / getscale();
	    //y += 1 / getscale();
	    if (x < 0 || y < 0 || x > canvas.width || y > canvas.height) return;
	    int pi = canvas.width * (int)(y) + (int)(x);
	    if (pi >= 0 && pi < canvas.pixels.length) {
	      int oc = canvas.pixels[pi];
	      canvas.pixels[pi] = gui.app.color(PApplet.min(255, PApplet.max(gui.app.red(oc), gui.app.red(nc))), 
	    		  							PApplet.min(255, PApplet.max(gui.app.green(oc), gui.app.green(nc))), 
	    		  							PApplet.min(255, PApplet.max(gui.app.blue(oc), gui.app.blue(nc))) );
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
//			if (do_rain2.get()) {
//				if (i+val_w.get() < can.pixels.length) {
//					if (gui.app.red(can.pixels[i]) - rain_strength.get() >=
//						gui.app.red(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() && 
//						gui.app.red(can.pixels[i]) - rain_strength.get() >= 0 && 
//						gui.app.red(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() <= 255) {
//					  can.pixels[i] = gui.app.color(
//							  gui.app.red(can.pixels[i]) - rain_strength.get(), 
//							  gui.app.green(can.pixels[i]), 
//							  gui.app.blue(can.pixels[i]), 
//							  gui.app.alpha(can.pixels[i]) );
//					  int j = i+val_w.get();
//					  can.pixels[j] = gui.app.color(
//							  gui.app.red(can.pixels[j]) + rain_strength.get(), 
//							  gui.app.green(can.pixels[j]), 
//							  gui.app.blue(can.pixels[j]), 
//							  gui.app.alpha(can.pixels[j]) );
//					}  
//					if (gui.app.green(can.pixels[i]) - rain_strength.get() >=
//						gui.app.green(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() && 
//						gui.app.green(can.pixels[i]) - rain_strength.get() >= 0 && 
//						gui.app.green(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() <= 255) {
//					  can.pixels[i] = gui.app.color(
//							  gui.app.red(can.pixels[i]), 
//							  gui.app.green(can.pixels[i]) - rain_strength.get(), 
//							  gui.app.blue(can.pixels[i]), 
//							  gui.app.alpha(can.pixels[i]) );
//					  int j = i+val_w.get();
//					  can.pixels[j] = gui.app.color(
//							  gui.app.red(can.pixels[j]), 
//							  gui.app.green(can.pixels[j]) + rain_strength.get(), 
//							  gui.app.blue(can.pixels[j]), 
//							  gui.app.alpha(can.pixels[j]) );
//					}  
//					if (gui.app.blue(can.pixels[i]) - rain_strength.get() >=
//						gui.app.blue(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() && 
//						gui.app.blue(can.pixels[i]) - rain_strength.get() >= 0 && 
//						gui.app.blue(can.pixels[i+val_w.get()-rain_dir.get()]) + rain_strength.get() <= 255) {
//					  can.pixels[i] = gui.app.color(
//							  gui.app.red(can.pixels[i]), 
//							  gui.app.green(can.pixels[i]), 
//							  gui.app.blue(can.pixels[i]) - rain_strength.get(), 
//							  gui.app.alpha(can.pixels[i]) );
//					  int j = i+val_w.get();
//					  can.pixels[j] = gui.app.color(
//							  gui.app.red(can.pixels[j]), 
//							  gui.app.green(can.pixels[j]), 
//							  gui.app.blue(can.pixels[j]) + rain_strength.get(), 
//							  gui.app.alpha(can.pixels[j]) );
//					}  
//				}
//			}
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
	  
}
