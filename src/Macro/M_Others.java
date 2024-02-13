package Macro;

import java.util.ArrayList;

import java.util.Map;

import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;

public class M_Others {}

class MPoint extends Macro_Bloc { 
  static class MPoint_Builder extends MAbstract_Builder {
	  MPoint_Builder() { super("point", "connection node"); }
      MPoint build(Macro_Sheet s, sValueBloc b) { MPoint m = new MPoint(s, b); return m; }
    }
  Macro_Connexion in, out; 
  MPoint(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "point", "connection node", _bloc); 
    in = addInputBang(0, "in", new nRunnable() {public void run() { 
    		if (in.lastPack() != null) out.send(in.lastPack()); }});
    out = addOutput(0, "out");
  }
  public MPoint clear() {
    super.clear(); 
    return this; }
  public MPoint toLayerTop() {
    super.toLayerTop(); 
    return this; }
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
      if (in.lastPack() != null && in.lastPack().isBang()) { load_prst(); }
    } });
    load_widg = addEmptyS(1).addCtrlModel("MC_Element_SButton")
      .setRunnable(new nRunnable() { public void run() { load_prst(); }});
  }
  void load_prst() {
    for (Map.Entry<String, sValueBloc> me : mmain().saved_preset.blocs.entrySet()) {
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





