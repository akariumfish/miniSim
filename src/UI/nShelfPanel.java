package UI;

import java.util.ArrayList;

import sData.nRunnable;

public class nShelfPanel extends nBuilder {
	public nFrontPanel getFront() { if (fronttab != null) return fronttab.front; else return null; }
	  public nFrontTab fronttab; // set by superclass fronttab with himself
	  
	  public nDrawer getDrawer(int c, int r) { return shelfs.get(c).drawers.get(r); }
	  public nShelf getShelf(int s) { return shelfs.get(s); }
	  public nShelf getShelf() { return shelfs.get(0); }
	  
	  public nShelf addShelf() {
	    nShelf s = new nShelf(this, space_factor);
	    s.setPosition(panel, ref_size*space_factor, ref_size*space_factor); 
	    shelfs.add(s);
	    updateWidth();
	    s.addEventHeight(new nRunnable(s) { public void run() { updateHeight(); } } );
	    s.addEventWidth(new nRunnable() { public void run() { updateWidth(); } } );
	    return s;
	  }
	  
	  public nDrawer addShelfaddDrawer(float x, float y) {
	    return addShelf().addDrawer(x, y);
	  }
	  
	  public nShelfPanel addGrid(int c, int r, float width_factor, float height_factor) {
	    for (int i = 0 ; i < c ; i++) {
	      nShelf s = addShelf();
	      for (int j = 0 ; j < r ; j++) s.addDrawer(width_factor, height_factor);
	    }
	    return this;
	  }
	  public nShelfPanel updateHeight() {  
	    float h = ref_size * 2 * space_factor;
	    for(nShelf s : shelfs) { s.ref.setPX(ref_size * space_factor);
	      if (h < s.total_height + ref_size * 2 * space_factor) 
	        h = s.total_height + ref_size * 2 * space_factor; }
	    panel.setSY(h); 
	    max_height = h - ref_size * 2 * space_factor;
	    return this; }
	  public nShelfPanel updateWidth() { 
	    float w = ref_size * space_factor;
	    for (nShelf s : shelfs) { s.ref.setPX(w); w += s.max_width + ref_size * space_factor; }
	    if (shelfs.size() == 0) w += ref_size * space_factor;
	    panel.setSX(w); 
	    max_width = w - ref_size * space_factor * 2;
	    return this; }
	  public nShelfPanel setSpace(float _space_factor) { 
	    space_factor = _space_factor;
	    return this; }
	  public nShelfPanel(nGUI _g, float _ref_size, float _space_factor) {
	    super(_g, _ref_size);
	    panel = addModel("Hard_Back");
	    panel.setSize(ref_size*_space_factor*2, ref_size*_space_factor*2);
	    space_factor = _space_factor;
	  }
	  float space_factor, max_width = 0, max_height = 0;
	  public nWidget panel;
	  public ArrayList<nShelf> shelfs = new ArrayList<nShelf>();
	  
	  nShelfPanel setLayer(int l) { super.setLayer(l); 
	    panel.setLayer(l); for (nShelf d : shelfs) d.setLayer(l); return this; }
	  public nShelfPanel toLayerTop() { super.toLayerTop(); 
	    panel.toLayerTop(); for (nShelf d : shelfs) d.toLayerTop(); return this; }
	  protected nWidget customBuild(nWidget w) { return w.setParent(panel); }
	  public nShelfPanel clear() { super.clear(); for (nShelf s : shelfs) s.clear(); return this; }
	}
