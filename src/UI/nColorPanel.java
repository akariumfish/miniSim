package UI;

import RApplet.RConst;
import sData.nRunnable;
import sData.sCol;

public class nColorPanel extends nWindowPanel {
	  //nColorPanel setOkEvent_Builder(Runnable r) { ok_run = r; ok_run.builder = this; return this; }
	  nWidget color_widget, red_widget, gre_widget, blu_widget, alp_widget;
	  nSlide sl_red, sl_gre, sl_blu, sl_alp;
	  float red, gre, blu, alp;
	  //Runnable ok_run;
	  sCol cval;
	  public nColorPanel(nGUI _g, nTaskPanel _task, sCol _cv) { 
	    super(_g, _task, "color "+_cv.bloc.ref + " " + _cv.ref); 
	    cval = _cv;
	    red = cval.getred(); gre = cval.getgreen(); blu = cval.getblue(); alp = cval.getalpha(); 
	    sl_red = new nSlide(gui, ref_size*7.375F, ref_size);
	    	sl_red.setValue(cval.getred() / 255)
		          .addEventSlide(new nRunnable() { public void run(float v) { 
			            red = v*255; update(); } }).setPosition(0, 0);
		getShelf().addDrawer(10.25F, 1).addWidget(sl_red);
		sl_gre = new nSlide(gui, ref_size*7.375F, ref_size);
		sl_gre.setValue(cval.getgreen() / 255)
		          .addEventSlide(new nRunnable() { public void run(float v) { 
			            gre = v*255; update(); } }).setPosition(0, 0);
		getShelf().addDrawer(10.25F, 1).addWidget(sl_gre);
		sl_blu = new nSlide(gui, ref_size*7.375F, ref_size);
		sl_blu.setValue(cval.getblue() / 255)
		          .addEventSlide(new nRunnable() { public void run(float v) { 
			            blu = v*255; update(); } }).setPosition(0, 0);
		getShelf().addDrawer(10.25F, 1).addWidget(sl_blu);
		sl_alp = new nSlide(gui, ref_size*7.375F, ref_size);
		sl_alp.setValue(cval.getalpha() / 255)
		          .addEventSlide(new nRunnable() { public void run(float v) { 
			            alp = v*255; update(); } }).setPosition(0, 0);
		getShelf().addDrawer(10.25F, 1).addWidget(sl_alp);

	    nDrawer d = getShelf().addDrawer(10.25F, 1);
	    addQuickColor(d, 2, gui.app.color(255, 0, 0, 255));
	    addQuickColor(d, 3, gui.app.color(255, 120, 0, 255));
	    addQuickColor(d, 4, gui.app.color(255, 255, 0, 255));
	    addQuickColor(d, 5, gui.app.color(0, 255, 0, 255));
	    addQuickColor(d, 6, gui.app.color(0, 255, 120, 255));
	    addQuickColor(d, 7, gui.app.color(0, 255, 255, 255));
	    addQuickColor(d, 8, gui.app.color(255, 255, 255, 255));
	    d = getShelf().addDrawer(10.25F, 1);
	    addQuickColor(d, 2, gui.app.color(0, 0, 255, 255));
	    addQuickColor(d, 3, gui.app.color(120, 0, 255, 255));
	    addQuickColor(d, 4, gui.app.color(255, 0, 255, 255));
	    addQuickColor(d, 5, gui.app.color(255, 0, 120, 255));
	    addQuickColor(d, 6, gui.app.color(120, 255, 0, 255));
	    addQuickColor(d, 7, gui.app.color(0, 120, 255, 255));
	    addQuickColor(d, 8, gui.app.color(0, 0, 0, 255));
	    d = getShelf().addDrawer(10.25F, 1);
	    addQuickColor(d, 2, gui.app.color(10, 40, 80, 255));
	    
        getShelf().addDrawer(10.25F, 1)
          .addCtrlModel("Button-S2-P3", "OK")
            .setRunnable(new nRunnable() { public void run() { clear(); } });
        
        getDrawer(0,7).addModel("Label-S3-P1").setStandbyColor(gui.app.color(0)); //background
        getDrawer(0,7).addModel("Label-S3-P1").setSY(0.5F * ref_size)
        		.setStandbyColor(gui.app.color(255)); //background
	    
        color_widget = getDrawer(0,7).addModel("Label-S3-P1")
	          .setStandbyColor(gui.app.color(red, gre, blu, alp));
	    red_widget = getDrawer(0,0)
	        .addModel("Label_Small_Outline-S2", String.valueOf(red)).setPX(7.5F*ref_size);
	    gre_widget = getDrawer(0,1)
	        .addModel("Label_Small_Outline-S2", String.valueOf(gre)).setPX(7.5F*ref_size);
	    blu_widget = getDrawer(0,2)
	        .addModel("Label_Small_Outline-S2", String.valueOf(blu)).setPX(7.5F*ref_size);
	    alp_widget = getDrawer(0,3)
	        .addModel("Label_Small_Outline-S2", String.valueOf(alp)).setPX(7.5F*ref_size);
	    
	    if (cval == null) clear();
	  } 
	void addQuickColor(nDrawer d, int pos, int color) {
		d.addCtrlModel("Button-S1-P"+pos)
        .setRunnable(new nRunnable() { public void run() { 
      	  	red = gui.app.red(color); 
      	  	gre = gui.app.green(color); 
      	  	blu = gui.app.blue(color); 
      		alp = gui.app.alpha(color); update(); update_slider(); } })
        .setStandbyColor(color).getDrawer()
        ;
	}
	void update() { 
	    if (cval != null) {
	    	  	red_widget.setText(RConst.trimFlt(red));
	    	  	gre_widget.setText(RConst.trimFlt(gre));
	    	  	blu_widget.setText(RConst.trimFlt(blu));
	    	  	alp_widget.setText(RConst.trimFlt(alp));
	    	  	color_widget.setStandbyColor(gui.app.color(red, gre, blu, alp)); 
	    	  	cval.set(gui.app.color(red, gre, blu, alp)); }
	    else clear(); }
	void update_slider() { 
	    if (cval != null) {
	    		sl_red.setValue(cval.getred() / 255);
	    		sl_gre.setValue(cval.getgreen() / 255);
	    		sl_blu.setValue(cval.getblue() / 255);
	    		sl_alp.setValue(cval.getalpha() / 255);
	    } }
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	}
