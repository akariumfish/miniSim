package Macro;

import UI.*;
import processing.core.*;
import sData.*;

public class M_Control {}


class MInput extends MBasic {
	static class Builder extends MAbstract_Builder {
		Builder() { super("input", "Input", "", "Control"); }
		MInput build(Macro_Sheet s, sValueBloc b) { MInput m = new MInput(s, b); return m; }
	}
  	sBoo modeKEY, modeCROSS, modeMOUSE, modeMKEY;
  	
  	Macro_Connexion out_key_t;
  	nLinkedWidget key_field, ctrl_swt; 
  	sStr val_cible; 
  	sBoo val_swtch, val_trig, ctrl_filter;
  	boolean swt_state = false;
  	
    Macro_Connexion in_m, out_d, out_t;
    PVector dir = new PVector();
    PVector pdir = new PVector();
    sFlt val_mag;
    nLinkedWidget mag_field;
    
    Macro_Connexion outp, outt, outm, outs;
    nRunnable frame_run, hovernotfound_run;
    PVector m, pm, mm, v;
    boolean st = false;
    
	MInput(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "input", _bloc); }
	public void init() {
		modeKEY = newBoo("modeKEY", true);
		modeCROSS = newBoo("modeCROSS", false);
		modeMOUSE = newBoo("modeMOUSE", false);
		modeMKEY = newBoo("modeMKEY", false);
	    
	    val_cible = newStr("cible", "cible", "");
	    val_swtch = newBoo("v1", false);
	    val_trig = newBoo("v2", true);
	    ctrl_filter = newBoo("ctrl_filter", false);

	    val_mag = newFlt("val_mag", "mag", 1);
	}
	boolean inib = false;
	public void build_param() { 
	    build_normal();
	    
		addEmptyS(1); //if (modeCROSS.get()) addEmptyS(2);
		nDrawer m = addEmptyL(0);
	    m.addLinkedModel("MC_Element_Button_Selector_2", "K").setLinkedValue(modeKEY);
	    m.addLinkedModel("MC_Element_Button_Selector_1", "DC").setLinkedValue(modeCROSS);
	    m.addLinkedModel("MC_Element_Button_Selector_3", "M").setLinkedValue(modeMOUSE);
	    m.addLinkedModel("MC_Element_Button_Selector_4", "MB").setLinkedValue(modeMKEY);
	    nRunnable mode_run = new nRunnable() { public void run() {
  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
  				if (!rebuilding) rebuild(); }});
	  	} };
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  	modeKEY.addEventChange(mode_run);
		  	modeCROSS.addEventChange(mode_run);
		  	modeMOUSE.addEventChange(mode_run);
		  	modeMKEY.addEventChange(mode_run);
	  	}});
	}
	public void build_normal() {
		if (!param_view.get() && !modeKEY.get() && !modeCROSS.get() && 
				!modeMOUSE.get() && !modeMKEY.get()) {
			param_view.set(true); rebuild(); }
		if (modeCROSS.get()) {
		    in_m = addInput(0, "magnitude").addEventReceive(new nRunnable() { public void run() { 
		    		if (in_m.lastPack() != null && in_m.lastPack().isFloat() && 
		    				in_m.lastPack().asFloat() != val_mag.get()) {
		    			val_mag.set(in_m.lastPack().asFloat());
		    		} 
		    } });
		    mag_field = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_mag);
		    
		    out_t = addOutput(1, "trig")
		    		.setDefBang();
		    out_d = addOutput(1, "direction vec")
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
		if (modeKEY.get()) {
		    key_field = addEmptyS(0).addLinkedModel("MC_Element_SField")
		    		.setLinkedValue(val_cible);
		    ctrl_swt = key_field.getDrawer().addLinkedModel("MC_Element_MiniButton")
		    		.setLinkedValue(ctrl_filter);
		    ctrl_swt.setInfo("desactive send when macros are hided");
		    addSelectS_Excl(0, val_swtch, val_trig, "Swtch", "Trig");
		    out_key_t = addOutput(1, "out")
		    		.setDefBang();
		    addEmptyS(1);
		    key_field.addEventFrame(new nRunnable() { public void run() {
		    		if ( val_swtch.get() && key_field.getText().length() > 0 && 
		    			mmain().inter.input.getState(key_field.getText().charAt(0)) && 
		    			!gui.field_used && 
		    			!mmain().screen_gui.field_used && 
		    			(!ctrl_filter.get() || mmain().show_macro.get())) {
		    			if (!swt_state) out_key_t.send(Macro_Packet.newPacketBool(true));
		    			swt_state = true; }
		    		else if (val_swtch.get() && key_field.getText().length() > 0) {
		    			if (swt_state) out_key_t.send(Macro_Packet.newPacketBool(false));
		    			swt_state = false; }
		    		if (val_trig.get() && !inib && mmain().inter.input.keyAll.trigClick && 
		    				key_field.getText().length() > 0 && 
		    				key_field.getText().charAt(0) == mmain().inter.input.getLastKey() && 
		    				!gui.field_used && 
		    				!mmain().screen_gui.field_used  && 
		    				(!ctrl_filter.get() || mmain().show_macro.get())) {
		    			out_key_t.send(Macro_Packet.newPacketBang());
		    			inib = true;
		    		}
		    		if ( val_trig.get() && inib && mmain().inter.input.keyAll.trigUClick && 
		    				key_field.getText().length() > 0 && 
		    				key_field.getText().charAt(0) == mmain().inter.input.getLastKey()) {
		    			inib = false;
		    		}
	    		} } );
		} 
		if (modeMOUSE.get()) {
			addLabelS(0, "mouse pos");
			addLabelS(0, "mouse move");
			outp = addOutput(1, "pos"); outm = addOutput(1, "mouv");
		    m = new PVector(0, 0); pm = new PVector(0, 0); mm = new PVector(0, 0); v = new PVector(0, 0);
		    frame_run = new nRunnable() { public void run() { 
		      if (m.x != gui.mouseVector.x || m.y != gui.mouseVector.y) { 
		        outp.send(Macro_Packet.newPacketVec(gui.mouseVector));
		        m.set(gui.mouseVector); }
//		      if (pm.x != gui.pmouseVector.x || pm.y != gui.pmouseVector.y) { 
//		        //out2.send(Macro_Packet.newPacketVec(gui.pmouseVector));
//		        pm.set(gui.pmouseVector); }
		      v.set(gui.mouseVector);
		      v = v.sub(gui.pmouseVector);
		      if (mm.x != v.x || mm.y != v.y) { 
		        outm.send(Macro_Packet.newPacketVec(v));
		        mm.set(v); }
		    } };
		    mmain().inter.addEventFrame(frame_run);
		} 
		if (modeMKEY.get()) {
			addLabelS(0, "mouseL trig");
			addLabelS(0, "mouseL start");
		    outt = addOutput(1, "trig"); outs = addOutput(1, "state"); 
		    hovernotfound_run = new nRunnable() { public void run() { 
		    		if (mmain().inter.input.mouseLeft.trigClick) {
		    	  		outt.send(Macro_Packet.newPacketBang());
		      	}
		      	boolean ms = mmain().inter.input.mouseLeft.state;
		      	if (st != ms) {
		    	  		st = ms;
		        		outs.send(Macro_Packet.newPacketBool(st));
		      	}
		    } };
		    mmain().inter.addEventHoverNotFound(hovernotfound_run);
		}
	}
	public MInput clear() {
	    if (frame_run != null) mmain().inter.removeEventFrame(frame_run);
	    if (hovernotfound_run != null) mmain().inter.removeEventHoverNotFound(hovernotfound_run);
		super.clear(); return this; }
} 


