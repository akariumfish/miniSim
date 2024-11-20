package Macro;

import RApplet.RConst;
import UI.nFrontPanel;
import processing.core.PVector;
import sData.sBoo;
import sData.sFlt;
import sData.sValueBloc;

public class MSModArrow extends MSetModel {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("smodarrow", "SMod_Arrow", 
				"Model for Set objects - Arrow", "Set Tool"); 
		first_start_show(m); }
		MSModArrow build(Macro_Sheet s, sValueBloc b) { MSModArrow m = new MSModArrow(s, b); return m; }
	}
	
	
	sFlt val_strokeweight;
	sBoo val_doradius, val_doarrow;
	
	MSModArrow(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "smodarrow", _bloc); 
	}
	public void init() {
		super.init();
		val_strokeweight = menuFltSlide(2.0F, 0.5F, 10.0F, "val_strokeweight");
		val_doradius = newBoo(true, "val_doradius");
		val_doarrow = newBoo(true, "val_doarrow");
		menuBoo(val_doradius, val_doarrow);
	}

	void draw_obj(MSet.SetObj o) {
		app.noFill();
		app.stroke(255);
		app.strokeWeight(val_strokeweight.get());
		if (val_doradius.get()) 
			app.ellipse(o.pos.x - o.radius, o.pos.y - o.radius, 
				o.radius * 2, o.radius * 2);
		if (val_doarrow.get()) {
			PVector depl = new PVector(o.pos_chng.x, o.pos_chng.y).setMag(o.radius);
			app.line(o.pos.x - depl.x, o.pos.y - depl.y, o.pos.x + depl.x, o.pos.y + depl.y);
			PVector arrow_end = new PVector(depl.x, depl.y);
			arrow_end.rotate(-RConst.PI / 8.0F);
			app.line(o.pos.x + depl.x, o.pos.y + depl.y, 
					o.pos.x + depl.x - arrow_end.x, o.pos.y + depl.y - arrow_end.y);
			arrow_end.rotate(RConst.PI / 4.0F);
			app.line(o.pos.x + depl.x, o.pos.y + depl.y, 
					o.pos.x + depl.x - arrow_end.x, o.pos.y + depl.y - arrow_end.y);
		}
	}
}
