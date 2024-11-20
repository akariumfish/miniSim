package Macro;

import java.util.ArrayList;

import Macro.MSet.SetObj;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import sData.*;

public abstract class MSetModel extends MBaseMenu {

	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Halo");
	    tab.getShelf()
    	  	  .addSeparator(0.125)
      	  .addDrawerColor(val_halo_col, 10.25F, 1, inter.taskpanel)
      	  .addSeparator(0.125)
	      .addDrawerFltSlide(val_halo_size)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(val_halo_size, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFltSlide(val_halo_dens)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(val_halo_dens, 1, 10, 1)
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
    sFlt val_radius, val_mass, val_density;
    sFlt val_halo_size, val_halo_dens;
    sCol val_halo_col;
    
    MSetModel(Macro_Sheet _sheet, String _typ, sValueBloc _bloc) { 
		super(_sheet, _typ, _bloc, "setmodel"); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		cam_gui = inter.cam_gui;
		ref_size = inter.ref_size;
		init_access();

		val_halo_size = newFlt(20, "val_halo_size");
		if (!loading_from_bloc) val_halo_size.set_limit(2, 100);
		val_halo_dens = newFlt(1.2F, "val_halo_dens");
		if (!loading_from_bloc) val_halo_dens.set_limit(0, 5);
		val_halo_col = newCol(app.color(255,120,0),"val_halo_col");

		val_radius = menuFltSlide(20, 5, 100, "val_radius");
		menuFltFact(val_radius, 2);
		val_mass = menuFltSlide(20, -100, 100, "val_mass");
		menuFltFact(val_mass, 2);
		val_density = menuFltSlide(1, 0, 100, "val_density");
		menuFltFact(val_density, 2);
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
	public MSetModel clear() {
		super.clear(); 
		return this; }

	void reset() { ; }
	void init_obj(MSet.SetObj o) {
		o.radius = val_radius.get();
		o.mass = val_mass.get();
		o.density = val_density.get();
	}
	void draw_canvas(MSet.SetObj o) {
		o.halo_size = val_halo_size.get();
		o.halo_density = val_halo_dens.get();
		o.halo_col = val_halo_col.get();
	}
	abstract void draw_obj(MSet.SetObj o);
}
