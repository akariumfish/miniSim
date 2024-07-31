package Macro;


import java.util.ArrayList;

import RApplet.RConst;
import UI.*;
import processing.core.PVector;
import sData.*;

public class M_Time {}


class MSequance extends MBaseTick { 
	  static class Builder extends MAbstract_Builder {
		  Builder() { super("sequ", "Sequance", "usefull for ordering executions", "Control"); }
		  MSequance build(Macro_Sheet s, sValueBloc b) { MSequance m = new MSequance(s, b); return m; }
	  }
	
	  boolean build_flag = false;
	  ArrayList<sInt> ivals;
	  ArrayList<Macro_Connexion> connects;
	  sInt current_delay;	int counter = 0;
	  Macro_Connexion current_out;
	  int current_index;
	  sInt row_nb; 
	  sBoo actif_val, show_actif, show_delay, show_row;
	  sBoo show_tkrs, show_sequ, show_loop, val_loop, send_delayed;
	  MSequance(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "sequ", _bloc); }
	  void init() {
		  super.init();
		  row_nb = newInt(4, "row_nb");
		  actif_val = newBoo(true, "actif_val");
		  show_actif = newBoo(false, "show_actif");
		  show_delay = newBoo(true, "show_delay");
		  show_row = newBoo(true, "show_row");
		  show_tkrs = newBoo(false, "show_tkrs");
		  show_sequ = newBoo(true, "show_sequ");
		  show_loop = newBoo(false, "show_loop");
		  val_loop = newBoo(false, "val_loop");
		  send_delayed = newBoo(false, "send_delayed");
		  ivals = new ArrayList<sInt>();
		  connects = new ArrayList<Macro_Connexion>();
	  }
	  void init_end() {
		  super.init_end();
		  get_reset();
	  }
	  void build_param() {
		  addEmptyS(1); addEmptyS(2);
		  Macro_Element e = addEmptyXL(0);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(show_actif).setText("actif")
		  	.setSX(ref_size*2.0).setPX(ref_size*0.125);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(show_delay).setText("delay")
		  	.setSX(ref_size*2.0).setPX(ref_size*2.375);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(show_row).setText("row")
		  	.setSX(ref_size*2.0).setPX(ref_size*4.625);

		  addEmptyS(1); addEmptyS(2);
		  e = addEmptyXL(0);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(show_tkrs).setText("tck/rst")
		  	.setSX(ref_size*2.0).setPX(ref_size*0.125);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(show_sequ).setText("sequance")
		  	.setSX(ref_size*2.0).setPX(ref_size*2.375);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(show_loop).setText("loop")
		  	.setSX(ref_size*2.0).setPX(ref_size*4.625);
		  addEmptyS(1); addEmptyS(2);
		  e = addEmptyXL(0);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(val_loop).setText("loop")
		  	.setSX(ref_size*2.0).setPX(ref_size*0.125);
		  e.addLinkedModel("MC_Element_Button")
		  	.setLinkedValue(send_delayed).setText("send_delayed")
		  	.setSX(ref_size*2.0).setPX(ref_size*2.375);
		  
		  // build loop solution
		  	nRunnable mode_run = new nRunnable() { public void run() {
		  		if (!build_flag)
		  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  				if (!rebuilding) rebuild(); }});
		  		build_flag = true;
		  	} };
		  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
				  show_actif.addEventChange(mode_run);
				  show_delay.addEventChange(mode_run);
				  show_row.addEventChange(mode_run);
				  show_tkrs.addEventChange(mode_run);
				  show_sequ.addEventChange(mode_run);
				  show_loop.addEventChange(mode_run);
				  show_loop.addEventChange(new nRunnable() { public void run() {
					  if (!show_loop.get()) val_loop.set(false);
				  }});
		  	}});
		  build_normal();
	  }
	  void build_normal() {
		  if (show_actif.get()) {
			  addEmptyS(1).addLinkedModel("MC_Element_SButton", "ON").setLinkedValue(actif_val);
		  	  addInputToValue(0, actif_val);
		  }
		  if (show_loop.get()) {
			  addEmptyS(1).addLinkedModel("MC_Element_SButton", "LOOP")
			  	  .setLinkedValue(val_loop);
		  	  addInputToValue(0, val_loop);
		  	  val_loop.addEventChange(new nRunnable() { public void run() { 
		  		  if (val_loop.get()) get_sequance(); }});
		  }
		  if (show_row.get() || param_view.get()) {
			  addEmptyS(1).addWatcherModel("MC_Element_SField").setLinkedValue(row_nb)
			  .setPX(-ref_size*3 / 16).setSX(ref_size*1.375)
			  .getDrawer().addCtrlModel("MC_Element_SButton", "+")
		  		.setRunnable(new nRunnable() { public void run() { 
		  			row_nb.add(1); rebuild(); }})
		  		.setPX(ref_size*1.1250).setSX(ref_size*1.0);
			  addInputToValue(0, row_nb)
			  	.elem.addCtrlModel("MC_Element_SButton", "-")
			  		.setRunnable(new nRunnable() { public void run() { 
			  			if (row_nb.get() > 1) row_nb.add(-1); rebuild(); }})
			  		.setPX(ref_size*1.1250).setSX(ref_size*1.0);
		  }
		  if (show_tkrs.get() || param_view.get()) {
			  nRunnable tick_run = new nRunnable() { public void run() { get_tick(); }};
			  addInputBang(0, "tick", tick_run).hide_msg()
			  	  .elem.addCtrlModel("MC_Element_SButton", "tick")
			      .setRunnable(tick_run);
			  nRunnable rst_run = new nRunnable() { public void run() { get_reset(); }};
			  addInputBang(0, "reset", rst_run).hide_msg()
		  	  .elem.addCtrlModel("MC_Element_SButton", "reset").setRunnable(rst_run);
		  }
		  if (show_sequ.get() || param_view.get()) {
			  nRunnable sequ_run = new nRunnable() { public void run() { get_sequance(); }};
			  addInputBang(0, "sequance", sequ_run).hide_msg()
		  	  	  .elem.addCtrlModel("MC_Element_SButton", "sequance").setRunnable(sequ_run);
		  }
		  int out_col = 1;
		  if (	show_sequ.get() && !show_tkrs.get() && 
				!show_actif.get() && !show_row.get() &&
				!param_view.get() && row_nb.get() > 1) out_col = 0;
		  for (int i = 0 ; i < row_nb.get() ; i++) {
			  sInt v = newInt(0, "delay"+i, "delay"+i);
			  ivals.add(v);
			  if (show_delay.get()) {
				  Macro_Connexion out = addOutput(out_col, "out"+i);
				  connects.add(out);
				  out.elem.addLinkedModel("MC_Element_SField")
					  .setLinkedValue(v)
					  .setPX(ref_size*7.0/16.0).getDrawer()
				  .addCtrlModel("MC_Element_MiniButton", "0")
					  .setRunnable(new nRunnable(v) { public void run() { 
						  ((sInt)builder).set(0);
					  }}).setPX(ref_size*0.125).getDrawer()
				  .addCtrlModel("MC_Element_MiniButton", "+")
					  .setRunnable(new nRunnable(v) { public void run() { 
						  ((sInt)builder).add(1);
					  }}).setPX(ref_size*1.75);
			  } else {
				  Macro_Connexion out = addOutput(out_col, "out"+i);
				  connects.add(out);
				  out.elem.addWatcherModel("MC_Element_SField")
					  .setLinkedValue(v);
			  }
		  }
		  get_reset();
	  }
	  int tick_pile = 0;
	  void tick() {
		  super.tick();
		  if (tick_pile > 0) {
			  tick_pile--;
			  get_tick();
		  }
	  }
	  void get_sequance() {
		  tick_pile++;
		  for (sInt i : ivals) tick_pile += i.get();
	  }
	  void get_reset() {
		  if (ivals.size() > 0) current_delay = ivals.get(0);
		  current_index = 0;
		  if (connects.size() > 0) current_out = connects.get(0);
		  counter = 0;
	  }
	  boolean tick_row() {
		  if (counter > current_delay.get()) {
			  counter -= current_delay.get();
			  current_out.sendBang();
			  current_index++;
			  if (current_index >= row_nb.get()) {
				  current_index = 0;
				  counter--;
				  current_delay = ivals.get(current_index);
				  current_out = connects.get(current_index);
				  if (val_loop.get()) get_sequance();
				  return false;
			  } else if (current_index < row_nb.get()) { 
				  current_delay = ivals.get(current_index);
				  current_out = connects.get(current_index);
				  
				  return true; }
		  } else if (send_delayed.get()) {
			  current_out.sendBang();
		  }
		  return false; 
	  }
	  void get_tick() {
		  if (actif_val.get()) {
			  counter++;
			  boolean run = true;
			  while (run) { run = tick_row() && run; }
		  }
	  }
	  public MSequance clear() {
	    super.clear(); 
	    return this; }
	  public MSequance toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
	}




