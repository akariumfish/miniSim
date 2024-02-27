package Macro;

import java.util.ArrayList;

import UI.nColorPanel;
import UI.nCursor;
import UI.nExplorer;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nLinkedWidget;
import UI.nSlide;
import UI.nWidget;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;


public class M_Sheet {}


class MBasic extends Macro_Bloc { 
  Macro_Element elem_com;
  
  sBoo param_view;
  
  nLinkedWidget param_ctrl;
  nRunnable pview_run;
  
  boolean rebuilding = false;
  MBasic(Macro_Sheet _sheet, String t, sValueBloc _bloc) { 
    super(_sheet, t, t, _bloc); 
    
    param_view = newBoo("com_param_view", "com_param_view", false);
    
    get_param_openner().setRunnable(new nRunnable() { public void run() { 
        param_view.set(!param_view.get());
        rebuild();
      } });
    init();
    if (param_view.get()) build_param();
    else build_normal();
  }
  void init() { ; }
  void build_param() { addEmptyS(0); addEmptyS(1); }
  void build_normal() { addEmptyS(0); addEmptyS(1); }
  
  void rebuild() {
    if (!rebuilding) {
      rebuilding = true;
      
      boolean was_select = mmain().selected_macro.contains(this);
      
      ArrayList<Macro_Abstract> prev_selected = new ArrayList<Macro_Abstract>();
      for(Macro_Abstract m : mmain().selected_macro) if (m != this) prev_selected.add(m);
      
      sValueBloc _bloc = mmain().inter.data.copy_bloc(value_bloc, mmain().inter.data);
      clear();
      sValueBloc v_bloc = mmain().inter.data.copy_bloc(_bloc, sheet.value_bloc);
      Macro_Abstract mv = sheet.addByValueBloc(v_bloc); 
      
      mmain().szone_clear_select();
      for(Macro_Abstract m : prev_selected) m.szone_select();
      if (was_select) mv.szone_select();
    }
  }
  public MBasic clear() {
    super.clear(); 
    return this; }
  public MBasic toLayerTop() {
    super.toLayerTop(); 
    return this; }
}




