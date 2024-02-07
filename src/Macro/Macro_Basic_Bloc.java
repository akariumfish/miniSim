package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import UI.*;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

//new bloc base :

//class MValue_Builder extends MAbstract_Builder {
//  MValue_Builder() { super("val", ""); show_in_buildtool = true; }
//  MValue build(Macro_Sheet s, sValueBloc b) { MValue m = new MValue(s, b); return m; }
//}
//class MValue extends Macro_Bloc { 
//  MValue(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "val", "val", _bloc); 
//  }
//  MValue clear() {
//    super.clear(); 
//    return this; }
//  MValue toLayerTop() {
//    super.toLayerTop(); 
//    return this; }
//}

//new bloc with double view

//class MBloc_Builder extends MAbstract_Builder {
//  MBloc_Builder() { super("sbloc", ""); show_in_buildtool = true; }
//  MBasic build(Macro_Sheet s, sValueBloc b) { MBasic m = new MBloc(s, b); return m; }
//}
//class MBloc extends MBasic { 
//  MBloc(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "sbloc", _bloc); 
    
//  }
//  void build_param() { ; }
//  MBloc clear() {
//    super.clear(); 
//    return this; }
//  MBloc toLayerTop() {
//    super.toLayerTop(); 
//    return this; }
//}






class MCursor extends MBasic { 
	static class MCursor_Builder extends MAbstract_Builder {
		MCursor_Builder() { super("cursor", "add a cursor"); show_in_buildtool = true; }
	MCursor build(Macro_Sheet s, sValueBloc b) { MCursor m = new MCursor(s, b); return m; }
	}
	public nCursor cursor;
	  public sVec pval = null;
	  public sVec dval = null;
	  public sBoo show = null;
	nRunnable sheet_grab_run, pval_run;
	MCursor(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cursor", _bloc); }
	void init() {
		pval = newVec("pos", "pos");
	    show = newBoo(false, "show", "show"); //!!!!! is hided by default
	    dval = newVec("dir", "dir");
	    cursor = new nCursor(mmain(), value_bloc, true);
	    mmain().cursors_list.add(cursor);
	    mmain().update_cursor_selector_list();
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
	      mmain().update_cursor_selector_list(); }});
	    
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
		}};
		cursor.pval.addEventChange(pval_run);
	    
	    
	    if (sheet != mmain()) {
	    	sheet_grab_run = new nRunnable() { public void run() {
		    	setPosition(cursor.pval.get().x - sheet.grabber.getX(), 
		    			cursor.pval.get().y - sheet.grabber.getY());
		    	moving();
		    } };
	    	sheet.grab_pos.addEventChange(sheet_grab_run);
	    }
	}
	void build_param() {
		
	}
	void build_normal() {
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




class MBasic extends Macro_Bloc { 
	static class MBasic_Builder extends MAbstract_Builder {
		  MBasic_Builder() { super("base", ""); show_in_buildtool = true; }
		  MBasic build(Macro_Sheet s, sValueBloc b) { MBasic m = new MBasic(s, "base", b); return m; }
		}
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
   	build_normal();
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





class MComment extends MBasic { 
  Macro_Connexion in;
  
  Macro_Element elem_param;
  
  sStr val_com;
  sFlt w_f, h_f;
  
  nLinkedWidget com_field;
  nCtrlWidget w_add, w_sub, h_add, h_sub;
  
	MComment(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "com", _bloc); }
	void init() {
		val_com = newStr("val_com", "val_com", "");
	    w_f = newFlt("w_f", "w_f", 3.875F);
	    h_f = newFlt("h_f", "h_f", 0.875F);
	}
	void build_param() {

      in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
	        if (in.lastPack() != null) {
	        	if (in.lastPack().isBang()) gui.app.logln(val_com.get());
	        	else gui.app.logln(val_com.get() + " : " + in.lastPack().getText());
	        }
      } });
      
      elem_param = addEmptyL(1);
      w_sub = elem_param.addCtrlModel("MC_Element_Button_Selector_1", "W-")
        .setRunnable(new nRunnable() { public void run() { 
	        if (w_f.get() > 3.875) {
			  w_f.set(3.875);
			  rebuild();
			  return;
			}
            if (w_f.get() > 	1.375) {
      	      w_f.set(1.375);
      	      rebuild();
      		  return;
	      	    }
        } });
      w_add = elem_param.addCtrlModel("MC_Element_Button_Selector_2", "W+")
        .setRunnable(new nRunnable() { public void run() { 
	        if (w_f.get() < 3.875) {
	            w_f.set(3.875);
	            rebuild();
	            return;
	        } 
	        if (w_f.get() < 5.75) {
		        w_f.set(5.75);
		        rebuild();
		  		return;
	        }
        } });
      h_sub = elem_param.addCtrlModel("MC_Element_Button_Selector_3", "H-")
        .setRunnable(new nRunnable() { public void run() { 
        if (h_f.get() > 1.125) {
          h_f.add(-1.125);
          rebuild();
        }
      } });
      h_add = elem_param.addCtrlModel("MC_Element_Button_Selector_4", "H+")
        .setRunnable(new nRunnable() { public void run() { 
        if (h_f.get() < 5) {
          h_f.add(1.125);
          rebuild();
        }
      } });
      w_sub.setFont((int)(ref_size/2.8)); w_add.setFont((int)(ref_size/2.8));
      h_sub.setFont((int)(ref_size/2.8)); h_add.setFont((int)(ref_size/2.8));
	}
	void build_normal() {
		addEmptyS(1);
	    if (h_f.get() > 1) addEmptyS(1);
	    if (h_f.get() > 2) addEmptyS(1);
	    if (h_f.get() > 4) addEmptyS(1);
	    if (h_f.get() > 5) addEmptyS(1);
		elem_com = addEmptyS(0);
	    com_field = elem_com.addLinkedModel("MC_Element_Field").setLinkedValue(val_com);
	    com_field.setPosition(ref_size*3 / 16, ref_size * 1 / 16)
	      .setSize(ref_size * w_f.get(), ref_size * h_f.get())
	      .setTextAlignment(PConstants.LEFT, PConstants.TOP)
	      .setTextAutoReturn(true)
	      .setFont((int)(ref_size / 1.7));
	}
	public MComment clear() {
	  super.clear(); 
	  return this; }
	public MComment toLayerTop() {
	  super.toLayerTop(); 
	  return this; }
}







