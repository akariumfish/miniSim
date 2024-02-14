package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import UI.Drawable;
import UI.nCtrlWidget;
import UI.nDrawer;
import UI.nLinkedWidget;
import UI.nSlide;
import UI.nToolPanel;
import UI.nWatcherWidget;
import UI.nWidget;
import UI.nWindowPanel;
import processing.core.PApplet;
import processing.core.PConstants;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sInt;
import sData.sStr;
import sData.sValue;
import sData.sValueBloc;
import sData.sVec;

public class M_GUI {}




class MSlide extends MBasic {
	  static class MSlide_Builder extends MAbstract_Builder {
		  MSlide_Builder() { super("slide", "slide", "", "GUI"); }
		  MSlide build(Macro_Sheet s, sValueBloc b) { MSlide m = new MSlide(s, b); return m; }
	  }
	  
  MSlide(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "slide", _bloc); }
  public void init() {
	  
  }
  public void build_normal() {
	    addEmpty(2);
	    addEmpty(1);
	    addEmptyXL(0);
	    
//	    slide = (nSlide)(dr.addWidget(new nSlide(front_panel.gui, ref_size * 6, ref_size * 0.75F)));
//	      slide.setPosition(4*ref_size, ref_size * 2 / 16);
//	      
//	      slide.addEventSlide(new nRunnable(this) { public void run(float c) { 
//	        flt = val_min.get() + c * (val_max.get() - val_min.get()); 
//	        val_flt.set(flt);
//	        
//	        val_label.set(val_txt.get() + " " + RConst.trimFlt(flt) ); 
//	        out.send(Macro_Packet.newPacketFloat(flt));
//	      } } );
//	      
//	      slide.setValue((flt - val_min.get()) / (val_max.get() - val_min.get()));
	      
  }
  public void build_param() {

  }
  public MSlide clear() {
    super.clear(); return this; }
}






class MTrig extends MBasic {
	  static class MTrig_Builder extends MAbstract_Builder {
		  MTrig_Builder() { super("trig", "Trigger", "", "GUI"); }
		  MTrig build(Macro_Sheet s, sValueBloc b) { MTrig m = new MTrig(s, b); return m; }
	  }
	  
  Macro_Connexion out_t;
  nCtrlWidget trig; 
  nLinkedWidget stp_view; sBoo setup_send;
  MTrig(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "trig", _bloc); 
  }
  public void init() {
	    setup_send = newBoo("stp_snd", "stp_snd", false);
  }
  public void build_param() {
	    out_t = addOutput(1, "trig")
	      .setDefBang();
	    
	    addEmptyS(0);
	    
	    Macro_Element e = addEmptyL(0);
	    trig = e.addCtrlModel("MC_Element_Button").setRunnable(new nRunnable() { public void run() {
	      out_t.send(Macro_Packet.newPacketBang());
	    } });
	    trig.setSY(ref_size*2).setPY(-ref_size*17/16);
	    e.addLinkedModel("MC_Element_MiniButton", "st").setLinkedValue(setup_send);
	    
	    
	    if (setup_send.get()) mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	      out_t.send(Macro_Packet.newPacketBang());
	    } });
  }
  public void build_normal() {

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






class MSwitch extends MBasic {
	  static class MSwitch_Builder extends MAbstract_Builder {
		  MSwitch_Builder() { super("switch", "Switch", "", "GUI"); }
		  MSwitch build(Macro_Sheet s, sValueBloc b) { MSwitch m = new MSwitch(s, b); return m; }
	  }
	  
  Macro_Connexion in, out_t;
  nLinkedWidget swtch; 
  sBoo state;
  MSwitch(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "switch", _bloc); 
    
  }
  public void init() {
	    
	    state = newBoo("state", "state", false);
	  
  }
  public void build_param() {

	    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isBang()) {
	        swtch.setSwitchState(!swtch.isOn());
	      } 
	      if (in.lastPack() != null && in.lastPack().isBool()) {
	        swtch.setSwitchState(in.lastPack().asBool());
	      } 
	    } });
	    addEmptyS(1);
	    
	    out_t = addOutput(1, "out")
	      .setDefBool();
	      
	    swtch = addEmptyS(0).addLinkedModel("MC_Element_Button").setLinkedValue(state);
	    swtch.setSY(ref_size*2).setPY(-ref_size*17/16);
	    
	    state.addEventChange(new nRunnable() { public void run() {
	      out_t.send(Macro_Packet.newPacketBool(state.get()));
	    } });
	    
	    mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	      out_t.send(Macro_Packet.newPacketBool(state.get()));
	    } });
  }
  public void build_normal() {
	    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isBang()) {
	    	  	state.swtch();
	    	    out_t.send(Macro_Packet.newPacketBool(state.get()));
	      } 
	      if (in.lastPack() != null && in.lastPack().isBool()) {
	    	  	if(state.get() != in.lastPack().asBool()) {
	    	  		state.swtch();
		    	    out_t.send(Macro_Packet.newPacketBool(state.get()));
	    	  	}
	      } 
	    } });
	    nRunnable swt_run = new nRunnable() { public void run() {
		    	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		    		out_t.send(Macro_Packet.newPacketBool(state.get()));
		    	}});
		}};

	    swtch = addEmptyS(1).addLinkedModel("MC_Element_SButton").setLinkedValue(state);
	    swtch.addEventSwitchOn(swt_run).addEventSwitchOff(swt_run);
	    
	    
	    out_t = addOutput(2, "out")
	      .setDefBool();
	    
	    mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	      out_t.send(Macro_Packet.newPacketBool(state.get()));
	    } });
	    
  }
  public MSwitch clear() {
    super.clear(); return this; }
}
















class MComment extends MBasic { 
	  static class MComment_Builder extends MAbstract_Builder {
		  MComment_Builder() { super("com", "Comment", "", "GUI"); }
		  MComment build(Macro_Sheet s, sValueBloc b) { MComment m = new MComment(s, b); return m; }
	  }
	  
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
      com_field = elem_com.addLinkedModel("MC_Element_Comment_Field").setLinkedValue(val_com);
      com_field.setSize(ref_size * w_f.get(), ref_size * h_f.get());
  }
  public MComment clear() {
    super.clear(); 
    return this; }
  public MComment toLayerTop() {
    super.toLayerTop(); 
    return this; }
}






