class MBaseMenu extends MBasic { 
//	static class MAlive_Builder extends MAbstract_Builder {
//	MAlive_Builder() { super("alive", "live blocs"); show_in_buildtool = true; }
//	MAlive build(Macro_Sheet s, sValueBloc b) { MAlive m = new MAlive(s, b); return m; }
//}
//
//MAlive(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "alive", _bloc); }

MBaseMenu(Macro_Sheet _sheet, String n, sValueBloc _bloc) { super(_sheet, n, _bloc); }
void init() {
	eventsBuildMenu = new ArrayList<nRunnable>();
	menu_run = new nRunnable() { public void run() { build_bloc_menu(); }};
	new_preset_name = newStr("preset_name", "pname", "new"); 
	menu_open_val = newBoo("menu_open_val", "menuop", false); 
}
void init_end() {
	super.init_end();
	if (menu_open_val.get()) build_bloc_menu();
}
void build_param() {
	addEmptyS(0);
	addTrigS(1, "menu", menu_run);
}
void build_normal() {
	addEmptyS(0);
	addTrigS(1, "menu", menu_run);
}
public MBaseMenu clear() {
	super.clear(); 
	if (bloc_front != null) bloc_front.clear();
	return this; }
public MBaseMenu toLayerTop() {
	super.toLayerTop(); 
	return this;
}

MBaseMenu addEventsBuildMenu(nRunnable r) { eventsBuildMenu.add(r); return this; }
ArrayList<nRunnable> eventsBuildMenu;
nFrontTab custom_tab;
nFrontPanel bloc_front;  
nExplorer bloc_viewer, preset_explorer;
sStr new_preset_name; sBoo menu_open_val;
nWidget match_flag;
nRunnable menu_run;

public void build_custom_menu(nFrontPanel sheet_front) {}

public void build_bloc_menu() {
if (bloc_front == null) {
		menu_open_val.set(true);	
  bloc_front = new nFrontPanel(mmain().screen_gui, mmain().inter.taskpanel, val_title.get());
  
  bloc_front.addTab("View").getShelf()
    .addSeparator(0.125)
    .addDrawer(10.25, 1).addModel("Label-S3", "sheet view :").setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
    .addSeparator()
    ;
  bloc_viewer = bloc_front.getTab(0).getShelf(0)
    .addSeparator()
    .addExplorer()
      .setChildAccess(false)
      .setStrtBloc(value_bloc)
      .addValuesModifier(mmain().inter.taskpanel)
      .addEventChange(new nRunnable() { public void run() { 
          if (bloc_viewer.explored_bloc != value_bloc) {
            bloc_viewer.setStrtBloc(value_bloc);
          }
      } } )
      ;
  match_flag = bloc_front.addTab("Preset").getShelf()
    .addSeparator(0.125)
    .addDrawer(10.25, 1).addModel("Label-S3", "Presets :").setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
    .addCtrlModel("Button-S2-P3", "Delete").setRunnable(new nRunnable() { public void run() { 
      preset_explorer.selected_bloc.clear(); 
      for (nExplorer e : mmain().presets_explorers) e.update(); } } )
      .setInfo("delete selected preset").getDrawer()
    .addModel("Label_DownLight_Back-S2-P2", "")
      .setInfo("selected preset compatibility with this sheet");
    
  match_flag.getShelf()
    .addSeparator()
    ;
  preset_explorer = bloc_front.getTab(1).getShelf(0)
    .addSeparator()
    .addExplorer()
      .setStrtBloc(mmain().saved_preset)
      //.hideGoBack()
      .addValuesModifier(mmain().inter.taskpanel)
      .addEventChange(new nRunnable() { public void run() { 
        if (!(preset_explorer.explored_bloc == mmain().saved_preset ||
            preset_explorer.explored_bloc.parent == mmain().saved_preset)) 
          preset_explorer.setStrtBloc(mmain().saved_preset);
        if (preset_explorer.selected_bloc != null && 
            (mmain().inter.data.values_found(value_bloc, preset_explorer.selected_bloc) || 
            		mmain().inter.data.values_found(preset_explorer.selected_bloc, value_bloc)) ) 
          match_flag.setLook(gui.theme.getLook("Label_HightLight_Back")).setText("matching");
        else match_flag.setLook(gui.theme.getLook("Label_DownLight_Back")).setText("");
      } } )
      ;
  mmain().presets_explorers.add(preset_explorer);
  
  preset_explorer.getShelf()
    .addSeparator(0.25)
    .addDrawer(1)
      .addCtrlModel("Button-S2-P1", "Save").setRunnable(new nRunnable() { public void run() { 
        save_preset(); } } ).setInfo("Save sheet values as preset").getDrawer()
      .addLinkedModel("Field-S2-P2").setLinkedValue(new_preset_name).getDrawer()
      .addCtrlModel("Button-S2-P3", "Load").setRunnable(new nRunnable() { public void run() { 
        load_preset(); } } ).setInfo("load corresponding preset values into sheet values").getDrawer()
      .getShelf()
    .addSeparator(0.25)
    ;
  //sheet_front.setPosition(
  //  screen_gui.view.pos.x + screen_gui.view.size.x - sheet_front.grabber.getLocalSX() - ref_size * 3, 
  //  screen_gui.view.pos.y + ref_size * 2 );
  
  custom_tab = bloc_front.addTab("Control");

  custom_tab.getShelf()
    .addDrawer(10.25, 0.75)
    .addModel("Label-S10/0.75", "-  Control  -").setFont((int)(ref_size/1.5)).getShelf()
    .addSeparator(0.125)
    ;
  nRunnable.runEvents(eventsBuildMenu);
  
  build_custom_menu(bloc_front);
  
  bloc_front.addEventClose(new nRunnable(this) { public void run() { 
    if (preset_explorer != null) mmain().presets_explorers.remove(preset_explorer);
    bloc_front = null;
    menu_open_val.set(false); }});
} else {
  bloc_front.popUp();
}
}

void save_preset() {
Save_Bloc b = new Save_Bloc("");
value_bloc.preset_value_to_save_bloc(b);
mmain().saved_preset.newBloc(b, new_preset_name.get());
for (nExplorer e : mmain().presets_explorers) { 
  e.update();
  e.selectEntry(new_preset_name.get());
}
}
void load_preset() {
if (preset_explorer.selected_bloc != null) {
	mmain().inter.data.transfer_bloc_values(preset_explorer.selected_bloc, value_bloc);
}
}
  

sInt menuIntSlide(int v, int _min, int _max, String r) {
	boolean new_val = value_bloc.getValue(r) == null;
	sInt f = newInt(v, r, r);
	if (new_val) f.set_limit(_min, _max);
	addEventsBuildMenu(new nRunnable(f) { public void run() { 
	  if (custom_tab != null) custom_tab.getShelf().addDrawer(10, 1)
	  .addModel("Label_Small_Text-S1-P1", ((sInt)builder).ref)
	    .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
	  .addWatcherModel("Auto_Watch_Label-S1-P3")
	    .setLinkedValue(((sInt)builder))
	    .setTextAlignment(PConstants.CENTER, PConstants.CENTER).getDrawer()
	  .addWidget(new nSlide(custom_tab.gui, ref_size * 6, ref_size * 0.75F)
	    .setValue( ( ((sInt)builder).get() - ((sInt)builder).getmin() ) / 
	               ( ((sInt)builder).getmax() - ((sInt)builder).getmin() ) )
	    .addEventSlide(new nRunnable(((sInt)builder)) { public void run(float c) { 
	      ((sInt)builder).set( (int)( ((sInt)builder).getmin() + 
	                                c * (((sInt)builder).getmax() - ((sInt)builder).getmin()) ) ); 
	    } } )
	    .setPosition(4*ref_size, ref_size * 2 / 16) ).getShelf()
	  .addSeparator(0.125);
	} });
	return f;
}
protected sFlt menuFltSlide(float v, float _min, float _max, String r) {
	boolean new_val = value_bloc.getValue(r) == null;
	sFlt f = newFlt(v, r, r);
	if (new_val) f.set_limit(_min, _max);
	addEventsBuildMenu(new nRunnable(f) { public void run() { 
	  if (custom_tab != null) custom_tab.getShelf().addDrawer(10, 1)
	  .addModel("Label_Small_Text-S1-P1", ((sFlt)builder).ref)
	    .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
	  .addWatcherModel("Auto_Watch_Label-S1-P3")
	    .setLinkedValue(((sFlt)builder))
        .setSX(ref_size*2.0)
        .setPX(ref_size*2.875)
	    .setTextAlignment(PConstants.CENTER, PConstants.CENTER).getDrawer()
	  .addWidget(new nSlide(custom_tab.gui, ref_size * 5, ref_size * 0.75F)
	    .setValue( ( ((sFlt)builder).get() - ((sFlt)builder).getmin() ) / 
	               ( ((sFlt)builder).getmax() - ((sFlt)builder).getmin() ) )
	    .addEventSlide(new nRunnable(((sFlt)builder)) { public void run(float c) { 
	      ((sFlt)builder).set( ((sFlt)builder).getmin() + 
	                           c * (((sFlt)builder).getmax() - ((sFlt)builder).getmin()) ); 
	    } } )
	    .setPosition(5*ref_size, ref_size * 2 / 16) ).getShelf()
	  .addSeparator(0.125);
	} });
	return f;
}
public sCol menuColor(int v, String r) {
    sCol f = newCol(r, r, v);
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
        .addDrawer(10, 1)
        .addCtrlModel("Auto_Button-S2-P3", "choose").setRunnable(new nRunnable(builder) { public void run() { 
          new nColorPanel(custom_tab.gui, mmain().inter.taskpanel, ((sCol)builder));
        } } ).getDrawer()
        .addWatcherModel("Auto_Watch_Label-S6/1", "Color picker: " + ((sCol)builder).ref)
          .setLinkedValue(((sCol)builder))
          .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
        .getShelf()
        .addSeparator(0.125);
    } });
    return f;
  }