class MSheetObj extends Macro_Bloc { 
	static class MSheetObj_Builder extends MAbstract_Builder {
		  MSheetObj_Builder() { super("sheet obj", "send his sheet as packet"); show_in_buildtool = true; }
		  MSheetObj build(Macro_Sheet s, sValueBloc b) { MSheetObj m = new MSheetObj(s, b); return m; }
		}
  nRunnable get_run;
  Macro_Connexion in, out;
  sBoo setup_send;
  MSheetObj(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "sheet obj", "sheet obj", _bloc); 
    setup_send = newBoo("stp_snd", "stp_snd", true);
    get_run = new nRunnable() { public void run() {
      out.send(Macro_Packet.newPacketSheet(sheet));
    }};
    in = addInputBang(0, "get", get_run);
    out = addOutput(1, "out");
    addTrigSwtchS(0, "st", setup_send, "get", get_run);
    if (setup_send.get()) get_run.run();
  }
  public MSheetObj clear() {
    super.clear(); 
    return this; }
  public MSheetObj toLayerTop() {
    super.toLayerTop(); 
    return this; }
}







class MValue extends MBasic { 
	static 
	class MValue_Builder extends MAbstract_Builder {
		  MValue_Builder() { super("svalue", "send selected value as packet"); show_in_buildtool = true; }
		  MValue build(Macro_Sheet s, sValueBloc b) { MValue m = new MValue(s, b); return m; }
		}
  nRunnable get_run;
  Macro_Connexion in, out;
  sStr val_cible; 
  sBoo setup_send;
  sValue cible;
  nCtrlWidget picker, pop_pan;
  nWatcherWidget val_field;
  MValue(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "svalue", _bloc); }
  void init() {
	    val_cible = newStr("cible", "cible", "");
	    setup_send = newBoo("stp_snd", "stp_snd", false);
	    get_cible();
	    get_run = new nRunnable() { public void run() {
	      if (cible != null) out.send(cible.asPacket());
	    }};
	    if (setup_send.get()) get_run.run();
  }
  void build_normal() { 
	    addTrigSwtchS(1, "st", setup_send, "get", get_run);
	    in = addInputBang(0, "get", get_run);
	    //addEmptyS(1);
	    out = addOutput(1, "out");
  }

  void build_param() { 
    Macro_Element e = addEmptyS(0);
    picker = e.addCtrlModel("MC_Element_SButton", "pick").setRunnable(new nRunnable() { public void run() {
      new nValuePicker(mmain().screen_gui, mmain().inter.taskpanel, val_cible, sheet.value_bloc, true)
        .addEventChoose(new nRunnable() { public void run() { get_cible(); } } );
    } });
    pop_pan = picker.getDrawer().addCtrlModel("MC_Element_MiniButton", "p")
    .setRunnable(new nRunnable() { public void run() {
      if (cible != null) {
        if (cible.type.equals("str")) { 
          new nTextPanel(mmain().screen_gui, mmain().inter.taskpanel, (sStr)cible);
        } else if (cible.type.equals("flt")) { 
          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sFlt)cible);
        } else if (cible.type.equals("int")) { 
          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sInt)cible);
        } else if (cible.type.equals("boo")) { 
          new nBoolPanel(mmain().screen_gui, mmain().inter.taskpanel, (sBoo)cible);
        } else if (cible.type.equals("col")) { 
          new nColorPanel(mmain().screen_gui, mmain().inter.taskpanel, (sCol)cible);
        }
      }
    } }); 
    addEmpty(1);
    val_field = addEmptyL(0).addWatcherModel("MC_Element_Text");
    addEmpty(1);
  }
  void get_cible() {
    if (val_field != null) val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
    cible = sheet.value_bloc.getValue(val_cible.get());
    if (val_field != null && cible != null) {
      val_field.setLinkedValue(cible).setLook(gui.theme.getLook("MC_Element_Field"));
    }
  }
  public MValue clear() {
    super.clear(); 
    return this; }
  public MValue toLayerTop() {
    super.toLayerTop(); 
    return this; }
}









