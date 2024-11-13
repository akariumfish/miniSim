package Macro;

import RApplet.Rapp;
import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nGUI;
import sData.sValueBloc;

public abstract class MSetLaw extends MBaseMenu {

	  public void build_custom_menu(nFrontPanel sheet_front) {
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
  
    MSetLaw(Macro_Sheet _sheet, String _typ, sValueBloc _bloc) { 
		super(_sheet, _typ, _bloc, "setlaw"); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		
		init_access();
		
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	    super.build_normal(); 
		link_out = addOutput(2, "Link_out").set_link();
	}
	void init_end() {  super.init_end();  }
	void rebuild() {  super.rebuild();  }
	public MSetLaw clear() {
		super.clear(); 
		return this; }

	abstract void tick_obj(MSet.SetObj o);
	abstract void pair_obj(MSet.SetObj o1, MSet.SetObj o2);
}