class MToolNCtrl extends MToolRow {  
  nDrawer dr;
  
  void build_front_panel(nToolPanel front_panel) {
    if (front_panel != null) {
      
      dr = front_panel.getShelf()
        .addSeparator(0.125)
        .addDrawer(10.25, 1);
      
      if (cible != null) {
        dr.addCtrlModel("Button-S1-P2", "<<").setLinkedValue(cible).setFactor(0.5F).getDrawer()
          .addCtrlModel("Button-S1-P3", "<").setLinkedValue(cible).setFactor(0.8).getDrawer()
          .addWatcherModel("Label_Back-S2-P2", "--").setLinkedValue(cible).getDrawer()
          .addCtrlModel("Button-S1-P7", ">").setLinkedValue(cible).setFactor(1.25).getDrawer()
          .addCtrlModel("Button-S1-P8", ">>").setLinkedValue(cible).setFactor(2).getDrawer();
      }
    }
  }
  void setValue(sValue v) {
    if (v.type.equals("flt") || v.type.equals("int")) {
      if (val_run != null && cible != null) cible.removeEventChange(val_run);
      val_cible.set(v.ref);
      cible = v; val_field.setLinkedValue(cible);
      if (cible.type.equals("flt")) setValue((sFlt)cible);
      if (cible.type.equals("int")) setValue((sInt)cible);
    }
  }
  void setValue(sFlt v) {
    fval = v;
    out.send(Macro_Packet.newPacketFloat(v.get()));
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(fval.get())); }};
    v.addEventChange(val_run);
    if (mtool != null && mtool.front_panel != null) mtool.rebuild();
  }
  void setValue(sInt v) {
    ival = v;
    out.send(Macro_Packet.newPacketFloat(v.get()));
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(ival.get())); }};
    v.addEventChange(val_run);
    if (mtool != null && mtool.front_panel != null) mtool.rebuild();
  }
  nRunnable val_run, in1_run, in2_run;
  sInt ival; sFlt fval;
  Macro_Connexion in_m, out;
  sStr val_cible; 
  sValue cible;
  nLinkedWidget ref_field, val_field;
  nLinkedWidget widgFAC, widgINC; 
  sBoo valFAC, valINC;
  float mod = 0;
  nLinkedWidget mod_view;
  sStr val_mod; 
  
  MToolNCtrl(Macro_Sheet _sheet, sValueBloc _bloc, sValue v) { 
    super(_sheet, "toolNC", "toolNC", _bloc); 
    
    val_cible = newStr("cible", "cible", "");
    
    val_mod = newStr("mod", "mod", "2");
    String t = val_mod.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { mod = 0; }
      else if (PApplet.parseFloat(t) != 0) { mod = PApplet.parseFloat(t); }
    }
    
    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
    val_field = addEmptyL(0).addLinkedModel("MC_Element_Field");
    val_cible.addEventChange(new nRunnable(this) { public void run() { 
      cible = sheet.value_bloc.getValue(val_cible.get());
      if (cible != null) setValue(cible); } } );
    
    addEmpty(1); addEmpty(1);
    
    in_m = addInput(0, "modifier").setFilterFloat().setLastFloat(mod).addEventReceive(new nRunnable() { public void run() { 
      if (in_m.lastPack() != null && in_m.lastPack().isFloat() && 
          in_m.lastPack().asFloat() != mod) {
        mod = in_m.lastPack().asFloat(); mod_view.setText(RConst.trimFlt(mod)); } } });
    
    
    out = addOutput(1, "out");
    
    mod_view = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod);
    mod_view.addEventFieldChange(new nRunnable() { public void run() { 
      String t = mod_view.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { mod = 0; }
        else if (PApplet.parseFloat(t) != 0) { mod = PApplet.parseFloat(t); }
      }
    } });
    
    valFAC = newBoo("valFAC", "valFAC", false);
    valINC = newBoo("valINC", "valINC", false);
    
    Macro_Element e = addEmptyS(1);
    widgFAC = e.addLinkedModel("MC_Element_Button_Selector_1", "x /").setLinkedValue(valFAC);
    widgINC = e.addLinkedModel("MC_Element_Button_Selector_2", "+ -").setLinkedValue(valINC);
    widgFAC.addExclude(widgINC);
    widgINC.addExclude(widgFAC);
    
    if (v != null) setValue(v);
    else {
      cible = sheet.value_bloc.getValue(val_cible.get());
      if (cible != null) setValue(cible);
    }
  }
  public MToolNCtrl clear() {
    if (val_run != null && cible != null) cible.removeEventChange(val_run);
    super.clear(); return this; }
}

class MToolBin extends MToolRow {  
  nDrawer dr;
  nWidget trig1, trig2, trig3; 
  nWatcherWidget pan_label;
  
  nRunnable trig1_run, trig2_run, trig3_run;
  
  void build_front_panel(nToolPanel front_panel) {
    if (front_panel != null) {
      dr = front_panel.getShelf()
        .addSeparator(0.125)
        .addDrawer(10.25, 1);
      pan_label = dr.addWatcherModel("Label-S3");
      pan_label.setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
        .addSeparator()
        ;
      
      trig2 = dr.addModel("Button-S2-P2").setSwitch().setSwitchState(b2)
        .addEventSwitchOn(trig2_run)
        .addEventSwitchOff(trig2_run);
      if (val_txt2 != null && trig2 != null) trig2.setText(val_txt2.get());
      
      param();
    }
  }
  
  nLinkedWidget widgWTRIG1, widgWTRIG2; 
  sBoo          valTRIG1,   valTRIG2;
  Macro_Connexion in1, in2, in3, out1, out2, out3;
  boolean b1, b2, b3;
  
  sStr val_lbl1, val_txt1, val_txt2, val_txt3; 
  String msg = "";
  nLinkedWidget txt1_field, txt2_field, txt3_field; 
  
