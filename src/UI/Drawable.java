package UI;

import java.util.ArrayList;

public class Drawable {
	  Drawing_pile pile = null;
	  int layer = 0;
	  boolean active = true;
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
	  public void drawing() {}
	}

	
