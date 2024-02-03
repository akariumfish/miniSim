package UI;

import java.util.ArrayList;

import sData.nRunnable;

public class nWindowPanel extends nShelfPanel {
	  nWindowPanel setPosition(float x, float y) {
	    grabber.setPosition(x-task.panel.getX(), y-task.panel.getY()); return this;}
	  //void reduc() { panel.hide(); }
	  //void enlarg() { panel.show(); }
	  public void collapse() { 
	    collapsed = true;
	    grabber.hide(); 
//	    if (taskpanel_button != null) taskpanel_button.setStandbyColor(color(90));
	    nRunnable.runEvents(eventCollapseRun);
	  }
	  public void popUp() { 
	    boolean p = collapsed;
	    collapsed = false;
	    if (task.hide) task.reduc();
	    grabber.show(); 
	    if (taskpanel_button != null) taskpanel_button.setStandbyColor(gui.app.color(70)); 
	    if (p) toLayerTop();
	    if (p) nRunnable.runEvents(eventCollapseRun);
	  }
	  ArrayList<nRunnable> eventCloseRun = new ArrayList<nRunnable>();
	  public nWindowPanel addEventClose(nRunnable r)       { eventCloseRun.add(r); return this; }
	  public nWindowPanel removeEventClose(nRunnable r)       { eventCloseRun.remove(r); return this; }
	  
	  ArrayList<nRunnable> eventCollapseRun = new ArrayList<nRunnable>();
	  public nWindowPanel addEventCollapse(nRunnable r)       { eventCollapseRun.add(r); return this; }
	  public nWindowPanel removeEventCollapse(nRunnable r)       { eventCollapseRun.remove(r); return this; }
	  
	  nTaskPanel task;
	  public nWidget grabber;
	nWidget closer;
	nWidget reduc;
	nWidget collapse;
	nWidget taskpanel_button;
	  nRunnable run_show;
	  public boolean collapsed = false;
	  public nWindowPanel(nGUI _g, nTaskPanel _task, String ti) { 
	    super(_g, _task.ref_size, _task.space_factor); 
	    task = _task;
	    
	    grabber = addModel("Head_Button_Small_Outline-SS4").setParent(task.panel).setText(ti)
	      .setGrabbable()
	      .setSX(ref_size*10.25F)
	      .show()
	      .addEventGrab(new nRunnable() { public void run() { toLayerTop(); } } )
	      ;
	    if (task.hide) grabber.setPosition(3*ref_size - task.panel.getX() + task.adding_pos*ref_size*1.5F + task.panel.getLocalSX(), 
	                                       1*ref_size - task.panel.getY() + task.adding_pos*ref_size*1.5F + task.panel.getLocalSY());
	    else grabber.setPosition(3*ref_size - task.panel.getX() + task.adding_pos*ref_size*1.5F, 
	                             1*ref_size - task.panel.getY() + task.adding_pos*ref_size*1.5F);
	    task.adding_pos++;
	    if (task.adding_pos > 5) task.adding_pos -= 5.25F;
	    
	    closer = addModel("Head_Button_Small_Outline-SS1").setText("X")
	      .setTrigger()
	      .addEventTrigger(new nRunnable() { public void run() { 
	        clear(); } } )
	      .setParent(grabber)
	      .alignRight()
	      ;
	    collapse = addModel("Head_Button_Small_Outline-SS1").setText("v")
	      .setTrigger()
	      .addEventTrigger(new nRunnable() { public void run() { collapse(); } } )
	      .setParent(closer)
	      .stackLeft()
	      ;
	    panel.setParent(grabber).stackDown();
	    addShelf()
	      //.addDrawer(10, 0)
	      ;
	    taskpanel_button = task.getWindowPanelButton(this);
	    run_show = new nRunnable() { public void run() { 
	      if (collapsed) popUp(); else collapse(); } };
	    if (taskpanel_button != null) taskpanel_button.addEventTrigger(run_show);
	  } 
	  public nWindowPanel clear() { 
		  nRunnable.runEvents(eventCloseRun); 
	    task.used_spot--;
	    if (taskpanel_button != null) 
	      taskpanel_button.removeEventTrigger(run_show).setText("").setPassif()
	      .setStandbyColor(gui.app.color(60));
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); grabber.setSX(Math.max(ref_size * 1.5F, panel.getLocalSX())); 
	    //log("wind grab "+grabber.getLocalSX()); 
	    return this; }
	}