  MToolBin(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "toolbin", "toolbin", _bloc); 
    
    valTRIG1 = newBoo("valTRIG1", "valTRIG1", false);
    valTRIG2 = newBoo("valTRIG2", "valTRIG2", false);
    
    val_lbl1 = newStr("lbl1", "lbl1", "");
    val_txt1 = newStr("txt1", "txt1", "");
    val_txt2 = newStr("txt2", "txt2", "");
    val_txt3 = newStr("txt3", "txt3", "");
    
    valTRIG1.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    valTRIG2.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    
    txt1_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_txt1);
    txt1_field.setInfo("label / trig 1");
    txt2_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_txt2);
    txt2_field.setInfo("trig 2");
    txt3_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_txt3);
    txt3_field.setInfo("trig 3");
    val_txt1.addEventChange(new nRunnable(this) { public void run() { 
      val_lbl1.set(val_txt1.get() + " " + msg); 
      if (trig1 != null) trig1.setText(val_txt1.get()); } });
    val_txt2.addEventChange(new nRunnable(this) { public void run() { 
      if (trig2 != null) trig2.setText(val_txt2.get()); } });
    val_txt3.addEventChange(new nRunnable(this) { public void run() { 
      if (trig3 != null) trig3.setText(val_txt3.get()); } });
    
    
    in1 = addInput(0, "in1/val").addEventReceive(new nRunnable(this) { public void run() { 
      if (in1.lastPack() != null && !in1.lastPack().isBool()) { 
        msg = in1.lastPack().getText();
        val_lbl1.set(val_txt1.get() + " " + msg); 
        if (trig1 != null) trig1.setText(val_txt1.get()); } 
      if (in1.lastPack() != null && in1.lastPack().isBool() && trig1 != null) { 
        trig1.setSwitchState(in1.lastPack().asBool()); b1 = in1.lastPack().asBool(); }
    } });
    in2 = addInput(0, "in2").addEventReceive(new nRunnable(this) { public void run() { 
      if (in2.lastPack() != null && in2.lastPack().isBool() && trig2 != null) { 
        trig2.setSwitchState(in2.lastPack().asBool()); b2 = in2.lastPack().asBool(); }
    } });
    in3 = addInput(0, "in3").addEventReceive(new nRunnable(this) { public void run() { 
      if (in3.lastPack() != null && in3.lastPack().isBool() && trig3 != null) { 
        trig3.setSwitchState(in3.lastPack().asBool()); b3 = in3.lastPack().asBool(); }
    } });
    
    Macro_Element e2 = addEmptyS(2);
    widgWTRIG1 = e2.addLinkedModel("MC_Element_Button_Selector_1", "").setLinkedValue(valTRIG1);
    widgWTRIG2 = e2.addLinkedModel("MC_Element_Button_Selector_2", "").setLinkedValue(valTRIG2);
    
    out1 = addOutput(2, "out 1").setDefBang();
    out2 = addOutput(2, "out 2").setDefBang();
    out3 = addOutput(2, "out 3").setDefBang();
    
    trig1_run = new nRunnable(this) { public void run() { 
      if (trig1 != null) out1.send(Macro_Packet.newPacketBool(trig1.isOn())); } };
    
    trig2_run = new nRunnable(this) { public void run() { 
      if (trig2 != null) out2.send(Macro_Packet.newPacketBool(trig2.isOn())); } };
    
    trig3_run = new nRunnable(this) { public void run() { 
      if (trig3 != null) out3.send(Macro_Packet.newPacketBool(trig3.isOn())); } };
    
    param();
    
  }
  void param() {
    if (pan_label != null) pan_label.setLinkedValue(val_lbl1);
    
     if (valTRIG1.get()) {
      if (trig1 != null) trig1.show();
    } 
    
    if (valTRIG2.get()) {
      if (trig2 != null) trig2.show();
      if (trig1 != null) trig1.clear();
      if (trig3 != null) trig3.clear();
      if (dr != null) trig1 = dr.addModel("Button-S2-P1").setSwitch().setSwitchState(b1)
        .addEventSwitchOn(trig1_run)
        .addEventSwitchOff(trig1_run);
      if (val_txt1 != null && trig1 != null) trig1.setText(val_lbl1.get());
      if (dr != null) trig3 = dr.addModel("Button-S2-P3").setSwitch().setSwitchState(b3)
        .addEventSwitchOn(trig3_run)
        .addEventSwitchOff(trig3_run);
      if (val_txt3 != null && trig3 != null) trig3.setText(val_txt3.get());
    } else { 
      if (trig2 != null) trig2.hide();
      if (valTRIG1.get()) {
        if (trig1 != null) trig1.clear();
        if (trig3 != null) trig3.clear();
        if (dr != null) trig1 = dr.addModel("Button-S3-P1").setSwitch()
          .addEventSwitchOn(trig1_run)
          .addEventSwitchOff(trig1_run);
        if (val_txt1 != null && trig1 != null) trig1.setText(val_lbl1.get());
        if (dr != null) trig3 = dr.addModel("Button-S3-P2").setSwitch()
          .addEventSwitchOn(trig3_run)
          .addEventSwitchOff(trig3_run);
        if (val_txt3 != null && trig3 != null) trig3.setText(val_txt3.get());
      } else {
      if (trig1 != null) trig1.clear();
        if (trig3 != null) trig3.clear();
        if (dr != null) trig1 = dr.addModel("Button-S2-P1").setSwitch()
          .addEventSwitchOn(trig1_run)
          .addEventSwitchOff(trig1_run);
        if (val_txt1 != null && trig1 != null) trig1.setText(val_lbl1.get());
        if (dr != null) trig3 = dr.addModel("Button-S2-P3").setSwitch()
          .addEventSwitchOn(trig3_run)
          .addEventSwitchOff(trig3_run);
        if (val_txt3 != null && trig3 != null) trig3.setText(val_txt3.get());
      }
    }
    
    if (valTRIG1.get()) {
      if (trig1 != null) trig1.show();
    } else if (trig1 != null) trig1.hide();
    
    if (trig3 != null) trig3.show();
  }
  public MToolBin clear() {
    super.clear(); return this; }
}
class MToolTri extends MToolRow {  
  nDrawer dr;
  nWidget trig1, trig2, trig3; 
  nWatcherWidget pan_label;
  