class MData extends Macro_Bloc {
  boolean inib_send = false;
  nRunnable val_run, in_run;
  sBoo bval; sInt ival; sFlt fval; sStr sval; sVec vval; sRun rval; sCol cval; sObj oval;
  boolean btmp; int itmp; float ftmp; String stmp = ""; PVector vtmp = new PVector(); int ctmp = 0;
  Macro_Connexion in, out;
  sBoo search_folder;
  sStr val_cible; 
  sValue cible;
  nLinkedWidget ref_field, search_view; 
  nCtrlWidget picker, pop_pan;
  nWatcherWidget val_field;
  MData(Macro_Sheet _sheet, sValueBloc _bloc, sValue v) { 
    super(_sheet, "data", "data", _bloc); 
    val_cible = newStr("cible", "cible", "");
    search_folder = newBoo("search_folder", "search_folder", false);
    addEmptyS(1); addEmpty(2);
    
    Macro_Element e = addEmptyS(1);
    picker = e.addCtrlModel("MC_Element_SButton", "pick").setRunnable(new nRunnable() { public void run() {
      if (!search_folder.get()) 
        new nValuePicker(mmain().screen_gui, mmain().inter.taskpanel, val_cible, sheet.value_bloc, true)
          .addEventChoose(new nRunnable() { public void run() { get_cible(); } } );
      else if (sheet != mmain()) 
        new nValuePicker(mmain().screen_gui, mmain().inter.taskpanel, val_cible, sheet.sheet.value_bloc, true)
          .addEventChoose(new nRunnable() { public void run() { get_cible(); } } );
    } });
    pop_pan = picker.getDrawer().addCtrlModel("MC_Element_MiniButton", "p")
    .setRunnable(new nRunnable() { public void run() {
      if (cible != null) {
        if (cible.type.equals("str")) { 
          new nTextPanel(mmain().screen_gui, mmain().inter.taskpanel, (sStr)cible);
        } else if (cible.type.equals("flt")) { 
          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sFlt)cible);
        } else if (cible.type.equals("int")) { 
          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sInt)cible);
        } else if (cible.type.equals("boo")) { 
          new nBoolPanel(mmain().screen_gui, mmain().inter.taskpanel, (sBoo)cible);
        } else if (cible.type.equals("col")) { 
          new nColorPanel(mmain().screen_gui, mmain().inter.taskpanel, (sCol)cible);
        }
      }
    } });
    
    ref_field = addEmptyXL(0).addLinkedModel("MC_Element_LField").setLinkedValue(val_cible);
    
    search_view = ref_field.getDrawer().addLinkedModel("MC_Element_MiniButton").setLinkedValue(search_folder);
    
    in = addInput(0, "in");
    
    val_field = addEmptyXL(0).addWatcherModel("MC_Element_LText");
    val_cible.addEventChange(new nRunnable(this) { public void run() { get_cible(); } } );
    
    out = addOutput(2, "out");
    
    if (v != null) setValue(v); else get_cible();
  }
  void get_cible() {
    val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
    if (val_run != null && cible != null) cible.removeEventChange(val_run);
    if (val_run != null && cible != null) cible.removeEventAllChange(val_run);
    if (!search_folder.get()) cible = sheet.value_bloc.getValue(val_cible.get());
    else if (sheet != mmain()) cible = sheet.sheet.value_bloc.getValue(val_cible.get());
    if (cible != null) setValue(cible);
  }
  void setValue(sValue v) {
    if (in_run != null) in.removeEventReceive(in_run);
    val_cible.set(v.ref);
    cible = v; val_field.setLinkedValue(cible);
    val_field.setLook(gui.theme.getLook("MC_Element_Field"));
    if (cible.type.equals("flt")) setValue((sFlt)cible);
    if (cible.type.equals("int")) setValue((sInt)cible);
    if (cible.type.equals("boo")) setValue((sBoo)cible);
    if (cible.type.equals("str")) setValue((sStr)cible);
    if (cible.type.equals("run")) setValue((sRun)cible);
    if (cible.type.equals("vec")) setValue((sVec)cible);
    if (cible.type.equals("col")) setValue((sCol)cible);
    if (cible.type.equals("obj")) setValue((sObj)cible);
  }
  void setValue(sObj v) {
    oval = v;
    val_run = new nRunnable() { public void run() { out.send(oval.asPacket()); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isValue()) { 
        oval.set(in.lastPack().asValue()); }
      if (in.lastPack() != null && in.lastPack().isBloc()) { 
        oval.set(in.lastPack().asBloc()); }
      if (in.lastPack() != null && in.lastPack().isSheet()) { 
        oval.set(in.lastPack().asSheet()); }
    } };
    v.addEventAllChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sRun v) {
    rval = v;
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketBang()); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBang()) { 
        rval.doEvent(false); 
        rval.run(); 
        rval.doEvent(true); 
      }
    } };
    v.addEventAllChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sFlt v) {
    fval = v;
    out.send(Macro_Packet.newPacketFloat(v.get()));
    ftmp = v.get();
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(fval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isFloat() && ftmp != in.lastPack().asFloat()) { 
        ftmp = in.lastPack().asFloat();
        fval.set(in.lastPack().asFloat()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
    mmain().inter.addEventNextFrame(val_run); 
  }
  void setValue(sInt v) {
    ival = v;
    out.send(Macro_Packet.newPacketInt(v.get()));
    itmp = v.get();
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketInt(ival.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isInt() && itmp != in.lastPack().asInt()) { 
        itmp = in.lastPack().asInt();
        ival.set(in.lastPack().asInt()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
    mmain().inter.addEventNextFrame(val_run); 
  }
  void setValue(sBoo v) {
    bval = v;
    out.send(Macro_Packet.newPacketBool(v.get()));
    btmp = v.get();
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketBool(bval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool() && !(btmp == in.lastPack().asBool())) { 
        btmp = in.lastPack().asBool();
        bval.set(in.lastPack().asBool()); }
      if (in.lastPack() != null && in.lastPack().isBang()) { 
        btmp = !btmp;
        bval.set(!bval.get()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
    mmain().inter.addEventNextFrame(val_run); 
  }
  void setValue(sStr v) {
    sval = v;
    out.send(Macro_Packet.newPacketStr(v.get()));
    stmp = RConst.copy(v.get());
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketStr(sval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isStr() && !stmp.equals(in.lastPack().asStr())) { 
        stmp = RConst.copy(in.lastPack().asStr());
        sval.set(in.lastPack().asStr()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
    mmain().inter.addEventNextFrame(val_run); 
  }
  void setValue(sVec v) {
    vval = v;
    out.send(Macro_Packet.newPacketVec(v.get()));
    vtmp.set(v.get());
    val_run = new nRunnable() { public void run() { 
      if (!inib_send) mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
      out.send(Macro_Packet.newPacketVec(vval.get())); inib_send = false; }}); 
      inib_send = true; }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isVec() && !in.lastPack().equalsVec(vtmp)) { 
        vtmp.set(in.lastPack().asVec());
        vval.set(in.lastPack().asVec()); 
      }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
    mmain().inter.addEventNextFrame(val_run); 
  }
  void setValue(sCol v) {
    cval = v;
    out.send(Macro_Packet.newPacketCol(v.get()));
    ctmp = v.get();
    val_run = new nRunnable() { public void run() { 
      if (!inib_send) mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
      out.send(Macro_Packet.newPacketCol(cval.get())); inib_send = false; }}); 
      inib_send = true; }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isCol() && !in.lastPack().equalsCol(ctmp)) { 
        ctmp = in.lastPack().asCol();
        cval.set(in.lastPack().asCol()); 
      }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
    mmain().inter.addEventNextFrame(val_run); 
  }
  public MData clear() {
    if (val_run != null && cible != null) cible.removeEventChange(val_run);
    if (val_run != null && cible != null) cible.removeEventAllChange(val_run);
    super.clear(); return this; }
}







class MSheetView extends Macro_Bloc { 
	static class MSheetView_Builder extends MAbstract_Builder {
		  MSheetView_Builder() { super("sheet view", "set the camera on his sheet"); show_in_buildtool = true; }
		  MSheetView build(Macro_Sheet s, sValueBloc b) { MSheetView m = new MSheetView(s, b); return m; }
		}
  nRunnable goto_run;
  Macro_Connexion in;
  MSheetView(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "sheet view", "sheet view", _bloc); 
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
    addTrigS(1, "goto", goto_run);
    in = addInput(0, "goto", new nRunnable() { public void run() { 
      if (in.lastPack().isBang()) goto_run.run(); }});
  }
  public MSheetView clear() {
    super.clear(); 
    return this; }
  public MSheetView toLayerTop() {
    super.toLayerTop(); 
    return this; }
}































