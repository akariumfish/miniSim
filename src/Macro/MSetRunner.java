package Macro;

import Macro.MSet.SetObj;
import RApplet.RConst;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nGUI;
import processing.core.PApplet;
import processing.core.PVector;
import sData.*;

public abstract class MSetRunner extends MBaseMT {

	Rapp app;
	sInterface inter;
  	nGUI cam_gui;
  	float ref_size;
  
  	Macro_Connexion link_out;
  	MSetSubset sset;
  	boolean use_cursordir;
  	nRunnable use_cursdir_run;
  	sFlt dir_cursdir_cible;
  	
  	sBoo do_max_age;
  	sInt max_age;
  
	MSetRunner(Macro_Sheet _sheet, String _typ, sValueBloc _bloc) { 
		super(_sheet, _typ, _bloc, "setrunner"); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		
		init_access();
		
		sset = null;
		use_cursordir = false;
		dir_cursdir_cible = null;
		use_cursdir_run = new nRunnable() { public void run() {
			if (dir_cursdir_cible != null && sset != null && sset.creator != null)
				dir_cursdir_cible.set(sset.creator.add_ref_cursor.dir().heading());
		}};
		
		do_max_age = newBoo(false, "do_max_age");
		max_age = newInt(4000, "max_age");
		menuIntIncr(max_age, 1000);
	    menuBoo(do_max_age);
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	    super.build_normal(); 
		link_out = addOutput(2, "Link_out").set_link();
		link_out.addEventChangeLink(new nRunnable() { public void run() { 
			deconnect(); 
			sset = null;
		  	for (Macro_Connexion c : link_out.connected_inputs) {
		  		if (c.elem.bloc.val_type.get().equals("subset")) {
		  			sset = (MSetSubset)c.elem.bloc;
		  			break; }
		  	}
		    reconnect(); 
		}});
	}
	void use_cursordir(sFlt r) { 
		use_cursordir = true; 
		dir_cursdir_cible = r; 
		addToInitend(new nRunnable() { public void run() { reconnect(); }});
	}
	void rebuild() {  super.rebuild();  }
	public MSetRunner clear() {
		super.clear(); 
		deconnect();
		return this; }
	void deconnect() {
		if (use_cursordir && sset != null && sset.creator != null) {
			sset.creator.add_ref_cursor.dval.removeEventChange(use_cursdir_run);
			sset.creator.add_ref_cursor.show_dir = false;
			sset.creator.add_ref_cursor.update_view();
		}
	}
	void reconnect() { 
		if (use_cursordir && sset != null && sset.creator != null) {
			sset.creator.add_ref_cursor.dval.addEventChange(use_cursdir_run);
			sset.creator.add_ref_cursor.show_dir = true;
			sset.creator.add_ref_cursor.update_view();
		}
	}

	void reset() { ; }
	void tick() { 
		if (sset != null) {
			if (do_max_age.get()) for (int i = sset.objects.size() - 1 ; i >= 0 ; i--) {
				SetObj o = sset.objects.get(i);
				if (o.age >= max_age.get()) o.clear();
			}
	
			for (SetObj o : sset.objects) tick_obj_strt(o);
			for (int i = 0 ; i < sset.objects.size() ; i++)
			for (int j = i+1 ; j < sset.objects.size() ; j++) {
					pair_obj(sset.objects.get(i), sset.objects.get(j)); }
			for (SetObj o : sset.objects) tick_obj_end(o);
		}
	}
	void tick_end() { }
	abstract void init_obj(MSet.SetObj o);
	abstract void tick_obj_strt(MSet.SetObj o);
	void pair_obj(MSet.SetObj o1, MSet.SetObj o2) {}
	abstract void tick_obj_end(MSet.SetObj o);
}


