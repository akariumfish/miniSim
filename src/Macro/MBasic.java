package Macro;

import java.util.ArrayList;

import UI.nCtrlWidget;
import UI.nWidget;
import processing.core.PApplet;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sStr;
import sData.sValueBloc;

public class MBasic extends Macro_Bloc { 
  static class Builder extends MAbstract_Builder {
	  Builder(Macro_Main m) { super("base", "Base", "Base Bloc", "Sheet"); }
	  MBasic build(Macro_Sheet s, sValueBloc b) { MBasic m = new MBasic(s); return m; }
    }
  sStr bloc_type;
  nWidget bloc_field;
  boolean builder_mode = false;
  MBasic(Macro_Sheet _sheet) { super(_sheet, "base", "base", null); init_creator(); }
  MBasic(Macro_Sheet _sheet, PVector pos) {  // for adding at mouse position
	  super(_sheet, "base", "base", null); 
      gui.in.do_shortcut = false;
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
    gui.in.do_shortcut = true;
    if (mmain().active_construct == this) mmain().active_construct = null;
    return this; }
  
  Macro_Element elem_com;
  
  sBoo param_view, mirror_view;
  sStr links_save;
  
  nCtrlWidget param_ctrl;
  nRunnable pview_run, link_change_run;
  nRunnable run_rebuild;
  
  boolean rebuilding = false;
  Macro_Abstract rebuild_as = null;
  MBasic(Macro_Sheet _sheet, String t, sValueBloc _bloc) { 
    super(_sheet, t, _bloc);
    basic_constructor(); }
  MBasic(Macro_Sheet _sheet, String t, sValueBloc _bloc, String spe) { 
    super(_sheet, t, _bloc, spe);
    basic_constructor(); }
  void basic_constructor() {
    builder_mode = true;
    links_save = newStr("links_save", "links_save", "");
    link_change_run = new nRunnable() {public void run() {
		  save_co_links(); } };
    param_view = newBoo("com_param_view", "com_param_view", false);
	mirror_view = newBoo("mirror_view", "mirror_view", false);
//		param_view.addEventChange(new nRunnable() { public void run() { 
//		}});
//		mirror_view.addEventChange(new nRunnable() { public void run() { 
//			save_co_links();
//			for (Macro_Element m : elements)
//				  m.mirror(mirror_view.get());
//		}});
    if (!param_view.get()) get_mirror().setRunnable(new nRunnable() { public void run() { 
    		mirror_view.set(!mirror_view.get());
//	    		save_co_links();
    		for (Macro_Element m : elements)
    			  m.mirror(mirror_view.get());
//	          rebuild();
    } });
	param_ctrl = get_param_openner().setRunnable(new nRunnable() { public void run() { 
        param_view.set(!param_view.get());
//	        if (param_view.get()) param_ctrl.setText("N").setInfo("hide param");
//	        else param_ctrl.setText("P").setInfo("show param");
        rebuild();
    } });
    if (param_view.get()) param_ctrl.setText("N").setInfo("hide param");
    else param_ctrl.setText("P").setInfo("show param");
    event_initend = new ArrayList<nRunnable>();
    event_buildparam = new ArrayList<nRunnable>();
    event_buildnorm = new ArrayList<nRunnable>();
    run_rebuild = new nRunnable() { public void run() {
    		mmain().inter.addEventTwoFrame(new nRunnable() { public void run() { 
    			rebuild(); }}); }};
    init();
    if (param_view.get()) {
    		build_param();
    		if (event_buildparam != null) nRunnable.runEvents(event_buildparam);
    } else {
    		build_normal();
    		if (event_buildnorm != null) nRunnable.runEvents(event_buildnorm); }
  }
  void init() { ; }
  void build_param() { }//addEmptyS(0); addEmptyS(1); }
  void build_normal() { }//addEmptyS(0); addEmptyS(1); }

  ArrayList<nRunnable> event_initend = null;
  ArrayList<nRunnable> event_buildparam = null;
  ArrayList<nRunnable> event_buildnorm = null;
  void addToInitend(nRunnable r) { event_initend.add(r); }
  void removeToInitend(nRunnable r) { event_initend.remove(r); }
  void addToBuildParam(nRunnable r) { event_buildparam.add(r); }
  void removeToBuildParam(nRunnable r) { event_buildparam.remove(r); }
  void addToBuildNorm(nRunnable r) { event_buildnorm.add(r); }
  void removeToBuildNorm(nRunnable r) { event_buildnorm.remove(r); }
  
  void init_end() {
	  super.init_end();
	  mmain().inter.addEventNextFrame(new nRunnable() {public void run() {
		  if (event_initend != null) nRunnable.runEvents(event_initend); }});
	  
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
				  String[] e = PApplet.splitTokens(d, LINK_TOKEN);
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
			links_save.add(c.elem.bloc.value_bloc.ref + LINK_TOKEN + c.elem.descr + INFO_TOKEN);
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
      rebuild_as = sheet.addByValueBloc(v_bloc); 
      rebuild_as.init_end();
      mmain().szone_clear_select();
      for(Macro_Abstract m : prev_selected) m.szone_select();
      if (was_select) rebuild_as.szone_select();
    }
  }
  
  
  
  public MBasic toLayerTop() {
    super.toLayerTop(); 
    return this; }
}




abstract class MBaseTick extends MBasic { 
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

abstract class MBaseMT extends MBaseMenu { 
	sBoo global_tick;
	MBaseMT(Macro_Sheet _sheet, String t, sValueBloc _bloc) { super(_sheet, t, _bloc); }
	void init() { 
		super.init(); 
		global_tick = newBoo(true, "global_tick");
		mmain().baseMticked_list.add(this); }
	void build_param() { super.build_param(); }
	void build_normal() { super.build_normal(); }
	void init_end() { super.init_end(); }
	void rebuild() { super.rebuild(); }
	void tick_strt() {}
	abstract void tick();
	void tick_end() {}
	void receive_tick_strt() { if (global_tick.get()) tick_strt(); }
	void receive_tick() { if (global_tick.get()) tick(); }
	void receive_tick_end() { if (global_tick.get()) tick_end(); }
	abstract void reset();
	void receive_reset() { if (global_tick.get()) reset(); }
	public MBaseMT toLayerTop() { super.toLayerTop(); return this; }
	public MBaseMT clear() { super.clear(); mmain().baseMticked_list.remove(this); return this; }
}
