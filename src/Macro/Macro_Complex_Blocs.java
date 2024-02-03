package Macro;

import java.util.ArrayList;
import java.util.Map;

import RBase.RConst;
import UI.*;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

//class MTemplate extends Macro_Bloc { 
//  MTemplate(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "tmpl", "tmpl", _bloc); 
//  }
//  MTemplate clear() {
//    super.clear(); return this; }
//}

//class MCursor extends Macro_Bloc { 
//  sStr val_txt; 
//  nLinkedWidget txt_field; nLinkedWidget show_widg;
//  //Macro_Connexion in_p, in_d, out_p, out_d;
//  nCursor cursor;
//  MCursor(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "cursor", "cursor", _bloc); 
//    cursor = new nCursor(sheet, "cursor", "curs");
//    if (sheet.sheet_viewer != null) sheet.sheet_viewer.update();
//    addEmptyS(1);
//    val_txt = newStr("txt", "txt", "cursor");
//    val_txt.addEventChange(new nRunnable(this) { public void run() { 
//      cursor.clear(); 
//      cursor = new nCursor(sheet, val_txt.get(), "curs");
//      cursor.show.set(show_widg.isOn());
//      show_widg.setLinkedValue(cursor.show);
//    } });
//    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
//    //out = addOutput(0, "load");
//    show_widg = addEmptyS(0).addLinkedModel("MC_Element_SButton").setLinkedValue(cursor.show);
//  }
//  MCursor clear() {
//    cursor.clear();
//    if (sheet.sheet_viewer != null) sheet.sheet_viewer.update();
//    super.clear(); return this; }
//}

class MVecCtrl extends Macro_Bloc { 
  void setValue(sValue v) { 
    if (v.type.equals("vec")) {
      if (val_run != null && cible != null) cible.removeEventChange(val_run);
      if (in1_run != null) in1.removeEventReceive(in1_run);
      if (in2_run != null) in2.removeEventReceive(in2_run);
      val_cible.set(v.ref);
      cible = v; val_field.setLinkedValue(cible);
      val_field.setLook(gui.theme.getLook("MC_Element_Field"));
      vval = (sVec)cible;
      out.send(Macro_Packet.newPacketVec(vval.get()));
      val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketVec(vval.get())); }};
      in1_run = new nRunnable() { public void run() { 
        if (in1.getLastPacket() != null && in1.getLastPacket().isBang()) { 
          if (valMAG.get()) {
            PVector p = new PVector().set(vval.get());
            p.setMag(p.mag() + mod_f);
            vval.set(p);
          } else if (valROT.get()) {
            PVector p = new PVector(vval.get().mag(), 0);
            p.rotate(vval.get().heading() + mod_f);
            vval.set(p);
          } else if (valADD.get()) {
            PVector p = new PVector().set(vval.get());
            p.x += mod_vec.x; p.y += mod_vec.y;
            vval.set(p);
          } 
        }
      } };
      in2_run = new nRunnable() { public void run() { 
        if (in2.getLastPacket() != null && in2.getLastPacket().isBang()) { 
          if (valMAG.get()) {
            PVector p = new PVector().set(vval.get());
            p.setMag(p.mag() - mod_f);
            vval.set(p);
          } else if (valROT.get()) {
            PVector p = new PVector(vval.get().mag(), 0);
            p.rotate(vval.get().heading() - mod_f);
            vval.set(p);
          } else if (valADD.get()) {
            PVector p = new PVector().set(vval.get());
            p.x -= mod_vec.x; p.y -= mod_vec.y;
            vval.set(p); //logln("setvec");
          } 
        }
      } };
      v.addEventChange(val_run);
      in1.addEventReceive(in1_run);
      in2.addEventReceive(in2_run);
    }
  }
  nRunnable val_run, in1_run, in2_run;
  sVec vval;
  Macro_Connexion in1, in2, in_m, out;
  sStr val_cible; 
  sBoo search_folder;
  sValue cible;
  nLinkedWidget ref_field, search_view;
  nWatcherWidget val_field;
  nLinkedWidget widgMAG, widgROT, widgADD; 
  sBoo valMAG, valROT, valADD;
  float mod_f = 0; PVector mod_vec;
  nLinkedWidget mod_view1, mod_view2;
  sStr val_mod1, val_mod2; 
  MVecCtrl(Macro_Sheet _sheet, sValueBloc _bloc, sValue v) { 
    super(_sheet, "vecCtrl", "vecCtrl", _bloc); 
    
    val_cible = newStr("cible", "cible", "");
    search_folder = newBoo("search_folder", "search_folder", false);
    mod_vec = new PVector();
    val_mod1 = newStr("mod1", "mod1", "0");
    String t = val_mod1.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { mod_f = 0; mod_vec.x = 0; }
      else if (Float.parseFloat(t) != 0) { mod_f = Float.parseFloat(t); mod_vec.x = mod_f; }
    }
    val_mod2 = newStr("mod2", "mod2", "0");
    t = val_mod2.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { mod_vec.y = 0; }
      else if (Float.parseFloat(t) != 0) { mod_vec.y = Float.parseFloat(t); }
    }
    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
    ref_field.setInfo("value");
    
    val_cible.addEventChange(new nRunnable(this) { public void run() { 
      get_cible(); } } );
    
    search_view = ref_field.getDrawer().addLinkedModel("MC_Element_MiniButton").setLinkedValue(search_folder);
    search_folder.addEventChange(new nRunnable(this) { public void run() { 
      get_cible(); } } );
    
    val_field = addEmptyL(0).addWatcherModel("MC_Element_Text");
    addEmpty(1); addEmpty(1);
    
    in1 = addInput(0, "+").setFilterBang();
    in2 = addInput(0, "-").setFilterBang();
    in_m = addInput(0, "modifier").addEventReceive(new nRunnable() { public void run() { 
      if (in_m.getLastPacket() != null && in_m.getLastPacket().isFloat() && 
          in_m.getLastPacket().asFloat() != mod_f) {
        mod_f = in_m.getLastPacket().asFloat(); 
        mod_view1.setText(RConst.trimStringFloat(mod_f)); 
        mod_view2.setText("-"); 
      } else if (in_m.getLastPacket() != null && in_m.getLastPacket().isVec() && 
          !in_m.getLastPacket().equalsVec(mod_vec)) {
        mod_vec.set(in_m.getLastPacket().asVec()); 
        mod_view1.setText(RConst.trimStringFloat(mod_vec.x)); 
        mod_view2.setText(RConst.trimStringFloat(mod_vec.y)); 
      }
    } });
    
    in1_run = new nRunnable() { public void run() { 
      if (in1.getLastPacket() != null && in1.getLastPacket().isBang()) { 
        if (valMAG.get()) {
          PVector p = new PVector(1, 0);
          p.setMag(mod_f);
          out.send(Macro_Packet.newPacketVec(p));
        } else if (valROT.get()) {
          PVector p = new PVector(1, 0);
          p.rotate(mod_f);
          out.send(Macro_Packet.newPacketVec(p));
        } else if (valADD.get()) {
          out.send(Macro_Packet.newPacketVec(mod_vec));
        } 
      }
    } };
    in2_run = new nRunnable() { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isBang()) { 
        if (valMAG.get()) {
          PVector p = new PVector();
          p.setMag( - mod_f);
          out.send(Macro_Packet.newPacketVec(p));
        } else if (valROT.get()) {
          PVector p = new PVector(1, 0);
          p.rotate( - mod_f);
          out.send(Macro_Packet.newPacketVec(p));
        } else if (valADD.get()) {
          PVector p = new PVector();
          p.x -= mod_vec.x; p.y -= mod_vec.y;
          out.send(Macro_Packet.newPacketVec(p));
        } 
      }
    } };
    in1.addEventReceive(in1_run);
    in2.addEventReceive(in2_run);
    
    
    out = addOutput(1, "out");
    
    mod_view1 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod1);
    mod_view1.addEventFieldChange(new nRunnable() { public void run() { 
      String t = mod_view1.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { mod_f = 0; mod_vec.x = 0; }
        else if (Float.parseFloat(t) != 0) { mod_f = Float.parseFloat(t); mod_vec.x = mod_f; }
      }
    } });
    mod_view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod2);
    mod_view2.addEventFieldChange(new nRunnable() { public void run() { 
      String t = mod_view2.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { mod_vec.y = 0; }
        else if (Float.parseFloat(t) != 0) { mod_vec.y = Float.parseFloat(t); }
      }
    } });
    
    valMAG = newBoo("valMAG", "valMAG", false);
    valROT = newBoo("valROT", "valROT", false);
    valADD = newBoo("valADD", "valADD", false);
    
    Macro_Element e = addEmptyL(0);
    widgMAG = e.addLinkedModel("MC_Element_Button_Selector_1", "Mag").setLinkedValue(valMAG);
    widgROT = e.addLinkedModel("MC_Element_Button_Selector_2", "Rot").setLinkedValue(valROT);
    widgADD = e.addLinkedModel("MC_Element_Button_Selector_4", "Add").setLinkedValue(valADD);
    widgMAG.addExclude(widgROT).addExclude(widgADD);
    widgROT.addExclude(widgMAG).addExclude(widgADD);
    widgADD.addExclude(widgROT).addExclude(widgMAG);
    
    if (v != null) setValue(v);
    else {
      get_cible();
    }
  }
  void get_cible() {
    val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
    if (!search_folder.get()) cible = sheet.value_bloc.getValue(val_cible.get());
    else if (sheet != mmain()) cible = sheet.sheet.value_bloc.getValue(val_cible.get());
    if (cible != null) setValue(cible);
  }
  public MVecCtrl clear() {
    if (val_run != null && cible != null) cible.removeEventChange(val_run);
    super.clear(); return this; }
}



