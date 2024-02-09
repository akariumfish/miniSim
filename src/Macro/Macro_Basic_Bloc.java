package Macro;


import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import sData.*;



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
          packet = Macro_Packet.newPacketFloat(PApplet.parseFloat(t));
          fval.set(PApplet.parseFloat(t));
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
        else if (PApplet.parseFloat(t) != 0) { pin2 = PApplet.parseFloat(t); in2.setLastFloat(pin2); receive(); }
      }
    } });
    String t = view.getText();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); }
      else if (PApplet.parseFloat(t) != 0) { pin2 = PApplet.parseFloat(t); in2.setLastFloat(pin2); }  }
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
        else if (PApplet.parseFloat(t) != 0) { pin2 = PApplet.parseFloat(t); in2.setLastFloat(pin2); receive(); }
      }
    } });
    String t = view.getText();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
      else if (PApplet.parseFloat(t) != 0) { pin2 = PApplet.parseFloat(t); in2.setLastFloat(pin2); receive(); }
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
