package UI;

import java.util.ArrayList;

import RBase.RConst;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sBoo;
import sData.sValue;

public class nShelf extends nBuilder {
	public nShelf addDrawerButton(sValue val1, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    if (val1 != null) {
	    d.addLinkedModel("Auto_Button-S3-P2")
	      .setLinkedValue(val1)
	      .setSY(h*ref_size*0.75F)
	      .setPY(h*ref_size*0.125F)
	      .setText(val1.shrt)
	      ;
	    d.addModel("Label_Small_Text-S1")
	      .setText(val1.ref)
	      .setPosition(ref_size*0, 0)
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    }
	    return this;
	  }
	  public nShelf addDrawerDoubleButton(sValue val1, sValue val2, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    if (val1 != null) {
	    d.addLinkedModel("Auto_Button-S3-P1")
	      .setLinkedValue(val1)
	      //.setSize(w*ref_size/3, h*ref_size)
	      //.setPosition(2*w*ref_size/3, 0)
	      .setText(val1.shrt)
	      ;
	    }
	    if (val2 != null) {
	    d.addLinkedModel("Auto_Button-S3-P2")
	      .setLinkedValue(val2)
	      //.setSize(w*ref_size/3, h*ref_size)
	      //.setPosition(2*w*ref_size/3, 0)
	      .setText(val2.shrt)
	      ;
	    }
	    return this;
	  }
	  public nShelf addDrawerTripleButton(sValue val1, sValue val2, sValue val3, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    if (val1 != null) {
	    d.addLinkedModel("Auto_Button-S2-P1")
	      .setLinkedValue(val1)
	      //.setSize(w*ref_size/3, h*ref_size)
	      //.setPosition(2*w*ref_size/3, 0)
	      .setText(val1.shrt)
	      ;
	    }
	    if (val2 != null) {
	    d.addLinkedModel("Auto_Button-S2-P2")
	      .setLinkedValue(val2)
	      //.setSize(w*ref_size/3, h*ref_size)
	      //.setPosition(2*w*ref_size/3, 0)
	      .setText(val2.shrt)
	      ;
	    }
	    if (val3 != null) {
	    d.addLinkedModel("Auto_Button-S2-P3")
	      .setLinkedValue(val3)
	      //.setSize(w*ref_size/3, h*ref_size)
	      //.setPosition(2*w*ref_size/3, 0)
	      .setText(val3.shrt)
	      ;
	    }
	    return this;
	  }
	  
	  public nShelf addDrawerIncrValue(sValue val2, float incr, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    d.addModel("Label_Small_Text-S1")
	      .setText(val2.ref)
	      .setPosition(ref_size*3.6F, 0)
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    d.addWatcherModel("Auto_Watch_Label-S2")
	      .setLinkedValue(val2)
	      .setSize(ref_size*1.625F, ref_size*0.8F)
	      .setPosition(ref_size*2.25F, ref_size*0.1F)
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P1")
	      .setLinkedValue(val2)
	      .setIncrement(incr)
	      .setText(RConst.trimStringFloat(incr))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P2")
	      .setLinkedValue(val2)
	      .setIncrement(incr/10)
	      .setText(RConst.trimStringFloat(incr/10))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P8")
	      .setLinkedValue(val2)
	      .setIncrement(-incr/10)
	      .setText(RConst.trimStringFloat(-incr/10))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P9")
	      .setLinkedValue(val2)
	      .setIncrement(-incr)
	      .setText(RConst.trimStringFloat(-incr))
	      ;
	    return this;
	  }
	  