public sBoo menuBoo(boolean v, String r) {
    sBoo f = newBoo(r, r, v);
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
        .addDrawer(10, 1)
        .addCtrlModel("Auto_Button-S2-P2", r).setLinkedValue(f).getShelf()
        .addSeparator(0.125);
    } });
    return f;
  }
protected sInt menuIntWatch(int v, String r) {
sInt f = newInt(v, r, r);
addEventsBuildMenu(new nRunnable(f) { public void run() { 
  if (custom_tab != null) custom_tab.getShelf()
  .addDrawerWatch(((sInt)builder), 10, 1)
  .addSeparator(0.125);
} });
return f;
}
sFlt menuFltIncr(float v, float _f, String r) {
sFlt f = newFlt(v, r, r);
f.ctrl_factor = _f;
addEventsBuildMenu(new nRunnable(f) { public void run() { 
  if (custom_tab != null) custom_tab.getShelf()
  .addDrawerIncrValue(((sFlt)builder), ((sFlt)builder).ctrl_factor, 10, 1)
  .addSeparator(0.125);
} });
return f;
}
public sFlt menuFltFact(float v, float _f, String r) {
sFlt f = newFlt(v, r, r);
f.ctrl_factor = _f;
addEventsBuildMenu(new nRunnable(f) { public void run() { 
  if (custom_tab != null) custom_tab.getShelf()
  .addDrawerFactValue(((sFlt)builder), ((sFlt)builder).ctrl_factor, 10, 1)
  .addSeparator(0.125);
} });
return f;
}
public sInt menuIntIncr(int v, float _f, String r) {
sInt f = newInt(v, r, r);
f.ctrl_factor = _f;
addEventsBuildMenu(new nRunnable(f) { public void run() { 
  if (custom_tab != null) custom_tab.getShelf()
  .addDrawerIncrValue(((sInt)builder), ((sInt)builder).ctrl_factor, 10, 1)
  .addSeparator(0.125);
} });
return f;
}
sInt menuIntFact(int v, float _f, String r) {
sInt f = newInt(v, r, r);
f.ctrl_factor = _f;
addEventsBuildMenu(new nRunnable(f) { public void run() { 
  if (custom_tab != null) custom_tab.getShelf()
  .addDrawerFactValue(((sInt)builder), ((sInt)builder).ctrl_factor, 10, 1)
  .addSeparator(0.125);
} });
return f;
}
}









