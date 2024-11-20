package Macro;

import Macro.MSet.SetObj;
import RApplet.RConst;
import processing.core.PApplet;
import processing.core.PVector;
import sData.sBoo;
import sData.sFlt;
import sData.sValueBloc;

public class MSLawSpace extends MSetLaw {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("slawspace", "SLaw_Space", 
				"Law on Set objects - Space", "Set Tool"); 
		first_start_show(m); }
		MSLawSpace build(Macro_Sheet s, sValueBloc b) { MSLawSpace m = new MSLawSpace(s, b); return m; }
	}
	
	sFlt grav_const, frictn_const, solid_const;
	sBoo do_grav, do_frictn, do_solid;
	
	MSLawSpace(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "slawspace", _bloc); 
	}
	public void init() {
		super.init();
		grav_const = newFlt(0.6F, "grav_const");
		frictn_const = newFlt(0.00015F, "frictn_const");
		solid_const = newFlt(0.04F, "solid_const");
	    globalFltFact(grav_const, 2, false);
	    globalFltFact(frictn_const, 2, false);
	    globalFltFact(solid_const, 2, false);
		do_grav = newBoo(true, "do_grav");
		do_frictn = newBoo(true, "do_frictn");
		do_solid = newBoo(true, "do_solid");
	    globalBin(do_grav, do_frictn, do_solid, false);
	}

	void tick_obj(SetObj o) { 
		if (do_frictn.get())
			o.mov.mult(PApplet.max(0, 1.0F - (frictn_const.get())) );
	}
	void pair_obj(SetObj s1, SetObj s2) {
		float d = 0;
		if (do_grav.get() || do_solid.get()) {
			d = RConst.distSqPointPoint(s1.pos.x, s1.pos.y, s2.pos.x, s2.pos.y);
			if (d < (s1.radius+s2.radius) / 4.0F) d = (s1.radius+s2.radius) / 4.0F;
		}
		if (do_grav.get()) {
			PVector gpush = new PVector(s1.pos.x - s2.pos.x, s1.pos.y - s2.pos.y);
			float tmp = (s1.mass+s2.mass) * grav_const.get() / d;
			if (tmp*tmp < d) {
				gpush.setMag(tmp);
			  	s2.accel.add(gpush);
			  	gpush.mult(-1);
			  	s1.accel.add(gpush);
			}
		}
	  	if (do_solid.get() && d < (s1.radius+s2.radius)*(s1.radius+s2.radius)) {
			PVector spush = new PVector(s1.pos.x - s2.pos.x, s1.pos.y - s2.pos.y);
			spush.mult(solid_const.get() * s2.density / (s1.density + s2.density));
			s1.accel.add(spush);
			spush.mult(-(s1.density / (s1.density + s2.density)) / (s2.density / (s1.density + s2.density)));
			s2.accel.add(spush);
	  	}
	}
}