class MVar extends Macro_Bloc {
  Macro_Connexion in, out;
  
  Macro_Packet packet;
  nLinkedWidget view, stp_view, aut_view;
  
  sStr val_view; sBoo setup_send, auto_send;
  
  sBoo bval; sFlt fval; sVec vval;
  sStr val_type;
  
  MVar(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "var", "var", _bloc); 
    
    val_view = newStr("val", "val", "");
    setup_send = newBoo("stp_snd", "stp_snd", false);
    auto_send = newBoo("auto_send", "auto_send", false);
    
    bval = newBoo("bval", "bval", false);
    fval = newFlt("fval", "fval", 0);
    vval = newVec("vval", "vval");
    val_type = newStr("val_type", "val_type", "");
    
    Macro_Element e = addEmptyS(1);
    e.addCtrlModel("MC_Element_SButton")
      .setRunnable(new nRunnable() { public void run() { 
        
        if (packet != null) out.send(packet); 
        
      } });
    e.addLinkedModel("MC_Element_MiniButton", "st").setLinkedValue(setup_send);
    
    view = addEmptyS(0).addLinkedModel("MC_Element_SField");
    view.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view.getText();
      if (t.length() > 0) {
        if (t.equals("true")) {
          packet = Macro_Packet.newPacketBool(true);
          bval.set(true);
          val_type.set("boo");
        } else 
        if (t.equals("false")) {
          packet = Macro_Packet.newPacketBool(false);
          bval.set(false);
          val_type.set("boo");
        }
        else {
          packet = Macro_Packet.newPacketFloat(Float.parseFloat(t));
          fval.set(Float.parseFloat(t));
          val_type.set("flt");
        }
        if (auto_send.get()) out.send(packet);
      }
    } });
    view.setLinkedValue(val_view);
    
    view.getDrawer().addLinkedModel("MC_Element_MiniButton", "A")
      .setLinkedValue(auto_send)
      .setInfo("auto send on change");
    
    if (val_type.get().equals("boo")) {
      packet = Macro_Packet.newPacketBool(bval.get()); 
      view.setText(packet.getText()); 
      val_view.set(view.getText());
    }
    else if (val_type.get().equals("vec")) {
      packet = Macro_Packet.newPacketVec(vval.get()); 
      view.setText(packet.getText()); 
      val_view.set(view.getText());
    }
    else if (val_type.get().equals("flt")) {
      packet = Macro_Packet.newPacketFloat(fval.get()); 
      view.setText(packet.getText()); 
      val_view.set(view.getText());
    }
    else {
      fval.set(0);
      val_type.set("flt");
      packet = Macro_Packet.newPacketFloat(fval.get()); 
      view.setText(packet.getText()); 
      val_view.set(view.getText());
    }
    
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null) {
        if (in.lastPack().isBang() && packet != null) out.send(packet);
        else { 
          
          if (in.lastPack().isBool()) {
            bval.set(in.lastPack().asBool());
            val_type.set("boo");
            packet = in.lastPack(); 
            view.setText(packet.getText()); 
            val_view.set(view.getText());
          }
          if (in.lastPack().isFloat()) {
            fval.set(in.lastPack().asFloat());
            val_type.set("flt");
            packet = in.lastPack(); 
            view.setText(packet.getText()); 
            val_view.set(view.getText());
          }
          if (in.lastPack().isVec()) {
            vval.set(in.lastPack().asVec());
            val_type.set("vec");
            packet = in.lastPack(); 
            view.setText(packet.getText()); 
            val_view.set(view.getText());
          }
        } 
      }
    } });
    out = addOutput(1, "out");
    
    if (setup_send.get()) mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
      if (packet != null) out.send(packet);
    } });
  }
  public MVar clear() {
    super.clear(); return this; }
}









