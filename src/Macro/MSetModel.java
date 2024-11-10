package Macro;

import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import sData.nRunnable;
import sData.sValueBloc;

public class MSetModel extends MBaseMT {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("setmodel", "SetModel", "Model for Set objects", "Work"); 
		first_start_show(m); }
		MSetModel build(Macro_Sheet s, sValueBloc b) { MSetModel m = new MSetModel(s, b); return m; }
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
    
    Macro_Connexion link_out;
    MSet set;
    
    MSetModel(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "setmodel", _bloc); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		
		init_access();
		
		init_object();
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	    super.build_normal(); 
		link_out = addOutput(1, "Link_out").set_link();
		link_out.addEventChangeLink(new nRunnable() { public void run() { 
			set = null;
		  	for (Macro_Connexion c : link_out.connected_inputs) {
		  		if (c.elem.bloc.val_type.get().equals("set")) 
		  			set = (MSet)c.elem.bloc;
		  	}
		}});
	}
	void init_end() { 
		super.init_end(); 
	}
	void rebuild() { 
		super.rebuild(); 
	}
	public MSetModel clear() {
		super.clear(); 
		return this; }
	public MSetModel toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	
	
	
	void init_object() {
		
	}
	void tick() {
		
	}
	void reset() {
		
	}
}
