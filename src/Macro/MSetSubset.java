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

public class MSetSubset extends MBaseMenu {
	
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("subset", "SubSet", 
				"Set objects group", "Set Tool"); 
		first_start_show(m); }
		MSetSubset build(Macro_Sheet s, sValueBloc b) { MSetSubset m = new MSetSubset(s, b); return m; }
	}

	public void build_custom_menu(nFrontPanel sheet_front) {
//		nFrontTab tab = sheet_front.addTab("Tab");
//	    tab.getShelf()
//	      .addSeparator(0.125)
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

	Rapp app;
	sInterface inter;
  	nGUI cam_gui;
  	float ref_size;
  
  	Macro_Connexion link_model,link_runner,link_creator,link_out;
  	MSet set;
  	MSetModel model;
	ArrayList<MSetRunner> runners;
  	MSetCreator creator;
	ArrayList<SetObj> objects;

  	sRun add_run,rst_run,emp_run;

	sBoo do_reset_add, do_reset_empty;
	
	MSetSubset(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "subset", _bloc, "subset"); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		init_access();
		set = null; model = null; creator = null;
		objects = new ArrayList<SetObj>();
		runners = new ArrayList<MSetRunner>();
		
		emp_run = newRun("emp_run", new nRunnable(this) { public void run() { 
			empty(); }});
		rst_run = newRun("rst_run", new nRunnable(this) { public void run() { 
			for (SetObj o : objects) o.reset();
			reset(); }});
		add_run = newRun("add_run", new nRunnable(this) { public void run() { 
			if (creator != null) creator.add(); }});
		globalBin(add_run, rst_run, emp_run, false);
		do_reset_add = newBoo(true, "do_reset_add");
		do_reset_empty = newBoo(true, "do_reset_empty");
		globalBin(do_reset_add, do_reset_empty, false);
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	    super.build_normal(); 
		link_out = addOutput(1, "Link_out").set_link();
		link_out.addEventChangeLink(new nRunnable() { public void run() { 
			MSet prev_set = set;
			set = null;
		  	for (Macro_Connexion c : link_out.connected_inputs) {
		  		if (c.elem.bloc.val_type.get().equals("set"))
		  			set = (MSet)c.elem.bloc;
		  	}
		  	if (set != prev_set) reconnect();
		}});
		link_creator = addInput(0, "Link_Creator").set_link();
		link_creator.addEventChangeLink(new nRunnable() { public void run() { 
			creator = null;
			for (Macro_Connexion c : link_creator.connected_outputs) {
				if (c.elem.bloc.bloc_specialization.equals("screator"))
					creator = (MSetCreator)c.elem.bloc;
			}
		  }});
		link_model = addInput(0, "Link_Model").set_link();
		link_model.addEventChangeLink(new nRunnable() { public void run() { 
			model = null;
			for (Macro_Connexion c : link_model.connected_outputs) {
				if (c.elem.bloc.bloc_specialization.equals("setmodel"))
					model = (MSetModel)c.elem.bloc;
			}
		  }});
		link_runner = addInput(0, "Link_Runner").set_link();
		link_runner.addEventChangeLink(new nRunnable() { public void run() { 
			runners.clear();
			for (Macro_Connexion c : link_runner.connected_outputs) {
				if (c.elem.bloc.bloc_specialization.equals("setrunner"))
					runners.add((MSetRunner)c.elem.bloc);
			}
		  }});
		addEmpty(1); addEmpty(1);
//		addEmpty(2); addEmpty(2); addEmpty(2); addEmpty(2);
	}
	void init_end() {  super.init_end();  }
	void rebuild() {  super.rebuild(); 
		if (rebuilding = true) {
			inter.addEventNextFrame(new nRunnable() { public void run() { 
				((MSetSubset)rebuild_as).reset(); }});
		}
	}
	public MSetSubset clear() {
		super.clear(); 
		empty();
		if (set != null) set.subset.remove(this);
		return this; }

	void reconnect() { empty(); }
	
	SetObj addObject(float x, float y) {
		if (set != null) {
			SetObj o = set.addObject(this);
			o.pos.set(x, y);
			if (model != null) model.init_obj(o);
			for (MSetRunner r : runners) r.init_obj(o);
			set.end_addObject(o);
			return o;
		}
		return null;
	}
	void empty() {
		for (int i = objects.size() - 1 ; i >= 0 ; i--) objects.get(i).clear();
		objects.clear();
	}
	void reset() {
		if (creator != null) creator.reset();
		if (model != null) model.reset();
		for (MSetRunner r : runners) r.reset();
		if (do_reset_empty.get()) empty();
		if (do_reset_add.get()) inter.addEventTwoFrame(add_run.get());
	}
	void tick() {
		
	}
	void tick(SetObj o) { }
	void draw(SetObj o) {
		if (model != null) model.draw_canvas(o);
		if (model != null) model.draw_obj(o);
	}
}
