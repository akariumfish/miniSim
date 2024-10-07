package Macro;

import RApplet.*;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;
import z_old_specialise.Canvas;
import z_old_specialise.Sheet_Specialize;
import z_old_specialise.Simulation;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/*


  

//connexion > element has method to send + runnable for receive
//  circle, hard outline, transparent, mode in or out, exist in a sheet, has an unique number
//  has a label with no back for a short description and a field acsessible or not for displaying values
//  the label and values are aligned, either of them can be on the left or right
//  the connexion circle is on the left right top or down side center of
//    the rectangle formed by the label and values
//  priority button
//    2 round button on top of eachother on left top corner of the connect
//    1 round widget covering half of each button with the priority layer
//  highlight connectable in when creating link
//  package info on top of connections
//
//element > drawer
//  has a text pour l'info bulle
//  is a rectangle without back who can hold different function :
//    button trigger / switch > runnable
//    label for info or values > element has method to set
//    selector : multi switch exclusives or not > runnable
//    jauge and graph? 
//    connexion
//    
//abstract extend shelfpanel
//  can be selected and group dragged copy/pasted > template or deleted
//
//bloc extend abstract
//  shelfpanel of element
//  methods to add and manipulate element for easy macro building
//  show directly connected in/out to detect loop more easily 
//    (cad show that an in will directly send through an out of his bloc when receiving)
//    use 2 axis aligned lines following elements outlines from connexions to connexions
//  
//sheet extend abstract
//  extended to make Simulation and communitys
//  methods for creating blocs inside
//    create in grid around center
//  can build a menu with all value manipulable with easy drawer
//    can choose drawer type when creating value
//    can set value limits
//  has spot for blocs to display when reducted
//
//main
//  is a sheet without grabber and with panel snapped to camera all time
//  is extended to interface ? so work standalone with UI
//  dont show soft back
//  sheet on the main sheet can be snapped to camera, 
//    they will keep their place and size and show panel content
//    only work when not deployed
//  dedicted toolpanel on top left of screen
//
//
//
//Template :
//  -save to template sValueBloc
//    popup for name with field and ok button ?
//  -paste last template (or one selected in menu) in selected sheet, 
//    if no macro group was selected when created it will copy sheet selected at creation, 
//    otherwise it will copy the group of blocs and sheets who was selected
//  bloc for auto saving/loading template by name?
//    macro can create macro !!!! > basic bloc create
//
//preset :
//  -save to preset sValueBloc
//    popup for name with field and ok button ?
//  bloc for auto saving/loading preset by name?
//  saving partial preset, some value marqued as unsavable
//    some value choosen to be ignored
//
//basic bloc :
//  data, var, random,
//  calc, comp, bool, not, bin,
//  trigg, switch, keyboard, gate, delay/pulse
//
//complexe bloc : ( in another menu ? )
//  template management
//    template choosen by name added to selected sheet on bang
//  preset save / load
//  sheet selector : select sheet choosen by name on bang
//  pack / unpack > build complex packet
//  setreset, counter, sequance, multigate 
//  
//MData : sValue access : only hold a string ref, search for corresponding svalue inside current sheet at creation
//  ?? if no value is found create one ??
//  has in and out
//  out can send on change or when receiving bang
//  if it cible a vec, the bloc can follow the corresponding position 
//
//MDataCtrl : sValue ctrl : only in + value view
//  in can change value multiple way
//    bool : set / switch
//    num : set / mult / add
//    vec : set / mult / add for values rotation and magnitude
//    tmpl : in bang > build in same sheet / parent sheet
//  
//MVar : when a packet is received, display and store it, send it when a bang is received
//  can as disable bool input
//  
//*/
///*
//
// 
//             DESIGN
//     !! MACRO ARE CRYSTALS !!
// 
//   hide labels! 
//   forme carre > plus petit possible
//   overlapp rectangles with those under them to show solidarity
// 
// 
//   GUI to build
//     widget jauge / graph
// 
//     text asking popup
//       build it
//       call it.popup
//       will respond with a runnable
//       
// */
//
//
//

/*
main
 is a sheet without UI
 
 dont show soft back
 
 sheet on the main sheet can be snapped to camera, 
 they will keep their place and size and show panel content
 only work when not deployed
 
 dedicated toolpanel on top left of screen
 has button :
 -delete selected blocs
 -save/paste template
 -drop down for basic macro
 -menu: see and organise template and sheet (goto sheet)
 
 
 */

public class Macro_Main extends Macro_Sheet { 
  nToolPanel macro_tool, build_tool;//, sheet_tool
  public nExplorer template_explorer;
nExplorer sheet_explorer;
  sValueBloc pastebin = null;
  
  public ArrayList<nExplorer> presets_explorers = new ArrayList<nExplorer>();
  
  public nWidget close_bp;
  
