package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;


public class M_Sheet {}


class MBasic extends Macro_Bloc { 
  static class Builder extends MAbstract_Builder {
	  Builder(Macro_Main m) { super("base", "Base", "Base Bloc", "Sheet"); 
	  first_start_show(m); }
	  MBasic build(Macro_Sheet s, sValueBloc b) { MBasic m = new MBasic(s); return m; }
    }
  sStr bloc_type;
  nWidget bloc_field;
  boolean builder_mode = false;
  MBasic(Macro_Sheet _sheet) { super(_sheet, "base", "base", null); init_creator(); }
  MBasic(Macro_Sheet _sheet, PVector pos) {  // for adding at mouse position
	  super(_sheet, "base", "base", null); 
	  setPosition(	pos.x-sheet.grab_pos.get().x-ref_size*2.25F, 
			  		pos.y-sheet.grab_pos.get().y-ref_size*1.0F);
	  init_creator(); 
	  if (mmain().active_construct != null) mmain().active_construct.clear();
	  mmain().active_construct = this; }
  void init_creator() {
	  hide_ctrl = true;
	  title.hide(); reduc.hide();
      prio_sub.hide(); prio_add.hide(); prio_view.hide(); 
	  bloc_type = newStr("val", "val", "");
	  addEmpty(1);
	  bloc_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(bloc_type);
	  bloc_field.setPosition(ref_size*0.125, ref_size*0.0625)
	  	.setSize(ref_size*4.25, ref_size*1);
	  bloc_type.addEventChange(new nRunnable() { public void run() { 
		  for (MAbstract_Builder mb : mmain().bloc_builders) if (mb.type.equals(bloc_type.get())) {
			  Macro_Abstract m = mb.build(sheet, null);//((MAbstract_Builder)builder)
			  m.setPosition(grab_pos.get().x, grab_pos.get().y);
			  mmain().inter.addEventNextFrame(new nRunnable(m) { public void run() {
				  clear();
				  m.szone_select();
			  }});
		  }
	  } } );
	  bloc_field.addEventFieldUnselect(new nRunnable() { public void run() { clear(); } } );
	  mmain().inter.addEventNextFrame(new nRunnable(this) { public void run() {
		  szone_unselect();
		  mmain().selected_macro.remove((MBasic)builder);
		  mmain().update_select_bound();
		  bloc_field.field_select();
	  }});
  }

  public MBasic clear() {
    super.clear(); 
    if (mmain().active_construct == this) mmain().active_construct = null;
    return this; }
  
  Macro_Element elem_com;
  
  sBoo param_view, mirror_view;
  sStr links_save;
  
  nCtrlWidget param_ctrl;
  nRunnable pview_run;
  nRunnable link_change_run;
  boolean rebuilding = false;
  MBasic(Macro_Sheet _sheet, String t, sValueBloc _bloc) { 
    super(_sheet, t, t, _bloc);
    builder_mode = true;
    links_save = newStr("links_save", "links_save", "");
    link_change_run = new nRunnable() {public void run() {
		  save_co_links(); } };
    param_view = newBoo("com_param_view", "com_param_view", false);
	mirror_view = newBoo("mirror_view", "mirror_view", false);
	param_view.addEventChange(new nRunnable() { public void run() { 
        if (param_view.get()) param_ctrl.setText("N").setInfo("hide param");
        else param_ctrl.setText("P").setInfo("show param");
	}});
	mirror_view.addEventChange(new nRunnable() { public void run() { 
		save_co_links();
		for (Macro_Element m : elements)
			  m.mirror(mirror_view.get());
	}});
    if (!param_view.get()) get_mirror().setRunnable(new nRunnable() { public void run() { 
    		mirror_view.set(!mirror_view.get());
    		save_co_links();
    		for (Macro_Element m : elements)
    			  m.mirror(mirror_view.get());
//          rebuild();
    } });
	param_ctrl = get_param_openner().setRunnable(new nRunnable() { public void run() { 
        param_view.set(!param_view.get());
        rebuild();
    } });
    if (param_view.get()) param_ctrl.setText("N").setInfo("hide param");
    else param_ctrl.setText("P").setInfo("show param");
    init();
    if (param_view.get()) build_param();
    else build_normal();
  }
  void init() { ; }
  void build_param() { }//addEmptyS(0); addEmptyS(1); }
  void build_normal() { }//addEmptyS(0); addEmptyS(1); }
  