class MColRGB extends Macro_Bloc {
  Macro_Connexion in1,in2,in3,out1,out2,out3;
  float r = 0, g = 0, b = 0;
  int col = 0;
  nLinkedWidget view1, view2, view3;
  sInt val_view1, val_view2, val_view3; 
  MColRGB(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "colRGB", "colRGB", _bloc); 
    
    in1 = addInput(0, "c/r").addEventReceive(new nRunnable() { public void run() { 
      if (in1.getLastPacket() != null && in1.getLastPacket().isCol() && 
          !in1.getLastPacket().equalsCol(col)) {
        col = in1.getLastPacket().asCol();
        float m = gui.app.red(col); float d = gui.app.green(col); float f = gui.app.blue(col);
        if (m != r) { r = m; out1.send(Macro_Packet.newPacketFloat(r)); }
        if (d != g) { g = d; out2.send(Macro_Packet.newPacketFloat(g)); }
        if (f != b) { b = f; out3.send(Macro_Packet.newPacketFloat(b)); }
        col = gui.app.color(r,g,b);
      } else if (in1.getLastPacket() != null && in1.getLastPacket().isFloat() && 
                 in1.getLastPacket().asFloat() != g) {
        r = in1.getLastPacket().asFloat();
        view1.changeText(""+r); 
        col = gui.app.color(r,g,b);
        out1.send(Macro_Packet.newPacketCol(col));
      }
    } });
    in2 = addInput(0, "g").addEventReceive(new nRunnable() { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isFloat() && 
                 in2.getLastPacket().asFloat() != g) {
        g = in2.getLastPacket().asFloat();
        view2.changeText(""+g); 
        col = gui.app.color(r,g,b);
        out1.send(Macro_Packet.newPacketCol(col));
      }
    } });
    in3 = addInput(0, "b").addEventReceive(new nRunnable() { public void run() { 
      if (in3.getLastPacket() != null && in3.getLastPacket().isFloat() && 
                 in3.getLastPacket().asFloat() != b) {
        b = in3.getLastPacket().asFloat();
        view3.changeText(""+b); 
        col = gui.app.color(r,g,b);
        out1.send(Macro_Packet.newPacketCol(col));
      }
    } });
    out1 = addOutput(2, "c/r");
    out2 = addOutput(2, "g");
    out3 = addOutput(2, "b");
    
    val_view1 = newInt("r", "r", 0);
    val_view2 = newInt("g", "g", 0);
    val_view3 = newInt("b", "b", 0);
    
    col = gui.app.color(r,g,b);
    view1 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
    view1.setInfo("r");
    view1.addEventFieldChange(new nRunnable() { public void run() { 
      r = val_view1.get();
      col = gui.app.color(r,g,b);
      out1.send(Macro_Packet.newPacketCol(col));
    } });
    view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view2);
    view2.setInfo("g");
    view2.addEventFieldChange(new nRunnable() { public void run() { 
      g = val_view2.get();
      col = gui.app.color(r,g,b);
      out1.send(Macro_Packet.newPacketCol(col));
    } });
    view3 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view3);
    view3.setInfo("b");
    view3.addEventFieldChange(new nRunnable() { public void run() { 
      b = val_view3.get();
      col = gui.app.color(r,g,b);
      out1.send(Macro_Packet.newPacketCol(col));
    } });
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() { //add un swtch start send
      //out1.send(Macro_Packet.newPacketVec(vec));
    } });
  }
  public MColRGB clear() {
    super.clear(); return this; }
}