class MSheetIn extends Macro_Bloc {
  Macro_Element elem;
  nLinkedWidget view;
  sStr val_view;
  
  MSheetIn(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "in", "in", _bloc); 
    val_view = newStr("val", "val", "");
    view = addEmptyS(1).addLinkedModel("MC_Element_SField");
    view.addEventFieldChange(new nRunnable() { public void run() { 
      elem.sheet_connect.setInfo(val_view.get());
    } });
    view.setLinkedValue(val_view);
    elem = addSheetInput(0, "in");
    if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
    //val_title.addEventChange(new nRunnable() { public void run() { 
    //if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_title.get()); } });
  }
  public MSheetIn clear() {
    super.clear(); return this; }
}

class MSheetOut extends Macro_Bloc {
  Macro_Element elem;
  nLinkedWidget view;
  sStr val_view;
  MSheetOut(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "out", "out", _bloc); 
    val_view = newStr("val", "val", "");
    view = addEmptyS(0).addLinkedModel("MC_Element_SField");
    view.addEventFieldChange(new nRunnable() { public void run() { 
      elem.sheet_connect.setInfo(val_view.get());
    } });
    view.setLinkedValue(val_view);
    elem = addSheetOutput(1, "out");
    if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
    //val_title.addEventChange(new nRunnable() { public void run() { 
    //if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_title.get()); } });
  }
  public MSheetOut clear() {
    super.clear(); return this; }
}






class MCalc extends Macro_Bloc {
  Macro_Connexion in1, in2, out;
  nLinkedWidget widgADD, widgSUB, widgMUL, widgDEV; 
  sBoo valADD, valSUB, valMUL, valDEV;
  float pin1 = 0, pin2 = 0;
  nLinkedWidget view;
  sStr val_view; 
  MCalc(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "calc", "calc", _bloc); 
    
    valADD = newBoo("valADD", "valADD", false);
    valSUB = newBoo("valSUB", "valSUB", false);
    valMUL = newBoo("valMUL", "valMUL", false);
    valDEV = newBoo("valDEV", "valDEV", false);
    
    valADD.addEventChange(new nRunnable() { public void run() { if (valADD.get()) receive(); } });
    valSUB.addEventChange(new nRunnable() { public void run() { if (valSUB.get()) receive(); } });
    valMUL.addEventChange(new nRunnable() { public void run() { if (valMUL.get()) receive(); } });
    valDEV.addEventChange(new nRunnable() { public void run() { if (valDEV.get()) receive(); } });
    