  void init_end() {
	  super.init_end();
	  if (builder_mode) {
		  for (Macro_Element m : elements) {
			  if (!param_view.get()) m.mirror(mirror_view.get());
			  if (m.connect != null) m.connect.addEventChangeLink(link_change_run);
			  if (m.sheet_connect != null) 
				  m.sheet_connect.addEventChangeLink(link_change_run);
		  }
		  load_co_links();
	  }
  }
  void load_co_links() {
	  for (Macro_Element m : elements) {
		  if (m.connect != null) m.connect.removeEventChangeLink(link_change_run);
		  if (m.sheet_connect != null) 
			  m.sheet_connect.removeEventChangeLink(link_change_run);
	  }
      for (Macro_Element e : elements) {
    	      if (e.connect != null) {
  	  		  load_co_links(e.connect);
  		  	  if (e.sheet_connect != null) load_co_links(e.sheet_connect);
    	      }
      }
      for (Macro_Element m : elements) {
		  if (m.connect != null) m.connect.addEventChangeLink(link_change_run);
		  if (m.sheet_connect != null) 
			  m.sheet_connect.addEventChangeLink(link_change_run);
	  }
      link_change_run.run();
  }
  void load_co_links(Macro_Connexion co) {
	  String[] co_type = PApplet.splitTokens(links_save.get(), OBJ_TOKEN);
	  for (String s : co_type) {
		  String[] co_links = PApplet.splitTokens(s, GROUP_TOKEN);
		  if (co_links.length > 1 && co.base_info.equals(co_links[0])) {
			  co_links = PApplet.splitTokens(co_links[1], INFO_TOKEN);
			  for (String d : co_links) {
				  String[] e = PApplet.splitTokens(d, BLOC_TOKEN);
				  for (Macro_Connexion c : co.sheet.child_connect) 
					  if (e.length > 1 && c.elem.descr.equals(e[1]) &&
						  c.elem.bloc.value_bloc.ref.equals(e[0])) {
					  co.connect_to(c);
				  }
			  }
		  }
	  }
  }
  void save_co_links() {
	  links_save.set("");
      for (Macro_Element e : elements) {
  	  	if (e.connect != null) {
  	  		save_co_links(e.connect);
  		  	if (e.sheet_connect != null) save_co_links(e.sheet_connect);
  	  	}
      }
  }
  void save_co_links(Macro_Connexion co) {
	ArrayList<Macro_Connexion> co_list = null;
	if (co.type == INPUT) co_list = co.connected_outputs;
	else if (co.type == OUTPUT) co_list = co.connected_inputs;
	if (co_list != null && co_list.size() > 0) {
		links_save.add(co.base_info + GROUP_TOKEN);
		for (Macro_Connexion c : co_list) {
			links_save.add(c.elem.bloc.value_bloc.ref + BLOC_TOKEN + c.elem.descr + INFO_TOKEN);
		}
		links_save.add(OBJ_TOKEN);
	}
  }
  void rebuild() {
    if (!rebuilding) {
    	  unclearable = false; 
      rebuilding = true;
      
      save_co_links();
      
      boolean was_select = mmain().selected_macro.contains(this);
      
      ArrayList<Macro_Abstract> prev_selected = new ArrayList<Macro_Abstract>();
      for(Macro_Abstract m : mmain().selected_macro) if (m != this) prev_selected.add(m);
      
      sValueBloc _bloc = mmain().inter.data.copy_bloc(value_bloc, mmain().inter.data);
      clear();
      sValueBloc v_bloc = mmain().inter.data.copy_bloc(_bloc, sheet.value_bloc);
      Macro_Abstract mv = sheet.addByValueBloc(v_bloc); 
      mv.init_end();
      mmain().szone_clear_select();
      for(Macro_Abstract m : prev_selected) m.szone_select();
      if (was_select) mv.szone_select();
    }
  }
  public MBasic toLayerTop() {
    super.toLayerTop(); 
    return this; }
}




class MBaseTick extends MBasic { 
	sBoo global_tick;
	MBaseTick(Macro_Sheet _sheet, String t, sValueBloc _bloc) { super(_sheet, t, _bloc); }
	void init() { 
		super.init(); 
		global_tick = newBoo(true, "global_tick");
		mmain().baseticked_list.add(this); }
	void build_param() { super.build_param(); }
	void build_normal() { super.build_normal(); }
	void init_end() { super.init_end(); }
	void rebuild() { super.rebuild(); }
	void tick() { ; }
	void receive_tick() { if (global_tick.get()) tick(); }
	public MBaseTick toLayerTop() { super.toLayerTop(); return this; }
	public MBaseTick clear() { super.clear(); mmain().baseticked_list.remove(this); return this; }
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
		addInputBang(0, "menu", menu_run);
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
  

	
	protected sValue menuWatch(sValue f) {
		addEventsBuildMenu(new nRunnable(f) { public void run() { 
			if (custom_tab != null) custom_tab.getShelf()
			.addDrawerWatch(((sValue)builder), 10, 1)
			.addSeparator(0.125);
		} });
		return f;
  	}
	public void menuRun(sRun r1, sRun r2) {
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addCtrlModel("Auto_Button-S3-P1", r1.ref).setLinkedValue(r1).getDrawer()
	        .addCtrlModel("Auto_Button-S3-P2", r2.ref).setLinkedValue(r2).getShelf()
	        .addSeparator(0.125);
	    } });
	}
	public void menuRun(sRun r1, sRun r2, sRun r3) {
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addCtrlModel("Auto_Button-S2-P1", r1.ref).setLinkedValue(r1).getDrawer()
	        .addCtrlModel("Auto_Button-S2-P2", r2.ref).setLinkedValue(r2).getDrawer()
	        .addCtrlModel("Auto_Button-S2-P3", r3.ref).setLinkedValue(r3).getShelf()
	        .addSeparator(0.125);
	    } });
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
	public sBoo menuBoo(sBoo f) {
	    addEventsBuildMenu(new nRunnable(f) { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addLinkedModel("Auto_Button-S2-P2", f.ref).setLinkedValue(f).getShelf()
	        .addSeparator(0.125);
	    } });
	    return f;
	}
	public void menuBoo(sBoo b1, sBoo b2) {
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addLinkedModel("Auto_Button-S3-P1", b1.ref).setLinkedValue(b1).getDrawer()
	        .addLinkedModel("Auto_Button-S3-P2", b2.ref).setLinkedValue(b2).getShelf()
	        .addSeparator(0.125);
	    } });
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