class MMIDI extends Macro_Bloc { 
  
  sStr val_txt; 
  nLinkedWidget txt_field; 
  Macro_Connexion in, out;
  
  MMIDI(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "midi", "midi", _bloc); 
    
    addEmptyS(1);
    val_txt = newStr("txt", "txt", "");
    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
    
    in = addInput(0, "in").setFilterBang().addEventReceive(new nRunnable() { public void run() { } });
    out = addOutput(1, "out");
    
    mmain().midi_macros.add(this);
  }
  void noteOn(int channel, int pitch, int velocity) {
    // Receive a noteOn
//    println();
//    println("Note On:");
//    println("Channel:"+channel);
//    println("Pitch:"+pitch);
//    println("Velocity:"+velocity);
  }
  
  void noteOff(int channel, int pitch, int velocity) {
    // Receive a noteOff
//    println();
//    println("Note Off:");
//    println("Channel:"+channel);
//    println("Pitch:"+pitch);
//    println("Velocity:"+velocity);
  }
  
  void controllerChange(int channel, int number, int value) {
    // Receive a controllerChange
//    println();
//    println("Controller Change:");
//    println("Channel:"+channel);
//    println("Number:"+number);
//    println("Value:"+value);
  }
  public MMIDI clear() {
    mmain().midi_macros.remove(this);
    super.clear(); return this; }
}

class MPreset extends Macro_Bloc { 
  sStr val_txt; 
  nLinkedWidget txt_field; nCtrlWidget load_widg;
  Macro_Connexion in;
  MPreset(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "preset", "preset", _bloc); 
    addEmptyS(1);
    val_txt = newStr("txt", "txt", "");
    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
    in = addInput(0, "load").setFilterBang().addEventReceive(new nRunnable() { public void run() { 
      if (in.getLastPacket() != null && in.getLastPacket().isBang()) { load_prst(); }
    } });
    load_widg = addEmptyS(1).addCtrlModel("MC_Element_SButton")
      .setRunnable(new nRunnable() { public void run() { load_prst(); }});
  }
  void load_prst() {
    for (Map.Entry me : mmain().saved_preset.blocs.entrySet()) {
      sValueBloc vb = ((sValueBloc)me.getValue());
      if (vb.ref.equals(val_txt.get())) {
        mmain().inter.data.transfer_bloc_values(vb, sheet.value_bloc);
        break;
      }
    }
  }
  public MPreset clear() {
    super.clear(); return this; }
}






class MRamp extends Macro_Bloc { 
  Macro_Connexion in_tick, in_start, in_stop, in_reset, out_val, out_end;
  sBoo valONE, valRPT, valINV, valLOOP;
  sBoo valLIN, valSIN;
  sFlt val_strt, val_end;
  sInt val_period, val_phi;
  nLinkedWidget len_field, str_field, end_field, phi_field;
  nRunnable reset_run;
  MRamp(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "ramp", "ramp", _bloc); 
    
    valONE = newBoo("valONE", "valONE", true);
    valRPT = newBoo("valRPT", "valRPT", false);
    valINV = newBoo("valINV", "valINV", false);
    valLOOP = newBoo("valLOOP", "valLOOP", false);
    valLIN = newBoo("valLIN", "valLIN", true);
    valSIN = newBoo("valSIN", "valSIN", false);
    
    val_period = newInt("val_period", "period", 10);
    val_phi = newInt("val_phi", "phi", 0);
    val_strt = newFlt("val_strt", "str", 0);
    val_end = newFlt("val_end", "end", 1);
    
    reset_run = new nRunnable() { public void run() { got_reset(); } };
    
    val_period.addEventChange(reset_run);
    val_strt.addEventChange(reset_run);
    val_end.addEventChange(reset_run);
    valONE.addEventChange(new nRunnable() { public void run() { if (valONE.get()) got_reset(); } });
    valRPT.addEventChange(new nRunnable() { public void run() { if (valRPT.get()) got_reset(); } });
    valINV.addEventChange(new nRunnable() { public void run() { if (valINV.get()) got_reset(); } });
    valLOOP.addEventChange(new nRunnable() { public void run() { if (valLOOP.get()) got_reset(); } });
    valLIN.addEventChange(new nRunnable() { public void run() { if (valLIN.get()) got_reset(); } });
    valSIN.addEventChange(new nRunnable() { public void run() { if (valSIN.get()) got_reset(); } });
    
