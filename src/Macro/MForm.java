package Macro;

import RApplet.Rapp;
import UI.Drawable;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nWidget;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sValueBloc;
import z_old_specialise.nBase;

public class MForm extends MBaseMenu { 
	static class MForm_Builder extends MAbstract_Builder {
		MForm_Builder() { super("form", "basic form"); show_in_buildtool = true; }
		MForm build(Macro_Sheet s, sValueBloc b) { MForm m = new MForm(s, b); return m; }
	}
	
	nBase shape;
	sBoo is_line;
	sFlt val_scale, vpax, vpay, vpbx, vpby, vpcx, vpcy, val_linew;
	sCol val_fill, val_stroke;
	sInt val_draw_layer;

	Macro_Connexion link;
	MForm(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "form", _bloc); }
	void init() {
		super.init();
	    shape = new nBase(gui.app);
	    is_line = menuBoo(false, "is_line");
	    val_draw_layer = menuIntIncr(0, 1, "val_draw_layer");
	    val_scale = menuFltSlide(500, 1, 1000, "scale");
	    shape.dir.setMag(val_scale.get());
	    val_linew = menuFltSlide(0.05F, 0.01F, 1.0F, "line_weight");
	    shape.line_w = val_linew.get();
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
	//    
	}
	void build_normal() {
		super.build_normal();
		link = addOutput(1,"link");
	}

  nWidget graph;
  Drawable g_draw;
  
  public void build_custom_menu(nFrontPanel sheet_front) {
    if (sheet_front != null) {
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
        if (val_scale.get() < 400) thm *= 400.0F / val_scale.get();
        shape.dir.setMag(thm/4);
        shape.draw(gui.app, is_line.get());
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

	public void draw(Rapp a) { 	
		shape.draw(a, is_line.get()); 
	}

	public MForm clear() {
		super.clear(); 
		return this; }
	public MForm toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