  void build_macro_menus() {
	  
	  
	  
    if (macro_tool != null) macro_tool.clear();
    macro_tool = new nToolPanel(screen_gui, ref_size, 0.0F, true, true);
    
    float ht = 0.875F;
    nDrawer last_drawer = macro_tool.addShelf().addDrawer(4.5F, ht)
    		.addLinkedModel("Menu_Bar_Small_Outline-Mm-P1", "M")
  		  .setLinkedValue(show_macro)
  		  .setInfo("show/hide macros").setFont((int)(ref_size/1.9)).getDrawer()
        .addLinkedModel("Menu_Bar_Small_Outline-Mm-P2", "P")
          .setLinkedValue(do_packet)
          .setInfo("do packet processing").setFont((int)(ref_size/1.9)).getDrawer()
        .addLinkedModel("Menu_Bar_Small_Outline-Mm-P3", "f")
          .setLinkedValue(show_frmrt)
          .setInfo("show/hide framerate").setFont((int)(ref_size/1.9)).getDrawer()
        .addLinkedModel("Menu_Bar_Small_Outline-Mm-P4", "i")
          .setLinkedValue(show_info)
          .setInfo("show/hide infos").setFont((int)(ref_size/1.9)).getDrawer()
        .addLinkedModel("Menu_Bar_Small_Outline-Mm-P5", "l")
          .setLinkedValue(show_link)
          .setInfo("show/hide links").setFont((int)(ref_size/1.9)).getDrawer()
        .addLinkedModel("Menu_Bar_Small_Outline-Mm-P6", "lv")
          .setLinkedValue(link_volatil)
          .setInfo("show hided links when hovering connexions")
          .setFont((int)(ref_size/1.9)).getDrawer()
          .getShelfPanel()
      .addShelf().addDrawer(0.01F, ht).getShelfPanel()
      .addShelf().addDrawer(9.9F, ht)
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P1", "Del")
          .setRunnable(new nRunnable() { public void run() { del_selected(); }})
          .setInfo("Delete selected bloc  -  d").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P2", "X")
          .setRunnable(new nRunnable() { public void run() { cut_run.run(); }})
          .setInfo("Cut selected blocs  -  x").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P3", "C")
          .setRunnable(new nRunnable() { public void run() { copy_to_tmpl(); }})
          .setInfo("Copy selected blocs  -  c").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P4", "V")
          .setRunnable(new nRunnable() { public void run() { pastebin_tmpl(); }})
          .setInfo("Paste  -  v").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P5", "Dup")
          .setRunnable(new nRunnable() { public void run() { duplic_run.run(); }})
          .setInfo("Duplicate selected blocs  -  w").setFont((int)(ref_size/2.5)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P6", "All")
          .setRunnable(new nRunnable() { public void run() { selall_run.run(); }})
          .setInfo("Selecte / Unselect all sheet blocs  -  a").setFont((int)(ref_size/2.5)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P7", "<>")
          .setRunnable(new nRunnable() { public void run() { reduc_run.run(); }})
          .setInfo("Switch selected blocs openning  -  s").setFont((int)(ref_size/2.5)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P8", "QS")
          .setRunnable(new nRunnable() { public void run() { inter.full_data_save(); }})
          .setInfo("Quick Save").setFont((int)(ref_size/2.5)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P9", "QL")
          .setRunnable(new nRunnable() { public void run() { inter.setup_load(); }})
          .setInfo("Quick Load").setFont((int)(ref_size/2.5)).getDrawer() 
          .getShelfPanel()
      .addShelf().addDrawer(0.0F, ht).getShelfPanel() 
      .addShelf().addDrawer(2.0F, ht)
	    .addCtrlModel("Menu_Bar", "MAIN")
	    	  .setRunnable(new nRunnable() { public void run() { 
	    	    if (main_sheetbloc != null) main_sheetbloc.menu();
	    	    else build_sheet_menu(); }})
	    	  .setSX(ref_size*2.0F).setSY(ref_size*0.75F)
	    	  .setPX(ref_size*0).setPY(ref_size*0.0625F)
	      .getShelfPanel()
      .addShelf().addDrawer(0.0F, ht).getShelfPanel()
      .addShelf().addDrawer(5.5F, ht)
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P1", "MG")
          .setRunnable(new nRunnable() { public void run() { inter.filesManagement(); }})
          .setInfo("Management").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P2", "ST")
          .setRunnable(new nRunnable() { public void run() { inter.save_to(); }})
          .setInfo("Save to").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P3", "OP")
          .setRunnable(new nRunnable() { public void run() { inter.quick_open(); }})
          .setInfo("Open").setFont((int)(ref_size/1.9)).getDrawer()
        .addCtrlModel("Menu_Bar_Downlight_Outline-M-P4", "FS")
          .setRunnable(new nRunnable() { public void run() { inter.full_screen_run.run(); }})
          .setInfo("Switch Fullscreen").setFont((int)(ref_size/1.9)).getDrawer();
//    
    close_bp = last_drawer.addCtrlModel("Menu_Bar_Downlight_Outline-M-P5", "X")
          .setRunnable(new nRunnable() { public void run() { gui.app.exit(); }})
          .setInfo("Exit")
          .setStandbyColor(app.color(175, 20, 20))
          .setHoveredColor(app.color(200, 100, 100))
          .setOutlineColor(app.color(125, 10, 10));
    
    if (!show_macro_tool.get()) macro_tool.reduc();
//    macro_tool.setPos(app.window_head);
    macro_tool.addEventReduc(new nRunnable() { public void run() { 
      show_macro_tool.set(!macro_tool.hide); }});
    
    build_buildtool();
    
  }
  void build_buildtool() {
	  int build_row = 24;
	  nShelf sh = null;
	  int row_count = 0;
//	  int shelf_cible = 0;
	  if (build_tool != null) build_tool.clear();
	  boolean found = false;
	  for (int i = 0 ; i < bloc_builders.size() ; i++) 
	    if (bloc_builders.get(i).show_in_buildtool && bloc_builders.get(i).visible) 
	    	found = true;
	  for (Sheet_Specialize t : Sheet_Specialize.prints) 
		if (!t.unique && t.visible && t.show_in_buildtool) { found = true;  }
	  if (found) {
	    build_tool = new nToolPanel(screen_gui, ref_size, 0.125F, true, true);
	    sh = build_tool.addShelf();
	    for (int i = 0 ; i < bloc_builders.size() ; i++) 
	    if (bloc_builders.get(i).show_in_buildtool && bloc_builders.get(i).visible) {
		    	row_count++;
		    	if (row_count > build_row) {
	//	    		shelf_cible++; 
		    		row_count = 0;
		    		sh = build_tool.addShelf();
		    	}
		      sh.addDrawer(3F, 0.55F).addCtrlModel("Menu_Button_Small_Outline-S3/0.6", bloc_builders.get(i).title)
		        .setRunnable(new nRunnable(bloc_builders.get(i).type) { public void run() { 
		          selected_sheet.addByType(((String)builder)); }})
		        .setFont((int)(ref_size/2)).setTextAlignment(PConstants.LEFT, PConstants.CENTER)
		        .setInfo(bloc_builders.get(i).descr)
		        ;
	    }
//	    sh.addSeparator(0.25);
	    for (Sheet_Specialize t : Sheet_Specialize.prints) 
	    if (!t.unique && t.visible && t.show_in_buildtool) {
	    		row_count++;
			if (row_count > build_row) {
//				shelf_cible++; 
				row_count = 0;
				sh = build_tool.addShelf();
			}
		    sh.addDrawer(3F, 0.55F)
		    .addCtrlModel("Menu_Button_Small_Outline-S3/0.6", t.name)
		    .setRunnable(new nRunnable(t) { public void run() { 
		      ((Sheet_Specialize)builder).add_new(selected_sheet, null, null); }})
		    .setFont((int)(ref_size/2));
		    ;
	    }
	    for (sValueBloc t : shown_templ_list) {
	    		row_count++;
			if (row_count > build_row) {
//				shelf_cible++; 
				row_count = 0;
				sh = build_tool.addShelf();
			}
		    sh.addDrawer(3F, 0.55F)
		    .addCtrlModel("Menu_Button_Small_Outline-S3/0.6", t.ref)
		    .setRunnable(new nRunnable(t) { public void run() { 
		    		paste_tmpl(((sValueBloc)builder));
		    	}})
		    .setFont((int)(ref_size/2));
		    ;
	    }
	    if (!show_build_tool.get()) build_tool.reduc();
	    build_tool.addEventReduc(new nRunnable() { public void run() { 
	      show_build_tool.set(!build_tool.hide); }});
	    build_tool.setPos(ref_size*0.75);
	}
  }
  public void build_custom_menu(nFrontPanel sheet_front) {
    nFrontTab tab = sheet_front.addTab("Explo");
    tab.getShelf()
      .addSeparator(0.125)
      .addDrawer(10.25, 1).addModel("Label-S3", "sheets explorer :")
      .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
      .addSeparator()
      ;
    sheet_explorer = tab.getShelf()
      .addSeparator()
      .addExplorer()
        .setStrtBloc(value_bloc)
        .addValuesModifier(mmain().inter.taskpanel)
        .addEventChange(new nRunnable() { public void run() { 
            if (value_bloc != sheet_explorer.selected_bloc && 
                sheet_explorer.selected_bloc != null && 
                sheet_explorer.selected_bloc.getBloc("settings") != null &&
                sheet_explorer.selected_bloc.getBloc("settings").getValue("self") != null && 
                sheet_explorer.selected_bloc.getBloc("settings").getValue("type") != null && 
                ((sStr)sheet_explorer.selected_bloc.getBloc("settings").getValue("type")).get().equals("sheet")) {
              Macro_Sheet s = ((Macro_Sheet)((sObj)(sheet_explorer.selected_bloc
                .getBloc("settings").getValue("self"))).get());
              selected_sheet.open();
              if (s != null) s.select();
            } else if (value_bloc == sheet_explorer.explored_bloc) {
              selected_sheet.open();
              select();
            }
        } } )
        ;
        
    tab = sheet_front.addTab("Templ");
    tab.getShelf()
      .addSeparator(0.125)
      .addDrawer(10.25, 1).addModel("Label-S3", "Templates :")
      	.setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
      .addCtrlModel("Button-S2-P3", "Delete").setRunnable(new nRunnable() { public void run() { 
        if (template_explorer.selected_bloc != null) template_explorer.selected_bloc.clear(); 
        template_explorer.update(); } } ).setPX(ref_size*4.875).setInfo("delete selected template").getDrawer()
      .addCtrlModel("Button-S2-P3", "DBdel").setRunnable(new nRunnable() { public void run() { 
        if (template_explorer.selected_bloc != null) 
          remove_from_database(template_explorer.selected_bloc.ref);
        template_explorer.update(); } } ).setPX(ref_size*7.5)
      	.setInfo("remove selected template from database").getShelf()
//      .addDrawer(10.25, 1).addModel("Label-S3", "Special sheet default : base_sheet")
//        .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
      .addSeparator()
      ;
      
    template_explorer = tab.getShelf()
      .addSeparator()
      .addExplorer();
    
    template_explorer.setComplexEntry(new nRunnable() { public void run() { 
		String[] saved_shown_tmpl = PApplet.splitTokens(shown_templ.get(), OBJ_TOKEN);
		shown_templ_list.clear();
		shown_templ.set("");
		
		saved_template.runBlocIterator(
		new nIterator<sValueBloc>() { public void run(sValueBloc m) { 
			for (String t : saved_shown_tmpl) if (t.equals(m.ref)) {
				shown_templ_list.add(m); }
	    }});
	    for (sValueBloc m2 : shown_templ_list) 
	    		shown_templ.set(shown_templ.get() + OBJ_TOKEN + m2.ref);
		
		build_buildtool();
  		template_explorer.explorer_list.start_complexe_entry();
	  	for (sValueBloc s : template_explorer.explorer_blocs) {
	  		nRunnable option_run = new nRunnable(RConst.copy(s.ref)) { public void run() { 
				sValueBloc m = saved_template.getBloc((String)builder);
				if (m != null) {
					if (shown_templ_list.contains(m)) shown_templ_list.remove(m);
					else shown_templ_list.add(m);
				    shown_templ.set("");
				    for (sValueBloc m2 : shown_templ_list) 
				    	shown_templ.set(shown_templ.get() + OBJ_TOKEN + m2.ref);
					build_buildtool();
				}
	  		}};
	  		if (shown_templ_list.contains(s)) 
	  		template_explorer.explorer_list.new_comp_entry(RConst.copy(s.ref))
	  		.setSelectable()
	  		.setOption(true, "", option_run);
	  		else 
	  		template_explorer.explorer_list.new_comp_entry(RConst.copy(s.ref))
	  		.setSelectable()
	  		.setOption(false, "", option_run);
		}
	  	template_explorer.explorer_list.end_complexe_entry();
    } } )
    .setStrtBloc(saved_template)
    .hideValueView()
    .hideGoBack()
    .addEventChange(new nRunnable() { public void run() { 
  		if (saved_template != template_explorer.explored_bloc) {
	  		template_explorer.setStrtBloc(saved_template);
	    		template_explorer.update(); 
	  	}
    } } )
        ;
    tab.getShelf()
      .addSeparator()
      .addDrawer(10, 1)
      .addModel("Label-S3", "New Tmplt Name :").setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
      .addLinkedModel("Field-S3-P2").setLinkedValue(new_temp_name).getShelf()
      .addSeparator(0.125)
      .addDrawer(10, 1)
      .addCtrlModel("Button-S3-P1", "New").setRunnable(new nRunnable() { public void run() { 
        new_tmpl(); template_explorer.update(); } } )
      .setInfo("save selected as new template").getDrawer()
      .addCtrlModel("Button-S3-P2", "Paste").setRunnable(new nRunnable() { public void run() { 
        pastedata_tmpl(); } } )
      .setInfo("add selected template to selected sheet").getShelf()
      .addSeparator()
      ;
    if (pastebin != null) template_explorer.selectEntry(pastebin.ref);
    template_explorer.getShelf()
      .addSeparator(0.25)
        ;
    
    tab = sheet_front.addTab("Blocs");
    tab.getShelf()
      .addSeparator(0.125)
      .addDrawer(10.25, 1).addModel("Label-S3", "Add new bloc to current sheet :       View")
      	.setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
      .addSeparator()
      ;
	  selector_list = tab.getShelf(0)
	    .addBetterList(15, 10, 0.9F);
	  selector_list.setTextAlign(PApplet.LEFT);
	  selector_list.getShelf()
	    .addSeparator(0.25F)
	    ;
	  if (catego == null) catego = new ArrayList<String>();
	  update_bloc_selector_list();
	  
	  sheet_front.addEventClose(new nRunnable(this) { public void run() { 
//		  if (preset_explorer != null) mmain().presets_explorers.remove(preset_explorer); 
		  if (selector_list != null) selector_list.clear(); 
		  if (template_explorer != null) template_explorer.clear(); 
		  if (sheet_explorer != null) sheet_explorer.clear(); 
		  selector_list = null;
		  template_explorer = null;
		  sheet_explorer = null;
	      if (main_sheetbloc != null) main_sheetbloc.menu_open.set(false);
      }});
	  
	  build_tick_menu(sheet_front);
	  build_video_menu(sheet_front);
  }
	ArrayList<String> catego;
  void update_bloc_selector_list() {
	  if (selector_list != null) {
		selector_list.start_complexe_entry();
		selector_list.new_comp_entry("            --- Blocs ---");
	  }
		String[] saved_shown = PApplet.splitTokens(shown_builder.get(), OBJ_TOKEN);
		String[] saved_shown_spe = PApplet.splitTokens(shown_spe.get(), OBJ_TOKEN);
		shown_type_list.clear();
		shown_builder.set("");
		shown_spe_list.clear();
		shown_spe.set("");
		for (MAbstract_Builder m : bloc_builders) {
			boolean found = false;
			for (String t : saved_shown) if (t.equals(m.type)) {
				found = true;
				shown_type_list.add(m);
				m.show_in_buildtool = true; }
			if (!found) m.show_in_buildtool = false;
		} 
	    for (MAbstract_Builder m2 : shown_type_list) 
	    		shown_builder.set(shown_builder.get() + OBJ_TOKEN + m2.type);

		for (Sheet_Specialize m : sheetspe_builders) {
			boolean found = false;
			for (String t : saved_shown_spe) if (t.equals(m.name)) {
				found = true;
				shown_spe_list.add(m);
				m.show_in_buildtool = true; }
			if (!found) m.show_in_buildtool = false;
		} 
	    for (Sheet_Specialize m2 : shown_spe_list) 
	    		shown_spe.set(shown_spe.get() + OBJ_TOKEN + m2.name);
		
		build_buildtool();

		for (MAbstract_Builder m : bloc_builders) {
			String cat = m.category; boolean found = false;
			for (String s : catego) if (s.equals(cat)) found = true;
			if (!found && m.visible) catego.add(cat);
		}

	  if (selector_list != null) {
		for (String cat : catego) {
			if (cat.length() > 0) selector_list.new_comp_entry("     - "+cat+" -");
			else selector_list.new_comp_entry("     - Others -");
			for (MAbstract_Builder m : bloc_builders) 
			if (m.visible && m.category.equals(cat)) {
				selector_list.new_comp_entry(" - "+m.title).setBuilder(m)
				.setSelectable(m.descr, new nRunnable(m) { public void run() {
					selected_sheet.addByType(((MAbstract_Builder)builder).type);
				}})
				.setOption(m.show_in_buildtool, "", new nRunnable(m) { public void run() {
					MAbstract_Builder m = ((MAbstract_Builder)builder);
					m.show_in_buildtool = !m.show_in_buildtool;
					if (m.show_in_buildtool) shown_type_list.add(m);
					else shown_type_list.remove(m);
	
				    shown_builder.set("");
				    for (MAbstract_Builder m2 : shown_type_list) 
				    	shown_builder.set(shown_builder.get() + OBJ_TOKEN + m2.type);
					
					build_buildtool();
				}});
			}
		}
		
		selector_list.new_comp_entry("            --- Specialized Sheet ---");
	    for (Sheet_Specialize m : Sheet_Specialize.prints) if (!m.unique && m.visible) 
			selector_list.new_comp_entry(" - "+m.name).setBuilder(m)
			.setSelectable(new nRunnable(m) { public void run() {
				((Sheet_Specialize)builder).add_new(selected_sheet, null, null); 
			}})
			.setOption(m.show_in_buildtool, "", new nRunnable(m) { public void run() {
				Sheet_Specialize m = ((Sheet_Specialize)builder);
				m.show_in_buildtool = !m.show_in_buildtool;
				if (m.show_in_buildtool) shown_spe_list.add(m);
				else shown_spe_list.remove(m);

			    shown_spe.set("");
			    for (Sheet_Specialize m2 : shown_spe_list) 
			    	shown_spe.set(shown_spe.get() + OBJ_TOKEN + m2.name);
				build_buildtool();
			}});

	     selector_list.end_complexe_entry();
	  }
  }

  ArrayList<MAbstract_Builder> shown_type_list = new ArrayList<MAbstract_Builder>();
  ArrayList<Sheet_Specialize> shown_spe_list = new ArrayList<Sheet_Specialize>();
  ArrayList<sValueBloc> shown_templ_list = new ArrayList<sValueBloc>();
  nBetterList selector_list;

  
  void new_tmpl() {
    if (selected_macro.size() > 0) {
      if (pastebin != null) pastebin.clear();
      String nn = new_temp_name.get();
      int t = 0;
      while (saved_template.getBloc(nn) != null) { nn = new_temp_name.get() + "_" + t; t++; }
      sValueBloc bloc = saved_template.newBloc(nn);
      for (Macro_Abstract m : selected_macro) inter.data.copy_bloc(m.value_bloc, bloc);
      sStr tmp_link = new sStr(bloc, "", "links", "links");
      sStr tmp_spot = new sStr(bloc, "", "spots", "spots");
      for (Macro_Abstract m : selected_macro) {
        tmp_link.set(tmp_link.get() + m.resum_link());
        tmp_spot.set(m.resum_spot(tmp_spot.get()));
      }
      tmp_spot.set(tmp_spot.get() + GROUP_TOKEN);
      //positionning
      //for (Map.Entry me : bloc.blocs.entrySet()) {
      //  sValueBloc vb = ((sValueBloc)me.getValue());
      //  if (vb.getBloc("settings") != null && vb.getBloc("settings").getValue("position") != null) {
      //    sVec v = (sVec)(vb.getBloc("settings").getValue("position"));
      //    v.setx(v.x() + ref_size * 2); v.sety(v.y() + ref_size * 3);
      //  }
      //}
      
      if (template_explorer != null) { 
        template_explorer.update();
        template_explorer.selectEntry(bloc.base_ref); }
    } else if (selected_sheet != this) {
      String nn = new_temp_name.get(); int t = 0;
      while (saved_template.getBloc(nn) != null) { nn = new_temp_name.get() + "_" + t; t++; }
      
      sValueBloc bloc = saved_template.newBloc(nn);
      sValueBloc vb = inter.data.copy_bloc(selected_sheet.value_bloc, bloc);
      
      //positionning
      if (vb.getBloc("settings") != null && vb.getBloc("settings").getValue("position") != null) {
        sVec v = (sVec)(vb.getBloc("settings").getValue("position"));
        v.setx(0); v.sety(0);
      }
      
      if (template_explorer != null) { 
        template_explorer.update();
        template_explorer.selectEntry(selected_sheet.value_bloc.base_ref); }
    }
  }
  
  void copy_to_tmpl() {
    if (selected_macro.size() > 0) {
      if (pastebin != null) pastebin.clear();
      if (saved_template.getBloc("copy") != null) saved_template.getBloc("copy").clear();
      
      sValueBloc bloc = saved_template.newBloc("copy");
      pastebin = bloc;
      
      for (Macro_Abstract m : selected_macro) inter.data.copy_bloc(m.value_bloc, bloc);
      sStr tmp_link = new sStr(pastebin, "", "links", "links");
      for (Macro_Abstract m : selected_macro) {
        tmp_link.set(tmp_link.get() + m.resum_link());
      }
      
      //positionning
      //for (Map.Entry me : pastebin.blocs.entrySet()) {
      //  sValueBloc vb = ((sValueBloc)me.getValue());
      //  if (vb.getBloc("settings") != null && vb.getBloc("settings").getValue("position") != null) {
      //    sVec v = (sVec)(vb.getBloc("settings").getValue("position"));
      //    v.setx(v.x() + ref_size * 2); v.sety(v.y() + ref_size * 3);
      //  }
      //}
      
      if (template_explorer != null) { 
        template_explorer.update();
        template_explorer.selectEntry(pastebin.base_ref); }
    } 
  }
  void pastedata_tmpl() {
    if (template_explorer != null && template_explorer.selected_bloc != null) {
      paste_tmpl(template_explorer.selected_bloc);
    }
  }
  void pastebin_tmpl() {
    if (pastebin != null) {
      paste_tmpl(pastebin);
    }
  }
  public void paste_tmpl(sValueBloc bloc) {
	  if (bloc.blocs.size()+selected_sheet.child_macro.size() > val_max_bloc.get()) {
		  return;
	  }
	  selected_sheet.cancel_new_spot();
    //save adding pos
    PVector prevs_gr_p = new PVector(); //def center
//    boolean to_empty_sheet = selected_sheet.child_macro.size() == 0;
//    if (selected_macro.size() > 0) { //use last selected med pos
//      prevs_gr_p.set(select_grab_widg.getX(), select_grab_widg.getY());
//      prevs_gr_p = inter.cam.screen_to_cam(prevs_gr_p);
//    } else {
//    	if (show_macro.get() && !to_empty_sheet) { //use screen point
      PVector sc_pos = new PVector(mmain().screen_gui.view.pos.x + mmain().screen_gui.view.size.x * 1.0F / 2.0F, 
                                   mmain().screen_gui.view.pos.y + mmain().screen_gui.view.size.y / 2.0F);
      sc_pos = mmain().inter.cam.screen_to_cam(sc_pos);
      prevs_gr_p.set(sc_pos.x - selected_sheet.grabber.getX(), sc_pos.y - selected_sheet.grabber.getY());
      prevs_gr_p.x = (prevs_gr_p.x - prevs_gr_p.x%(ref_size * GRID_SNAP_FACT));
      prevs_gr_p.y = (prevs_gr_p.y - prevs_gr_p.y%(ref_size * GRID_SNAP_FACT));
//    }
    
    //add macros and select them
    szone_clear_select();
    is_paste_loading = true;
    selected_sheet.addCopyofBlocContent(bloc, true); //true: will select 
    
//    if (to_empty_sheet) { 
//      szone_clear_select(); 
//      //selected_sheet.szone_select(); 
//      prevs_gr_p.set(select_grab_widg.getX(), select_grab_widg.getY());
//      prevs_gr_p = inter.cam.screen_to_cam(prevs_gr_p);
//    }
    
    //move group to adding pos
    PVector s_gr_p = new PVector(select_grab_widg.getX(), select_grab_widg.getY());
    s_gr_p = inter.cam.screen_to_cam(s_gr_p);
    s_gr_p.add(-prevs_gr_p.x - selected_sheet.grabber.getX(), -prevs_gr_p.y - selected_sheet.grabber.getY());
    s_gr_p.mult(-1);
    for (Macro_Abstract m : selected_macro) 
      m.group_move(s_gr_p.x, s_gr_p.y);
  
    //find place
    int adding_dir_x = 0;
	int adding_dir_y = 1;
	int adding_side_l = 1;
	int adding_side_cnt = 0;
    int adding_count = 0;
    float phf = 0.0F;
//    float move_fct = 3 * GRID_SNAP_FACT;
    float move_fct_x = select_bound_widg.getPhantomRect().size.x;
    float move_fct_y = select_bound_widg.getPhantomRect().size.y;
    move_fct_x /= ref_size;
    move_fct_x = move_fct_x + 2 * GRID_SNAP_FACT;
    move_fct_x = move_fct_x - move_fct_x%(GRID_SNAP_FACT*3);
    move_fct_y /= ref_size;
    move_fct_y = move_fct_y + 2 * GRID_SNAP_FACT;
    move_fct_y = move_fct_y - move_fct_y%(GRID_SNAP_FACT*3);
    boolean found = false;
    while (!found) {
      boolean col = false;
      selected_sheet.updateBack();
      for (Macro_Abstract c : selected_sheet.child_macro) 
        if (!selected_macro.contains(c) && c.openning.get() == DEPLOY 
            && Rect.rectCollide(select_bound_widg.getRect(), c.back.getRect(), ref_size * phf)) col = true;
        else if (!selected_macro.contains(c) && c.openning.get() == REDUC 
                 && Rect.rectCollide(select_bound_widg.getRect(), c.grabber.getRect(), ref_size * phf)) col = true;
        else if (!selected_macro.contains(c) && c.openning.get() == OPEN 
                 && Rect.rectCollide(select_bound_widg.getRect(), c.panel.getRect(), ref_size * phf)) col = true;
        else if (!selected_macro.contains(c) && c.openning.get() == HIDE && c.openning_pre_hide.get() == DEPLOY
                 && Rect.rectCollide(select_bound_widg.getPhantomRect(), c.back.getPhantomRect(), ref_size * phf)) col = true;
        else if (!selected_macro.contains(c) && c.openning.get() == HIDE && c.openning_pre_hide.get() == REDUC
                 && Rect.rectCollide(select_bound_widg.getPhantomRect(), c.grabber.getPhantomRect(), ref_size * phf)) col = true;
        else if (!selected_macro.contains(c) && c.openning.get() == HIDE && c.openning_pre_hide.get() == OPEN
                 && Rect.rectCollide(select_bound_widg.getPhantomRect(), c.panel.getPhantomRect(), ref_size * phf)) col = true;
      
      if (selected_sheet != mmain() && !show_macro.get()//openning.get() == HIDE 
          && Rect.rectCollide(select_bound_widg.getPhantomRect(), sheet.panel.getPhantomRect(), ref_size * 1)) col = true;
      if (selected_sheet != mmain() && show_macro.get()//openning.get() != HIDE 
          && Rect.rectCollide(select_bound_widg.getRect(), sheet.panel.getRect(), ref_size * 1)) col = true;
      
      if (!col) found = true;
      else {
	      for (Macro_Abstract m : selected_macro) 
	    	  	m.group_move(ref_size * adding_dir_x * move_fct_x, ref_size * adding_dir_y * move_fct_y);
	      adding_count++;
  		  if (adding_count >= adding_side_l) {
			adding_count = 0;
			if (adding_dir_x == 0) { adding_dir_x = adding_dir_y * -1; adding_dir_y = 0; }
			else { adding_dir_y = adding_dir_x * -1; adding_dir_x = 0; }
			adding_side_cnt++;
			if (adding_side_cnt >= 2) {
				adding_side_cnt = 0;
				adding_side_l++;
				adding_dir_x *= -1;
				adding_dir_y *= -1;
			}
	  	  }
      }
    }
    selected_sheet.updateBack();
  
    inter.addEventTwoFrame(new nRunnable() { public void run() { is_paste_loading = false; } } );
    if (sheet_explorer != null) sheet_explorer.update();
  }
  //boolean del_order = false;
  void del_selected() {
    //del_order = true;
    inter.addEventNextFrame(new nRunnable() { public void run() { 
    	for (int i = selected_macro.size() ; i >= 0 ; i--)
    		if (i < selected_macro.size()) selected_macro.get(i).clear(); 
    	
      if (sheet_explorer != null) sheet_explorer.update(); 
      selected_macro.clear(); 
      update_select_bound();
    } } );
  }
  
  /*
  setup loading : 
    clear everything
      macro, template, presets, clear call to simulation
    search setup file:
      interface data : transfer values
      template, preset : copy blocs
      for blocs inside main sheet :
        sheet bloc : (has all settings)
          allready same name n spe sheet : transfer value, copy inside blocs and values
          no same name sheet : copy full bloc, build sheet with 
          same name but diff spe : delete it then do as if no same name sheet
        not a sheet bloc : delete it
    load corresponding gui property
  
  */
  public void setup_load(sValueBloc b) { 
	app.BLACKOUT = true;

    inter.addEventNextFrame(new nRunnable() { public void run() { 
	    is_setup_loading = true;
	    is_paste_loading = true;
	    saved_template.empty();
	    saved_preset.empty();
	    
	    load_database();
	    
	    if (b.getBloc("Template") != null) {
	      b.getBloc("Template").runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
	        Save_Bloc b = new Save_Bloc("");
	        bloc.preset_to_save_bloc(b);
	        if (saved_template.getBloc(bloc.base_ref) == null && !bloc.base_ref.equals("copy")) saved_template.newBloc(b, bloc.base_ref);
	      }});
	    }if (b.getBloc("Preset") != null) {
	      b.getBloc("Preset").runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
	        Save_Bloc b = new Save_Bloc("");
	        bloc.preset_to_save_bloc(b);
	        if (saved_preset.getBloc(bloc.base_ref) == null) saved_preset.newBloc(b, bloc.base_ref);
	      }});//
	    }
	    
	    setupFromBloc(b.getBloc(value_bloc.base_ref));
	
	    if (b.getValue("show_macro") != null) 
	      show_macro.set(((sBoo)b.getValue("show_macro")).get());
	    if (b.getValue("show_build_tool") != null) 
	      show_build_tool.set(((sBoo)b.getValue("show_build_tool")).get());
//	    if (b.getValue("show_sheet_tool") != null) 
//	      show_sheet_tool.set(((sBoo)b.getValue("show_sheet_tool")).get());
	    if (b.getValue("show_macro_tool") != null) 
	      show_macro_tool.set(((sBoo)b.getValue("show_macro_tool")).get());
	    
	    if (sheet_explorer != null) sheet_explorer.update();
	    inter.addEventTwoFrame(new nRunnable() { public void run() { 
	    	is_paste_loading = false; select(); szone_clear_select(); 
	    	build_sheet_menu();
	    	sheet_front.clear(); sheet_front = null;
	    } } );
	      
	    szone_clear_select();
	    is_setup_loading = false;

        if (main_sheetbloc == null) main_sheetbloc = new MSheetBloc(mmain(), null);
        main_sheetbloc.init_end();
        
//	    buildLayer();
	} } );
  }
  
  void load_database() {
    Save_Bloc sb = new Save_Bloc("");
    sb.load_from(database_path.get());
    
    if (database_setup_bloc != null) database_setup_bloc.clear();
    database_setup_bloc = inter.data.newBloc(sb, "db_setup");
    if (database_setup_bloc == null) database_setup_bloc = inter.data.newBloc("db_setup");
    
    if (database_setup_bloc.getBloc("Template") != null) {
      database_setup_bloc.getBloc("Template").runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (saved_template.getBloc(bloc.ref) == null && !bloc.base_ref.equals("copy")) 
          saved_template.newBloc(b, bloc.ref);
      }});
    }if (database_setup_bloc.getBloc("Preset") != null) {
      database_setup_bloc.getBloc("Preset").runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (saved_preset.getBloc(bloc.ref) == null) saved_preset.newBloc(b, bloc.ref);
      }});
    }
  }
  
  String to_remove = "";
  void remove_from_database(String tr) {
    to_remove = tr;
    if (saved_template.getBloc(to_remove) != null) saved_template.getBloc(to_remove).clear();
    
    //if (saved_preset.getBloc(to_remove) != null) saved_preset.getBloc(to_remove).clear();
      
    sValueBloc vb = inter.getTempBloc();
    sValueBloc tb = inter.data.copy_bloc(saved_template, vb);
    sValueBloc pb = inter.data.copy_bloc(saved_preset, vb);
    
    Save_Bloc sb = new Save_Bloc("");
    sb.load_from(database_path.get());
    
    if (database_setup_bloc != null) database_setup_bloc.clear();
    database_setup_bloc = inter.data.newBloc(sb, "db_setup");
    if (database_setup_bloc == null) database_setup_bloc = inter.data.newBloc("db_setup");
    
    if (database_setup_bloc.getBloc("Template") != null) {
      database_setup_bloc.getBloc("Template").runBlocIterator(new nIterator<sValueBloc>(tb) { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (((sValueBloc)builder).getBloc(bloc.ref) == null && !bloc.base_ref.equals("copy") && 
            !bloc.ref.equals(to_remove)) 
          ((sValueBloc)builder).newBloc(b, bloc.ref);
      }});
    }
    if (database_setup_bloc.getBloc("Preset") != null) {
      database_setup_bloc.getBloc("Preset").runBlocIterator(new nIterator<sValueBloc>(pb) { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (((sValueBloc)builder).getBloc(bloc.ref) == null
            //&& !bloc.ref.equals(to_remove) 
            ) 
          ((sValueBloc)builder).newBloc(b, bloc.ref);
      }});//
    }
    
    vb.preset_to_save_bloc(sb); 
    sb.save_to(database_path.get());
    to_remove = "";
  }
  
  public void saving_database() {
    sValueBloc vb = inter.getTempBloc();
    sValueBloc tb = inter.data.copy_bloc(saved_template, vb);
    sValueBloc pb = inter.data.copy_bloc(saved_preset, vb);
    
    Save_Bloc sb = new Save_Bloc("");
    sb.load_from(database_path.get());
    
    if (database_setup_bloc != null) database_setup_bloc.clear();
    database_setup_bloc = inter.data.newBloc(sb, "db_setup");
    if (database_setup_bloc == null) database_setup_bloc = inter.data.newBloc("db_setup");
    
    if (database_setup_bloc.getBloc("Template") != null) {
      database_setup_bloc.getBloc("Template").runBlocIterator(new nIterator<sValueBloc>(tb) { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (((sValueBloc)builder).getBloc(bloc.ref) == null && !bloc.base_ref.equals("copy")) ((sValueBloc)builder).newBloc(b, bloc.ref);
      }});
    }
    if (database_setup_bloc.getBloc("Preset") != null) {
      database_setup_bloc.getBloc("Preset").runBlocIterator(new nIterator<sValueBloc>(pb) { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (((sValueBloc)builder).getBloc(bloc.ref) == null) ((sValueBloc)builder).newBloc(b, bloc.ref);
      }});//
    }
    
    vb.preset_to_save_bloc(sb); 
    sb.save_to(database_path.get());
  }
  
 

  boolean packet_process_asked = false;
  boolean first_process_frame = false;
  boolean all_packet_processed = true;
  LinkedBlockingQueue<Macro_Sheet> sheet_to_process = new LinkedBlockingQueue<Macro_Sheet>();
  Macro_Sheet proccessed_sheet = null;
  void frame() { 
	  if (loading_delay > 0) loading_delay--;
	  if (is_paste_loading) {
		  loading_delay = 10;
	  }
	  if (loading_delay == 1) {
		  app.BLACKOUT = false;
	  }
	  
	  frame_tick();
  }
  void ask_packet_process(Macro_Sheet sh) {
//      gui.app.plogln("Main ask_packet_process from " + sh.value_bloc.ref);
    if (!do_packet.get()) {
      sheet_to_process.clear();
    } else {
      if (!sheet_to_process.contains(sh)) { 
        sheet_to_process.add(sh);
      } 
      	if (!packet_process_asked)  {
      		gui.app.plogln("Main ------------ packet process frame ASK");
	      packet_process_asked = true;
	      first_process_frame = true;
	      all_packet_processed = false;
	      inter.addEventNextFrameEnd(packet_frame_run);
	    }
    }
  }
  public void packet_frame() {
	  if (first_process_frame)
		  gui.app.plogln("Main ------------ packet process frame START");
	  packpross_pile += packpross_by_frame.get();
	  
	  pross_to_do = 0;
	  while (packpross_pile >= 1) { 
		  pross_to_do++; packpross_pile--; 
	  }
//	  int done_turn = 0;
      while((pross_to_do >= 1 || mmain().loading_delay > 0) && sheet_to_process.size() > 0) {
        proccessed_sheet = sheet_to_process.remove();
        int sheet_turn = proccessed_sheet.process_packets(pross_to_do);
//        done_turn += sheet_turn;
        pross_to_do -= sheet_turn;
      }
      pross_to_do = 0;
      if (pross_in_waiting <= 0) {//done_turn >= pross_to_do
    	  	gui.app.plogln("Main ------------ packet process frame END");
    	  	packet_process_asked = false;
    	  	all_packet_processed = true;
      }
      else {
    	  	if (first_process_frame) 
    	  		gui.app.plogln("Main ------------ packet process frame unfinished"
    	  				+ "  --  pross_waiting : " + pross_in_waiting);
    	  	inter.addEventNextFrameEnd(packet_frame_run);
      }
	  first_process_frame = false;
  }
  int pross_in_waiting = 0;
  float packp_holder = 0;
  int pross_to_do = 0;
  float packpross_pile = 0;
  sFlt packpross_by_frame;
  nRunnable packet_frame_run;
  
  //, show_sheet_tool
  sBoo show_info, show_frmrt, show_gui, show_macro, show_link, link_volatil, show_build_tool, show_macro_tool, do_packet;
  sStr new_temp_name, database_path, shown_builder, shown_spe, shown_templ; 
  sRun del_select_run, copy_run, paste_run, duplic_run, cut_run, selall_run, reduc_run, construct_run;
  public sInterface inter;
  Rapp app;
  public sValueBloc saved_template;