    in_tick = addInput(0, "tick / period").addEventReceive(new nRunnable() { public void run() { 
      if (in_tick.getLastPacket() != null && in_tick.getLastPacket().isBang()) { got_tick(); } 
      if (in_tick.getLastPacket() != null && in_tick.getLastPacket().isFloat()) 
        val_period.set((int)(in_tick.getLastPacket().asFloat()));
      if (in_tick.getLastPacket() != null && in_tick.getLastPacket().isInt()) 
        val_period.set(in_tick.getLastPacket().asInt()); } });
    in_start = addInput(0, "start / phi").addEventReceive(new nRunnable() { public void run() { 
      if (in_start.getLastPacket() != null && in_start.getLastPacket().isBang()) { got_start(); } 
      if (in_start.getLastPacket() != null && in_start.getLastPacket().isFloat()) 
        val_phi.set((int)(in_start.getLastPacket().asFloat()));
      if (in_start.getLastPacket() != null && in_start.getLastPacket().isInt()) 
        val_phi.set(in_start.getLastPacket().asInt()); } });
    in_stop = addInput(0, "stop / end").addEventReceive(new nRunnable() { public void run() { 
      if (in_stop.getLastPacket() != null && in_stop.getLastPacket().isBang()) { got_stop(); } 
      if (in_stop.getLastPacket() != null && in_stop.getLastPacket().isFloat()) 
        val_end.set(in_stop.getLastPacket().asFloat());
      if (in_stop.getLastPacket() != null && in_stop.getLastPacket().isInt()) 
        val_end.set(in_stop.getLastPacket().asInt()); } });
    
    out_val = addOutput(2, "val").setDefFloat();
    out_end = addOutput(2, "end").setDefBang();
    
    len_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_period);
    phi_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_phi);
    str_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_strt);
    end_field = addEmptyS(2).addLinkedModel("MC_Element_SField").setLinkedValue(val_end);
    str_field.setInfo("start value");
    end_field.setInfo("end value");
    len_field.setInfo("period in tick");
    phi_field.setInfo("dephasing in tick");
    
    addSelectS(0, valLIN, valSIN, "Lin", "Sin");
    addSelectL(1, valONE, valRPT, valINV, valLOOP, "1", "RP", "IV", "LP");
    
    got_reset();
  }
  float getval() {
    float v = 0;
    if (valLIN.get()) v = val_strt.get() + ((float)(count) / (float)(val_period.get())) * (val_end.get() - val_strt.get());
    else v = (float) (val_strt.get() + (0.5 + Math.cos(RConst.PI * ((float)(count) / (float)(val_period.get()))) / 2 ) * (val_end.get() - val_strt.get()));
    return v;
  }
  int count = 0;
  boolean isrunning = false;
  void got_tick() {
    if (isrunning) {
      if (valONE.get()) {
        count++;
        out_val.send(Macro_Packet.newPacketFloat(getval()));
        if (count == val_period.get()) {
          isrunning = false;
          out_end.send(Macro_Packet.newPacketBang());
        }
      } else if (valRPT.get()) {
        count++;
        out_val.send(Macro_Packet.newPacketFloat(getval()));
        if (count == val_period.get()) {
          count = val_phi.get();
          out_end.send(Macro_Packet.newPacketBang());
        }
      } else if (valINV.get()) {
        count--;
        out_val.send(Macro_Packet.newPacketFloat(getval()));
        if (count == 0) {
          count = val_period.get() + 1 - val_phi.get();
          out_end.send(Macro_Packet.newPacketBang());
        }
      } else if (valLOOP.get()) {
        if (loop_up) {
          count++;
          out_val.send(Macro_Packet.newPacketFloat(getval()));
          if (count == val_period.get()) {
            loop_up = false;
          }
        } else {
          count--;
          out_val.send(Macro_Packet.newPacketFloat(getval()));
          if (count == 0) {
            out_end.send(Macro_Packet.newPacketBang());
            loop_up = true;
          }
        }
      }
    }
  }
  boolean loop_up = true;
  void got_start() {
    got_reset();
    isrunning = true;
  }
  void got_stop() {
    got_reset();
    isrunning = false;
    //if (!valINV.get()) out_val.send(Macro_Packet.newPacketFloat(val_end.get()));
    //else out_val.send(Macro_Packet.newPacketFloat(val_strt.get()));
  }
  void got_reset() {
    if (valONE.get()) {
      count = 0;
    } else if (valRPT.get()) {
      count = val_phi.get();
    } else if (valINV.get()) {
      count = val_period.get() + 1 - val_phi.get();
    } else if (valLOOP.get()) {
      count = val_phi.get();
      loop_up = true;
    }
  }
  public MRamp clear() {
    super.clear(); return this; }
}



class MCrossVec extends Macro_Bloc {
  Macro_Connexion in_m, out_d, out_t;
  PVector dir = new PVector();
  PVector pdir = new PVector();
  sFlt val_mag;
  nLinkedWidget mag_field;
  