class MSRunStatic extends MSetRunner {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("srunstat", "SRun_Static", 
				"Runner for SetCreator objects - Static", "Set Tool"); 
		first_start_show(m); }
		MSRunStatic build(Macro_Sheet s, sValueBloc b) { MSRunStatic m = new MSRunStatic(s, b); return m; }
	}
	
	MSRunStatic(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "srunstat", _bloc); 
	}

	void tick_strt() {  }
	void init_obj(MSet.SetObj o) { o.mov.set(0,0); }
	void tick_obj_strt(SetObj o) {  }
	void tick_obj_end(SetObj o) {  }
	void tick_end() { 
		if (sset != null) for (SetObj o : sset.objects) {
			o.pos.set(o.pos_strt); o.mov.set(0,0); }
	}
}

class MSRunBoids extends MSetRunner {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("srunboids", "SRun_Boids", 
				"Runner for SetCreator objects - Boids", "Set Tool"); 
		first_start_show(m); }
		MSRunBoids build(Macro_Sheet s, sValueBloc b) { MSRunBoids m = new MSRunBoids(s, b); return m; }
	}
	
//  	nCursor cible_cursor;
//  	sVec cible_pos_vec;
//  	sBoo show_cible_cursor, use_cible;
  	
  	sFlt track_fact, avoid_fact, follow_fact, vision_dist, close_dist,
  			speed_min, speed_max;
	
	MSRunBoids(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "srunboids", _bloc); 
	}
	public void init() {
		super.init();

		vision_dist = newFlt(200, "vision_dist");
		close_dist = newFlt(30, "close_dist");
		track_fact = newFlt(0.00024F, "track_fact");
		avoid_fact = newFlt(0.0005F, "avoid_fact");
		follow_fact = newFlt(0.00009F, "follow_fact");
		speed_min = newFlt(0.0001F, "speed_min");
		speed_max = newFlt(4.0F, "speed_max");

		menuFltFact(vision_dist, 2);
		menuFltFact(close_dist, 2);
		menuFltFact(track_fact, 2);
		menuFltFact(avoid_fact, 2);
		menuFltFact(follow_fact, 2);
		menuFltFact(speed_min, 2);
		menuFltFact(speed_max, 2);
		
//		min_space.set_resolution(-4);
		
//		cible_pos_vec = newVec("cible_pos_vec");
//	    show_cible_cursor = newBoo(true, "show_cible_cursor");
//	    use_cible = newBoo(false, "use_cible");
//	    globalBin(show_cible_cursor, use_cible, false);
//	    cible_cursor = sheet.newCursor(value_bloc.ref, false);
//	    show_cible_cursor.addEventChange(new nRunnable() { public void run() { 
//	    		if (cible_cursor.show != null) 
//	    			cible_cursor.show.set(show_cible_cursor.get()); }});
//	    if (cible_cursor.show != null) cible_cursor.show.set(show_cible_cursor.get());
//	    if (cible_cursor.show != null) cible_cursor.show.addEventChange(new nRunnable() { public void run() { 
//	    		show_cible_cursor.set(cible_cursor.show.get()); }});
//	    cible_pos_vec.addEventChange(new nRunnable() { public void run() { 
//	        if (cible_cursor.pval != null) { 
//	        		cible_cursor.pval.set(cible_pos_vec.get().x, cible_pos_vec.get().y); }
//	    }});
//	    if (cible_cursor.pval != null) cible_cursor.pval.set(cible_pos_vec.get());
//	    if (cible_cursor.pval != null) cible_cursor.pval.addEventChange(new nRunnable() { public void run() { 
//	    		cible_pos_vec.set(cible_cursor.pval.get().x, 
//	    				cible_cursor.pval.get().y); }});
	}
	public MSRunBoids clear() {
		super.clear();
//		cible_pos_vec.clear();
		return this;
	}
	void tick_obj_strt(MSet.SetObj o) { 
		o.neigh_posavg.set(0,0); 
		o.neigh_movavg.set(0,0);
		o.avoid_posavg.set(0,0); 
		o.avoid_number = 0;
		o.neigh_number = 0;
	}
	void tick_obj_end(SetObj o) {
		if (o.neigh_number > 0) {
			o.neigh_posavg.mult(1.0F/o.neigh_number);
			o.neigh_movavg.mult(1.0F/o.neigh_number);
			o.mov.x += (o.mov.x + 
	                (o.neigh_posavg.x - o.pos.x)*track_fact.get() + 
	                (o.neigh_movavg.x - o.mov.x)*follow_fact.get() );
			o.mov.y += (o.mov.y + 
	                (o.neigh_posavg.y - o.pos.y)*track_fact.get() + 
	                (o.neigh_movavg.y - o.mov.y)*follow_fact.get() );
		}
		if (o.avoid_number > 0) {
			o.avoid_posavg.mult(1.0F/o.avoid_number);
			o.mov.x += (o.mov.x + 
	                (o.avoid_posavg.x - o.pos.x)*avoid_fact.get() );
			o.mov.y += (o.mov.y + 
	                (o.avoid_posavg.y - o.pos.y)*avoid_fact.get() );
		}
		if (o.mov.mag() < speed_min.get()) o.mov.setMag(speed_min.get());
		if (o.mov.mag() > speed_max.get()) o.mov.setMag(speed_max.get());
	}
	void init_obj(MSet.SetObj o) { }
	void pair_obj(MSet.SetObj s1, MSet.SetObj s2) {
		float d = RConst.dist(s1.pos.x, s1.pos.y, s2.pos.x, s2.pos.y);
		if (d < vision_dist.get() && d > close_dist.get()) {
			s1.neigh_posavg.add(s2.pos);
			s2.neigh_posavg.add(s1.pos);
			s1.neigh_movavg.add(s2.mov);
			s2.neigh_movavg.add(s1.mov);
			s1.neigh_number++;
			s2.neigh_number++;
		} else if (d < close_dist.get()) {
			s1.avoid_posavg.add(s2.pos);
			s2.avoid_posavg.add(s1.pos);
			s1.avoid_number++;
			s2.avoid_number++;
		}
	}
}

