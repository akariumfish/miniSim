package Macro;

import java.util.ArrayList;

import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import processing.core.PVector;
import sData.nRunnable;
import sData.sValueBloc;

public class MSet extends MBaseMT {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("set", "Set", "Set of objects", "Work"); 
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

    Macro_Connexion link_in;
	ArrayList<MSetModel> models;
    
    MSet(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "set", _bloc); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		
		models = new ArrayList<MSetModel>();
		objects = new ArrayList<SetObj>();
	}
	void build_param() { 
		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	  super.build_normal(); 
	  link_in = addInput(0, "Link_in").set_link();
	  link_in.addEventChangeLink(new nRunnable() { public void run() { 
		  models.clear();
		  for (Macro_Connexion c : link_in.connected_outputs) {
			  if (c.elem.bloc.val_type.get().equals("setmodel")) 
				  models.add((MSetModel)c.elem.bloc);
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
		return this; }
	public MSet toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	
	ArrayList<SetObj> objects;
	
	void tick() {
		
	}
	void reset() {
		
	}
	
	class SetObj {
		
		PVector pos = new PVector(0, 0);
	  	PVector mov = new PVector(0, 0);
	    float radius;//, mass, density;

//	    int age = 0;

//	    float halo_size = 0;
//	    float halo_density = 0;
//	    int halo_col = 0, fill_col = 0, stroke_col = 0;
	    
//	    PVector accel = new PVector(0, 0); //just a holder, reset each tick
//	    PVector prev_pos = new PVector(0, 0);
//	    float squared_speed = 0;
	    
		SetObj() {
			
		}
	}
}