class MGate extends MBasic {
  static class MGate_Builder extends MAbstract_Builder {
	  MGate_Builder(Macro_Main m) { super("gate", "Gate", "flow control", "Control"); 
	  	  first_start_show(m); }
	  MGate build(Macro_Sheet s, sValueBloc b) { MGate m = new MGate(s, b); return m; }
  }
  Macro_Connexion in_m, in_b, out, out_inv;
  nLinkedWidget swtch; 
  sBoo state; sStr label;
  MGate(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "gate", _bloc); }
  public void init() {
    state = newBoo("state", "state", false);
    label = newStr("label", "label");
  }
  public void build_param() {
	build_normal();
	addEmpty(1);
  	addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(label);
  	label.addEventChange(new nRunnable() { public void run() { 
 		if (swtch != null) swtch.setText(label.get());
 	} } );
  }
  public void build_normal() {
    in_m = addInput(0, "in")
    .addEventReceive(new nRunnable() { public void run() { 
//      if (in_m.lastPack() != null)
//    	  	if (state.get()) out.send(in_m.lastPack());
//    	  	else out_inv.send(in_m.lastPack());
    } });
    in_b = addInput(0, "gate").addEventReceive(new nRunnable() { public void run() { 
      if (in_b.lastPack() != null && in_b.lastPack().isBool()) 
        state.set(in_b.lastPack().asBool()); 
      if (in_b.lastPack() != null && in_b.lastPack().isBang()) 
        state.set(!state.get()); 
    } });
    out = addOutput(1, "out");
    out_inv = addOutput(1, "inv");

    in_m.set_undefine();
  	out_inv.set_undefine();
  	out.set_undefine();
    swtch = in_b.elem.addLinkedModel("MC_Element_SButton", label.get())
    		.setLinkedValue(state);
    swtch.setPX(ref_size*0.5);
    state.addEventChange(new nRunnable() { public void run() { 
		if (state.get()) { 
//			in_m.direct_deconnect(out); 
			in_m.direct_deconnect(out_inv); 
			in_m.direct_connect(out); 
		} else { 
			in_m.direct_deconnect(out); 
//			in_m.direct_deconnect(out_inv); 
			in_m.direct_connect(out_inv); 
		}
    }});
  }
  public Macro_Connexion get_active_out() { 
	  if (state.get()) return out; else return out_inv; }
  public MGate clear() {
    super.clear(); return this; }
}




