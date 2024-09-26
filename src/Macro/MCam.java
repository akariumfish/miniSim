package Macro;

import java.util.ArrayList;

import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sValueBloc;

public class MCam extends MBaseMenu { 
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("cam", "Camera", "camera drawing point", "Work"); 
		first_start_show(m); }
		MCam build(Macro_Sheet s, sValueBloc b) { MCam m = new MCam(s, b); return m; }
	}
	Drawable drawable;
	Macro_Connexion struct_link, sheet_in;
	ArrayList<MCursor> cursors;
	ArrayList<Macro_Sheet> csheets;
	ArrayList<MShape> shapes;
	ArrayList<MStructure> structs;
	nRunnable up_sheet_run, up_draw_run;
	sBoo show, show_preview, show_shape, show_struct;
	
	//mask shader, experimental
	public ArrayList<PVector> mask_vert_stack;
	
	MCam(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cam", _bloc); }
	void init() {
		super.init();   

		cursors = new ArrayList<MCursor>();
		csheets = new ArrayList<Macro_Sheet>();
		shapes = new ArrayList<MShape>();
		structs = new ArrayList<MStructure>();
		
		drawable = new Drawable() { 
			public void drawing() { draw_to_cam(); } } ;
		mmain().inter.addToCamDrawerPile(drawable);
		priority.addEventChange(new nRunnable() {public void run() {
			drawable.setLayer(priority.get());
		}});
		drawable.setLayer(priority.get());
		
		//mask shader, experimental
		mask_vert_stack = new ArrayList<PVector>();
	}
	void build_param() {
		addInputBang(0, "menu", menu_run);
		addTrigS(1, "menu", menu_run);
		addEmptyS(2);
  	  	newRowValue_Pan(priority);
		show = newRowBoo(true, "show");
		show_preview = newRowBoo(true, "preview");
		show_shape = newBoo(true, "shape");
		show_struct = newBoo(true, "struct");
		menuBoo(show, show_preview);
		menuBoo(show_shape, show_struct);
		build_normal();
	}
	int cnt = 0;
	int prio = 100;
	void class_by_prio(ArrayList<Macro_Connexion> array, boolean mode) {
		for (Macro_Connexion co : array) {
			if (co.elem.bloc.val_type.get().equals("sheetbloc")) {
				Macro_Sheet mf = ((Macro_Sheet)co.elem.bloc.sheet);
				
				if (mode) { cnt++; if (mf.priority.get() < prio) prio = mf.priority.get(); }
				
				else if (mf.priority.get() == prio) {
					csheets.add(mf);
					cnt--; mf.priority.addEventChange(up_sheet_run); }
			} else if (co.elem.bloc.val_type.get().equals("cursor")) {
				MCursor mf = ((MCursor)co.elem.bloc);
				
				if (mode) { cnt++; if (mf.priority.get() < prio) prio = mf.priority.get(); }
				
				else if (mf.priority.get() == prio) {
					cursors.add(mf);
					cnt--; mf.priority.addEventChange(up_sheet_run); }
			}
		}
	}
	
	void build_normal() {
		if (!param_view.get()) {
			addInputBang(0, "menu", menu_run);
			addTrigS(1, "menu", menu_run);
			show = newBoo(true, "show");
			show_preview = newBoo(true, "preview");
			show_shape = newBoo(true, "shape");
			show_struct = newBoo(true, "struct");
			menuBoo(show, show_preview);
			menuBoo(show_shape, show_struct);
		}
		sheet_in = addInput(0, "sheet/cursor");
		up_sheet_run = new nRunnable() { public void run() {
			for (Macro_Sheet mf : csheets) mf.priority.removeEventChange(up_sheet_run);
			for (MCursor mf : cursors) mf.priority.removeEventChange(up_sheet_run);
			csheets.clear();
			cursors.clear();
			cnt = 0;
			prio = 100;
			class_by_prio(sheet_in.connected_outputs, true);
			if (sheet_in.elem.sheet_connect != null) 
				class_by_prio(sheet_in.elem.sheet_connect.connected_outputs, true);
			while(cnt > 0) {
				class_by_prio(sheet_in.connected_outputs, false);
				if (sheet_in.elem.sheet_connect != null) 
					class_by_prio(sheet_in.elem.sheet_connect.connected_outputs, false);
				prio++;
			}
		}};
		sheet_in.addEventLink(up_sheet_run).addEventUnLink(up_sheet_run).set_link();
		if (sheet_in.elem.sheet_connect != null) {
			sheet_in.elem.sheet_connect.addEventLink(up_sheet_run).addEventUnLink(up_sheet_run);
		}
		struct_link = addInput(0, "link");
		up_draw_run = new nRunnable() { public void run() {
			for (MShape mf : shapes) mf.priority.removeEventChange(up_draw_run);
			for (MStructure mf : structs) mf.priority.removeEventChange(up_draw_run);
			shapes.clear();
			structs.clear();
			int cnt = 0;
			int prio = 100;
			for (Macro_Connexion co : struct_link.connected_outputs) {
				if (co.elem.bloc.val_type.get().equals("shape")) {
					MShape mf = ((MShape)co.elem.bloc);
					cnt++;
					if (mf.priority.get() < prio) {
						prio = mf.priority.get();
					}
				}
				if (co.elem.bloc.val_type.get().equals("struct")) {
					MStructure mf = ((MStructure)co.elem.bloc);
					cnt++;
					if (mf.priority.get() < prio) {
						prio = mf.priority.get();
					}
				}
			}
			while(cnt > 0) {
				for (Macro_Connexion co : struct_link.connected_outputs) {
					if (co.elem.bloc.val_type.get().equals("shape")) {
						MShape mf = ((MShape)co.elem.bloc);
						if (mf.priority.get() == prio) {
							shapes.add(mf);
							mf.priority.addEventChange(up_draw_run);
							cnt--;
						}
					}
					if (co.elem.bloc.val_type.get().equals("struct")) {
						MStructure mf = ((MStructure)co.elem.bloc);
						if (mf.priority.get() == prio) {
							structs.add(mf);
							mf.priority.addEventChange(up_draw_run);
							cnt--;
						}
					}
				}
				prio++;
			}
		}};
		struct_link.set_link().addEventLink(up_draw_run).addEventUnLink(up_draw_run);
		
		addInput(0, "custom");
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {
			sheet_front.toLayerTop();
		}
	}
	
	public void draw_to_cam() {
		if (show.get()) {
			
			//mask shader, experimental
			mask_vert_stack.clear();
			
			if (csheets.size() > 0) { 
				for (Macro_Sheet mf : csheets) for (nCursor cur : mf.sheet_cursors_list) {
					if (show_struct.get()) 
						for (MStructure m : structs) m.draw(gui.app, cur.pos(), cur.dir().heading());
					if (show_shape.get()) 
						for (MShape m : shapes) {
							m.draw(gui.app, cur.pos(), cur.dir());
							
							//mask shader, experimental
							PVector p1 = new PVector(m.draw_vec[0].x, m.draw_vec[0].y);
							PVector p2 = new PVector(m.draw_vec[1].x, m.draw_vec[1].y);
							PVector p3 = new PVector(m.draw_vec[2].x, m.draw_vec[2].y);
							mask_vert_stack.add(p1);
							mask_vert_stack.add(p2);
							mask_vert_stack.add(p3);
						}
				} 
			}
			if (cursors.size() > 0) { 
				for (MCursor mf : cursors) {
					if (show_struct.get()) 
						for (MStructure m : structs) m.draw(gui.app, mf.cursor.pos(), 
							mf.cursor.dir().heading());
					if (show_shape.get()) 
						for (MShape m : shapes) {
							m.draw(gui.app, mf.cursor.pos(), mf.cursor.dir());
							
							//mask shader, experimental
							PVector p1 = new PVector(m.draw_vec[0].x, m.draw_vec[0].y);
							PVector p2 = new PVector(m.draw_vec[1].x, m.draw_vec[1].y);
							PVector p3 = new PVector(m.draw_vec[2].x, m.draw_vec[2].y);
							mask_vert_stack.add(p1);
							mask_vert_stack.add(p2);
							mask_vert_stack.add(p3);
						}
				} 
			}
			if (show_preview.get()) {
				PVector p = grab_pos.get().add(ref_size*3.375F, ref_size*2.75F)
						.add(sheet.grabber.getX(), sheet.grabber.getY());
				for (MStructure m : structs) m.draw(gui.app, p, 0);
				for (MShape m : shapes) m.draw(gui.app, p, 0, ref_size*1.0F/m.median_radius);
				
			}
		}
	}
	public MCam clear() {
		super.clear(); 
		drawable.clear();
		return this; }
	public MCam toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
