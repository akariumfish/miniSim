package UI;

import java.util.ArrayList;

import sData.nRunnable;

public class nSelectZone {
	  Hoverable_pile pile;
	  Drawable drawer;
	  Rect select_zone = new Rect();
	  boolean emptyClick = false;
	  int clickDelay = 0;
	  boolean ON = true;
	  
	  public nSelectZone addEventEndSelect(nRunnable r)  { eventEndSelect.add(r); return this; }
	  public nSelectZone removeEventEndSelect(nRunnable r)       { eventEndSelect.remove(r); return this; }
	  ArrayList<nRunnable> eventEndSelect = new ArrayList<nRunnable>();
	  public nSelectZone addEventStartSelect(nRunnable r)  { eventStartSelect.add(r); return this; }
	  public nSelectZone removeEventStartSelect(nRunnable r)       { eventStartSelect.remove(r); return this; }
	  ArrayList<nRunnable> eventStartSelect = new ArrayList<nRunnable>();
	  public nSelectZone addEventSelecting(nRunnable r)  { eventStartSelect.add(r); return this; }
	  public nSelectZone removeEventSelecting(nRunnable r)       { eventStartSelect.remove(r); return this; }
	  ArrayList<nRunnable> eventSelecting = new ArrayList<nRunnable>();
	  
	  public boolean isSelecting() { return emptyClick; }
	  
	  nGUI gui;
	  public nSelectZone(nGUI _g) {
	    gui = _g;
	    gui.addEventFrame(new nRunnable() { public void run() { frame(); } } );
	    pile = _g.hoverable_pile;
	    pile.addEventNotFound(new nRunnable() { public void run() { 
	      if (ON && gui.in.getClick("MouseLeft")) clickDelay = 1; 
	    } } );
	    drawer = new Drawable(_g.drawing_pile, 25) { public void drawing() {
	      gui.app.noFill();
	      gui.app.stroke(255);
	      gui.app.strokeWeight(2.0F/gui.scale);
	      Rect z = new Rect(select_zone);
	      if (z.size.x < 0) { z.pos.x += z.size.x; z.size.x *= -1; }
	      if (z.size.y < 0) { z.pos.y += z.size.y; z.size.y *= -1; }
	      if (ON && emptyClick) z.draw(gui.app);
	    } };
	  }
	  public boolean isUnder(nWidget w) {
	    Rect z = new Rect(select_zone);
	    if (z.size.x < 0) { z.pos.x += z.size.x; z.size.x *= -1; }
	    if (z.size.y < 0) { z.pos.y += z.size.y; z.size.y *= -1; }
	    if (emptyClick && !w.isHided() && Rect.rectCollide(w.getRect(), z)) return true;
	    return false;
	  }
	  void frame() {
	    if (ON) {
	      if (clickDelay > 0) {
	        clickDelay--;
	        if (clickDelay == 0) { 
	          emptyClick = true;
	          select_zone.pos.x = gui.mouseVector.x;
	          select_zone.pos.y = gui.mouseVector.y;
	          select_zone.size.x = 1;
	          select_zone.size.y = 1;
	          nRunnable.runEvents(eventStartSelect);
	        }
	      }
	      if (emptyClick) {
	    	  nRunnable.runEvents(eventSelecting);
	        select_zone.size.x = gui.mouseVector.x - select_zone.pos.x;
	        select_zone.size.y = gui.mouseVector.y - select_zone.pos.y;
	        if (gui.in.getUnClick("MouseLeft")) { 
	        	nRunnable.runEvents(eventEndSelect);
	          emptyClick = false; 
	        }
	      }
	    }
	    if (!gui.in.getState("MouseLeft")) emptyClick = false;
	  }
	}
