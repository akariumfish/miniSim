package UI;

import java.util.ArrayList;

import processing.core.PApplet;
import sData.nRunnable;

public class nTextPop extends nWindowPanel {
	  
	  nWidget info;
	  ArrayList<nRunnable> eventsChoose = new ArrayList<nRunnable>();
	  nTextPop addEventChoose(nRunnable r) { eventsChoose.add(r);  return this; } 
	  
	  public nTextPop(nGUI _g, nTaskPanel _task, String t) { 
	    super(_g, _task, t); 
	    info = getShelf().addSeparator(0.25F)
	      .addDrawer(10.25F, 1)
	        .addModel("Label-S4").setTextAlignment(PApplet.CENTER, PApplet.CENTER);
	        
	    info.setText(t);
	  
	    getShelf()
	      .addSeparator(0.25F)
	      .addDrawer(10.25F, 1)
	        .addCtrlModel("Button-S2-P2", "OK")
	        .setRunnable(new nRunnable() { public void run() { clear(); runEvents(eventsChoose); } }).getShelf();
	  }
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
