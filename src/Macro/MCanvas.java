package Macro;

import java.util.ArrayList;

import Macro.MSet.SetObj;
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
	      .addDrawerButton(val_show, val_show_grab, val_show_bound, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerButton(val_rst_run, val_centered, 10, 1)
	      .addSeparator(0.125)
	      ;
	    
	    nFrontTab tab = sheet_front.addTab("Effect");
	    tab.getShelf()
//	      .addDrawer(10.25, 0.5)
//	      .addModel("Label-S4", "-Back Control-").setFont((int)(ref_size/1.4)).getShelf()
//	      .addSeparator(0.125)
//	      .addDrawerButton(back_clear, back_fill, back_add, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerButton(back_save, back_load, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerFieldCtrl(back_file, 10, 1)
//	      .addDrawer(10.25, 0.5)
//	      .addModel("Label-S4", "-Rain Control-").setFont((int)(ref_size/1.4)).getShelf()
//	      .addSeparator(0.125)
//	      .addDrawerButton(do_rain1, do_rain2, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerFactValue(rain_strength, 2, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerIncrValue(rain_dir, 1, 10, 1)
//	      .addDrawer(10.25, 0.5)
//	      .addModel("Label-S4", "-Brush Control-").setFont((int)(ref_size/1.4)).getShelf()
//	      .addSeparator(0.125)
//	      .addDrawerButton(do_brush, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerFactValue(brush_size, 2, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerFactValue(brush_dens, 2, 10, 1)
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
	Macro_Connexion link_cam, link_group, link_effect;
	
	ArrayList<MCanvasEffect> effects;
	
	MCanvas(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "mcanvas", _bloc); 
	}
	public void init() {
		super.init();
		init_access();
		inter = mmain().inter;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		effects = new ArrayList<MCanvasEffect>();
		init_canvas();
	}
	void build_param() { /*super.build_param();*/ build_normal(); }
	void build_normal() { 
		super.build_normal(); 
		link_cam = addInput(0, "Masking Cams").set_link();
		link_group = addInput(0, "Groups").set_link();
		link_effect = addInput(0, "link_effect").set_link();
		link_effect.addEventChangeLink(new nRunnable() { public void run() { 
			effects.clear();
			for (Macro_Connexion c : link_effect.connected_outputs) {
				if (c.elem.bloc.bloc_specialization.equals("caneffect"))
					effects.add((MCanvasEffect)c.elem.bloc);
			}
		}});
		addEmptyS(1); addEmptyS(1);
	}
	void rebuild() { super.rebuild(); }
	void init_end() { super.init_end(); }
	

	  nRunnable rst_run, setup_run; Drawable cam_draw;
	  sRun val_rst_run;
	  nCursor ref_cursor;
	
	  sVec val_pos;
	  sInt val_w, val_h, can_div;
	  sCol val_col_def;
	  sFlt val_scale, val_scale_fine, val_bound_thick;
	  sBoo val_show, val_show_bound, val_show_grab, val_centered;
	  sCol val_col_bound;

	  PImage can1,can2;
	  int active_can = 0;
	  int can_st;

	  PShader shader_blur;
	  PShader shader_mask;
	  PGraphics mask_image;
	  sBoo use_blur, use_mask;
	
	void init_canvas() {
	    int def_pix_size = 5;
	    int def_size_w = (int) (app.width * 2.5F / def_pix_size);
	    int def_size_h = (int) (app.height * 2.5F / def_pix_size);
	    def_size_w -= def_size_w % 10;
	    def_size_h -= def_size_h % 10;
	    val_pos = newVec("val_pos", "val_pos");
	    val_w = menuIntIncr(def_size_w, 100, "val_w");
	    val_h = menuIntIncr(def_size_h, 100, "val_h");
	    can_div = menuIntIncr(1, 1, "can_div");
	    can_div.set_min(0);
	    val_scale = menuFltSlide(1, 1, 150, "val_scale");
	    val_scale_fine = menuFltSlide(def_pix_size - 1, 0, 15, "fine_scale");
	    val_show = newBoo(true, "val_show", "show_canvas");
	    val_show_bound = newBoo(true, "val_show_bound", "show_bound");
	    val_show_grab = newBoo(false, "val_show_grab", "show_grab");
	    val_centered = newBoo(true, "val_centered", "val_centered");
	    val_col_def = menuColor(gui.app.color(0), "background");
	    val_col_def.addEventChange(new nRunnable() { public void run() { reset(); } });
	    val_col_bound = menuColor(gui.app.color(255), "col_bound");
	    val_bound_thick = menuFltSlide(2.0F, 0, 8.0F, "val_bound_thick");

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
	    
	    reset();
	    setup_run = new nRunnable() { public void run() { 
		      mmain().inter.addEventNextFrame(new nRunnable() {public void run() { 
			    	    reset();
//			        cam_draw.toLayerBottom();
			      }}); } } ;
	    addEventSetupLoad(setup_run);
	  }

	  private void init_pim(PImage canvas) {
	    for(int i = 0; i < canvas.pixels.length; i++) {
	      canvas.pixels[i] = val_col_def.get(); 
	    }
	  }

	  float getscale() { return val_scale.get() + val_scale_fine.get(); }  

	  void clear_pim(PImage canvas) {
		  for (MCanvasEffect e : effects) e.clear_pim(canvas);
	  }
	  void med_pim(PImage canvas1, PImage canvas2) {
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
//		for (Macro_Connexion c : link_group.connected_outputs) {
////			MFlocGroup fcom = null;
////			MSolidGroup solidg = null;
////			if (c.elem.bloc.val_type.get().equals("flocs")) {
////				fcom = (MFlocGroup)c.elem.bloc;
////			} 
////			if (c.elem.bloc.val_type.get().equals("solids")) {
////				solidg = (MSolidGroup)c.elem.bloc;
////			}
////			
////		    if (fcom != null) {
////		      for (int i = can_st ; i < fcom.entity_list.size() ; i += Math.max(1, can_div.get()) )
////		        if (fcom.entity_list.get(i).active) {
////		          ((EFloc)fcom.entity_list.get(i)).draw_halo(this);
////		      }
////		    }
////		    if (solidg != null) { 
////		    		for (ESolid s : solidg.solid_list) s.draw_halo(this);
////		    }
//		    
//			if (c.elem.bloc.val_type.get().equals("set")) {
//				MSet set = (MSet)c.elem.bloc;
//				for (SetObj o : set.objects) o.draw_halo(this);
//			} 
//		}

		if (active_can == 0) for (MCanvasEffect e : effects) e.tick_end(can2);
		else if (active_can == 1) for (MCanvasEffect e : effects) e.tick_end(can1);
		
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
		for (MCanvasEffect e : effects) e.drawCanvas();
		
		for (Macro_Connexion c : link_group.connected_outputs) {
			if (c.elem.bloc.val_type.get().equals("set")) {
				MSet set = (MSet)c.elem.bloc;
				for (SetObj o : set.objects) o.draw_halo(this);
			} 
		}
		
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
	  }

	  void draw(PImage canvas, boolean shadering) {
		for (MCanvasEffect e : effects) e.draw_pim(canvas);
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
	  
	  public void draw_halo(PVector pos, float halo_size, float halo_density, 
			  int col_int, int col_ext, float intfct) {
	    //walk a box of pix around entity containing the halo (pos +/- halo radius)
	    for (float px = (int)(pos.x - halo_size) ; px < (int)(pos.x + halo_size) ; px+=getscale())
	      for (float py = (int)(pos.y - halo_size) ; py < (int)(pos.y + halo_size) ; py+=getscale()) {
	        PVector m = new PVector(pos.x - px, pos.y - py);
	        if (m.mag() < halo_size) { //get and try distence of current pix
	          //the color to add to the current pix is function of his distence to the center
	          //the decreasing of the quantity of color to add is soothed
	        	  float a1 = 0, a2 = 0, discr = m.mag() / halo_size, 
	        			  int_dwn = intfct / 2.0F, int_up = intfct + (1-intfct) / 2.0F;
	        	  if (discr < int_dwn) {
	        		  a1 = (halo_density) * RConst.soothedcurve(1.0F, 
	        				  discr / int_up );
	        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
	        				  (1 - discr / intfct) );
	        	  } else if (discr > int_dwn && discr < int_up) {
	        		  if (discr < intfct) {
	        			  a1 = (halo_density) * RConst.soothedcurve(1.0F, 
	        					  discr / int_up );
		        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
		        				  (1 - discr / intfct) );
	        		  } else {
	        			  a1 = (halo_density) * RConst.soothedcurve(1.0F, 
	        					  discr / int_up );
		        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
		        				  (discr - intfct) / (1 - intfct) );
	        		  }
	        	  } else {
	        		  a1 = 0;
	        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
	        				  (discr - intfct) / (1 - intfct) );
	        	  }
	          PImage pi = null;
	          if (active_can == 0) pi = can1;
	          if (active_can == 1) pi = can2;
        	  	  addpix(pi, px, py, gui.app.color(gui.app.red(col_int)*a1 + gui.app.red(col_ext)*a2, 
        	  								gui.app.green(col_int)*a1 + gui.app.green(col_ext)*a2, 
        	  								gui.app.blue(col_int)*a1 + gui.app.blue(col_ext)*a2, 
        	  			  					gui.app.alpha(col_int)*a1 + gui.app.alpha(col_ext)*a2));
	        }
	    }
	}
	  
	  public void draw_line_halo(PVector p1, PVector p2, 
			  					float halo_size, float halo_density, 
			  					int col_int, int col_ext, float intfct) {
		  float xmin = PApplet.min(p1.x, p2.x);
		  float xmax = PApplet.max(p1.x, p2.x);
		  float ymin = PApplet.min(p1.y, p2.y);
		  float ymax = PApplet.max(p1.y, p2.y);
		  for (float px = xmin - halo_size ; px < xmax + halo_size ; px += getscale())
		  for (float py = ymin - halo_size ; py < ymax + halo_size ; py += getscale()) {
			  float d = RConst.distancePointToLine(px, py, p1.x, p1.y, p2.x, p2.y);
			  if (d < halo_size) {
				  float a1 = 0, a2 = 0, discr = d / halo_size, 
						  int_dwn = intfct / 2.0F, int_up = intfct + (1-intfct) / 2.0F;
		        	  if (discr < int_dwn) {
		        		  a1 = (halo_density) * RConst.soothedcurve(1.0F, 
		        				  discr / int_up );
		        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
		        				  (1 - discr / intfct) );
		        	  } else if (discr > int_dwn && discr < int_up) {
		        		  if (discr < intfct) {
		        			  a1 = (halo_density) * RConst.soothedcurve(1.0F, 
		        					  discr / int_up );
			        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
			        				  (1 - discr / intfct) );
		        		  } else {
		        			  a1 = (halo_density) * RConst.soothedcurve(1.0F, 
		        					  discr / int_up );
			        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
			        				  (discr - intfct) / (1 - intfct) );
		        		  }
		        	  } else {
		        		  a1 = 0;
		        		  a2 = (halo_density) * RConst.soothedcurve(1.0F, 
		        				  (discr - intfct) / (1 - intfct) );
		        	  }
		          PImage pi = null;
		          if (active_can == 0) pi = can1;
		          if (active_can == 1) pi = can2;
		          addpix(pi, px, py, gui.app.color(
		        		  		gui.app.red(col_int)*a1 + gui.app.red(col_ext)*a2, 
							gui.app.green(col_int)*a1 + gui.app.green(col_ext)*a2, 
							gui.app.blue(col_int)*a1 + gui.app.blue(col_ext)*a2, 
		  					gui.app.alpha(col_int)*a1 + gui.app.alpha(col_ext)*a2));
		      }
		  }
	  }

	  void addpix(PImage canvas, float x, float y, int nc) {
		  if (val_centered.get()) {
			  x -= val_pos.get().x - val_w.get() * getscale() / 2;
			  y -= val_pos.get().y - val_h.get() * getscale() / 2;
		  } else {
			  x -= val_pos.get().x;
			  y -= val_pos.get().y;
		  }
		  x /= getscale();
		  y /= getscale();
		  if (x < 0 || y < 0 || x > canvas.width || y > canvas.height) return;
		  int pi = canvas.width * (int)(y) + (int)(x);
		  if (pi >= 0 && pi < canvas.pixels.length) {
			  int oc = canvas.pixels[pi];
			  canvas.pixels[pi] = RConst.add_color(oc, nc, 0, gui.app); }
	  }
	  
	  
