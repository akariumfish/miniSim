package RApplet;

import java.util.ArrayList;

import Macro.Macro_Main;
import Macro.Macro_Sheet;
import UI.*;
import processing.core.PConstants;
import sData.*;
import z_old_specialise.Sheet_Specialize;

/*
 Interface
   Inputs, DataHolding
   class CameraView 
     name
     pos and scale as svalue
     map<name, CameraView> : views
     name of current view as svalue
   
   drawing_pile screen_draw, cam_draw
   hover_pile screen and cam
   
   event hoverpilesearch both no find
   
   list<runnable> frameEvents
   
   frame()
     hover_pile.search() if screen found dont do cam
     run frameEvents
     update cam view from inputs
     clear screen
     draw grid if needed
     draw cam then screen from their pov
     to control when to screenshot maybe do it in a Drawer
 
 
 
 */
//

public class sInterface {
  
  public void quick_open() {
    new nFilePicker(screen_gui, taskpanel, savepath_value, true, "0pen", true)
      .addFilter("sdata")
      .addEventChoose(new nRunnable() { public void run() { 
        setup_load();
      } } );
  }
  
  public void save_to() {
    new nFilePicker(screen_gui, taskpanel, savepath_value, false, "Save to", true)
      .addFilter("sdata")
      .addEventChoose(new nRunnable() { public void run() { 
        full_data_save();
      } } );
  }
  
  public void save_as() {
    new nTextPicker(screen_gui, taskpanel, savepath_value, "Save as")
      .addSuffix("sdata")
      .addEventChoose(new nRunnable() { public void run() { 
        full_data_save();
      } } );
  }

