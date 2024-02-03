package UI;

import java.util.ArrayList;

import processing.core.PApplet;
import sData.nRunnable;
import sData.sStr;

public class nTextPicker extends nWindowPanel {
	  
	  nWidget info;
	  String suff = "";
	  
	  sStr val_cible;
	  ArrayList<nRunnable> eventsChoose = new ArrayList<nRunnable>();
	  public nTextPicker addEventChoose(nRunnable r) { eventsChoose.add(r);  return this; } 
	  public nTextPicker addSuffix(String s) { suff = s;  return this; } 
	  
	  public nTextPicker(nGUI _g, nTaskPanel _task, sStr _sv, String t) { 
	    super(_g, _task, t); 
	    val_cible = _sv; 
	    info = getShelf().addSeparator(0.25F)
	      .addDrawer(10.25F, 1)
	        .addModel("Field-S4").setTextAlignment(PApplet.LEFT, PApplet.CENTER);
	        
	    info.setText(val_cible.get());
	    val_cible.addEventChange(new nRunnable() { public void run() { 
	      info.changeText(val_cible.get()); } } );
	    info.setField(true);
	    info.addEventFieldChange(new nRunnable() { public void run() { 
	      String s = info.getText();
	      
	      int i = s.length() - 1;
	      while (i > 0 && s.charAt(i) != '.') { i--; }
	      s = s.substring(0, i);
	      val_cible.set(s + "." + suff); 
	      //logln(s + "." + suff);
	    } } );
	  
	    getShelf()
	      .addSeparator(0.25F)
	      .addDrawer(10.25F, 1)
	        .addCtrlModel("Button-S2-P2", "OK")
	        .setRunnable(new nRunnable() { public void run() { clear(); nRunnable.runEvents(eventsChoose); } }).getShelf();
	  }
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