  MCrossVec(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "crossVec", "crossVec", _bloc); 
    val_mag = newFlt("val_mag", "mag", 1);
    mag_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mag);
    
    in_m = addInput(0, "magnitude").addEventReceive(new nRunnable() { public void run() { 
      if (in_m.getLastPacket() != null && in_m.getLastPacket().isFloat() && 
          in_m.getLastPacket().asFloat() != val_mag.get()) {
        val_mag.set(in_m.getLastPacket().asFloat());
      } 
    } });
    
    out_t = addOutput(1, "trig")
      .setDefBang();
    out_d = addOutput(2, "direction vec")
      .setDefVec();
    gui.addEventFrame(new nRunnable() { public void run() {
      //logln("key up");
      dir.set(0, 0);
      if (!gui.field_used && !mmain().screen_gui.field_used) {
        if ( mmain().inter.input.getState('z') ) dir.y -=1;
        if ( mmain().inter.input.getState('s') ) dir.y +=1;
        if ( mmain().inter.input.getState('q') ) dir.x -=1;
        if ( mmain().inter.input.getState('d') ) dir.x +=1;
      }
      if ( dir.magSq() > 0) {
        dir.setMag(val_mag.get());
        if (!pdir.equals(dir)) out_d.send(Macro_Packet.newPacketVec(dir));
        pdir.set(dir);
        out_t.send(Macro_Packet.newPacketBang());
      } else if (!pdir.equals(dir)) {
        out_d.send(Macro_Packet.newPacketVec(dir));
        pdir.set(dir);
      }
    } } );
  }
  
  public MCrossVec clear() {
    super.clear(); return this; }
  
}




