package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import RApplet.sInterface;
import UI.Drawable;
import UI.nFrontPanel;
import UI.nGUI;
import processing.core.PVector;
import sData.*;

public class MSet extends MBaseMT {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("set", "Set", "Set of objects", "Set Tool"); 
		first_start_show(m); }
		MSet build(Macro_Sheet s, sValueBloc b) { MSet m = new MSet(s, b); return m; }
	}

	  public void build_custom_menu(nFrontPanel sheet_front) {
//	    nFrontTab tab = sheet_front.addTab("Community");
//	    tab.getShelf()
//	      ;
	      
	    if (sheet_front.collapsed) {
	    		sheet_front.popUp();
	    		sheet_front.collapse();
	    } else {
			sheet_front.collapse();
			sheet_front.popUp();
	    }
	    sheet_front.toLayerTop();
	  }

    sInterface inter;
    nGUI cam_gui;
    float ref_size;
    Drawable drawable;

    Macro_Connexion link_subset, link_law, link_canvas;
	ArrayList<MSetSubset> subset;
	ArrayList<MSetLaw> laws;
	ArrayList<SetObj> objects;
	MCanvas canvas;
	
	sInt objects_count;
	
	sBoo do_ticks, do_draws;
    
    MSet(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "set", _bloc); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;

		subset = new ArrayList<MSetSubset>();
		laws = new ArrayList<MSetLaw>();
		objects = new ArrayList<SetObj>();
		
		drawable = new Drawable() { public void drawing() { draw_Cam(); } } ;
		mmain().inter.addToCamDrawerPile(drawable);
		drawable.setLayer(priority.get());
		priority.addEventChange(new nRunnable() { public void run() { 
			drawable.setLayer(priority.get()); }});
		
	    inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );

	    objects_count = newInt(0, "objects_count");
	    objects_count.set(0);
	    menuWatch(objects_count);

	    do_ticks = newBoo(true, "do_ticks");
	    do_draws = newBoo(true, "do_draws");
	    globalBin(do_ticks, do_draws, true);
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	  super.build_normal(); 
	  link_subset = addInput(0, "link_subset").set_link();
	  link_subset.addEventChangeLink(new nRunnable() { public void run() { 
		  subset.clear();
		  for (Macro_Connexion c : link_subset.connected_outputs) {
			  if (c.elem.bloc.bloc_specialization.equals("subset"))
				  subset.add((MSetSubset)c.elem.bloc);
		  }
	  }});
	  link_law = addInput(0, "link_law").set_link();
	  link_law.addEventChangeLink(new nRunnable() { public void run() { 
		  laws.clear();
		  for (Macro_Connexion c : link_law.connected_outputs) {
			  if (c.elem.bloc.bloc_specialization.equals("setlaw"))
				  { laws.add((MSetLaw)c.elem.bloc); }
		  }
	  }});
	  link_canvas = addOutput(1, "link_canvas").set_link();
	  link_canvas.addEventChangeLink(new nRunnable() { public void run() { 
		  canvas = null;
		  for (Macro_Connexion c : link_law.connected_outputs) {
			  if (c.elem.bloc.val_type.get().equals("mcanvas"))
				  canvas = (MCanvas)c.elem.bloc;
		  }
	  }});
	}
	void init_end() { 
		super.init_end(); 
	}
	void rebuild() { 
		super.rebuild(); 
	}
	public MSet clear() {
		super.clear(); 
		for (int i = objects.size() - 1 ; i >= 0 ; i--) objects.get(i).clear();
		objects.clear();
		return this; }
	public MSet toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	
	SetObj addObject(MSetSubset m) {
		SetObj o = new SetObj(m);
		return o;
	}
	void end_addObject(SetObj o) { o.end_init(); }
	
	void frame() { }

	void empty() {
		for (int i = objects.size() - 1 ; i >= 0 ; i--) objects.get(i).clear();
	}
	void reset() {
		for (SetObj o : objects) o.reset();
		for (MSetSubset c : subset) c.reset();
	}
	void tick() {
		if (do_ticks.get()) {
			for (MSetSubset c : subset) c.tick();
			for (MSetLaw c : laws) for (SetObj o : objects) c.tick_obj(o);
			for (int i = 0 ; i < objects.size() ; i++)
				for (int j = i+1 ; j < objects.size() ; j++) 
					for (MSetLaw c : laws) { 
						c.pair_obj(objects.get(i), objects.get(j));
					}
			for (SetObj o : objects) o.tick();
		}
	}
	void tick_strt() {
		if (do_ticks.get()) {
			for (SetObj o : objects) o.tick_strt();
		}
	}
	void draw_Cam() {
		if (do_draws.get()) {
			for (SetObj o : objects) o.subset.draw(o);
		}
	}
	
	class SetObj {
		int id = 0;
	    int age = 0;

		PVector pos = new PVector(0, 0);
	  	PVector mov = new PVector(0, 0);
	  	PVector accel = new PVector(0, 0); 
	    float radius, mass, density;

	    boolean do_halo = false;
	    float halo_size = 0;
	    float halo_density = 0;
	    int halo_col_int = 0, halo_col_ext_min = 0, halo_col_ext_max = 0; 
	    float halo_col_intfct = 0, halo_col_speedmin = 0, halo_col_speedmax = 0;

	    // 			inside data :
		PVector pos_strt = new PVector(0, 0);
		PVector pos_prev = new PVector(0, 0);
		PVector pos_chng = new PVector(0, 0);
	  	PVector mov_strt = new PVector(0, 0);
		PVector mov_prev = new PVector(0, 0);
		PVector mov_chng = new PVector(0, 0);
	    float radius_strt, mass_strt, density_strt;
	    //neighbours (for boids)
		PVector neigh_posavg = new PVector(0, 0);
		PVector neigh_movavg = new PVector(0, 0);
		PVector avoid_posavg = new PVector(0, 0);
		int neigh_number = 0, avoid_number;
	    
	    MSetSubset subset;
	    
		SetObj(MSetSubset r) {
			subset = r;
			objects.add(this);
			r.objects.add(this);
			objects_count.add(1);
		}
		void end_init() {
			id = 0;
			boolean found = true;
			while (found) {
				found = false;
				for (SetObj o : objects) if (o != this && o.id == id) { 
					found = true; id++; } }
			age = 0;
			pos_strt.set(pos);
			mov_strt.set(mov);
			radius_strt = radius;
			mass_strt = mass;
			density_strt = density;
		}
		void clear() {
			objects.remove(this);
			subset.objects.remove(this);
			objects_count.add(-1);
		}
		void reset() {
			age = 0;
			pos.set(pos_strt);
			mov.set(mov_strt);
			radius = radius_strt;
			mass = mass_strt;
			density = density_strt;
		}
		void tick() {
			age++;
			mov.add(accel);
			pos.add(mov);
			accel.set(0, 0);
		}
		void tick_strt() {
			pos_chng.set(pos.x - pos_prev.x, pos.y - pos_prev.y);
			pos_prev.set(pos);
			mov_chng.set(mov.x - mov_prev.x, mov.y - mov_prev.y);
			mov_prev.set(mov);
		}
		
		void draw_halo(MCanvas canvas) {
			if (do_halo && canvas != null) {
				float speed = pos_chng.mag();
				float spfact = 
						(speed - halo_col_speedmin) / (halo_col_speedmax - halo_col_speedmin);
				spfact = 1 - RConst.limit(spfact, 0, 1);
				int ce = RConst.merge_color(halo_col_ext_min, halo_col_ext_max, 
								spfact, gui.app);
				if (pos.x != pos_prev.x || pos.y != pos_prev.y)
					canvas.draw_line_halo(pos, pos_prev, halo_size, halo_density, 
						halo_col_int, ce, halo_col_intfct);
				else canvas.draw_halo(pos, halo_size, halo_density, 
						halo_col_int, ce, halo_col_intfct);
			}
		}
	}
}
