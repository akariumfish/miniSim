package UI;

import java.util.ArrayList;

public class nBuilder { // base pour les class constructrice de nwidget basic

	  public nWidget addWidget(nWidget w) { 
	    customBuild(w); widgets.add(w); w.toLayerTop(); return w; }
	  
	  nWidget addRef(float x, float y) { 
	    nWidget w = gui.theme.newWidget(gui, "ref").setPosition(x*ref_size, y*ref_size); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	    
	  public nWidget addModel(String r) { 
	    nWidget w = gui.theme.newWidget(gui, r); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  public nWidget addModel(String r, String t) { 
	    nWidget w = gui.theme.newWidget(gui, r).setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nWidget addModel(String r, float x, float y) { 
	    nWidget w = gui.theme.newWidget(gui, r).setPosition(x*ref_size, y*ref_size); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nWidget addModel(String r, String t, float x, float y) { 
	    nWidget w = gui.theme.newWidget(gui, r).setPosition(x*ref_size, y*ref_size).setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nWidget addModel(String r, float x, float y, float w, float h) { 
	    nWidget nw = gui.theme.newWidget(gui, r).setPosition(x*ref_size, y*ref_size).setSize(w, h); customBuild(nw);
	    widgets.add(nw); nw.toLayerTop(); return nw; }
	  nWidget addModel(String r, String t, float x, float y, float w, float h) { 
	    nWidget nw = gui.theme.newWidget(gui, r).setPosition(x*ref_size, y*ref_size).setSize(w, h).setText(t); customBuild(nw);
	    widgets.add(nw); nw.toLayerTop(); return nw; }
	      
	  public nLinkedWidget addLinkedModel(String r) { 
	    nLinkedWidget w = gui.theme.newLinkedWidget(gui, r); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  public nLinkedWidget addLinkedModel(String r, String t) { 
	    nLinkedWidget w = gui.theme.newLinkedWidget(gui, r); w.setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nLinkedWidget addLinkedModel(String r, float x, float y) { 
	    nLinkedWidget w = gui.theme.newLinkedWidget(gui, r); w.setPosition(x*ref_size, y*ref_size); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }  
	  nLinkedWidget addLinkedModel(String r, String t, float x, float y) { 
	    nLinkedWidget w = gui.theme.newLinkedWidget(gui, r); w.setPosition(x*ref_size, y*ref_size).setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }    
	    
	  public nWatcherWidget addWatcherModel(String r) { 
	    nWatcherWidget w = gui.theme.newWatcherWidget(gui, r); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  public nWatcherWidget addWatcherModel(String r, String t) { 
	    nWatcherWidget w = gui.theme.newWatcherWidget(gui, r); w.setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nWatcherWidget addWatcherModel(String r, float x, float y) { 
	    nWatcherWidget w = gui.theme.newWatcherWidget(gui, r); w.setPosition(x*ref_size, y*ref_size); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nWatcherWidget addWatcherModel(String r, String t, float x, float y) { 
	    nWatcherWidget w = gui.theme.newWatcherWidget(gui, r); w.setPosition(x*ref_size, y*ref_size).setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	      
	  public nCtrlWidget addCtrlModel(String r) { 
	    nCtrlWidget w = gui.theme.newCtrlWidget(gui, r); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  public nCtrlWidget addCtrlModel(String r, String t) { 
	    nCtrlWidget w = gui.theme.newCtrlWidget(gui, r); w.setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nCtrlWidget addCtrlModel(String r, float x, float y) { 
	    nCtrlWidget w = gui.theme.newCtrlWidget(gui, r); w.setPosition(x*ref_size, y*ref_size); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  nCtrlWidget addCtrlModel(String r, String t, float x, float y) { 
	    nCtrlWidget w = gui.theme.newCtrlWidget(gui, r); w.setPosition(x*ref_size, y*ref_size).setText(t); customBuild(w);
	    widgets.add(w); w.toLayerTop(); return w; }
	  
	  public nGUI gui; 
	  ArrayList<nWidget> widgets = new ArrayList<nWidget>();
	  public float ref_size = 30;
	  
	  nBuilder setLayer(int l) { for (nWidget w : widgets) w.setLayer(l); return this; }
	  public nBuilder toLayerTop() { for (nWidget w : widgets) w.toLayerTop(); return this; }
	  public nBuilder clear() { 
	    for (int i = widgets.size() - 1 ; i >= 0 ; i--) widgets.get(i).clear(); 
	    widgets.clear(); 
	    return this; 
	  }
	  protected nWidget customBuild(nWidget w) { return w; }
	  
	  public nBuilder(nGUI _g, float s) { gui = _g; ref_size = s; }
	}