class MNumCtrl extends Macro_Bloc { 
  void setValue(sValue v) {
    if (v.type.equals("flt") || v.type.equals("int")) {
      if (val_run != null && cible != null) cible.removeEventChange(val_run);
      if (in1_run != null) in1.removeEventReceive(in1_run);
      if (in2_run != null) in2.removeEventReceive(in2_run);
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
    in1_run = new nRunnable() { public void run() { 
      if (in1.getLastPacket() != null && in1.getLastPacket().isBang()) { 
        if (valFAC.get()) fval.set(fval.get()*mod); 
        if (valINC.get()) fval.set(fval.get()+mod); }
    } };
    in2_run = new nRunnable() { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isBang()) { 
        if (valINC.get()) fval.set(fval.get()-mod); 
        if (valFAC.get() && mod != 0) fval.set(fval.get()/mod); }
    } };
    v.addEventChange(val_run);
    in1.addEventReceive(in1_run);
    in2.addEventReceive(in2_run);
  }
  void setValue(sInt v) {
    ival = v;
    out.send(Macro_Packet.newPacketFloat(v.get()));
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(ival.get())); }};
    in1_run = new nRunnable() { public void run() { 
      if (in1.getLastPacket() != null && in1.getLastPacket().isBang()) { 
        if (valFAC.get()) ival.set((int)(ival.get()*mod)); 
        if (valINC.get()) ival.set((int)(ival.get()+mod)); }
    } };
    in2_run = new nRunnable() { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isBang()) { 
        if (valINC.get()) ival.set((int)(ival.get()-mod)); 
        if (valFAC.get() && mod != 0) ival.set((int)(ival.get()/mod)); }
    } };
    v.addEventChange(val_run);
    in1.addEventReceive(in1_run);
    in2.addEventReceive(in2_run);
  }
  nRunnable val_run, in1_run, in2_run;
  sInt ival; sFlt fval;
  Macro_Connexion in1, in2, in_m, out;
  sStr val_cible; 
  sValue cible;
  nLinkedWidget ref_field, val_field;
  nLinkedWidget widgFAC, widgINC; 
  sBoo valFAC, valINC;
  float mod = 0;
  nLinkedWidget mod_view;
  sStr val_mod; 
  MNumCtrl(Macro_Sheet _sheet, sValueBloc _bloc, sValue v) { 
    super(_sheet, "numCtrl", "numCtrl", _bloc); 
    
    val_cible = newStr("cible", "cible", "");
    
    val_mod = newStr("mod", "mod", "2");
    String t = val_mod.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { mod = 0; }
      else if (Float.parseFloat(t) != 0) { mod = Float.parseFloat(t); }
    }
    
    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
    val_field = addEmptyL(0).addLinkedModel("MC_Element_Field");
    val_cible.addEventChange(new nRunnable(this) { public void run() { 
      cible = sheet.value_bloc.getValue(val_cible.get());
      if (cible != null) setValue(cible); } } );
    
    addEmpty(1); addEmpty(1);
    
    in1 = addInput(0, "+/x").setFilterBang();
    in2 = addInput(0, "-//").setFilterBang();
    in_m = addInput(0, "modifier").setFilterFloat().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
      if (in_m.getLastPacket() != null && in_m.getLastPacket().isFloat() && 
          in_m.getLastPacket().asFloat() != mod) {
        mod = in_m.getLastPacket().asFloat(); mod_view.setText(RConst.trimStringFloat(mod)); } } });
    
    
    out = addOutput(1, "out");
    
    mod_view = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod);
    mod_view.addEventFieldChange(new nRunnable() { public void run() { 
      String t = mod_view.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { mod = 0; }
        else if (Float.parseFloat(t) != 0) { mod = Float.parseFloat(t); }
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
  public MNumCtrl clear() {
    if (val_run != null && cible != null) cible.removeEventChange(val_run);
    super.clear(); return this; }
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
      else if (Float.parseFloat(t) != 0) { mod = Float.parseFloat(t); }
    }
    
    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
    val_field = addEmptyL(0).addLinkedModel("MC_Element_Field");
    val_cible.addEventChange(new nRunnable(this) { public void run() { 
      cible = sheet.value_bloc.getValue(val_cible.get());
      if (cible != null) setValue(cible); } } );
    
    addEmpty(1); addEmpty(1);
    
    in_m = addInput(0, "modifier").setFilterFloat().setLastFloat(mod).addEventReceive(new nRunnable() { public void run() { 
      if (in_m.getLastPacket() != null && in_m.getLastPacket().isFloat() && 
          in_m.getLastPacket().asFloat() != mod) {
        mod = in_m.getLastPacket().asFloat(); mod_view.setText(RConst.trimStringFloat(mod)); } } });
    
    
    out = addOutput(1, "out");
    
    mod_view = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod);
    mod_view.addEventFieldChange(new nRunnable() { public void run() { 
      String t = mod_view.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { mod = 0; }
        else if (Float.parseFloat(t) != 0) { mod = Float.parseFloat(t); }
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
      if (in1.getLastPacket() != null && !in1.getLastPacket().isBool()) { 
        msg = in1.getLastPacket().getText();
        val_lbl1.set(val_txt1.get() + " " + msg); 
        if (trig1 != null) trig1.setText(val_txt1.get()); } 
      if (in1.getLastPacket() != null && in1.getLastPacket().isBool() && trig1 != null) { 
        trig1.setSwitchState(in1.getLastPacket().asBool()); b1 = in1.getLastPacket().asBool(); }
    } });
    in2 = addInput(0, "in2").addEventReceive(new nRunnable(this) { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isBool() && trig2 != null) { 
        trig2.setSwitchState(in2.getLastPacket().asBool()); b2 = in2.getLastPacket().asBool(); }
    } });
    in3 = addInput(0, "in3").addEventReceive(new nRunnable(this) { public void run() { 
      if (in3.getLastPacket() != null && in3.getLastPacket().isBool() && trig3 != null) { 
        trig3.setSwitchState(in3.getLastPacket().asBool()); b3 = in3.getLastPacket().asBool(); }
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
      if (in.getLastPacket() != null) { 
        msg = in.getLastPacket().getText();
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
      
      for (MToolRow m : tool_macros) m.build_front_panel(front_panel);
      
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
    mmain().pan_macros.remove(this);
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
  float max = 10;
  
  MPanGrph(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "pangrph", "pangrph", _bloc); 
    
    addEmptyS(1);
    val_txt = newStr("txt", "txt", "");
    val_label = newStr("lbl", "lbl", "");
    val_txt.addEventChange(new nRunnable(this) { public void run() { 
      val_label.set(val_txt.get() + " " + RConst.trimStringFloat(flt)); } });
    txt_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_txt);
    txt_field.setInfo("description");
    
    in_val = addInput(0, "val").addEventReceive(new nRunnable(this) { public void run() { 
      if (in_val.getLastPacket() != null && in_val.getLastPacket().isFloat()) {
        flt = in_val.getLastPacket().asFloat();
        val_label.set(val_txt.get() + " " + RConst.trimStringFloat(flt) ); 
      }
      if (in_val.getLastPacket() != null && in_val.getLastPacket().isInt()) {
        flt = in_val.getLastPacket().asInt();
        val_label.set(val_txt.get() + " " + RConst.trimStringFloat(flt) ); 
      }
    } });
    in_tick = addInput(0, "tick").addEventReceive(new nRunnable(this) { public void run() { 
      if (in_tick.getLastPacket() != null && in_tick.getLastPacket().isBang() && 
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
        
        val_label.set(val_txt.get() + " " + RConst.trimStringFloat(flt) ); 
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
      val_label.set(val_txt.get() + " " + RConst.trimStringFloat(flt)); } });
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
          in.getLastPacket() != null && in.getLastPacket().isFloat() && 
          in.getLastPacket().asFloat() != flt) {
        flt = in.getLastPacket().asFloat();
        if (flt < val_min.get()) flt = val_min.get();
        if (flt > val_max.get()) flt = val_max.get();
        val_flt.set(flt);
        
        if (slide != null && !slide.curs.isGrabbed()) slide.setValue((flt - val_min.get()) / (val_max.get() - val_min.get()));
        
        val_label.set(val_txt.get() + " " + RConst.trimStringFloat(flt) ); 
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
      if (in.getLastPacket() != null && in.getLastPacket().isBool() && pan_button != null) { 
        pan_button.setSwitchState(in.getLastPacket().asBool()); }
    } };
    
    wtch_in_run = new nRunnable(this) { public void run() { 
      if (in.getLastPacket() != null) { 
        msg = in.getLastPacket().getText();
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
      
      for (MPanTool m : tool_macros) m.build_front_panel(front_panel);
      
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

class MMenu extends Macro_Bloc {  
  Macro_Connexion in;
  nLinkedWidget stp_view; sBoo setup_send, menu_reduc; sVec menu_pos; sInt menu_tab;
  nRunnable grab_run, reduc_run, close_run, tab_run;
  MMenu(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "menu", "menu", _bloc); 
    setup_send = newBoo("stp_snd", "stp_snd", false);
    menu_reduc = newBoo("menu_reduc", "menu_reduc", false);
    menu_tab = newInt("menu_tab", "menu_tab", 0);
    menu_pos = newVec("menu_pos", "menu_pos");
    //addEmptyS(1);
    Macro_Element e = addEmptyS(0);
    e.addCtrlModel("MC_Element_SButton", "menu").setRunnable(new nRunnable() { public void run() {
      menu(); } }).setInfo("open sheet general menu");
    e.addLinkedModel("MC_Element_MiniButton", "st").setLinkedValue(setup_send);
    
    grab_run = new nRunnable() { public void run() {
      if (sheet.sheet_front != null) 
        menu_pos.set(sheet.sheet_front.grabber.getLocalX(), sheet.sheet_front.grabber.getLocalY());
    } };
    reduc_run = new nRunnable() { public void run() {
      if (sheet.sheet_front != null) 
        menu_reduc.set(sheet.sheet_front.collapsed);
    } };
    tab_run = new nRunnable() { public void run() {
      if (sheet.sheet_front != null) 
        menu_tab.set(sheet.sheet_front.current_tab_id);
    } };
    close_run = new nRunnable() { public void run() { setup_send.set(false); } };
    if (setup_send.get()) mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
      menu();
    } });
    in = addInput(0, "open").setFilterBang().addEventReceive(new nRunnable() { public void run() { 
      menu(); setup_send.set(true);
    } });
  }
  void menu() {
    sheet.build_sheet_menu();
    if (sheet.sheet_front != null) { 
      if (setup_send.get()) sheet.sheet_front.grabber.setPosition(menu_pos.get());
      if (setup_send.get() && menu_reduc.get()) sheet.sheet_front.collapse();
      if (setup_send.get() && !menu_reduc.get()) sheet.sheet_front.popUp();
      if (setup_send.get()) sheet.sheet_front.setTab(menu_tab.get());
      setup_send.set(true);
      sheet.sheet_front.grabber.addEventDrag(grab_run); 
      sheet.sheet_front.addEventCollapse(reduc_run);  
      sheet.sheet_front.addEventClose(close_run);  
      sheet.sheet_front.addEventTab(tab_run); 
    }
  }
  public MMenu clear() {
    if (sheet.sheet_front != null) sheet.sheet_front.grabber.removeEventDrag(grab_run);
    if (sheet.sheet_front != null) sheet.sheet_front.removeEventCollapse(reduc_run); 
    if (sheet.sheet_front != null) sheet.sheet_front.removeEventClose(close_run); 
    if (sheet.sheet_front != null) sheet.sheet_front.clear();
    super.clear(); return this; }
}




class MVecXY extends Macro_Bloc {
  Macro_Connexion in1,in2,out1,out2;
  float x = 0, y = 0;
  PVector vec;
  nLinkedWidget view1, view2;
  sStr val_view1, val_view2; 
  MVecXY(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "vecXY", "vecXY", _bloc); 
    
    in1 = addInput(0, "v/x").addEventReceive(new nRunnable() { public void run() { 
      if (in1.getLastPacket() != null && in1.getLastPacket().isVec() && 
          (in1.getLastPacket().asVec().x != vec.x || in1.getLastPacket().asVec().y != vec.y)) {
        vec.set(in1.getLastPacket().asVec());
        float m = vec.x; float d = vec.y;
        if (m != x) { x = m; out1.send(Macro_Packet.newPacketFloat(m)); }
        if (d != y) { y = d; out2.send(Macro_Packet.newPacketFloat(d)); }
      } else if (in1.getLastPacket() != null && in1.getLastPacket().isFloat() && 
                 in1.getLastPacket().asFloat() != x) {
        x = in1.getLastPacket().asFloat();
        view1.changeText(RConst.trimStringFloat(x)); 
        vec.set(x, y);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    in2 = addInput(0, "y").addEventReceive(new nRunnable() { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isFloat() && 
                 in2.getLastPacket().asFloat() != y) {
        y = in2.getLastPacket().asFloat();
        view2.changeText(RConst.trimStringFloat(y)); 
        vec.set(x, y);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    out1 = addOutput(1, "v/x");
    out2 = addOutput(1, "y");
    
    vec = new PVector(1, 0);
    
    val_view1 = newStr("x", "x", "0");
    val_view2 = newStr("y", "y", "0");
    
    String t = val_view1.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { x = 0; }
      else if (Float.parseFloat(t) != 0) { x = Float.parseFloat(t); }
    }
    t = val_view2.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { y = 0; }
      else if (Float.parseFloat(t) != 0) { y = Float.parseFloat(t); }
    }
    vec.set(x, y);
    view1 = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
    view1.setInfo("x");
    view1.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view1.getText();
      float a = x;
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { x = 0; }
        else if (Float.parseFloat(t) != 0) { x = Float.parseFloat(t); }
      }
      if (x != a) {
        //view1.changeText(RConst.trimStringFloat(x)); 
        vec.set(x, y);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view2);
    view2.setInfo("y");
    view2.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view2.getText();
      float a = y;
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { y = 0; }
        else if (Float.parseFloat(t) != 0) { y = Float.parseFloat(t); }
      }
      if (y != a) {
        //view2.changeText(RConst.trimStringFloat(y)); 
        vec.set(x, y);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() { //add un swtch start send
      //out1.send(Macro_Packet.newPacketVec(vec));
    } });
  }
  public MVecXY clear() {
    super.clear(); return this; }
}
class MVecMD extends Macro_Bloc {
  Macro_Connexion in1,in2,out1,out2;
  float mag = 1, dir = 0;
  PVector vec;
  nLinkedWidget view1, view2;
  sStr val_view1, val_view2; 
  MVecMD(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "vecMD", "vecMD", _bloc); 
    
    in1 = addInput(0, "v/mag").addEventReceive(new nRunnable() { public void run() { 
      if (in1.getLastPacket() != null && in1.getLastPacket().isVec() && 
          (in1.getLastPacket().asVec().x != vec.x || in1.getLastPacket().asVec().y != vec.y)) {
        vec.set(in1.getLastPacket().asVec());
        float m = vec.mag(); float d = vec.heading();
        if (m != mag) { mag = m; out1.send(Macro_Packet.newPacketFloat(m)); }
        if (d != dir) { dir = d; out2.send(Macro_Packet.newPacketFloat(d)); }
      } else if (in1.getLastPacket() != null && in1.getLastPacket().isFloat() && 
                 in1.getLastPacket().asFloat() != mag) {
        mag = in1.getLastPacket().asFloat();
        view1.changeText(RConst.trimStringFloat(mag)); 
        vec.set(mag, 0).rotate(dir);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    in2 = addInput(0, "dir").addEventReceive(new nRunnable() { public void run() { 
      if (in2.getLastPacket() != null && in2.getLastPacket().isFloat() && 
                 in2.getLastPacket().asFloat() != dir) {
        dir = in2.getLastPacket().asFloat();
        view2.changeText(RConst.trimStringFloat(dir)); 
        vec.set(mag, 0).rotate(dir);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    out1 = addOutput(1, "v/mag");
    out2 = addOutput(1, "dir");
    
    vec = new PVector(1, 0);
    
    val_view1 = newStr("mag", "mag", "1");
    val_view2 = newStr("dir", "dir", "0");
    
    String t = val_view1.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { mag = 0; }
      else if (Float.parseFloat(t) != 0) { mag = Float.parseFloat(t); }
    }
    t = val_view2.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { dir = 0; }
      else if (Float.parseFloat(t) != 0) { dir = Float.parseFloat(t); }
    }
    vec.set(mag, 0).rotate(dir);
    view1 = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
    view1.setInfo("mag");
    view1.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view1.getText();
      float a = mag;
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { mag = 0; }
        else if (Float.parseFloat(t) != 0) { mag = Float.parseFloat(t); }
      }
      if (mag != a) {
        //view1.changeText(RConst.trimStringFloat(mag)); 
        vec.set(mag, 0).rotate(dir);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view2);
    view2.setInfo("dir");
    view2.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view2.getText();
      float a = dir;
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { dir = 0; }
        else if (Float.parseFloat(t) != 0) { dir = Float.parseFloat(t); }
      }
      if (dir != a) {
        //view2.changeText(RConst.trimStringFloat(dir)); 
        vec.set(mag, 0).rotate(dir);
        out1.send(Macro_Packet.newPacketVec(vec));
      }
    } });
    
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
      out1.send(Macro_Packet.newPacketVec(vec));
    } });
  }
  public MVecMD clear() {
    super.clear(); return this; }
}

