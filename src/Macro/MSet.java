package Macro;

import java.util.ArrayList;

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
	ArrayList<MSubSet> creators;
	ArrayList<MSetLaw> laws;
	ArrayList<SetObj> objects;
	
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

		creators = new ArrayList<MSubSet>();
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
		  creators.clear();
		  for (Macro_Connexion c : link_subset.connected_outputs) {
			  if (c.elem.bloc.bloc_specialization.equals("subset"))
				  creators.add((MSubSet)c.elem.bloc);
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
	
	SetObj addObject(MSubSet m) {
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
		for (MSubSet c : creators) c.reset();
	}
	void tick() {
		if (do_ticks.get()) {
			for (MSubSet c : creators) c.tick();
			for (SetObj o : objects) o.subset.tick(o);
			for (MSetLaw c : laws) for (SetObj o : objects) c.tick_obj(o);
			for (int i = 0 ; i < objects.size() ; i++)
				for (int j = i+1 ; j < objects.size() ; j++) 
					for (MSetLaw c : laws) { 
						c.pair_obj(objects.get(i), objects.get(j));
					}
			for (SetObj o : objects) o.tick();
		}
	}
	void draw_Cam() {
		if (do_draws.get()) {
			for (SetObj o : objects) o.subset.draw(o);
		}
		//debug
//		for 	(MSetCreator c : creators) c.draw();
	}
	
	class SetObj {
		int id = 0;
	    int age = 0;

		PVector pos = new PVector(0, 0);
	  	PVector mov = new PVector(0, 0);
	  	PVector accel = new PVector(0, 0); 
	    float radius, mass, density;

	    float halo_size = 0;
	    float halo_density = 0;
	    int halo_col = 0;

	    // 			inside data :
		PVector pos_strt = new PVector(0, 0);
		PVector pos_prev = new PVector(0, 0);
		PVector pos_chng = new PVector(0, 0);
	  	PVector mov_strt = new PVector(0, 0);
		PVector mov_prev = new PVector(0, 0);
		PVector mov_chng = new PVector(0, 0);
	    float radius_strt, mass_strt, density_strt;
	    
	    MSubSet subset;
	    
		SetObj(MSubSet r) {
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

			pos_chng.set(pos.x - pos_prev.x, pos.y - pos_prev.y);
			pos_prev.set(pos);
			mov_chng.set(mov.x - mov_prev.x, mov.y - mov_prev.y);
			mov_prev.set(mov);
		}
		
		void draw_halo(MCanvas canvas) {
			if (canvas != null) canvas.draw_halo(pos, halo_size, halo_density, halo_col);
		}
	}
}
