package UI;

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
	      if (s.length() > 0 && !String.valueOf(Float.parseFloat(s)).equals("NaN")) 
	        fval.set(Float.parseFloat(s)); 
	    } };
	    build_ui(fval);
	  } 
	  public nNumPanel(nGUI _g, nTaskPanel _task, sInt _cv) { 
	    super(_g, _task, "int "+_cv.bloc.ref + " " + _cv.ref); 
	    ival = _cv; if (ival == null) clear();
	    field_run = new nRunnable() { public void run() { 
	      String s = field_widget.getText();
	      if (s.length() > 0 && !String.valueOf(Integer.parseInt(s)).equals("NaN")) 
	        ival.set(Integer.parseInt(s)); 
	    } };
	    build_ui(ival);
	  } 
	  
	  nWidget field_widget, min_v, max_v;
	  nSlide slide;
	  nRunnable slide_run, val_run, field_run;
	  
	  void build_ui(sValue v) {
	    
	    val = v;
	    
	    getShelf().addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).getShelf()
	      .addDrawer(10.25F, 1).addCtrlModel("Button-S2-P3", "OK")
	        .setRunnable(new nRunnable() { public void run() { clear(); } }).getDrawer();
	    
	    field_widget = getDrawer(0,0).addModel("Field-S4");
	    field_widget.setText(String.valueOf(val.asFloat()))
	      .setField(true)
	      .addEventFieldChange(field_run);
	    
	    slide_run = new nRunnable() { public void run(float v) { 
	      val.setscale(v);
	    } };
	    val_run = new nRunnable() { public void run() { 
	      slide.setValue(val.getscale());
	      field_widget.changeText(String.valueOf(val.asFloat()));
	    } };
	    
	    v.addEventChange(val_run);
	    
	    slide = (nSlide)getDrawer(0,1).addWidget(new nSlide(gui, ref_size * 10, ref_size * 1));
	    slide.setValue(val.getscale()).addEventSlide(slide_run);
	    
	    max_v = getDrawer(0,2).addModel("Label_Outline-S1-P7").setSX(ref_size*2.125F)
	      .setText(String.valueOf(val.getmax()));
	    min_v = getDrawer(0,2).addModel("Label_Outline-S1-P2", "min:").setSX(ref_size*2.125F)
	      .setText(String.valueOf(val.getmin()));
	    
	    getDrawer(0,2).addCtrlModel("Button-N1-P1", "-").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()-1); up_limit_view(); } }).setInfo("min -1");
	    getDrawer(0,2).addCtrlModel("Button-N1-P4", "+").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()+1); up_limit_view(); } }).setInfo("min +1");
	    getDrawer(0,2).addCtrlModel("Button-N2-P1", "-").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()-10); up_limit_view(); } }).setInfo("min -10");
	    getDrawer(0,2).addCtrlModel("Button-N2-P4", "+").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()+10); up_limit_view(); } }).setInfo("min +10");
	    
	    getDrawer(0,2).addCtrlModel("Button-N3-P1", "/").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()/1.41F); up_limit_view(); } }).setInfo("min /1.41");
	    getDrawer(0,2).addCtrlModel("Button-N3-P4", "x").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()*1.41F); up_limit_view(); } }).setInfo("min x1.41");
	    getDrawer(0,2).addCtrlModel("Button-N4-P1", "/").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()/2); up_limit_view(); } }).setInfo("min /2");
	    getDrawer(0,2).addCtrlModel("Button-N4-P4", "x").setRunnable(new nRunnable() { public void run() { 
	      val.set_min(val.getmin()*2); up_limit_view(); } }).setInfo("min x2");
	    
	    getDrawer(0,2).addCtrlModel("Button-N1-P6", "-").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()-1); up_limit_view(); } }).setInfo("max -1");
	    getDrawer(0,2).addCtrlModel("Button-N1-P9", "+").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()+1); up_limit_view(); } }).setInfo("max +1");
	    getDrawer(0,2).addCtrlModel("Button-N2-P6", "-").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()-10); up_limit_view(); } }).setInfo("max -10");
	    getDrawer(0,2).addCtrlModel("Button-N2-P9", "+").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()+10); up_limit_view(); } }).setInfo("max +10");
	      
	    getDrawer(0,2).addCtrlModel("Button-N3-P6", "/").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()/1.41F); up_limit_view(); } }).setInfo("max /1.41");
	    getDrawer(0,2).addCtrlModel("Button-N3-P9", "x").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()*1.41F); up_limit_view(); } }).setInfo("max x1.41");
	    getDrawer(0,2).addCtrlModel("Button-N4-P6", "/").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()/2); up_limit_view(); } }).setInfo("max /2");
	    getDrawer(0,2).addCtrlModel("Button-N4-P9", "x").setRunnable(new nRunnable() { public void run() { 
	      val.set_max(val.getmax()*2); up_limit_view(); } }).setInfo("max x2");
	    
	  }
	  void up_limit_view() {
	    min_v.setText(String.valueOf(val.getmin())); 
	    max_v.setText(String.valueOf(val.getmax())); 
	    val.setscale(slide.cursor_value);
	  }
	  public nWindowPanel clear() { 
	    val.removeEventChange(val_run);
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
