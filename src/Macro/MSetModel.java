package Macro;

import java.util.ArrayList;

import Macro.MSet.SetObj;
import RApplet.RConst;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nGUI;
import processing.core.PVector;
import sData.*;

public abstract class MSetModel extends MBaseMenu {

	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Halo");
	    tab.getShelf()
    	  	  .addSeparator(0.125)
      	  .addDrawerButton(val_do_halo, 10.25F, 1)
      	  .addSeparator(0.125)
      	  .addDrawerColor(val_halo_col_int, 10.25F, 1, inter.taskpanel)
      	  .addSeparator(0.125)
      	  .addDrawerColor(val_halo_col_ext_min, 10.25F, 1, inter.taskpanel)
      	  .addSeparator(0.125)
      	  .addDrawerColor(val_halo_col_ext_max, 10.25F, 1, inter.taskpanel)
      	  .addSeparator(0.125)
	      .addDrawerFltSlide(val_halo_intfct)
	      .addSeparator(0.125)
	      .addDrawerFactValue(val_halo_speedmin, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(val_halo_speedmax, 2, 10, 1)
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
	      
	  }

	Rapp app;
    sInterface inter;
    nGUI cam_gui;
    float ref_size;
    
    Macro_Connexion link_out;
    sBoo val_do_halo;
    sFlt val_radius, val_mass, val_density;
    sFlt val_halo_size, val_halo_dens, val_halo_intfct, val_halo_speedmin, val_halo_speedmax;
    sCol val_halo_col_int, val_halo_col_ext_min, val_halo_col_ext_max;
    
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

		val_do_halo = newBoo(true, "val_do_halo");
		val_halo_size = newFlt(20, "val_halo_size");
		if (!loading_from_bloc) val_halo_size.set_limit(2, 400);
		val_halo_dens = newFlt(1.2F, "val_halo_dens");
		if (!loading_from_bloc) val_halo_dens.set_limit(0, 5);
		val_halo_col_int = newCol(app.color(255,120,0),"val_halo_col_int");
		val_halo_col_ext_min = newCol(app.color(255,120,0),"halocol_ext_min");
		val_halo_col_ext_max = newCol(app.color(255,120,0),"halocol_ext_max");
		
		val_halo_intfct = newFlt(0.5F, "val_halo_intfct");
		if (!loading_from_bloc) val_halo_intfct.set_limit(0, 1);
		val_halo_speedmin = newFlt(0.001F, "val_halo_speedmin");
		val_halo_speedmax = newFlt(2, "val_halo_speedmax");
		
		val_radius = menuFltSlide(20, 5, 100, "val_radius");
		menuFltFact(val_radius, 2);
		val_mass = menuFltSlide(20, 0, 100, "val_mass");
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
		o.do_halo = val_do_halo.get();
		o.halo_size = val_halo_size.get();
		o.halo_density = val_halo_dens.get();
		o.halo_col_int = val_halo_col_int.get();
		o.halo_col_ext_min = val_halo_col_ext_min.get();
		o.halo_col_ext_max = val_halo_col_ext_max.get();
		o.halo_col_intfct = val_halo_intfct.get();
		o.halo_col_speedmin = val_halo_speedmin.get();
		o.halo_col_speedmax = val_halo_speedmax.get();
	}
	abstract void draw_obj(MSet.SetObj o);
}


class MSModArrow extends MSetModel {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("smodarrow", "SMod_Arrow", 
				"Model for Set objects - Arrow", "Set Tool"); 
		first_start_show(m); }
		MSModArrow build(Macro_Sheet s, sValueBloc b) { MSModArrow m = new MSModArrow(s, b); return m; }
	}
	
	
	sFlt val_strokeweight;
	sBoo val_doradius, val_doarrow;
	
	MSModArrow(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "smodarrow", _bloc); 
	}
	public void init() {
		super.init();
		val_strokeweight = menuFltSlide(2.0F, 0.5F, 10.0F, "val_strokeweight");
		val_doradius = newBoo(true, "val_doradius");
		val_doarrow = newBoo(true, "val_doarrow");
		menuBoo(val_doradius, val_doarrow);
	}

	void draw_obj(MSet.SetObj o) {
		app.noFill();
		app.stroke(255);
		app.strokeWeight(val_strokeweight.get());
		if (val_doradius.get()) 
			app.ellipse(o.pos.x - o.radius, o.pos.y - o.radius, 
				o.radius * 2, o.radius * 2);
		if (val_doarrow.get()) {
			PVector depl = new PVector(o.pos_chng.x, o.pos_chng.y).setMag(o.radius);
			app.line(o.pos.x - depl.x, o.pos.y - depl.y, o.pos.x + depl.x, o.pos.y + depl.y);
			PVector arrow_end = new PVector(depl.x, depl.y);
			arrow_end.rotate(-RConst.PI / 8.0F);
			app.line(o.pos.x + depl.x, o.pos.y + depl.y, 
					o.pos.x + depl.x - arrow_end.x, o.pos.y + depl.y - arrow_end.y);
			arrow_end.rotate(RConst.PI / 4.0F);
			app.line(o.pos.x + depl.x, o.pos.y + depl.y, 
					o.pos.x + depl.x - arrow_end.x, o.pos.y + depl.y - arrow_end.y);
		}
	}
}


class MSModBase extends MSetModel {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("smodbase", "SMod_Basic", 
				"Model for Set objects - Basic", "Set Tool"); 
		first_start_show(m); }
		MSModBase build(Macro_Sheet s, sValueBloc b) { MSModBase m = new MSModBase(s, b); return m; }
	}
	
	sFlt val_radius, val_strokeweight;
	sBoo val_dofill, val_dostroke;
	
	MSModBase(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "smodbase", _bloc); 
	}
	public void init() {
		super.init();
		val_strokeweight = menuFltSlide(2.0F, 0.5F, 10.0F, "val_strokeweight");
		val_dofill = newBoo(false, "val_dofill");
		val_dostroke = newBoo(true, "val_dostroke");
		menuBoo(val_dofill, val_dostroke);
	}

	void draw_obj(MSet.SetObj o) {
		if (val_dofill.get()) app.fill(255);
		else app.noFill();
		if (val_dostroke.get()) {
			app.stroke(255);
			app.strokeWeight(val_strokeweight.get());
		}
		else app.noStroke();
		app.ellipse(o.pos.x - o.radius, o.pos.y - o.radius, 
				o.radius * 2, o.radius * 2);
	}
}