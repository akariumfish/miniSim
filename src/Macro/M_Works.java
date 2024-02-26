package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import RApplet.Rapp;
import UI.Drawable;
import UI.nCursor;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nWidget;
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
import sData.sStr;
import sData.sValueBloc;
import sData.sVec;
import z_old_specialise.nBase;

public class M_Works {}

class MCam extends MBaseMenu { 
	static class MCam_Builder extends MAbstract_Builder {
		MCam_Builder() { super("cam", "Camera", "camera drawing point", "Work"); }
		MCam build(Macro_Sheet s, sValueBloc b) { MCam m = new MCam(s, b); return m; }
	}
	Drawable drawable;
	Macro_Connexion struct_link, sheet_in;
	ArrayList<Macro_Sheet> csheets = new ArrayList<Macro_Sheet>();
	ArrayList<MForm> forms = new ArrayList<MForm>();
	ArrayList<MStructure> structs = new ArrayList<MStructure>();
	nRunnable up_sheet_run, up_draw_run;
	MCam(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cam", _bloc); }
	void init() {
		super.init();   
		drawable = new Drawable() { 
			public void drawing() { draw_to_cam(); } } ;
		mmain().inter.addToCamDrawerPile(drawable);
		priority.addEventChange(new nRunnable() {public void run() {
			drawable.setLayer(priority.get());
		}});
		drawable.setLayer(priority.get());
	}
	void build_normal() {
		super.build_normal();
		sheet_in = addInput(0, "sheet");
		up_sheet_run = new nRunnable() { public void run() {
			for (Macro_Sheet mf : csheets) mf.priority.removeEventChange(up_sheet_run);
			csheets.clear();
			int cnt = 0;
			int prio = 100;
			for (Macro_Connexion co : sheet_in.connected_outputs) {
				if (co.elem.bloc.val_type.get().equals("sheetmain")) {
					Macro_Sheet mf = ((Macro_Sheet)co.elem.bloc.sheet);
					cnt++;
					if (mf.priority.get() < prio) {
						prio = mf.priority.get();
					}
				}
			}
			while(cnt > 0) {
				for (Macro_Connexion co : sheet_in.connected_outputs) {
					if (co.elem.bloc.val_type.get().equals("sheetmain")) {
						Macro_Sheet mf = ((Macro_Sheet)co.elem.bloc.sheet);
						if (mf.priority.get() == prio) {
							csheets.add(mf);
							mf.priority.addEventChange(up_sheet_run);
							cnt--;
						}
					}
				}
				prio++;
			}
		}};
		sheet_in.set_link().addEventLink(up_sheet_run).addEventUnLink(up_sheet_run);
		struct_link = addInput(0, "link");
		up_draw_run = new nRunnable() { public void run() {
			forms.clear();
			for (Macro_Connexion co : struct_link.connected_outputs) {
				if (co.elem.bloc.val_type.get().equals("form")) {
					MForm mf = ((MForm)co.elem.bloc);
					forms.add(mf);
				}
			}
			for (MStructure mf : structs) mf.priority.removeEventChange(up_draw_run);
			structs.clear();
			int cnt = 0;
			int prio = 100;
			for (Macro_Connexion co : struct_link.connected_outputs) {
				if (co.elem.bloc.val_type.get().equals("struct")) {
					MStructure mf = ((MStructure)co.elem.bloc);
					cnt++;
					if (mf.priority.get() < prio) {
						prio = mf.priority.get();
					}
				}
			}
			while(cnt > 0) {
				for (Macro_Connexion co : struct_link.connected_outputs) {
					if (co.elem.bloc.val_type.get().equals("struct")) {
						MStructure mf = ((MStructure)co.elem.bloc);
						if (mf.priority.get() == prio) {
							structs.add(mf);
							mf.priority.addEventChange(up_draw_run);
							cnt--;
						}
					}
				}
				prio++;
			}
		}};
		struct_link.set_link().addEventLink(up_draw_run).addEventUnLink(up_draw_run);
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {
			sheet_front.toLayerTop();
		}
	}
	public void draw_to_cam() {
		if (csheets.size() > 0) { 
			for (Macro_Sheet mf : csheets) for (nCursor cur : mf.sheet_cursors_list) {
				gui.app.pushMatrix();
				gui.app.translate(cur.x(), cur.y());
				gui.app.rotate(cur.dir().heading());
				
				for (MForm m : forms) m.draw(gui.app);
				for (MStructure m : structs) m.draw(gui.app);
				
				gui.app.popMatrix();
			} 
		} else {
			for (MForm m : forms) m.draw(gui.app);
			for (MStructure m : structs) m.draw(gui.app);
		}
	}
	public MCam clear() {
		super.clear(); 
		drawable.clear();
		return this; }
	public MCam toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}











class MCanvas extends MBaseMenu { 
	static class MCanvas_Builder extends MAbstract_Builder {
		MCanvas_Builder() { super("canvas", "Canvas", "canvas of actif pixel", "Work"); }
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
			