	  public nShelf addDrawerActFactValue(String title, sBoo val1, sValue val2, float fact, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    d.addModel("Label_Small_Text-S1")
	      .setText(val2.ref)
	      .setPosition(ref_size*4.3F, 0)
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    d.addWatcherModel("Auto_Watch_Label")
	      .setLinkedValue(val2)
	      .setSize(ref_size*1.625F, ref_size*0.8F)
	      .setPosition(ref_size*3.125F, ref_size*0.1F)
	      ;
	    d.addLinkedModel("Button_Check-SS1-P3", "")
	      .setLinkedValue(val1)
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P1")
	      .setLinkedValue(val2)
	      .setFactor(fact)
	      .setText("x"+RConst.trimStringFloat(fact))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P2")
	      .setLinkedValue(val2)
	      .setFactor((float) Math.sqrt(fact))
	      .setText("x"+RConst.trimStringFloat((float) Math.sqrt(fact)))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P8")
	      .setLinkedValue(val2)
	      .setFactor(1/(float) Math.sqrt(fact))
	      .setText("/"+RConst.trimStringFloat(1/(float) Math.sqrt(fact)))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P9")
	      .setLinkedValue(val2)
	      .setFactor(1/fact)
	      .setText("/"+RConst.trimStringFloat(fact))
	      ;
	    return this;
	  }
	  public nShelf addDrawerFactValue(sValue val2, float fact, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    d.addModel("Label_Small_Text-S1")
	      .setText(val2.ref)
	      .setPosition(ref_size*3.6F, 0)
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    d.addWatcherModel("Auto_Watch_Label-S2")
	      .setLinkedValue(val2)
	      .setSize(ref_size*1.625F, ref_size*0.8F)
	      .setPosition(ref_size*2.25F, ref_size*0.1F)
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P1")
	      .setLinkedValue(val2)
	      .setFactor(fact)
	      .setText("x"+RConst.trimStringFloat(fact))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P2")
	      .setLinkedValue(val2)
	      .setFactor((float) Math.sqrt(fact))
	      .setText("x"+RConst.trimStringFloat((float) Math.sqrt(fact)))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P8")
	      .setLinkedValue(val2)
	      .setFactor(1/(float) Math.sqrt(fact))
	      .setText("/"+RConst.trimStringFloat(1/(float) Math.sqrt(fact)))
	      ;
	    d.addCtrlModel("Auto_Ctrl_Button-S1-P9")
	      .setLinkedValue(val2)
	      .setFactor(1/fact)
	      .setText("/"+RConst.trimStringFloat(fact))
	      ;
	    return this;
	  }
	  //nShelf addDrawerSlideCtrl(sValue val, float w, float h) {
	  //  nDrawer d = addDrawer(w, h);
	  //  d.addWidget(new nSlide(gui, w*ref_size, h*ref_size)
	  //    .setLinkedValue(val)
	  //    .setSize(w*ref_size/3, h*ref_size)
	  //    .setPosition(2*w*ref_size/3, 0)
	  //    )
	  //    ;
	  //  d.addModel("Label_Small_Text")
	  //    .setSize(w*ref_size/10, h*ref_size)
	  //    .setPosition(0, 0)
	  //    .setText(val.ref)
	  //    .setFont(int(ref_size/1.9))
	  //    .setTextAlignment(LEFT, CENTER)
	  //    ;
	  //  return this;
	  //}
	  public nShelf addDrawerFieldCtrl(sValue val, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    d.addLinkedModel("Field")
	      .setLinkedValue(val)
	      .setSize(w*ref_size/3, h*ref_size)
	      .setPosition(2*w*ref_size/3, 0)
	      ;
	    d.addModel("Label_Small_Text")
	      .setSize(w*ref_size/10, h*ref_size)
	      .setPosition(0, 0)
	      .setText(val.ref)
	      .setFont((int)(ref_size/1.9F))
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    return this;
	  }
	  public nShelf addDrawerLargeFieldCtrl(sValue val, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    d.addLinkedModel("Field")
	      .setLinkedValue(val)
	      .setSize(2*w*ref_size/3, h*ref_size)
	      .setPosition(w*ref_size/3, 0)
	      ;
	    d.addModel("Label_Small_Text")
	      .setSize(w*ref_size/10, h*ref_size)
	      .setPosition(0, 0)
	      .setText(val.ref)
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    return this;
	  }
	  public nShelf addDrawerWatch(sValue val, float w, float h) {
	    nDrawer d = addDrawer(w, h);
	    d.addWatcherModel("Label_Back")
	      .setLinkedValue(val)
	      .setSize(w*ref_size/3, h*ref_size)
	      .setPosition(2*w*ref_size/3, 0)
	      ;
	    d.addModel("Label_Small_Text")
	      .setSize(w*ref_size/10, h*ref_size)
	      .setPosition(0, 0)
	      .setText(val.ref)
	      .setFont((int)(ref_size/1.9F))
	      .setTextAlignment(PApplet.LEFT, PApplet.CENTER)
	      ;
	    return this;
	  }
	  
	  public nDrawer getDrawer(int s) { return drawers.get(s); }
	  public nDrawer getLastDrawer() { return drawers.get(drawers.size()-1); }
	  public nShelfPanel getShelfPanel() { return shelfPanel; }
	  public nFrontTab getTab() { return ((nFrontTab)shelfPanel); }
	  public nShelf setPosition(nWidget p, float x, float y) { ref.setParent(p).setPosition(x, y); return this; }
	  