class MRandom extends Macro_Bloc { 
  Macro_Connexion in, out;
  float min = 0, max = 1;
  nLinkedWidget view1, view2;
  sStr val_view1, val_view2; 
  MRandom(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "rng", "rng", _bloc); 
    
    in = addInput(0, "bang").setFilterBang().addEventReceive(new nRunnable() { public void run() { 
      if (in.getLastPacket() != null && in.getLastPacket().isBang()) {
        out.send(Macro_Packet.newPacketFloat(gui.app.random(min, max))); } } });
    
    out = addOutput(1, "out")
      .setDefFloat();
      
    val_view1 = newStr("min", "min", "0");
    val_view2 = newStr("max", "max", "1");
    
    String t = val_view1.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { min = 0; }
      else if (Float.parseFloat(t) != 0) { min = Float.parseFloat(t); }
    }
    t = val_view2.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { max = 0; }
      else if (Float.parseFloat(t) != 0) { max = Float.parseFloat(t); }
    }
    view1 = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
    view1.setInfo("min");
    view1.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view1.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { min = 0; }
        else if (Float.parseFloat(t) != 0) { min = Float.parseFloat(t); }
      }
      if (min > max) { float a = min; min = max; max = a; }
      //view1.setText(RConst.trimStringFloat(min)); 
      //view2.setText(RConst.trimStringFloat(max)); 
    } });
    view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view2);
    view2.setInfo("max");
    view2.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view2.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { max = 0; }
        else if (Float.parseFloat(t) != 0) { max = Float.parseFloat(t); }
      }
      if (min > max) { float a = min; min = max; max = a; }
      //view1.setText(RConst.trimStringFloat(min)); 
      //view2.setText(RConst.trimStringFloat(max)); 
    } });
  }
  public MRandom clear() {
    super.clear(); return this; }
}