public sValueBloc saved_preset;
sValueBloc database_setup_bloc;
  public nGUI cam_gui;
public nGUI screen_gui;
  nInfo info;
  nSelectZone szone;
  nWidget select_bound_widg, select_grab_widg;
  public Macro_Sheet selected_sheet = this;
Macro_Sheet search_sheet = this;
  ArrayList<Macro_Abstract> selected_macro = new ArrayList<Macro_Abstract>();
  boolean buildingLine = false, is_setup_loading = false, is_paste_loading = false;
  Macro_Connexion line_building_co = null;
  MBasic active_construct = null;
  int loading_delay = 0;
  String access;
  public boolean canAccess(String a) { return inter.canAccess(a); }
  ArrayList<MChan> chan_macros = new ArrayList<MChan>();
  ArrayList<MMIDI> midi_macros = new ArrayList<MMIDI>();
  ArrayList<MPanel> pan_macros = new ArrayList<MPanel>();
  ArrayList<MTool> tool_macros = new ArrayList<MTool>();
  ArrayList<nCursor> cursors_list = new ArrayList<nCursor>();
  ArrayList<MBaseTick> baseticked_list = new ArrayList<MBaseTick>();
  ArrayList<MGrow> grow_list = new ArrayList<MGrow>();
  MSheetBloc main_sheetbloc;
  MPanel last_added_panel;
  MTool last_added_tool;
  int pan_nb = 0, tool_nb = 0;
  Macro_Sheet last_link_sheet = null;
  String last_created_link = "";
  

  
  void updateBack() { update_select_bound(); }
  
  void szone_clear_select() {
    for (Macro_Abstract m : selected_macro) m.szone_unselect();
    selected_macro.clear();
    update_select_bound();
  }
  void reduc_selected() {
    for (Macro_Abstract m : selected_macro) m.changeOpenning();
  }
  void select_all() {
	  if (selected_macro.size() != selected_sheet.child_macro.size())
		  for (Macro_Abstract m : selected_sheet.child_macro) m.szone_select();
	  else szone_clear_select();
  }

  ArrayList<MAbstract_Builder> bloc_builders = new ArrayList<MAbstract_Builder>();
  ArrayList<Sheet_Specialize> sheetspe_builders = new ArrayList<Sheet_Specialize>();
  
  void add_bloc_builders(MAbstract_Builder m) {
    bloc_builders.add(m);
  }