	  nShelfPanel shelfPanel;
	  public nWidget ref;
	  public ArrayList<nDrawer> drawers = new ArrayList<nDrawer>();
	  int max_drawer = 0; // 0 = no limit
	  float space_factor, max_width = 0, total_height = 0;
	  nRunnable eventWidth = null, eventHeight = null;

	  public nShelf(nShelfPanel s, float _space_factor) {
	    super(s.gui, s.ref_size);
	    shelfPanel = s; space_factor = _space_factor;
	    ref = addModel("ref");
	  }
	  public nShelf addEventWidth(nRunnable r) { eventWidth = r; return this; }
	  public nShelf addEventHeight(nRunnable r) { eventHeight = r; return this; }
	  
	  public nShelf setLayer(int l) { super.setLayer(l); 
	    ref.setLayer(l); for (nDrawer d : drawers) d.setLayer(l); return this; }
	  public nShelf toLayerTop() { super.toLayerTop(); 
	    ref.toLayerTop(); for (nDrawer d : drawers) d.toLayerTop(); return this; }
	  public nWidget customBuild(nWidget w) { return w.setParent(ref); }
	  public nShelf clear() { super.clear(); for (nDrawer s : drawers) s.clear(); return this; }
	  public nDrawer addDrawer() { return addDrawer(0, 0); }
	  public nDrawer addDrawer(float h) { return addDrawer(0, h); }
	  public nDrawer addDrawer(double h) { return addDrawer(0, (float)h); }
	  public nShelf addSeparator() { addDrawer(0, 0); return this; }
	  public nShelf addSeparator(float h) { addDrawer(0, h-space_factor); return this; }
	  public nShelf addSeparator(double h) { addDrawer(0, (float)h-space_factor); return this; }
	  public nShelf setMax(int m) { max_drawer = m; return this; }
	  public nDrawer addDrawer(float w, float h) { 
		  return insertDrawer(new nDrawer(this, w*ref_size, h*ref_size)); }
	  public nDrawer addDrawer(double w, double h) { 
		  return insertDrawer(new nDrawer(this, (float)(w)*ref_size, (float)(h)*ref_size)); }
	  public nDrawer insertDrawer(nDrawer d) {
	    if (d != null && max_drawer == 0 || drawers.size() < max_drawer) {
	      if (drawers.size() == 0) { d.ref.setParent(ref).setPY(0); }
	      else {
	        nDrawer prev = drawers.get(drawers.size()-1);
	        prev.drawer_height += ref_size*space_factor/2;
	        d.ref.setParent(prev.ref)
	          .setPY(prev.drawer_height);  }
	      drawers.add(d); 
	      
	      total_height = 0;
	      for (nDrawer dr : drawers) total_height += dr.drawer_height;
	      if (eventHeight != null) eventHeight.run();
	      if (max_width <= d.drawer_width) { max_width = d.drawer_width; if (eventWidth != null) eventWidth.run(); }
	      return d;  }
	    return null;
	  }
	  public nShelf removeDrawer(nDrawer d) {
	    if (drawers.contains(d)) {
	      int d_i = 0;
	      for (nDrawer td : drawers) { if (td == d) break; else d_i++; }
	      if (drawers.size() == 1) { d.ref.setPY(0).clearParent(); drawers.remove(d); }
	      else if (d_i == 0) { 
	        drawers.get(1).ref.setPY(0).clearParent().setParent(ref); 
	        d.ref.clearParent(); drawers.remove(d); }
	      else if (d_i < drawers.size() - 1) { 
	        drawers.get(d_i+1).ref.setPY(0).clearParent().setParent(drawers.get(d_i-1).ref); 
	        d.ref.clearParent(); drawers.remove(d); }
	      else if (d_i == drawers.size() - 1) { d.ref.clearParent(); drawers.remove(d); }
	      total_height = 0;
	      for (nDrawer dr : drawers) total_height += dr.drawer_height;
	      if (eventHeight != null) eventHeight.run();
	    }
	    return this;
	  }
	  public nShelf clearDrawer() {
	    ArrayList<nDrawer> a = new ArrayList<nDrawer>();
	    for (nDrawer d : drawers) a.add(d);
	    //for (nDrawer d : a) d.clear();
	    return this;
	  }
	  
	  
	  public nList addList(int n, float wf, float hf) {
	    nList d = new nList(this, n, ref_size, wf, hf);
	    insertDrawer(d);
	    return d;
	  }
	  
	  public nExplorer addExplorer() {
	    nExplorer d = new nExplorer(this);
	    insertDrawer(d);
	    return d;
	  }
	}
