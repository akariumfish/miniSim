package UI;

import java.util.ArrayList;

public class nTaskPanel extends nToolPanel {
	  ArrayList<nWindowPanel> windowPanels = new ArrayList<nWindowPanel>();
	  ArrayList<nWidget> window_buttons = new ArrayList<nWidget>();
	  int used_spot = 0, max_spot = 8;
	  int row = 2, col = 4;
	  float adding_pos;
	  public nWidget getWindowPanelButton(nWindowPanel w) {
	    if (used_spot < max_spot) {
	      int i = 0;
	      while(!window_buttons.get(i).getText().equals("")) i++;
	      w.taskpanel_button = window_buttons.get(i);
	      w.taskpanel_button.setTrigger().setText(w.grabber.getText())
//	      	.setStandbyColor(color(70))
	        //.addEventTrigger(new Runnable() { public void run() {} } )
	        ;
	      windowPanels.add(w);
	      used_spot++;
	      if (hide) reduc();
	      return w.taskpanel_button;
	    }
	    return null;
	  }
	  
	  public nTaskPanel(nGUI _g, float ref_size, float space_factor) { 
	    super(_g, ref_size, space_factor, true, false); 
	    
	    addGrid(col, row, 4, 0.75F);
	    for (int i = 0 ; i < col ; i++) for (int j = 0 ; j < row ; j++) {
	      nWidget nw = getDrawer(i, j).addModel("Button-S4/0.75").setStandbyColor(gui.app.color(60));
	      window_buttons.add(nw);
	    }
	    //gui.addEventSetup(new Runnable() { public void run() { reduc(); } } );
	  } 
	  public nTaskPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nTaskPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