class MZone extends MBasic { 
  static class Builder extends MAbstract_Builder {
    Builder(Macro_Main m) { super("zone", "Zone", "--", "Sheet"); 
	  first_start_show(m); }
    MZone build(Macro_Sheet s, sValueBloc b) { MZone m = new MZone(s, b); return m; }
  }
  sVec corner_pos;
  nLinkedWidget corner_widg;
  nWidget zone_widg;
  Drawable zone_draw;
  
  MZone(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "zone", _bloc); }
  void init() {
	  corner_pos = newVec("corner_pos");
	  if (!loading_from_bloc) corner_pos.set(ref_size*3.0F, ref_size*3.0F);
  }
  void build_param() {
	  build_normal();
  }
  void build_normal() {
	  Macro_Element e = addEmptyS(0);
	  
	  zone_widg = e.addModel("Field");
	  zone_widg.setPosition(ref_size * 2 / 16, ref_size * 2 / 16).setPassif();
	  
	  corner_pos.addEventChange(new nRunnable() { public void run() {
		  zone_widg.setSize(corner_pos.get().x, 	corner_pos.get().y); }});
	  
	  zone_draw = new Drawable(gui.drawing_pile, 0) { public void drawing() {
		  gui.app.noFill(); gui.app.stroke(220);
		  gui.app.rect(	zone_widg.getX(), 	zone_widg.getY(), 
				  		corner_pos.get().x+corner_widg.getSX(), 	
				  		corner_pos.get().y+corner_widg.getSY());
	  } };
	  zone_widg.setDrawable(zone_draw);
	  
	  corner_widg = e.addLinkedModel("MC_SpeTrig");
	  corner_widg.alignUp().alignLeft();
	  corner_widg.setLinkedValue(corner_pos);
  }
  public MZone clear() {
    super.clear(); 
    zone_draw.clear();
    return this; }
  public MZone toLayerTop() {
    super.toLayerTop(); 
    return this; }
}