  public void filesManagement() {
    if (files_panel == null) {
      files_panel = new nWindowPanel(screen_gui, taskpanel, "Files");
      files_panel.setSpace(0.25F);
      
      files_panel.getShelf()
        .addSeparator(0.25)
        .addDrawer(1)
          .addCtrlModel("Button-S4", "Select file")
          .setRunnable(new nRunnable() { public void run() { 
            new nFilePicker(screen_gui, taskpanel, filempath_value, true, "Select file to explore", false)
              .addFilter("sdata");
          } } )
          .setInfo("choose a .sdata to explore in the manager")
          //.getDrawer()
          //.addCtrlModel("Button-S3-P2", "New file")
          //.setRunnable(new Runnable() { public void run() { 
            
          //} } )
          .getShelf()
        .addSeparator(0.25)
        .addDrawer(0.75)
          .addModel("Label-S4", "Selected File :")
            .setTextAlignment(PConstants.LEFT, PConstants.CENTER).setPY(-0.2*ref_size).getDrawer()
          .addLinkedModel("Field-SS3").setLinkedValue(filempath_value).setPX(ref_size*5).getShelf()
        .addSeparator(0.25)
        .addDrawer(1)
          .addModel("Label-S4", "File :                                   ").getDrawer()
          .addCtrlModel("Button_Outline-S2", "Save")
            .setRunnable(new nRunnable() { public void run() { file_explorer_save(); } } )
            .setPX(ref_size*4)
            .getDrawer()
          .addCtrlModel("Button_Outline-S2", "Open")
            .setRunnable(new nRunnable() { public void run() { file_explorer_load(); } } )
            .setPX(ref_size*7)
            .getShelf()
            ;
        
      
      files_panel.addShelf()
        .addSeparator(0.25)
        .addDrawer(1)
          .addCtrlModel("Button_Small_Text_Outline-S3-P1", "close file")
            .setRunnable(new nRunnable() { public void run() { 
              if (explored_bloc != null) explored_bloc.clear();
              explored_bloc = null;
              file_explorer.setStrtBloc(null);
              file_explorer.update(); data_explorer.update(); update_list(); 
            } } )
            .getDrawer()
          .addCtrlModel("Button_Small_Text_Outline-S3-P2", "go to /")
            .setRunnable(new nRunnable() { public void run() { 
              data_explorer.setStrtBloc(data); 
              data_explorer.update(); update_list(); 
            } } )
            .setInfo("send data explorer to root")
            .getShelf()
        .addDrawer(10, 1)
          .addCtrlModel("Button_Small_Text_Outline-S3-P1", "delete file bloc")
            .setRunnable(new nRunnable() { public void run() { 
              if (file_explorer.selected_bloc != null) { file_explorer.selected_bloc.clear(); }
              file_explorer.update();
            } } )
            .setInfo("delete selected bloc from file")
            .getDrawer()
          .addCtrlModel("Button_Small_Text_Outline-S3-P2", "delete data bloc")
            .setRunnable(new nRunnable() { public void run() { 
              if (data_explorer.selected_bloc != null) { data_explorer.selected_bloc.clear(); }
              data_explorer.update();
            } } )
            .setInfo("delete selected bloc from data")
            .getShelf()
        .addDrawer(10, 1)
          //.addCtrlModel("Button_Small_Text_Outline-S3-P1", "Quick Save")
          //  .setRunnable(new Runnable() { public void run() { full_data_save(); } } )
          //  .getDrawer()
          //.addCtrlModel("Button_Small_Text_Outline-S3-P2", "Quick Load")
          //  .setRunnable(new Runnable() { public void run() { setup_load(); } } ) //full_data_load();
          //  .getShelf()
        ;
        
      
      files_panel.getShelf(0)
        .addSeparator(0.25)
        .addDrawer(2)
          .addCtrlModel("Button_Small_Text_Outline-S3", "COPY BLOC\nINTO DATA")
            .setRunnable(new nRunnable() { public void run() { copy_file_to_data(); } } )
            .setPX(ref_size*0).setSY(ref_size*2)
            .setInfo("copy selected bloc from file to current bloc in data")
            .getDrawer()
          //.addCtrlModel("Button_Small_Text_Outline-S3", "TRANSFER\nFILE VALUES\nTO DATA")
          //  .setRunnable(new Runnable() { public void run() { transfer_file_to_data(); } } )
          //  .setPX(ref_size*4).setSY(ref_size*2)
            ;
            
      match_flag = files_panel.getShelf(0)
        .getLastDrawer()
          .addModel("Label_DownLight_Back_Downlight_Outline-S3", "MATCHING\nBLOCS PRINT")
            .setPX(ref_size*8).setSY(ref_size*2);
      
      files_panel.getShelf(0)
        .getLastDrawer()
          //.addCtrlModel("Button_Small_Text_Outline-S3", "TRANSFER\nDATA VALUES\nTO FILE")
          //  .setRunnable(new Runnable() { public void run() { transfer_data_to_file(); } } )
          //  .setPX(ref_size*12).setSY(ref_size*2)
          //  .getDrawer()
          .addCtrlModel("Button_Small_Text_Outline-S3", "COPY BLOC\nINTO FILE")
            .setRunnable(new nRunnable() { public void run() { copy_data_to_file(); } } )
            .setPX(ref_size*16).setSY(ref_size*2)
            .setInfo("copy selected bloc from data to current bloc in file")
            .getShelf()
        ;
        
      file_explorer = files_panel.getShelf(0)
        .addExplorer()
          .addEventChange(new nRunnable() { public void run() { update_list(); } } )
          ;
      
      files_panel.getShelf(0)
        .addSeparator(0.25)
        .addDrawer(0.75)
          .addModel("Label-S4", "logs :")
            .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
          .addCtrlModel("Button_Small_Text_Outline-S3-P1", "Update explorers")
          .setRunnable(new nRunnable() { public void run() { 
            data_explorer.update(); file_explorer.update(); } } )
          .setPX(ref_size*13)
          .getShelf()
        .addSeparator(0.25)
        .addDrawer(1)
          //.addCtrlModel("Button_Small_Text_Outline-S3-P1", "Add to file")
          //  .setRunnable(new Runnable() { public void run() { 
          //    templ_prst_add_to_file();
          //  } } ).setInfo("Add templates and presets to file")
          //  .getDrawer()
          //.addCtrlModel("Button_Small_Text_Outline-S3-P2", "Load file")
          //  .setRunnable(new Runnable() { public void run() { 
          //    templ_prst_get_from_file();
          //  } } ).setInfo("Add file templates and presets")
          //  .getDrawer()
            
          .addModel("Button_Small_Text_Outline-S1-P1", "LA")
            .setSwitch().setSwitchState(app.save_log_all)
            .addEventSwitchOn(new nRunnable() { public void run() { 
            	app.save_log_all = true; } } ).setInfo("log all-slow")
            .addEventSwitchOff(new nRunnable() { public void run() { 
            	app.save_log_all = false; } } )
            .getDrawer()
            
          .addModel("Button_Small_Text_Outline-S1-P2", "LE")
            .setSwitch().setSwitchState(log_ext_save.get()).setInfo("log at exit-saved param")
            .addEventSwitchOn(new nRunnable() { public void run() { 
            	app.save_log_exit = true; log_ext_save.set(app.save_log_exit); } } )
            .addEventSwitchOff(new nRunnable() { public void run() { 
            	app.save_log_exit = false; log_ext_save.set(app.save_log_exit); } } )
            .getDrawer()
            
          .addModel("Button_Small_Text_Outline-S1-P4", "C")
            .setSwitch().setSwitchState(app.DEBUG)
            .addEventSwitchOn(new nRunnable() { public void run() { 
            	app.DEBUG = true; } } ).setInfo("log")
            .addEventSwitchOff(new nRunnable() { public void run() { 
            	app.DEBUG = false; } } )
            .getDrawer()
            
          .addModel("Button_Small_Text_Outline-S1-P6", "M")
            .setSwitch().setSwitchState(app.DEBUG_MACRO)
            .addEventSwitchOn(new nRunnable() { public void run() { 
            	app.DEBUG_MACRO = true; } } ).setInfo("macro logs")
            .addEventSwitchOff(new nRunnable() { public void run() { 
            	app.DEBUG_MACRO = false; } } )
            .getDrawer()
            
	        .addModel("Button_Small_Text_Outline-S1-P7", "P")
	          .setSwitch().setSwitchState(app.DEBUG_PACKET)
	          .addEventSwitchOn(new nRunnable() { public void run() { 
	          	app.DEBUG_PACKET = true; } } ).setInfo("macro packet process logs")
	          .addEventSwitchOff(new nRunnable() { public void run() { 
	          	app.DEBUG_PACKET = false; } } )
	          .getDrawer()
          //.addModel("Button_Small_Text_Outline-S1-P7", "")
          //  //.setSwitch()
          //  //.addEventSwitchOn(new Runnable() { public void run() { 
          //  //  DEBUG_SAVE_FULL = true; } } ).setInfo("slogs")
          //  //.addEventSwitchOff(new Runnable() { public void run() { 
          //  //  DEBUG_SAVE_FULL = false; } } )
          //  .getDrawer()
            
          .addModel("Button_Small_Text_Outline-S1-P9", "")
            .setSwitch().setSwitchState(app.DEBUG_NOFILL)
            .addEventSwitchOn(new nRunnable() { public void run() { 
            	app.DEBUG_NOFILL = true; app.DEBUG_HOVERPILE = true; } } )
            .addEventSwitchOff(new nRunnable() { public void run() { 
            	app.DEBUG_NOFILL = false; app.DEBUG_HOVERPILE = false; } } )
        ;
      data_explorer = files_panel.getShelf(1)
        .addSeparator(2.25)
        .addExplorer()
          .setStrtBloc(data)
          .addValuesModifier(taskpanel)
          .addEventChange(new nRunnable() { public void run() { update_list(); } } )
          ;
      
      data_explorer.getShelf()
        //.addSeparator(0.25)
        //.addDrawer(1)
        //  .addCtrlModel("Button_Small_Text_Outline-S3-P1", "Update")
        //    .setRunnable(new Runnable() { public void run() { 
        //      data_explorer.update();
        //      file_explorer.update();
        //    } } )
          //  .getDrawer()
          //.addCtrlModel("Button_Small_Text_Outline-S3-P2", "")
          //  .setRunnable(new Runnable() { public void run() { 
          //    templ_prst_get_from_file();
          //  } } ).setInfo("Add file templates and presets")
        ;
      
      files_panel.addEventClose(new nRunnable(this) { public void run() { files_panel = null; }});
      addEventSetup(new nRunnable() { public void run() { data_explorer.update(); file_explorer.update(); } } );
    } else files_panel.popUp();
  }
  
