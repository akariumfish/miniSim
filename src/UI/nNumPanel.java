package UI;

import RApplet.RConst;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sFlt;
import sData.sInt;
import sData.sValue;

public class nNumPanel extends nWindowPanel {
	  sValue val;
	  sInt ival; sFlt fval;
	  public nNumPanel(nGUI _g, nTaskPanel _task, sFlt _cv) { 
	    super(_g, _task, "float "+_cv.bloc.ref + " " + _cv.ref); 
	    fval = _cv; if (fval == null) clear();
	    field_run = new nRunnable() { public void run() { 
	      String s = field_widget.getText();
	      if (s.length() > 0 && !PApplet.str(PApplet.parseFloat(s)).equals("NaN")) 
	        fval.set(PApplet.parseFloat(s)); 
	    } };
	    build_ui(fval);
	  } 
	  public nNumPanel(nGUI _g, nTaskPanel _task, sInt _cv) { 
	    super(_g, _task, "int "+_cv.bloc.ref + " " + _cv.ref); 
	    ival = _cv; if (ival == null) clear();
	    field_run = new nRunnable() { public void run() { 
	      String s = field_widget.getText();
	      if (s.length() > 0 && !PApplet.str(PApplet.parseInt(s)).equals("NaN")) 
	        ival.set(PApplet.parseInt(s)); 
	    } };
	    build_ui(ival);
	  } 
	  
	  nWidget field_widget, min_v, max_v, lim_min_v, lim_max_v;
	  nSlide slide;
	  nRunnable slide_run, val_run, field_run;
	  
	  boolean event_paused = false;
	  
	  void up_limit() {
		    min_v.setText(RConst.trimStringFloat(val.getmin(), 4)); 
		    max_v.setText(RConst.trimStringFloat(val.getmax(), 4)); 
		    val.setscale(slide.cursor_value);
		  }
	  
