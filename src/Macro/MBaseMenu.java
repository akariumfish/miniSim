package Macro;

import java.util.ArrayList;

import UI.nColorPanel;
import UI.nExplorer;
import UI.nFrontPanel;
import UI.nFrontTab;
import UI.nSlide;
import UI.nWidget;
import processing.core.PConstants;
import sData.*;

public class MBaseMenu extends MBasic { 
	
	MBaseMenu addEventsBuildMenu(nRunnable r) { eventsBuildMenu.add(r); return this; }
	ArrayList<nRunnable> eventsBuildMenu;
	nFrontTab custom_tab;
	nFrontPanel bloc_front;  
	nExplorer bloc_viewer, preset_explorer;
	sStr new_preset_name; sBoo menu_open, menu_reduc;
	sVec menu_pos; sInt menu_tab;
	nRunnable grab_run, reduc_run, close_run, tab_run;
	nWidget match_flag;
	nRunnable menu_run;
	
	MBaseMenu(Macro_Sheet _sheet, String n, sValueBloc _bloc) { super(_sheet, n, _bloc); }
	MBaseMenu(Macro_Sheet _sheet, String n, sValueBloc _bloc, String spe) { 
		super(_sheet, n, _bloc, spe); }
	void init() {
		eventsBuildMenu = new ArrayList<nRunnable>();
		menu_run = new nRunnable() { public void run() { 
			menu(); grab_run.run(); tab_run.run(); reduc_run.run(); }};
		new_preset_name = newStr("preset_name", "pname", "new"); 
		menu_open = newBoo("menu_open", "menuop", false); 
		menu_pos = newVec("menu_pos"); 
	    menu_reduc = newBoo(false, "menu_reduc");
	    menu_tab = newInt(0, "menu_tab");
	    grab_run = new nRunnable() { public void run() { if (bloc_front != null) 
	        menu_pos.set(bloc_front.grabber.getLocalX(), 
	        				bloc_front.grabber.getLocalY()); } };
	    reduc_run = new nRunnable() { public void run() {
	    		if (bloc_front != null) menu_reduc.set(bloc_front.collapsed); } };
	    tab_run = new nRunnable() { public void run() {
	    		if (bloc_front != null) menu_tab.set(bloc_front.current_tab_id); } };
	    close_run = new nRunnable() { public void run() { 
	    		menu_open.set(false); } };
	}
	void init_end() {
		super.init_end();
	    if (menu_open.get()) mmain().inter.addEventTwoFrame(new nRunnable() { public void run() {
    		menu(); } });
	}
	void build_param() {
		addInputBang(0, "menu", menu_run);
		addTrigS(1, "menu", menu_run);
	}
	void build_normal() {
		addInputBang(0, "menu", menu_run);
		addTrigS(1, "menu", menu_run);
	}
	public MBaseMenu clear() {
		if (bloc_front != null) bloc_front.clear();
		super.clear(); 
		return this; }
	public MBaseMenu toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
	void menu() {
	    menu_open.set(true);
	    build_bloc_menu();
	    if (bloc_front != null) { 
		    if (menu_pos.get().x == 0 && menu_pos.get().y == 0) grab_run.run();
		    if (menu_open.get()) {
				if (!(menu_pos.get().x == 0 && menu_pos.get().y == 0)) 
					bloc_front.grabber.setPosition(menu_pos.get());
				if (menu_reduc.get()) bloc_front.collapse(); else bloc_front.popUp();
				bloc_front.setTab(menu_tab.get());
		    }
//			setup_send.set(true);
		    bloc_front.grabber.addEventDrag(grab_run); 
		    bloc_front.addEventTab(tab_run)
			.addEventCollapse(reduc_run).addEventClose(close_run);  
	    }
		grab_run.run(); tab_run.run(); reduc_run.run(); 
	}
	public void build_custom_menu(nFrontPanel sheet_front) {}
	
