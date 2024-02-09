package UI;

import java.util.ArrayList;

import RApplet.Rapp;
import processing.core.PVector;
import sData.nRunnable;

public class Hoverable_pile {
	  ArrayList<Hoverable> hoverables = new ArrayList<Hoverable>();
	  ArrayList<nRunnable> eventsFound = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsNotFound = new ArrayList<nRunnable>();
	  public boolean found = false;
	  Rapp app;
	  Hoverable_pile(Rapp a) { app = a; }
	  void addEventNotFound(nRunnable r) { eventsNotFound.add(r); }
	  void removeEventNotFound(nRunnable r) { eventsNotFound.remove(r); }
	  void addEventFound(nRunnable r) { eventsFound.add(r); }
	  void removeEventFound(nRunnable r) { eventsFound.remove(r); }
	  void search(PVector pointer, boolean passif) {
	    int layer = 0;
	    for (Hoverable h : hoverables) { 
	      if (layer < h.layer) layer = h.layer;
	      h.mouseOver = false;
	    }
	    
	    found = false; int count = 0;
	    if (!passif) {
	      if (hoverables.size() > 0) while (count < hoverables.size() && !found) {
	        for (int i = 0; i < hoverables.size() ; i++) {
	          Hoverable h = hoverables.get(i);
	          if (h.layer == layer) {
	            count++;
	            if (!found && h.active && h.rect != null && Rect.rectCollide(pointer, h.rect, h.phantom_space)) {
	              h.mouseOver = true;
	              if (app.DEBUG_HOVERPILE) {
	            	  app.fill(255, 0);
	            	  app.strokeWeight(5);
	            	  app.stroke(0, 0, 255);
	            	  app.rect(h.rect.pos.x, h.rect.pos.y, h.rect.size.x, h.rect.size.y);
	              }
	              found = true;
	            }
	          }
	        }
	        layer--;
	      }
	      if (found) nRunnable.runEvents(eventsFound); else nRunnable.runEvents(eventsNotFound);
	    } 
	    //else runEvents(eventsNotFound);
	  }
	}
