package Macro;

import RApplet.RConst;
import UI.nCursor;
import processing.core.PApplet;
import processing.core.PVector;
import sData.*;

public class MSRunFloc extends MSetRunner {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("srunfloc", "SRun_Floc", 
				"Runner for SetCreator objects - Floc", "Set Tool"); 
		first_start_show(m); }
		MSRunFloc build(Macro_Sheet s, sValueBloc b) { MSRunFloc m = new MSRunFloc(s, b); return m; }
	}
	
	sFlt min_space, max_space, cible_space, rot_speed, cible_rot_speed, accel_speed, 
		cible_accel_speed, max_speed;

  	nCursor cible_cursor;
  	sVec cible_pos_vec;
  	sBoo show_cible_cursor, use_cible;
	
	MSRunFloc(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "srunfloc", _bloc); 
	}
	public void init() {
		super.init();
		min_space = newFlt(100, "min_space");
		max_space = newFlt(300, "max_space");
		cible_space = newFlt(200, "cible_space");
		cible_accel_speed = newFlt(0.1F, "cible_accel_speed");
		cible_rot_speed = newFlt(0.1F, "cible_rot_speed");
		rot_speed = newFlt(0.1F, "rot_speed");
		accel_speed = newFlt(0.1F, "accel_speed");
		max_speed = newFlt(0.1F, "max_speed");
		min_space.set_resolution(-4);
		max_space.set_resolution(-4);
		cible_space.set_resolution(-4);
		cible_accel_speed.set_resolution(-4);
		cible_rot_speed.set_resolution(-4);
		rot_speed.set_resolution(-4);
		accel_speed.set_resolution(-4);
		max_speed.set_resolution(-4);
		menuFltFact(min_space, 2);
		menuFltFact(max_space, 2);
		menuFltFact(rot_speed, 2);
		menuFltFact(accel_speed, 2);
		menuFltFact(max_speed, 2);
		menuFltFact(cible_rot_speed, 2);
		menuFltFact(cible_accel_speed, 2);
		menuFltFact(cible_space, 2);
		
		cible_pos_vec = newVec("cible_pos_vec");
	    show_cible_cursor = newBoo(true, "show_cible_cursor");
	    use_cible = newBoo(false, "use_cible");
	    globalBin(show_cible_cursor, use_cible, false);
	    cible_cursor = sheet.newCursor(value_bloc.ref, false);
	    show_cible_cursor.addEventChange(new nRunnable() { public void run() { 
	    		if (cible_cursor.show != null) 
	    			cible_cursor.show.set(show_cible_cursor.get()); }});
	    if (cible_cursor.show != null) cible_cursor.show.set(show_cible_cursor.get());
	    if (cible_cursor.show != null) cible_cursor.show.addEventChange(new nRunnable() { public void run() { 
	    		show_cible_cursor.set(cible_cursor.show.get()); }});
	    cible_pos_vec.addEventChange(new nRunnable() { public void run() { 
	        if (cible_cursor.pval != null) { 
	        		cible_cursor.pval.set(cible_pos_vec.get().x, cible_pos_vec.get().y); }
	    }});
	    if (cible_cursor.pval != null) cible_cursor.pval.set(cible_pos_vec.get());
	    if (cible_cursor.pval != null) cible_cursor.pval.addEventChange(new nRunnable() { public void run() { 
	    		cible_pos_vec.set(cible_cursor.pval.get().x, 
	    				cible_cursor.pval.get().y); }});
	}
	public MSRunFloc clear() {
		super.clear();
		return this;
	}
	void tick_obj(MSet.SetObj o) { 
//		if (o.accel.mag() > max_speed.get()) o.accel.setMag(max_speed.get());
//		if (o.mov.mag() > max_speed.get()) o.mov.setMag(max_speed.get());
		if (use_cible.get()) {
			float d = RConst.distSqPointPoint(o.pos.x, o.pos.y, cible_pos_vec.x(), cible_pos_vec.y());
			if (d > min_space.get() * min_space.get()) {
				o.accel.add(headTo(o.pos, o.mov, cible_pos_vec.get(), 
						cible_accel_speed.get(), cible_rot_speed.get()));
			}
		}
	}
	void init_obj(MSet.SetObj o) { }
	void pair_obj(MSet.SetObj s1, MSet.SetObj s2) {
		float d = RConst.distSqPointPoint(s1.pos.x, s1.pos.y, s2.pos.x, s2.pos.y);
		if (d < min_space.get() * min_space.get()) {
			s1.accel.add(headAway(s1.pos, s1.mov, s2.pos, 
					accel_speed.get(), rot_speed.get()));
			s2.accel.add(headAway(s2.pos, s2.mov, s1.pos, 
					accel_speed.get(), rot_speed.get())); }
		else if (d > max_space.get() * max_space.get()) {
			s1.accel.add(headTo(s1.pos, s1.mov, s2.pos, 
					accel_speed.get(), rot_speed.get()));
			s2.accel.add(headTo(s2.pos, s2.mov, s1.pos, 
					accel_speed.get(), rot_speed.get()));
		}
	}
	
	PVector headTo(PVector p, PVector m, PVector c, float speed, float rot) {
	    PVector l = new PVector(c.x, c.y);
	    l.add(-p.x, -p.y);
	    float r = RConst.mapToCircularValues(m.heading(), l.heading(), rot_speed.get(), 
	    		-RConst.PI, RConst.PI);
	    float sp = PApplet.max(m.mag(), speed);
	    PVector m2 = new PVector(sp, 0);
	    m2.rotate(r);
	    m2.add(-m.x,-m.y);
	    return m2;
	}
	PVector headAway(PVector p, PVector m, PVector c, float speed, float rot) {
	    PVector l = new PVector(c.x, c.y);
	    l.add(-p.x, -p.y);
	    l.mult(-1);
	    float r = RConst.mapToCircularValues(m.heading(), l.heading(), rot_speed.get(), 
	    		-RConst.PI, RConst.PI);
	    float sp = PApplet.max(m.mag(), speed);
	    PVector m2 = new PVector(sp, 0);
	    m2.rotate(r);
	    m2.add(-m.x,-m.y);
	    return m2;
	}
}
