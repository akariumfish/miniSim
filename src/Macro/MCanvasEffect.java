package Macro;

import RApplet.Rapp;
import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nGUI;
import processing.core.PConstants;
import processing.core.PImage;
import sData.*;

public abstract class MCanvasEffect  extends MBaseMenu {
	
	Rapp app;
	sInterface inter;
  	nGUI cam_gui;
  	float ref_size;
  
  	Macro_Connexion link_out;
  	MCanvas canvas;

  	MCanvasEffect(Macro_Sheet _sheet, String _n, sValueBloc _bloc) { 
		super(_sheet, _n, _bloc, "caneffect"); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		init_access();
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
		link_out = addOutput(2, "Link_out").set_link();
		link_out.addEventChangeLink(new nRunnable() { public void run() { 
			MCanvas prev_set = canvas;
			canvas = null;
		  	for (Macro_Connexion c : link_out.connected_inputs) {
		  		if (c.elem.bloc.val_type.get().equals("mcanvas"))
		  			canvas = (MCanvas)c.elem.bloc;
		  	}
		  	if (canvas != prev_set) reconnect();
		}});
	    super.build_normal(); 
	}
	void init_end() {  super.init_end();  }
	void rebuild() {  
		super.rebuild();
	}
	public MCanvasEffect clear() {
		super.clear(); 
		return this; }

	void reconnect() { }
	
	float sat(int c) {
		return (gui.app.alpha(c) / 255.0F) * 
				(gui.app.red(c) + gui.app.green(c) + gui.app.blue(c)) / 3.0F; }
  
	public abstract void clear_pim(PImage img);
	public abstract void tick_end(PImage img);
	public abstract void reset();
	public abstract void drawCanvas();
	public abstract void draw_pim(PImage img);
}

class MCanBrush extends MCanvasEffect {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("canbrush", "CanvasBrush", 
				"Canvas Brush", "Set Tool"); 
		first_start_show(m); }
		MCanBrush build(Macro_Sheet s, sValueBloc b) { MCanBrush m = new MCanBrush(s, b); return m; }
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
	    sheet_front.getTab(2).getShelf()
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-Brush Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerFactValue(brush_size, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(brush_dens, 2, 10, 1)
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
	  sBoo do_brush; //, do_rain3
	  sFlt brush_size, brush_dens;
	  sCol val_col_brush;
	
	MCanBrush(Macro_Sheet _sheet, sValueBloc _bloc) {
		super(_sheet, "canbrush", _bloc);
	}
	public void init() {
		super.init();
	    do_brush = newBoo(false, "do_brush", "do_brush");
	    brush_size = newFlt(30, "brush_size", "brush_size");
	    brush_dens = newFlt(1, "brush_dens", "brush_dens");
	    val_col_brush = menuColor(gui.app.color(255, 0, 0), "val_col_brush");
	    globalBin(do_brush, false);
	}
	
	public void clear_pim(PImage can) {
		
	}
	
	public void tick_end() {
		if (canvas != null && do_brush.get() && gui.in.getState("MouseRight")) {
			canvas.draw_halo(gui.mouseVector, 
					brush_size.get(), brush_dens.get(), 
					val_col_brush.get());
		}
	    
	}
	public void tick_end(PImage img) {
		
	}
	public void reset() {
		
	}
	@Override
	public void drawCanvas() {
		if (do_brush.get() && gui.in.getState("MouseRight")) {
			gui.app.noFill();
			gui.app.stroke(255);
			gui.app.ellipse(gui.mouseVector.x - brush_size.get() / 2.0F, 
					gui.mouseVector.y - brush_size.get() / 2.0F, 
					brush_size.get(), brush_size.get());
		}
		
	}
	@Override
	public void draw_pim(PImage img) {
		// TODO Auto-generated method stub
		
	}
}

