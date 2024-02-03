package UI;

import sData.nRunnable;
import sData.sBoo;

public class nBoolPanel extends nWindowPanel {
	  nWidget widget;
	  sBoo val;
	  public nBoolPanel(nGUI _g, nTaskPanel _task, sBoo _cv) { 
	    super(_g, _task, "bool "+_cv.bloc.ref + " " + _cv.ref); 
	    val = _cv;
	    
	    if (val == null) clear();
	    
	    getShelf()
	      .addDrawer(7.25F, 1).getShelf()
	      .addDrawer(7.25F, 1)
	        .addCtrlModel("Button-S2", "OK")
	          .setRunnable(new nRunnable() { public void run() { clear(); } })
	          .setPX(2.375F*ref_size).getDrawer()
	          ;
	    
	    widget = getDrawer(0,0).addLinkedModel("Button-S3-P1")
	      .setLinkedValue(val)
	      ;
	  } 
	  public nBoolPanel clear() { 
	    super.clear(); return this; }
	  public nBoolPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nBoolPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
