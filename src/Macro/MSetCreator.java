package Macro;

import java.util.ArrayList;

import Macro.MSet.SetObj;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import processing.core.PVector;
import sData.*;

public abstract class MSetCreator  extends MBaseMenu {

	public void build_custom_menu(nFrontPanel sheet_front) {
		nFrontTab tab = sheet_front.addTab("Push");
	    tab.getShelf()
	      .addDrawer(10.25, 0).getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(add_pushval, add_pushin, add_pushout, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_pushx, 1, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_pushx, 100, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_pushy, 1, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_pushy, 100, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_pushspeed, 1, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_pushspeed, 100, 10, 1)
	      .addSeparator(0.125)
	      ;
	}
	
	Rapp app;
	sInterface inter;
  	nGUI cam_gui;
  	float ref_size;
  
  	Macro_Connexion link_out;
  	MSetSubset sset;
	ArrayList<SetObj> objects;

  	sRun add_run;

  	nCursor add_ref_cursor;
  	sVec add_pos_vec;
  	sBoo show_add_cursor;
	sFlt add_pos_x, add_pos_y;
	
	sBoo add_pushval, add_pushout, add_pushin;
	sFlt add_pushx, add_pushy, add_pushspeed;
	
	MSetCreator(Macro_Sheet _sheet, String _n, sValueBloc _bloc) { 
		super(_sheet, _n, _bloc, "screator"); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		init_access();
		sset = null; 
		objects = new ArrayList<SetObj>();

//		add_duration = newInt(100, "add_duration");
//		menuIntIncr(add_duration, 1000);
//		menuIntIncr(add_duration, 10);

		add_pushx = newFlt(0, "add_imovx");
		add_pushy = newFlt(0, "add_imovy");
		add_pushspeed = newFlt(1.0F, "add_pushspeed");
		add_pushval = newBoo(false, "add_pushval");
		add_pushout = newBoo(false, "add_pushout");
		add_pushin = newBoo(false, "add_pushin");
	    add_pos_vec = newVec("val_pos");
	    show_add_cursor = newBoo(true, "show_add_cursor");
	    add_pos_x = newFlt(0, "add_pos_x");
	    add_pos_y = newFlt(0, "add_pos_y");
//	    menuFltIncr(add_pushy, 10);
//	    globalFltIncr(add_pushy, 0.1F, false);
//	    menuFltIncr(add_pushx, 10);
//	    globalFltIncr(add_pushx, 0.1F, false);
	    globalFltIncr(add_pos_y, 10, false);
	    menuFltIncr(add_pos_y, 1000);
	    globalFltIncr(add_pos_x, 10, false);
	    menuFltIncr(add_pos_x, 1000);
	    globalBin(show_add_cursor, false);
	    add_ref_cursor = sheet.newCursor(value_bloc.ref, false);
	    show_add_cursor.addEventChange(new nRunnable() { public void run() { 
	    		if (add_ref_cursor.show != null) add_ref_cursor.show.set(show_add_cursor.get()); }});
	    if (add_ref_cursor.show != null) add_ref_cursor.show.set(show_add_cursor.get());
	    if (add_ref_cursor.show != null) add_ref_cursor.show.addEventChange(new nRunnable() { public void run() { 
	    		show_add_cursor.set(add_ref_cursor.show.get()); }});
	    add_pos_x.addEventChange(new nRunnable() { public void run() { 
    			add_pos_vec.setx(add_pos_x.get()); }});
	    add_pos_y.addEventChange(new nRunnable() { public void run() { 
    			add_pos_vec.sety(add_pos_y.get()); }});
	    add_pos_vec.addEventChange(new nRunnable() { public void run() { 
	        if (add_ref_cursor.pval != null) { 
	        		add_ref_cursor.pval.set(add_pos_vec.get().x, add_pos_vec.get().y); }
	        add_pos_x.set(add_pos_vec.x()); add_pos_y.set(add_pos_vec.y()); 
	    }});
	    if (add_ref_cursor.pval != null) add_ref_cursor.pval.set(add_pos_vec.get());
	    if (add_ref_cursor.pval != null) add_ref_cursor.pval.addEventChange(new nRunnable() { public void run() { 
	    		add_pos_vec.set(add_ref_cursor.pval.get().x, 
	    				add_ref_cursor.pval.get().y); }});

		add_run = newGlobalRun("add_run", false, new nRunnable(this) { public void run() { 
			add(); }});
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
		link_out = addOutput(2, "Link_out").set_link();
		link_out.addEventChangeLink(new nRunnable() { public void run() { 
			MSetSubset prev_set = sset;
			sset = null;
		  	for (Macro_Connexion c : link_out.connected_inputs) {
		  		if (c.elem.bloc.val_type.get().equals("subset"))
		  			sset = (MSetSubset)c.elem.bloc;
		  	}
		  	if (sset != prev_set) reconnect();
		}});
	    super.build_normal(); 
	}
	void init_end() {  super.init_end();  }
	void rebuild() {  super.rebuild(); 
		if (rebuilding = true) for (MSet.SetObj o : objects) {
			((MSetCreator)rebuild_as).objects.add(o);
		}
	}
	public MSetCreator clear() {
		super.clear(); 
		if (sset != null) sset.objects.remove(this);
		add_ref_cursor.clear();
		return this; }

