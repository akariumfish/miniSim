package Macro;

import Macro.MSet.SetObj;
import sData.*;

public class MSModBase extends MSetModel {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("smodbase", "SMod_Basic", 
				"Model for Set objects - Basic", "Set Tool"); 
		first_start_show(m); }
		MSModBase build(Macro_Sheet s, sValueBloc b) { MSModBase m = new MSModBase(s, b); return m; }
	}
	
	sFlt val_radius, val_strokeweight;
	sBoo val_dofill, val_dostroke;
	
	MSModBase(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "smodbase", _bloc); 
	}
	public void init() {
		super.init();
		val_strokeweight = menuFltSlide(2.0F, 0.5F, 10.0F, "val_strokeweight");
		val_dofill = newBoo(false, "val_dofill");
		val_dostroke = newBoo(true, "val_dostroke");
		menuBoo(val_dofill, val_dostroke);
	}

	void draw_obj(MSet.SetObj o) {
		if (val_dofill.get()) app.fill(255);
		else app.noFill();
		if (val_dostroke.get()) {
			app.stroke(255);
			app.strokeWeight(val_strokeweight.get());
		}
		else app.noStroke();
		app.ellipse(o.pos.x - o.radius, o.pos.y - o.radius, 
				o.radius * 2, o.radius * 2);
	}
}