class MCursor extends MBasic { 
  static class MCursor_Builder extends MAbstract_Builder {
    MCursor_Builder(Macro_Main m) { super("cursor", "Cursor", "add a cursor", "Sheet"); 
	  first_start_show(m); }
  MCursor build(Macro_Sheet s, sValueBloc b) { MCursor m = new MCursor(s, b); return m; }
  }
  public nCursor cursor;
    public sVec pval = null;
    public sVec dval = null;
    public sBoo show = null;
    public sVec mval;
  nRunnable sheet_grab_run, pval_run, movingchild_run;
  Macro_Connexion in, out;
  Macro_Connexion out_link;
  MCursor(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cursor", _bloc); }
  void init_cursor() {
	  cursor = new nCursor(mmain(), value_bloc, true);
	  mmain().cursors_list.add(cursor);
	  sheet.sheet_cursors_list.add(cursor);
	  sheet.cursor_count++;
	  
	  if (!(pval.x() == 0 && pval.y() == 0)) {
	    setPosition(pval.get().x - sheet.grabber.getX(), 
	        pval.get().y - sheet.grabber.getY());
	    moving(); 
	  }
	  
	  cursor.addEventClear(new nRunnable(cursor) { public void run() { 
		  cursor = null;
	      sheet.sheet_cursors_list.remove(((nCursor)builder));
	    mmain().cursors_list.remove(((nCursor)builder)); 
	    sheet.cursor_count--;
	  }});
	  
	  grab_pos.addEventChange(new nRunnable() { public void run() {
	      if (cursor != null && cursor.pval!= null) cursor.pval.set(
	          grab_pos.get().x + sheet.grabber.getX(), 
	          grab_pos.get().y + sheet.grabber.getY());
	  } });
  	pval_run = new nRunnable() { public void run() {
		if (sheet != mmain())
			setPosition(cursor.pval.get().x - sheet.grabber.getX(), 
	          cursor.pval.get().y - sheet.grabber.getY());
		else setPosition(cursor.pval.get().x, cursor.pval.get().y);
		moving();
		  
		if (out != null && cursor != null) out.send(cursor.pval.asPacket());
	}};
	cursor.pval.addEventChange(pval_run);
	
	movingchild_run = new nRunnable() { public void run() { moving(); } };
	  
	if (sheet != mmain()) {
		sheet_grab_run = new nRunnable() { public void run() {
			PVector tm = new PVector(cursor.pval.get().x, cursor.pval.get().y);
		    setPosition(tm.x - sheet.grabber.getX(), 
		                tm.y - sheet.grabber.getY());
		    moving();
		} };
		sheet.grab_pos.addEventAllChange(sheet_grab_run);
	}
    addEmptyS(1);
    in = addInput(0, "move", new nRunnable() { public void run() {
        if (in.lastPack() != null && in.lastPack().isBang()) // && pval != null && mval != null
      	  	cursor.pval.add(mval.get());
    } });
  }

  protected void group_move_custom(float x, float y) {
//	  cursor.pval.set(cursor.pval.get().x + x, 
//			  cursor.pval.get().y + y);
  }
  void build_param() {
	    addEmptyS(0);
	    addEmptyS(1);
	    out_link = addOutput(2, "cursor_link");
	    out_link.set_link();
	  show = newRowBoo(false, "show"); //!!!!! is hided by default
	  pval = newRowVec("pos");
	  dval = newRowVec("dir");
	  mval = newRowVec("mov");
	  init_cursor();
  }
  void build_normal() {
	  show = newBoo(false, "show"); //!!!!! is hided by default
	  pval = newVec("pos");
	  dval = newVec("dir");
	  mval = newRowVec("mov");
	  init_cursor();
	    out_link = addOutput(2, "cursor_link");
	    out_link.set_link();
	    addEmptyS(0);
	    addSwitchS(1, "show", show);
  }
  boolean flag_del = false;
  public MCursor clear() {
	  flag_del = true;
	if (sheet != mmain()) sheet.grab_pos.removeEventAllChange(sheet_grab_run);
	if (pval != null) pval.removeEventChange(pval_run);
    super.clear(); 
    if (cursor != null) cursor.clear();
    return this; }
  public MCursor toLayerTop() {
    super.toLayerTop(); 
    return this; }
}


class MNode extends MBasic { 
  static class Builder extends MAbstract_Builder {
	  Builder(Macro_Main m) { super("node", "Node", "Connections Node", "Sheet"); 
	  first_start_show(m); }
	  MNode build(Macro_Sheet s, sValueBloc b) { MNode m = new MNode(s, b); return m; }
    }
  sInt co_nb; sBoo as_in, as_out, as_label, as_link, is_point;
  Macro_Connexion in_link,out_link; 
  ArrayList<MNode> nodes;
  ArrayList<Macro_Connexion> out_list;
  ArrayList<Macro_Connexion> in_list;
  int in_col = 0; int out_col = 2; int lab_col = 1;
  MNode(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "node", _bloc); }
	public void init() {
		co_nb = newInt(1, "co_nb");
		co_nb.set_limit(1, 8);

		as_in = newBoo(true, "as_in");
		as_out = newBoo(true, "as_out");
		as_label = newBoo(false, "as_label");
		as_link = newBoo(false, "as_link");
		is_point = newBoo(true, "is_point");
		  
		nodes = new ArrayList<MNode>();
		out_list = new ArrayList<Macro_Connexion>();
		in_list = new ArrayList<Macro_Connexion>();
		
		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
			nRunnable change_run = new nRunnable() { public void run() { 
				  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  			  if (!rebuilding) rebuild(); }}); }};
		  	co_nb.addEventChange(change_run);
		  	as_in.addEventChange(change_run); 
		  	as_out.addEventChange(change_run); 
		  	as_in.addEventChange(new nRunnable() { public void run() { if (!as_in.get()) as_out.set(true); }}); 
		  	as_out.addEventChange(new nRunnable() { public void run() { if (!as_out.get()) as_in.set(true); }}); 
		  	as_label.addEventChange(change_run); 
		  	as_link.addEventChange(change_run); 
		}});
		if (RConst.xor(as_in.get(), as_out.get())) {
			if (!as_label.get()) out_col = 0;
			else {
				if (as_in.get()) { in_col = 0; out_col = 1; lab_col = 1; addEmptyS(0); }
				if (as_out.get()) { in_col = 0; out_col = 1; lab_col = 0; addEmptyS(1); }
			}
		} else {
			if (!as_label.get() ) out_col = 1;
			else { out_col = 2; lab_col = 1; if (as_link.get()) addEmptyS(1); }
		}
	}	
	public void build_param() {
		if (is_point.get()) {
			build_normal();
			return;
		}
		build_normal();
		addSSelectToInt(in_col, co_nb).no_mirror();
//		addEmptyS(2).no_mirror();
		addSelectL(out_col, as_in, as_out, as_label, as_link, "in", "out", "label", "link")
			.no_mirror();
	}	
	public void build_normal() {
		if (is_point.get()) {
			build_point();
			return;
		}
		if (as_link.get() && (as_out.get() || as_in.get())) {
			nRunnable link_run;
			if (as_out.get()) {
				in_link = addInput(in_col, "link_in");
				in_link.set_link();
			}
			if (as_in.get()) {
				out_link = addOutput(out_col, "link_out");
				link_run = new nRunnable() { public void run() { update_nodelist(); }};
				out_link.set_link();
				out_link.addEventLink(link_run).addEventUnLink(link_run);
			}
		}
		for (int i = 0 ; i < co_nb.get() ; i++) {
			Macro_Connexion o = null;
			if (as_out.get()) {
			  	if (!as_in.get()) o = addSheetInput(out_col, "out"+(i+1)).connect;
			  	else o = addOutput(out_col, "out"+(i+1));
			  	o.set_undefine();
				out_list.add(o);
			}
			if (as_in.get()) {
			  	nObjectTrio pr = new nObjectTrio();
			  	pr.obj2 = o;
				Macro_Connexion c = null;
				if (!as_out.get()) c = addSheetOutput(in_col, "in"+(i+1)).connect;
				else c = addInput(in_col, "in"+(i+1));
			  	c.set_undefine();
				in_list.add(c);
				if (o != null) c.direct_connect(o);
			  	pr.obj1 = c; pr.obj3 = i;
			  	c.addEventReceive(new nRunnable(pr) { public void run() {
			  		update_nodelist();
					nObjectTrio p = (nObjectTrio)builder;
					int id = (int) p.obj3;
					Macro_Connexion in = (Macro_Connexion) p.obj1;
					for (MNode n : nodes) {
						if (n.as_in.get() && id < n.in_list.size())
							n.in_list.get(id).receive(in, in.lastPack());
						else if (!n.as_in.get() && n.as_out.get() && id < n.out_list.size())
							n.out_list.get(id).send(in.lastPack());
							
					}
				}});
			}
			if (as_label.get()) {
				sStr l = newStr("label"+(i+1), "label"+(i+1));
				nLinkedWidget w = addLinkedSField(lab_col, l);
				if (as_in.get() && as_out.get()) 
					w.setPosition(ref_size*-1.625, ref_size*0.125).setSX(ref_size*5.5);
				else if (as_in.get()) 
					w.setPosition(ref_size*-1.375, ref_size*0.125).setSX(ref_size*3.5);
				else if (as_out.get()) 
					w.setPosition(ref_size*0.125, ref_size*0.125).setSX(ref_size*3.5);
			}
		}
	}

	ArrayList<Macro_Bloc> link_blocs = new ArrayList<Macro_Bloc>();
	void test_connect(Macro_Connexion out) {
		if (out != null) for (Macro_Connexion m : out.connected_inputs) {
			if (m.elem.bloc.val_type.get().equals("node")) {
				MNode n = (MNode)m.elem.bloc;
				if (m.base_info.equals("link_in")) {
					nodes.add((MNode)m.elem.bloc);
					if (n.out_link != null) {
						test_connect(n.out_link);
						for (Macro_Connexion c2 : n.out_link.direct_cos) 
							test_connect(c2);
					}
				} 
				else for (int i = 1 ; i < 9 ; i++) if (m.base_info.equals("in"+i)) {
					for (Macro_Connexion c : m.direct_cos) test_connect(c);
					for (MNode n2 : n.nodes) for (Macro_Connexion c : n2.in_list) {
						if (c.base_info.equals("in"+i)) {
							test_connect(c);
							for (Macro_Connexion c2 : c.direct_cos) test_connect(c2);
						}
					}
				}
			}
			else if (m.elem.bloc.val_type.get().equals("gate")) {
				MGate g = (MGate)m.elem.bloc;
				test_connect(g.get_active_out());
			} else if (m.linkable) {
				link_blocs.add(m.elem.bloc);
//				if (!m.elem.bloc.link_blocs.contains(out.elem.bloc)) 
//					m.elem.bloc.link_blocs.add(out.elem.bloc);
			}
		}
	}
	void update_nodelist() {
		nodes.clear();
		link_blocs.clear();
		if (out_link != null) {
			test_connect(out_link);
			for (Macro_Connexion c : out_link.direct_cos) test_connect(c); 
		}
	}

	//as link angle
	boolean angle_constructor = false;
	MNode(Macro_Sheet _sheet, Macro_Connexion building_co, PVector pos) { 
		super(_sheet, "node", null); 
		if (building_co.linkable) {
			as_link.set(true);
			in_list.get(0).set_link();
		}
//		if (!loading_from_bloc) build_point();
		if (building_co.type == OUTPUT) {
			pos.x -= pos.x%GRID_SNAP_FACT;
			pos.y -= pos.y%GRID_SNAP_FACT;
			setPosition(pos.x-ref_size*0.375F, pos.y-ref_size*1.0F);
			building_co.connect_to(in_list.get(0));
			building_co.end_building_line();
			out_list.get(0).start_building_line();
		} else {
			pos.x -= pos.x%GRID_SNAP_FACT;
			pos.y -= pos.y%GRID_SNAP_FACT;
			setPosition(pos.x-ref_size*1.0F, pos.y-ref_size*1.0F);
			building_co.connect_to(out_list.get(0));
			building_co.end_building_line();
			in_list.get(0).start_building_line();
		}
		angle_constructor = true;
	}
  
	void build_point() {
		hideCtrl();
		if (param_open != null) param_open.setPX(-ref_size * 0.75);
		if (mirror_sw != null) mirror_sw.setPX(-ref_size * 1.25);
//		grabber.hide();
	    Macro_Element ei = new Macro_Element(this, "", "MC_Element_Single", "in1", INPUT, OUTPUT, true);
	      elements.add(ei);
		ei.back.setSX(ref_size*0);
	    ei.drawer_width = ref_size*1.25F;
	    if (ei.sheet_connect != null) ei.connect.direct_connect(ei.sheet_connect);
	    ei.connect.msg_view.setSX(0);
	    if (ei.sheet_connect != null) ei.sheet_connect.msg_view.setSX(0);
	    addElement(0, ei); 
		in_list.add(ei.connect);
	    Macro_Element eo = new Macro_Element(this, "", "MC_Element_Single", "out1", OUTPUT, INPUT, true);
	      elements.add(eo);
	    eo.back.setSX(ref_size*0);
	    eo.drawer_width = ref_size*1.25F;
	    if (eo.sheet_connect != null) eo.sheet_connect.direct_connect(eo.connect);
	    eo.connect.msg_view.setSX(0);
	    if (eo.sheet_connect != null) eo.sheet_connect.msg_view.setSX(0);
	    addElement(0, eo); 
		out_list.add(eo.connect);
	    getShelf(0).removeDrawer(eo);
	    eo.ref.setParent(ei.ref);
		eo.ref.setPX(ref_size*0).setPY(ref_size*0);
		ei.ref.setPX(ref_size*0.75); 
		if (as_link.get()) {
			ei.connect.set_link();
			eo.connect.set_link();
		} 
		ei.connect.direct_connect(eo.connect);
//		if (ei.sheet_connect != null && eo.sheet_connect != null) 
//			ei.sheet_connect.direct_connect(eo.sheet_connect);
		
		if (openning.get() == OPEN) for (Macro_Element e : elements) e.show();
		toLayerTop();

		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
			if (!loading_from_bloc && !angle_constructor) {
				is_point.set(false);
				if (!rebuilding) rebuild();
			} else {
//				if (	out_list.get(0).connected_inputs.size() > 0 && 
//						in_list.get(0).connected_outputs.size() > 0 ) reduc();
//				else {
//					//auto close after co
//				}
			}
		}});
	}
	public MNode clear() {
		super.clear(); 
		return this; }
}