class MChan extends MBasic {
	  static class MChan_Builder extends MAbstract_Builder {
		  MChan_Builder() { super("chan", "Channel", "", "Control"); }
		  MChan build(Macro_Sheet s, sValueBloc b) { MChan m = new MChan(s, b); return m; }
	  }

	  Macro_Connexion in_chan, in, out;
	  sStr val_cible; 
	  nLinkedWidget ref_field; 
	
	MChan(Macro_Sheet _sheet, sValueBloc _bloc) { 
		  super(_sheet, "chan", _bloc); }
	void init() { 
		super.init(); 

	    val_cible = newStr("cible", "cible", "");
	    addEmpty(1); 
	    in_chan = addInput(0, "chan").addEventReceive(new nRunnable() { public void run() { 
	        if (in.lastPack() != null && in.lastPack().isStr()) val_cible.set(in.lastPack().asStr());
	      } });
//	    in_chan.elem.back.copy(gui.theme.getModel("MC_Element_Double"));
	    ref_field = in_chan.elem.addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
	    in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
	      if (in.lastPack() != null) receive(in.lastPack());
	    } });
	    out = addOutput(1, "out");
	    
	    mmain().chan_macros.add(this);
	}
	void init_end() { 
		  super.init_end(); }
	void buil_param() { }
	void buil_normal() { }

	  void receive(Macro_Packet p) {
	    out.send(p);
	    for (MChan m : mmain().chan_macros) 
	      if (m != this && m.val_cible.get().equals(val_cible.get())) m.out.send(p);
	  }
	public MChan clear() {
	  super.clear(); 
	    mmain().chan_macros.remove(this); return this; }
	public MChan toLayerTop() {
		    super.toLayerTop(); 
		    return this; }
}





class MBin extends MBasic {

	  static class MBin_Builder extends MAbstract_Builder {
		  MBin_Builder() { super("bin", "Binary", "", "Control"); }
		  MBin build(Macro_Sheet s, sValueBloc b) { MBin m = new MBin(s, b); return m; }
	  }
	  
  Macro_Connexion in, out;
  nLinkedWidget swtch; 
  sBoo state, val_not;
  MBin(Macro_Sheet _sheet, sValueBloc _bloc) { 
	  super(_sheet, "bin", _bloc); 
	  state = newBoo("state", "state", false); 
	  val_not = newBoo("val_not", "val_not", false); 
      out = addOutput(1, "out").setDefBool();
      swtch = out.elem.addLinkedModel("MC_Element_SButton", "all").setLinkedValue(state);
      swtch.setInfo("all to bang");
	  in = addInput(0, "in", new nRunnable() { public void run() { 
        if (in.lastPack() != null && in.lastPack().isBool() && 
            in.lastPack().asBool()) out.send(Macro_Packet.newPacketBang()); 
        else if (in.lastPack() != null && in.lastPack().isBang()) 
	    		if (!val_not.get()) out.send(Macro_Packet.newPacketBool(true));
	    		else out.send(Macro_Packet.newPacketBool(false));
        else if (state.get() && in.lastPack() != null) 
        		out.send(Macro_Packet.newPacketBang());  } });
	  in.elem.addLinkedModel("MC_Element_SButton", "!")
			  .setLinkedValue(val_not).setInfo("bool not");
  }
  void init() { 
	  super.init(); }
  void init_end() { 
	  super.init_end(); }
  void buil_param() { }
  void buil_normal() { }
  public MBin clear() {
    super.clear(); return this; }
  public MBin toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
}

