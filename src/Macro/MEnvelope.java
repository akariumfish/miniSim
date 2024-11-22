package Macro;

import java.util.ArrayList;

import RApplet.Rapp;
import UI.Drawable;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nWidget;
import processing.core.PVector;
import sData.*;

public class MEnvelope extends MBaseMT {
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("envelope", "Envelope", "Change output along a curve", "Work"); 
		first_start_show(m); }
		MEnvelope build(Macro_Sheet s, sValueBloc b) { MEnvelope m = new MEnvelope(s, b); return m; }
	}
	
	class CurveSection {
		MEnvelope curv;
		CurveSection prev_sec;
		int sec_num;
		sFlt tot_length;
		sFlt val_cible;
		sInt val_length;
		sVec graph_pos;
		
		CurveSection(MEnvelope _c, CurveSection _p, int _sec_num) {
			curv = _c;
			sec_num = _sec_num;
			prev_sec = _p;
			graph_pos = curv.newVec("graph_pos"+sec_num);
			val_cible = curv.newFlt(0.0F, "curv_sec_cible"+sec_num);
			val_length = curv.newInt(0, "curv_sec_length"+sec_num);
			tot_length = curv.newFlt(0, "tot_length"+sec_num);
		}
		void get_pval(PVector p) {
			graph_pos.set(p.x * graph.getSX(), p.y * graph.getSY());
			val_cible.set(p.y * max_value.get());
			if (prev_sec == null) val_length.set(p.y * total_time.get());
			else val_length.set(
					(p.y - prev_sec.graph_pos.x() / graph.getSX()) * total_time.get());
			tot_length.set(p.y * total_time.get());
		}
	}
	
	boolean running = false;
	int run_counter = 0;

	void do_start() {
		running = true;
		run_counter = 0;
	}

	void reset() {
		do_stop();
	}
	
	void tick() {
		if (running) {
			run_counter++;
			int time_cnt = 0;
			CurveSection current_sec = null;
			for (CurveSection cs : sections) {
				time_cnt += cs.val_length.get();
				current_sec = cs;
				if (time_cnt >= run_counter) break;
			}
			int curv_counter = run_counter;
			if (current_sec.prev_sec != null) 
					curv_counter = (int) (run_counter - current_sec.prev_sec.tot_length.get());
			float curv_fact = curv_counter / current_sec.val_length.get();
			float prev_cible = end_value.get();
			if (current_sec.prev_sec != null) 
					prev_cible = current_sec.prev_sec.val_cible.get();
			float curr_value = prev_cible + curv_fact * 
					( current_sec.val_cible.get() - prev_cible );
			current_value.set( curr_value );
			if (run_counter == total_time.get()) { do_stop(); }
		}
	}
	
	void do_stop() {
		running = false;
	}
	
	Rapp app;

	sInt sec_number, sec_selected, total_time;
	sFlt current_value, max_value, end_value;
	
	sRun run_start, run_reset, run_stop;
	Macro_Connexion co_val_out;
	
	MEnvelope(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "envelope", _bloc); 
	}
	
	void init() {
		super.init();
		app = gui.app;
		sec_number = newInt(1, "sec_number");
		if (!loading_from_bloc) sec_number.set_limit(1, 6);
		sec_selected = newInt(1, "sec_selected");
		total_time = newInt(1, "total_time");
		max_value = newFlt(0, "max_value");
		end_value = newFlt(0, "end_value");
		current_value = newFlt(0, "current_value");
		current_value.set(end_value.get());
		current_value.addEventChange(new nRunnable() { public void run() { 
			co_val_out.sendFloat(current_value.get()); }});
		
		sections = new ArrayList<CurveSection>();
		curs = new ArrayList<nWidget>();
		pval = new ArrayList<PVector>();
		
		run_start = newRun("run_start", 
				new nRunnable() { public void run() { do_start(); }});
		run_reset = newRun("run_reset", 
				new nRunnable() { public void run() { if (running) do_start(); }});
		run_stop = newRun("run_stop", 
				new nRunnable() { public void run() { if (running) do_stop(); }});
		
		CurveSection prev_cs = null;
		for (int i = 0 ; i < sec_number.get() + 1 ; i++) {
			CurveSection cs = new CurveSection(this, prev_cs, i); 
			prev_cs = cs;
			sections.add(cs);
		}
	}
	
	void init_end() {
		super.init_end();
	  	nRunnable rebuild_run = new nRunnable() { public void run() {
  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
  				rebuild(); }}); }};
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  		sec_number.addEventChange(rebuild_run);
	  		for (CurveSection cs : sections) {
	  			cs.val_cible.addEventChange(run_reset.get());
	  			cs.val_length.addEventChange(run_reset.get());
	  		}
	  	}});
  	}
	void build_param() { 
		super.build_param(); 
		co_val_out = addOutput(2, "co_val_out");
		newRowValue(run_start);
		preview = addEmpty(0).addModel("ref");
		p_draw = new Drawable(gui.drawing_pile, 0) { public void drawing() {
			float h = ref_size * 2.0F;
			float w = ref_size * 6.0F;
			PVector ref = new PVector(preview.getX() + ref_size * 6 / 16, preview.getY() + h);
			gui.app.stroke(220);
			gui.app.strokeWeight(3);
			gui.app.line(ref.x, ref.y, ref.x + w, ref.y);
			gui.app.line(ref.x, ref.y, ref.x, ref.y - h);

			gui.app.strokeWeight(2);
			float run_fact = (float)run_counter / total_time.get();
			gui.app.line(ref.x + run_fact * w, ref.y, ref.x + run_fact * w, ref.y - h);
			
			app.pushMatrix();
			app.translate(ref.x, ref.y - h);
			PVector prev = new PVector(0, end_value.get() / max_value.get() * h);
			for (int i = 0 ; i < sections.size() - 1 ; i++) {
				CurveSection cs = sections.get(i);
				app.line(prev.x, prev.y, 
						cs.graph_pos.x() * w / graphSX, cs.graph_pos.y() * h / graphSY);
				prev.set(cs.graph_pos.x() * w / graphSX, cs.graph_pos.y() * h / graphSY);
			}
			app.line(prev.x, prev.y, w, end_value.get() / max_value.get() * h); 
			app.popMatrix();
		} }; 
		preview.setDrawable(p_draw); 
		addEmpty(0);
		addEmpty(1); addEmpty(1);
		addEmpty(2); addEmpty(2);
		newRowValue(max_value);
		newRowValue(total_time);
	}
	void build_normal() {
		super.build_normal(); 
		co_val_out = addOutput(2, "co_val_out");
		newRowValue(run_start);
	}
	
	public MEnvelope clear() {
		super.clear(); 
		return this; 
	}
	public MEnvelope toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	
	nWidget graph = null, preview;
	Drawable g_draw, p_draw;
	ArrayList<nWidget> curs;
	ArrayList<PVector> pval;
	ArrayList<CurveSection> sections;
	nWidget end_curs;
	float graphSX = ref_size * (10.0F - 4.0F/16.0F), graphSY = ref_size * 5;
	
	void build_curs(CurveSection cs) {
		PVector p = new PVector();
		nObject obj = new nObject();
		obj.obj = cs;
		nWidget c = graph.getDrawer().addModel("EnvelopeCursor");
	    c.setParent(graph)
	    	  .center()
	      .setGrabbable()
	      .setPosition(cs.graph_pos.x(), cs.graph_pos.y())
	      .addEventDrag(new nRunnable(obj) { public void run() {
	        if (c.getLocalX() < 0) c.setPX(0); 
	        if (c.getLocalX() > graph.getLocalSX() - c.getLocalSX()) 
	            c.setPX(graph.getLocalSX() - c.getLocalSX()); 
	        if (c.getLocalY() < 0) c.setPY(0); 
	        if (c.getLocalY() > graph.getLocalSY() - c.getLocalSY()) 
	            c.setPY(graph.getLocalSY() - c.getLocalSY()); 
	        p.x = c.getLocalX() / graph.getLocalSX(); 
	        p.y = c.getLocalY() / graph.getLocalSY(); 
	        nObject obj = (nObject)builder; 
	        ((CurveSection)obj.obj).get_pval(p); 
	      }});
		graph.addEventTopLayer(new nRunnable() { public void run() { c.toLayerTop(); }});
		pval.add(p);
		curs.add(c);
	}
	
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {
			nDrawer dr = sheet_front.getTab(2).getShelf()
		        .addDrawer(10.25, 5.25);
		  
			graph = dr.addModel("Field");
			graph.setPosition(ref_size * 2 / 16, ref_size * 2 / 16)
				.setSize(graphSX, graphSY);
			
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
				gui.app.strokeWeight(3);
				gui.app.line(ref.x, ref.y, ref.x + w, ref.y);
				gui.app.line(ref.x, ref.y, ref.x, ref.y - h);

				gui.app.strokeWeight(2);
				float run_fact = (float)run_counter / total_time.get();
				gui.app.line(ref.x + run_fact * w, ref.y, ref.x + run_fact * w, ref.y - h);
				
				app.pushMatrix();
				app.translate(ref.x, ref.y - h);
				PVector prev = new PVector(0, end_value.get() / max_value.get() * h);
				for (int i = 0 ; i < sections.size() - 1 ; i++) {
					CurveSection cs = sections.get(i);
					app.line(prev.x, prev.y, 
							cs.graph_pos.x(), cs.graph_pos.y());
					prev.set(cs.graph_pos.x(), cs.graph_pos.y());
				}
				app.line(prev.x, prev.y, w, end_value.get() / max_value.get() * h);
				app.popMatrix();
			} };
			graph.setDrawable(g_draw);

			end_curs = graph.getDrawer().addModel("EnvelopeCursor");
			end_curs.setParent(graph)
		    	  .center()
		      .setGrabbable().setConstrainX(true)
		      .setPosition(0, end_value.get() / max_value.get() * graph.getLocalSY())
		      .addEventDrag(new nRunnable() { public void run() {
		    	  	PVector p = new PVector();
		        if (end_curs.getLocalY() < 0) end_curs.setPY(0); 
		        if (end_curs.getLocalY() > graph.getLocalSY() - end_curs.getLocalSY()) 
		        		end_curs.setPY(graph.getLocalSY() - end_curs.getLocalSY()); 
		        end_value.set(max_value.get() * end_curs.getLocalY() / graph.getLocalSY()); 
		        p.x = graph.getLocalSX(); 
		        p.y = end_curs.getLocalY() / graph.getLocalSY(); 
		        sections.get(sections.size() - 1).get_pval(p);
		      }});
			graph.addEventTopLayer(new nRunnable() { public void run() { end_curs.toLayerTop(); }});
			
			for (int i = 0 ; i < sections.size() - 1 ; i++) {
				CurveSection cs = sections.get(i);
				build_curs(cs);
			}
			
			dr.getShelf().addDrawer(10.25, 0.0).getShelf()
	  	  		.addDrawerWatch(current_value, 10.25F, 1)
	  	  		.addSeparator(0.125)
		    	  	.addDrawerButton(run_start, run_reset, run_stop, 10.25F, 1)
		  	  	.addSeparator(0.125)
		  	  	.addDrawerIncrValue(sec_number, 1, 10, 1)
		  	  	.addSeparator(0.125)
		  	  	.addDrawerFactValue(max_value, 2, 10, 1)
		  	  	.addSeparator(0.125)
		  	  	.addDrawerIncrValue(total_time, 100, 10, 1)
		  	  	.addSeparator(0.125)
				;
			
		  	sheet_front.addEventClose(new nRunnable(this) { public void run() { 
		  		graph = null; g_draw.clear(); g_draw = null; } } );
		  	sheet_front.toLayerTop();
		}
	}
}