    in1 = addInput(0, "in").setFilterFloat().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
      if (in1.lastPack() != null && in1.lastPack().isFloat() && in1.lastPack().asFloat() != pin1) {
        pin1 = in1.lastPack().asFloat(); receive(); } } });
    in2 = addInput(0, "in").setFilterFloat().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
      if (in2.lastPack() != null && in2.lastPack().isFloat() && in2.lastPack().asFloat() != pin2) {
        pin2 = in2.lastPack().asFloat(); view.setText(RConst.trimStringFloat(pin2)); receive(); } } });
    
    out = addOutput(1, "out")
      .setDefFloat();
      
    val_view = newStr("val", "val", "");
    
    view = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view);
    view.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
        else if (Float.parseFloat(t) != 0) { pin2 = Float.parseFloat(t); in2.setLastFloat(pin2); receive(); }
      }
    } });
    String t = view.getText();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); }
      else if (Float.parseFloat(t) != 0) { pin2 = Float.parseFloat(t); in2.setLastFloat(pin2); }  }
    Macro_Element e = addEmptyL(0);
    widgADD = e.addLinkedModel("MC_Element_Button_Selector_1", "+").setLinkedValue(valADD);
    widgSUB = e.addLinkedModel("MC_Element_Button_Selector_2", "-").setLinkedValue(valSUB);
    widgMUL = e.addLinkedModel("MC_Element_Button_Selector_3", "X").setLinkedValue(valMUL);
    widgDEV = e.addLinkedModel("MC_Element_Button_Selector_4", "/").setLinkedValue(valDEV);
    widgADD.addExclude(widgDEV).addExclude(widgSUB).addExclude(widgMUL);
    widgSUB.addExclude(widgADD).addExclude(widgDEV).addExclude(widgMUL);
    widgMUL.addExclude(widgADD).addExclude(widgSUB).addExclude(widgDEV);
    widgDEV.addExclude(widgADD).addExclude(widgSUB).addExclude(widgMUL);
    
  }
  void receive() { 
    if      (valADD.get()) out.send(Macro_Packet.newPacketFloat(pin1 + pin2));
    else if (valSUB.get()) out.send(Macro_Packet.newPacketFloat(pin1 - pin2));
    else if (valMUL.get()) out.send(Macro_Packet.newPacketFloat(pin1 * pin2));
    else if (valDEV.get() && pin2 != 0) out.send(Macro_Packet.newPacketFloat(pin1 / pin2));
  }
  public MCalc clear() {
    super.clear(); return this; }
}









class MBool extends Macro_Bloc {
  Macro_Connexion in1, in2, out;
  nLinkedWidget widgAND, widgOR; 
  sBoo valAND, valOR;
  boolean pin1 = false, pin2 = false;
  MBool(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "bool", "bool", _bloc); 
    
    valAND = newBoo("valAND", "valAND", false);
    valOR = newBoo("valOR", "valOR", false);
    
    in1 = addInput(0, "in").setFilterBool().addEventReceive(new nRunnable() { public void run() { 
      if (in1.lastPack() != null && in1.lastPack().isBool() && in1.lastPack().asBool() != pin1) {
        pin1 = in1.lastPack().asBool(); receive(); } } });
    in2 = addInput(0, "in").setFilterBool().addEventReceive(new nRunnable() { public void run() { 
      if (in2.lastPack() != null && in2.lastPack().isBool() && in2.lastPack().asBool() != pin2) {
        pin2 = in2.lastPack().asBool(); receive(); } } });
    
    out = addOutput(1, "out")
      .setDefBool();
    
    Macro_Element e = addEmptyS(1);
    widgAND = e.addLinkedModel("MC_Element_Button_Selector_1", "&&").setLinkedValue(valAND);
    widgOR = e.addLinkedModel("MC_Element_Button_Selector_2", "||").setLinkedValue(valOR);
    widgAND.addExclude(widgOR);
    widgOR.addExclude(widgAND);
    
  }
  void receive() { 
    if (valAND.get() && (pin1 && pin2)) 
        out.send(Macro_Packet.newPacketBool(true));
    else if (valOR.get() && (pin1 || pin2)) 
      out.send(Macro_Packet.newPacketBool(true));
    else if (valAND.get() || valOR.get()) 
      out.send(Macro_Packet.newPacketBool(false));
  }
  public MBool clear() {
    super.clear(); return this; }
}










class MBin extends Macro_Bloc {
  Macro_Connexion in, out;
  nLinkedWidget swtch; 
  sBoo state;
  MBin(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "bin", "bin", _bloc); 
    state = newBoo("state", "state", false);
    
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool() && 
          in.lastPack().asBool()) out.send(Macro_Packet.newPacketBang()); 
      else if (in.lastPack() != null && in.lastPack().isBang()) out.send(Macro_Packet.newPacketBool(true));
      else if (state.get() && in.lastPack() != null) out.send(Macro_Packet.newPacketBang());  } });
    out = addOutput(1, "out")
      .setDefBool();
      
    swtch = out.elem.addLinkedModel("MC_Element_SButton").setLinkedValue(state);
    swtch.setInfo("all to bang");
  }
  public MBin clear() {
    super.clear(); return this; }
}

class MNot extends Macro_Bloc {
  Macro_Connexion in, out;
  MNot(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "not", "not", _bloc); 
    
    in = addInput(0, "in").setFilterBool().addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool()) {
        if (in.lastPack().asBool()) out.send(Macro_Packet.newPacketBool(false)); 
        else out.send(Macro_Packet.newPacketBool(true)); } } });
    out = addOutput(1, "out")
      .setDefBool();
  }
  public MNot clear() {
    super.clear(); return this; }
}












class MGate extends Macro_Bloc {
  Macro_Connexion in_m, in_b, out;
  nLinkedWidget swtch; 
  sBoo state;
  MGate(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "gate", "gate", _bloc); 
    
    state = newBoo("state", "state", false);
    
