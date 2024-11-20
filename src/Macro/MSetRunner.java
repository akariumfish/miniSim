package Macro;

import Macro.MSet.SetObj;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nGUI;
import sData.*;

public abstract class MSetRunner extends MBaseMenu {

	  public void build_custom_menu(nFrontPanel sheet_front) {
//	    nFrontTab tab = sheet_front.addTab("Control");
//	    tab.getShelf()
//    	  .addDrawerButton(add_run, 10.25F, 1)
//    	  .addSeparator(0.125)
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
  
  	Macro_Connexion link_out;
  	MSubSet sset;
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
		  		if (c.elem.bloc.val_type.get().equals("setcreator")) {
		  			sset = (MSubSet)c.elem.bloc;
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
		if (do_max_age.get()) for (int i = sset.objects.size() - 1 ; i >= 0 ; i--) {
			SetObj o = sset.objects.get(i);
			if (o.age >= max_age.get()) o.clear();
		}
	}
	abstract void init_obj(MSet.SetObj o);
	abstract void tick_obj(MSet.SetObj o);
	void pair_obj(MSet.SetObj o1, MSet.SetObj o2) {}
}