class MSheetBloc extends MBasic { 
	static class MSheetMain_Builder extends MAbstract_Builder {
		MSheetMain_Builder() { 
			super("sheetmain", "Old - SheetBloc", "sheet tools", "Old"); }
		MSheetBloc build(Macro_Sheet s, sValueBloc b) { MSheetBloc m = new MSheetBloc(s, b); return m; }
	}
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { 
		super("sheetbloc", "SheetBloc", "sheet tools", "Sheet"); 
		first_start_show(m); }
	MSheetBloc build(Macro_Sheet s, sValueBloc b) { MSheetBloc m = new MSheetBloc(s, b); return m; }
}
	Macro_Element menu_elem;
	Macro_Connexion in_menu;
	nLinkedWidget stp_view; sBoo menu_open, menu_reduc; sVec menu_pos; sInt menu_tab;
	nRunnable grab_run, reduc_run, close_run, tab_run;
	  nRunnable goto_run;
	  Macro_Connexion out_sheet;//, out_obj;
	  sBoo show1, show2, show3, show4;
	MSheetBloc(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "sheetbloc", _bloc); 
//		unclearable = true; 
		if (sheet == mmain() && mmain().main_sheetbloc != null) { clear(); return; }
		if (sheet == mmain()) mmain().main_sheetbloc = this;
	}
	void init() {
	    menu_open = newBoo("stp_snd", "stp_snd", false);
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
	    close_run = new nRunnable() { public void run() { 
	    		menu_open.set(false); } };
	}
	void init_end() {
		super.init_end();
		if (sheet != mmain() && !loading_from_bloc && menu_elem != null) { //
			sheet.add_spot("left", menu_elem);
			sheet.add_spot("right", out_sheet.elem);
		}

	    if (menu_open.get()) mmain().inter.addEventTwoFrame(new nRunnable() { public void run() {
	    		menu(); } });
	}
	void build_param() {
		menu_elem = addEmptyS(1);
		menu_elem.addTrigS("menu", new nRunnable() { public void run() {
		    		menu(); } })
		    .setInfo("open sheet general menu");
		menu_elem.addLinkedMiniSwitch("st", menu_open);
	    addInputBang(0, "open menu", new nRunnable() { public void run() { 
	    		mmain().inter.addEventTwoFrame(new nRunnable() { public void run() {
	    		menu(); } }); } });
	    out_sheet = addOutput(2, "sheet link");
	    out_sheet.set_link();
//		menu_elem.addTrigS("get obj", new nRunnable() { public void run() {
//			out_obj.send(Macro_Packet.newPacketObject(sheet)); } })
//		.setInfo("get object");
//	    out_obj = addOutput(2, "object");

	    addEmptyS(2); 
	    addTrigS(0, "show", "show all sheet cursor", new nRunnable() { public void run() {
	    		for (nCursor c : sheet.sheet_cursors_list) c.show.set(true);
	    } });
	    addTrigS(1, "hide", "hide all sheet cursor", new nRunnable() { public void run() {
    			for (nCursor c : sheet.sheet_cursors_list) c.show.set(false);
	    } });
    		
	    addEmptyS(1); addEmptyS(2); 
	    Macro_Element m = 
	    addEmptyXL(0);
	    m.addSwitchXLSelector(1, "view", "show view access in normal view", show1);
	    if (sheet != mmain()) m.addSwitchXLSelector(2, "open", "show openning control in normal view", show2);
	    m.addSwitchXLSelector(3, "self", "show selh object access in normal view", show3);
	    if (sheet != mmain()) m.addSwitchXLSelector(4, "prio", "show sheet priority control in normal view", show4);
	    
	    addTrigS(1, "view", goto_run).setInfo("view full sheet");
	    addInputBang(0, "view", new nRunnable() { public void run() { goto_run.run(); }});
	    addEmptyS(2);
	    
	    if (sheet != mmain()) {
	    addInputBang(0, "Deploy Sheet", 
	    		new nRunnable() { public void run() { sheet.deploy(); }});
	    addInputBang(1, "Open Sheet", 
	    		new nRunnable() { public void run() { sheet.open(); }});
	    addInputBang(2, "Reduc Sheet", 
	    		new nRunnable() { public void run() { sheet.reduc(); }});
	    }
	    newRowProtectedValue(sheet.val_self);
	    if (sheet != mmain()) newRowValue_Pan(sheet.priority);

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
//		menu_elem.addTrigS("get obj", new nRunnable() { public void run() {
//			out_obj.send(Macro_Packet.newPacketObject(sheet)); } })
//		.setInfo("get object");
//	    out_obj = addOutput(2, "object");

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
	    if (show2.get() && sheet != mmain()) {
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
	    if (show4.get() && sheet != mmain()) {
	    		newRowValue_Pan(sheet.priority);
	    }
	}
	void menu() {
	    menu_open.set(true);
	    sheet.build_sheet_menu();
	    if (sheet.sheet_front != null) { 
		    if (menu_pos.get().x == 0 && menu_pos.get().y == 0) grab_run.run();
		    if (menu_open.get()) {
				if (!(menu_pos.get().x == 0 && menu_pos.get().y == 0)) 
					sheet.sheet_front.grabber.setPosition(menu_pos.get());
				if (menu_reduc.get()) sheet.sheet_front.collapse(); else sheet.sheet_front.popUp();
				sheet.sheet_front.setTab(menu_tab.get());
		    }
//			setup_send.set(true);
			sheet.sheet_front.grabber.addEventDrag(grab_run); 
			sheet.sheet_front.addEventTab(tab_run)
			.addEventCollapse(reduc_run).addEventClose(close_run);  
	    }
		grab_run.run(); tab_run.run(); reduc_run.run(); 
	}
	public MSheetBloc clear() {
	    if (sheet.sheet_front != null) sheet.sheet_front.grabber.removeEventDrag(grab_run);
	    if (sheet.sheet_front != null) sheet.sheet_front.removeEventCollapse(reduc_run); 
	    if (sheet.sheet_front != null) sheet.sheet_front.removeEventClose(close_run); 
	    if (sheet.sheet_front != null) sheet.sheet_front.clear();
	    if (mmain().main_sheetbloc == this) mmain().main_sheetbloc = null; 
		super.clear(); 
		return this; }
	public MSheetBloc toLayerTop() {
		super.toLayerTop(); 
		return this; }
}


