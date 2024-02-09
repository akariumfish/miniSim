package Macro;

import RApplet.RConst;
import UI.nBinPanel;
import UI.nColorPanel;
import UI.nCtrlWidget;
import UI.nLinkedWidget;
import UI.nNumPanel;
import UI.nTextPanel;
import UI.nValuePicker;
import UI.nWatcherWidget;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sObj;
import sData.sRun;
import sData.sStr;
import sData.sValue;
import sData.sValueBloc;
import sData.sVec;

public class MData extends Macro_Bloc {
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
	    
	    ref_field = addEmptyXL(0).addLinkedModel("MC_Element_LField").setLinkedValue(val_cible);
	    
	    search_view = ref_field.getDrawer().addLinkedModel("MC_Element_MiniButton").setLinkedValue(search_folder);
	    
	    in = addInput(0, "in");
	    
	    pop_pan = addEmptyS(0).addCtrlModel("MC_Element_SButton", "panel")
	    .setRunnable(new nRunnable() { public void run() {
	      if (cible != null) {
	        if (cible.type.equals("str")) { 
	          new nTextPanel(mmain().screen_gui, mmain().inter.taskpanel, (sStr)cible);
	        } else if (cible.type.equals("flt")) { 
	          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sFlt)cible);
	        } else if (cible.type.equals("int")) { 
	          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sInt)cible);
	        } else if (cible.type.equals("boo")) { 
	          new nBinPanel(mmain().screen_gui, mmain().inter.taskpanel, (sBoo)cible);
	        } else if (cible.type.equals("col")) { 
	          new nColorPanel(mmain().screen_gui, mmain().inter.taskpanel, (sCol)cible);
	        }
	      }
	    } });
	    
	    val_field = addEmptyL(1).addWatcherModel("MC_Element_Text");
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