class MNot extends MBasic {

  static class MNot_Builder extends MAbstract_Builder {
	  MNot_Builder() { super("not", "Not", "", "Control"); }
	  MNot build(Macro_Sheet s, sValueBloc b) { MNot m = new MNot(s, b); return m; }
  }
	  
  Macro_Connexion in, out;
  MNot(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "not", _bloc); 
    
    in = addInput(0, "in").setFilterBool().addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool()) {
        if (in.lastPack().asBool()) out.send(Macro_Packet.newPacketBool(false)); 
        else out.send(Macro_Packet.newPacketBool(true)); } } });
    out = addOutput(1, "out")
      .setDefBool();
  }
  void init() { 
	  super.init(); }
  void init_end() { 
	  super.init_end(); }
  void buil_param() { }
  void buil_normal() { }
  public MNot toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
  public MNot clear() {
    super.clear(); return this; }
}





class MCounter extends MBasic { 
	static class MCount_Builder extends MAbstract_Builder {
	  	MCount_Builder() { super("count", "Counter", "", "Control"); }
	  	MCounter build(Macro_Sheet s, sValueBloc b) { MCounter m = new MCounter(s, b); return m; }
    }
  	sInt count_val, max_val;
  	sBoo lim_max;
  	Macro_Connexion out, end;
  	nRunnable get_run, rst_run, add_run, sub_run;
	boolean build_flag = false;
  	MCounter(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "count", _bloc); }
	void init() {
		super.init();
	    count_val = newInt("count_val", "count_val", 0);
	    lim_max = newBoo("lim_max", false);
	    max_val = newInt(10, "max_val");
	}
	void init_end() { 
		super.init_end(); 

	  	nRunnable mode_run = new nRunnable() { public void run() {
	  		if (!build_flag)
	  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  				if (!rebuilding) rebuild(); }});
	  		build_flag = true;
	  	} };
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  		lim_max.addEventChange(mode_run);
	  	}});
	}
	void build_param() { 
	    addEmpty(1);
	    nDrawer dr = addEmptyL(0);
	    dr.addLinkedModel("MC_Element_Button", "limit").setLinkedValue(lim_max);
	    
		build_normal(); 
	}
	void build_normal() {
	    addEmptyS(0).addWatcherModel("MC_Element_SText").setLinkedValue(count_val);
	    if (!lim_max.get()) addEmptyS(0);
	    else addInputToValue(0, max_val)
	    				.elem.addLinkedModel("MC_Element_SField").setLinkedValue(max_val);
	    out = addOutput(1, "count_out");
	    end = addOutput(1, "end");
	    get_run = new nRunnable() {public void run() { 
			out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }};
		rst_run = new nRunnable() {public void run() { 
			count_val.set(0); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); 
			end.sendBang();
		}};
		add_run = new nRunnable() {public void run() { 
			count_val.add(1); 
			if (lim_max.get() && count_val.get() >= max_val.get()) {
				rst_run.run();
			} else {
				out.send(Macro_Packet.newPacketFloat((float)count_val.get())); 
			}
		}};
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