//class MPack extends MBasic { 
//static class Builder extends MAbstract_Builder {
//	  Builder() { super("pack", "Pack", "Packing links", "Sheet"); }
//	  MPack build(Macro_Sheet s, sValueBloc b) { MPack m = new MPack(s, b); return m; }
//  }
//sInt co_nb;
//Macro_Connexion link; 
//ArrayList<MPack> packs;
//ArrayList<Macro_Connexion> connects;
//
//MPack(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "pack", _bloc); }
//	public void init() {
//		co_nb = newInt(2, "co_nb");
//		addSSelectToInt(0, co_nb);
//		co_nb.set_limit(2, 8);
//		packs = new ArrayList<MPack>();
//		connects = new ArrayList<Macro_Connexion>();
//	}	
//	public void build_param() {
//		link = addInput(0, "link");
//		nRunnable link_run = new nRunnable() { public void run() {packs.clear();
//		for (Macro_Connexion m : link.connected_inputs) 
//			if (m.elem.bloc.val_type.get().equals("pack")) 
//				packs.add((MPack)m.elem.bloc);
//		if (link.direct_co != null)
//			for (Macro_Connexion m : link.direct_co.connected_inputs) 
//				if (m.elem.bloc.val_type.get().equals("pack")) 
//					packs.add((MPack)m.elem.bloc);
//		}};
//		link.set_link().addEventLink(link_run).addEventUnLink(link_run);
//		for (int i = 0 ; i < co_nb.get() ; i++) {
//			Macro_Connexion c = addOutput(0, "out"+(i+1));
//			connects.add(c);
//		}
//		finish();
//	}	
//	public void build_normal() {
//		link = addOutput(0, "link");
//		nRunnable link_run = new nRunnable() { public void run() {packs.clear();
//		for (Macro_Connexion m : link.connected_inputs) 
//			if (m.elem.bloc.val_type.get().equals("pack")) 
//				packs.add((MPack)m.elem.bloc);
//		if (link.direct_co != null)
//			for (Macro_Connexion m : link.direct_co.connected_inputs) 
//				if (m.elem.bloc.val_type.get().equals("pack")) 
//					packs.add((MPack)m.elem.bloc);
//		}};
//		link.set_link().addEventLink(link_run).addEventUnLink(link_run);
//		for (int i = 0 ; i < co_nb.get() ; i++) {
//			Macro_Connexion c = addInput(0, "in"+(i+1));
//		  	nObjectPair pr = new nObjectPair();
//		  	pr.obj1 = c; pr.obj2 = i;
//		  	c.addEventReceive(new nRunnable(pr) { public void run() {
//
//				packs.clear();
//				for (Macro_Connexion m : link.connected_inputs) 
//					if (m.elem.bloc.val_type.get().equals("pack")) 
//						packs.add((MPack)m.elem.bloc);
//				if (link.direct_co != null)
//					for (Macro_Connexion m : link.direct_co.connected_inputs) 
//						if (m.elem.bloc.val_type.get().equals("pack")) 
//							packs.add((MPack)m.elem.bloc);
//				for (MPack p : packs) 
//					p.connects.get((int) ((nObjectPair)builder).obj2).send(
//						((Macro_Connexion) ((nObjectPair)builder).obj1).lastPack());
//			}});
//			connects.add(c);
//		}
//		finish();
//	}
//	public void finish() {
//		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//		co_nb.addEventChange(new nRunnable() { public void run() { 
//			  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//	  			  if (!rebuilding) rebuild(); }}); }}); }});
//	}
//public MPack clear() {
//  super.clear(); 
//  return this; }
//public MPack toLayerTop() {
//  super.toLayerTop(); 
//  return this; }
//}
//
//
//
//class MMPoints extends MBasic { 
//static class Builder extends MAbstract_Builder {
//	  Builder() { super("points", "MultiPoint", "", "Sheet"); }
//	  MMPoints build(Macro_Sheet s, sValueBloc b) { MMPoints m = new MMPoints(s, b); return m; }
//  }
//sInt co_nb;
//ArrayList<Macro_Connexion> out_list;
//ArrayList<Macro_Connexion> in_list;
//
//MMPoints(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "points", _bloc); }
//	public void init() {
//		co_nb = newInt(2, "co_nb");
//		co_nb.set_limit(2, 8);
//		out_list = new ArrayList<Macro_Connexion>();
//		in_list = new ArrayList<Macro_Connexion>();
//	}	
//	public void build_param() {
//		addSSelectToInt(0, co_nb);
//		addEmptyS(1);
//		addEmptyS(2);
//		build_normal();
//	}	
//	public void build_normal() {
//		finish();
//	}
//	public void finish() {
//		for (int i = 0 ; i < co_nb.get() ; i++) {
//			Macro_Connexion c = addInput(0, "in"+(i+1));
//			in_list.add(c);
//		  	Macro_Connexion o = addOutput(2, "out"+(i+1));
//			out_list.add(o);
//		  	nObjectPair pr = new nObjectPair();
//		  	pr.obj1 = c; pr.obj2 = o;
//		  	c.addEventReceive(new nRunnable(pr) { public void run() {
//		  		nObjectPair p = (nObjectPair)builder;
//		  		if (((Macro_Connexion)p.obj1).lastPack() != null) 
//		  			((Macro_Connexion)p.obj2)
//		  			.send(((Macro_Connexion)p.obj1).lastPack());
//			}});
//			sStr l = newStr("label"+(i+1), "label"+(i+1));
//			addLinkedSField(1, l).setPX(ref_size*-1.8).setSX(ref_size*5.5);
//		}
//		
//		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//		co_nb.addEventChange(new nRunnable() { public void run() { 
//			  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//	  			  if (!rebuilding) rebuild(); }}); }}); }});
//	}
//public MMPoints clear() {
//  super.clear(); 
//  return this; }
//public MMPoints toLayerTop() {
//  super.toLayerTop(); 
//  return this; }
//}
//
//
//
//class MPoint extends MBasic { 
//	static class MPoint_Builder extends MAbstract_Builder {
//		MPoint_Builder() { super("point", "Point", "connection node", "Sheet"); }
//    	MPoint build(Macro_Sheet s, sValueBloc b) { MPoint m = new MPoint(s, b); return m; }
//  }
//	Macro_Connexion in, out; 
//	sStr val_label;
//	MPoint(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "point", _bloc); }
//	public void init() {
//		val_label = newStr("val_label", "val_label");
//	}	
//	public void build_param() {
//		in = addInput(0, "in", new nRunnable() {public void run() { 
//  			if (in.lastPack() != null) out.send(in.lastPack()); }});
//		out = addOutput(2, "out");
//		addLinkedSField(1, val_label).setPX(ref_size*-1.8).setSX(ref_size*5.5);
//	}	
//	public void build_normal() {
//		in = addInput(0, "in", new nRunnable() {public void run() { 
//  			if (in.lastPack() != null) out.send(in.lastPack()); }});
//		out = addOutput(0, "out");
//	}	
//public MPoint clear() {
//  super.clear(); 
//  return this; }
//public MPoint toLayerTop() {
//  super.toLayerTop(); 
//  return this; }
//}
//
//
//
//
//class MSheetCo extends MBasic {
//	static class MSheetCo_Builder extends MAbstract_Builder {
//		MSheetCo_Builder() { 
//			super("co", "Sheet Co", "connexions with sheet parent", "Sheet"); }
//		MSheetCo build(Macro_Sheet s, sValueBloc b) { MSheetCo m = new MSheetCo(s, b); return m; }
//	}
//Macro_Element elem;
//nLinkedWidget view;
//sStr val_view, links_save;
//
//MSheetCo(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "co", _bloc); }
//public void init() {
//  val_view = newStr("val", "val", "");
//}
//public void build_param() {
//  view = addEmptyS(0).addLinkedModel("MC_Element_SField");
//  view.setLinkedValue(val_view);
//  val_view.addEventChange(new nRunnable() { public void run() { 
//      if (sheet != mmain()) elem.sheet_connect.setInfo(val_view.get());
//      elem.connect.setInfo(val_view.get());
//  } });
//  elem = addSheetOutput(1, "out");
//  if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
//  elem.connect.setInfo(val_view.get());
//}
//
//public void build_normal() {
//  view = addEmptyS(1).addLinkedModel("MC_Element_SField");
//  view.addEventFieldChange(new nRunnable() { public void run() { 
//    elem.sheet_connect.setInfo(val_view.get());
//  } });
//  view.setLinkedValue(val_view);
//  elem = addSheetInput(0, "in");
//  if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
//}
//
//public MSheetCo clear() {
//  super.clear(); return this; }
//}