  void update_list() {
    if (data_explorer.selected_bloc != null && file_explorer.selected_bloc != null) {
      if (file_explorer.selected_bloc.getHierarchy(true)
            .equals(data_explorer.selected_bloc.getHierarchy(true))) {
        match_flag.setLook(screen_gui.theme, "Label_HightLight_Back_Highlight_Outline-S3");
      } else match_flag.setLook(screen_gui.theme, "Label_DownLight_Back_Downlight_Outline-S3");
    } else match_flag.setLook(screen_gui.theme, "Label_DownLight_Back_Downlight_Outline-S3");
  }
  
  void file_explorer_save() {
    if (file_explorer != null && !filempath_value.get().equals("default.sdata")) {
      file_savebloc.clear();
      file_explorer.starting_bloc.preset_to_save_bloc(file_savebloc);
      file_savebloc.save_to(filempath_value.get());
    }
  }
  void file_explorer_load() {
    file_savebloc.clear();
    file_savebloc.load_from(filempath_value.get());
    if (explored_bloc != null) explored_bloc.clear();
    explored_bloc = data.newBloc(file_savebloc, "file");
    file_explorer.setStrtBloc(explored_bloc);
  }
  
  
  void templ_prst_add_to_file() {
    if (file_explorer.starting_bloc.getBloc("Template") != null) {
      macro_main.saved_template.runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (file_explorer.starting_bloc.getBloc("Template").getBloc(bloc.ref) == null)
          file_explorer.starting_bloc.getBloc("Template").newBloc(b, bloc.ref);
      }});
    }
    if (file_explorer.starting_bloc.getBloc("Preset") != null) {
      macro_main.saved_preset.runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (file_explorer.starting_bloc.getBloc("Preset").getBloc(bloc.ref) == null)
          file_explorer.starting_bloc.getBloc("Preset").newBloc(b, bloc.ref);
      }});
    }
    macro_main.template_explorer.update(); 
    for (nExplorer e : macro_main.presets_explorers) e.update(); 
  }
  void templ_prst_get_from_file() {
    if (file_explorer.starting_bloc.getBloc("Template") != null) {
      file_explorer.starting_bloc.getBloc("Template").runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        if (macro_main.saved_template.getBloc(bloc.ref) == null)
          macro_main.saved_template.newBloc(b, bloc.ref);
      }});
    }if (file_explorer.starting_bloc.getBloc("Preset") != null) {
      file_explorer.starting_bloc.getBloc("Preset").runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        Save_Bloc b = new Save_Bloc("");
        bloc.preset_to_save_bloc(b);
        macro_main.saved_preset.newBloc(b, bloc.base_ref);
        if (macro_main.saved_preset.getBloc(bloc.ref) == null)
          macro_main.saved_preset.newBloc(b, bloc.ref);
      }});
    }
    if (macro_main.template_explorer != null) macro_main.template_explorer.update(); 
    for (nExplorer e : macro_main.presets_explorers) e.update(); 
  }
  
  void copy_file_to_data() {
    if (data_explorer.explored_bloc != null && file_explorer.selected_bloc != null) {
      file_savebloc.clear();
      file_explorer.selected_bloc.preset_to_save_bloc(file_savebloc);
      data_explorer.explored_bloc.newBloc(file_savebloc, file_explorer.selected_bloc.ref);
      data_explorer.update();
    } 
  }
  void copy_data_to_file() {
    if (data_explorer.selected_bloc != null && file_explorer.explored_bloc != null) {
      file_savebloc.clear();
      data_explorer.selected_bloc.preset_to_save_bloc(file_savebloc);
      file_explorer.explored_bloc.newBloc(file_savebloc, data_explorer.selected_bloc.ref);
      file_explorer.update();
    } 
  }
  //void transfer_file_to_data() {
  //  if (data_explorer.selected_bloc != null && file_explorer.selected_bloc != null &&
  //      file_explorer.selected_bloc.getHierarchy(true)
  //        .equals(data_explorer.selected_bloc.getHierarchy(true))) {
  //    file_savebloc.clear();
  //    file_explorer.selected_bloc.preset_to_save_bloc(file_savebloc);
  //    data_explorer.selected_bloc.load_from_bloc(file_savebloc);
  //    data_explorer.update();
  //    //update_list();
  //  } 
  //}
  //void transfer_data_to_file() {
  //  if (data_explorer.selected_bloc != null && file_explorer.selected_bloc != null &&
  //      file_explorer.selected_bloc.getHierarchy(true)
  //        .equals(data_explorer.selected_bloc.getHierarchy(true))) {
  //    file_savebloc.clear();
  //    data_explorer.selected_bloc.preset_to_save_bloc(file_savebloc);
  //    file_explorer.selected_bloc.load_from_bloc(file_savebloc);
  //    file_explorer.update();
  //    //update_list();
  //  } 
  //}

  public void full_data_save() {

		conf_save();
		
    macro_main.saving_database();
    if (!savepath_value.get().equals("default.sdata")) {
  	  app.logln("full_data_save to "+savepath_value.get());
      file_savebloc.clear(); 
      interface_bloc.preset_to_save_bloc(file_savebloc); 
      file_savebloc.save_to(savepath_value.get());
      prev_savepth.set(savepath_value.get());
    } else {
      new nTextPop(screen_gui, taskpanel, "cant save on default file");
    } }
  //void full_data_load() {
  //  file_savebloc.clear();
  //  file_savebloc.load_from(savepath_value.get());
  //  interface_bloc.load_from_bloc(file_savebloc);
  //  file_explorer.update(); data_explorer.update(); }
  
  void build_default_ui(float ref_size) {
    taskpanel = new nTaskPanel(screen_gui, ref_size, 0.125F);
    
    if (!show_taskpanel.get()) taskpanel.reduc();
    taskpanel.addEventReduc(new nRunnable() { public void run() { 
      show_taskpanel.set(!taskpanel.hide); }});
      
    savepath_value = new sStr(interface_bloc, savepath, "savepath", "spath");
    savepath_value.addEventChange(new nRunnable() { public void run() { 
      app.app_grab.setText(version_title + "  -  "+savepath_value.get());
      //init in inter.frame() > is_starting if bloc
     } } );
    
    filempath_value = new sStr(interface_bloc, "", "filempath", "fmpath");
    file_savebloc = new Save_Bloc(savepath);
    //filesManagement();
  }
  public nWidget match_flag;
  public  nWindowPanel files_panel;
  public String version_title = "MiniSim 0.3.2";
  public String savepath = "save.sdata";
  public sStr savepath_value, filempath_value;
  public sBoo auto_load, log_ext_save;
  public Save_Bloc file_savebloc;
  public sValueBloc explored_bloc, setup_bloc;
  public nExplorer file_explorer, data_explorer;
  public nTaskPanel taskpanel;
  public float ref_size;
  
  public sBoo show_taskpanel;
  
  
  public Rapp app;
  public sInput input;
  public DataHolder data; 
  public sValueBloc interface_bloc;

  public nTheme gui_theme;
  public nGUI screen_gui;
  public nGUI cam_gui;

  public Camera cam;
  public sFramerate framerate;

  public Macro_Main macro_main;
  
  public User user;
  
  public Rect screen_view;
  /*
  method for sStr : pack unpack
    get string list + token > convert to string
    inversement
  
  */

  sInterface(Rapp _a, float s) {
	app = _a;
	Save_Bloc.app = app;
    ref_size = s;
    user = new User("user");
    input = new sInput(app);
    data = new DataHolder(app);
    interface_bloc = new sValueBloc(data, "Interface");
    
    conf_init();
    
    gui_theme = new nTheme(app, ref_size);
    screen_gui = new nGUI(input, gui_theme, ref_size);
    screen_view = new Rect(app.screen_0_x, app.screen_0_y, 
    						app.screen_width, app.screen_height);
    screen_gui.setView(screen_view);
    cam_gui = new nGUI(input, gui_theme, ref_size);
    
    show_taskpanel = interface_bloc.newBoo("show_taskpanel", "taskpanel", true);
    show_taskpanel.addEventChange(new nRunnable(this) { public void run() { 
      if (taskpanel != null && taskpanel.hide == show_taskpanel.get()) taskpanel.reduc();
    }});
    
    log_ext_save = interface_bloc.newBoo("log_ext_save", "log_ext_save", true);
    
    build_default_ui(ref_size);
//    
    macro_main = new Macro_Main(this);

    framerate = new sFramerate(app, macro_main.value_bloc, 60);
    
    cam = new Camera(input, macro_main.value_bloc)
      .addEventZoom(new nRunnable() { public void run() { cam_gui.updateView(); } } )
      .addEventMove(new nRunnable() { public void run() { cam_gui.updateView(); } } );

    screen_gui.addEventFound(new nRunnable() { public void run() { 
      cam.GRAB = false; cam_gui.hoverpile_passif = true; } } )
    .addEventNotFound(new nRunnable() { public void run() { 
      cam.GRAB = true; cam_gui.hoverpile_passif = false; } } );
    
    cam_gui
      .setMouse(cam.mouse).setpMouse(cam.pmouse)
      .setView(cam.cam_view)
      .addEventFound(new nRunnable() { public void run() { cam.GRAB = false; } } )
      .addEventNotFound(new nRunnable() { public void run() { 
        if (!screen_gui.hoverable_pile.found) { cam.GRAB = true; runEvents(eventsHoverNotFound); } } } );
    
    auto_load = macro_main.newBoo(false, "auto_load", "autoload");
    
    quicksave_run = macro_main.newRun("quicksave", "qsave", 
      new nRunnable() { public void run() { full_data_save(); } } );
    quickload_run = macro_main.newRun("quickload", "qload", 
      new nRunnable() { public void run() { addEventNextFrame(new nRunnable() { 
      public void run() { setup_load(); } } ); } } );
    filesm_run = macro_main.newRun("files_management", "filesm", 
      new nRunnable() { public void run() { filesManagement(); } } );
    full_screen_run = macro_main.newRun("full_screen_run", "fulls", new nRunnable() { public void run() { 
      app.fs_switch(); 
      screen_view.pos.set(app.screen_0_x, app.screen_0_y);
      screen_view.size.set(app.screen_width, app.screen_height);
      screen_gui.updateView();
      cam.up_view_run.run();
      cam_gui.updateView();
      runEvents(screen_gui.eventsFullScreen); 
      runEvents(screen_gui.eventsFullScreen); 
      runEvents(eventsFullS); } } );
    
    
  }
  
  public sRun quicksave_run;
