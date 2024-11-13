package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import RApplet.Rapp;
import UI.Drawable;
import UI.nCtrlWidget;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nWidget;
import processing.core.PApplet;
import processing.core.PVector;
import sData.nRunnable;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sValueBloc;
import sData.sVec;

public class MShape extends MBaseMenu { 
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("shape", "Shape", "Basic shapes", "Work"); }
		MShape build(Macro_Sheet s, sValueBloc b) { MShape m = new MShape(s, b); return m; }
	}
	public sVec[] list_vertices;
	sFlt[] list_coords;
	sInt val_type; sCol vcol_fill, vcol_line; sFlt val_scale, val_linew, val_modifier;
	nCtrlWidget[] type_selects;
	Macro_Connexion link;
	ArrayList<MStructure> structs;
	
	MShape(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "shape", _bloc); 
	}
	void init() {
		super.init();
		init_access();
		structs = new ArrayList<MStructure>();
		val_type = newInt(0, "type");
		val_scale = menuFltSlide(10, 1, 50, "val_scale");
		val_linew = menuFltSlide(0.5F, 0, 1.0F, "val_linew");
		vcol_fill = menuColor(gui.app.color(10, 190, 40), "vcol_fill");
		vcol_line = menuColor(gui.app.color(190, 190, 40), "vcol_line");
		val_modifier = newFlt(1, "val_modifier");
		draw_vec = new PVector[max_vertices];
		list_vertices = new sVec[max_vertices];
		list_coords = new sFlt[max_vertices*2];
		for (int i = 0 ; i < max_vertices ; i++) {
			draw_vec[i] = new PVector();
			list_vertices[i] = newVec("vert_"+i);
			list_vertices[i].addEventChange(new nRunnable(i) {public void run() {
				int i = (Integer)builder;
				list_coords[i*2].set(list_vertices[i].x());
				list_coords[(i*2)+1].set(list_vertices[i].y());
				update_info();
			}} );
			list_coords[i*2] = newFlt(0, "coord_"+i+"_x");
			list_coords[i*2].addEventChange(new nRunnable(i) {public void run() {
				list_vertices[(Integer)builder]
						.setx(list_coords[(Integer)builder*2].get());
			}} );
			list_coords[(i*2)+1] = newFlt(0, "coord_"+i+"_y");
			list_coords[(i*2)+1].addEventChange(new nRunnable(i) {public void run() {
				list_vertices[(Integer)builder]
						.sety(list_coords[((Integer)builder*2)+1].get());
			}} );
			if (i == 0) list_vertices[i].setx(10);
			if (i == 1) list_vertices[i].sety(10);
		}
		median = new PVector(0, 0);
		nRunnable val_run = new nRunnable() {public void run() { 
			link_shape_change(); }};
		val_type.addEventChange(val_run);
		val_scale.addEventChange(val_run);
		val_linew.addEventChange(val_run);
		vcol_fill.addEventChange(val_run);
		vcol_line.addEventChange(val_run);
	}
	public MShape clear() {
		super.clear(); 
		structs.clear();
		return this; 
	}
	void build_param() {
		build_normal();
	}
	void build_normal() {
		addInputBang(0, "menu", menu_run);
		addTrigS(1, "menu", menu_run);
		link = addOutput(1,"shape_link");
		link.set_link();
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
		update_info();
		if (sheet_front != null) {
			nDrawer dr = sheet_front.getTab(2).getShelf()
		        .addSeparator(0.125)
		        .addDrawer(10.25, 6.25);
		  
			graph = dr.addModel("Field");
			graph.setPosition(ref_size * 4, ref_size * 2 / 16)
				.setSize(ref_size * 6, ref_size * 6);
			
			g_draw = new Drawable(sheet_front.gui.drawing_pile, 0) { public void drawing() {
			    gui.app.fill(graph.look.standbyColor);
			    gui.app.noStroke();
				gui.app.rect(graph.getX(), graph.getY(), graph.getSX(), graph.getSY());
				PVector cent = new PVector(graph.getX() + graph.getSX()/2, 
						graph.getY() + graph.getSY()/2);
				gui.app.stroke(220);
				gui.app.line(cent.x, cent.y, cent.x + 100, cent.y);
				gui.app.line(cent.x, cent.y, cent.x, cent.y + 100);
				gui.app.stroke(150);
				gui.app.line(cent.x, cent.y, cent.x - 100, cent.y);
				gui.app.line(cent.x, cent.y, cent.x, cent.y - 100);
				add_points = true;
				float f = 1;
				if (val_scale.get() < 12) f = 5;
				if (val_scale.get() < 2) f = 10;
				draw(gui.app, cent, 0, (f / radius) * val_scale.get() * 2.0F, 
						vcol_fill.get(), vcol_line.get(), (1 / radius) * val_linew.get() * 2.0F);
				add_points = false;
			} };
			graph.setDrawable(g_draw);
			
			for (int i = 0 ; i < max_vertices ; i++) {
				point_menu(dr, i*2, list_coords[i*2]);
				point_menu(dr, (i*2)+1, list_coords[(i*2)+1]);
			}
		  
		  	dr.getShelf().addSeparator(0.125).addDrawer(10.25, 1)
		  		.addCtrlModel("Button-S1.5/1-P1", shapes_names[0])
		  			.setRunnable(new nRunnable(this) { public void run() { val_type.set(0); }}).getDrawer()
				.addCtrlModel("Button-S1.5/1-P2", shapes_names[1])
					.setRunnable(new nRunnable(this) { public void run() { val_type.set(1); }}).getDrawer()
				.addCtrlModel("Button-S1.5/1-P3", shapes_names[2])
					.setRunnable(new nRunnable(this) { public void run() { val_type.set(2); }}).getDrawer()
				.addCtrlModel("Button-S1.5/1-P4", shapes_names[3])
					.setRunnable(new nRunnable(this) { public void run() { val_type.set(3); }}).getDrawer()
				.addCtrlModel("Button-S1.5/1-P5", shapes_names[4])
					.setRunnable(new nRunnable(this) { public void run() { val_type.set(4); }}).getShelf()
			.addSeparator(0.125).addDrawer(10.25, 1)
				.addCtrlModel("Button-S2-P1", "Center")
					.setRunnable(new nRunnable(this) { public void run() { center(); }}).getDrawer()
				.addCtrlModel("Button-S2-P2", "Normalize")
					.setRunnable(new nRunnable(this) { public void run() { normalize(); }}).getDrawer()
				.addCtrlModel("Button-S2-P3", "Equilat")
					.setRunnable(new nRunnable(this) { public void run() { equilat(); }}).getShelf()
			.addSeparator(0.125).addDrawer(10.25, 1)
				.addCtrlModel("Button-S1-P1", "X2")
					.setRunnable(new nRunnable(this) { public void run() { resize(2); }}).getDrawer()
				.addCtrlModel("Button-S1-P2", "/2")
					.setRunnable(new nRunnable(this) { public void run() { resize(0.5F); }}).getDrawer()
				.addCtrlModel("Button-S1-P3", "L")
					.setRunnable(new nRunnable(this) { public void run() { move(-10.0F, 0.0F); }}).getDrawer()
				.addCtrlModel("Button-S1-P4", "R")
					.setRunnable(new nRunnable(this) { public void run() { move(10.0F, 0.0F); }}).getDrawer()
				.addCtrlModel("Button-S1-P6", "U")
					.setRunnable(new nRunnable(this) { public void run() { move(0.0F, -10.0F); }}).getDrawer()
				.addCtrlModel("Button-S1-P7", "D")
					.setRunnable(new nRunnable(this) { public void run() { move(0.0F, 10.0F); }}).getDrawer()
				.addCtrlModel("Button-S1-P8", "CCW")
					.setRunnable(new nRunnable(this) { public void run() { rot(-RConst.PI/4.0F); }}).getDrawer()
				.addCtrlModel("Button-S1-P9", "CW")
					.setRunnable(new nRunnable(this) { public void run() { rot(RConst.PI/4.0F); }}).getShelf()
			.addSeparator(0.125);
		  
		  	sheet_front.addEventClose(new nRunnable(this) { public void run() { 
		  		graph = null; g_draw.clear(); g_draw = null; } } );
		  	sheet_front.toLayerTop();
		}
	}
	private void point_menu(nDrawer dr, int n, sFlt v) {
	  dr.addLinkedModel("Field").setLinkedValue(v)
	    .setPosition(ref_size * 18 / 16, (ref_size * 0.8) * (n+(4.0/16)))
	  	.setSize(ref_size * 1.75, ref_size * 0.75);
	  dr.addCtrlModel("Button-S1", "-")
	    .setRunnable(new nRunnable(v) { public void run() { ((sFlt)builder).add(-val_modifier.get()); }})
	  	.setPosition(ref_size * 4 / 16, (ref_size * 0.8) * (n+(4.0/16)))
	  	.setSize(ref_size * 0.75, ref_size * 0.75);
	  dr.addCtrlModel("Button-S1", "+")
	    .setRunnable(new nRunnable(v) { public void run() { ((sFlt)builder).add(val_modifier.get()); }})
	    .setPosition(ref_size * 48 / 16, (ref_size * 0.8) * (n+(4.0/16)))
	  	.setSize(ref_size * 0.75, ref_size * 0.75);
	}
	
	nWidget graph;
	Drawable g_draw;
	PVector[] draw_vec;
	
	PVector temp, median;
	float radius = 0, median_radius;
	void link_shape_change() {
		for (MStructure s : structs) if (s.shape_cible == this) s.get_shape(this);
	}
	void update_info() { 
		median.set(0, 0); radius = 0; median_radius = 0;
		float d = 0; 
		for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
			temp = list_vertices[i].get();
			median.add(temp);
			d = temp.mag();
			median_radius +=d;
			if (radius < d) radius = d;
		}
		median_radius /= shapes_vertices[val_type.get()];
		median.set(	median.x/shapes_vertices[val_type.get()], 
					median.y/shapes_vertices[val_type.get()]);
	}
	void normalize() {
		update_info();
		if (radius > 0) {
			for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
				temp = list_vertices[i].get();
				temp.mult(10/median_radius);
				list_vertices[i].set(temp);
			}
		}
		update_info();
		link_shape_change();
	}
	void center() {
		update_info();
		median.mult(-1);
		for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
			temp = list_vertices[i].get();
			temp.add(median);
			list_vertices[i].set(temp);
		}
		update_info();
		link_shape_change();
	}
	void equilat() {
		update_info();
		temp = new PVector(median_radius, 0);
		for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
			list_vertices[i].set(temp);
			temp.rotate(2*RConst.PI / shapes_vertices[val_type.get()]);
		}
		update_info();
		link_shape_change();
	}
	void resize(float fact) {
		update_info();
		for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
			temp = list_vertices[i].get();
			temp.mult(fact);
			list_vertices[i].set(temp);
		}
		update_info();
		link_shape_change();
	}
	void rot(float fact) {
		update_info();
		for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
			temp = list_vertices[i].get();
			temp.rotate(fact);
			list_vertices[i].set(temp);
		}
		update_info();
	}
	void move(float x, float y) {
		update_info();
		for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
			temp = list_vertices[i].get();
			temp.add(x, y);
			list_vertices[i].set(temp);
		}
		update_info();
		link_shape_change();
	}
	void move_to(int cible) {
		if (cible > 0 && cible <= shapes_vertices[val_type.get()]) {
			update_info();
			temp = list_vertices[cible].get().mult(-1);
			for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) {
				list_vertices[i].set(list_vertices[i].get().add(temp));
			}
			update_info();
			link_shape_change();
		}
	}
	public void draw(Rapp a, PVector pos) { 
		draw(a, pos, 0, val_scale.get(), vcol_fill.get(), vcol_line.get(), val_linew.get()); }
	public void draw(Rapp a, PVector pos, PVector dir) { 
		draw(a, pos, dir.heading(), val_scale.get(), 
				vcol_fill.get(), vcol_line.get(), val_linew.get()); }
	public void draw(Rapp a, PVector pos, float d) { 
		draw(a, pos, d, val_scale.get(), 
				vcol_fill.get(), vcol_line.get(), val_linew.get()); }
	public void draw(Rapp a, PVector pos, float d, float sca) { 
		draw(a, pos, d, sca, 
				vcol_fill.get(), vcol_line.get(), val_linew.get()); }
	private boolean add_points = false;
	public void update_drawvec() {
		
	}
	public void draw(Rapp app, PVector pos, float head, float scale, int fill, int line, float lw) { 	
		for (int i = 0 ; i < max_vertices ; i++) {
			draw_vec[i].set(list_vertices[i].get());
			draw_vec[i] = draw_vec[i].rotate(head).mult(scale).add(pos);
		}
		app.fill(vcol_fill.get());
		app.stroke(vcol_line.get());
		if (val_linew.get() > 0) app.strokeWeight(val_linew.get() * scale); else app.noStroke();
		app.ellipseMode(PApplet.CENTER);
		if (val_type.get() == TRIG)
			app.triangle(	draw_vec[0].x, draw_vec[0].y, 
							draw_vec[1].x, draw_vec[1].y, 
							draw_vec[2].x, draw_vec[2].y);
		else if (val_type.get() == LINE)
			app.line(	draw_vec[0].x, draw_vec[0].y, 
						draw_vec[1].x, draw_vec[1].y);
		else if (val_type.get() == CIRCLE)
			app.ellipse(	draw_vec[0].x, draw_vec[0].y, 
						PApplet.max((draw_vec[1].x - draw_vec[0].x) * 2, 
								(draw_vec[1].y - draw_vec[0].y) * 2) * 2,
						PApplet.max((draw_vec[1].x - draw_vec[0].x) * 2, 
								(draw_vec[1].y - draw_vec[0].y) * 2) * 2);
		else if (val_type.get() == ARROW) {
			app.line(	draw_vec[0].x, draw_vec[0].y, 
						draw_vec[1].x, draw_vec[1].y);
			app.line(	draw_vec[0].x, draw_vec[0].y, 
						draw_vec[2].x, draw_vec[2].y);
		} else if (val_type.get() == RECT) {
			app.rectMode(PApplet.CORNERS);
			app.rect(	draw_vec[0].x, draw_vec[0].y, 
						draw_vec[1].x, draw_vec[1].y);
		}	
		app.rectMode(PApplet.CORNER);
		if (add_points) {
			app.fill(200, 200, 0);
			app.noStroke();
			for (int i = 0 ; i < shapes_vertices[val_type.get()] ; i++) 
				app.ellipse(draw_vec[i].x, draw_vec[i].y, 10, 10);
		}
	}
	public MShape toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
