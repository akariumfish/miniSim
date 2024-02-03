package UI;

public class Hoverable {
	  Hoverable_pile pile = null;
	  int layer;
	  Rect rect = null;
	  boolean mouseOver = false;
	  boolean active = true;
	  float phantom_space = 0;
	  Hoverable(Hoverable_pile p, Rect r) {
	    layer = 0;
	    pile = p;
	    if (pile != null) pile.hoverables.add(this);
	    rect = r;
	  }
	  Hoverable(Hoverable_pile p, Rect r, int l) {
	    layer = l;
	    pile = p;
	    if (pile != null) pile.hoverables.add(this);
	    rect = r;
	  }
	  void clear() { if (pile != null) pile.hoverables.remove(this); }
	  void toLayerTop() { if (pile != null) { pile.hoverables.remove(this); pile.hoverables.add(0, this); } }
	  void toLayerBottom() { if (pile != null) {pile.hoverables.remove(this); pile.hoverables.add(this); } }
	  Hoverable setLayer(int l) {
	    layer = l;
	    return this;
	  }
	}