  nRunnable trig1_run, trig2_run, trig3_run;
  
  void build_front_panel(nToolPanel front_panel) {
    if (front_panel != null) {
      
      dr = front_panel.getShelf()
        .addSeparator(0.125)
        .addDrawer(10.25, 1);
      pan_label = dr.addWatcherModel("Label-S3");
      pan_label.setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
        .addSeparator()
        ;
      trig1 = dr.addModel("Button-S2-P1").setTrigger()
        .addEventTrigger(trig1_run);
      trig2 = dr.addModel("Button-S2-P2").setTrigger()
        .addEventTrigger(trig2_run);
      trig3 = dr.addModel("Button-S2-P3").setTrigger()
        .addEventTrigger(trig3_run);
      
      if (val_txt1 != null) trig1.setText(val_txt1.get());
      if (val_txt2 != null) trig2.setText(val_txt2.get());
      if (val_txt3 != null) trig3.setText(val_txt3.get());
      
      param();
      
      //front_panel.addEventClose(new nRunnable(this) { public void run() { 
      //  pan_label = null; pan_button = null; } } );
    }
  }
  
  nLinkedWidget widgWTRIG1, widgWTRIG2; 
  sBoo          valTRIG1,   valTRIG2;
  Macro_Connexion in, out1, out2, out3;
  
  sStr val_lbl1, val_txt1, val_txt2, val_txt3; 
  String msg = "";
  nLinkedWidget txt1_field, txt2_field, txt3_field; 
  
  MToolTri(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "tooltri", "tooltri", _bloc); 
    
    valTRIG1 = newBoo("valTRIG1", "valTRIG1", false);
    valTRIG2 = newBoo("valTRIG2", "valTRIG2", false);
    
    val_lbl1 = newStr("lbl1", "lbl1", "");
    val_txt1 = newStr("txt1", "txt1", "");
    val_txt2 = newStr("txt2", "txt2", "");
    val_txt3 = newStr("txt3", "txt3", "");
    
    valTRIG1.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    valTRIG2.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    
    trig1_run = new nRunnable(this) { public void run() { out1.send(Macro_Packet.newPacketBang()); } };
    trig2_run = new nRunnable(this) { public void run() { out2.send(Macro_Packet.newPacketBang()); } };
    trig3_run = new nRunnable(this) { public void run() { out3.send(Macro_Packet.newPacketBang()); } };
    
    addEmptyS(1);
    txt1_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt1);
    txt1_field.setInfo("label / trig 1");
    txt2_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_txt2);
    txt2_field.setInfo("trig 2");
    txt3_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_txt3);
    txt3_field.setInfo("trig 3");
    val_txt1.addEventChange(new nRunnable(this) { public void run() { 
      val_lbl1.set(val_txt1.get() + " " + msg); 
      if (trig1 != null) trig1.setText(val_txt1.get()); } });
    val_txt2.addEventChange(new nRunnable(this) { public void run() { 
      if (trig2 != null) trig2.setText(val_txt2.get()); } });
    val_txt3.addEventChange(new nRunnable(this) { public void run() { 
      if (trig3 != null) trig3.setText(val_txt3.get()); } });
    
    
    in = addInput(0, "val").addEventReceive(new nRunnable(this) { public void run() { 
      if (in.lastPack() != null) { 
        msg = in.lastPack().getText();
        val_lbl1.set(val_txt1.get() + " " + msg); 
        if (trig1 != null) trig1.setText(val_txt1.get()); } 
    } });
    
    Macro_Element e2 = addEmptyS(0);
    widgWTRIG1 = e2.addLinkedModel("MC_Element_Button_Selector_1", "").setLinkedValue(valTRIG1);
    widgWTRIG2 = e2.addLinkedModel("MC_Element_Button_Selector_2", "").setLinkedValue(valTRIG2);
    
    addEmptyS(2);
    out1 = addOutput(2, "out 1").setDefBang();
    out2 = addOutput(2, "out 2").setDefBang();
    out3 = addOutput(2, "out 3").setDefBang();
    
    param();
    
  }
  void param() {
    if (pan_label != null) pan_label.setLinkedValue(val_lbl1);
    
     if (valTRIG1.get()) {
      if (trig1 != null) trig1.show();
    } 
    
    if (valTRIG2.get()) {
      if (trig2 != null) trig2.show();
      if (trig1 != null) trig1.clear();
      if (trig3 != null) trig3.clear();
      if (dr != null) trig1 = dr.addModel("Button-S2-P1").setTrigger()
        .addEventTrigger(trig1_run);
      if (val_txt1 != null && trig1 != null) trig1.setText(val_lbl1.get());
      if (dr != null) trig3 = dr.addModel("Button-S2-P3").setTrigger()
        .addEventTrigger(trig3_run);
      if (val_txt3 != null && trig3 != null) trig3.setText(val_txt3.get());
    } else { 
      if (trig2 != null) trig2.hide();
      if (valTRIG1.get()) {
        if (trig1 != null) trig1.clear();
        if (trig3 != null) trig3.clear();
        if (dr != null) trig1 = dr.addModel("Button-S3-P1").setTrigger()
          .addEventTrigger(trig1_run);
        if (val_txt1 != null && trig1 != null) trig1.setText(val_lbl1.get());
        if (dr != null) trig3 = dr.addModel("Button-S3-P2").setTrigger()
          .addEventTrigger(trig3_run);
        if (val_txt3 != null && trig3 != null) trig3.setText(val_txt3.get());
      } else {
      if (trig1 != null) trig1.clear();
        if (trig3 != null) trig3.clear();
        if (dr != null) trig1 = dr.addModel("Button-S2-P1").setTrigger()
          .addEventTrigger(trig1_run);
        if (val_txt1 != null && trig1 != null) trig1.setText(val_lbl1.get());
        if (dr != null) trig3 = dr.addModel("Button-S2-P3").setTrigger()
          .addEventTrigger(trig3_run);
        if (val_txt3 != null && trig3 != null) trig3.setText(val_txt3.get());
      }
    }
    
    if (valTRIG1.get()) {
      if (trig1 != null) trig1.show();
    } else if (trig1 != null) trig1.hide();
    
    if (trig3 != null) trig3.show();
  }
  public MToolTri clear() {
    super.clear(); return this; }
}
abstract class MToolRow extends Macro_Bloc {  
  abstract void build_front_panel(nToolPanel front_panel);
  
