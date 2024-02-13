package Macro;


import java.util.ArrayList;

import RApplet.RConst;
import UI.*;
import sData.*;

public class M_Time {}


class MSequance extends MBasic { 
	  static class MSequencor_Builder extends MAbstract_Builder {
		  MSequencor_Builder() { super("sequ", "Sequance", "usefull for ordering executions", "Time"); }
		  MSequance build(Macro_Sheet s, sValueBloc b) { MSequance m = new MSequance(s, b); return m; }
	  }

	  ArrayList<sInt> ivals;
	  ArrayList<Macro_Connexion> connects;
	  sInt current_delay;	int counter = 0;
	  Macro_Connexion current_out;
	  int current_index;
	  sInt row_nb; sBoo actif_val;
	  MSequance(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "sequ", _bloc); }
	  void init() {
		  row_nb = newInt(5, "row_nb");
		  actif_val = newBoo(true, "actif_val");
		  ivals = new ArrayList<sInt>();
		  connects = new ArrayList<Macro_Connexion>();
	  }
	  void build_param() {
//		  addEmptyS(2);
		  addTrigS(2, "add row", new nRunnable() { public void run() { row_nb.add(1); rebuild(); }});
		  addEmptyS(1).addWatcherModel("MC_Element_SField").setLinkedValue(row_nb);
		  addTrigS(0, "del row", new nRunnable() { public void run() { 
			  if (row_nb.get() > 2) row_nb.add(-1); rebuild(); }});
		  build_normal();
	  }
	  
	  void build_normal() {

		  addEmptyS(0).addLinkedModel("MC_Element_SButton").setLinkedValue(actif_val);
		  addInputToValue(0, actif_val);
		  
		  addInputBang(0, "tick", new nRunnable() { public void run() { 
			  if (actif_val.get()) {
				  counter++;
				  if (counter >= current_delay.get()) {
					  current_out.sendBang();
					  current_index++;
					  if (current_index >= row_nb.get()) current_index = 0;
					  current_delay = ivals.get(current_index);
					  current_out = connects.get(current_index);
					  counter = 0;
				  }
			  }
		  }});

		  addInputBang(0, "reset", new nRunnable() { public void run() { 
			  if (ivals.size() > 0) current_delay = ivals.get(0);
			  current_index = 0;
			  if (connects.size() > 0) current_out = connects.get(0);
			  counter = 0;
		  }});
		  
		  for (int i = 0 ; i < row_nb.get() ; i++) {
			  sInt v = newInt(100, "delay"+i, "delay"+i);
			  if (!ivals.contains(v)) ivals.add(v);
			  addEmptyS(1).addLinkedModel("MC_Element_SField")
					  .setLinkedValue(v);
			  Macro_Connexion out = addOutput(2, "out"+i);
			  connects.add(out);
			  if (current_delay == null || current_out == null) {
				  current_delay = v;
				  current_out = out;
				  current_index = i;
				  counter = 0;
			  }
		  }
	  }
	  public MSequance clear() {
	    super.clear(); 
	    return this; }
	  public MSequance toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
	}




class MFrame extends Macro_Bloc { 
	  static class MFrame_Builder extends MAbstract_Builder {
		  MFrame_Builder() { super("frame", "Frame", "", "Time"); }
		  MFrame build(Macro_Sheet s, sValueBloc b) { MFrame m = new MFrame(s, b); return m; }
	  }
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
	  static class MPulse_Builder extends MAbstract_Builder {
		  MPulse_Builder() { super("pulse", "Pulse", "", "Time"); }
		  MPulse build(Macro_Sheet s, sValueBloc b) { MPulse m = new MPulse(s, b); return m; }
	  }
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






class MRamp extends Macro_Bloc { 
	  static class MRamp_Builder extends MAbstract_Builder {
		  MRamp_Builder() { super("ramp", "Rampe", "", "Time"); }
		  MRamp build(Macro_Sheet s, sValueBloc b) { MRamp m = new MRamp(s, b); return m; }
	  }
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
      if (in_tick.lastPack() != null && in_tick.lastPack().isBang()) { got_tick(); } 
      if (in_tick.lastPack() != null && in_tick.lastPack().isFloat()) 
        val_period.set((int)(in_tick.lastPack().asFloat()));
      if (in_tick.lastPack() != null && in_tick.lastPack().isInt()) 
        val_period.set(in_tick.lastPack().asInt()); } });
    in_start = addInput(0, "start / phi").addEventReceive(new nRunnable() { public void run() { 
      if (in_start.lastPack() != null && in_start.lastPack().isBang()) { got_start(); } 
      if (in_start.lastPack() != null && in_start.lastPack().isFloat()) 
        val_phi.set((int)(in_start.lastPack().asFloat()));
      if (in_start.lastPack() != null && in_start.lastPack().isInt()) 
        val_phi.set(in_start.lastPack().asInt()); } });
    in_stop = addInput(0, "stop / end").addEventReceive(new nRunnable() { public void run() { 
      if (in_stop.lastPack() != null && in_stop.lastPack().isBang()) { got_stop(); } 
      if (in_stop.lastPack() != null && in_stop.lastPack().isFloat()) 
        val_end.set(in_stop.lastPack().asFloat());
      if (in_stop.lastPack() != null && in_stop.lastPack().isInt()) 
        val_end.set(in_stop.lastPack().asInt()); } });
    
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



