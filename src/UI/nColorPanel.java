package UI;

import RApplet.RConst;
import sData.nRunnable;
import sData.sCol;

public class nColorPanel extends nWindowPanel {
	  //nColorPanel setOkEvent_Builder(Runnable r) { ok_run = r; ok_run.builder = this; return this; }
	  nWidget color_widget, red_widget, gre_widget, blu_widget, alp_widget;
	  float red, gre, blu, alp;
	  //Runnable ok_run;
	  sCol cval;
	  public nColorPanel(nGUI _g, nTaskPanel _task, sCol _cv) { 
	    super(_g, _task, "color "+_cv.bloc.ref + " " + _cv.ref); 
	    cval = _cv;
	    red = cval.getred(); gre = cval.getgreen(); blu = cval.getblue(); alp = cval.getalpha(); 
	    getShelf()
	      .addDrawer(10.25F, 1)
	        .addWidget(new nSlide(gui, ref_size*7.375F, ref_size).setValue(cval.getred() / 255)
	          .addEventSlide(new nRunnable() { public void run(float v) { 
	            red = v*255; update(); red_widget.setText(RConst.trimFlt(red)); } } )
	          .setPosition(0, 0) ).getShelf()
	      .addDrawer(10.25F, 1)
	        .addWidget(new nSlide(gui, ref_size*7.375F, ref_size).setValue(cval.getgreen() / 255)
	          .addEventSlide(new nRunnable() { public void run(float v) { 
	            gre = v*255; update(); gre_widget.setText(RConst.trimFlt(gre)); } } )
	          .setPosition(0, 0) ).getShelf()
	      .addDrawer(10.25F, 1)
	        .addWidget(new nSlide(gui, ref_size*7.375F, ref_size).setValue(cval.getblue() / 255)
	          .addEventSlide(new nRunnable() { public void run(float v) { 
	            blu = v*255; update(); blu_widget.setText(RConst.trimFlt(blu)); } } )
	          .setPosition(0, 0) ).getShelf()
	      .addDrawer(10.25F, 1)
	        .addWidget(new nSlide(gui, ref_size*7.375F, ref_size).setValue(cval.getalpha() / 255)
	          .addEventSlide(new nRunnable() { public void run(float v) { 
	            alp = v*255; update(); alp_widget.setText(RConst.trimFlt(alp)); } } )
	          .setPosition(0, 0) ).getShelf()
	      .addDrawer(10.25F, 1)
	        .addCtrlModel("Button-S2-P3", "OK")
	          .setRunnable(new nRunnable() { public void run() { clear(); } }).getDrawer()
	          ;
	        
	    color_widget = getDrawer(0,4).addModel("Label-S3-P1")
	          .setStandbyColor(gui.app.color(red, gre, blu));
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
	void update() { 
	    if (cval != null) {
	      color_widget.setStandbyColor(gui.app.color(red, gre, blu, alp)); 
	      cval.set(gui.app.color(red, gre, blu, alp)); }
	    else clear(); }
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	}
