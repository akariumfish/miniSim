package Specialize;

import Macro.Macro_Sheet;
import Macro.Sheet_Specialize;
import RApplet.Rapp;
import UI.Drawable;
import UI.nCursor;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nWidget;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sValueBloc;




public class Face extends Macro_Sheet {
	

public static class FacePrint extends Sheet_Specialize {
  Canvas sim;
  public FacePrint(Canvas s) { super("Face"); sim = s; }
  public void default_build() { }
  public Face get_new(Macro_Sheet s, String n, sValueBloc b) { return new Face(sim, b); }
}
  nWidget graph;
  Drawable g_draw;
  
  public void build_custom_menu(nFrontPanel sheet_front) {
    if (sheet_front != null) {
      
      sheet_front.getTab(2).getShelf()
        .addSeparator(0.125)
        .addDrawerFactValue(val_dens, 2, 10, 1)
        .addSeparator(0.125)
        .addDrawerFactValue(val_disp, 2, 10, 1)
        .addSeparator(0.125)
        .addDrawerDoubleButton(val_halo_type, val_halo_type2, 10, 1)
        .addSeparator(0.125)
        .addDrawerButton(val_mirage, 10, 1)
        .addSeparator(0.125)
        .addDrawerButton(val_transf, 10, 1)
        .addSeparator(0.125);
      
      nDrawer dr = sheet_front.addTab("Shape").getShelf()
    	        .addSeparator(0.125)
    	        .addDrawer(10.25, 6.25);
      
      graph = dr.addModel("Field");
      graph.setPosition(ref_size * 2, ref_size * 2 / 16)
        .setSize(ref_size * 6, ref_size * 6);
      
      g_draw = new Drawable(sheet_front.gui.drawing_pile, 0) { public void drawing() {
        gui.app.fill(graph.look.standbyColor);
        gui.app.noStroke();
        gui.app.rect(graph.getX(), graph.getY(), graph.getSX(), graph.getSY());
        gui.app.pushMatrix();
        gui.app.translate(graph.getX() + graph.getSX()/2 - shape.pos.x, 
                  graph.getY() + graph.getSY()/2 - shape.pos.y);
        float hm = shape.dir.mag();
        float thm = hm;
        if (shape.nrad() > 2) thm = thm / (shape.nrad() / 2);
        shape.dir.setMag(thm/4);
        shape.draw(gui.app);
        shape.dir.setMag(hm);
        gui.app.strokeWeight(3);
        gui.app.stroke(255);
        float fac = 1.2F * 100 / val_scale.get();
        gui.app.line(shape.pos.x,shape.pos.y,shape.pos.x + shape.dir.x*fac,shape.pos.y + shape.dir.y*fac);
        gui.app.stroke(180);
        gui.app.line(shape.pos.x,shape.pos.y,shape.pos.x - shape.dir.x*fac,shape.pos.y - shape.dir.y*fac);
        gui.app.popMatrix();
      } };
      graph.setDrawable(g_draw);
      
      dr.addLinkedModel("Field").setLinkedValue(vpax).setPosition(ref_size * 2 / 16, ref_size * (0+(4.0/16)))
        .setSize(ref_size * 1.75, ref_size * 0.75);
      dr.addLinkedModel("Field").setLinkedValue(vpay).setPosition(ref_size * 2 / 16, ref_size * (1+(4.0/16)))
        .setSize(ref_size * 1.75, ref_size * 0.75);
      dr.addLinkedModel("Field").setLinkedValue(vpbx).setPosition(ref_size * 2 / 16, ref_size * (2+(4.0/16)))
        .setSize(ref_size * 1.75, ref_size * 0.75);
      dr.addLinkedModel("Field").setLinkedValue(vpby).setPosition(ref_size * 2 / 16, ref_size * (3+(4.0/16)))
        .setSize(ref_size * 1.75, ref_size * 0.75);
      dr.addLinkedModel("Field").setLinkedValue(vpcx).setPosition(ref_size * 2 / 16, ref_size * (4+(4.0/16)))
        .setSize(ref_size * 1.75, ref_size * 0.75);
      dr.addLinkedModel("Field").setLinkedValue(vpcy).setPosition(ref_size * 2 / 16, ref_size * (5+(4.0/16)))
        .setSize(ref_size * 1.75, ref_size * 0.75);
      
      sheet_front.addEventClose(new nRunnable(this) { public void run() { 
        graph = null; g_draw.clear(); g_draw = null; } } );
      sheet_front.toLayerTop();
    }
  }
  
  Canvas can;
  nBase shape;
  nCursor ref_cursor;
  sFlt val_scale, vpax, vpay, vpbx, vpby, vpcx, vpcy, val_linew, val_dens, val_disp;
  sCol val_fill, val_stroke;
  sInt val_draw_layer;
  sBoo val_halo_type, val_halo_type2, val_mirage, val_transf;
  
  
//  ArrayList<nCursor> duplication_cursors = new ArrayList<nCursor>();
  
