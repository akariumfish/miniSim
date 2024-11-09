package Macro;

import java.util.ArrayList;

import UI.Drawable;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nWidget;
import processing.core.PVector;
import sData.*;

public class MCurve extends MBaseMT {
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("curve", "Curve", "Change output along a curve", "Work"); 
		first_start_show(m); }
		MCurve build(Macro_Sheet s, sValueBloc b) { MCurve m = new MCurve(s, b); return m; }
	}

	nWidget graph;
	Drawable g_draw;
	
	sInt sec_number, sec_selected, total_time;
	sFlt max_value;
	
	ArrayList<CurveSection> sections;

	MCurve(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "curve", _bloc); 
	}
	
	void init() {
		super.init();
		sec_number = newInt(1, "sec_number");
		if (!loading_from_bloc) sec_number.set_limit(1, 6);
		sec_selected = newInt(1, "sec_selected");
		total_time = newInt(1, "total_time");
		max_value = newFlt(0, "max_value");
		
		sections = new ArrayList<CurveSection>();

		for (int i = 0 ; i < sec_number.get() ; i++) {
			CurveSection cs = new CurveSection(this, i);
			sections.add(cs);
		}
		calc_bound();
	}
	
	void calc_bound() {
		total_time.set(0);
		max_value.set(0);
		for (CurveSection cs : sections) {
			total_time.add(cs.val_length.get());
			if (cs.val_cible.get() > max_value.get()) 
				max_value.set(cs.val_cible.get());
		}
	}
	
	void init_end() {
		super.init_end();
	  	nRunnable rebuild_run = new nRunnable() { public void run() {
  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
  				rebuild(); }}); }};
  		nRunnable bound_run = new nRunnable() { public void run() {
  			calc_bound(); }};
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  		sec_number.addEventChange(rebuild_run);
	  		
	  		for (CurveSection cs : sections) {
	  			cs.val_cible.addEventChange(bound_run);
	  			cs.val_length.addEventChange(bound_run);
	  		}
	  	}});
  	}
	void build_param() { 
		super.build_param(); 
		addSelectToInt(0, sec_number);
	}
	void build_normal() {
		super.build_normal(); 
		addSelectToInt(0, sec_number);
	}
	
	public MCurve clear() {
		super.clear(); 
		return this; 
	}
	public MCurve toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {
			nDrawer dr = sheet_front.getTab(2).getShelf()
		        .addDrawer(10.25, 5.25);
		  
			graph = dr.addModel("Field");
			graph.setPosition(ref_size * 2 / 16, ref_size * 2 / 16)
				.setSize(ref_size * (10.0 - 4.0/16.0), ref_size * 5);
			
			g_draw = new Drawable(sheet_front.gui.drawing_pile, 0) { public void drawing() {
				gui.app.fill(graph.look.standbyColor);
			    gui.app.noStroke();
				gui.app.rect(graph.getX(), graph.getY(), graph.getSX(), graph.getSY());
				PVector ref = new PVector(graph.getX() + ref_size * 2 / 16, 
						graph.getY() + graph.getSY()
						- ref_size * 2 / 16);
				float h = graph.getSY() - ref_size * 4.0F / 16;
				float w = graph.getSX() - ref_size * 4.0F / 16;
				gui.app.stroke(220);
				gui.app.line(ref.x, ref.y, ref.x + w, ref.y);
				gui.app.line(ref.x, ref.y, ref.x, ref.y - h);
				
				for (int i = 0 ; i < sec_number.get() ; i++) {
					CurveSection cs = sections.get(i);
					
				}
			} };
			graph.setDrawable(g_draw);
			
			dr.getShelf().addDrawer(10.25, 1.0)
				
				;
			
		  	sheet_front.addEventClose(new nRunnable(this) { public void run() { 
		  		graph = null; g_draw.clear(); g_draw = null; } } );
		  	sheet_front.toLayerTop();
		}
	}

	void reset() {
		
	}
	
	void tick() {
		
	}

	class CurveSection {
		MCurve curv;
		int sec_num;
		sFlt val_cible;
		sInt val_length;
		
		CurveSection(MCurve _c, int _sec_num) {
			curv = _c;
			sec_num = _sec_num;
			val_cible = curv.newFlt(1.0F, "curv_sec_cible"+sec_num);
			val_length = curv.newInt(10, "curv_sec_length"+sec_num);
		}
	}
}
