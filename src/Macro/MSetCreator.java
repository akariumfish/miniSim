package Macro;

import java.util.ArrayList;

import Macro.MSet.SetObj;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.nCursor;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import sData.*;

public class MSetCreator  extends MBaseMenu {
	
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("screator", "SetCreator", 
				"Set objects creator", "Set Tool"); 
		first_start_show(m); }
		MSetCreator build(Macro_Sheet s, sValueBloc b) { MSetCreator m = new MSetCreator(s, b); return m; }
	}

	public void build_custom_menu(nFrontPanel sheet_front) {
		nFrontTab tab = sheet_front.addTab("Adding");
	    tab.getShelf()
	      .addSeparator(0.125)
	      .addDrawerButton(addtyp_grid, addtyp_rng, 10.25F, 1)
	      .addSeparator(0.125)
//	      .addDrawerIncrValue(add_duration, 10, 10, 1)
//	      .addSeparator(0.125)
//	      .addDrawerIncrValue(add_duration, 1000, 10, 1)
//	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_grid_w, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_grid_h, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_grid_space, 100, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_rng_zone, 100, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_rng_count, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(addtyp_rng_count, 1000, 10, 1)
	      .addSeparator(0.125)
	      ;
	    
	    if (sheet_front.collapsed) {
	    		sheet_front.popUp();
	    		sheet_front.collapse();
	    } else {
			sheet_front.collapse();
			sheet_front.popUp();
	    }
	    sheet_front.toLayerTop();
	}

	Rapp app;
	sInterface inter;
  	nGUI cam_gui;
  	float ref_size;
  
  	Macro_Connexion link_out;
  	MSubSet sset;
	ArrayList<SetObj> objects;

  	sRun add_run;

  	nCursor add_ref_cursor;
  	sVec add_pos_vec;
  	sBoo show_add_cursor;
	sFlt add_pos_x, add_pos_y;
	
	sBoo do_reset_add, do_reset_empty, addtyp_grid, addtyp_rng;
	sInt addtyp_grid_w, addtyp_grid_h, addtyp_rng_count;//, add_duration;
	sFlt addtyp_grid_space, addtyp_rng_zone, add_initmovx, add_initmovy;
	
	MSetCreator(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "screator", _bloc, "screator"); 
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

		addtyp_grid = newBoo(true, "addtyp_grid");
		addtyp_grid_w = newInt(1, "addtyp_grid_w");
		addtyp_grid_h = newInt(1, "addtyp_grid_h");
		if (!loading_from_bloc) addtyp_grid_w.set_min(1);
		if (!loading_from_bloc) addtyp_grid_h.set_min(1);
		addtyp_grid_space = newFlt(50, "addtyp_grid_space");
		addtyp_rng = newBoo(false, "addtyp_rng");
		addtyp_rng_zone = newFlt(500, "addtyp_rng_zone");
		addtyp_rng_count = newInt(1, "addtyp_rng_count");
//		add_duration = newInt(100, "add_duration");

//		menuIntIncr(addtyp_rng_count, 1000);
//		menuIntIncr(addtyp_rng_count, 10);
//	    menuFltIncr(addtyp_rng_zone, 100);
//	    menuFltIncr(addtyp_grid_space, 100);
//		menuIntIncr(addtyp_grid_h, 10);
//		menuIntIncr(addtyp_grid_w, 10);
//	    menuBoo(addtyp_grid, addtyp_rng);
//		menuIntIncr(add_duration, 1000);
//		menuIntIncr(add_duration, 10);

		add_initmovx = newFlt(0, "add_imovx");
		add_initmovy = newFlt(0, "add_imovy");
	    add_pos_vec = newVec("val_pos");
	    show_add_cursor = newBoo(true, "show_add_cursor");
	    add_pos_x = newFlt(0, "add_pos_x");
	    add_pos_y = newFlt(0, "add_pos_y");
	    globalFltIncr(add_initmovx, 0.1F, false);
	    globalFltIncr(add_initmovy, 0.1F, false);
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
		do_reset_add = newBoo(false, "do_reset_add");
		do_reset_empty = newBoo(false, "do_reset_empty");
		globalBin(do_reset_add, do_reset_empty, false);
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
		link_out = addOutput(2, "Link_out").set_link();
		link_out.addEventChangeLink(new nRunnable() { public void run() { 
			MSubSet prev_set = sset;
			sset = null;
		  	for (Macro_Connexion c : link_out.connected_inputs) {
		  		if (c.elem.bloc.val_type.get().equals("subset"))
		  			sset = (MSubSet)c.elem.bloc;
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
		if (addtyp_rng.get()) {
			for (int i = 0 ; i < addtyp_rng_count.get() ; i++) {
				add_at(	add_pos_x.get() + app.random(-1, 1) * addtyp_rng_zone.get(), 
						add_pos_y.get() + app.random(-1, 1) * addtyp_rng_zone.get());
			}
		} else if (addtyp_grid.get()) {
			for (int i = 0 ; i < addtyp_grid_w.get() ; i++)
			for (int j = 0 ; j < addtyp_grid_h.get() ; j++) {
				add_at(	add_pos_x.get() + (i * addtyp_grid_space.get()) 
						- ((addtyp_grid_w.get()-1) * addtyp_grid_space.get()) / 2.0F, 
						add_pos_y.get() + (j * addtyp_grid_space.get())
						- ((addtyp_grid_h.get()-1) * addtyp_grid_space.get()) / 2.0F);
			}
		} else {
			add_at(add_pos_x.get(), add_pos_y.get());
		}
	}
	void add_at(float x, float y) {
		if (sset != null) {
			SetObj o = sset.addObject(x, y);
			if (o != null) o.mov.set(add_initmovx.get(), add_initmovy.get());
			if (o != null) objects.add(o);
		}
	}
	void empty() {
		for (int i = objects.size() - 1 ; i >= 0 ; i--) objects.get(i).clear();
		objects.clear();
	}
	void reset() {
		if (do_reset_empty.get()) empty();
		if (do_reset_add.get()) inter.addEventTwoFrame(add_run.get());
	}
	//debug
//	void draw() {
//		app.stroke(app.color(255,0,0));
////		if (add_ref_cursor != null && add_ref_cursor.pval != null) 
////			app.drawcross(add_ref_cursor.pval.x(), add_ref_cursor.pval.y(), 30);
////		app.drawcross(add_pos_vec.x(), add_pos_vec.y(), 30);
//		app.drawcross(add_pos_x.get(), add_pos_y.get(), 30);
//	}
	void tick() {
		
	}
	void tick(SetObj o) {
		
	}
	void draw(SetObj o) {
		
	}
}