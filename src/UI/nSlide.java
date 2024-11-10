package UI;

import java.util.ArrayList;

import sData.nRunnable;
import sData.sValue;

public class nSlide extends nWidget {
	  //nSlide setLinkedValue(sValue v) {
    
	  //  return this;
	  //}
	  public nSlide setValue(float v) {
		  if (v < 0) v = 0; if (v > 1) v = 1;
		  if (v != cursor_value) {
		    curs.setPX(v * (bar.getLocalSX() - curs.getLocalSX()));
		    cursor_value = v;
		  }
	    return this;
	  }
	  public nWidget addEventSlide(nRunnable r)   { eventSlide.add(r); return this; }
	  public nWidget addEventLiberate(nRunnable r)   { eventLiberate.add(r); return this; }
	  nWidget addEventSlide_Builder(nRunnable r)   { r.builder = this; eventSlide.add(r); return this; }
	  
	  nWidget bar;
	public nWidget curs;
	  sValue val;
	  ArrayList<nWidget> widgets = new ArrayList<nWidget>();
	  float scale_height, scale_width;
	  float cursor_value = 0;
	  ArrayList<nRunnable> eventSlide = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventLiberate = new ArrayList<nRunnable>();
	  public nSlide(nGUI g, float _scale_width, float _scale_height) { super(g); 
	    scale_height = _scale_height; scale_width = _scale_width;
	    bar = new nWidget(gui, 0, scale_height * 3 / 8, _scale_width, scale_height * 1 / 4).setParent(this);
	    bar.setStandbyColor(gui.app.color(120));
	    curs = new nWidget(gui, 0, -scale_height * 3 / 8, scale_height * 1 / 2, scale_height)
	      .setParent(bar)
	      .setText("|")
	      .setStandbyColor(gui.app.color(200))
	      .setGrabbable().setConstrainY(true)
	      .addEventDrag(new nRunnable() { public void run() {
	        if (curs.getLocalX() < 0) curs.setPX(0);
	        if (curs.getLocalX() > bar.getLocalSX() - curs.getLocalSX()) 
	          curs.setPX(bar.getLocalSX() - curs.getLocalSX());
	        cursor_value = curs.getLocalX() / (bar.getLocalSX() - curs.getLocalSX());
	        nRunnable.runEvents(eventSlide, cursor_value);
	        nRunnable.runEvents(eventSlide);
	      }})
	      .addEventLiberate(new nRunnable() { public void run() {
	    	  nRunnable.runEvents(eventLiberate);
	      }});
	    widgets.add(bar);
	    widgets.add(curs);
	  }
	  public nSlide setLayer(int l) { super.setLayer(l); for (nWidget w : widgets) w.setLayer(l); return this; }
	  public nSlide toLayerTop() { super.toLayerTop(); for (nWidget w : widgets) w.toLayerTop(); return this; }
	  public void clear() { for (nWidget w : widgets) w.clear(); super.clear(); }
	}
