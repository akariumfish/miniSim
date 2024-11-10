package Macro;


import java.util.ArrayList;

import RApplet.RConst;
import UI.Drawable;
import sData.*;

public class MCurve extends MBasic {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("curve", "Curve", "Draw a Curve", "Work"); 
		first_start_show(m); }
		MCurve build(Macro_Sheet s, sValueBloc b) { MCurve m = new MCurve(s, b); return m; }
	}
	
	Drawable drawable;
	sInt point_nb, curv_nb;
	sFlt size;
	
	ArrayList<sFlt> curv_point_ys;

	MCurve(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "curve", _bloc); 
	}
	public void init() {
		super.init();
		init_access();
		size = newFlt(30, "size");
		point_nb = newInt(10, "point_nb");
		if (!loading_from_bloc) point_nb.set_limit(2,10);
		curv_nb = newInt(10, "curv_nb");
		if (!loading_from_bloc) curv_nb.set_limit(1,16);
		
		curv_point_ys = new ArrayList<sFlt>();
		for (int i = 0; i < point_nb.get(); i++) {
			sFlt y = newFlt(0, "y"+i);
			if (!loading_from_bloc) y.set(i / 10.0);
			if (!loading_from_bloc && i%2 == 0) y.mult(-1);
			if (!loading_from_bloc && i == point_nb.get() - 1) y.set(0);
			curv_point_ys.add(y);
		}
		drawable = new Drawable() { public void drawing() { draw_to_cam(); } };
		mmain().inter.addToCamDrawerPile(drawable);
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	    super.build_normal(); 
	    addEmptyS(0);
	}
	void draw_curve() {
		if (curv_point_ys.size() > 0) {
			gui.app.beginShape();
			float cx = 0, cy = curv_point_ys.get(0).get();
			gui.app.curveVertex(cx , cy);
			for (sFlt fy : curv_point_ys) {
				cx += size.get();
				cy = fy.get() * size.get();
				gui.app.curveVertex(cx , cy);
			}
			cx += size.get();
			gui.app.curveVertex(cx , cy);
			gui.app.endShape();
		}
	}
	void draw_to_cam() {
		gui.app.noFill();
		gui.app.stroke(255);
		gui.app.strokeWeight(3);
		for (int i = 0 ; i < curv_nb.get() ; i++) {
			gui.app.pushMatrix();
			gui.app.rotate(RConst.PI * 2.0F * (float)i / (float)curv_nb.get());
			draw_curve();
			gui.app.popMatrix();
		}
	}
	public MCurve clear() {
		super.clear(); 
		drawable.clear();
		return this; }
}