public sRun quickload_run;
public sRun filesm_run;
public sRun full_screen_run;

  public sInterface addToCamDrawerPile(Drawable d) { d.setPile(cam_gui.drawing_pile); return this; }
  public sInterface addToScreenDrawerPile(Drawable d) { d.setPile(screen_gui.drawing_pile); return this; }
  
  public sInterface addEventHoverNotFound(nRunnable r) { eventsHoverNotFound.add(r); return this; }
  public sInterface removeEventHoverNotFound(nRunnable r) { eventsHoverNotFound.remove(r); return this; }
  public sInterface addEventFullS(nRunnable r) { eventsFullS.add(r); return this; }
  public sInterface addEventFrame(nRunnable r) { eventsFrame.add(r); return this; }
  public sInterface removeEventFrame(nRunnable r) { eventsFrame.remove(r); return this; }
  public sInterface addEventNextFrameEnd(nRunnable r) { 
    if (active_nxtfrm_pile) eventsFrameEnd1.add(r); else eventsFrameEnd2.add(r); return this; }
  public sInterface addEventNextFrame(nRunnable r) { 
    if (active_nxtfrm_pile) eventsNextFrame1.add(r); else eventsNextFrame2.add(r); return this; }
  public sInterface addEventNextFrameInverted(nRunnable r) { 
    if (active_nxtfrm_pile) eventsNextFrame2.add(r); else eventsNextFrame1.add(r); return this; }
  public sInterface addEventSetup(nRunnable r) { eventsSetup.add(r); return this; }
  
  public sInterface addEventTwoFrame(nRunnable r) { 
    addEventNextFrame(new nRunnable(r) { public void run() { addEventNextFrame(((nRunnable)builder)); }});
    return this; 
  }
  
  public String getAccess() { return user.access; }
  public boolean canAccess(String a) { 
    if (getAccess().equals("admin") || getAccess().equals(a) || a.equals("all")) return true; 
    else return false; }
  
  public sInterface addEventSetupLoad(nRunnable r) { eventsSetupLoad.add(r); return this; }
  ArrayList<nRunnable> eventsSetupLoad = new ArrayList<nRunnable>();
  public void setup_load() {
		app.logln("setup_load from "+savepath_value.get());
	    file_savebloc.clear();
	    if (setup_bloc != null) setup_bloc.clear();
	    if (file_savebloc.load_from(savepath_value.get())) {
	      
	      setup_bloc = data.newBloc(file_savebloc, "setup");
	      
	      if (setup_bloc.getValue("auto_load") == null || 
	          (setup_bloc.getValue("auto_load") != null && ((sBoo)setup_bloc.getValue("auto_load")).get())) {
	        
	        macro_main.setup_load(setup_bloc);
	        nRunnable.runEvents(eventsSetupLoad);
	        for (nRunnable r : eventsSetupLoad) r.builder = setup_bloc;
	        
	        if (setup_bloc.getValue("show_taskpanel") != null) 
	          show_taskpanel.set(((sBoo)setup_bloc.getValue("show_taskpanel")).get());
	        
	        if (setup_bloc.getValue("log_ext_save") != null) {
	          log_ext_save.set(((sBoo)setup_bloc.getValue("log_ext_save")).get());
	          app.save_log_exit = log_ext_save.get();
	        }
	      }
	      if (setup_bloc.getValue("auto_load") != null) 
	        auto_load.set(((sBoo)setup_bloc.getValue("auto_load")).get());
	    }
	    
	    cam.GRAB = true; cam_gui.hoverpile_passif = false; 
  }
  
  
  String confpth = "conf.txt";
  sValueBloc conf_bloc;
  sStr prev_savepth;
  void conf_init() {
	  if (file_savebloc != null) file_savebloc.clear();
	  file_savebloc = new Save_Bloc("fsb");
      if (conf_bloc != null) conf_bloc.clear();
      if (file_savebloc.load_from(confpth)) {
	    	  	conf_bloc = data.newBloc(file_savebloc, "conf");
	    	  	if (conf_bloc.getValue("prev_savepth") != null) {
	    		  prev_savepth = ((sStr)conf_bloc.getValue("prev_savepth"));
	    		  file_savebloc.clear();
	//    		  if (file_savebloc.load_from(prev_savepth.get())) 
	//    			  ;
	    	  	} 
      } else { 
	    	  conf_bloc = data.newBloc("conf");
			  prev_savepth = new sStr(conf_bloc, "", "prev_savepth", "prev_savepth");
	    	  //fullscreen
	    	  //last file
	    	  //menu colors
      }
  }
  void conf_save() {
	  file_savebloc.clear();
	  conf_bloc.preset_to_save_bloc(file_savebloc); 
      file_savebloc.save_to(confpth);
  }
  
  
  
  
  void addSpecializedSheet(Sheet_Specialize s) {
    macro_main.addSpecializedSheet(s); }
  Macro_Sheet addUniqueSheet(Sheet_Specialize s) {
    return macro_main.addUniqueSheet(s); }


  public sValueBloc getTempBloc() {
    return new sValueBloc(data, "temp"); }


  ArrayList<nRunnable> eventsFullS = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsFrame = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsFrameEnd1 = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsFrameEnd2 = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsNextFrame1 = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsNextFrame2 = new ArrayList<nRunnable>();
  boolean active_nxtfrm_pile = false;
  ArrayList<nRunnable> eventsHoverNotFound = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsSetup = new ArrayList<nRunnable>();
  boolean is_starting = true;
  public boolean show_info = true;
  void frame() {
	  
    input.frame_str(); // track mouse
    framerate.frame(); // calc last frame
    app.background(0);

    if (is_starting) { 
      is_starting = false; 
      app.app_grab.setText(version_title + "  -  "+savepath_value.get());
      nRunnable.runEvents(eventsSetup);
    }
    nRunnable.runEvents(eventsFrame); // << sim runs here
    if (!active_nxtfrm_pile) { nRunnable.runEvents(eventsNextFrame1); eventsNextFrame1.clear(); } 
    else { nRunnable.runEvents(eventsNextFrame2); eventsNextFrame2.clear(); } 
//    active_nxtfrm_pile = !active_nxtfrm_pile;
    
    screen_gui.frame();
    cam.pushCam(); // matrice d'affichage
    cam_gui.frame(); // << macros
    
    // packet process
    active_nxtfrm_pile = !active_nxtfrm_pile;
    if (!active_nxtfrm_pile) { nRunnable.runEvents(eventsFrameEnd1); eventsFrameEnd1.clear(); } 
    else { nRunnable.runEvents(eventsFrameEnd2); eventsFrameEnd2.clear(); } 
    
    cam_gui.draw();
    cam.popCam();
    screen_gui.draw();

    //info:
    if (show_info) {
      app.fill(255); 
//      app.textSize(18); 
      app.textAlign(PConstants.LEFT);
      app.text(framerate.get(), 10, app.window_head + 24 );
      app.text(" C " + RConst.trimFlt(cam.mouse.x) + 
        "," + RConst.trimFlt(cam.mouse.y), 40, app.window_head + 24 );
      app.text("S " + RConst.trimFlt(input.mouse.x) + 
        "," + RConst.trimFlt(input.mouse.y), 250, app.window_head + 24 );
    }
    
    data.frame(); // reset flags
    input.frame_end(); // reset flags
  }
}