    in_m = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in_m.lastPack() != null && state.get()) out.send(in_m.lastPack());
    } });
    in_b = addInput(0, "gate").addEventReceive(new nRunnable() { public void run() { 
      if (in_b.lastPack() != null && in_b.lastPack().isBool()) 
        state.set(in_b.lastPack().asBool()); 
      if (in_b.lastPack() != null && in_b.lastPack().isBang()) 
        state.set(!state.get()); 
    } });
    out = addOutput(1, "out");
    
    swtch = addEmptyS(1).addLinkedModel("MC_Element_SButton").setLinkedValue(state);
    
  }
  public MGate clear() {
    super.clear(); return this; }
}











class MTrig extends Macro_Bloc {
  Macro_Connexion out_t;
  nCtrlWidget trig; 
  nLinkedWidget stp_view; sBoo setup_send;
  MTrig(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "trig", "trig", _bloc); 
    setup_send = newBoo("stp_snd", "stp_snd", false);
    
    Macro_Element e = addEmptyS(0);
    trig = e.addCtrlModel("MC_Element_SButton").setRunnable(new nRunnable() { public void run() {
      out_t.send(Macro_Packet.newPacketBang());
    } });
    e.addLinkedModel("MC_Element_MiniButton", "st").setLinkedValue(setup_send);
    
    out_t = addOutput(1, "trig")
      .setDefBang();
    if (setup_send.get()) mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
      out_t.send(Macro_Packet.newPacketBang());
    } });
  }
  public MTrig clear() {
    super.clear(); return this; }
}
class MSwitch extends Macro_Bloc {
  Macro_Connexion in, out_t;
  nLinkedWidget swtch; 
  sBoo state;
  MSwitch(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "switch", "switch", _bloc); 
    
    state = newBoo("state", "state", false);
    
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBang()) {
        swtch.setSwitchState(!swtch.isOn());
      } 
      if (in.lastPack() != null && in.lastPack().isBool()) {
        swtch.setSwitchState(in.lastPack().asBool());
      } 
    } });
    
    swtch = addEmptyS(1).addLinkedModel("MC_Element_SButton").setLinkedValue(state);
    
    state.addEventChange(new nRunnable() { public void run() {
      out_t.send(Macro_Packet.newPacketBool(state.get()));
    } });
    
    out_t = addOutput(2, "out")
      .setDefBool();
    
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
      out_t.send(Macro_Packet.newPacketBool(state.get()));
    } });
    
  }
  public MSwitch clear() {
    super.clear(); return this; }
}














class MChan extends Macro_Bloc { 
  Macro_Connexion in, out;
  sStr val_cible; 
  nLinkedWidget ref_field; 
  MChan(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "chan", "chan", _bloc); 
    val_cible = newStr("cible", "cible", "");
    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
    addEmpty(1); 
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null) receive(in.lastPack());
    } });
    out = addOutput(1, "out");
    
    mmain().chan_macros.add(this);
  }
  void receive(Macro_Packet p) {
    out.send(p);
    for (MChan m : mmain().chan_macros) 
      if (m != this && m.val_cible.get().equals(val_cible.get())) m.out.send(p);
  }
  public MChan clear() {
    super.clear(); 
    mmain().chan_macros.remove(this); return this; }
}













class MFrame extends Macro_Bloc { 
  Macro_Connexion in, out;
  Macro_Packet packet1, packet2;
  boolean pack_balance = false;
  MFrame(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "frame", "frame", _bloc); 
    
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null) { 
        if (pack_balance) { 
          pack_balance = false;
          packet1 = in.lastPack();
          mmain().inter.addEventNextFrame(new nRunnable() { public void run() { out.send(packet1); }});
        } else {
          pack_balance = true;
          packet2 = in.lastPack();
          mmain().inter.addEventNextFrame(new nRunnable() { public void run() { out.send(packet2); }});
        }
      } 
    } });
        
    out = addOutput(1, "out");
  }
  public MFrame clear() {
    super.clear(); return this; }
}



class MPulse extends Macro_Bloc { //let throug only 1 bang every <delay> bang
  Macro_Connexion in, out;
  sInt delay;
  nLinkedWidget del_field;
  int count = 0;
  MPulse(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "pulse", "pulse", _bloc); 
    
    delay = newInt("delay", "delay", 100);
    
    addEmptyS(1);
    del_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(delay);
    
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBang()) {
        count++;
        if (count > delay.get()) { count = 0; out.send(Macro_Packet.newPacketBang()); }
      } else if (in.lastPack() != null && in.lastPack().isFloat()) {
        count = 0;
        delay.set((int)(in.lastPack().asFloat()));
      } else if (in.lastPack() != null && in.lastPack().isInt()) {
        count = 0;
        delay.set(in.lastPack().asInt());
      } 
    } });
        
    out = addOutput(1, "out")
      .setDefBool();
  }
  public MPulse clear() {
    super.clear(); return this; }
}









class MComp extends Macro_Bloc {
  Macro_Connexion in1, in2, out;
  nLinkedWidget widgSUP, widgINF, widgEQ; 
  sBoo valSUP, valINF, valEQ;
  float pin1 = 0, pin2 = 0;
  nLinkedWidget view;
  sStr val_view; 
  MComp(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "comp", "comp", _bloc); 
    
    valSUP = newBoo("valSUP", "valSUP", false);
    valINF = newBoo("valINF", "valINF", false);
    valEQ = newBoo("valEQ", "valEQ", false);
    