		priority.addEventChange(new nRunnable() {public void run() {
			drawable.setLayer(priority.get());
		}});
		drawable.setLayer(priority.get());

	    mmain().inter.addToCamDrawerPile(drawable);

	    reset();
	}
	void build_param() {
		super.build_param();
		tick_in = addInputBang(0, "tick", tick_run);
		sheet_in = addInput(0,"sheet");
		struct_link = addInput(0,"link");
		sheet_in.set_link();
		struct_link.set_link();
	}
	void build_normal() {
		super.build_normal();
		tick_in = addInputBang(0, "tick", tick_run);
		sheet_in = addInput(0,"sheet");
		struct_link = addInput(0,"link");
		sheet_in.set_link();
		struct_link.set_link();
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
	    int prio = -1;
		for (Macro_Connexion co : sheet_in.connected_outputs) {
			if (co.elem.bloc.val_type.get().equals("sheetmain")) {
				Macro_Sheet mf = ((Macro_Sheet)co.elem.bloc.sheet);
				if (mf.priority.get() >= prio) {
					cursor_sheet = mf;
					prio = mf.priority.get();
				}
			}
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

		cursor_sheet = null;
		
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








class Replic implements Macro_Interf {
	PVector pos = new PVector();
	PVector dir = new PVector();
	PVector pos() { return new PVector(pos.x, pos.y); } 
	PVector dir() { return new PVector(dir.x, dir.y); } 
	Replic pos(PVector f) { pos.x = f.x; pos.y = f.y; return this; } 
	Replic dir(PVector f) { dir.x = f.x; dir.y = f.y; return this; } 
	String to_str() {
		String s = PApplet.str(pos.x) + INFO_TOKEN + 
				PApplet.str(pos.y) + INFO_TOKEN + 
				PApplet.str(dir.x) + INFO_TOKEN + 
				PApplet.str(dir.y);
		return s;
	}
	void from_str(String s) {
		String[] l = PApplet.splitTokens(s, INFO_TOKEN);
		if (l.length == 4) {
			pos.x = Rapp.parseFlt(l[0]);
			pos.y = Rapp.parseFlt(l[1]);
			dir.x = Rapp.parseFlt(l[2]);
			dir.y = Rapp.parseFlt(l[3]);
		}
	}
}






class MStructure extends MBaseMenu { 
	static class MStructure_Builder extends MAbstract_Builder {
		MStructure_Builder() { super("struct", "Structure", "structure of forms", "Work"); }
		MStructure build(Macro_Sheet s, sValueBloc b) { MStructure m = new MStructure(s, b); return m; }
	}
	MStructure new_replic() {
		Replic r = new Replic().pos(new_pos.get()).dir(new_dir.get());
		replics.add(r);
		last_build = r;
//		save_copy = replics_save.get();
		replics_save.set(replics_save.get() + OBJ_TOKEN + r.to_str());
		return this;
	}
//	String save_copy = "";
	MStructure del_oldest_replic() {
		if (last_build != null && last_build != first_rep) {
			replics.remove(last_build);
			if (replics.size() > 1) {
				last_build = replics.get(1);
//				replics_save.set(save_copy);
//				String[] l = PApplet.splitTokens(save_copy, OBJ_TOKEN);
//				save_copy = "";
//				if (l.length > 0) {
//					save_copy = l[0];
//					for (int i = 2 ; i < l.length ; i ++) save_copy = save_copy + l[i];
//				}
			} else {
				last_build = null;
//				replics_save.set("");
//				save_copy = "";
			}
		}
		return this;
	}
	MStructure clear_replic() {
		replics.clear();
		replics_save.set("");
		nposx.set(0); nposy.set(0); nscale.set(10); nrot.set(0);
		first_rep = new Replic().pos(new PVector(0, 0)).dir(new PVector(10, 0));
		replics.add(first_rep); 
		return this;
	}
	String current_replic() {
		return first_rep.to_str();
	}
	MStructure set_replic_from(Replic r) {
		nposx.set(r.pos.x);
		nposy.set(r.pos.y);
		nrot.set(r.dir.heading());
		nscale.set(r.dir.mag());
		new_pos.set(nposx.get(), nposy.get()); 
		first_rep.pos.set(nposx.get(), nposy.get());
		first_rep.dir.set(nscale.get(), 0);
		first_rep.dir.rotate(nrot.get());
		new_dir.set(first_rep.dir.x, first_rep.dir.y); 
		return this;
	}

	MStructure translate(PVector r) {
		nposx.add(r.x);
		nposy.add(r.y);
		up_npos.run();
		return this;
	}
	MStructure rotate(float r) {
		nrot.add(r);
		up_npos.run();
		return this;
	}
	MStructure scale(float r) {
		nscale.add(r);
		up_npos.run();
		return this;
	}
	
	ArrayList<Replic> replics;
	Replic first_rep, last_build;
	Macro_Connexion form_link, cam_link, com_in;
	MForm last_form;
	sFlt nposx,nposy,nscale,nrot;
	sVec new_pos, new_dir;
	sRun new_rep_run, clear_rep_run;
	nRunnable up_npos;
	sStr replics_save;
	MStructure(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "struct", _bloc); }
	void init() {
		super.init();   
		replics = new ArrayList<Replic>();
		first_rep = new Replic().pos(new PVector(100, 100)).dir(new PVector(10, 0));
		replics.add(first_rep);
		new_rep_run = newRun("new_rep_run", "new_rep_run", new nRunnable() {public void run() { 
			new_replic(); }});
		clear_rep_run = newRun("clear_rep_run", "clear_rep_run", new nRunnable() {public void run() { 
			clear_replic(); }});
		new_pos = newVec("new_pos", "new_pos");
		new_dir = newVec("new_dir", "new_dir");
		nposx = menuFltSlide(0, -10000, 10000, "adding_x");
		nposy = menuFltSlide(0, -10000, 10000, "adding_y");
		nscale = menuFltSlide(10, 1, 20, "adding_scale");
		nrot = menuFltSlide(0, -RConst.PI, RConst.PI, "adding_rot");
		up_npos = new nRunnable() {public void run() { 
			new_pos.set(nposx.get(), nposy.get()); 
			first_rep.pos.set(nposx.get(), nposy.get());
			first_rep.dir.set(nscale.get(), 0);
			first_rep.dir.rotate(nrot.get());
			new_dir.set(first_rep.dir.x, first_rep.dir.y); 
		}};
		up_npos.run();
		nposx.addEventChange(up_npos);
		nposy.addEventChange(up_npos);
		nscale.addEventChange(up_npos);
		nrot.addEventChange(up_npos);
		replics_save = newStr("replics_save", "replics_save", "");
		if (replics_save.get().length() > 0) {
			String[] l = PApplet.splitTokens(replics_save.get(), OBJ_TOKEN);
			for (String s : l) {
				Replic r = new Replic();
				replics.add(r);
				r.from_str(s);
			}
		}
	}
	void build_normal() {
		super.build_normal();
		com_in = addInput(0,"COM", new nRunnable() {public void run() { 
//			if (com_in.lastPack() == null) return;
//			String m_def = com_in.lastPack().def;
//			String msg = com_in.lastPack().popMsg();
//			if (com_in.lastPack() != null && m_def.equals("ASK") && 
//					com_in.lastPack().hasMsg() &&
//					msg.equals("STRUCT_NEW")) {
//				new_replic();
//			} else if (com_in.lastPack() != null && m_def.equals("ASK") && 
//					com_in.lastPack().hasMsg() &&
//					msg.equals("STRUCT_CLR")) {
//				clear_replic();
//			} else if (com_in.lastPack() != null && m_def.equals("ASK") && 
//					com_in.lastPack().hasMsg() &&
//					msg.equals("STRUCT_DEF")) {
//				while(com_in.lastPack().hasMsg()) {
//					String st = com_in.lastPack().popMsg();
//					Replic r = new Replic();
//					replics.add(r);
//					r.from_str(st);
//					replics_save.set(replics_save.get() + OBJ_TOKEN + st);
//				}
//			} else if (com_in.lastPack() != null && com_in.lastPack().isObject()) {
//				Replic r = (Replic)com_in.lastPack().asObject();
//				r.from_str(first_rep.to_str());
//			} 
		}});
		com_in.set_link();
		addEmptyS(1);
		addTrigS(0, "new", new_rep_run.get());
		addTrigS(1, "clear", clear_rep_run.get());
		form_link = addInput(0,"form");
		cam_link = addOutput(1,"cam");
		form_link.set_link();
		cam_link.set_link();
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.getTab(2);
	    tab.getShelf()
//	      .addDrawer(10.25, 0.75)
//	      .addModel("Label-S4", "-Canvas Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(new_rep_run, clear_rep_run, 10, 1)
	      .addSeparator(0.125)
	      ;
	}

	public void draw(Rapp a) { 	
		if (replics.size() > 0) {
			MForm mf = null;
			for (Macro_Connexion c : form_link.connected_outputs) {
				if (c.elem.bloc.val_type.get().equals("form")) {
					mf = ((MForm)c.elem.bloc);
					if (mf != null) for (Replic r : replics) {
						a.pushMatrix();
						a.translate(r.pos.x, r.pos.y);
						a.rotate(r.dir.heading());
						a.scale(r.dir.mag());
						mf.draw(a);
						a.popMatrix();
					}
					if (mf != null) last_form = mf;
				}
			}
		}
	}

	public MStructure clear() {
		super.clear(); 
		return this; }
	public MStructure toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}












class MPatern extends MBaseMenu { 
	static class MPatern_Builder extends MAbstract_Builder {
		MPatern_Builder() { super("patern", "Patern", "auto build structures", "Work"); }
		MPatern build(Macro_Sheet s, sValueBloc b) { MPatern m = new MPatern(s, b); return m; }
	}
	Macro_Connexion in_tick, struct_order;
	nRunnable tick_run;
	sBoo auto_mode_val, mode_1, mode_2;
	sFlt rot_speed, col_grad;

	sFlt nposx,nposy,nscale,nrot;
	nRunnable up_npos;
	
	sRun ask_add_run, ask_clear_run, ask_del_run, ask_get_run, ask_set_run;
	Replic current_replic;
	
	
	MPatern(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "patern", _bloc); }
	void init() {
		super.init();   
		tick_run = new nRunnable() { public void run() { 
			if (in_tick.lastPack() != null &&in_tick.lastPack().isBang()) tick(); 
		} };

		auto_mode_val = newBoo(false, "auto_mode_val");
		mode_1 = newBoo(false, "mode_1");
		mode_2 = newBoo(false, "mode_2");

		rot_speed = menuFltSlide(0, -RConst.PI / 60, RConst.PI / 60, "rot_speed");

		col_grad = menuFltSlide(0, 0, 1, "col_grad");

		ask_add_run = newRun("ask_add_run", "ask_add_run", new nRunnable() { 
		      public void run() { ask_add(); } } );
		ask_clear_run = newRun("ask_clear_run", "ask_clear_run", new nRunnable() { 
		      public void run() { ask_clear(); } } );
		ask_del_run = newRun("ask_del_run", "ask_del_run", new nRunnable() { 
		      public void run() { ask_del(); } } );
		ask_get_run = newRun("ask_get_run", "ask_get_run", new nRunnable() { 
		      public void run() { ask_get(); } } );
		ask_set_run = newRun("ask_set_run", "ask_set_run", new nRunnable() { 
		      public void run() { ask_set(); } } );
		
		current_replic = new Replic();
		
		nposx = menuFltSlide(0, -500, 500, "adding_x");
		nposy = menuFltSlide(0, -500, 500, "adding_y");
		nscale = menuFltSlide(10, 5, 20, "adding_scale");
		nrot = menuFltSlide(0, -RConst.PI, RConst.PI, "adding_rot");
		up_npos = new nRunnable() {public void run() { 
			current_replic.pos.set(nposx.get(), nposy.get());
			current_replic.dir.set(nscale.get(), 0);
			current_replic.dir.rotate(nrot.get());
		}};
		
		nposx.addEventChange(new nRunnable() { public void run() { 
			out_px.send(Macro_Packet.newPacketFloat(nposx.get())); }});
		nposy.addEventChange(new nRunnable() { public void run() { 
			out_py.send(Macro_Packet.newPacketFloat(nposy.get())); }});
		nscale.addEventChange(new nRunnable() { public void run() { 
			out_s.send(Macro_Packet.newPacketFloat(nscale.get())); }});
		nrot.addEventChange(new nRunnable() { public void run() { 
			out_r.send(Macro_Packet.newPacketFloat(nrot.get())); }});
		nposx.addEventChange(up_npos);
		nposy.addEventChange(up_npos);
		nscale.addEventChange(up_npos);
		nrot.addEventChange(up_npos);
		up_npos.run();
	}
	Macro_Connexion in_px,in_py,in_s,in_r , out_px,out_py,out_s,out_r;
	void build_param() {
		super.build_param();
//		in_tick = addInput(0,"tick", tick_run);
//		struct_order = addOutput(1,"struct order out");
//		addInputBang(0, "add", ask_add_run.get());
//		addInputBang(0, "clear", ask_clear_run.get());
//		addInputBang(0, "get", ask_get_run.get());
//		addInputBang(0, "set", ask_set_run.get());
//		
//		addEmptyS(1); addEmptyS(1); addEmptyS(1); addEmptyS(1);
//
//		out_px = addOutput(1, "nposx");
//		out_py = addOutput(1, "nposy");
//		out_s = addOutput(1, "nscale");
//		out_r = addOutput(1, "nrot");
//
//		in_px = addInputFloat(0, "nposx", new nRunnable() { public void run() { 
//			nposx.set(in_px.lastPack().asFloat()); }});
//		in_py = addInputFloat(0, "nposy", new nRunnable() { public void run() { 
//			nposy.set(in_py.lastPack().asFloat()); }});
//		in_s = addInputFloat(0, "nscale", new nRunnable() { public void run() { 
//			nscale.set(in_s.lastPack().asFloat()); }});
//		in_r = addInputFloat(0, "nrot", new nRunnable() { public void run() { 
//			nrot.set(in_r.lastPack().asFloat()); }});
	}
	void build_normal() {
		super.build_normal();
		in_tick = addInput(0,"tick", tick_run);
		struct_order = addOutput(1,"struct order out");
		struct_order.set_link();
		
		addInputBang(0, "add", ask_add_run.get());
		addInputBang(0, "del", ask_del_run.get());
		addInputBang(0, "clear", ask_clear_run.get());
		addInputBang(0, "get", ask_get_run.get());
		addInputBang(0, "set", ask_set_run.get());
		
		addEmptyS(1); addEmptyS(1); addEmptyS(1); addEmptyS(1); addEmptyS(1);

		out_px = addOutput(1, "nposx");
		out_py = addOutput(1, "nposy");
		out_s = addOutput(1, "nscale");
		out_r = addOutput(1, "nrot");

		in_px = addInputFloat(0, "nposx", new nRunnable() { public void run() { 
			nposx.set(in_px.lastPack().asFloat()); }});
		in_py = addInputFloat(0, "nposy", new nRunnable() { public void run() { 
			nposy.set(in_py.lastPack().asFloat()); }});
		in_s = addInputFloat(0, "nscale", new nRunnable() { public void run() { 
			nscale.set(in_s.lastPack().asFloat()); }});
		in_r = addInputFloat(0, "nrot", new nRunnable() { public void run() { 
			nrot.set(in_r.lastPack().asFloat()); }});
		
		in_m1 = addInputBool(0, "mode1", new nRunnable() { public void run() { 
			if (in_m1.lastPack() != null) mode_1.set(in_m1.lastPack().asBool()); }});
		in_m2 = addInputBool(0, "mode2", new nRunnable() { public void run() { 
			if (in_m2.lastPack() != null) mode_2.set(in_m2.lastPack().asBool()); }});
	}
	Macro_Connexion in_m1, in_m2;
	void ask_add() {
//		Macro_Packet p = new Macro_Packet("ASK");
//		p.addMsg("STRUCT_NEW");
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.new_replic();
			}
		}
	}
	void ask_clear() {
//		Macro_Packet p = new Macro_Packet("ASK");
//		p.addMsg("STRUCT_CLR");
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.clear_replic();
			}
		}
	}
	void ask_del() {
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.del_oldest_replic();
			}
		}
	}
	void ask_get() {
//		Macro_Packet p = Macro_Packet.newPacketObject((Object)current_replic);
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				current_replic.from_str(mf.current_replic());
				out_px.sendFloat(current_replic.pos.x);
				out_py.sendFloat(current_replic.pos.y);
				out_s.sendFloat(current_replic.dir.mag());
				out_r.sendFloat(current_replic.dir.heading());
			}
		}
	}
	void ask_set() {
//		Macro_Packet p = new Macro_Packet("ASK");
//		p.addMsg("STRUCT_DEF");
//		p.addMsg(current_replic.to_str());
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.set_replic_from(current_replic);
			}
		}
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.getTab(2);
	
	    tab.getShelf()
	      .addDrawer(10.25, 0.6)
	      .addModel("Label-S4", "- Simulation Control -").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(ask_add_run, ask_clear_run, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(ask_get_run, ask_set_run, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(mode_1, mode_2, auto_mode_val, 10, 1)
	      .addSeparator(0.125)
	      ;
	}
	public void tick() {
		
		if (auto_mode_val.get()) {
			//rot replic around center ou add by rotating ou add in line ...
			// find how: add after last to make object chaine
			
			// send order via Msg packet trough COM ???
		
			MStructure mf = null;
			for (Macro_Connexion c : struct_order.connected_inputs) {
				if (c.elem.bloc.val_type.get().equals("struct")) {
					mf = ((MStructure)c.elem.bloc);
					for (Replic r : mf.replics) {
						if (mode_1.get())
							r.dir(r.dir().rotate(rot_speed.get()));
						if (mode_2.get())
							r.pos(r.pos().rotate(rot_speed.get()));
						
					}
				}
			}
		}
	}
	
	public MPatern clear() {
		super.clear(); 
		return this; }
	public MPatern toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}