class MVecLerp extends MBaseTick {
	static class Builder extends MAbstract_Builder {
		Builder() { super("vecLerp", "VectorLerp", "", "Data"); }
		MVecLerp build(Macro_Sheet s, sValueBloc b) { MVecLerp m = new MVecLerp(s, b); return m; }
	}
    boolean build_flag = false;
	Macro_Connexion in_tick, in_start, in_stop, in_reset, out_val, out_end;
  	sBoo valONE, valRPT, valINV, valLOOP;
  	sBoo valLIN, valSIN;
  	sVec val_strt, val_end;
  	sInt val_period, val_phi;
  	sBoo show_period, show_phi, show_strt, show_end;
  	sBoo ext_tick, show_strstp;
  	nRunnable reset_run;
	MVecLerp(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "vecLerp", _bloc); }
	public void init() {
  		super.init();
	    valONE = newBoo("valONE", "valONE", true);
	    valRPT = newBoo("valRPT", "valRPT", false);
	    valINV = newBoo("valINV", "valINV", false);
	    valLOOP = newBoo("valLOOP", "valLOOP", false);
	    valLIN = newBoo("valLIN", "valLIN", true);
	    valSIN = newBoo("valSIN", "valSIN", false);
	    show_period = newBoo("show_period", true);
	    show_phi = newBoo("show_phi", false);
	    show_strt = newBoo("show_strt", true);
	    show_end = newBoo("show_end", true);
	    ext_tick = newBoo("ext_tixk", false);
	    show_strstp = newBoo("show_strstp", true);
	    val_period = newInt("val_period", "period", 10);
	    val_phi = newInt("val_phi", "phi", 0);
	    val_strt = newVec("val_strt", "str");
	    val_end = newVec("val_end", "end");
	    
	    reset_run = new nRunnable() { public void run() { got_reset(); } };
	    val_period.addEventChange(reset_run);
	    val_phi.addEventChange(reset_run);
	    val_strt.addEventChange(reset_run);
	    val_end.addEventChange(reset_run);
	    valONE.addEventChange(new nRunnable() { public void run() { 
	    		if (valONE.get()) got_reset(); } });
	    valRPT.addEventChange(new nRunnable() { public void run() { 
	    		if (valRPT.get()) got_reset(); } });
	    valINV.addEventChange(new nRunnable() { public void run() { 
	    		if (valINV.get()) got_reset(); } });
	    valLOOP.addEventChange(new nRunnable() { public void run() { 
	    		if (valLOOP.get()) got_reset(); } });
	    valLIN.addEventChange(new nRunnable() { public void run() { 
	    		if (valLIN.get()) got_reset(); } });
	    valSIN.addEventChange(new nRunnable() { public void run() { 
	    		if (valSIN.get()) got_reset(); } });
	}
  	void init_end() {
  		super.init_end();
	    got_stop();
  	}
  	void build_param() {
	    addSelectS_Excl(0, valLIN, valSIN, "Lin", "Sin");
	    addSelectL_Excl(1, valONE, valRPT, valINV, valLOOP, "1", "RP", "IV", "LP");
	    addSelectS(0, ext_tick, show_strstp, "extTk", "StSp");
	    addSelectL(1, show_period, show_phi, show_strt, show_end, "T", "PH", "ST", "ND");
  		build_normal();

	    // build loop solution
	  	nRunnable mode_run = new nRunnable() { public void run() {
	  		if (!build_flag)
	  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  				if (!rebuilding) rebuild(); }});
	  		build_flag = true;
	  	} };
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  		show_period.addEventChange(mode_run);
	  		show_phi.addEventChange(mode_run);
	  		show_strt.addEventChange(mode_run);
	  		show_end.addEventChange(mode_run);
	  		ext_tick.addEventChange(mode_run);
	  		show_strstp.addEventChange(mode_run);
	  	}});
  	}
  	void build_normal() {
  		if (show_period.get()) addInputToValue(0, val_period)
	  		.elem.addLinkedModel("MC_Element_SField").setLinkedValue(val_period)
	  		.setPX(ref_size*0.5).setInfo("period in tick"); 
  		if (show_phi.get()) addInputToValue(1, val_phi)
	  		.elem.addLinkedModel("MC_Element_SField").setLinkedValue(val_phi)
	  		.setPX(ref_size*0.5).setInfo("dephasing in tick");
  		
  		if (show_strt.get()) addInputToValue(0, val_strt)
	  		.elem.addWatcherModel("MC_Element_SField").setLinkedValue(val_strt)
	  		.setPX(ref_size*0.5).setInfo("start value");
  		if (show_end.get()) addInputToValue(1, val_end)
	  		.elem.addWatcherModel("MC_Element_SField").setLinkedValue(val_end)
	  		.setPX(ref_size*0.5).setInfo("end value");
	    
  		if (ext_tick.get()) addInputBang(0, "tick", 
  				new nRunnable() { public void run() { got_tick(); } })
  			.elem.addCtrlModel("MC_Element_SButton", "tick")
		  .setRunnable(new nRunnable() { public void run() { got_tick(); }});
  		if (show_strstp.get()) {
  	  		addInputBang(0, "start", new nRunnable() { public void run() { got_start(); } })
  	  		.elem.addCtrlModel("MC_Element_SButton", "start")
			  .setRunnable(new nRunnable() { public void run() { got_start(); }});
  	  		addInputBang(0, "stop", new nRunnable() { public void run() { got_stop(); } })
  	  		.elem.addCtrlModel("MC_Element_SButton", "stop")
			  .setRunnable(new nRunnable() { public void run() { got_stop(); }});
  		}
	    out_val = addOutput(1, "val").setDefFloat();
	    out_end = addOutput(1, "end").setDefBang();
  	}
  	void tick() {
  		super.tick();
  		if (!ext_tick.get()) got_tick();
  	}
  	PVector getval() {
  		PVector v = new PVector();
  		if (valLIN.get()) {
  			v.x = val_strt.x() + ((float)(count) / (float)(val_period.get())) * 
  			(val_end.x() - val_strt.x());
  			v.y = val_strt.y() + ((float)(count) / (float)(val_period.get())) * 
  			(val_end.y() - val_strt.y());
  		}
  		else {
  			v.x = (float) (val_strt.x() + (0.5 + Math.cos(RConst.PI * ((float)(count) / 
  	  				(float)(val_period.get()))) / 2 ) * (val_end.x() - val_strt.x()));
  			v.y = (float) (val_strt.y() + (0.5 + Math.cos(RConst.PI * ((float)(count) / 
  	  				(float)(val_period.get()))) / 2 ) * (val_end.y() - val_strt.y()));
  		}
  		return v;
  	}
  	int count = 0;
  	boolean isrunning = false;
  	void got_tick() {
  		if (isrunning) {
  			if (valONE.get()) {
  				count++;
  				out_val.send(Macro_Packet.newPacketVec(getval()));
  				if (count == val_period.get()) {
  					isrunning = false;
  					out_end.send(Macro_Packet.newPacketBang());
  				}
			} else if (valRPT.get()) {
  				count++;
  				out_val.send(Macro_Packet.newPacketVec(getval()));
  				if (count == val_period.get()) {
  					count = val_phi.get();
  					out_end.send(Macro_Packet.newPacketBang());
  				}
			} else if (valINV.get()) {
				count--;
				out_val.send(Macro_Packet.newPacketVec(getval()));
				if (count == 0) {
					count = val_period.get() + 1 - val_phi.get();
					out_end.send(Macro_Packet.newPacketBang());
				}
			} else if (valLOOP.get()) {
				if (loop_up) {
					count++;
					out_val.send(Macro_Packet.newPacketVec(getval()));
					if (count == val_period.get()) {
						loop_up = false;
					}
				} else {
					count--;
					out_val.send(Macro_Packet.newPacketVec(getval()));
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
	public MVecLerp clear() {
		super.clear(); return this; }
}


class MRamp extends MBaseTick { 
	static class MRamp_Builder extends MAbstract_Builder {
		MRamp_Builder() { super("ramp", "Rampe", "", "Control"); }
	  	MRamp build(Macro_Sheet s, sValueBloc b) { MRamp m = new MRamp(s, b); return m; }
	}
    boolean build_flag = false;
	Macro_Connexion in_tick, in_start, in_stop, in_reset, out_val, out_end;
  	sBoo valONE, valRPT, valINV, valLOOP;
  	sBoo valLIN, valSIN;
  	sFlt val_strt, val_end;
  	sInt val_period, val_phi;
  	sBoo show_period, show_phi, show_strt, show_end;
  	sBoo ext_tick, show_strstp;
  	nRunnable reset_run;
  	MRamp(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "ramp", _bloc); }
  	void init() {
  		super.init();
	    valONE = newBoo("valONE", "valONE", true);
	    valRPT = newBoo("valRPT", "valRPT", false);
	    valINV = newBoo("valINV", "valINV", false);
	    valLOOP = newBoo("valLOOP", "valLOOP", false);
	    valLIN = newBoo("valLIN", "valLIN", true);
	    valSIN = newBoo("valSIN", "valSIN", false);
	    show_period = newBoo("show_period", true);
	    show_phi = newBoo("show_phi", false);
	    show_strt = newBoo("show_strt", true);
	    show_end = newBoo("show_end", true);
	    ext_tick = newBoo("ext_tixk", false);
	    show_strstp = newBoo("show_strstp", true);
	    val_period = newInt("val_period", "period", 10);
	    val_phi = newInt("val_phi", "phi", 0);
	    val_strt = newFlt("val_strt", "str", 0);
	    val_end = newFlt("val_end", "end", 1);
	    
	    reset_run = new nRunnable() { public void run() { got_reset(); } };
	    val_period.addEventChange(reset_run);
	    val_phi.addEventChange(reset_run);
	    val_strt.addEventChange(reset_run);
	    val_end.addEventChange(reset_run);
	    valONE.addEventChange(new nRunnable() { public void run() { 
	    		if (valONE.get()) got_reset(); } });
	    valRPT.addEventChange(new nRunnable() { public void run() { 
	    		if (valRPT.get()) got_reset(); } });
	    valINV.addEventChange(new nRunnable() { public void run() { 
	    		if (valINV.get()) got_reset(); } });
	    valLOOP.addEventChange(new nRunnable() { public void run() { 
	    		if (valLOOP.get()) got_reset(); } });
	    valLIN.addEventChange(new nRunnable() { public void run() { 
	    		if (valLIN.get()) got_reset(); } });
	    valSIN.addEventChange(new nRunnable() { public void run() { 
	    		if (valSIN.get()) got_reset(); } });
  	}
  	void init_end() {
  		super.init_end();
	    got_stop();
  	}
  	void build_param() {
	    addSelectS_Excl(0, valLIN, valSIN, "Lin", "Sin");
	    addSelectL_Excl(1, valONE, valRPT, valINV, valLOOP, "1", "RP", "IV", "LP");
	    addSelectS(0, ext_tick, show_strstp, "extTk", "StSp");
	    addSelectL(1, show_period, show_phi, show_strt, show_end, "T", "PH", "ST", "ND");
  		build_normal();

	    // build loop solution
	  	nRunnable mode_run = new nRunnable() { public void run() {
	  		if (!build_flag)
	  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  				if (!rebuilding) rebuild(); }});
	  		build_flag = true;
	  	} };
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  		show_period.addEventChange(mode_run);
	  		show_phi.addEventChange(mode_run);
	  		show_strt.addEventChange(mode_run);
	  		show_end.addEventChange(mode_run);
	  		ext_tick.addEventChange(mode_run);
	  		show_strstp.addEventChange(mode_run);
	  	}});
  	}
  	void build_normal() {
  		if (show_period.get()) addInputToValue(0, val_period)
	  		.elem.addLinkedModel("MC_Element_SField").setLinkedValue(val_period)
	  		.setPX(ref_size*0.5).setInfo("period in tick"); 
  		if (show_phi.get()) addInputToValue(1, val_phi)
	  		.elem.addLinkedModel("MC_Element_SField").setLinkedValue(val_phi)
	  		.setPX(ref_size*0.5).setInfo("dephasing in tick");
  		if (show_strt.get()) addInputToValue(0, val_strt)
	  		.elem.addLinkedModel("MC_Element_SField").setLinkedValue(val_strt)
	  		.setPX(ref_size*0.5).setInfo("start value");
  		if (show_end.get()) addInputToValue(1, val_end)
	  		.elem.addLinkedModel("MC_Element_SField").setLinkedValue(val_end)
	  		.setPX(ref_size*0.5).setInfo("end value");
	    
  		if (ext_tick.get()) addInputBang(0, "tick", 
  				new nRunnable() { public void run() { got_tick(); } })
  			.elem.addCtrlModel("MC_Element_SButton", "tick")
		  .setRunnable(new nRunnable() { public void run() { got_tick(); }});
  		if (show_strstp.get()) {
  	  		addInputBang(0, "start", new nRunnable() { public void run() { got_start(); } })
  	  		.elem.addCtrlModel("MC_Element_SButton", "start")
			  .setRunnable(new nRunnable() { public void run() { got_start(); }});
  	  		addInputBang(0, "stop", new nRunnable() { public void run() { got_stop(); } })
  	  		.elem.addCtrlModel("MC_Element_SButton", "stop")
			  .setRunnable(new nRunnable() { public void run() { got_stop(); }});
  		}
	    out_val = addOutput(1, "val").setDefFloat();
	    out_end = addOutput(1, "end").setDefBang();
  	}
  	void tick() {
  		super.tick();
  		if (!ext_tick.get()) got_tick();
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



//class MPulse extends Macro_Bloc { //let throug only 1 bang every <delay> bang
//	  static class MPulse_Builder extends MAbstract_Builder {
//		  MPulse_Builder() { super("pulse", "Pulse", "", "Time"); }
//		  MPulse build(Macro_Sheet s, sValueBloc b) { MPulse m = new MPulse(s, b); return m; }
//	  }
//Macro_Connexion in, out;
//sInt delay;
//nLinkedWidget del_field;
//int count = 0;
//MPulse(Macro_Sheet _sheet, sValueBloc _bloc) { 
//  super(_sheet, "pulse", "pulse", _bloc); 
//  
//  delay = newInt("delay", "delay", 100);
//  
//  addEmptyS(1);
//  del_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(delay);
//  
//  in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
//    if (in.lastPack() != null && in.lastPack().isBang()) {
//      count++;
//      if (count > delay.get()) { count = 0; out.send(Macro_Packet.newPacketBang()); }
//    } else if (in.lastPack() != null && in.lastPack().isFloat()) {
//      count = 0;
//      delay.set((int)(in.lastPack().asFloat()));
//    } else if (in.lastPack() != null && in.lastPack().isInt()) {
//      count = 0;
//      delay.set(in.lastPack().asInt());
//    } 
//  } });
//      
//  out = addOutput(1, "out")
//    .setDefBool();
//}
//public MPulse clear() {
//  super.clear(); return this; }
//}

//class MFrame extends Macro_Bloc { 
//	  static class MFrame_Builder extends MAbstract_Builder {
//		  MFrame_Builder() { super("frame", "Frame", "", "Time"); }
//		  MFrame build(Macro_Sheet s, sValueBloc b) { MFrame m = new MFrame(s, b); return m; }
//	  }
//Macro_Connexion in, out;
//Macro_Packet packet1, packet2;
//boolean pack_balance = false;
//MFrame(Macro_Sheet _sheet, sValueBloc _bloc) { 
//  super(_sheet, "frame", "frame", _bloc); 
//  
//  in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
//    if (in.lastPack() != null) { 
//      if (pack_balance) { 
//        pack_balance = false;
//        packet1 = in.lastPack();
//        mmain().inter.addEventNextFrame(new nRunnable() { public void run() { out.send(packet1); }});
//      } else {
//        pack_balance = true;
//        packet2 = in.lastPack();
//        mmain().inter.addEventNextFrame(new nRunnable() { public void run() { out.send(packet2); }});
//      }
//    } 
//  } });
//      
//  out = addOutput(1, "out");
//}
//public MFrame clear() {
//  super.clear(); return this; }
//}