  Face(Canvas m, sValueBloc b) { 
    super(m.mmain(), "Face", b);
    can = m;
    can.faces.add(this);
    can.sim.faces.add(this);
    shape = new nBase(gui.app);
    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");
    val_scale = menuFltSlide(500, 1, 1000, "scale");
    shape.dir.setMag(val_scale.get());
    val_linew = menuFltSlide(0.05F, 0.01F, 1.0F, "line_weight");
    shape.line_w = val_linew.get();
    val_dens = newFlt(0.5F, "val_dens", "val_dens");
    val_disp = newFlt(5, "val_disp", "val_disp");
    val_halo_type = newBoo(false, "val_halo_type", "val_halo_type");
    val_halo_type2 = newBoo(false, "val_halo_type2", "val_halo_type2");
    val_mirage = newBoo(false, "val_mirage", "val_mirage");
    val_transf = newBoo(false, "val_transf", "val_transf");
    vpax = newFlt(shape.face.p1.x, "vpax", "vpax");
    vpay = newFlt(shape.face.p1.y, "vpay", "vpay");
    vpbx = newFlt(shape.face.p2.x, "vpbx", "vpbx");
    vpby = newFlt(shape.face.p2.y, "vpby", "vpby");
    vpcx = newFlt(shape.face.p3.x, "vpcx", "vpcx");
    vpcy = newFlt(shape.face.p3.y, "vpcy", "vpcy");

    vpax.addEventChange(new nRunnable() { public void run() { shape.face.p1.x = vpax.get(); }});
    vpay.addEventChange(new nRunnable() { public void run() { shape.face.p1.y = vpay.get(); }});
    vpbx.addEventChange(new nRunnable() { public void run() { shape.face.p2.x = vpbx.get(); }});
    vpby.addEventChange(new nRunnable() { public void run() { shape.face.p2.y = vpby.get(); }});
    vpcx.addEventChange(new nRunnable() { public void run() { shape.face.p3.x = vpcx.get(); }});
    vpcy.addEventChange(new nRunnable() { public void run() { shape.face.p3.y = vpcy.get(); }});
    shape.face.p1.x = vpax.get(); 
    shape.face.p1.y = vpay.get(); 
    shape.face.p2.x = vpbx.get(); 
    shape.face.p2.y = vpby.get();
    shape.face.p3.x = vpcx.get();
    shape.face.p3.y = vpcy.get();
    
    val_stroke = menuColor(gui.app.color(10, 190, 40), "val_stroke");
    val_fill = menuColor(gui.app.color(30, 90, 20), "val_fill");
    val_stroke.addEventChange(new nRunnable() { public void run() { shape.col_line = val_stroke.get(); }});
    val_fill.addEventChange(new nRunnable() { public void run() { shape.col_fill = val_fill.get(); }});
    
    val_scale.addEventChange(new nRunnable() { public void run() { shape.dir.setMag(val_scale.get()); }});
    val_linew.addEventChange(new nRunnable() { public void run() { shape.line_w = val_linew.get(); }});
    
    ref_cursor = menuSheetCursor("center", true);
    //ref_cursor.show.set(true);
    if (ref_cursor.pval != null) ref_cursor.pval.addEventChange(new nRunnable() { public void run() { 
      shape.pos.set(ref_cursor.pval.get()); }});
    if (ref_cursor.dval != null) ref_cursor.dval.addEventChange(new nRunnable() { public void run() {
      shape.dir.set(shape.dir.mag(), 0);
      shape.dir.rotate(ref_cursor.dval.get().heading());
    }});
  }
  
  
  
  void tick() {
	  if (val_transf.get()) {
		  PixelTransform rn = new PixelTransform() { public int result(int v) {
			  float r = gui.app.red(v);
			  float g = gui.app.green(v);
			  float b = gui.app.blue(v);
			  if ( r > 10) r-=5;
			  if ( g > 10) g-=5;
			  if ( b > 10) b-=5;
			  return gui.app.color(r, g, b);
		  } };
		  can.shape_transform(shape, rn);
	  } else  {
	    if (!val_halo_type.get()) {
	      if (!val_halo_type2.get()) {
	        can.draw_halo(shape.pos, val_scale.get()*val_disp.get(), val_dens.get(), val_fill.get());
	      } else { can.draw_halo(shape.pos, val_scale.get()*val_disp.get(), val_dens.get(), val_fill.get()); }
	    } else { 
	      if (!val_halo_type2.get()) {
	        can.draw_shape_line(shape, val_scale.get()*val_disp.get(), val_dens.get(), val_stroke.get());
	      } else { can.draw_shape_fill(shape, val_scale.get()*val_disp.get(), val_dens.get(), val_fill.get()); }
	    }
	  }
  }
  
  void draw(Rapp a) { 	
	  shape.draw(a); 
	  if (val_mirage.get()) {
		  PVector tp = new PVector();
		  PVector td = new PVector();
		  tp.set(shape.pos); td.set(shape.dir);
		  for (nCursor c : sheet_cursors_list) if (c != ref_cursor) {
			  shape.pos.set(c.pos()); 
//			  if (c.dir().mag() > 0.01) { 
				  shape.dir.set(1, 0);
				  shape.dir.rotate(c.dir().heading());
				  shape.dir.setMag(td.mag());
//			  } //else shape.dir.set(td);
			  shape.draw(a); 
		  }
		  shape.pos.set(tp); shape.dir.set(td);
	  }
  }
  
  public Face clear() {
    can.faces.remove(this);
    can.sim.faces.remove(this);
    super.clear();
    return this;
  }
}