class MCursor extends MBasic { 
  static class MCursor_Builder extends MAbstract_Builder {
    MCursor_Builder() { super("cursor", "Cursor", "add a cursor", "Sheet"); }
  MCursor build(Macro_Sheet s, sValueBloc b) { MCursor m = new MCursor(s, b); return m; }
  }
  public nCursor cursor;
    public sVec pval = null;
    public sVec dval = null;
    public sBoo show = null;
  nRunnable sheet_grab_run, pval_run;
  Macro_Connexion in, out;
  MCursor(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cursor", _bloc); }
  void init_cursor() {
	  cursor = new nCursor(mmain(), value_bloc, true);
	  mmain().cursors_list.add(cursor);
//	  mmain().update_cursor_selector_list();
	  sheet.sheet_cursors_list.add(cursor);
	  sheet.cursor_count++;
	  
	  if (!(pval.x() == 0 && pval.y() == 0)) {
	    setPosition(pval.get().x - sheet.grabber.getX(), 
	        pval.get().y - sheet.grabber.getY());
	    moving(); 
	  }
	  
	  cursor.addEventClear(new nRunnable(cursor) { public void run() { 
	      sheet.sheet_cursors_list.remove(((nCursor)builder));
	    mmain().cursors_list.remove(((nCursor)builder)); 
	    sheet.cursor_count--;
//	    mmain().update_cursor_selector_list(); 
	    }});
	  
	  grab_pos.addEventChange(new nRunnable() { public void run() {
	      if (cursor.pval != null) cursor.pval.set(
	          grab_pos.get().x + sheet.grabber.getX(), 
	          grab_pos.get().y + sheet.grabber.getY());
	  } });
  	pval_run = new nRunnable() { public void run() {
	if (sheet != mmain())
	setPosition(cursor.pval.get().x - sheet.grabber.getX(), 
          cursor.pval.get().y - sheet.grabber.getY());
	else setPosition(cursor.pval.get().x, cursor.pval.get().y);
	moving();
	  
	if (out != null) out.send(cursor.pval.asPacket());
	}};
	cursor.pval.addEventChange(pval_run);
	  
	  
	if (sheet != mmain()) {
		sheet_grab_run = new nRunnable() { public void run() {
			PVector tm = new PVector(cursor.pval.get().x, cursor.pval.get().y);
		    setPosition(tm.x - sheet.grabber.getX(), 
		                tm.y - sheet.grabber.getY());
		    moving();
		} };
		sheet.grab_pos.addEventAllChange(sheet_grab_run);
	}
  }
  void build_param() {
	  show = newRowBoo(false, "show"); //!!!!! is hided by default
	  pval = newRowVec("pos");
	  dval = newRowVec("dir");
	  init_cursor();
  }
  void build_normal() {
	  show = newBoo(false, "show"); //!!!!! is hided by default
	  pval = newVec("pos");
	  dval = newVec("dir");
	  init_cursor();
	    addEmptyS(0);
	    addSwitchS(1, "show", show);
  }
  public MCursor clear() {
    super.clear(); 
    cursor.clear();
    if (pval != null) pval.removeEventChange(pval_run);
    if (sheet != mmain()) sheet.grab_pos.removeEventChange(sheet_grab_run);
    return this; }
  public MCursor toLayerTop() {
    super.toLayerTop(); 
    return this; }
}


//class MSheetIn extends Macro_Bloc {
//	static class MSheetI_Builder extends MAbstract_Builder {
//		MSheetI_Builder() { 
//			super("in", "connexions with sheet parent"); show_in_buildtool = true; }
//		MSheetIn build(Macro_Sheet s, sValueBloc b) { MSheetIn m = new MSheetIn(s, b); return m; }
//	}
//  Macro_Element elem;
//  nLinkedWidget view;
//  sStr val_view;
//  
//  MSheetIn(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "in", "in", _bloc); 
//    val_view = newStr("val", "val", "");
//    view = addEmptyS(1).addLinkedModel("MC_Element_SField");
//    view.addEventFieldChange(new nRunnable() { public void run() { 
//      elem.sheet_connect.setInfo(val_view.get());
//    } });
//    view.setLinkedValue(val_view);
//    elem = addSheetInput(0, "in");
//    if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
//    //val_title.addEventChange(new nRunnable() { public void run() { 
//    //if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_title.get()); } });
//  }
//  public MSheetIn clear() {
//    super.clear(); return this; }
//}
//
//class MSheetOut extends Macro_Bloc {
//	static class MSheetO_Builder extends MAbstract_Builder {
//		MSheetO_Builder() { 
//			super("out", "connexions with sheet parent"); show_in_buildtool = true; }
//		MSheetOut build(Macro_Sheet s, sValueBloc b) { MSheetOut m = new MSheetOut(s, b); return m; }
//	}
//  Macro_Element elem;
//  nLinkedWidget view;
//  sStr val_view;
//  MSheetOut(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "out", "out", _bloc); 
//    val_view = newStr("val", "val", "");
//    view = addEmptyS(0).addLinkedModel("MC_Element_SField");
//    view.addEventFieldChange(new nRunnable() { public void run() { 
//      elem.sheet_connect.setInfo(val_view.get());
//    } });
//    view.setLinkedValue(val_view);
//    elem = addSheetOutput(1, "out");
//    if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
//    //val_title.addEventChange(new nRunnable() { public void run() { 
//    //if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_title.get()); } });
//  }
//  public MSheetOut clear() {
//    super.clear(); return this; }
//}

class MSheetCo extends MBasic {
	static class MSheetCo_Builder extends MAbstract_Builder {
		MSheetCo_Builder() { 
			super("co", "Sheet Co", "connexions with sheet parent", "Sheet"); }
		MSheetCo build(Macro_Sheet s, sValueBloc b) { MSheetCo m = new MSheetCo(s, b); return m; }
	}
  Macro_Element elem;
  nLinkedWidget view;
  sStr val_view;
  MSheetCo(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "co", _bloc); }
  public void init() {
    val_view = newStr("val", "val", "");
  }
  public void build_param() {
    view = addEmptyS(0).addLinkedModel("MC_Element_SField");
    view.addEventFieldChange(new nRunnable() { public void run() { 
      elem.sheet_connect.setInfo(val_view.get());
    } });
    view.setLinkedValue(val_view);
    elem = addSheetOutput(1, "out");
    if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
  }

