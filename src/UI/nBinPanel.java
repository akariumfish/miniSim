package UI;

import sData.*;

public class nBinPanel extends nWindowPanel {
	  nWidget widget, shortcutwidg;
	  sBoo bval;
	  sRun rval;
	  sStr shortcut;
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
		    
		    widget = getDrawer(0,0).addLinkedModel("Button-S3", "STATE")
		      .setLinkedValue(bval)
	    	  	  .setSX(3*ref_size).setPX(1.0*ref_size)
		      ;
		    shortcutwidg = widget.getDrawer().addLinkedModel("Field-S3")
  	  	  	  .setSX(2*ref_size).setPX(4.5*ref_size)
  	  	  	  .setField(true)
  	  	  	  .addEventFieldChange(new nRunnable() { public void run() { 
  	  	  		  char c = 0; String s = shortcutwidg.getText();
  	  	  		  if (s.length() > 0) c = s.charAt(0);
  	  	  		  if (c != 0) bval.set_directshortcut(c);
  	  	  		  if (s.length() == 0) bval.set_directshortcut((char)0);
  	  	  	  } })
  	  	  	  ;
		    if (bval.direct_shortcut != 0) shortcutwidg.setText(""+bval.direct_shortcut);
		      
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
