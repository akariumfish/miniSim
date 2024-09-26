package UI;

import java.util.ArrayList;

import sData.nRunnable;

public class nToolPanel extends nShelfPanel {
	  
	  ArrayList<nRunnable> eventReducRun = new ArrayList<nRunnable>();
	  public nToolPanel addEventReduc(nRunnable r)       { eventReducRun.add(r); return this; }
	  nToolPanel removeEventReduc(nRunnable r)       { eventReducRun.remove(r); return this; }
	  public void openit() { if (hide) reduc(); }
	  public void closeit() { if (!hide) reduc(); }
	  public void reduc() {
	    if      (hide && !right)  { panel.show(); reduc.setText("<"); } 
	    else if (hide && right)   { panel.show(); reduc.setText(">"); } 
	    else if (!hide && !right) { panel.hide(); reduc.show().setText(">"); }
	    else                      { panel.hide(); reduc.show().setText("<"); }
	    hide = !hide; 
	    nRunnable.runEvents(eventReducRun); }
	  nCtrlWidget reduc;
	  public boolean hide = false;
	boolean right = true;
	boolean top = true;
	  public nToolPanel(nGUI _g, float ref_size, float space_factor, boolean rgh, boolean tp) { 
	    super(_g, ref_size, space_factor); 
	    top = tp; right = rgh;
	    reduc = addCtrlModel("Menu_Button_Small_Outline", "<")
	      .setRunnable(new nRunnable(this) { public void run() { reduc(); } } );
	    reduc.setSize(ref_size/2.3F, panel.getSY()).stackRight().show().setLabelColor(gui.app.color(180));
	    up_pos();
	    gui.addEventsFullScreen(new nRunnable(this) { public void run() { 
	      up_pos();
	    } } );
	  } 
	  float py = 0;
	  public nToolPanel setPos(float d) { py = d; up_pos(); return this; }
	  public nToolPanel setPos(double d) { return setPos((float)d); }
	  void up_pos() {
	    if (top)    { panel.setPY(py + gui.view.pos.y).stackDown(); reduc.alignUp(); }
	    else        { panel.setPY(py + gui.view.pos.y + gui.view.size.y).stackUp(); reduc.alignDown(); }
	    if (!right) { panel.setPX(gui.view.pos.x).stackRight(); reduc.setText("<").stackRight(); }
	    else        { panel.setPX(gui.view.pos.x + gui.view.size.x).stackLeft(); reduc.setText(">").stackLeft(); }
	  }
	  public nToolPanel updateHeight() { 
	    super.updateHeight(); if (reduc != null) reduc.setSY(panel.getLocalSY()); return this; }
	}