class MSetReset extends MBasic {
	static class Builder extends MAbstract_Builder {
		Builder() { super("strst", "SetReset", "", "Control"); }
		MSetReset build(Macro_Sheet s, sValueBloc b) { MSetReset m = new MSetReset(s, b); return m; }
	}
	Macro_Connexion out_state, out_over;
	sBoo state;
	MSetReset(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "strst", _bloc);  }
	void init() {
		super.init();
		state = newBoo("state", "state", false);
	}
	void init_end() { super.init_end(); }
	void build_param() { build_normal(); }
	void build_normal() {
		out_state = addOutput(1, "state");
		out_state.elem.addWatcherModel("MC_Element_SText")
		.setLinkedValue(state).setBackground();
		out_over = addOutput(1, "over");
		nRunnable set_run = new nRunnable() {public void run() { 
			boolean flag = false;
			if (state.get()) flag = true;
			state.set(true);
			out_state.sendBool(true);
			if (flag) { out_over.sendBang(); }
		}};
		addInputBang(0, "set", set_run)
			.elem.addCtrlModel("MC_Element_SButton", "set").setRunnable(set_run);
		nRunnable rst_run = new nRunnable() {public void run() { 
			boolean flag = false;
			if (!state.get()) flag = true;
			state.set(false);
			out_state.sendBool(false);
			if (flag) { out_over.sendBang(); }
		}};
		addInputBang(0, "reset", rst_run)
			.elem.addCtrlModel("MC_Element_SButton", "reset").setRunnable(rst_run);
	}
	public MSetReset clear() {
		super.clear(); return this; }
}