	  void build_ui(sValue v) {
	    
	    val = v;
	    
	    getShelf().addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).getShelf()
	      .addSeparator(0.5)
	      .addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).addCtrlModel("Button-S2-P3", "OK")
	        .setRunnable(new nRunnable() { public void run() { clear(); } }).getDrawer();
	    
	    field_widget = getDrawer(0,0).addModel("Field-S4");
	    field_widget.setText(RConst.trimStringFloat(val.asFlt(), 10))
	      .setField(true)
	      .addEventFieldChange(field_run);
	    
	    slide_run = new nRunnable() { public void run(float v) { 
			if(event_paused) val.pauseEvent();
	      val.setscale(v);
	    } };
	    val_run = new nRunnable() { public void run() { 
		      field_widget.changeText(RConst.trimStringFloat(val.asFlt(), 10));
	      slide.setValue(val.getscale());
	    } };
	    
	    v.addEventChange(val_run);
	    
	    slide = (nSlide)getDrawer(0,1).addWidget(new nSlide(gui, ref_size * 10, ref_size * 1));
	    slide.setValue(val.getscale()).addEventSlide(slide_run);
	    
	    int mod_drawer = 2;
	    do_incr_quatro(mod_drawer, 2, 1, 10, 100, 1000);
	    do_incr_quatro(mod_drawer, 4, -1, -10, -100, -1000);

	    do_mult_quatro(mod_drawer, 6, 1.01F, 1.1F, 2, 10);
	    do_div_quatro(mod_drawer, 8, 1.01F, 1.1F, 2, 10);

	    int limit_set_drawer = 5;
	    max_v = getDrawer(0,limit_set_drawer).addModel("Label_Outline-S1-P7").setSX(ref_size*2.125F)
	      .setText(RConst.trimStringFloat(val.getmax(), 4));
	    min_v = getDrawer(0,limit_set_drawer).addModel("Label_Outline-S1-P2", "min:").setSX(ref_size*2.125F)
	      .setText(RConst.trimStringFloat(val.getmin(), 4));
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N1-P1", "-").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()-1); up_limit(); } }).setInfo("min -1");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N1-P4", "+").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()+1); up_limit(); } }).setInfo("min +1");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N2-P1", "-").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()-10); up_limit(); } }).setInfo("min -10");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N2-P4", "+").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()+10); up_limit(); } }).setInfo("min +10");
	    
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N3-P1", "/").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()/1.41F); up_limit(); } }).setInfo("min /1.41");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N3-P4", "x").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()*1.41F); up_limit(); } }).setInfo("min x1.41");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N4-P1", "/").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()/2); up_limit(); } }).setInfo("min /2");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N4-P4", "x").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_min(val.getmin()*2); up_limit(); } }).setInfo("min x2");
	    
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N1-P6", "-").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()-1); up_limit(); } }).setInfo("max -1");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N1-P9", "+").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()+1); up_limit(); } }).setInfo("max +1");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N2-P6", "-").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()-10); up_limit(); } }).setInfo("max -10");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N2-P9", "+").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()+10); up_limit(); } }).setInfo("max +10");
	      
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N3-P6", "/").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()/1.41F); up_limit(); } }).setInfo("max /1.41");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N3-P9", "x").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()*1.41F); up_limit(); } }).setInfo("max x1.41");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N4-P6", "/").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
	      val.set_max(val.getmax()/2); up_limit(); } }).setInfo("max /2");
	    getDrawer(0,limit_set_drawer).addCtrlModel("Button-N4-P9", "x").setRunnable(new nRunnable() { public void run() { 
			if(event_paused) val.pauseEvent();
		      val.set_max(val.getmax()*2); up_limit(); } }).setInfo("max x2");
	    
	    int switch_drawer = 4;
	    lim_min_v = getDrawer(0,switch_drawer).addModel("Button-S1-P1", "l min").setSwitch().setSwitchState(val.limited_min)
	    		.addEventSwitchOn(new nRunnable() { public void run() { 
	    			if(event_paused) val.pauseEvent();
	    			val.set_limit_min(true); } }).setSX(ref_size*2.125F).setInfo("limited min")
	    		.addEventSwitchOff(new nRunnable() { public void run() { 
	    			if(event_paused) val.pauseEvent();
			    val.set_limit_min(false); } });
	    lim_max_v = getDrawer(0,switch_drawer).addModel("Button-S1-P8", "l max").setSwitch().setSwitchState(val.limited_max)
			.addEventSwitchOn(new nRunnable() { public void run() { 
    			if(event_paused) val.pauseEvent();
				val.set_limit_max(true); } }).setSX(ref_size*2.125F).setInfo("limited max")
			.addEventSwitchOff(new nRunnable() { public void run() { 
    			if(event_paused) val.pauseEvent();
				val.set_limit_max(false); } });

	    getDrawer(0,switch_drawer).addModel("Button-S1-P4", "do_E").setSwitch().setSwitchState(val.doevent())
			.addEventSwitchOn(new nRunnable() { public void run() { 
				val.doEvent(true); } }).setInfo("do_event")
			.addEventSwitchOff(new nRunnable() { public void run() { 
				val.doEvent(false); } });
	    getDrawer(0,switch_drawer).addModel("Button-S1-P6", "P_E").setSwitch().setSwitchState(event_paused)
			.addEventSwitchOn(new nRunnable() { public void run() { 
				event_paused = true; } }).setInfo("pause_event")
			.addEventSwitchOff(new nRunnable() { public void run() { 
				event_paused = false; } });
	  }
	  private void do_incr_quatro(int d, int p, float m1, float m2, float m3, float m4) {
			incr_mod(d, p, 1, m1);
			incr_mod(d, p, 2, m2);
			incr_mod(d, p, 3, m3);
			incr_mod(d, p, 4, m4);
	  }
	  private void do_mult_quatro(int d, int p, float m1, float m2, float m3, float m4) {
		  mult_mod(d, p, 1, m1);
			mult_mod(d, p, 2, m2);
			mult_mod(d, p, 3, m3);
			mult_mod(d, p, 4, m4);
	  }
	  private void do_div_quatro(int d, int p, float m1, float m2, float m3, float m4) {
		  div_mod(d, p, 1, m1);
			div_mod(d, p, 2, m2);
			div_mod(d, p, 3, m3);
			div_mod(d, p, 4, m4);
	  }
	  private void incr_mod(int d, int p, int n, float m) {
		  String pre = "+";
		  if (m < 0) pre = "-";
		  getDrawer(0,d).addCtrlModel("Button-N"+n+"-P"+p, pre)
		  	.setRunnable(new nRunnable() { public void run() { 
		  		if(event_paused) val.pauseEvent();
		      if (ival != null) ival.add((int)m);
		      if (fval != null) { gui.app.logln("add "+m+"to"+fval.ref); fval.add(m); } 
		    } }).setInfo("add "+m);
	  }
	  private void mult_mod(int d, int p, int n, float m) {
		  getDrawer(0,d).addCtrlModel("Button-N"+n+"-P"+p, "x")
		  	.setRunnable(new nRunnable() { public void run() { 
		  		if(event_paused) val.pauseEvent();
		      if (ival != null) ival.mult((int)m);
		      if (fval != null) fval.mult(m); } }).setInfo("mult "+m);
	  }
	  private void div_mod(int d, int p, int n, float m) {
		  getDrawer(0,d).addCtrlModel("Button-N"+n+"-P"+p, "/")
		  	.setRunnable(new nRunnable() { public void run() { 
		  		if(event_paused) val.pauseEvent();
		      if (ival != null) ival.div(m);
		      if (fval != null) fval.div(m); } }).setInfo("div by "+m);
	  }
	  public nWindowPanel clear() { 
	    val.removeEventChange(val_run);
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
