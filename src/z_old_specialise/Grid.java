package z_old_specialise;

import java.util.ArrayList;
import Macro.Macro_Sheet;
import UI.*;
import processing.core.*;
import sData.*;




public class Grid extends Macro_Sheet {
	public static class GridPrint extends Sheet_Specialize {
		  Simulation sim;
		  public GridPrint(Simulation s) { super("Grid"); sim = s; }
		  public Grid get_new(Macro_Sheet s, String n, sValueBloc b) { return new Grid(sim, b); }
	}
	
	public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Community");
	    tab.getShelf()
	      .addDrawer(10.25, 0.75)
	      .addModel("Label-S4", "-Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(decay, median, 10, 1)
	      .addSeparator(0.125)
	      ;
	      
	    sheet_front.toLayerTop();
	}
	
	Simulation sim;

	nRunnable tick_run, rst_run; Drawable cam_draw;
	ArrayList<Cell> cells;

	sBoo decay, median;
	sInt height, width, cell_size, back_col, add_col;
	sVec adding_pos;
	sFlt adding_diameter;
	sRun add;
	
	sBoo gravity;
	sVec gravity_direction;

	Grid(Simulation m, sValueBloc b) { 
	    super(m.mmain(), "Grid", b);
	    sim = m;

	    decay = newBoo(false, "decay", "decay");
	    median = newBoo(false, "median", "median");
	    width = menuIntIncr(20, 100, "width");
	    height = menuIntIncr(20, 100, "height");
	    cell_size = menuIntIncr(20, 100, "cell_size");
	    back_col = menuIntIncr(20, 100, "back_col");
	    add_col = menuIntIncr(20, 100, "add_col");
	    
//	    gravity = newBoo(false, "gravity", "gravity");
//	    adding_diameter = menuFltSlide(1, 1, 10, "adding_diameter");
//	    adding_pos = newVec("adding_pos", "adding_pos");
//	    gravity_direction = newVec("gravity_direction", "gravity_direction");
//	    add = newRun("add", "add", new nRunnable() { public void run() { ; } });
	    
	    nRunnable reset_run = new nRunnable() { public void run() { reset(); } };
	    width.addEventChange(reset_run);
	    height.addEventChange(reset_run);
	    cell_size.addEventChange(reset_run);
	    back_col.addEventChange(reset_run);
	    
	    tick_run = new nRunnable() { public void run() { tick(); } };
	    cam_draw = new Drawable() { public void drawing() { draw(); } };
	    sim.addEventTick(tick_run);
	    sim.inter.addToCamDrawerPile(cam_draw);
	    
		cells = new ArrayList<Cell>();
		
		grid_init();
	}

	void reset() {
		cells.clear();
		grid_init();
	}
	
	void grid_init() {
		for (int j = 0 ; j < height.get() ; j++) 
	    for (int i = 0 ; i < width.get() ; i++) {
    			Cell c = new Cell();
    			c.x = i; c.y = j; c.w = back_col.get();
    			cells.add(c);
    		}
		for (Cell c1 : cells) for (Cell c2 : cells) {
			float d = (c1.x - c2.x)*(c1.x - c2.x) + (c1.y - c2.y)*(c1.y - c2.y);
			if (d < 2 && c1 != c2) {
				c1.neight.add(c2);
			}
		}
	}
	
	Cell getCell(int x, int y) {
		if (x >= 0 && x < width.get() && y >= 0 && y < height.get() )
			return cells.get(x + y*width.get());
		else return null;
	}

	Cell getHoveredCell() {
		PVector m = new PVector(mmain().cam_gui.mouseVector.x, mmain().cam_gui.mouseVector.y);
		return getCell((int)m.x/cell_size.get(), (int)m.y/cell_size.get());
	}
	
	void tick() {
		if (mmain().inter.input.getState("MouseRight")) {
			Cell c = getHoveredCell();
			if (c != null) {
				c.w = add_col.get();
//				for (Cell n : c.neight) n.w = 255;
			}
		}
		if (median.get()) {
			for (Cell c : cells) c.neightMedian();
			for (Cell c : cells) c.applyMedian();
		}
		if (decay.get()) for (Cell c : cells) c.decay();
	}
	
	void draw() {
		gui.app.stroke(gui.app.color(100));
		gui.app.strokeWeight(1);
		for (Cell c : cells) c.draw();
	}

	
	class Cell {
		int x = 0, y = 0;
		float w = 0, m = 0;
		ArrayList<Cell> neight;
		Cell() {
			neight = new ArrayList<Cell>();
		}
		void draw() {
			gui.app.fill(gui.app.color(w));
			gui.app.rect(x*cell_size.get(), y*cell_size.get(), cell_size.get(), cell_size.get());
		}
		void neightMedian() {
			m = 0;
			int n = 0;
			for (Cell c : neight) {
				m += c.w; n++;
			}
			m /= n;
		}
		void applyMedian() {
			w = m;
		}
		void decay() {
			w -= 1;
			if (w < 0) w = 0;
		}
	}
  	public Grid clear() {
	    super.clear();
	    sim.removeEventTick(tick_run);
	    sim.removeEventReset(rst_run);
	    cam_draw.clear();
	    return this;
  	}
}
