package UI;

import Macro.Macro_Interf;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sStr;

public class nTextPanel extends nWindowPanel implements Macro_Interf  { //
	  nWidget text_widget;
	  sStr cval;
	  String txt; boolean auto_return = true;
	  nRunnable val_run;
	  int larg = 20;
	  public nTextPanel(nGUI _g, nTaskPanel _task, sStr _cv) { 
	    super(_g, _task, "string "+_cv.bloc.ref + " " + _cv.ref); 
	    cval = _cv;
	    
	    if (cval == null) clear();
	    if (cval.get().length() < 50) larg = 10;
	    float font = (int)(ref_size/2.1F);
	    int max_l = (int)(larg * ref_size / (font / 1.7F));
	    
	    txt = cval.get();
	    
	    float line_cnt = (float)(txt.length()) / (float)(max_l);
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
	    
	    float h_fact = (font * line_cnt) / ref_size + font / (ref_size * 2);
	    
	    //logln("m " + max_l + " lc " + line_cnt + " hf " + h_fact);
	    
	    getShelf()
	      .addDrawer(larg + 0.25F, h_fact)
	        .getShelf()
	      .addDrawer(larg, 1)
	        .addCtrlModel("Button-S2-P3", "OK")
	          .setRunnable(new nRunnable() { public void run() { 
	            for (int i = txt.length() - 1 ; i >= 0  ; i--) 
	              if (txt.charAt(i) == '\n' || txt.charAt(i) == '\r') {
	                txt = txt.substring(0, i);
	                if (i+1 < txt.length()) txt += txt.substring(i + 1, txt.length());
	            }
	            if (cval != null) cval.set(txt);
	            clear(); 
	          } }).getDrawer()
	          ;
	    
	    text_widget = getDrawer(0,0).addModel("Field")
	      .setText(txt)
	      .setField(true)
	      .setTextAlignment(PApplet.CENTER, PApplet.TOP)
	      .setTextAutoReturn(auto_return)
	      .setFont((int)(font))
	      .setSX(ref_size * larg)
	      .setSY(ref_size * h_fact)
	      .setPX(ref_size * 0.125F)
	      ;
	    val_run = new nRunnable() { public void run() { 
	      float font = (int)(ref_size/2.1);
	      int max_l = (int)(larg * ref_size / (font / 1.7));
	      
	      txt = cval.get();
	      
	      float line_cnt = (float)(txt.length()) / (float)(max_l);
	      if (line_cnt % 1 > 0) line_cnt += 1 - (line_cnt % 1);
	      
//	      if (cval.ref.equals("links") || cval.ref.equals("spots")) {
//	        auto_return = false;
//	        line_cnt = 1;
//	        int char_counter = 0;
//	        for (int i = 0 ; i < txt.length() ; i++) {
//	          char_counter++;
//	          if (char_counter >= max_l) { line_cnt++; char_counter = 0; }
//	          if (txt.charAt(i) == OBJ_TOKEN.charAt(0) || txt.charAt(i) == GROUP_TOKEN.charAt(0)) {
//	            txt = txt.substring(0, i+1) + '\n' + txt.substring(i+1, txt.length());
//	            line_cnt++;
//	            char_counter = 0;
//	          }
//	        }
//	      }
	      text_widget.setText(txt);
	    } };
	    cval.addEventChange(val_run);
	  } 
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