public Macro_Main(sInterface _int) {
    super(_int);
    app = _int.app;
    Macro_Packet.app = app;
    
    app.mlogln("build macro main ");
    
    inter = _int; 
    access = inter.getAccess();
    cam_gui = inter.cam_gui; 
    screen_gui = inter.screen_gui;
//    info = new nInfo(cam_gui, ref_size);
    info = cam_gui.info;
    saved_template = inter.interface_bloc.newBloc("Template");
    saved_preset = inter.interface_bloc.newBloc("Preset");
    new_temp_name = setting_bloc.newStr("new_temp_name", "new_temp_name", "template");
    database_path = setting_bloc.newStr("database_path", "database_path", "database.sdata");
    load_database();
    
    packpross_by_frame = newFlt(60, "packpross_by_frame", "packpross_by_frame");
    packp_holder = packpross_by_frame.get();
    packpross_by_frame.set(3000);
    packet_frame_run = new nRunnable() { public void run() { packet_frame(); } };

    show_macro = setting_bloc.newBoo("show_macro", "show", true);
    show_macro.addEventChange(new nRunnable(this) { public void run() { 
      if (show_macro.get()) for (Macro_Abstract m : child_macro) m.show();
      else for (Macro_Abstract m : child_macro) m.hide();
      update_select_bound();
    }});
    show_link = setting_bloc.newBoo("show_link", "showL", true);
    link_volatil = setting_bloc.newBoo("show_link_volat", "showLV", true);
    
    show_build_tool = setting_bloc.newBoo("show_build_tool", "build tool", true);
    show_build_tool.addEventChange(new nRunnable(this) { public void run() { 
      if (build_tool != null && build_tool.hide == show_build_tool.get()) build_tool.reduc();
    }});

    shown_builder = setting_bloc.newStr("shown_builder", "shown_builder", "");
    shown_spe = setting_bloc.newStr("shown_spe", "shown_spe", "");
    shown_templ = setting_bloc.newStr("shown_templ", "shown_templ", "");
    
    show_macro_tool = setting_bloc.newBoo("show_macro_tool", "macro tool", true);
    show_macro_tool.addEventChange(new nRunnable(this) { public void run() { 
      if (macro_tool != null && macro_tool.hide == show_macro_tool.get()) macro_tool.reduc();
    }});
    show_gui = newBoo("show_gui", "show gui", true, 'h');
    show_gui.addEventChange(new nRunnable(this) { public void run() { 
      screen_gui.isShown = show_gui.get();
      inter.show_info = show_gui.get();
    }});
    show_info = newBoo("show_info", "show_info", true);
    show_info.addEventChange(new nRunnable(this) { public void run() { 
		inter.show_info = show_info.get(); }});
    show_frmrt = newBoo("show_framerate", "show_framerate", true);
    show_frmrt.addEventChange(new nRunnable(this) { public void run() { 
		inter.show_frmrt = show_frmrt.get(); }});
    do_packet = newBoo(true, "do_packet", "do_packet");
    
    del_select_run = newRun("del_select_run", "del", 'd',
    		new nRunnable() { public void run() { del_selected(); }});

    copy_run = newRun("copy_run", "copy", 'c',
    		new nRunnable() { public void run() { copy_to_tmpl(); }});
    
    paste_run = newRun("paste_run", "paste", 'v',
    		new nRunnable() { public void run() { pastebin_tmpl(); }});

    duplic_run = newRun("duplic_run", "duplic", 'w',
    		new nRunnable() { public void run() { copy_to_tmpl(); pastebin_tmpl(); }});

    cut_run = newRun("cut_run", "cut", 'x',
    		new nRunnable() { public void run() { copy_to_tmpl(); del_selected(); }});
    
    selall_run = newRun("selall_run", "selall", 'a',
    		new nRunnable() { public void run() { select_all(); }});
    
    reduc_run = newRun("switch_reduc_run", "switch_reduc", 's',
    		new nRunnable() { public void run() { reduc_selected(); }});
    
    construct_run = newRun("construct_run", "construct", ' ', 
    		new nRunnable() { public void run() { 
		  if (buildingLine) { new MNode(selected_sheet, line_building_co, gui.mouseVector);
		  } else { new MBasic(selected_sheet, gui.mouseVector); }  }});
    

    	  add_bloc_builders(new MZone.Builder(this));

      add_bloc_builders(new Macro_Sheet.MSheet_Builder(this));
      add_bloc_builders(new MBasic.Builder(this));
      add_bloc_builders(new MNode.Builder(this));
      add_bloc_builders(new MSheetBloc.Builder(this));
      add_bloc_builders(new MCustom.Builder(this));
      add_bloc_builders(new MCursor.MCursor_Builder(this));
      add_bloc_builders(new MMButton.Builder(this));
      add_bloc_builders(new MText.Builder(this));
      add_bloc_builders(new MValue.MValue_Builder(this));
      add_bloc_builders(new MMVar.Builder(this));
      add_bloc_builders(new MGate.MGate_Builder(this));
      add_bloc_builders(new MShape.Builder(this));
      add_bloc_builders(new MStructure.Builder(this));
      add_bloc_builders(new MCam.Builder(this));
      add_bloc_builders(new MChan.MChan_Builder());
      add_bloc_builders(new MRamp.MRamp_Builder());
      add_bloc_builders(new MSequance.Builder());
      add_bloc_builders(new MCounter.MCount_Builder());
      add_bloc_builders(new MSetReset.Builder());
      add_bloc_builders(new MInput.Builder());
      add_bloc_builders(new MQuickFloat.Builder());

      add_bloc_builders(new MRandom.MRandom_Builder());
	  add_bloc_builders(new MBin.MBin_Builder());
	  add_bloc_builders(new MNot.MNot_Builder());
      add_bloc_builders(new MSlide.MSlide_Builder());
      add_bloc_builders(new MVecCalc.MVecCalc_Builder());
      add_bloc_builders(new MBoolCalc.Builder());
      add_bloc_builders(new MNumCalc.Builder());
      add_bloc_builders(new MTransform.Builder());
      add_bloc_builders(new MFilter.Builder());
      add_bloc_builders(new MVecLerp.Builder());
      add_bloc_builders(new MGrow.Builder(this));

      add_bloc_builders(new MButton.Builder(this));
      add_bloc_builders(new MColRGB.MColRGB_Builder());
      add_bloc_builders(new MToolBin.MToolBin_Builder());
      add_bloc_builders(new MToolTri.MToolTri_Builder());
      add_bloc_builders(new MTool.MTool_Builder());
      add_bloc_builders(new MPanGrph.MPanGrph_Builder());
      add_bloc_builders(new MPanSld.MPanSld_Builder());
      add_bloc_builders(new MPanBin.MPanBin_Builder());
      add_bloc_builders(new MPanel.MPanel_Builder());
      add_bloc_builders(new MVar.MVar_Builder());

//    add_bloc_builders(new MComment.MComment_Builder());
//    add_bloc_builders(new MValView.Builder());
//    add_bloc_builders(new MPulse.MPulse_Builder());
//    add_bloc_builders(new MFrame.MFrame_Builder());
//    add_bloc_builders(new MForm.MForm_Builder());
//    add_bloc_builders(new MStructure.MStructure_Builder());
//    add_bloc_builders(new MPatern.MPatern_Builder());
//    add_bloc_builders(new MCanvas.MCanvas_Builder());
//    add_bloc_builders(new MSheetCo.MSheetCo_Builder());
//    add_bloc_builders(new MPoint.MPoint_Builder());
//    add_bloc_builders(new MMPoints.Builder());
//    add_bloc_builders(new MPack.Builder());
//    add_bloc_builders(new MTick.MTick_Builder());
//    add_bloc_builders(new MKeyboard.MKeyboard_Builder());
//    add_bloc_builders(new MMouse.MMouse_Builder());
//    add_bloc_builders(new MTrig.MTrig_Builder());
//    add_bloc_builders(new MSwitch.MSwitch_Builder());
//    add_bloc_builders(new MVecMD.MVecMD_Builder());
//    add_bloc_builders(new MVecXY.MVecXY_Builder());
//    add_bloc_builders(new MComp.MComp_Builder());
//    add_bloc_builders(new MBool.MBool_Builder());
//    add_bloc_builders(new MCalc.MCalc_Builder());
//    add_bloc_builders(new MSheetBloc.MSheetMain_Builder());
      
      
    
    szone = new nSelectZone(cam_gui);
    szone.addEventStartSelect(new nRunnable(this) { public void run() { 
      select_bound_widg.hide();
      select_grab_widg.hide();
      selected_macro.clear();
    }}).addEventSelecting(new nRunnable(this) { public void run() {
//      if (selected_sheet == search_sheet) 
//      inter.addEventNextFrame(new nRunnable() { public void run() { update_select_bound(); } } );
    	  if (selected_sheet != search_sheet) search_sheet.select();
    }}).addEventEndSelect(new nRunnable(this) { public void run() {
	  search_sheet = ((Macro_Sheet)builder);
	}});
    
    select_bound_widg = addModel("MC_Selection_Front")
      .setLayer(1)
      .hide();
    select_bound_widg.clearParent();
    select_grab_widg = screen_gui.theme.newWidget(screen_gui, "MC_Selection_Grabber")
      .setGrabbable()
      .hide();
    
    select_grab_widg
      .addEventGrab(new nRunnable() { public void run() {
        sgrab_px = select_grab_widg.getX();
        sgrab_py = select_grab_widg.getY();
      } } )
      .addEventDrag(new nRunnable() { public void run() {
        
        PVector gr_p = new PVector(select_grab_widg.getX(), select_grab_widg.getY());
        
        PVector prev_gr_p = new PVector(sgrab_px, sgrab_py);
        gr_p = inter.cam.screen_to_cam(gr_p);
        prev_gr_p = inter.cam.screen_to_cam(prev_gr_p);
        
        for (Macro_Abstract m : selected_macro) 
          m.group_move(gr_p.x - prev_gr_p.x, gr_p.y - prev_gr_p.y);
        
        sgrab_px = select_grab_widg.getX();
        sgrab_py = select_grab_widg.getY();
        
        selected_sheet.updateBack();
        update_select_bound();
      } } ).addEventLiberate(new nRunnable() { public void run() {
        
        for (Macro_Abstract m : selected_macro) {
            m.setPosition(m.grabber.getLocalX() - m.grabber.getLocalX()%(ref_size * GRID_SNAP_FACT), 
          		  m.grabber.getLocalY() - m.grabber.getLocalY()%(ref_size * GRID_SNAP_FACT));
        }
        
        selected_sheet.updateBack();
      } } );
    
    init_tick();
    
    inter.addEventNextFrame(new nRunnable() { public void run() { 
      inter.cam.addEventMove(new nRunnable() { public void run() { update_select_bound(); } } );
//      build_macro_menus();
      inter.cam.cam_grid_spacing.set(ref_size*5*Macro_Main.GRID_SNAP_FACT*3);
    } } );

    inter.addEventTwoFrame(new nRunnable() { public void run() { 
		videoRecodingSetup(); }});
    
    inter.addEventTwoFrame(new nRunnable() { public void run() { 
	    inter.cam.cam_pos.set(-600, 0);
	    	inter.addEventNextFrame(new nRunnable() { public void run() { 
	    	    
	        packpross_by_frame.set(packp_holder);
	        if (main_sheetbloc == null) {
	        		main_sheetbloc = new MSheetBloc(mmain(), null);
	    	        main_sheetbloc.init_end();
	    	        main_sheetbloc.grabber.setPosition(0, -ref_size*10.5);
	        }
		    szone_clear_select();
		    update_select_bound();
	//        update_bloc_selector_list();
	//        buildLayer();
	        inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );
	    	} } );
    } } );
    
  }
  float sgrab_px = 0, sgrab_py = 0;
  void update_select_bound() {
    if (show_macro.get() && selected_macro.size() > 0) {
      float elem_space = ref_size*0.5F;
      float minx = 0, miny = 0, maxx = 0, maxy = 0;
      
      Macro_Abstract f = selected_macro.get(0);
      if (f.openning.get() == DEPLOY) {
          minx = f.grabber.getX() + f.back.getLocalX() - elem_space;
          miny = f.grabber.getY() + f.back.getLocalY() - elem_space;
          maxx = f.grabber.getX() + f.back.getLocalX() + f.back.getLocalSX() + elem_space;
          maxy = f.grabber.getY() + f.back.getLocalY() + f.back.getLocalSY() + elem_space;
      } else if (f.openning.get() == OPEN) {
          minx = f.grabber.getX() + f.panel.getLocalX() - elem_space;
          miny = f.grabber.getY() + f.panel.getLocalY() - elem_space;
          maxx = f.grabber.getX() + f.panel.getLocalX() + f.panel.getLocalSX() + elem_space;
          maxy = f.grabber.getY() + f.panel.getLocalY() + f.panel.getLocalSY() + elem_space;
      } else if (f.openning.get() == REDUC) {
          minx = f.grabber.getX() - elem_space;
          miny = f.grabber.getY() - elem_space;
          maxx = f.grabber.getX() + f.grabber.getLocalSX() + elem_space;
          maxy = f.grabber.getY() + f.grabber.getLocalSY() + elem_space;
      }
      
      for (Macro_Abstract m : selected_macro) if (m.openning.get() == DEPLOY) {
        if (minx > m.grabber.getX() + m.back.getLocalX() - elem_space) 
          minx = m.grabber.getX() + m.back.getLocalX() - elem_space;
        if (miny > m.grabber.getY() + m.back.getLocalY() - elem_space) 
          miny = m.grabber.getY() + m.back.getLocalY() - elem_space;
        if (maxx < m.grabber.getX() + m.back.getLocalX() + m.back.getLocalSX() + elem_space) 
          maxx = m.grabber.getX() + m.back.getLocalX() + m.back.getLocalSX() + elem_space;
        if (maxy < m.grabber.getY() + m.back.getLocalY() + m.back.getLocalSY() + elem_space) 
          maxy = m.grabber.getY() + m.back.getLocalY() + m.back.getLocalSY() + elem_space;
      } else if (m.openning.get() == OPEN) {
        if (minx > m.grabber.getX() + m.panel.getLocalX() - elem_space) 
          minx = m.grabber.getX() + m.panel.getLocalX() - elem_space;
        if (miny > m.grabber.getY() + m.panel.getLocalY() - elem_space) 
          miny = m.grabber.getY() + m.panel.getLocalY() - elem_space;
        if (maxx < m.grabber.getX() + m.panel.getLocalX() + m.panel.getLocalSX() + elem_space) 
          maxx = m.grabber.getX() + m.panel.getLocalX() + m.panel.getLocalSX() + elem_space;
        if (maxy < m.grabber.getY() + m.panel.getLocalY() + m.panel.getLocalSY() + elem_space) 
          maxy = m.grabber.getY() + m.panel.getLocalY() + m.panel.getLocalSY() + elem_space;
      } else if (m.openning.get() == REDUC) {
        if (minx > m.grabber.getX() - elem_space) 
          minx = m.grabber.getX() - elem_space;
        if (miny > m.grabber.getY() - elem_space) 
          miny = m.grabber.getY() - elem_space;
        if (maxx < m.grabber.getX() + m.grabber.getLocalSX() + elem_space) 
          maxx = m.grabber.getX() + m.grabber.getLocalSX() + elem_space;
        if (maxy < m.grabber.getY() + m.grabber.getLocalSY() + elem_space) 
          maxy = m.grabber.getY() + m.grabber.getLocalSY() + elem_space;
      }
      
      select_bound_widg.show();
      select_bound_widg.setPosition(minx, miny);
      select_bound_widg.setSize(maxx - minx, maxy - miny);
      PVector p = new PVector(minx + (maxx - minx) / 2, miny + (maxy - miny) / 2);
//      p.add(-p.x%ref_size/2, -p.y%ref_size/2);
      p = inter.cam.cam_to_screen(p);
      p.add(-select_grab_widg.getLocalSX()/2, -select_grab_widg.getLocalSY()/2);
      if (selected_macro.size() > 0)// || ref_size * gui.scale < 30) 
        select_grab_widg.show().setPosition(p.x, p.y);
      else select_grab_widg.hide();
    } else {
      select_bound_widg.hide();
      select_grab_widg.hide();
    }
  }
  
  public void addSpecializedSheet(Sheet_Specialize s) {
    s.mmain = this;
    sheetspe_builders.add(s);
    build_macro_menus();
  }
  public Macro_Sheet addUniqueSheet(Sheet_Specialize s) {
    s.mmain = this;
    s.unique = true;
    sheetspe_builders.add(s);
    build_macro_menus();
    Macro_Sheet ms = s.add_new(this, null, null);
    return ms;
  }
  
  
  
  
  
  

	  sInt tick_counter; //conteur de tour depuis le dernier reset ou le debut
	  public sBoo pause; //permet d'interompre le defilement des tour
	  sInt force_next_tick; 
	  sFlt tick_by_frame; //nombre de tour a executé par frame
	  sFlt tick_sec,tick_time;
	  sInt SEED; //seed pour l'aleatoire
	  sBoo auto_reset, auto_reset_rng_seed, auto_reset_screenshot;
	  sInt auto_reset_turn;
	  sRun srun_reset, srun_rngr, srun_nxtf, srun_tick, srun_scrsht;
	  sBoo show_toolpanel;

	  float tick_pile = 0; //pile des tick a exec
	  int run_tck_cnt = 0, met_tck_cnt = 0;
	  int run_rst_cnt = 0, met_rst_cnt = 0;

	void init_tick() {
		
		addEventSetupLoad(new nRunnable() { 
	      public void run() { mmain().inter.addEventNextFrame(new nRunnable() { 
	    	  	public void run() { reset(); } } );   } } );
		
	    tick_counter = newInt(0, "tick_counter", "tick");
	    tick_by_frame = newFlt(2, "tick by frame", "tck/frm");
	    tick_sec = newFlt(0, "tick seconde", "tps");
	    tick_time = newFlt(0, "tick duration (ms)", "tls");
	    pause = newBoo(false, "pause", "pause");
	    force_next_tick = newInt(0, "force_next_tick", "nxt tick");
	    auto_reset = newBoo(true, "auto_reset", "auto reset");
	    auto_reset_rng_seed = newBoo(true, "auto_reset_rng_seed", "auto rng");
	    auto_reset_screenshot = newBoo(false, "auto_rest_screenshot", "auto shot");
	    auto_reset_turn = newInt(4000, "auto_reset_turn", "auto turn");
	    SEED = newInt(548651008, "SEED", "SEED");
	
	    srun_tick = newRun("sim_tick", "tick", new nRunnable() { public void run() { 
	    		  run_tck_cnt++; 
	    		  while (run_tck_cnt > met_tck_cnt) tick(); 	} } );
	    srun_reset = newRun("sim_reset", "reset", new nRunnable() { public void run() { 
	    		run_rst_cnt++; 
	    		while (run_rst_cnt > met_rst_cnt) reset(); } } );
	    srun_rngr = newRun("sim_rng_reset", "rst rng", new nRunnable() { 
	      public void run() { resetRng(); } } );
	    srun_nxtf = newRun("sim_next_frame", "nxt frm", new nRunnable() { 
	      public void run() { force_next_tick.set((int)(tick_by_frame.get())); } } );
	    srun_scrsht = newRun("screen_shot", "impr", new nRunnable() { 
	      public void run() { mmain().inter.cam.screenshot = true; } } );
	}
	    
	
	  ArrayList<nRunnable> eventsReset = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsFrame = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsUnpausedFrame = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsTick = new ArrayList<nRunnable>();