  MTool mtool;
  
  sStr val_pan_title; 
  nLinkedWidget title_field; 
  
  MToolRow(Macro_Sheet _sheet, String r, String s, sValueBloc _bloc) { 
    super(_sheet, r, s, _bloc); 
    
    addEmptyS(1);
    val_pan_title = newStr("pan_title", "pan_title", "");
    val_pan_title.addEventChange(new nRunnable(this) { public void run() { 
      search_panel();
    } });
    title_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_pan_title);
    title_field.setInfo("tool cible title");
    
    mmain().inter.addEventNextFrame(new nRunnable(this) { public void run() { 
      if (mmain().last_added_tool != null && val_pan_title.get().length() == 0) 
    	  val_pan_title.set(mmain().last_added_tool.val_pan_title.get());
      search_panel();
    } });
  }
  void search_panel() {
    if (mtool != null) mtool.tool_macros.remove(this);
    if (mtool != null) mtool.rebuild();
    if (val_pan_title.get().length() > 0) {
      for (MTool m : mmain().tool_macros) 
        if (m.val_pan_title.get().equals(val_pan_title.get())) {
          mtool = m;
          mtool.tool_macros.add(this);
          mtool.rebuild();
          break;
        }
    }
  }
  public MToolRow clear() {
    if (mtool != null) mtool.tool_macros.remove(this);
    if (mtool != null && mtool.front_panel != null) mtool.rebuild();
    super.clear(); return this; }
}

class MTool extends Macro_Bloc {  
  nToolPanel front_panel = null;  
  
  nLinkedWidget stp_view, title_field; 
  nRunnable reduc_run;
  sBoo setup_send, menu_reduc, menu_top; 
  sInt menu_pos;
  sStr val_pan_title; 
  Macro_Connexion in;
  
  ArrayList<MToolRow> tool_macros = new ArrayList<MToolRow>();
  
  MTool(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "tool", "tool", _bloc); 
    setup_send = newBoo("stp_send", "stp_send", false);
    menu_reduc = newBoo("menu_reduc", "menu_reduc", false);
    menu_top = newBoo("menu_top", "menu_top", true);
    menu_pos = newInt("y_pos", "y_pos", 1);
    
    reduc_run = new nRunnable() { public void run() {
      if (front_panel != null) { menu_reduc.set(front_panel.hide); 
      if (!front_panel.hide) rebuild(); } } };
    
    addEmptyS(1);
    Macro_Element e = addEmptyL(0);
    e.addCtrlModel("MC_Element_Button", "tool").setRunnable(new nRunnable() { public void run() { 
      open_menu(); 
      setup_send.set(true); } });
    e.addLinkedModel("MC_Element_MiniButton", "st").setLinkedValue(setup_send);
    
    addEmptyS(1);
    val_pan_title = newStr("pan_title", "pan_title", "tool_"+mmain().tool_nb);
    title_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_pan_title);
    title_field.setInfo("tool title");
    val_pan_title.addEventChange(new nRunnable(this) { public void run() { 
      if (front_panel != null) front_panel.clear();
      for (MToolRow m : tool_macros) m.val_pan_title.set(val_pan_title.get());
      rebuild();
    } });
    
    mmain().tool_macros.add(this);
    mmain().tool_nb++;
    mmain().last_added_tool = this;
    
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() { if (setup_send.get()) open_menu(); } });
    
    addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(menu_pos).getDrawer()
      .addLinkedModel("MC_Element_MiniButton", "t").setLinkedValue(menu_top);
    menu_pos.addEventChange(new nRunnable(this) { public void run() { 
      rebuild();
    } });
    menu_top.addEventChange(new nRunnable(this) { public void run() { 
      rebuild();
    } });
    addEmptyS(1).addCtrlModel("MC_Element_SButton", "close").setRunnable(new nRunnable() { public void run() { 
      if (front_panel != null) front_panel.clear(); 
      front_panel = null;
      setup_send.set(false); } });
    
    in = addInput(0, "open").setFilterBang().addEventReceive(new nRunnable() { public void run() { 
      open_menu(); setup_send.set(true);
    } });
    
  }
  void rebuild() {
    boolean st = setup_send.get();
    boolean op = front_panel != null;
    if (op) { front_panel.clear(); front_panel = null; open_menu(); }
    setup_send.set(st);
  }
  void open_menu() {
    if (front_panel == null) {
      front_panel = new nToolPanel(mmain().screen_gui, mmain().ref_size, 0.125F, false, menu_top.get());
      
      front_panel.addShelf().addDrawer(4, 0);
      
      int prio = 0;
      for (MToolRow m : tool_macros) prio = Math.max(prio, m.priority.get());
      int co_done = 0;
      while (prio >= 0 && co_done <= tool_macros.size()) {
        for (MToolRow m : tool_macros) {
          if (prio == m.priority.get()) { 
            co_done++; 
            m.build_front_panel(front_panel);
          }
        }
        prio--;
      }
      
      front_panel.setPos(ref_size*menu_pos.get());
      
      //if (menu_top.get()) front_panel.setPos(ref_size*menu_pos.get());
      //else front_panel.setPos(front_panel.gui.view.pos.y + front_panel.gui.view.size.y - (front_panel.panel.getLocalSY() + ref_size*menu_pos.get()) );
      
      if (menu_reduc.get()) front_panel.closeit();
      else front_panel.openit();
      
      front_panel.addEventReduc(reduc_run); 
      
    } else front_panel.openit();
  }
  public MTool clear() {
    if (front_panel != null) front_panel.clear();
    mmain().tool_macros.remove(this);
    super.clear(); return this; }
}