class MMouse extends Macro_Bloc { 
  Macro_Connexion out1, outt, out3, outb;
  nRunnable run, hovernotfound_run;
  PVector m, pm, mm, v;
  boolean st = false;
  MMouse(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "mouse", "mouse", _bloc); 
    out1 = addOutput(0, "pos"); out3 = addOutput(0, "mouv");
    outt = addOutput(0, "trig"); outb = addOutput(0, "state"); 
    m = new PVector(0, 0); pm = new PVector(0, 0); mm = new PVector(0, 0); v = new PVector(0, 0);
    run = new nRunnable() { public void run() { 
      if (m.x != gui.mouseVector.x || m.y != gui.mouseVector.y) { 
        out1.send(Macro_Packet.newPacketVec(gui.mouseVector));
        m.set(gui.mouseVector); }
      if (pm.x != gui.pmouseVector.x || pm.y != gui.pmouseVector.y) { 
        //out2.send(Macro_Packet.newPacketVec(gui.pmouseVector));
        pm.set(gui.pmouseVector); }
      v.set(gui.mouseVector);
      v = v.sub(gui.pmouseVector);
      if (mm.x != v.x || mm.y != v.y) { 
        out3.send(Macro_Packet.newPacketVec(v));
        mm.set(v); }
    } };
    hovernotfound_run = new nRunnable() { public void run() { 
      if (mmain().inter.input.mouseLeft.trigClick) {
        outt.send(Macro_Packet.newPacketBang());
      }
      boolean ms = mmain().inter.input.mouseLeft.state;
      if (st != ms) {
        st = ms;
        outb.send(Macro_Packet.newPacketBool(st));
      }
    } };
    mmain().inter.addEventFrame(run);
    mmain().inter.addEventHoverNotFound(hovernotfound_run);
  }
  public MMouse clear() {
    mmain().inter.removeEventFrame(run);
    mmain().inter.removeEventHoverNotFound(hovernotfound_run);
    super.clear(); return this; }
}

class MBigTrig extends Macro_Bloc {
  Macro_Connexion out_t;
  nCtrlWidget trig; 
  nLinkedWidget stp_view; sBoo setup_send;
  MBigTrig(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "btrig", "btrig", _bloc); 
    setup_send = newBoo("stp_snd", "stp_snd", false);
    
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
  public MBigTrig clear() {
    super.clear(); return this; }
}
class MBigSwitch extends Macro_Bloc {
  Macro_Connexion in, out_t;
  nLinkedWidget swtch; 
  sBoo state;
  MBigSwitch(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "bswitch", "bswitch", _bloc); 
    
    state = newBoo("state", "state", false);
    
    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
      if (in.getLastPacket() != null && in.getLastPacket().isBang()) {
        swtch.setSwitchState(!swtch.isOn());
      } 
      if (in.getLastPacket() != null && in.getLastPacket().isBool()) {
        swtch.setSwitchState(in.getLastPacket().asBool());
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
  public MBigSwitch clear() {
    super.clear(); return this; }
}