//	  ArrayList<nRunnable> eventsTick2 = new ArrayList<nRunnable>();
	  public Macro_Main addEventReset(nRunnable r) { eventsReset.add(r); return this; }
	  public Macro_Main removeEventReset(nRunnable r) { eventsReset.remove(r); return this; }
//	  public Macro_Main addEventFrame(nRunnable r) { eventsFrame.add(r); return this; }
//	  public Macro_Main addEventUnpausedFrame(nRunnable r) { eventsUnpausedFrame.add(r); return this; }
	  public Macro_Main addEventTick(nRunnable r) { eventsTick.add(r); return this; }
	  public Macro_Main removeEventTick(nRunnable r) { eventsTick.remove(r); return this; }
//	  public Macro_Main addEventTick2(nRunnable r) { eventsTick2.add(r); return this; }
//	  public Macro_Main removeEventTick2(nRunnable r) { eventsTick2.remove(r); return this; }
	

	  public void build_tick_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.addTab("Time");

		tab.getShelf()
		  .addDrawer(10.25, 0.6)
		  .addModel("Label-S4", "- Tick Control -").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_counter, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerLargeFieldCtrl(SEED, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(tick_by_frame, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_sec, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_time, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(auto_reset_turn, 1000, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(auto_reset, auto_reset_rng_seed, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(srun_scrsht, auto_reset_screenshot, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(srun_reset, srun_rngr, srun_tick, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(pause, mmain().inter.cam.grid, 10, 1)
	      .addSeparator(0.125)
	      ;
	}
	  
	void resetRng() { 
	  SEED.set((int)(gui.app.random(1000000000))); 
	  reset();
	}
	void reset() {
	  gui.app.randomSeed(SEED.get());
	  tick_counter.set(0);
	  mmain().inter.framerate.reset();
	  nRunnable.runEvents(eventsReset);
	  run_tck_cnt = 0; met_tck_cnt = 0;
	  met_rst_cnt++; 
	  while (run_rst_cnt < met_rst_cnt) srun_reset.run();
	}

	void tick() {
	  //auto reset
	  if (auto_reset.get() && auto_reset_turn.get() <= tick_counter.get()) {
	    if (auto_reset_rng_seed.get()) {
	      SEED.set((int)(gui.app.random(1000000000)));
	    }
	    reset();
	  }
	  tick_counter.set(tick_counter.get()+1);
	  for (MBaseTick b : baseticked_list) b.receive_tick();
	  for (MGrow b : grow_list) b.receive_tick();
	  nRunnable.runEvents(eventsTick);
	  met_tck_cnt++; 
	  while (run_tck_cnt < met_tck_cnt) srun_tick.run();
	}
	void frame_tick() {
	  if (!pause.get()) {
	  	tick_sec.set(mmain().inter.framerate.median_framerate.get() * tick_by_frame.get());
	  	tick_time.set(1 / tick_sec.get()*1000);
	
	//      tick_pile += tick_by_frame.get();
	    if (all_packet_processed) tick_pile += tick_by_frame.get();
	
	    //auto screenshot before reset
	    if (auto_reset.get() && auto_reset_screenshot.get() &&
	      auto_reset_turn.get() == tick_counter.get() + tick_by_frame.get() + tick_by_frame.get()) {
	      mmain().inter.cam.screenshot = true;
	    }
	
	    while (tick_pile >= 1) {
	      tick();
	      tick_pile--;
	    }
	
	  } else tick_sec.set(0);
	
	  // tick by tick control
	  if (pause.get() && force_next_tick.get() > 0) { 
	    for (int i = 0; i < force_next_tick.get(); i++) tick(); 
	    force_next_tick.set(0);
	  }
	  if (!pause.get() && force_next_tick.get() > 0) { 
	    force_next_tick.set(0);
	  }
	
	}

	sRun vid_start, vid_stop;
	sInt vid_len, vid_final_frmrate, vid_max_len, vid_capturerate;
	sBoo vid_run, vid_paused, vid_limit_len, vid_rst_start, vid_canvas_record;
	sStr vid_name;
	sFlt vid_dur;
	int vid_cnt = 0;
	boolean vid_started = false;
	
	Canvas recorded_canvas = null;
  
	void videoRecodingSetup() {
	  // Set video and audio quality.
	  // Video quality: 100 is best, lossless video (but big file size)
	  //   Set it to 0 (worst) if you enjoy poor quality videos :)
	  //   70 is the default.
	  // Audio quality: 128 is the default, 192 very good,
	  //   256 is near lossless but big file size.
//	  gui.app.videoExport.setQuality(50, 32);
	  
	  // This sets the frame rate of the resulting video file. I has nothing to do
	  // with the current Processing frame rate. For instance you could have a 
	  // Processing sketch that does heavy computation and renders only one frame 
	  // every 5 seconds, but here you could still set that the resulting video should 
	  // play at 10 frames per second. Default: 30 fps.
	  //videoExport.setFrameRate(10);  
	  
	  // If your sketch already calls loadPixels(), you can tell videoExport to 
	  // not do that again. It's not necessary, but your sketch may perform a 
	  // bit better if you avoid calling it twice.
//	  gui.app.videoExport.setLoadPixels(false);
	  
	  // If video is being exported correctly, you can call this function to avoid
	  // creating .txt files containing debug information.
//	  gui.app.videoExport.setDebugging(false);

	  // Use the next line once if you changed the location of the ffmpeg tool.
	  // This will make the library ask for it's location again.
	  //videoExport.forgetFfmpegPath();
	  
	  // This advanced example shows how to customize the ffmpeg command that gets
	  // executed by this library.
	  // This allows you to access the full power of ffmpeg and select custom
	  // codecs, apply audio or video filters and even do video streaming!
	  
	  // Everything as by default except -vf (video filter)
//	  gui.app.videoExport.setFfmpegVideoSettings(
//	    new String[]{
//	    "[ffmpeg]",                       // ffmpeg executable
//	    "-y",                             // overwrite old file
//	    "-f",        "rawvideo",          // format rgb raw
//	    "-vcodec",   "rawvideo",          // in codec rgb raw
//	    "-s",        "[width]x[height]",  // size
//	    "-pix_fmt",  "rgb24",             // pix format rgb24
//	    "-r",        "[fps]",             // frame rate
//	    "-i",        "-",                 // pipe input
//
//	                                      // video filter with vignette, blur,
//	                                      // noise and text. font commented out
//	    "-vf", "vignette,gblur=sigma=1,noise=alls=10:allf=t+u," +
//	    "drawtext=text='Made with Processing':x=50:y=(h-text_h-50):fontsize=24:fontcolor=white@0.8",
//	    // drawtext=fontfile=/path/to/a/font/myfont.ttf:text='Made...
//
//	    "-an",                            // no audio
//	    "-vcodec",   "h264",              // out codec h264
//	    "-pix_fmt",  "yuv420p",           // color space yuv420p
//	    "-crf",      "[crf]",             // quality
//	    "-metadata", "comment=[comment]", // comment
//	    "[output]"                        // output file
//	    });

	  // Everything as by default. Unused: no audio in this example.
//	  gui.app.videoExport.setFfmpegAudioSettings(new String[]{
//	    "[ffmpeg]",                       // ffmpeg executable
//	    "-y",                             // overwrite old file
//	    "-i",        "[inputvideo]",      // video file path
//	    "-i",        "[inputaudio]",      // audio file path
//	    "-filter_complex", "[1:0]apad",   // pad with silence
//	    "-shortest",                      // match shortest file
//	    "-vcodec",   "copy",              // don't reencode vid
//	    "-acodec",   "aac",               // aac audio encoding
//	    "-b:a",      "[bitrate]k",        // bit rate (quality)
//	    "-metadata", "comment=[comment]", // comment
//	    // https://stackoverflow.com/questions/28586397/ffmpeg-error-while-re-encoding-video#28587897
//	    "-strict",   "-2",                // enable aac
//	    "[output]"                        // output file
//	    });
	  
	  vid_start = newRun("vid_start", "vid_start", new nRunnable() { public void run() { 
		  if (!vid_run.get()) { 
			  gui.app.videoExport.setMovieFileName("video\\" + vid_name.get() + 
					  gui.app.global_frame_count + ".mp4");
			  gui.app.videoExport.setFrameRate(vid_final_frmrate.get());
			  gui.app.videoExport.startMovie(); 
			  vid_len.set(0);
			  vid_run.set(true); 
			  vid_paused.set(false); } } } );
	  vid_stop = newRun("vid_stop", "vid_stop", new nRunnable() { public void run() { 
		  if (vid_run.get()) { 
		    	  gui.app.videoExport.endMovie(); 
		    	  vid_run.set(false); } } } );
	  
	  vid_len = newInt(0, "vid_len", "frame");
	  vid_max_len = newInt(300, "vid_max_len");
	  vid_capturerate = newInt(0, "vid_capturerate");
	  vid_final_frmrate = newInt(60, "vid_final_frmrate");
	  vid_dur = newFlt(0, "vid_dur", "duration (s)");
	  
	  vid_len.addEventChange(new nRunnable() { public void run() { 
	  	  vid_dur.set((float)vid_len.get() / (float)vid_final_frmrate.get()); } } );
	  
	  vid_run = newBoo(false, "vid_run"); 
	  vid_paused = newBoo(false, "vid_paused"); 
	  vid_limit_len = newBoo(false, "vid_limit_len");
	  vid_rst_start = newBoo(false, "vid_rst_start");
	  vid_canvas_record = newBoo(false, "vid_canvas_record");
	  
	  vid_name = newStr("miniVid", "vid_name");
	  
	  inter.addEventTwoFrame(new nRunnable() { public void run() { 
		  addEventReset(new nRunnable() { public void run() { 
			  if (vid_rst_start.get()) {
//				  vid_stop.run();
				  vid_start.run();
			  }  } } );
	  
		  inter.cam.setPopCamRun(new nRunnable() { public void run() { 
			  if (!vid_paused.get() && vid_run.get()) {
				  vid_cnt++;
				  if (vid_cnt > vid_capturerate.get()) {
					  if (vid_limit_len.get() && vid_len.get() >= vid_max_len.get()) {
						  vid_stop.run();
					  } else {
						  vid_len.add(1);
						  gui.app.videoExport.saveFrame(); 
					  } } }
		  } } );
		  
		  vid_run.set(false);
		  vid_paused.set(false);
		  vid_started = false;
		  vid_cnt = 0;
		  vid_len.set(0);
		  gui.app.videoExport.setFrameRate(vid_final_frmrate.get());
	  } } );
  }
  
  nWidget recording_flag;
  
  public void build_video_menu(nFrontPanel sheet_front) {
	  nFrontTab tab = sheet_front.addTab("Video");
	    tab.getShelf()
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-Video Recording Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.35)
	      .addDrawerDoubleButton(vid_start, vid_stop, 10, 1);
	    nDrawer flag_drw = tab.getShelf().addDrawer(10.25, 0.0);
	    tab.getShelf()
	      .addSeparator(0.125)
	      .addDrawerCentralButton(vid_paused, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleWatch(vid_len, vid_dur, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(vid_rst_start, vid_limit_len, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(vid_max_len, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(vid_max_len, 1000, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(vid_capturerate, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawer(10.25, 0.5)
	      .addModel("Label-S4", "-File-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.35)
	      .addDrawerIncrValue(vid_final_frmrate, 10, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerLargeFieldCtrl(vid_name, 10, 1)
	      .addSeparator(0.125)
	      ;
	    
	    recording_flag = flag_drw.addModel("Label_DownLight_Back_Downlight_Outline-S3", "--")
	    		.setPX(ref_size*4).setPY(-ref_size*18.0/16.0)
	    		.setSX(ref_size*2).setSY(ref_size*18.0/16.0);
	    
	    vid_run.addEventChange(new nRunnable() { public void run() { 
		  if (!vid_run.get()) recording_flag.setLook(inter.screen_gui.theme, 
				    "Label_DownLight_Back_Downlight_Outline-S3")
	  	  			.setText("--"); 
		  else recording_flag.setLook(inter.screen_gui.theme, 
				    "Label_HightLight_Back_Highlight_Outline-S3")
					.setText("REC"); 
		} } );
   }

	
  
  
  
  
  
  
  void noteOn(int channel, int pitch, int velocity) {
    for (MMIDI m : midi_macros) m.noteOn(channel, pitch, velocity);
  }
  
  void noteOff(int channel, int pitch, int velocity) {
    for (MMIDI m : midi_macros) m.noteOff(channel, pitch, velocity);
  }
  
  void controllerChange(int channel, int number, int value) {
    for (MMIDI m : midi_macros) m.controllerChange(channel, number, value);
  }
  
  
  
  
  
  
  
  
  
  
  
  
  static public void myTheme_MACRO(nTheme theme, float ref_size) {
	  theme.addModel("mc_ref", new nWidget(theme.app)
	    .setPassif()
	    .setLabelColor(theme.app.color(200, 200, 220))
	    .setFont((int)(ref_size/1.6F))
	    .setOutlineWeight(0)
	    .setOutlineColor(theme.app.color(255, 0))
	    );
	  theme.addModel("MC_Panel", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(50, 0))
	    .setOutlineColor(theme.app.color(105))
	    .setOutlineWeight(ref_size * 2.25F / 24.0F)// 0.25/16.0
	    .setOutline(true)
	    );
	  
	  theme.addModel("MC_Title", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(40))
	    .setOutlineColor(theme.app.color(80))
	    .setOutlineSelectedColor(theme.app.color(160))
	    .setOutlineWeight(ref_size / 12F)
	    .setOutline(true)
	    .setFont((int)(ref_size/1.6F))
	    .setText("--")
	    .setSize(ref_size*2, ref_size*0.75F).setPosition(ref_size*0.75F, ref_size*0.5F)
	    );
	  theme.addModel("MC_Front", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(50, 0))
	    .setOutlineColor(theme.app.color(200))
	    .setOutlineWeight(ref_size * 1.0 / 16.0)
	    .setPassif()
	    );
	  theme.addModel("MC_Front_Sheet", theme.newWidget("MC_Front")
	    .setOutlineColor(theme.app.color(200, 200, 0))
	    .setOutlineWeight(ref_size * 2.0 / 16.0)
	    );
	  theme.addModel("MC_Panel_Spot_Back", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(60))
	    .setOutlineColor(theme.app.color(105, 105, 80))
	    .setOutlineWeight(ref_size * 1.0 / 16.0)
	    .setSize(ref_size*2.25F, ref_size*1.125)
//	    .setPosition(ref_size*0.0, ref_size*0.0)
	    .setFont((int)(ref_size/2))
	    .setOutline(true)
	    );
	  theme.addModel("MC_Add_Spot_Actif", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(120, 70, 0))
	    .setHoveredColor(theme.app.color(180, 90, 10))
	    .setLabelColor(theme.app.color(60, 30, 0))
	    .setSize(ref_size*2.250, ref_size*1.125)
//	    .setPosition(ref_size*0.125, ref_size*(1.125-0.75)/2)
	    );
	  theme.addModel("MC_Add_Spot_Passif", theme.newWidget("MC_Add_Spot_Actif")
	    .setStandbyColor(theme.app.color(50))
	    );
	  theme.addModel("MC_Sheet_Soft_Back", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(180, 60))
	    .setOutlineColor(theme.app.color(140))
	    .setOutlineWeight(ref_size / 12F)
	    .setOutline(true)
	    );
	  theme.addModel("MC_Sheet_Hard_Back", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(45))
	    .setOutlineColor(theme.app.color(140))
	    .setOutlineWeight(ref_size / 12F)
	    .setOutline(true)
	    );
	  theme.addModel("MC_Element", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(70))///////////
	    .setOutlineColor(theme.app.color(90))
	    .setOutlineWeight(ref_size / 20.0F)//////////
	    .setOutline(true)
//	    .setPosition(0, ref_size*0.0)
	    );
	  theme.addModel("MC_Element_For_Spot", theme.newWidget("MC_Element")
	    .setStandbyColor(theme.app.color(120, 70, 0))
	    .setHoveredColor(theme.app.color(200, 120, 0))
//	    .setOutlineColor(theme.app.color(150, 150, 0))
//	    .setOutlineWeight(ref_size / 8F)
	    .setOutline(false)
	    );
	  theme.addModel("MC_Element_At_Spot", theme.newWidget("MC_Element")
	    .setOutlineColor(theme.app.color(120, 70, 0))
	    .setOutlineWeight(ref_size / 16F)
	    .setOutline(true)
	    );
	  theme.addModel("MC_Element_Single", theme.newWidget("MC_Element")
	    .setSize(ref_size*2.25, ref_size*1.125)
	    );
	  theme.addModel("MC_Element_Empty", theme.newWidget("MC_Element")
			    .setStandbyColor(theme.app.color(0, 0, 0, 0))
			    .setOutlineColor(theme.app.color(0, 0, 0, 0))
			    .setOutline(false)
	    .setSize(ref_size*2.25, ref_size*1.125)
	    );
	  theme.addModel("MC_Element_Double", theme.newWidget("MC_Element")
	    .setSize(ref_size*4.50, ref_size*1.125)
	    );
	  theme.addModel("MC_Element_Triple", theme.newWidget("MC_Element")
	    .setSize(ref_size*6.75, ref_size*1.125)
	    );
	  theme.addModel("MC_Element_Big", theme.newWidget("MC_Element")
	    .setSize(ref_size*4.50, ref_size*4.50)
	    );
	  theme.addModel("MC_Element_Bigger", theme.newWidget("MC_Element")
	    .setSize(ref_size*6.75, ref_size*6.75)
	    );
	  theme.addModel("MC_Element_Field", theme.newWidget("mc_ref")
			    .setStandbyColor(theme.app.color(10, 40, 80))
			    .setOutlineColor(theme.app.color(10, 110, 220))
			    .setOutlineSelectedColor(theme.app.color(130, 230, 240))
			    .setOutlineWeight(ref_size / 16F)
			    .setFont((int)(ref_size/2))
			    .setPosition(ref_size*3 / 16, ref_size * 2 / 16)
			    .setSize(ref_size*3.125, ref_size*0.875)
			    );
	  theme.addModel("MC_Element_Comment_Field", theme.newWidget("mc_ref")
			    .setStandbyColor(theme.app.color(0, 0, 50))
			    .setOutlineColor(theme.app.color(10, 110, 220))
			    .setOutlineSelectedColor(theme.app.color(130, 230, 240))
			    .setOutline(true)
			    .setOutlineWeight(ref_size / 16F)
			    .setPosition(ref_size*3 / 16, ref_size * 2 / 16)
		        .setTextAlignment(PConstants.LEFT, PConstants.TOP)
		        .setTextAutoReturn(true)
		        .setFont((int)(ref_size / 1.7))
			    );
	  theme.addModel("MC_Element_SField", theme.newWidget("MC_Element_Field")
	    .setPosition(ref_size*3 / 16, ref_size * 2 / 16)
	    .setSize(ref_size*1.375, ref_size*0.875)
	    );
	  theme.addModel("MC_Element_LField", theme.newWidget("MC_Element_Field")
	    .setPosition(ref_size*3 / 16, ref_size * 2 / 16)
	    .setSize(ref_size*5.25, ref_size*0.875)
	    );
	  theme.addModel("MC_Element_Text", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(40))
	    .setOutlineColor(theme.app.color(140))
	    .setOutlineSelectedColor(theme.app.color(200))
	    .setOutlineWeight(ref_size / 16F)
	    .setFont((int)(ref_size/2))
	    .setPosition(ref_size*3 / 16, ref_size * 3 / 16)
	    .setSize(ref_size*3.125, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_SText", theme.newWidget("MC_Element_Text")
	    .setPosition(ref_size*3 / 16, ref_size * 3 / 16)
	    .setSize(ref_size*1.375, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_LText", theme.newWidget("MC_Element_Text")
	    .setPosition(ref_size*3 / 16, ref_size * 3 / 16)
	    .setSize(ref_size*5.25, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_Button", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(10, 40, 80))
	    .setHoveredColor(theme.app.color(10, 110, 220))
	    .setClickedColor(theme.app.color(10, 90, 180))
	    .setOutlineColor(theme.app.color(10, 50, 100))
	    .setOutlineWeight(ref_size / 16F)
	    .setOutline(true)
	    .setFont((int)(ref_size/2))
	    .setPosition(ref_size*3 / 16, ref_size * 3 / 16)
	    .setSize(ref_size*3.125, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_SButton", theme.newWidget("MC_Element_Button")
	    //.setPX(-ref_size*0.25)
	    .setSize(ref_size*1.375, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_LButton", theme.newWidget("MC_Element_Button")
	    .setPosition(ref_size*3 / 16, ref_size * 3 / 16)
	    .setSize(ref_size*5.25, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_MiniButton", theme.newWidget("MC_Element_Button")
	    .setPosition(ref_size*1 / 16, ref_size * 5 / 16)
	    .setSize(ref_size*6 / 16, ref_size*0.5)
	    .setFont((int)(ref_size/3))
	    );
	  theme.addModel("MC_Element_Button_Selector_1", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 1 / 16)
	    .setSize(ref_size*0.875, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_Button_Selector_2", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 17 / 16)
	    .setSize(ref_size*0.875, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_Button_Selector_3", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 35 / 16)
	    .setSize(ref_size*0.875, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_Button_Selector_4", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 51 / 16)
	    .setSize(ref_size*0.875, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_Button_Selector_5", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 69 / 16)
	    .setSize(ref_size*0.875, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_Button_Selector_6", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 85 / 16)
	    .setSize(ref_size*0.875, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_XLButton_Selector_1", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * 4 / 16)
	    .setSize(ref_size*1.25, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_XLButton_Selector_2", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * (1.75+4 / 16))
	    .setSize(ref_size*1.25, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_XLButton_Selector_3", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * (3.25+4 / 16))
	    .setSize(ref_size*1.25, ref_size*0.75)
	    );
	  theme.addModel("MC_Element_XLButton_Selector_4", theme.newWidget("MC_Element_Button")
	    .setPX(ref_size * (4.75+4 / 16))
	    .setSize(ref_size*1.25, ref_size*0.75)
			    );
	  theme.addModel("MC_Grabber", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(70))
	    .setHoveredColor(theme.app.color(100))
	    .setClickedColor(theme.app.color(130))
	    .setOutlineWeight(ref_size / 9F)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(180, 150, 120))
	    .setLosange(true)
	    .setSize(ref_size*1, ref_size*0.75)
	    );
	  theme.addModel("MC_Selection_Grabber", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(220, 220, 0))
	    .setHoveredColor(theme.app.color(100))
	    .setClickedColor(theme.app.color(130))
	    .setOutlineWeight(ref_size / 5)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(150, 150, 0))
	    .setLosange(true)
	    .setSize(ref_size*0.75, ref_size*0.75)
	    );
	  theme.addModel("MC_Selection_Front", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(50, 0))
	    .setOutlineColor(theme.app.color(200))
	    .setOutlineConstant(true)
	    .setOutline(true)
	    .setOutlineWeight(ref_size * 2.0F / 40.0F)
	    .setPassif()
	    );
	  theme.addModel("MC_Grabber_Deployed", theme.newWidget("MC_Grabber")
	    .setStandbyColor(theme.app.color(70, 70, 0))
	    .setOutlineColor(theme.app.color(150, 150, 0))
	    );
	  theme.addModel("MC_Grabber_Selected", theme.newWidget("MC_Grabber")
	    .setStandbyColor(theme.app.color(220, 220, 0))
	    .setOutlineColor(theme.app.color(150, 150, 0))
	    );
	  theme.addModel("MC_Basic", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(100))
	    .setHoveredColor(theme.app.color(125))
	    .setClickedColor(theme.app.color(150))
	    .setOutlineWeight(ref_size / 8F)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(150))
	    .setLosange(true)
	    .setTrigger()
	    .setSize(ref_size*0.75, ref_size*0.75)
	    .setPosition(-ref_size*0.375, -ref_size*0.375)
	    );
	  theme.addModel("MC_Reduc", theme.newWidget("MC_Basic")
	    .setStandbyColor(theme.app.color(60))
	    .setHoveredColor(theme.app.color(120))
	    .setClickedColor(theme.app.color(160))
	    .setOutlineWeight(ref_size / 12)
	    .setSize(ref_size*0.4, ref_size*0.5)
	    .setPosition(-ref_size*0.75, ref_size*0.5)
	    );
	  theme.addModel("MC_Deploy", theme.newWidget("MC_Reduc")
	    .setSize(ref_size*0.55, ref_size*0.65).setPosition(-ref_size*0.375, -ref_size*0.25)
	    );
	  theme.addModel("MC_Prio", theme.newWidget("MC_Reduc")
	    .setSize(ref_size*0.75, ref_size*0.5)
	    );
	  theme.addModel("MC_Prio_View", theme.newWidget("MC_Prio")
	    .setStandbyColor(theme.app.color(40))
	    .setOutlineWeight(ref_size / 18)
	    .setOutlineColor(theme.app.color(110))
	    .setPosition(ref_size*0.125, ref_size*0.125).setBackground()
	    );
	  theme.addModel("MC_Prio_Sub", theme.newWidget("MC_Prio")
	    .setPosition(-ref_size*0.25, ref_size*0.125)
	    );
	  theme.addModel("MC_Prio_Add", theme.newWidget("MC_Prio")
	    .setPosition(ref_size*0.5, ref_size*0.125)
	    );
	  theme.addModel("MC_SpeTrig", theme.newWidget("MC_Reduc")
	    .setSize(ref_size*0.75, ref_size*0.75)
	    .alignDown().stackRight()
	    .setOutlineColor(theme.app.color(80))
	    );
	  theme.addModel("MC_Mirror", theme.newWidget("MC_SpeTrig")
	    .setPosition(-ref_size*1.875, ref_size*0.5)
	    );
	  theme.addModel("MC_Param", theme.newWidget("MC_SpeTrig")
	    .setPosition(-ref_size*1.375, ref_size*0.5)
	    );
	  theme.addModel("MC_Connect_Default", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(140, 140))
	    .setHoveredColor(theme.app.color(180, 180))
	    .setClickedColor(theme.app.color(180, 220))
	    .setOutlineWeight(ref_size / 12)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(100))
	    .setRound(true)
	    .setTrigger()
	    );
	  theme.addModel("MC_Connect_Out_Actif", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(140, 140))
	    .setHoveredColor(theme.app.color(180, 180))
	    .setClickedColor(theme.app.color(180, 220))
	    .setOutlineWeight(ref_size / 12)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(220, 140, 25))
	    .setRound(true)
	    );
	  theme.addModel("MC_Connect_Out_Passif", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(140, 0))
	    .setHoveredColor(theme.app.color(180, 180))
	    .setClickedColor(theme.app.color(180, 220))
	    .setOutlineWeight(ref_size / 12)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(60))
	    .setRound(true)
	    );
	  theme.addModel("MC_Connect_In_Actif", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(140, 140))
	    .setHoveredColor(theme.app.color(180, 180))
	    .setClickedColor(theme.app.color(180, 220))
	    .setOutlineWeight(ref_size / 12)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(220, 200, 25))
	    .setRound(true)
	    );
	  theme.addModel("MC_Connect_In_Passif", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(140, 0))
	    .setHoveredColor(theme.app.color(180, 180))
	    .setClickedColor(theme.app.color(180, 220))
	    .setOutlineWeight(ref_size / 12)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(60))
	    .setRound(true)
	    );
	  theme.addModel("MC_Connect_Linkable", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(140, 140))
	    .setHoveredColor(theme.app.color(180, 180))
	    .setClickedColor(theme.app.color(180, 220))
	    .setOutlineWeight(ref_size / 12)
	    .setOutline(true)
	    .setOutlineColor(theme.app.color(25, 180, 240))
	    .setRound(true)
	    );
	  theme.addModel("MC_Connect_Link", theme.newWidget("mc_ref")
	    .setStandbyColor(theme.app.color(200))
	    .setHoveredColor(theme.app.color(205, 205, 200))
//	    .setClickedColor(theme.app.color(220, 220, 200))
	    .setOutlineColor(theme.app.color(200, 100, 100))
	    .setClickedColor(theme.app.color(10, 110, 250))
	    .setLabelColor  (theme.app.color(20, 180, 240))
	    .setOutlineSelectedColor(theme.app.color(200, 200, 0))
	    .setOutlineWeight(ref_size / 10)
	    .setOutline(true)
	    .setRound(true)
	    );
	  theme.addModel("MC_Connect_View", theme.newWidget("mc_ref")
	    .setFont((int)(ref_size/2))
	    .setStandbyColor(theme.app.color(40))
	    .setOutline(false)
	    .setPosition(0, -ref_size*4/16)
	    .setSize(ref_size*1.5, ref_size*0.75)
	    );
	}
  
  
}
































    