//
//class MMouse extends Macro_Bloc { 
//	  static class MMouse_Builder extends MAbstract_Builder {
//		  MMouse_Builder() { super("mouse", "Mouse", "", "Input"); }
//		  MMouse build(Macro_Sheet s, sValueBloc b) { MMouse m = new MMouse(s, b); return m; }
//	    }
//	  Macro_Connexion outp, outt, outm, outs;
//	  nRunnable run, hovernotfound_run;
//	  PVector m, pm, mm, v;
//	  boolean st = false;
//	  MMouse(Macro_Sheet _sheet, sValueBloc _bloc) { 
//	    super(_sheet, "mouse", "mouse", _bloc); 
//	    outp = addOutput(0, "pos"); outm = addOutput(0, "mouv");
//	    outt = addOutput(0, "trig"); outs = addOutput(0, "state"); 
//	    m = new PVector(0, 0); pm = new PVector(0, 0); mm = new PVector(0, 0); v = new PVector(0, 0);
//	    run = new nRunnable() { public void run() { 
//	      if (m.x != gui.mouseVector.x || m.y != gui.mouseVector.y) { 
//	        outp.send(Macro_Packet.newPacketVec(gui.mouseVector));
//	        m.set(gui.mouseVector); }
//	      if (pm.x != gui.pmouseVector.x || pm.y != gui.pmouseVector.y) { 
//	        //out2.send(Macro_Packet.newPacketVec(gui.pmouseVector));
//	        pm.set(gui.pmouseVector); }
//	      v.set(gui.mouseVector);
//	      v = v.sub(gui.pmouseVector);
//	      if (mm.x != v.x || mm.y != v.y) { 
//	        outm.send(Macro_Packet.newPacketVec(v));
//	        mm.set(v); }
//	    } };
//	    hovernotfound_run = new nRunnable() { public void run() { 
//	      if (mmain().inter.input.mouseLeft.trigClick) {
//	        outt.send(Macro_Packet.newPacketBang());
//	      }
//	      boolean ms = mmain().inter.input.mouseLeft.state;
//	      if (st != ms) {
//	        st = ms;
//	        outs.send(Macro_Packet.newPacketBool(st));
//	      }
//	    } };
//	    mmain().inter.addEventFrame(run);
//	    mmain().inter.addEventHoverNotFound(hovernotfound_run);
//	  }
//	  public MMouse clear() {
//	    mmain().inter.removeEventFrame(run);
//	    mmain().inter.removeEventHoverNotFound(hovernotfound_run);
//	    super.clear(); return this; }
//	}
//
//
//
//
//class MKeyboard extends Macro_Bloc {
//	  static class MKeyboard_Builder extends MAbstract_Builder {
//		  MKeyboard_Builder() { super("keyb", "Keyboard", "", "Input"); }
//		  MKeyboard build(Macro_Sheet s, sValueBloc b) { MKeyboard m = new MKeyboard(s, b); return m; }
//	    }
//Macro_Connexion out_t;
//nLinkedWidget key_field, ctrl_swt; 
//sStr val_cible; 
//sBoo val_swtch, val_trig, ctrl_filter;
//boolean swt_state = false;
//MKeyboard(Macro_Sheet _sheet, sValueBloc _bloc) { 
//  super(_sheet, "keyb", "keyb", _bloc); 
//  val_cible = newStr("cible", "cible", "");
//  init();
//}
//void init() {
//  key_field = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_cible);
//  
//  val_swtch = newBoo("v1", true);
//  val_trig = newBoo("v2", false);
//  ctrl_filter = newBoo("ctrl_filter", false);
//  
//  ctrl_swt = key_field.getDrawer().addLinkedModel("MC_Element_MiniButton").setLinkedValue(ctrl_filter);
//  ctrl_swt.setInfo("desactive send when macros are hided");
//  
//  addSelectS(0, val_swtch, val_trig, "Swtch", "Trig");
//  
//  out_t = addOutput(1, "out")
//    .setDefBang();
//  key_field.addEventFrame(new nRunnable() { public void run() {
//    if ( val_swtch.get() && key_field.getText().length() > 0 && 
//         mmain().inter.input.getState(key_field.getText().charAt(0)) && !gui.field_used && 
//         !mmain().screen_gui.field_used && 
//         (!ctrl_filter.get() || mmain().show_macro.get())) {
//      if (!swt_state) out_t.send(Macro_Packet.newPacketBool(true));
//      swt_state = true; }
//    else if (val_swtch.get() && key_field.getText().length() > 0) {
//      if (swt_state) out_t.send(Macro_Packet.newPacketBool(false));
//      swt_state = false; }
//    if (val_trig.get() && !inib && mmain().inter.input.keyAll.trigClick && 
//        key_field.getText().length() > 0 && 
//        key_field.getText().charAt(0) == mmain().inter.input.getLastKey() && !gui.field_used && 
//        !mmain().screen_gui.field_used  && 
//        (!ctrl_filter.get() || mmain().show_macro.get())) {
//      out_t.send(Macro_Packet.newPacketBang());
//      inib = true;
//    }
//    if ( val_trig.get() && inib && mmain().inter.input.keyAll.trigUClick && key_field.getText().length() > 0 && 
//        key_field.getText().charAt(0) == mmain().inter.input.getLastKey()) {
//      inib = false;
//    }
//  } } );
//}
//boolean inib = false;
//public MKeyboard clear() {
//  super.clear(); return this; }
//
//}
//
//
//
//
//
//class MCrossVec extends Macro_Bloc {
//Macro_Connexion in_m, out_d, out_t;
//PVector dir = new PVector();
//PVector pdir = new PVector();
//sFlt val_mag;
//nLinkedWidget mag_field;
//
//MCrossVec(Macro_Sheet _sheet, sValueBloc _bloc) { 
//  super(_sheet, "crossVec", "crossVec", _bloc); 
//  val_mag = newFlt("val_mag", "mag", 1);
//  mag_field = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mag);
//  
//  in_m = addInput(0, "magnitude").addEventReceive(new nRunnable() { public void run() { 
//    if (in_m.lastPack() != null && in_m.lastPack().isFloat() && 
//        in_m.lastPack().asFloat() != val_mag.get()) {
//      val_mag.set(in_m.lastPack().asFloat());
//    } 
//  } });
//  
//  out_t = addOutput(1, "trig")
//    .setDefBang();
//  out_d = addOutput(2, "direction vec")
//    .setDefVec();
//  gui.addEventFrame(new nRunnable() { public void run() {
//    //logln("key up");
//    dir.set(0, 0);
//    if (!gui.field_used && !mmain().screen_gui.field_used) {
//      if ( mmain().inter.input.getState('z') ) dir.y -=1;
//      if ( mmain().inter.input.getState('s') ) dir.y +=1;
//      if ( mmain().inter.input.getState('q') ) dir.x -=1;
//      if ( mmain().inter.input.getState('d') ) dir.x +=1;
//    }
//    if ( dir.magSq() > 0) {
//      dir.setMag(val_mag.get());
//      if (!pdir.equals(dir)) out_d.send(Macro_Packet.newPacketVec(dir));
//      pdir.set(dir);
//      out_t.send(Macro_Packet.newPacketBang());
//    } else if (!pdir.equals(dir)) {
//      out_d.send(Macro_Packet.newPacketVec(dir));
//      pdir.set(dir);
//    }
//  } } );
//}
//
//public MCrossVec clear() {
//  super.clear(); return this; }
//
//}