class MSRunAuto extends MSetRunner {

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
			nRunnable up_accel_params = new nRunnable() { public void run() { 
				PVector p = new PVector(accel_x.get(), accel_y.get());
				accel_speed.set(p.mag()); accel_dir.set(p.heading());  }};
			nRunnable up_accel_coord = new nRunnable() { public void run() { 
				PVector p = new PVector(accel_speed.get(), 0).rotate(accel_dir.get());
				accel_x.set(p.x); accel_y.set(p.y); }};
			nRunnable up_rstmov_params = new nRunnable() { public void run() { 
				PVector p = new PVector(rstmov_x.get(), rstmov_y.get());
				rstmov_speed.set(p.mag()); rstmov_dir.set(p.heading()); }};
			nRunnable up_rstmov_coord = new nRunnable() { public void run() { 
				PVector p = new PVector(rstmov_speed.get(), 0).rotate(rstmov_dir.get());
				rstmov_x.set(p.x); rstmov_y.set(p.y); }};
			up_accel_coord.run();
			accel_x.addEventChange(up_accel_params); 
			accel_y.addEventChange(up_accel_params); 
			accel_speed.addEventChange(up_accel_coord); 
			accel_dir.addEventChange(up_accel_coord); 
			rstmov_x.addEventChange(up_rstmov_params); 
			rstmov_y.addEventChange(up_rstmov_params); 
			rstmov_speed.addEventChange(up_rstmov_coord); 
			rstmov_dir.addEventChange(up_rstmov_coord); 
		}});
	}
	public MSRunAuto clear() {
		super.clear();
		return this;
	}
	void init_obj(MSet.SetObj o) {
		o.mov.add(rstmov_x.get(), rstmov_y.get()); }
	void tick_obj_strt(MSet.SetObj o) {
		o.accel.add(accel_x.get(), accel_y.get()); }
	void tick_obj_end(SetObj o) { }
}