class MCanRain extends MCanvasEffect {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("canrain", "CanvasRain", 
				"Canvas Rain Effect", "Set Tool"); 
		first_start_show(m); }
		MCanRain build(Macro_Sheet s, sValueBloc b) { MCanRain m = new MCanRain(s, b); return m; }
	}
	
	public void build_custom_menu(nFrontPanel sheet_front) {
	    sheet_front.getTab(2).getShelf()
//	      .addModel("Label-S4", "-Rain Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(do_rain1, do_rain2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(rain_strength, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(rain_dir, 1, 10, 1)
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
	
	  sBoo do_rain1, do_rain2; //, do_rain3
	  sFlt rain_strength;
	  sInt rain_dir;
	
	MCanRain(Macro_Sheet _sheet, sValueBloc _bloc) {
		super(_sheet, "canrain", _bloc);
	}
	public void init() {
		super.init();

	    do_rain1 = newBoo(false, "do_rain1", "do_rain1");
	    do_rain2 = newBoo(false, "do_rain2", "do_rain2");
//	    do_rain3 = newBoo(false, "do_rain3", "do_rain3");
	    rain_strength = newFlt(1, "rain_strength", "rain_strength");
	    rain_dir = newInt(0, "rain_dir", "rain_dir");
	}
	
	public void clear_pim(PImage can) {
		
	}
	
	public void tick_end(PImage img) {
		rain(img);
	}

	  void rain(PImage can) {
		  for (int i = 0 ; i < can.pixels.length ; i++) {
			if (do_rain1.get()) {
			  if (i+canvas.val_w.get() < can.pixels.length) {
				  if (sat(can.pixels[i]) - rain_strength.get() > 
			  	  sat(can.pixels[i+canvas.val_w.get()])) {
				  can.pixels[i] = gui.app.color(
						  gui.app.red(can.pixels[i]) - rain_strength.get()/3.0F, 
						  gui.app.green(can.pixels[i]) - rain_strength.get()/3.0F, 
						  gui.app.blue(can.pixels[i]) - rain_strength.get()/3.0F 
	//						  gui.app.alpha(can.pixels[i]) - rain_strength.get()/4
						  );
				  int j = i+canvas.val_w.get();
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
				if (i+canvas.val_w.get() < can.pixels.length) {
					int id_to = i+canvas.val_w.get()-rain_dir.get();
					if (id_to >= 0 && id_to < can.pixels.length) {
						if (gui.app.red(can.pixels[i]) - rain_strength.get() >=
							gui.app.red(can.pixels[id_to]) + rain_strength.get() && 
							gui.app.red(can.pixels[i]) - rain_strength.get() >= 0 && 
							gui.app.red(can.pixels[id_to]) + rain_strength.get() <= 255) {
						  can.pixels[i] = gui.app.color(
								  gui.app.red(can.pixels[i]) - rain_strength.get(), 
								  gui.app.green(can.pixels[i]), 
								  gui.app.blue(can.pixels[i]), 
								  gui.app.alpha(can.pixels[i]) );
						  int j = i+canvas.val_w.get();
						  can.pixels[j] = gui.app.color(
								  gui.app.red(can.pixels[j]) + rain_strength.get(), 
								  gui.app.green(can.pixels[j]), 
								  gui.app.blue(can.pixels[j]), 
								  gui.app.alpha(can.pixels[j]) );
						}  
						if (gui.app.green(can.pixels[i]) - rain_strength.get() >=
							gui.app.green(can.pixels[id_to]) + rain_strength.get() && 
							gui.app.green(can.pixels[i]) - rain_strength.get() >= 0 && 
							gui.app.green(can.pixels[id_to]) + rain_strength.get() <= 255) {
						  can.pixels[i] = gui.app.color(
								  gui.app.red(can.pixels[i]), 
								  gui.app.green(can.pixels[i]) - rain_strength.get(), 
								  gui.app.blue(can.pixels[i]), 
								  gui.app.alpha(can.pixels[i]) );
						  int j = i+canvas.val_w.get();
						  can.pixels[j] = gui.app.color(
								  gui.app.red(can.pixels[j]), 
								  gui.app.green(can.pixels[j]) + rain_strength.get(), 
								  gui.app.blue(can.pixels[j]), 
								  gui.app.alpha(can.pixels[j]) );
						}  
						if (gui.app.blue(can.pixels[i]) - rain_strength.get() >=
							gui.app.blue(can.pixels[id_to]) + rain_strength.get() && 
							gui.app.blue(can.pixels[i]) - rain_strength.get() >= 0 && 
							gui.app.blue(can.pixels[id_to]) + rain_strength.get() <= 255) {
						  can.pixels[i] = gui.app.color(
								  gui.app.red(can.pixels[i]), 
								  gui.app.green(can.pixels[i]), 
								  gui.app.blue(can.pixels[i]) - rain_strength.get(), 
								  gui.app.alpha(can.pixels[i]) );
						  int j = i+canvas.val_w.get();
						  can.pixels[j] = gui.app.color(
								  gui.app.red(can.pixels[j]), 
								  gui.app.green(can.pixels[j]), 
								  gui.app.blue(can.pixels[j]) + rain_strength.get(), 
								  gui.app.alpha(can.pixels[j]) );
						}  
					}
				}
			}
		}
	//		if (do_rain3.get()) {
	//			for (int i = 0 ; i < can.pixels.length ; i++) {
	//				if (i+canvas.val_w.get() < can.pixels.length) {
	//					transfer_pixel(can, can, i, i+canvas.val_w.get(), (int)rain_strength.get());
	//				}
	//			}
	//		}
	  }
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawCanvas() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw_pim(PImage img) {
		// TODO Auto-generated method stub
		
	}
}


class MCanBack extends MCanvasEffect {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("canback", "CanvasBack", 
				"Canvas Back", "Set Tool"); 
		first_start_show(m); }
		MCanBack build(Macro_Sheet s, sValueBloc b) { MCanBack m = new MCanBack(s, b); return m; }
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
	    sheet_front.getTab(2).getShelf()
	      .addSeparator(0.125)
	      .addDrawerFieldCtrl(back_file, 10, 1)
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

	  sBoo val_show_back;
	  PImage back;
	  sRun back_clear, back_add, back_fill, back_save, back_load;
	  sStr back_file;
	  
	
	MCanBack(Macro_Sheet _sheet, sValueBloc _bloc) {
		super(_sheet, "canback", _bloc);
	}
	public void init() {
		super.init();
	    val_show_back = menuBoo(false, "val_show_back");

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
	    back_file = newStr("back_file", "canvas");
	    menuRun(back_clear, back_fill, back_add);
	    menuRun(back_save, back_load);
	}
	
	public void clear_pim(PImage can) {
		
	}
	
	public void tick_end() {
		
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
	    back = gui.app.createImage(canvas.val_w.get(), canvas.val_h.get(), PConstants.ARGB);
	    for(int i = 0; i < back.pixels.length; i++) {
	    		back.pixels[i] = gui.app.color(0); 
	    }
	  }
	  void fillBack() {
	    back = gui.app.createImage(canvas.val_w.get(), canvas.val_h.get(), PConstants.ARGB);
	    for(int i = 0; i < back.pixels.length; i++) {
	    		back.pixels[i] = gui.app.color(0);  
	    }
	  }
	  void addToBack() {
		  PImage t = null;
	      if (canvas.active_can == 0) t = canvas.can1;
	      else if (canvas.active_can == 1) t = canvas.can2;
		  for(int i = 0; i < back.pixels.length; i++) {
			  float a = gui.app.alpha(t.pixels[i]) / 255.0F;
			  back.pixels[i] = gui.app.color(
					  			gui.app.red(back.pixels[i]) + gui.app.red(t.pixels[i]) * a, 
		             			gui.app.green(back.pixels[i]) + gui.app.green(t.pixels[i]) * a, 
		             			gui.app.blue(back.pixels[i]) + gui.app.blue(t.pixels[i]) * a
		             		); 
		  }
	  }
	@Override
	public void tick_end(PImage img) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawCanvas() {
	    if (val_show_back.get()) canvas.draw(back, false);
	}
	public void draw_pim(PImage img) {
		
	}
	  

}


class MCanDecay extends MCanvasEffect {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("candecay", "CanvasDecay", 
				"Canvas Decay", "Set Tool"); 
		first_start_show(m); }
		MCanDecay build(Macro_Sheet s, sValueBloc b) { MCanDecay m = new MCanDecay(s, b); return m; }
	}
	  sInt rate_decay;
	  sFlt color_keep_thresh, decay_fact;

	MCanDecay(Macro_Sheet _sheet, sValueBloc _bloc) {
		super(_sheet, "candecay", _bloc);
	}
	public void init() {
		super.init();
	    color_keep_thresh = menuFltSlide(0, 0, 260, "clrkeep_thresh");
	    decay_fact = menuFltSlide(0.95F, 0.9F, 1.0F, "decay_fact");
	    rate_decay = menuIntIncr(20, 10, "rate_decay");
	}
	
	int decay(int c) {
	    return gui.app.color(1.0F*gui.app.red(c)*decay_fact.get(), 
	    					1.0F*gui.app.green(c)*decay_fact.get(), 
	    					1.0F*gui.app.blue(c)*decay_fact.get(), 
	    		             gui.app.alpha(c));//*val_decay.get()); 
	}

	int dc_cnt = 0;
	public void clear_pim(PImage can) {
		dc_cnt++;
	  	if (dc_cnt >= rate_decay.get()) 
	  		dc_cnt = 0;
	    for (int i = 0 ; i < can.pixels.length ; i++) {
	    		if (sat(can.pixels[i]) < color_keep_thresh.get() && canvas != null) 
	    			can.pixels[i] = canvas.val_col_def.get(); 
	    		else if (dc_cnt == 0) 
	    			can.pixels[i] = decay(can.pixels[i]);
	    	}
	}
	@Override
	public void tick_end(PImage img) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawCanvas() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw_pim(PImage img) {
		// TODO Auto-generated method stub
		
	}
}