class MForm extends MBaseMenu { 
	static class MForm_Builder extends MAbstract_Builder {
		MForm_Builder() { super("form", "Form", "basic form", "Work"); }
		MForm build(Macro_Sheet s, sValueBloc b) { MForm m = new MForm(s, b); return m; }
	}
	
	nBase shape;
	sBoo is_line;
	sFlt val_scale, vpax, vpay, vpbx, vpby, vpcx, vpcy, val_linew, val_col_grad;
	sCol val_fill1, val_fill2, val_stroke;
	sInt val_draw_layer;

	Macro_Connexion link;
	MForm(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "form", _bloc); }
	void init() {
		super.init();
	    shape = new nBase(gui.app);
	    is_line = menuBoo(false, "is_line");
	    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");
	    val_scale = menuFltSlide(500, 1, 1000, "scale");
	    shape.dir.setMag(val_scale.get());
	    val_linew = menuFltSlide(0.05F, 0.01F, 1.0F, "line_weight");
	    val_col_grad = menuFltSlide(0.0F, 0.0F, 1.0F, "val_col_grad");
	    shape.line_w = val_linew.get();
	    vpax = newFlt(shape.face.p1.x, "vpax", "vpax");
	    vpay = newFlt(shape.face.p1.y, "vpay", "vpay");
	    vpbx = newFlt(shape.face.p2.x, "vpbx", "vpbx");
	    vpby = newFlt(shape.face.p2.y, "vpby", "vpby");
	    vpcx = newFlt(shape.face.p3.x, "vpcx", "vpcx");
	    vpcy = newFlt(shape.face.p3.y, "vpcy", "vpcy");
	
	    vpax.addEventChange(new nRunnable() { public void run() { shape.face.p1.x = vpax.get(); }});
	    vpay.addEventChange(new nRunnable() { public void run() { shape.face.p1.y = vpay.get(); }});
	    vpbx.addEventChange(new nRunnable() { public void run() { shape.face.p2.x = vpbx.get(); }});
	    vpby.addEventChange(new nRunnable() { public void run() { shape.face.p2.y = vpby.get(); }});
	    vpcx.addEventChange(new nRunnable() { public void run() { shape.face.p3.x = vpcx.get(); }});
	    vpcy.addEventChange(new nRunnable() { public void run() { shape.face.p3.y = vpcy.get(); }});
	    shape.face.p1.x = vpax.get(); 
	    shape.face.p1.y = vpay.get(); 
	    shape.face.p2.x = vpbx.get(); 
	    shape.face.p2.y = vpby.get();
	    shape.face.p3.x = vpcx.get();
	    shape.face.p3.y = vpcy.get();
	    
	    val_stroke = menuColor(gui.app.color(10, 190, 40), "val_stroke");
	    val_stroke.addEventChange(new nRunnable() { public void run() { shape.col_line = val_stroke.get(); }});
	    
	    val_fill1 = menuColor(gui.app.color(30, 90, 20), "val_fill1");
	    val_fill2 = menuColor(gui.app.color(30, 90, 20), "val_fill2");
	    val_fill1.addEventChange(new nRunnable() { public void run() { 
    		float r = val_fill1.getred() * val_col_grad.get() + val_fill2.getred() * (1-val_col_grad.get());
    		float g = val_fill1.getgreen() * val_col_grad.get() + val_fill2.getgreen() * (1-val_col_grad.get());
    		float b = val_fill1.getblue() * val_col_grad.get() + val_fill2.getblue() * (1-val_col_grad.get());
	    		shape.col_fill = gui.app.color(r,g,b); }});
	    val_fill2.addEventChange(new nRunnable() { public void run() { 
    		float r = val_fill1.getred() * val_col_grad.get() + val_fill2.getred() * (1-val_col_grad.get());
    		float g = val_fill1.getgreen() * val_col_grad.get() + val_fill2.getgreen() * (1-val_col_grad.get());
    		float b = val_fill1.getblue() * val_col_grad.get() + val_fill2.getblue() * (1-val_col_grad.get());
	    		shape.col_fill = gui.app.color(r,g,b); }});
	    val_col_grad.addEventChange(new nRunnable() { public void run() { 
    		float r = val_fill1.getred() * val_col_grad.get() + val_fill2.getred() * (1-val_col_grad.get());
    		float g = val_fill1.getgreen() * val_col_grad.get() + val_fill2.getgreen() * (1-val_col_grad.get());
    		float b = val_fill1.getblue() * val_col_grad.get() + val_fill2.getblue() * (1-val_col_grad.get());
	    		shape.col_fill = gui.app.color(r,g,b); }});
	    val_scale.addEventChange(new nRunnable() { public void run() { shape.dir.setMag(val_scale.get()); }});
	    val_linew.addEventChange(new nRunnable() { public void run() { shape.line_w = val_linew.get(); }});
	//    
	}
	void build_normal() {
		super.build_normal();
		link = addOutput(1,"link");
		link.set_link();
	}

  nWidget graph;
  Drawable g_draw;
  
  public void build_custom_menu(nFrontPanel sheet_front) {
    if (sheet_front != null) {
      nDrawer dr = sheet_front.addTab("Shape").getShelf()
    	        .addSeparator(0.125)
    	        .addDrawer(10.25, 6.25);
      
      graph = dr.addModel("Field");
      graph.setPosition(ref_size * 4, ref_size * 2 / 16)
        .setSize(ref_size * 6, ref_size * 6);
      
      g_draw = new Drawable(sheet_front.gui.drawing_pile, 0) { public void drawing() {
        gui.app.fill(graph.look.standbyColor);
        gui.app.noStroke();
        gui.app.rect(graph.getX(), graph.getY(), graph.getSX(), graph.getSY());
        gui.app.pushMatrix();
        gui.app.translate(graph.getX() + graph.getSX()/2 - shape.pos.x, 
                  graph.getY() + graph.getSY()/2 - shape.pos.y);
        float hm = shape.dir.mag();
        float thm = hm;
        if (shape.nrad() > 2) thm = thm / (shape.nrad() / 2);
        if (val_scale.get() < 400) thm *= 400.0F / val_scale.get();
        shape.dir.setMag(thm/4);
        shape.draw(gui.app, is_line.get());
        shape.dir.setMag(hm);
        gui.app.strokeWeight(3);
        gui.app.stroke(255);
        float fac = 1.2F * 100 / val_scale.get();
        gui.app.line(shape.pos.x,shape.pos.y,shape.pos.x + shape.dir.x*fac,shape.pos.y + shape.dir.y*fac);
        gui.app.stroke(180);
        gui.app.line(shape.pos.x,shape.pos.y,shape.pos.x - shape.dir.x*fac,shape.pos.y - shape.dir.y*fac);
        gui.app.popMatrix();
      } };
      graph.setDrawable(g_draw);
      
      point_menu(dr, 0, vpax);
      point_menu(dr, 1, vpay);
      point_menu(dr, 2, vpbx);
      point_menu(dr, 3, vpby);
      point_menu(dr, 4, vpcx);
      point_menu(dr, 5, vpcy);
      
      dr.getShelf().addSeparator(0.125).addDrawer(10.25, 1)
  		.addCtrlModel("Button", "Center").setRunnable(new nRunnable(this) { public void run() { 
  			PVector pa = new PVector(vpax.get(), vpay.get());
  			PVector pb = new PVector(vpbx.get(), vpby.get());
  			PVector pc = new PVector(vpcx.get(), vpcy.get());
  			PVector m = new PVector((pa.x + pb.x + pc.x) / 3.0F, (pa.y + pb.y + pc.y) / 3.0F);
  			vpax.set(pa.x - m.x); vpay.set(pa.y - m.y);
  			vpbx.set(pb.x - m.x); vpby.set(pb.y - m.y);
  			vpcx.set(pc.x - m.x); vpcy.set(pc.y - m.y);
  		}})
  		.setPosition(ref_size * 1.0, ref_size * 0.25)
  		.setSize(ref_size * 3, ref_size * 0.75)
  		.getDrawer()
  		.addCtrlModel("Button", "Normalize").setRunnable(new nRunnable(this) { public void run() {
  			
  		}})
  		.setPosition(ref_size * 6.0, ref_size * 0.25)
  		.setSize(ref_size * 3, ref_size * 0.75)
  		.getShelf()
  	  .addSeparator(0.125);
      
      sheet_front.addEventClose(new nRunnable(this) { public void run() { 
        graph = null; g_draw.clear(); g_draw = null; } } );
      sheet_front.toLayerTop();
    }
  }
  
  private void point_menu(nDrawer dr, int n, sFlt v) {
      dr.addLinkedModel("Field").setLinkedValue(v).setPosition(ref_size * 18 / 16, ref_size * (n+(4.0/16)))
      	.setSize(ref_size * 1.75, ref_size * 0.75);
  }

	public void draw(Rapp a) { 	
		shape.draw(a, is_line.get()); 
	}

	public MForm clear() {
		super.clear(); 
		return this; }
	public MForm toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}