//	  void transfer_pixel(PImage from_can, PImage to_can, int from, int to, int amount) {
//		  if (from_can.pixels.length != to_can.pixels.length) return;
//		  if (from < 0 || from > from_can.pixels.length) return;
//		  if (to < 0 || to > from_can.pixels.length) return;
//		  Rapp ap = gui.app;
//		  int rf = (int)ap.red(from_can.pixels[from]);
//		  int gf = (int)ap.green(from_can.pixels[from]);
//		  int bf = (int)ap.blue(from_can.pixels[from]);
//		  int af = (int)ap.alpha(from_can.pixels[from]);
//		  int rt = (int)ap.red(from_can.pixels[to]);
//		  int gt = (int)ap.green(from_can.pixels[to]);
//		  int bt = (int)ap.blue(from_can.pixels[to]);
//		  int at = (int)ap.alpha(from_can.pixels[to]);
//		  int[] result = transfer_color(rf, rt, amount);
//		  rf = result[0];
//		  rt = result[1];
//		  result = transfer_color(gf, gt, amount);
//		  gf = result[0];
//		  gt = result[1];
//		  result = transfer_color(bf, bt, amount);
//		  bf = result[0];
//		  bt = result[1];
//		  to_can.pixels[from] = ap.color(rf,gf,bf,af);
//		  to_can.pixels[to] = ap.color(rt,gt,bt,at);
//	  }
//	  int[] transfer_color(int from, int to, int amount) {
//		  if (from - amount < 0) amount = from;
//		  if (to + amount > 255) amount = 255 - to;
//		  int[] result = new int[3];
//		  result[0] = from - amount;
//		  result[1] = to + amount;
//		  result[2] = amount;
//		  return result;
//	  }
	  
}