class MPanGrph extends MPanTool { 
  
  nWatcherWidget pan_label;
  nWidget graph;
  Drawable g_draw;
  void build_front_panel(nWindowPanel front_panel) {
    if (front_panel != null) {
      
      nDrawer dr = front_panel.getShelf()
        //.addSeparator(0.125)
        .addDrawer(10.25, 3.625);
      
      graph = dr.addModel("Field");
      graph.setPosition(ref_size * 2 / 16, ref_size * 2 / 16)
        .setSize(ref_size * 10, ref_size * 3.5);
        
      larg = (int)(graph.getLocalSX());
      graph_data = new float[larg];
      for (int i = 0; i < larg; i++) { 
        graph_data[i] = 0; 
      }
      gc = 0;
      max = 10;
      g_draw = new Drawable(front_panel.gui.drawing_pile, 0) { public void drawing() {
        gui.app.fill(graph.look.standbyColor);
        gui.app.noStroke();
        gui.app.rect(graph.getX(), graph.getY(), graph.getSX(), graph.getSY());
        gui.app.strokeWeight(ref_size / 40);
        gui.app.stroke(255);
        for (int i = 1; i < larg; i++) if (i != gc) {
          //stroke(255);
        	gui.app.line( graph.getX() + (i-1), 
                graph.getY() + graph.getSY() - ref_size / 4 - (graph_data[(i-1)] * (graph.getSY()-ref_size*3/4) / max), 
                graph.getX() + i, 
                graph.getY() + graph.getSY() - ref_size / 4 - (graph_data[i] * (graph.getSY()-ref_size*3/4) / max) );
        }
        gui.app.stroke(255, 0, 0);
        gui.app.strokeWeight(ref_size / 6);
        if (gc != 0) {
        	gui.app.point(graph.getX() + gc-1, graph.getY() + graph.getSY() - ref_size / 4 - (graph_data[gc-1] * (graph.getSY()-ref_size*3/4) / max) );
        }
      } };
      graph.setDrawable(g_draw);
      
      pan_label = dr.addWatcherModel("Label-S3").setLinkedValue(val_label);
      pan_label.setTextAlignment(PConstants.LEFT, PConstants.CENTER).setSY(ref_size*0.6).setFont((int)(ref_size/2.0)).getShelf()
        .addSeparator()
        ;
      front_panel.addEventClose(new nRunnable(this) { public void run() { 
        pan_label = null; graph = null; g_draw.clear(); g_draw = null; } } );
      front_panel.toLayerTop();
    }
  }
  Macro_Connexion in_val, in_tick;
  
  sStr val_txt, val_label;
  nLinkedWidget txt_field; float flt;
  
  int larg = 0, gc = 0;
  float[] graph_data = null;
  float max = 0.0001F;
  
  MPanGrph(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "pangrph", "pangrph", _bloc); 
    
    addEmptyS(1);
    val_txt = newStr("txt", "txt", "");
    val_label = newStr("lbl", "lbl", "");
    val_txt.addEventChange(new nRunnable(this) { public void run() { 
      val_label.set(val_txt.get() + " " + RConst.trimFlt(flt)); } });
    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
    txt_field.setInfo("description");
    
    in_val = addInput(0, "val").addEventReceive(new nRunnable(this) { public void run() { 
      if (in_val.lastPack() != null && in_val.lastPack().isFloat()) {
        flt = in_val.lastPack().asFloat();
        val_label.set(val_txt.get() + " " + RConst.trimFlt(flt) ); 
      }
      if (in_val.lastPack() != null && in_val.lastPack().isInt()) {
        flt = in_val.lastPack().asInt();
        val_label.set(val_txt.get() + " " + RConst.trimFlt(flt) ); 
      }
    } });
    in_tick = addInput(0, "tick").addEventReceive(new nRunnable(this) { public void run() { 
      if (in_tick.lastPack() != null && in_tick.lastPack().isBang() && 
          mpanel != null && mpanel.front_panel != null && graph_data != null) {
        //enregistrement des donner dans les array
        float g = flt;
        if (max < g) max = g;
        if (graph_data[gc] == max) {
          max = 10;
          for (int i = 0; i < graph_data.length; i++) if (i != gc && max < graph_data[i]) max = graph_data[i];
        }
        graph_data[gc] = g;
      
        if (gc < larg-1) gc++; 
        else gc = 0;
      }
    } });
  }
  public MPanGrph clear() {
    super.clear(); if (g_draw != null) g_draw.clear(); return this; }
}


class MPanSld extends MPanTool { 
  
  nWatcherWidget pan_label;
  nSlide slide;
  void build_front_panel(nWindowPanel front_panel) {
    if (front_panel != null) {
      
      nDrawer dr = front_panel.getShelf()
        .addSeparator(0.125)
        .addDrawer(10.25, 1);
      pan_label = dr.addWatcherModel("Label-S3").setLinkedValue(val_label);
      pan_label.setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
        .addSeparator()
        ;
      
      slide = (nSlide)(dr.addWidget(new nSlide(front_panel.gui, ref_size * 6, ref_size * 0.75F)));
      slide.setPosition(4*ref_size, ref_size * 2 / 16);
      
      slide.addEventSlide(new nRunnable(this) { public void run(float c) { 
        flt = val_min.get() + c * (val_max.get() - val_min.get()); 
        val_flt.set(flt);
        
        val_label.set(val_txt.get() + " " + RConst.trimFlt(flt) ); 
        out.send(Macro_Packet.newPacketFloat(flt));
      } } );
      
      slide.setValue((flt - val_min.get()) / (val_max.get() - val_min.get()));
      
      front_panel.addEventClose(new nRunnable(this) { public void run() { 
        pan_label = null; slide = null; } } );
    }
  }
  Macro_Connexion in, out;
  
  sStr val_txt, val_label;
  sFlt val_flt,val_min, val_max;
  nLinkedWidget txt_field, min_field, max_field; 
  float flt = 0;
  