class MTick extends MBaseMenu { 
	static class MTick_Builder extends MAbstract_Builder {
		MTick_Builder() { super("tick", "Tick", "time control", "Work"); }
		MTick build(Macro_Sheet s, sValueBloc b) { MTick m = new MTick(s, b); return m; }
	}
	
	int run_tck_cnt = 0, met_tck_cnt = 0;
	Macro_Connexion tick_out, rst_out, com_in;
	

	  sInt tick_counter; //conteur de tour depuis le dernier reset ou le debut
	  sBoo pause; //permet d'interompre le defilement des tour
	  sInt force_next_tick; 
	  sFlt tick_by_frame; //nombre de tour a executé par frame
	  sFlt tick_sec,tick_time;
	  sInt SEED; //seed pour l'aleatoire
	  sBoo auto_reset, auto_reset_rng_seed, auto_reset_screenshot, show_com;
	  sInt auto_reset_turn;
	  sRun srun_reset, srun_rngr, srun_nxtf, srun_tick, srun_scrsht;
	  sBoo show_toolpanel;

	  float tick_pile = 0; //pile des tick a exec

	
	MTick(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "tick", _bloc); }
	void init() {
		super.init();
	    tick_counter = newInt(0, "tick_counter", "tick");
	    tick_by_frame = newFlt(2, "tick by frame", "tck/frm");
	    tick_sec = newFlt(0, "tick seconde", "tps");
	    tick_time = newFlt(0, "tick duration (ms)", "tls");
	    pause = newBoo(false, "pause", "pause");
	    force_next_tick = newInt(0, "force_next_tick", "nxt tick");
	    auto_reset = newBoo(true, "auto_reset", "auto reset");
	    auto_reset_rng_seed = newBoo(true, "auto_reset_rng_seed", "auto rng");
	    auto_reset_screenshot = newBoo(false, "auto_rest_screenshot", "auto shot");
	    show_com = newBoo(false, "show_com", "show");
	    auto_reset_turn = newInt(4000, "auto_reset_turn", "auto turn");
	    SEED = newInt(548651008, "SEED", "SEED");
	
	    mmain().inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );

	    srun_tick = newRun("sim_tick", "tick", new nRunnable() { 
	    	  public void run() { 
	    		  run_tck_cnt++; 
	    		  while (run_tck_cnt > met_tck_cnt) tick();
	    		  if (tick_out != null) tick_out.send(Macro_Packet.newPacketBang());
	    	} } );
	    srun_reset = newRun("sim_reset", "reset", new nRunnable() { 
	      public void run() { reset(); } } );
	    srun_rngr = newRun("sim_rng_reset", "rst rng", new nRunnable() { 
	      public void run() { resetRng(); } } );
	    srun_nxtf = newRun("sim_next_frame", "nxt frm", new nRunnable() { 
	      public void run() { force_next_tick.set((int)(tick_by_frame.get())); } } );
	    srun_scrsht = newRun("screen_shot", "impr", new nRunnable() { 
	      public void run() { mmain().inter.cam.screenshot = true; } } );
	}
	void build_param() {
		super.build_param();
		
	}
	void build_normal() {
		super.build_normal();
		com_in = addInput(0, "COM");
		tick_out = addOutput(1, "tick");
		rst_out = addOutput(1, "reset");
	}
	

	
	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.getTab(2);
	
	    tab.getShelf()
	      .addDrawer(10.25, 0.6)
	      .addModel("Label-S4", "- Tick Control -").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_counter, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerLargeFieldCtrl(SEED, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(tick_by_frame, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_sec, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_time, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(auto_reset_turn, 1000, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(auto_reset, auto_reset_rng_seed, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(srun_scrsht, auto_reset_screenshot, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(srun_reset, srun_rngr, srun_tick, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(pause, show_com, mmain().inter.cam.grid, 10, 1)
	      .addSeparator(0.125)
	      ;
	  }

  void resetRng() { 
    SEED.set((int)(gui.app.random(1000000000))); 
    reset();
  }
  void reset() {
	gui.app.randomSeed(SEED.get());
    tick_counter.set(0);
    mmain().inter.framerate.reset();
    run_tck_cnt = 0; met_tck_cnt = 0;
    if (rst_out != null) rst_out.send(Macro_Packet.newPacketBang());
  }

  void frame() {
    if (!pause.get()) {
    	tick_sec.set(mmain().inter.framerate.median_framerate.get() * tick_by_frame.get());
    	tick_time.set(1 / tick_sec.get()*1000);

//        tick_pile += tick_by_frame.get();
        tick_pile += tick_by_frame.get();

      //auto screenshot before reset
      if (auto_reset.get() && auto_reset_screenshot.get() &&
        auto_reset_turn.get() == tick_counter.get() + tick_by_frame.get() + tick_by_frame.get()) {
        mmain().inter.cam.screenshot = true;
      }

      while (tick_pile >= 1) {
        tick();
        tick_pile--;
      }

    } else tick_sec.set(0);

    // tick by tick control
    if (pause.get() && force_next_tick.get() > 0) { 
      for (int i = 0; i < force_next_tick.get(); i++) tick(); 
      force_next_tick.set(0);
    }
    if (!pause.get() && force_next_tick.get() > 0) { 
      force_next_tick.set(0);
    }

  }

  void tick() {
	  met_tck_cnt++; 
	  while (run_tck_cnt < met_tck_cnt) srun_tick.run();
    //auto reset
    if (auto_reset.get() && auto_reset_turn.get() <= tick_counter.get()) {
      if (auto_reset_rng_seed.get()) {
        SEED.set((int)(gui.app.random(1000000000)));
      }
      reset();
    }
    tick_counter.set(tick_counter.get()+1);
  }

	public MTick clear() {
		super.clear(); 
		return this; }
	public MTick toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}












