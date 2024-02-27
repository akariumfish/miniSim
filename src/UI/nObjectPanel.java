package UI;

import Macro.Macro_Interf;
import RApplet.RConst;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sObj;

public class nObjectPanel extends nWindowPanel implements Macro_Interf  { //
	  nWidget text_widget;
	  sObj cval;
	  String txt; boolean auto_return = true;
	  nRunnable val_run;
	  int larg = 20;
	  int max_l; float line_cnt, h_fact;
	  int font = (int)(ref_size/2.1F);
	  private void up_text() {

		if (text_widget != null) text_widget.setFont((int)(font));
	    gui.app.textFont(gui.app.getFont((int)font));
	    
//		    int max_l = (int)(larg * ref_size / (font / 1.7F));
	    max_l = (int)(larg * ref_size / gui.app.textWidth('m'));
	    
	    txt = ""+cval.get();
	    
	    line_cnt = (float)(txt.length()) / (float)(max_l);
	    if (line_cnt % 1 > 0) line_cnt += 1 - (line_cnt % 1);
	    
	    if (cval.ref.equals("links") || cval.ref.equals("spots")) {
	      auto_return = false;
	      line_cnt = 1;
	      int char_counter = 0;
	      for (int i = 0 ; i < txt.length() ; i++) {
	        char_counter++;
	        if (char_counter >= max_l) { line_cnt++; char_counter = 0; }
	        if (txt.charAt(i) == OBJ_TOKEN.charAt(0) || txt.charAt(i) == GROUP_TOKEN.charAt(0)) {
	          txt = txt.substring(0, i+1) + '\n' + txt.substring(i+1, txt.length());
	          line_cnt++;
	          char_counter = 0;
	        }
	      }
	    }

	    h_fact = (font * line_cnt / 0.7F) / ref_size;
	    
	    if (text_widget != null) { 
	    		text_widget.changeText(txt)
	  	      .setSY(ref_size * h_fact);
	    }
	  }
	  public nObjectPanel(nGUI _g, nTaskPanel _task, sObj _cv) { 
	    super(_g, _task, "string "+_cv.bloc.ref + " " + _cv.ref); 
	    cval = _cv;
	    
	    if (cval == null) clear();
	    if ((""+cval.get()).length() < 50) larg = 10;
	    
	    up_text();
	    
	    getShelf()
	      .addDrawer(larg, 1)
	        .addCtrlModel("Button-S2", "OK")
	          .setRunnable(new nRunnable() { public void run() { 
	            for (int i = txt.length() - 1 ; i >= 0  ; i--) 
	              if (txt.charAt(i) == '\n' || txt.charAt(i) == '\r') {
	                String t2 = txt.substring(0, i);
	                if (i+1 < txt.length()) t2 += txt.substring(i + 1, txt.length());
	                txt = RConst.copy(t2);
	            }
	            if (cval != null) cval.set(txt);
	            clear(); 
	          } })
	          .setPX(ref_size*(larg-2.5)/2).getDrawer()
	        .getShelf()
	      .addSeparator(0.125)
	      .addDrawer(larg + 0.25F, h_fact)
        ;
	    
	    text_widget = getDrawer(0,1).addModel("Field")
	    	  .setText(txt)
	      .setField(true)
	      .setTextAlignment(PApplet.CENTER, PApplet.TOP)
	      .setTextAutoReturn(auto_return)
	      .setFont((int)(font))
	      .setSX(ref_size * larg)
	      .setSY(ref_size * h_fact)
	      .setPX(ref_size * 0.125F)
	      ;
	    val_run = new nRunnable() { public void run() { up_text(); } };
	    cval.addEventChange(val_run);
	  } 
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}