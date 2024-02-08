package Macro;

import RApplet.RConst;
import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sRun;
import sData.sValueBloc;
import sData.sVec;

public class MCanvas extends MBaseMenu { 
	static class MCanvas_Builder extends MAbstract_Builder {
		MCanvas_Builder() { super("canvas", "canvas of actif pixel"); show_in_buildtool = true; }
		MCanvas build(Macro_Sheet s, sValueBloc b) { MCanvas m = new MCanvas(s, b); return m; }
	}
	Drawable drawable;
	Macro_Connexion struct_link, sheet_in, tick_in;
	Macro_Sheet cursor_sheet;
	
	  nCursor ref_cursor;
	
	  nRunnable tick_run, rst_run;       sRun val_rst_run;
	  sVec val_pos; 						sCol val_col_back;
	  sInt val_w, val_h, can_div;     	sFlt val_scale, color_keep_thresh, val_decay;
	  sBoo val_show, val_show_bound, val_show_grab;
	  
	  sBoo val_draw_mode;
	  
	  PImage can1,can2;
	  int active_can = 0;
	  int can_st;
	  
//	  //nLinkedWidget canvas_grabber;
	  
	MCanvas(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "canvas", _bloc); }
	void init() {
		super.init();   
	    int def_pix_size = 10;
	    val_pos = newVec("val_pos", "val_pos");
	    val_w = menuIntIncr((int)((float)gui.app.screen_width / (float)def_pix_size), 100, "val_w");
	    val_h = menuIntIncr((int)((float)gui.app.screen_height / (float)def_pix_size), 100, "val_h");
	    can_div = menuIntIncr(4, 1, "can_div");
	    val_scale = menuFltSlide(def_pix_size, 10, 500, "val_scale");
	    color_keep_thresh = menuFltSlide(200, 10, 260, "clrkeep_thresh");
	    val_decay = menuFltSlide(1, 0.99F, 1.01F, "decay");
	    val_show = newBoo(true, "val_show", "show_canvas");
	    val_show_bound = newBoo(true, "val_show_bound", "show_bound");
	    val_show_grab = newBoo(true, "val_show_grab", "show_grab");
	    val_draw_mode = newBoo(true, "val_draw_mode", "val_draw_mode");
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
	    
	    ref_cursor = sheet.newCursor("Canvas", false);
	    if (ref_cursor.show != null) ref_cursor.show.set(val_show.get() && val_show_grab.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get());
	    if (ref_cursor.pval != null) ref_cursor.pval.addEventChange(new nRunnable() { public void run() { 
	      val_pos.set(ref_cursor.pval.get()); }});
	    val_pos.addEventChange(new nRunnable() { public void run() { 
	      if (ref_cursor.pval != null) ref_cursor.pval.set(val_pos.get()); }});
	    
	    tick_run = new nRunnable() { public void run() { canvas_drawtick(); } };
	    rst_run = new nRunnable() { public void run() { reset(); } };
	    val_rst_run = newRun("val_rst_run", "rst_run", rst_run);
	    
	    val_w.addEventChange(rst_run);
	    val_h.addEventChange(rst_run);
	    
		drawable = new Drawable() { 
			public void drawing() { canvas_drawcall(); } } ;
			
//		priority.addEventChange(new nRunnable() {public void run() {
//			drawable.setLayer(priority.get());
//		}});
//		drawable.setLayer(priority.get());

	    mmain().inter.addToCamDrawerPile(drawable);

	    reset();
	}
	void build_param() {
		super.build_param();
		tick_in = addInputBang(0, "tick", tick_run);
		sheet_in = addInput(0,"sheet", new nRunnable() {public void run() {
			if (sheet_in.lastPack().isSheet()) cursor_sheet = sheet_in.lastPack().asSheet();
		}});
		struct_link = addInput(0,"link");
	}
	void build_normal() {
		super.build_normal();
		tick_in = addInputBang(0, "tick", tick_run);
		sheet_in = addInput(0,"sheet", new nRunnable() {public void run() {
			if (sheet_in.lastPack().isSheet()) cursor_sheet = sheet_in.lastPack().asSheet();
		}});
		struct_link = addInput(0,"link");
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {

		    nFrontTab tab = sheet_front.getTab(2);
		    tab.getShelf()
//		      .addDrawer(10.25, 0.75)
//		      .addModel("Label-S4", "-Canvas Control-").setFont((int)(ref_size/1.4)).getShelf()
		      .addSeparator(0.125)
		      .addDrawerButton(val_draw_mode, 10, 1)
		      .addSeparator(0.125)
		      ;
			sheet_front.toLayerTop();
		}
	}

	  void reset() {
	    can1 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.RGB);
	    init_pim(can1);
	    can2 = gui.app.createImage(val_w.get(), val_h.get(), PConstants.RGB);
	    init_pim(can2);
	    can_st = can_div.get();
	    active_can = 0;
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
	  
	  void canvas_drawtick() {
	  }
	  
	  void canvas_drawcall() {
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
		if (cursor_sheet != null && cursor_sheet.sheet_cursors_list.size() > 0) {
			for (nCursor cur : cursor_sheet.sheet_cursors_list) {
				for (Macro_Connexion co : struct_link.connected_outputs) {
					if (co.elem.bloc.val_type.get().equals("struct")) {
						MStructure mf = ((MStructure)co.elem.bloc);
//						mf.draw(gui.app);
						int col = gui.app.color(255, 0, 0);
						PVector pv = new PVector(cur.pos().x, cur.pos().y);
						for (Replic r : mf.replics) {
							PVector rv = new PVector(r.pos.x, r.pos.y);
							rv.rotate(cur.dir().heading());
							pv.add(rv);
							if (val_draw_mode.get()) {
								draw_halo(pv, 55.5F, 1, col);
							} else {
								if (mf.last_form != null) {
									PVector p1 = new PVector(mf.last_form.shape.face.p1.x, 
											mf.last_form.shape.face.p1.y);
									PVector p2 = new PVector(mf.last_form.shape.face.p2.x, 
											mf.last_form.shape.face.p2.y);
									PVector p3 = new PVector(mf.last_form.shape.face.p3.x, 
											mf.last_form.shape.face.p3.y);
									p1.mult(mf.last_form.val_scale.get());
									p1.rotate(r.dir.heading() + cur.dir().heading());
									p1.add(pv.x, pv.y);
									p2.mult(mf.last_form.val_scale.get() * mf.nscale.get());
									p2.rotate(r.dir.heading() + cur.dir().heading());
									p2.add(pv.x, pv.y);
									p3.mult(mf.last_form.val_scale.get() * mf.nscale.get());
									p3.rotate(r.dir.heading() + cur.dir().heading());
									p3.add(pv.x, pv.y);
									draw_shape_fill(p1, p2, p3, 55.5F, 1, col);
								}
								pv.set(cur.pos().x, cur.pos().y);
								pv.rotate(cur.dir().heading());
							}
						}
					}
				}
			} 
		} else {
			for (Macro_Connexion c : struct_link.connected_outputs) {
				if (c.elem.bloc.val_type.get().equals("struct")) {
					MStructure mf = ((MStructure)c.elem.bloc);
//					mf.draw(gui.app);
					int col = gui.app.color(255, 0, 0);
					for (Replic r : mf.replics) {
						if (!val_draw_mode.get()) {
							draw_halo(r.pos, 55.5F, 1, col);
						} else {
							if (mf.last_form != null) {
								PVector p1 = new PVector(mf.last_form.shape.face.p1.x, 
										mf.last_form.shape.face.p1.y);
								PVector p2 = new PVector(mf.last_form.shape.face.p2.x, 
										mf.last_form.shape.face.p2.y);
								PVector p3 = new PVector(mf.last_form.shape.face.p3.x, 
										mf.last_form.shape.face.p3.y);
								p1.rotate(r.dir.heading());
								p1.add(r.pos.x, r.pos.y);
								p1.mult(mf.last_form.val_scale.get() * mf.nscale.get());
								p2.rotate(r.dir.heading());
								p2.add(r.pos.x, r.pos.y);
								p2.mult(mf.last_form.val_scale.get() * mf.nscale.get());
								p3.rotate(r.dir.heading());
								p3.add(r.pos.x, r.pos.y);
								p3.mult(mf.last_form.val_scale.get() * mf.nscale.get());
//								draw_shape_fill(p1, p2, p3, 55.5F, 1, col);

							  PixelTransform rn = new PixelTransform() { public int result(int v) {
								  float r = gui.app.red(v);
								  float g = gui.app.green(v);
								  float b = gui.app.blue(v);
								  if ( r > 10) r-=5;
								  if ( g > 10) g-=5;
								  if ( b > 10) b-=5;
								  return gui.app.color(r, g, b);
							  } };
							  shape_transform(p1, p2, p3, rn);
							}
						}
					}
				}
			}
		}
		  
	    if (val_show_bound.get()) {

		    	gui.app.stroke(180);
		    	gui.app.strokeWeight(ref_size / (10 * mmain().gui.scale) );
		    	gui.app.noFill();
		    	gui.app.rect(val_pos.get().x - val_w.get() * val_scale.get() / 2, 
		    				 val_pos.get().y - val_h.get() * val_scale.get() / 2, 
		    				 val_w.get() * val_scale.get(), val_h.get() * val_scale.get());
	    }
	    if (val_show.get()) {
	      if (active_can == 0) pim_drawcall(can1);
	      else if (active_can == 1) pim_drawcall(can2);
	    }
	  }
	  
	  private void pim_drawcall(PImage canvas) {
	    canvas.updatePixels();
	    gui.app.pushMatrix();
	    gui.app.translate(val_pos.get().x - val_w.get() * val_scale.get() / 2, 
	    				  val_pos.get().y - val_h.get() * val_scale.get() / 2);
	    gui.app.scale(val_scale.get());
	    gui.app.image(canvas, 0, 0);
	    gui.app.popMatrix();
	  }


	  void draw_shape_fill(PVector p1, PVector p2, PVector p3, float halo_size, float halo_density, int c) {
	    PVector med = new PVector((p1.x+p2.x+p3.x)/3.0F, (p1.y+p2.y+p3.y)/3.0F);
	    PVector m1 = new PVector(med.x-p1.x,med.y-p1.y);
	    PVector m2 = new PVector(med.x-p2.x,med.y-p2.y);
	    PVector m3 = new PVector(med.x-p3.x,med.y-p3.y);
	    float rad = Math.max(Math.max(m1.mag(), m2.mag()), m3.mag());
//	    gui.point(p1.x, p1.y);
//	    gui.point(p2.x, p2.y);
//	    gui.point(p3.x, p3.y);
		for (float px = (int)(med.x - rad - halo_size) ; 
	         px < (int)(med.x + rad + halo_size) ; px+=val_scale.get())
	      for (float py = (int)(med.y - rad - halo_size) ; 
	           py < (int)(med.y + rad + halo_size) ; py+=val_scale.get()) {
	      PVector p = new PVector(px, py);
	      float l1 = RConst.distancePointToLine(px, py, p1.x, p1.y, p2.x, p2.y);
	      float l2 = RConst.distancePointToLine(px, py, p3.x, p3.y, p2.x, p2.y);
	      float l3 = RConst.distancePointToLine(px, py, p1.x, p1.y, p3.x, p3.y);
	      float m = Math.min(Math.min(l1, l2), l3);
	      if (RConst.point_in_trig(p1, p2, p3, p)) m = 0;
	      if (m < halo_size) { //get and try distence of current pix
	        //the color to add to the current pix is function of his distence to the center
	        //the decreasing of the quantity of color to add is soothed
	        float a = (halo_density) * RConst.soothedcurve(1.0F, m / halo_size);
	        if (active_can == 0) addpix(can2, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	        if (active_can == 1) addpix(can1, px, py, gui.app.color(gui.app.red(c)*a, gui.app.green(c)*a, gui.app.blue(c)*a));
	      }
	    }
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

	  void shape_transform(PVector p1, PVector p2, PVector p3, PixelTransform rn) {
		  PVector med = new PVector((p1.x+p2.x+p3.x)/3.0F, (p1.y+p2.y+p3.y)/3.0F);
		    PVector m1 = new PVector(med.x-p1.x,med.y-p1.y);
		    PVector m2 = new PVector(med.x-p2.x,med.y-p2.y);
		    PVector m3 = new PVector(med.x-p3.x,med.y-p3.y);
		    float rad = Math.max(Math.max(m1.mag(), m2.mag()), m3.mag());
		  for (float px = (int)(med.x - rad) ; 
				  px < (int)(med.x + rad) ; px+=val_scale.get())
			  for (float py = (int)(med.y - rad) ; 
					  py < (int)(med.y + rad) ; py+=val_scale.get()) {
			  PVector p = new PVector(px, py);
			  if (RConst.point_in_trig(p1, p2, p3, p))
				  if (active_can == 0) transformpix(can2, px, py, rn);
				  else if (active_can == 1) transformpix(can1, px, py, rn);
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

	  
	public MCanvas clear() {
		super.clear(); 
		drawable.clear();
		ref_cursor.clear();
		return this; }
	public MCanvas toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}

abstract class PixelTransform {
	public abstract int result(int v); }