	void reconnect() { }
	
//	int add_len,add_span,add_quent,add_tt;
//	int add_cnt = 0;
//	void add_tick() {
//		if (add_len > 0) {
//			add_len--;
//			add_cnt++;
//			if (add_cnt >= add_span) {
//				add_cnt = 0;
//				int cnt = add_quent;
//				while (cnt > 0) {
//					cnt--;
//					add();
//				}
//			}
//		}
//	}
	void add() {							//adding incremental --- Surbeuger 
//		if (add_len == 0) {
//			add_tt = 0;
//			add_len = add_duration.get();
//			if (addtyp_rng.get()) add_tt = addtyp_rng_count.get();
//			else if (addtyp_grid.get()) add_tt = addtyp_grid_w.get() * addtyp_grid_h.get();
//			if ((float)add_tt / (float)add_duration.get() > 0) {
//				add_span = 1; add_quent = (int)((float)add_tt / add_duration.get());
//			} else {
//				add_span = (int)((float)add_duration.get() / (float)add_tt); add_quent = 1;
//			}
//		}
		do_add();
	}
	void add_at(float x, float y) {
		if (sset != null) {
			PVector push = new PVector();
			if (add_pushval.get()) push.set(add_pushx.get(), add_pushy.get());
			else if (add_pushin.get()) {
				push.set(add_pos_x.get() - x, add_pos_y.get() - y);
				push.setMag(add_pushspeed.get());
			}
			else if (add_pushout.get()) {
				push.set(x - add_pos_x.get(), y - add_pos_y.get());
				push.setMag(add_pushspeed.get());
			}
			SetObj o = sset.addObject(x, y);
			if (o != null) o.mov.set(push.x, push.y);
			if (o != null) objects.add(o);
		}
	}
	void reset() {
		
	}
	void tick() {
		
	}
	void tick(SetObj o) {
		
	}
	void draw(SetObj o) {
		
	}
	abstract void do_add(); 
}

class MSCreatGrid  extends MSetCreator {
	
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("screagrid", "SetCrtGrid", 
				"Set objects grid creator", "Set Tool"); 
		first_start_show(m); }
		MSCreatGrid build(Macro_Sheet s, sValueBloc b) { MSCreatGrid m = new MSCreatGrid(s, b); return m; }
	}

	public void build_custom_menu(nFrontPanel sheet_front) {
		super.build_custom_menu(sheet_front);
		nFrontTab tab = sheet_front.addTab("Adding");
	    tab.getShelf()
	      .addDrawer(10.25, 0).getShelf()
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_grid_w, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_grid_h, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_grid_space, 100, 10, 1)
	      .addSeparator(0.125)
	      ;
	}
	
	sInt addtyp_grid_w, addtyp_grid_h;
	sFlt addtyp_grid_space;
	
	MSCreatGrid(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "screagrid", _bloc); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		init_access();
		sset = null; 
		objects = new ArrayList<SetObj>();

		addtyp_grid_w = newInt(1, "addtyp_grid_w");
		addtyp_grid_h = newInt(1, "addtyp_grid_h");
		if (!loading_from_bloc) addtyp_grid_w.set_min(1);
		if (!loading_from_bloc) addtyp_grid_h.set_min(1);
		addtyp_grid_space = newFlt(50, "addtyp_grid_space");
	}
	public MSCreatGrid clear() {
		super.clear(); 
		return this; }

	void do_add() {		
		for (int i = 0 ; i < addtyp_grid_w.get() ; i++)
		for (int j = 0 ; j < addtyp_grid_h.get() ; j++) {
			add_at(	add_pos_x.get() + (i * addtyp_grid_space.get()) 
					- ((addtyp_grid_w.get()-1) * addtyp_grid_space.get()) / 2.0F, 
					add_pos_y.get() + (j * addtyp_grid_space.get())
					- ((addtyp_grid_h.get()-1) * addtyp_grid_space.get()) / 2.0F);
		}
	}
}

