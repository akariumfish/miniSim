package UI;

import java.util.ArrayList;

public class Drawable {
	  Drawing_pile pile = null;
	  int layer = 0;
	  private boolean active = true;
	  Drawable set_view(boolean b) { if (b) show(); else hide(); return this; }
	  Drawable show() { 
		  //removing from array break drawing order...
//		  if (!active && pile != null && !pile.drawables.contains(this)) 
//			  pile.drawables.add(this); 
		  active = true; return this; }
	  Drawable hide() { 
//		  if (pile != null) while (pile.drawables.contains(this)) 
//			  pile.drawables.remove(this); 
		  active = false; return this; }
	  boolean get_view() { return active; }
	  public Drawable setPile(Drawing_pile p) {
	    pile = p; pile.drawables.add(this);
	    return this; }
	  public Drawable() {}
	  public Drawable(Drawing_pile p) {
	    pile = p; pile.drawables.add(this); }
	  public Drawable(Drawing_pile p, int l) {
	    layer = l;
	    pile = p; pile.drawables.add(this); }
	  public void clear() { if (pile != null) pile.drawables.remove(this); }
	  public void toLayerTop() { pile.drawables.remove(this); pile.drawables.add(0, this); }
	  public void toLayerBottom() { pile.drawables.remove(this); pile.drawables.add(this); }
	  public Drawable setLayer(int l) {
	    layer = l;
	    return this;
	  }
	  public void do_drawing() {
		  drawing();
		  if (co_orderered_drawable == null)
			  co_orderered_drawable = new ArrayList<Drawable>();
		  for (Drawable w : co_orderered_drawable) 
			  if (w != null) w.do_drawing();

	  }
	  ArrayList<Drawable> co_orderered_drawable;
	  public Drawable add_coDrawer(Drawable w) {
	    if (w != null) {
	        if (co_orderered_drawable == null)
	    			co_orderered_drawable = new ArrayList<Drawable>();
	    		if (co_orderered_drawable.contains(w))
	    			co_orderered_drawable.remove(w);
	    		co_orderered_drawable.add(w);
	    }
	    return this;
	  }
	  public Drawable clear_coDrawer() {
	    if (co_orderered_drawable != null) 
	        	co_orderered_drawable.clear();
	    return this;
	  }
	  public void drawing() {}
	}

	
