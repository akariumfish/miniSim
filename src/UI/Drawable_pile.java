package UI;

import java.util.ArrayList;

class Drawing_pile {
	  ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	  //ArrayList<Drawer> top_drawables = new ArrayList<Drawer>();
	  Drawing_pile() { }
	  void drawing() {
	    int layer = 0;
	    int run_count = 0;
	    for (Drawable d : drawables) d.drawn_this_frame = false;
	    while (run_count < drawables.size()) {
	      for (int i = drawables.size() - 1; i >= 0 ; i--) {
	        Drawable r = drawables.get(i);
	        if (r.layer == layer) {
	          if (r.get_view()) r.do_drawing();
	          run_count++;
	        }
	      }
	      layer++;
	    }
	  }
	  int getHighestLayer() {
	    if (drawables.size() > 0) {
	      int l = drawables.get(0).layer;
	      for (Drawable r : drawables) if (r.layer > l) l = r.layer;
	      return l;
	    } else return 0; }
	  int getLowestLayer() {
	    if (drawables.size() > 0) {
	      int l = drawables.get(0).layer;
	      for (Drawable r : drawables) if (r.layer < l) l = r.layer;
	      return l;
	    } else return 0; }
	}