class MSCreatSpiral  extends MSetCreator {
	
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("screaspir", "SetCrtSpiral", 
				"Set objects spiral creator", "Set Tool"); 
		first_start_show(m); }
		MSCreatSpiral build(Macro_Sheet s, sValueBloc b) { MSCreatSpiral m = new MSCreatSpiral(s, b); return m; }
	}
	
	public void build_custom_menu(nFrontPanel sheet_front) {
		super.build_custom_menu(sheet_front);
		nFrontTab tab = sheet_front.addTab("Adding");
	    tab.getShelf()
	      .addDrawer(10.25, 0).getShelf()
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_count, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(add_count, 1000, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(add_rot_incr, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(add_dist_start, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(add_dist_incr, 2, 10, 1)
	      .addSeparator(0.125)
	      ;
	    
	}
	
	sInt add_count;
	sFlt add_rot_incr, add_dist_start, add_dist_incr;
	
	MSCreatSpiral(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "screaspir", _bloc); 
	}
	public void init() {
		super.init();

		add_count = newInt(10, "add_count");
		if (!loading_from_bloc) add_count.set_min(0);
		add_rot_incr = newFlt(0.4F, "add_rot_incr");
		add_dist_start = newFlt(40, "add_dist_start");
		add_dist_incr = newFlt(5, "add_dist_incr");
	}
	public MSCreatSpiral clear() {
		super.clear(); 
		return this; }

	void do_add() {	
		PVector addpos = new PVector(add_dist_start.get(), 0);
		for (int i = 0 ; i < add_count.get() ; i++) {
			add_at(	add_pos_x.get() + addpos.x, add_pos_y.get() + addpos.y);
			addpos.setMag(addpos.mag() + add_dist_incr.get());
			addpos.rotate(add_rot_incr.get());
		}
	}
}

class MSCreatRng  extends MSetCreator {
	
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("screarng", "SetCrtRandom", 
				"Set objects random creator", "Set Tool"); 
		first_start_show(m); }
		MSCreatRng build(Macro_Sheet s, sValueBloc b) { MSCreatRng m = new MSCreatRng(s, b); return m; }
	}

	public void build_custom_menu(nFrontPanel sheet_front) {
		super.build_custom_menu(sheet_front);
		nFrontTab tab = sheet_front.addTab("Adding");
	    tab.getShelf()
	      .addDrawer(10.25, 0).getShelf()
	      .addDrawerIncrValue(addtyp_rng_zone, 100, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_rng_count, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_rng_count, 1000, 10, 1)
	      .addSeparator(0.125)
	      ;
	}
	
	sInt addtyp_rng_count;//, add_duration;
	sFlt addtyp_rng_zone;
	
	MSCreatRng(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "screarng", _bloc); 
	}
	public void init() {
		super.init();
		addtyp_rng_zone = newFlt(500, "addtyp_rng_zone");
		addtyp_rng_count = newInt(1, "addtyp_rng_count");
	}
	public MSCreatRng clear() {
		super.clear(); 
		return this; }

	void do_add() {		
		for (int i = 0 ; i < addtyp_rng_count.get() ; i++) {
			add_at(	add_pos_x.get() + app.random(-1, 1) * addtyp_rng_zone.get(), 
					add_pos_y.get() + app.random(-1, 1) * addtyp_rng_zone.get());
		}
	}
}