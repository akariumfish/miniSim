package Macro;

import RApplet.RConst;
import UI.*;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;






class MMcomp extends Macro_Bloc { 
  static class MMcomp_Builder extends MAbstract_Builder {
	  MMcomp_Builder() { super("minicomp", "minicomp"); show_in_buildtool = true; }
	  MMcomp build(Macro_Sheet s, sValueBloc b) { MMcomp m = new MMcomp(s, b); return m; }
    }
  sFlt val1, val2;
  Macro_Connexion v1_in, v2_in, out;
  MMcomp(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "minicomp", "minicomp", _bloc); 
    val1 = newFlt("val1", "val1", 0);
    val2 = newFlt("val2", "val2", 0);
    out = addOutput(1, "out");
    v1_in = addInputFloat(0, "v1", new nRunnable() {public void run() { 
    		val1.set(v1_in.lastPack().asFloat()); }});
    v2_in = addInputFloat(0, "v2", new nRunnable() {public void run() { 
    		val2.set(v2_in.lastPack().asFloat()); }});
    addInputBang(1, "try v1 = v2", new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketBool(val1.get() == val2.get())); }});
    addInputBang(1, "try v1 > v2", new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketBool(val1.get() > val2.get())); }});
    addInputBang(0, "try v1 < v2", new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketBool(val1.get() < val2.get())); }});
  }
  public MMcomp clear() {
    super.clear(); 
    return this; }
  public MMcomp toLayerTop() {
    super.toLayerTop(); 
    return this; }
}


class MCount extends Macro_Bloc { 
  static class MCount_Builder extends MAbstract_Builder {
	  MCount_Builder() { super("count", "count"); show_in_buildtool = true; }
      MCount build(Macro_Sheet s, sValueBloc b) { MCount m = new MCount(s, b); return m; }
    }
  sInt count_val;
  Macro_Connexion add_in, sub_in, rst_in, out;
  MCount(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "count", "count", _bloc); 
    count_val = newInt("count_val", "count_val", 0);
    out = addOutput(1, "out");
    add_in = addInputBang(0, "add", new nRunnable() {public void run() { 
    		count_val.add(1); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }});
    sub_in = addInputBang(0, "sub", new nRunnable() {public void run() { 
    		count_val.add(-1); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }});
    rst_in = addInputBang(0, "rst", new nRunnable() {public void run() { 
    		count_val.set(0); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }});
  }
  public MCount clear() {
    super.clear(); 
    return this; }
  public MCount toLayerTop() {
    super.toLayerTop(); 
    return this; }
}









class MCamera extends MBaseMenu { 
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
            if (w_f.get() >   1.375) {
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
