package Macro;

import UI.nLinkedWidget;
import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sStr;
import sData.sValueBloc;

public class M_Inputs {}



class MKeyboard extends Macro_Bloc {
	  static class MKeyboard_Builder extends MAbstract_Builder {
		  MKeyboard_Builder() { super("keyb", "Keyboard", "", "Input"); }
		  MKeyboard build(Macro_Sheet s, sValueBloc b) { MKeyboard m = new MKeyboard(s, b); return m; }
	    }
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
      if (in_m.lastPack() != null && in_m.lastPack().isFloat() && 
          in_m.lastPack().asFloat() != val_mag.get()) {
        val_mag.set(in_m.lastPack().asFloat());
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




class MMouse extends Macro_Bloc { 
	  static class MMouse_Builder extends MAbstract_Builder {
		  MMouse_Builder() { super("mouse", "Mouse", "", "Input"); }
		  MMouse build(Macro_Sheet s, sValueBloc b) { MMouse m = new MMouse(s, b); return m; }
	    }
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