	public void build_bloc_menu() {
		if (bloc_front == null) {
		  menu_open.set(true);	
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
		  
		  custom_tab = bloc_front.addTab("Auto");
		
		  custom_tab.getShelf()
		    .addDrawer(10.25, 0.05)
//		    .addModel("Label-S10/0.75", "-  Auto  -").setFont((int)(ref_size/1.5)).getShelf()
//		    .addSeparator(0.125)
		    ;
		  nRunnable.runEvents(eventsBuildMenu);
		  
		  build_custom_menu(bloc_front);
		  
		  if (bloc_front.collapsed) {
			  bloc_front.popUp();
			  bloc_front.collapse();
		  } else {
			  bloc_front.collapse();
			  bloc_front.popUp();
		  }
		  bloc_front.toLayerTop();
		  
		  bloc_front.addEventClose(new nRunnable(this) { public void run() { 
		    if (preset_explorer != null) mmain().presets_explorers.remove(preset_explorer);
		    bloc_front = null;
		    menu_open.set(false); }});
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
  

	public sRun newGlobalRun(String ref, boolean param_shown, nRunnable rn) {
		sRun srun = newRun(ref, rn);
		sBoo sacc = newBoo(false, "access_"+ref);
		
		addToInitend(new nRunnable() { public void run() {
			sacc.addEventChange(run_rebuild); }});
		addToBuildNorm(new nRunnable() { public void run() {
			if (sacc.get()) newRowValue(srun); }});
		addToBuildParam(new nRunnable() { public void run() {
			if (param_shown || sacc.get()) newRowValue(srun); }});
		
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addCtrlModel("Auto_Button-S3-P1", srun.ref).setLinkedValue(srun)
	        		.setSX(ref_size * 8.375F).setPX(ref_size * 0.5F).getDrawer()
	        .addLinkedModel("Button_Check_AutoMacro-SS1-P9").setLinkedValue(sacc)
	        .getShelf()
	        .addSeparator(0.125);
	    } });
	    return srun;
	}
	public void globalBin(sValue val1, boolean param_shown) {
		if (val1.type.equals("run") || val1.type.equals("boo")) {
			sBoo sacc1 = newBoo(false, "access_"+val1.ref);
			
			addToInitend(new nRunnable() { public void run() {
				sacc1.addEventChange(run_rebuild); }});
			addToBuildNorm(new nRunnable() { public void run() {
				if (sacc1.get()) newRowValue(val1); }});
			addToBuildParam(new nRunnable() { public void run() {
				if (param_shown || sacc1.get()) newRowValue(val1); }});
			
		    addEventsBuildMenu(new nRunnable() { public void run() { 
		      if (custom_tab != null) custom_tab.getShelf()
		        .addDrawer(10, 1)
		        .addLinkedModel("Auto_Button-S3-P1", val1.ref).setLinkedValue(val1)
		        		.setSX(ref_size * 8.375F).setPX(ref_size * 0.5F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P9").setLinkedValue(sacc1)
		        	.getShelf()
		        .addSeparator(0.125);
		    } });
		}
	}
	public void globalBin(sValue val1, sValue val2, boolean param_shown) {
		if ( (val1.type.equals("run") || val1.type.equals("boo")) &&
			 (val2.type.equals("run") || val2.type.equals("boo")) ) {
			sBoo sacc1 = newBoo(false, "access_"+val1.ref);
			sBoo sacc2 = newBoo(false, "access_"+val2.ref);
			
			addToInitend(new nRunnable() { public void run() {
				sacc1.addEventChange(run_rebuild); 
				sacc2.addEventChange(run_rebuild); }});
			addToBuildNorm(new nRunnable() { public void run() {
				if (sacc1.get()) newRowValue(val1); 
				if (sacc2.get()) newRowValue(val2); }});
			addToBuildParam(new nRunnable() { public void run() {
				if (param_shown || sacc1.get()) newRowValue(val1); 
				if (param_shown || sacc2.get()) newRowValue(val2); }});
			
		    addEventsBuildMenu(new nRunnable() { public void run() { 
		      if (custom_tab != null) custom_tab.getShelf()
		        .addDrawer(10, 1)
		        .addLinkedModel("Auto_Button-S2-P1", val1.ref).setLinkedValue(val1)
		        		.setSX(ref_size * 3.5F).setPX(ref_size * 0.5F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P5").setLinkedValue(sacc1)
		        		.setPX(ref_size * 4.375F).getDrawer()
		        .addLinkedModel("Auto_Button-S2-P3", val2.ref).setLinkedValue(val2)
		        		.setSX(ref_size * 3.375F).setPX(ref_size * 5.5F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P9").setLinkedValue(sacc2)
		        .getShelf()
		        .addSeparator(0.125);
		    } });
		}
	}
	public void globalBin(sValue val1, sValue val2, sValue val3, boolean param_shown) {
		if ( (val1.type.equals("run") || val1.type.equals("boo")) &&
			 (val2.type.equals("run") || val2.type.equals("boo")) &&
			 (val3.type.equals("run") || val3.type.equals("boo")) ) {
			sBoo sacc1 = newBoo(false, "access_"+val1.ref);
			sBoo sacc2 = newBoo(false, "access_"+val2.ref);
			sBoo sacc3 = newBoo(false, "access_"+val3.ref);
			
			addToInitend(new nRunnable() { public void run() {
				sacc1.addEventChange(run_rebuild); 
				sacc2.addEventChange(run_rebuild); 
				sacc3.addEventChange(run_rebuild); }});
			addToBuildNorm(new nRunnable() { public void run() {
				if (sacc1.get()) newRowValue(val1); 
				if (sacc2.get()) newRowValue(val2); 
				if (sacc3.get()) newRowValue(val3); }});
			addToBuildParam(new nRunnable() { public void run() {
				if (param_shown || sacc1.get()) newRowValue(val1); 
				if (param_shown || sacc2.get()) newRowValue(val2); 
				if (param_shown || sacc3.get()) newRowValue(val3); }});
			
		    addEventsBuildMenu(new nRunnable() { public void run() { 
		      if (custom_tab != null) custom_tab.getShelf()
		        .addDrawer(10, 1)
		        .addLinkedModel("Auto_Button-S2-P1", val1.ref).setLinkedValue(val1)
		        		.setSX(ref_size * 2.0F).setPX(ref_size * 0.25F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P1").setLinkedValue(sacc1)
		        		.setPX(ref_size * 2.5F).getDrawer()
		        .addLinkedModel("Auto_Button-S2-P1", val2.ref).setLinkedValue(val2)
		        		.setSX(ref_size * 2.0F).setPX(ref_size * 3.5F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P5").setLinkedValue(sacc2)
		        		.setPX(ref_size * 5.75F).getDrawer()
		        .addLinkedModel("Auto_Button-S2-P3", val3.ref).setLinkedValue(val3)
		        		.setSX(ref_size * 2.0F).setPX(ref_size * 6.75F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P9")
		        		.setLinkedValue(sacc3).setPX(ref_size * 9.0F).getShelf()
		        .addSeparator(0.125);
		    } });
		}
	}
	public void globalSBin(sValue val1, sValue val2, sValue val3, boolean param_shown) {
		if ( (val1.type.equals("run") || val1.type.equals("boo")) &&
			 (val2.type.equals("run") || val2.type.equals("boo")) &&
			 (val3.type.equals("run") || val3.type.equals("boo")) ) {
			sBoo sacc1 = newBoo(false, "access_"+val1.ref);
			sBoo sacc2 = newBoo(false, "access_"+val2.ref);
			sBoo sacc3 = newBoo(false, "access_"+val3.ref);
			
			addToInitend(new nRunnable() { public void run() {
				sacc1.addEventChange(run_rebuild); 
				sacc2.addEventChange(run_rebuild); 
				sacc3.addEventChange(run_rebuild); }});
			addToBuildNorm(new nRunnable() { public void run() {
				if (sacc1.get()) newDRowValue(val1); 
				if (sacc2.get()) newDRowValue(val2); 
				if (sacc3.get()) newDRowValue(val3); }});
			addToBuildParam(new nRunnable() { public void run() {
				if (param_shown || sacc1.get()) newDRowValue(val1); 
				if (param_shown || sacc2.get()) newDRowValue(val2); 
				if (param_shown || sacc3.get()) newDRowValue(val3); }});
			
		    addEventsBuildMenu(new nRunnable() { public void run() { 
		      if (custom_tab != null) custom_tab.getShelf()
		        .addDrawer(10, 1)
		        .addLinkedModel("Auto_Button-S2-P1", val1.ref).setLinkedValue(val1)
		        		.setSX(ref_size * 2.0F).setPX(ref_size * 0.25F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P1").setLinkedValue(sacc1)
		        		.setPX(ref_size * 2.5F).getDrawer()
		        .addLinkedModel("Auto_Button-S2-P1", val2.ref).setLinkedValue(val2)
		        		.setSX(ref_size * 2.0F).setPX(ref_size * 3.5F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P5").setLinkedValue(sacc2)
		        		.setPX(ref_size * 5.75F).getDrawer()
		        .addLinkedModel("Auto_Button-S2-P3", val3.ref).setLinkedValue(val3)
		        		.setSX(ref_size * 2.0F).setPX(ref_size * 6.75F).getDrawer()
		        .addLinkedModel("Button_Check_AutoMacro-SS1-P9")
		        		.setLinkedValue(sacc3).setPX(ref_size * 9.0F).getShelf()
		        .addSeparator(0.125);
		    } });
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
	public void menuRun(sRun r1) {
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addCtrlModel("Auto_Button-S3-P1", r1.ref).setLinkedValue(r1).getShelf()
	        .addSeparator(0.125);
	    } });
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
	public sCol menuColor(int v, String r, String s) {
	    sCol f = newCol(r, s, v);
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
	        .addLinkedModel("Auto_Button-S3-P1", f.ref).setLinkedValue(f).getShelf()
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
	public void globalFltIncr(sFlt f, float _f, boolean param_shown) {
		sBoo sacc1 = newBoo(false, "access_"+f.ref);
		
		addToInitend(new nRunnable() { public void run() {
			sacc1.addEventChange(run_rebuild); }});
		addToBuildNorm(new nRunnable() { public void run() {
			if (sacc1.get()) newRowValue(f); }});
		addToBuildParam(new nRunnable() { public void run() {
			if (param_shown || sacc1.get()) newRowValue(f); }});

		nObject obj = new nObject();
		obj.obj = _f;
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawerActIncrValue(sacc1, f, (Float)obj.obj, 10, 1, true)
	        .addSeparator(0.125);
	    } });
	}
	public void globalFltFact(sFlt f, float _f, boolean param_shown) {
		sBoo sacc1 = newBoo(false, "access_"+f.ref);
		
		addToInitend(new nRunnable() { public void run() {
			sacc1.addEventChange(run_rebuild); }});
		addToBuildNorm(new nRunnable() { public void run() {
			if (sacc1.get()) newRowValue(f); }});
		addToBuildParam(new nRunnable() { public void run() {
			if (param_shown || sacc1.get()) newRowValue(f); }});

		nObject obj = new nObject();
		obj.obj = _f;
	    addEventsBuildMenu(new nRunnable() { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawerActFactValue("", sacc1, f, _f, 10, 1, true)
	        .addSeparator(0.125);
	    } });
	}
	sFlt menuFltIncr(sFlt f, float _f) {
		nObject obj = new nObject();
		obj.obj = _f;
		addEventsBuildMenu(new nRunnable() { public void run() { 
		  if (custom_tab != null) custom_tab.getShelf()
		  .addDrawerIncrValue(f, (Float)obj.obj, 10, 1)
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
	public void menuFltFact(sFlt f, float _f) {
		f.ctrl_factor = _f;
		addEventsBuildMenu(new nRunnable(f) { public void run() { 
		  if (custom_tab != null) custom_tab.getShelf()
		  .addDrawerFactValue(((sFlt)builder), ((sFlt)builder).ctrl_factor, 10, 1)
		  .addSeparator(0.125);
		} });
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
	public sInt menuIntIncr(sInt f, float _f) {
//		f.ctrl_factor = _f;
		nObject obj = new nObject();
		obj.obj = _f;
		addEventsBuildMenu(new nRunnable(obj) { public void run() { 
		  if (custom_tab != null) custom_tab.getShelf()
		  .addDrawerIncrValue(f, (float)((nObject)builder).obj, 10, 1)
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
