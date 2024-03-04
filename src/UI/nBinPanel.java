package UI;

import sData.nRunnable;
import sData.sBoo;
import sData.sRun;

public class nBinPanel extends nWindowPanel {
	  nWidget widget;
	  sBoo bval;
	  sRun rval;
	  public nBinPanel(nGUI _g, nTaskPanel _task, sBoo _cv) { 
		    super(_g, _task, "bool "+_cv.bloc.ref + " " + _cv.ref); 
		    bval = _cv;
		    
		    if (bval == null) clear();
		    
		    getShelf()
		      .addDrawer(7.25F, 1).getShelf()
		      .addDrawer(7.25F, 1)
		        .addCtrlModel("Button-S2", "OK")
		          .setRunnable(new nRunnable() { public void run() { clear(); } })
		          .setPX(2.375F*ref_size).getDrawer()
		          ;
		    
		    widget = getDrawer(0,0).addLinkedModel("Button-S3-P1")
		      .setLinkedValue(bval)
		      ;
		  } 

	  public nBinPanel(nGUI _g, nTaskPanel _task, sRun _cv) { 
	    super(_g, _task, "bool "+_cv.bloc.ref + " " + _cv.ref); 
	    rval = _cv;
	    
	    if (rval == null) clear();
	    
	    getShelf()
	      .addDrawer(7.25F, 1).getShelf()
	      .addDrawer(7.25F, 1)
	        .addCtrlModel("Button-S2", "OK")
	          .setRunnable(new nRunnable() { public void run() { clear(); } })
	          .setPX(2.375F*ref_size).getDrawer()
	          ;
	    
	    widget = getDrawer(0,0).addLinkedModel("Button-S3", rval.ref)
	      .setLinkedValue(rval).setPX(1.625F*ref_size)
	      ;
	  } 
	  public nBinPanel clear() { 
	    super.clear(); return this; }
	  public nBinPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nBinPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