    valSUP.addEventChange(new nRunnable() { public void run() { if (valSUP.get()) receive(); } });
    valINF.addEventChange(new nRunnable() { public void run() { if (valINF.get()) receive(); } });
    valEQ.addEventChange(new nRunnable() { public void run() { if (valEQ.get()) receive(); } });
    
    in1 = addInput(0, "in").setFilterNumber().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
      if (in1.lastPack() != null && in1.lastPack().isFloat() && 
          in1.lastPack().asFloat() != pin1) {
        pin1 = in1.lastPack().asFloat(); receive(); 
      } else if (in1.lastPack() != null && in1.lastPack().isInt() && 
                 in1.lastPack().asInt() != pin1) {
        pin1 = in1.lastPack().asInt(); receive(); 
      } 
    } });
    in2 = addInput(0, "in").setFilterNumber().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
      if (in2.lastPack() != null && in2.lastPack().isFloat() && 
          in2.lastPack().asFloat() != pin2) {
        pin2 = in2.lastPack().asFloat(); view.setText(RConst.trimStringFloat(pin2)); receive(); 
      } else if (in2.lastPack() != null && in2.lastPack().isInt() && 
                 in2.lastPack().asInt() != pin2) {
        pin2 = in2.lastPack().asInt(); view.setText(RConst.trimStringFloat(pin2)); receive(); 
      } 
    } });
    
    out = addOutput(1, "out")
      .setDefBool();
      
    val_view = newStr("val", "val", "");
    
    view = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view);
    view.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
        else if (Float.parseFloat(t) != 0) { pin2 = Float.parseFloat(t); in2.setLastFloat(pin2); receive(); }
      }
    } });
    String t = view.getText();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
      else if (Float.parseFloat(t) != 0) { pin2 = Float.parseFloat(t); in2.setLastFloat(pin2); receive(); }
    }
    Macro_Element e = addEmptyL(0);
    widgSUP = e.addLinkedModel("MC_Element_Button_Selector_1", ">").setLinkedValue(valSUP);
    widgINF = e.addLinkedModel("MC_Element_Button_Selector_2", "<").setLinkedValue(valINF);
    widgEQ = e.addLinkedModel("MC_Element_Button_Selector_4", "=").setLinkedValue(valEQ);
    widgSUP.addExclude(widgINF);
    widgINF.addExclude(widgSUP);
    
  }
  void receive() { 
    if      (valSUP.get() && (pin1 > pin2)) out.send(Macro_Packet.newPacketBool(true));
    else if (valINF.get() && (pin1 < pin2)) out.send(Macro_Packet.newPacketBool(true));
    else if (valEQ.get() && (pin1 == pin2)) out.send(Macro_Packet.newPacketBool(true));
    else                                    out.send(Macro_Packet.newPacketBool(false));
  }
  public MComp clear() {
    super.clear(); return this; }
}













class MKeyboard extends Macro_Bloc {
  Macro_Connexion out_t;
  nLinkedWidget key_field, ctrl_swt; 
  sStr val_cible; 
  sBoo val_swtch, val_trig, ctrl_filter;
  boolean swt_state = false;
  MKeyboard(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "keyb", "keyb", _bloc); 
    val_cible = newStr("cible", "cible", "");
    init();
  }
  void init() {
    key_field = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_cible);
    
    val_swtch = newBoo("v1", true);
    val_trig = newBoo("v2", false);
    ctrl_filter = newBoo("ctrl_filter", false);
    
    ctrl_swt = key_field.getDrawer().addLinkedModel("MC_Element_MiniButton").setLinkedValue(ctrl_filter);
    ctrl_swt.setInfo("desactive send when macros are hided");
    
    addSelectS(0, val_swtch, val_trig, "Swtch", "Trig");
    
    out_t = addOutput(1, "out")
      .setDefBang();
    key_field.addEventFrame(new nRunnable() { public void run() {
      if ( val_swtch.get() && key_field.getText().length() > 0 && 
           mmain().inter.input.getState(key_field.getText().charAt(0)) && !gui.field_used && 
           !mmain().screen_gui.field_used && 
           (!ctrl_filter.get() || mmain().show_macro.get())) {
        if (!swt_state) out_t.send(Macro_Packet.newPacketBool(true));
        swt_state = true; }
      else if (val_swtch.get() && key_field.getText().length() > 0) {
        if (swt_state) out_t.send(Macro_Packet.newPacketBool(false));
        swt_state = false; }
      if (val_trig.get() && !inib && mmain().inter.input.keyAll.trigClick && 
          key_field.getText().length() > 0 && 
          key_field.getText().charAt(0) == mmain().inter.input.getLastKey() && !gui.field_used && 
          !mmain().screen_gui.field_used  && 
          (!ctrl_filter.get() || mmain().show_macro.get())) {
        out_t.send(Macro_Packet.newPacketBang());
        inib = true;
      }
      if ( val_trig.get() && inib && mmain().inter.input.keyAll.trigUClick && key_field.getText().length() > 0 && 
          key_field.getText().charAt(0) == mmain().inter.input.getLastKey()) {
        inib = false;
      }
    } } );
  }
  boolean inib = false;
  public MKeyboard clear() {
    super.clear(); return this; }
  
}
