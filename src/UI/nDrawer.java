package UI;

public class nDrawer extends nBuilder {
	  public nShelf getShelf() { return shelf; }
	  public nShelfPanel getShelfPanel() { return shelf.shelfPanel; }
	  nShelf shelf;
	  public nWidget ref;
	  float drawer_width = 0, drawer_height = 0;
	  public nDrawer(nShelf s, float w, float h) {
	    super(s.gui, s.ref_size);
	    ref = addModel("ref"); shelf = s;
	    drawer_width = w; drawer_height = h; }
	  public nDrawer clear() { super.clear(); return this; }
	  public nDrawer setLayer(int l) { super.setLayer(l); ref.setLayer(l); return this; }
	  public nDrawer toLayerTop() { super.toLayerTop(); ref.toLayerTop(); return this; }
	  protected nWidget customBuild(nWidget w) { return w.setParent(ref).setDrawer(this); }
	  
	}
