package Macro;

import UI.nLinkedWidget;
import UI.nWatcherWidget;
import sData.nRunnable;
import sData.sBoo;
import sData.sInt;
import sData.sStr;
import sData.sValue;
import sData.sValueBloc;

public class M_Control {}


class MGate extends Macro_Bloc {

  static class MGate_Builder extends MAbstract_Builder {
	  MGate_Builder() { super("gate", "Gate", "flow control", "Control"); }
	  MGate build(Macro_Sheet s, sValueBloc b) { MGate m = new MGate(s, b); return m; }
  }
  
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





class MChan extends Macro_Bloc { 

	  static class MChan_Builder extends MAbstract_Builder {
		  MChan_Builder() { super("chan", "Chan", "", "Control"); }
		  MChan build(Macro_Sheet s, sValueBloc b) { MChan m = new MChan(s, b); return m; }
	  }
	  
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





class MBin extends Macro_Bloc {

	  static class MBin_Builder extends MAbstract_Builder {
		  MBin_Builder() { super("bin", "Binary", "", "Control"); }
		  MBin build(Macro_Sheet s, sValueBloc b) { MBin m = new MBin(s, b); return m; }
	  }
	  
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

	  static class MNot_Builder extends MAbstract_Builder {
		  MNot_Builder() { super("not", "Not", "", "Control"); }
		  MNot build(Macro_Sheet s, sValueBloc b) { MNot m = new MNot(s, b); return m; }
	  }
	  
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





class MCounter extends Macro_Bloc { 
  static class MCount_Builder extends MAbstract_Builder {
	  MCount_Builder() { super("count", "Counter", "", "Control"); }
      MCounter build(Macro_Sheet s, sValueBloc b) { MCounter m = new MCounter(s, b); return m; }
    }
  sInt count_val, max_val;
  Macro_Connexion out;
  nRunnable get_run, rst_run, add_run, sub_run;
  MCounter(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "count", "count", _bloc); 

//    max_val = newRowInt(0, "max_val");
    
    count_val = newInt("count_val", "count_val", 0);
    addEmptyS(0).addWatcherModel("MC_Element_SText").setLinkedValue(count_val);
    out = addOutput(1, "out");
    get_run = new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }};
	rst_run = new nRunnable() {public void run() { 
		count_val.set(0); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }};
	add_run = new nRunnable() {public void run() { 
		count_val.add(1); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }};
	sub_run = new nRunnable() {public void run() { 
		count_val.add(-1); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }};
    addInputBang(0, "get", get_run).hide_msg().elem.addCtrlModel("MC_Element_SButton", "get")
    	.setRunnable(get_run);
    addInputBang(0, "rst", rst_run).hide_msg().elem.addCtrlModel("MC_Element_SButton", "rst")
		.setRunnable(rst_run);
    addInputBang(1, "add", add_run).hide_msg().elem.addCtrlModel("MC_Element_SButton", "add")
		.setRunnable(add_run);
    addInputBang(1, "sub", sub_run).hide_msg().elem.addCtrlModel("MC_Element_SButton", "sub")
		.setRunnable(sub_run);
    
  }
  public MCounter clear() {
    super.clear(); 
    return this; }
  public MCounter toLayerTop() {
    super.toLayerTop(); 
    return this; }
}

