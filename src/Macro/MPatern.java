package Macro;

import RApplet.RConst;
import UI.nFrontPanel;
import UI.nFrontTab;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sRun;
import sData.sValueBloc;

public class MPatern extends MBaseMenu { 
	static class MPatern_Builder extends MAbstract_Builder {
		MPatern_Builder() { super("patern", "auto build structures"); show_in_buildtool = true; }
		MPatern build(Macro_Sheet s, sValueBloc b) { MPatern m = new MPatern(s, b); return m; }
	}
	Macro_Connexion in_tick, struct_order;
	nRunnable tick_run;
	sBoo auto_mode_val, mode_1, mode_2;
	sFlt rot_speed;

	sFlt nposx,nposy,nscale,nrot;
	nRunnable up_npos;
	
	sRun ask_add_run, ask_clear_run, ask_del_run, ask_get_run, ask_set_run;
	Replic current_replic;
	
	
	MPatern(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "patern", _bloc); }
	void init() {
		super.init();   
		tick_run = new nRunnable() { public void run() { 
			if (in_tick.lastPack() != null &&in_tick.lastPack().isBang()) tick(); 
		} };

		auto_mode_val = menuBoo(false, "auto_mode_val");
		mode_1 = menuBoo(false, "mode_1");
		mode_2 = menuBoo(false, "mode_2");
		
		rot_speed = menuFltSlide(0, -RConst.PI / 60, RConst.PI / 60, "rot_speed");

		ask_add_run = newRun("ask_add_run", "ask_add_run", new nRunnable() { 
		      public void run() { ask_add(); } } );
		ask_clear_run = newRun("ask_clear_run", "ask_clear_run", new nRunnable() { 
		      public void run() { ask_clear(); } } );
		ask_del_run = newRun("ask_del_run", "ask_del_run", new nRunnable() { 
		      public void run() { ask_del(); } } );
		ask_get_run = newRun("ask_get_run", "ask_get_run", new nRunnable() { 
		      public void run() { ask_get(); } } );
		ask_set_run = newRun("ask_set_run", "ask_set_run", new nRunnable() { 
		      public void run() { ask_set(); } } );
		
		current_replic = new Replic();
		
		nposx = menuFltSlide(0, -500, 500, "adding_x");
		nposy = menuFltSlide(0, -500, 500, "adding_y");
		nscale = menuFltSlide(10, 5, 20, "adding_scale");
		nrot = menuFltSlide(0, -RConst.PI, RConst.PI, "adding_rot");
		up_npos = new nRunnable() {public void run() { 
			current_replic.pos.set(nposx.get(), nposy.get());
			current_replic.dir.set(nscale.get(), 0);
			current_replic.dir.rotate(nrot.get());
		}};
		
		nposx.addEventChange(new nRunnable() { public void run() { 
			out_px.send(Macro_Packet.newPacketFloat(nposx.get())); }});
		nposy.addEventChange(new nRunnable() { public void run() { 
			out_py.send(Macro_Packet.newPacketFloat(nposy.get())); }});
		nscale.addEventChange(new nRunnable() { public void run() { 
			out_s.send(Macro_Packet.newPacketFloat(nscale.get())); }});
		nrot.addEventChange(new nRunnable() { public void run() { 
			out_r.send(Macro_Packet.newPacketFloat(nrot.get())); }});
		nposx.addEventChange(up_npos);
		nposy.addEventChange(up_npos);
		nscale.addEventChange(up_npos);
		nrot.addEventChange(up_npos);
		up_npos.run();
	}
	Macro_Connexion in_px,in_py,in_s,in_r , out_px,out_py,out_s,out_r;
	void build_param() {
		super.build_param();
//		in_tick = addInput(0,"tick", tick_run);
//		struct_order = addOutput(1,"struct order out");
//		addInputBang(0, "add", ask_add_run.get());
//		addInputBang(0, "clear", ask_clear_run.get());
//		addInputBang(0, "get", ask_get_run.get());
//		addInputBang(0, "set", ask_set_run.get());
//		
//		addEmptyS(1); addEmptyS(1); addEmptyS(1); addEmptyS(1);
//
//		out_px = addOutput(1, "nposx");
//		out_py = addOutput(1, "nposy");
//		out_s = addOutput(1, "nscale");
//		out_r = addOutput(1, "nrot");
//
//		in_px = addInputFloat(0, "nposx", new nRunnable() { public void run() { 
//			nposx.set(in_px.lastPack().asFloat()); }});
//		in_py = addInputFloat(0, "nposy", new nRunnable() { public void run() { 
//			nposy.set(in_py.lastPack().asFloat()); }});
//		in_s = addInputFloat(0, "nscale", new nRunnable() { public void run() { 
//			nscale.set(in_s.lastPack().asFloat()); }});
//		in_r = addInputFloat(0, "nrot", new nRunnable() { public void run() { 
//			nrot.set(in_r.lastPack().asFloat()); }});
	}
	void build_normal() {
		super.build_normal();
		in_tick = addInput(0,"tick", tick_run);
		struct_order = addOutput(1,"struct order out");
		addInputBang(0, "add", ask_add_run.get());
		addInputBang(0, "del", ask_del_run.get());
		addInputBang(0, "clear", ask_clear_run.get());
		addInputBang(0, "get", ask_get_run.get());
		addInputBang(0, "set", ask_set_run.get());
		
		addEmptyS(1); addEmptyS(1); addEmptyS(1); addEmptyS(1); addEmptyS(1);

		out_px = addOutput(1, "nposx");
		out_py = addOutput(1, "nposy");
		out_s = addOutput(1, "nscale");
		out_r = addOutput(1, "nrot");

		in_px = addInputFloat(0, "nposx", new nRunnable() { public void run() { 
			nposx.set(in_px.lastPack().asFloat()); }});
		in_py = addInputFloat(0, "nposy", new nRunnable() { public void run() { 
			nposy.set(in_py.lastPack().asFloat()); }});
		in_s = addInputFloat(0, "nscale", new nRunnable() { public void run() { 
			nscale.set(in_s.lastPack().asFloat()); }});
		in_r = addInputFloat(0, "nrot", new nRunnable() { public void run() { 
			nrot.set(in_r.lastPack().asFloat()); }});
	}
	void ask_add() {
//		Macro_Packet p = new Macro_Packet("ASK");
//		p.addMsg("STRUCT_NEW");
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.new_replic();
			}
		}
	}
	void ask_clear() {
//		Macro_Packet p = new Macro_Packet("ASK");
//		p.addMsg("STRUCT_CLR");
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.clear_replic();
			}
		}
	}
	void ask_del() {
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.del_oldest_replic();
			}
		}
	}
	void ask_get() {
//		Macro_Packet p = Macro_Packet.newPacketObject((Object)current_replic);
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				current_replic.from_str(mf.current_replic());
			}
		}
	}
	void ask_set() {
//		Macro_Packet p = new Macro_Packet("ASK");
//		p.addMsg("STRUCT_DEF");
//		p.addMsg(current_replic.to_str());
//		struct_order.send(p);
		MStructure mf = null;
		for (Macro_Connexion c : struct_order.connected_inputs) {
			if (c.elem.bloc.val_type.get().equals("struct")) {
				mf = ((MStructure)c.elem.bloc);
				mf.set_replic_from(current_replic);
			}
		}
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.getTab(2);
	
	    tab.getShelf()
	      .addDrawer(10.25, 0.6)
	      .addModel("Label-S4", "- Simulation Control -").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(ask_add_run, ask_clear_run, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(ask_get_run, ask_set_run, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(mode_1, mode_2, 10, 1)
	      .addSeparator(0.125)
	      ;
	}
	public void tick() {
		
		if (auto_mode_val.get()) {
			//rot replic around center ou add by rotating ou add in line ...
			// find how: add after last to make object chaine
			
			// send order via Msg packet trough COM ???
		
			MStructure mf = null;
			for (Macro_Connexion c : struct_order.connected_inputs) {
				if (c.elem.bloc.val_type.get().equals("struct")) {
					mf = ((MStructure)c.elem.bloc);
					for (Replic r : mf.replics) {
						if (mode_1.get())
							r.dir(r.dir().rotate(rot_speed.get()));
						if (mode_2.get())
							r.pos(r.pos().rotate(rot_speed.get()));
						
					}
				}
			}
		}
	}
	
	public MPatern clear() {
		super.clear(); 
		return this; }
	public MPatern toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