  MPanSld(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "pansld", "pansld", _bloc); 
    
    addEmptyS(1);
    val_txt = newStr("txt", "txt", "");
    val_label = newStr("lbl", "lbl", "");
    val_min = newFlt("min", "min", 0);
    val_max = newFlt("max", "max", 1);
    val_flt = newFlt("flt", "flt", flt);
    
    flt = val_flt.get();
    
    val_txt.addEventChange(new nRunnable(this) { public void run() { 
      val_label.set(val_txt.get() + " " + RConst.trimFlt(flt)); } });
    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
    txt_field.setInfo("description");
    min_field = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_min);
    min_field.setInfo("min");
    max_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_max);
    max_field.setInfo("max");
    
    in = addInput(0, "in");
    out = addOutput(1, "out");
    
    in.addEventReceive(new nRunnable(this) { public void run() { 
      if (((slide != null && !slide.curs.isGrabbed()) || slide == null) && 
          in.lastPack() != null && in.lastPack().isFloat() && 
          in.lastPack().asFloat() != flt) {
        flt = in.lastPack().asFloat();
        if (flt < val_min.get()) flt = val_min.get();
        if (flt > val_max.get()) flt = val_max.get();
        val_flt.set(flt);
        
        if (slide != null && !slide.curs.isGrabbed()) slide.setValue((flt - val_min.get()) / (val_max.get() - val_min.get()));
        
        val_label.set(val_txt.get() + " " + RConst.trimFlt(flt) ); 
        if ((slide != null && !slide.curs.isGrabbed()) || slide == null) 
          out.send(Macro_Packet.newPacketFloat(flt));
      }
    } });
    mmain().inter.addEventNextFrame(new nRunnable(this) { public void run() { out.send(Macro_Packet.newPacketFloat(flt)); } });
  }
  public MPanSld clear() {
    super.clear(); return this; }
}

class MPanBin extends MPanTool {  
  nWidget pan_button; 
  nWatcherWidget pan_label;
  
  nRunnable wtch_in_run, trig_widg_run, trig_in_run, swch_widg_run, swch_in_run;
  
  void build_front_panel(nWindowPanel front_panel) {
    if (front_panel != null) {
      
      nDrawer dr = front_panel.getShelf()
        .addSeparator(0.125)
        .addDrawer(10.25, 1);
      pan_label = dr.addWatcherModel("Label-S3");
      pan_label.setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
        .addSeparator()
        ;
      pan_button = dr.addModel("Button-S2-P3");
      
      if (val_butt_txt != null) pan_button.setText(val_butt_txt.get());
      
      param();
      
      front_panel.addEventClose(new nRunnable(this) { public void run() { 
        pan_label = null; pan_button = null; } } );
    }
  }
  
  nLinkedWidget widgWTCH, widgSWCH, widgTRIG; 
  sBoo valWTCH, valSWCH, valTRIG;
  Macro_Connexion in, out;
  
  sStr val_txt, val_butt_txt, val_label; 
  String msg = "";
  nLinkedWidget txt_field, butt_txt_field; 
  
  MPanBin(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "panbin", "panbin", _bloc); 
    
    valWTCH = newBoo("valWTCH", "valWTCH", false);
    valSWCH = newBoo("valSWCH", "valSWCH", false);
    valTRIG = newBoo("valTRIG", "valTRIG", false);
    
    val_txt = newStr("txt", "txt", "");
    val_label = newStr("lbl", "lbl", "");
    val_butt_txt = newStr("b_txt", "b_txt", "");
    
    valWTCH.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    valSWCH.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    valTRIG.addEventChange(new nRunnable(this) { public void run() { param(); } } );
    
    trig_widg_run = new nRunnable(this) { public void run() { out.send(Macro_Packet.newPacketBang()); } };
    
    trig_in_run = new nRunnable(this) { public void run() { ; } };
    
    swch_widg_run = new nRunnable(this) { public void run() { 
      if (pan_button != null) out.send(Macro_Packet.newPacketBool(pan_button.isOn())); } };
    
    swch_in_run = new nRunnable(this) { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool() && pan_button != null) { 
        pan_button.setSwitchState(in.lastPack().asBool()); }
    } };
    
    wtch_in_run = new nRunnable(this) { public void run() { 
      if (in.lastPack() != null) { 
        msg = in.lastPack().getText();
        val_label.set(val_txt.get() + " " + msg); } } };
    
    addEmptyS(1);
    val_txt.addEventChange(new nRunnable(this) { public void run() { 
      val_label.set(val_txt.get() + " " + msg); } });
    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
    txt_field.setInfo("description");
    
    addEmptyS(1);
    val_butt_txt.addEventChange(new nRunnable(this) { public void run() { 
      if (pan_button != null) pan_button.setText(val_butt_txt.get()); } });
    butt_txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_butt_txt);
    butt_txt_field.setInfo("button text");
    
    addEmptyS(1);
    Macro_Element e2 = addEmptyL(0);
    widgWTCH = e2.addLinkedModel("MC_Element_Button_Selector_1", "W").setLinkedValue(valWTCH);
    widgSWCH = e2.addLinkedModel("MC_Element_Button_Selector_2", "S").setLinkedValue(valSWCH);
    widgTRIG = e2.addLinkedModel("MC_Element_Button_Selector_3", "T").setLinkedValue(valTRIG);
    //widgWTCH.addExclude(widgSWCH).addExclude(widgTRIG);
    widgSWCH.addExclude(widgTRIG);
    widgTRIG.addExclude(widgSWCH);
    
    in = addInput(0, "in");
    
    out = addOutput(1, "out");
    
    param();
    
  }
  void param() {
    if (valWTCH.get()) {
      if (pan_label != null) pan_label.setLinkedValue(val_label);
      val_label.set(val_txt.get());
      in.addEventReceive(wtch_in_run);
    } else {
      if (pan_label != null) pan_label.setLinkedValue(val_txt);
      in.removeEventReceive(wtch_in_run);
    }
    if (valSWCH.get()) {
      if (pan_button != null) pan_button
        .setSwitch()
        .clearEventTrigger()
        .addEventSwitchOn(swch_widg_run)
        .addEventSwitchOff(swch_widg_run)
        .show();
      in.addEventReceive(swch_in_run);
      in.removeEventReceive(trig_in_run);
      
    } else if (valTRIG.get()) {
      if (pan_button != null) pan_button
        .setTrigger()
        .clearEventSwitchOn()
        .clearEventSwitchOff()
        .addEventTrigger(trig_widg_run)
        .show();
      //in.setFilterBang().addEventReceive(trig_in_run);
      in.removeEventReceive(swch_in_run);
    } else {
      if (pan_button != null) pan_button.hide();
    }
  }
  public MPanBin clear() {
    super.clear(); return this; }
}
abstract class MPanTool extends Macro_Bloc {  
  abstract void build_front_panel(nWindowPanel front_panel);
  
