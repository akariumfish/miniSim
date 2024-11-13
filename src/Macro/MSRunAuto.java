package Macro;

import processing.core.PVector;
import sData.*;

public class MSRunAuto extends MSetRunner {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("srunauto", "SRun_Auto", 
				"Runner for SetCreator objects - Automate", "Set Tool"); 
		first_start_show(m); }
		MSRunAuto build(Macro_Sheet s, sValueBloc b) { MSRunAuto m = new MSRunAuto(s, b); return m; }
	}
	
	sFlt accel_speed, accel_dir, accel_x, accel_y;
	sFlt rstmov_speed, rstmov_dir, rstmov_x, rstmov_y;
	
	MSRunAuto(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "srunauto", _bloc); 
	}
	public void init() {
		super.init();
		accel_speed = newFlt(1, "accel_speed");
		accel_dir = newFlt(0, "accel_dir");
		accel_x = newFlt(0, "accel_x");
		accel_y = newFlt(0, "accel_y");
		accel_speed.set_resolution(-2);
		accel_dir.set_resolution(-4);
		accel_x.set_resolution(-4);
		accel_y.set_resolution(-4);
		rstmov_speed = newFlt(1, "rstmov_speed");
		rstmov_dir = newFlt(0, "rstmov_dir");
		rstmov_x = newFlt(0, "rstmov_x");
		rstmov_y = newFlt(0, "rstmov_y");
		rstmov_speed.set_resolution(-4);
		rstmov_dir.set_resolution(-4);
		rstmov_x.set_resolution(-4);
		rstmov_y.set_resolution(-4);
		up_accel_coord();
		use_cursordir(accel_dir);
		menuFltIncr(rstmov_y, 10);
	    menuFltIncr(rstmov_x, 10);
	    menuFltFact(rstmov_speed, 2);
	    menuFltIncr(rstmov_speed, 0.1F);
	    menuFltIncr(rstmov_dir, 10);
	    menuFltIncr(accel_y, 10);
//	    globalFltIncr(accel_y, 0.1F, false);
	    menuFltIncr(accel_x, 10);
//	    globalFltIncr(accel_x, 0.1F, false);
	    menuFltFact(accel_speed, 2);
	    globalFltIncr(accel_speed, 0.1F, false);
	    menuFltIncr(accel_dir, 10);
		
		addToInitend(new nRunnable() { public void run() {
			accel_x.addEventChange(new nRunnable() { public void run() { up_accel_params(); }}); 
			accel_y.addEventChange(new nRunnable() { public void run() { up_accel_params(); }}); 
			accel_speed.addEventChange(new nRunnable() { public void run() { up_accel_coord(); }}); 
			accel_dir.addEventChange(new nRunnable() { public void run() { up_accel_coord(); }}); 
			rstmov_x.addEventChange(new nRunnable() { public void run() { up_rstmov_params(); }}); 
			rstmov_y.addEventChange(new nRunnable() { public void run() { up_rstmov_params(); }}); 
			rstmov_speed.addEventChange(new nRunnable() { public void run() { up_rstmov_coord(); }}); 
			rstmov_dir.addEventChange(new nRunnable() { public void run() { up_rstmov_coord(); }}); 
		}});
	}
	public MSRunAuto clear() {
		super.clear();
		return this;
	}
	void up_accel_coord() {
		PVector p = new PVector(accel_speed.get(), 0).rotate(accel_dir.get());
		accel_x.set(p.x); accel_y.set(p.y); }
	void up_accel_params() {
		PVector p = new PVector(accel_x.get(), accel_y.get());
		accel_speed.set(p.mag()); accel_dir.set(p.heading()); }

	void up_rstmov_coord() {
		PVector p = new PVector(rstmov_speed.get(), 0).rotate(rstmov_dir.get());
		rstmov_x.set(p.x); rstmov_y.set(p.y); }
	void up_rstmov_params() {
		PVector p = new PVector(rstmov_x.get(), rstmov_y.get());
		rstmov_speed.set(p.mag()); rstmov_dir.set(p.heading()); }

	void tick_obj(MSet.SetObj o) {
		o.accel.add(accel_x.get(), accel_y.get()); }
	void init_obj(MSet.SetObj o) {
		o.mov.add(rstmov_x.get(), rstmov_y.get()); }
}