  public void build_normal() {
    view = addEmptyS(1).addLinkedModel("MC_Element_SField");
    view.addEventFieldChange(new nRunnable() { public void run() { 
      elem.sheet_connect.setInfo(val_view.get());
    } });
    view.setLinkedValue(val_view);
    elem = addSheetInput(0, "in");
    if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
  }
  
  public MSheetCo clear() {
    super.clear(); return this; }
}


class MSheetMain extends MBasic { 
	static class MSheetMain_Builder extends MAbstract_Builder {
		MSheetMain_Builder() { 
			super("sheetmain", "SheetMain", "sheet mains tools", "Sheet"); 
//			visible = false;
			show_in_buildtool = false; }
		MSheetMain build(Macro_Sheet s, sValueBloc b) { MSheetMain m = new MSheetMain(s, b); return m; }
	}
	Macro_Element menu_elem;
	Macro_Connexion in_menu;
	nLinkedWidget stp_view; sBoo setup_send, menu_reduc; sVec menu_pos; sInt menu_tab;
	nRunnable grab_run, reduc_run, close_run, tab_run;
	  nRunnable goto_run;
	  Macro_Connexion out_sheet;
	  sBoo show1, show2, show3, show4;
	MSheetMain(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "sheetmain", _bloc); }
	void init() {
	    setup_send = newBoo("stp_snd", "stp_snd", false);
	    menu_reduc = newBoo("menu_reduc", "menu_reduc", false);
	    menu_tab = newInt("menu_tab", "menu_tab", 0);
	    menu_pos = newVec("menu_pos", "menu_pos");
	    show1 = newBoo("show1", "show1", false);
	    show2 = newBoo("show2", "show2", false);
	    show3 = newBoo("show3", "show3", false);
	    show4 = newBoo("show4", "show4", false);

	    goto_run = new nRunnable() { public void run() {
	      float ns = mmain().inter.cam.cam_scale.get();
	      if (gui.view.size.x / sheet.back.getLocalSX() < gui.view.size.y / sheet.back.getLocalSY())
	        ns *= gui.view.size.x / sheet.back.getLocalSX();
	      else ns *= gui.view.size.y / sheet.back.getLocalSY();
	      ns = Math.min(1.0F, ns);
	      ns = Math.max(0.03F, ns);
	      mmain().inter.cam.cam_scale.set(ns);
	      mmain().inter.cam.cam_pos
	        .set(-(sheet.back.getX() + sheet.back.getLocalSX()/2) * mmain().inter.cam.cam_scale.get(), 
	             -(sheet.back.getY() + sheet.back.getLocalSY()/2) * mmain().inter.cam.cam_scale.get() );
	    }};
	    grab_run = new nRunnable() { public void run() { if (sheet.sheet_front != null) 
	        menu_pos.set(sheet.sheet_front.grabber.getLocalX(), 
	        				sheet.sheet_front.grabber.getLocalY()); } };
	    reduc_run = new nRunnable() { public void run() {
	    		if (sheet.sheet_front != null) menu_reduc.set(sheet.sheet_front.collapsed); } };
	    tab_run = new nRunnable() { public void run() {
	    		if (sheet.sheet_front != null) menu_tab.set(sheet.sheet_front.current_tab_id); } };
	    close_run = new nRunnable() { public void run() { setup_send.set(false); } };

	    if (setup_send.get()) mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	    		menu(); } });
	}
	void init_end() {
		super.init_end();
		if (!loading_from_bloc && menu_elem != null) { //
			sheet.add_spot("left", menu_elem);
			sheet.add_spot("right", out_sheet.elem);
		}
	}
	void build_param() {
		menu_elem = addEmptyS(1);
		menu_elem.addTrigS("menu", new nRunnable() { public void run() {
		    		menu(); grab_run.run(); tab_run.run(); reduc_run.run(); } })
		    .setInfo("open sheet general menu");
		menu_elem.addLinkedMiniSwitch("st", setup_send);
	    addInputBang(0, "open menu", new nRunnable() { public void run() { 
	    		menu(); setup_send.set(true); } });
	    out_sheet = addOutput(2, "sheet link");
	    out_sheet.set_link();

	    addEmptyS(2); 
	    addTrigS(0, "show", "show all sheet cursor", new nRunnable() { public void run() {
	    		for (nCursor c : sheet.sheet_cursors_list) c.show.set(true);
	    } });
	    addTrigS(1, "hide", "hide all sheet cursor", new nRunnable() { public void run() {
    			for (nCursor c : sheet.sheet_cursors_list) c.show.set(false);
	    } });
    		
	    addEmptyS(1); addEmptyS(2); 
	    addEmptyXL(0).addSwitchXLSelector(1, "view", "show view access in normal view", show1)
	    .addSwitchXLSelector(2, "open", "show openning control in normal view", show2)
	    .addSwitchXLSelector(3, "self", "show selh object access in normal view", show3)
	    .addSwitchXLSelector(4, "prio", "show sheet priority control in normal view", show4);
	    
	    addTrigS(1, "view", goto_run).setInfo("view full sheet");
	    addInputBang(0, "view", new nRunnable() { public void run() { goto_run.run(); }});
	    addEmptyS(2);
	    
	    addInputBang(0, "Deploy Sheet", 
	    		new nRunnable() { public void run() { sheet.deploy(); }});
	    addInputBang(1, "Open Sheet", 
	    		new nRunnable() { public void run() { sheet.open(); }});
	    addInputBang(2, "Reduc Sheet", 
	    		new nRunnable() { public void run() { sheet.reduc(); }});
	    
	    newRowProtectedValue(sheet.val_self);
	    newRowValue_Pan(sheet.priority);

//	    newRowValue(sheet.grab_pos);
	}
	void build_normal() {
	    if (show2.get() || show3.get() || show4.get()) addEmptyS(2);
	    menu_elem = addEmptyS(0);
	    menu_elem.addTrigS("menu", new nRunnable() { public void run() {
	    	menu(); grab_run.run(); tab_run.run(); reduc_run.run(); } })
		    .setInfo("open sheet general menu");

	    out_sheet = addOutput(1, "sheet link");
	    out_sheet.set_link();

	    if (show2.get() || show3.get() || show4.get()) addEmptyS(2); 
	    addTrigS(0, "show", "show all sheet cursor", new nRunnable() { public void run() {
	    		for (nCursor c : sheet.sheet_cursors_list) c.show.set(true);
	    } });
	    addTrigS(1, "hide", "hide all sheet cursor", new nRunnable() { public void run() {
    			for (nCursor c : sheet.sheet_cursors_list) c.show.set(false);
	    } });
	    
	    if (show1.get()) {
		    addTrigS(1, "view", goto_run).setInfo("view full sheet");
		    addInputBang(0, "view", new nRunnable() { public void run() { goto_run.run(); }});
		    if (show2.get() || show3.get() || show4.get())
		    		addEmptyS(2);
	    }
	    if (show2.get()) {
		    addInputBang(0, "Dep", 
		    		new nRunnable() { public void run() { sheet.deploy(); }});
		    addInputBang(1, "Opn", 
		    		new nRunnable() { public void run() { sheet.open(); }});
		    addInputBang(2, "Red", 
		    		new nRunnable() { public void run() { sheet.reduc(); }});
	    }
	    if (show3.get()) {
		  newRowProtectedValue(sheet.val_sheet);
	    }
	    if (show4.get()) {
	    		newRowValue_Pan(sheet.priority);
	    }
	}
	void menu() {
	    sheet.build_sheet_menu();
	    if (sheet.sheet_front != null) { 
		    if (menu_pos.get().x == 0 && menu_pos.get().y == 0) grab_run.run();
		    if (setup_send.get()) {
				if (!(menu_pos.get().x == 0 && menu_pos.get().y == 0)) sheet.sheet_front.grabber.setPosition(menu_pos.get());
				if (menu_reduc.get()) sheet.sheet_front.collapse(); else sheet.sheet_front.popUp();
				sheet.sheet_front.setTab(menu_tab.get());
		    }
			setup_send.set(true);
			sheet.sheet_front.grabber.addEventDrag(grab_run); 
			sheet.sheet_front.addEventTab(tab_run)
			.addEventCollapse(reduc_run).addEventClose(close_run);  
	    }
	}
	public MSheetMain clear() {
	    if (sheet.sheet_front != null) sheet.sheet_front.grabber.removeEventDrag(grab_run);
	    if (sheet.sheet_front != null) sheet.sheet_front.removeEventCollapse(reduc_run); 
	    if (sheet.sheet_front != null) sheet.sheet_front.removeEventClose(close_run); 
	    if (sheet.sheet_front != null) sheet.sheet_front.clear();
		super.clear(); 
		return this; }
	public MSheetMain toLayerTop() {
		super.toLayerTop(); 
		return this; }
}