  MPanel mpanel;
  
  sStr val_pan_title; 
  nLinkedWidget title_field; 
  
  MPanTool(Macro_Sheet _sheet, String r, String s, sValueBloc _bloc) { 
    super(_sheet, r, s, _bloc); 
    
    addEmptyS(1);
    val_pan_title = newStr("pan_title", "pan_title", "");
    val_pan_title.addEventChange(new nRunnable(this) { public void run() { 
      search_panel();
    } });
    title_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_pan_title);
    title_field.setInfo("panel cible title");
    
    mmain().inter.addEventNextFrame(new nRunnable(this) { public void run() { 
      if (mmain().last_added_panel != null && val_pan_title.get().length() == 0) 
    	  val_pan_title.set(mmain().last_added_panel.val_pan_title.get());
      search_panel();
    } });
  }
  void search_panel() {
    if (mpanel != null) mpanel.tool_macros.remove(this);
    if (mpanel != null) mpanel.rebuild();
    if (val_pan_title.get().length() > 0) {
      for (MPanel m : mmain().pan_macros) 
        if (m.val_pan_title.get().equals(val_pan_title.get())) {
          mpanel = m;
          mpanel.tool_macros.add(this);
          mpanel.rebuild();
          break;
        }
    }
  }
  public MPanTool clear() {
    if (mpanel != null) mpanel.tool_macros.remove(this);
    if (mpanel != null && mpanel.front_panel != null) mpanel.rebuild();
    super.clear(); return this; }
}

class MPanel extends Macro_Bloc {  
  nWindowPanel front_panel = null;  
  
  nLinkedWidget stp_view, title_field; 
  nRunnable grab_run, reduc_run;
  sBoo setup_send, menu_reduc; 
  sVec menu_pos;
  sStr val_pan_title; 
  Macro_Connexion in;
  
  ArrayList<MPanTool> tool_macros = new ArrayList<MPanTool>();
  
  MPanel(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "pan", "pan", _bloc); 
    setup_send = newBoo("stp_send", "stp_send", false);
    menu_reduc = newBoo("menu_reduc", "menu_reduc", false);
    menu_pos = newVec("menu_pos", "menu_pos");
    
    grab_run = new nRunnable() { public void run() {
      if (front_panel != null) 
        menu_pos.set(front_panel.grabber.getLocalX(), front_panel.grabber.getLocalY()); } };
    reduc_run = new nRunnable() { public void run() {
      if (front_panel != null) menu_reduc.set(front_panel.collapsed); } };
    
    addEmptyS(1);
    Macro_Element e = addEmptyL(0);
    e.addCtrlModel("MC_Element_Button", "panel").setRunnable(new nRunnable() { public void run() { 
      open_menu(); 
      setup_send.set(true); } });
    e.addLinkedModel("MC_Element_MiniButton", "st").setLinkedValue(setup_send);
    
    addEmptyS(1);
    val_pan_title = newStr("pan_title", "pan_title", "pan_"+mmain().pan_nb);
    title_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_pan_title);
    title_field.setInfo("panel title");
    val_pan_title.addEventChange(new nRunnable(this) { public void run() { 
      if (front_panel != null) front_panel.clear();
      for (MPanTool m : tool_macros) m.val_pan_title.set(val_pan_title.get());
      rebuild();
      //if (front_panel != null) front_panel.grabber.setPosition(menu_pos.get());
    } });
    
    mmain().pan_macros.add(this);
    mmain().pan_nb++;
    mmain().last_added_panel = this;
    
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() { if (setup_send.get()) open_menu(); } });
    
    in = addInput(0, "open").setFilterBang().addEventReceive(new nRunnable() { public void run() { 
      open_menu(); setup_send.set(true);
    } });
  }
  void rebuild() {
    boolean st = setup_send.get();
    boolean op = front_panel != null;
    if (op) { front_panel.clear(); open_menu(); }
    setup_send.set(st);
  }
  void open_menu() {
    if (front_panel == null) {
      front_panel = new nWindowPanel(mmain().screen_gui, mmain().inter.taskpanel, val_pan_title.get());
      front_panel.getShelf(0).addDrawer(4, 0);
      
      //if (setup_send.get()) 
        front_panel.grabber.setPosition(menu_pos.get());
      
      front_panel.addEventClose(new nRunnable(this) { public void run() { 
        front_panel = null; setup_send.set(false); } } );
      
      int prio = 0;
      for (MPanTool m : tool_macros) prio = Math.max(prio, m.priority.get());
      int co_done = 0;
      while (prio >= 0 && co_done <= tool_macros.size()) {
        for (MPanTool m : tool_macros) {
          if (prio == m.priority.get()) { 
            co_done++; 
            m.build_front_panel(front_panel);
          }
        }
        prio--;
      }
      
      front_panel.grabber.addEventDrag(grab_run); 
      front_panel.addEventCollapse(reduc_run); 
      
      if (menu_reduc.get()) front_panel.collapse();
      else front_panel.popUp();
      
    } else front_panel.popUp();
  }
  public MPanel clear() {
    if (front_panel != null) front_panel.clear();
    mmain().pan_macros.remove(this);
    super.clear(); return this; }
}