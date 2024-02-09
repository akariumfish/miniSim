package Macro;

import UI.Drawable;
import UI.nCursor;
import UI.nFrontPanel;
import sData.nRunnable;
import sData.sValueBloc;

public class MCamera extends MBaseMenu { 
	static class MCam_Builder extends MAbstract_Builder {
		MCam_Builder() { super("cam", "camera drawing point"); show_in_buildtool = true; }
		MCamera build(Macro_Sheet s, sValueBloc b) { MCamera m = new MCamera(s, b); return m; }
	}
	Drawable drawable;
	Macro_Connexion struct_link, sheet_in;
	Macro_Sheet cursor_sheet;
	MCamera(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cam", _bloc); }
	void init() {
		super.init();   
		drawable = new Drawable() { 
			public void drawing() { draw_to_cam(); } } ;
		mmain().inter.addToCamDrawerPile(drawable);
		priority.addEventChange(new nRunnable() {public void run() {
			drawable.setLayer(priority.get());
		}});
		drawable.setLayer(priority.get());
	}
	void build_normal() {
		super.build_normal();
		sheet_in = addInput(0,"sheet", new nRunnable() {public void run() {
			if (sheet_in.lastPack().isSheet()) cursor_sheet = sheet_in.lastPack().asSheet();
		}});
		struct_link = addInput(0,"link");
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {
			
			sheet_front.toLayerTop();
		}
	}
	public void draw_to_cam() {
		if (cursor_sheet != null && cursor_sheet.sheet_cursors_list.size() > 0) {
			for (nCursor cur : cursor_sheet.sheet_cursors_list) {
				gui.app.pushMatrix();
				gui.app.translate(cur.x(), cur.y());
				gui.app.rotate(cur.dir().heading());
				
				for (Macro_Connexion co : struct_link.connected_outputs) {
					if (co.elem.bloc.val_type.get().equals("form")) {
						MForm mf = ((MForm)co.elem.bloc);
						mf.draw(gui.app);
					}
					if (co.elem.bloc.val_type.get().equals("struct")) {
						MStructure mf = ((MStructure)co.elem.bloc);
						mf.draw(gui.app);
					}
				}
				
				gui.app.popMatrix();
			} 
		} else {
			for (Macro_Connexion c : struct_link.connected_outputs) {
				if (c.elem.bloc.val_type.get().equals("form")) {
					MForm mf = ((MForm)c.elem.bloc);
					mf.draw(gui.app);
				}
				if (c.elem.bloc.val_type.get().equals("struct")) {
					MStructure mf = ((MStructure)c.elem.bloc);
					mf.draw(gui.app);
				}
			}
		}
	}
	public MCamera clear() {
		super.clear(); 
		drawable.clear();
		return this; }
	public MCamera toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
