package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import RApplet.Rapp;
import UI.nFrontPanel;
import UI.nFrontTab;
import processing.core.PApplet;
import processing.core.PVector;
import sData.nRunnable;
import sData.sFlt;
import sData.sRun;
import sData.sStr;
import sData.sValueBloc;
import sData.sVec;

class Replic implements Macro_Interf {
	PVector pos = new PVector();
	PVector dir = new PVector();
	PVector pos() { return new PVector(pos.x, pos.y); } 
	PVector dir() { return new PVector(dir.x, dir.y); } 
	Replic pos(PVector f) { pos.x = f.x; pos.y = f.y; return this; } 
	Replic dir(PVector f) { dir.x = f.x; dir.y = f.y; return this; } 
	String to_str() {
		String s = PApplet.str(pos.x) + INFO_TOKEN + 
				PApplet.str(pos.y) + INFO_TOKEN + 
				PApplet.str(dir.x) + INFO_TOKEN + 
				PApplet.str(dir.y);
		return s;
	}
	void from_str(String s) {
		String[] l = PApplet.splitTokens(s, INFO_TOKEN);
		if (l.length == 4) {
			pos.x = PApplet.parseFloat(l[0]);
			pos.y = PApplet.parseFloat(l[1]);
			dir.x = PApplet.parseFloat(l[2]);
			dir.y = PApplet.parseFloat(l[3]);
		}
	}
}






public class MStructure extends MBaseMenu { 
	static class MStructure_Builder extends MAbstract_Builder {
		MStructure_Builder() { super("struct", "structure of forms"); show_in_buildtool = true; }
		MStructure build(Macro_Sheet s, sValueBloc b) { MStructure m = new MStructure(s, b); return m; }
	}
	MStructure new_replic() {
		Replic r = new Replic().pos(new_pos.get()).dir(new_dir.get());
		replics.add(r);
		last_build = r;
//		save_copy = replics_save.get();
		replics_save.set(replics_save.get() + OBJ_TOKEN + r.to_str());
		return this;
	}
//	String save_copy = "";
	MStructure del_oldest_replic() {
		if (last_build != null && last_build != first_rep) {
			replics.remove(last_build);
			if (replics.size() > 1) {
				last_build = replics.get(1);
//				replics_save.set(save_copy);
//				String[] l = PApplet.splitTokens(save_copy, OBJ_TOKEN);
//				save_copy = "";
//				if (l.length > 0) {
//					save_copy = l[0];
//					for (int i = 2 ; i < l.length ; i ++) save_copy = save_copy + l[i];
//				}
			} else {
				last_build = null;
//				replics_save.set("");
//				save_copy = "";
			}
		}
		return this;
	}
	MStructure clear_replic() {
		replics.clear();
		replics_save.set("");
		nposx.set(0); nposy.set(0); nscale.set(10); nrot.set(0);
		first_rep = new Replic().pos(new PVector(0, 0)).dir(new PVector(10, 0));
		replics.add(first_rep); 
		return this;
	}
	String current_replic() {
		return first_rep.to_str();
	}
	MStructure set_replic_from(Replic r) {
		nposx.set(r.pos.x);
		nposy.set(r.pos.y);
		nrot.set(r.dir.heading());
		nscale.set(r.dir.mag());
		new_pos.set(nposx.get(), nposy.get()); 
		first_rep.pos.set(nposx.get(), nposy.get());
		first_rep.dir.set(nscale.get(), 0);
		first_rep.dir.rotate(nrot.get());
		new_dir.set(first_rep.dir.x, first_rep.dir.y); 
		return this;
	}

	MStructure translate(PVector r) {
		nposx.add(r.x);
		nposy.add(r.y);
		up_npos.run();
		return this;
	}
	MStructure rotate(float r) {
		nrot.add(r);
		up_npos.run();
		return this;
	}
	MStructure scale(float r) {
		nscale.add(r);
		up_npos.run();
		return this;
	}
	
	ArrayList<Replic> replics;
	Replic first_rep, last_build;
	Macro_Connexion form_link, cam_link, com_in;
	MForm last_form;
	sFlt nposx,nposy,nscale,nrot;
	sVec new_pos, new_dir;
	sRun new_rep_run, clear_rep_run;
	nRunnable up_npos;
	sStr replics_save;
	MStructure(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "struct", _bloc); }
	void init() {
		super.init();   
		replics = new ArrayList<Replic>();
		first_rep = new Replic().pos(new PVector(100, 100)).dir(new PVector(10, 0));
		replics.add(first_rep);
		new_rep_run = newRun("new_rep_run", "new_rep_run", new nRunnable() {public void run() { 
			new_replic(); }});
		clear_rep_run = newRun("clear_rep_run", "clear_rep_run", new nRunnable() {public void run() { 
			clear_replic(); }});
		new_pos = newVec("new_pos", "new_pos");
		new_dir = newVec("new_dir", "new_dir");
		nposx = menuFltSlide(0, -10000, 10000, "adding_x");
		nposy = menuFltSlide(0, -10000, 10000, "adding_y");
		nscale = menuFltSlide(10, 1, 20, "adding_scale");
		nrot = menuFltSlide(0, -RConst.PI, RConst.PI, "adding_rot");
		up_npos = new nRunnable() {public void run() { 
			new_pos.set(nposx.get(), nposy.get()); 
			first_rep.pos.set(nposx.get(), nposy.get());
			first_rep.dir.set(nscale.get(), 0);
			first_rep.dir.rotate(nrot.get());
			new_dir.set(first_rep.dir.x, first_rep.dir.y); 
		}};
		up_npos.run();
		nposx.addEventChange(up_npos);
		nposy.addEventChange(up_npos);
		nscale.addEventChange(up_npos);
		nrot.addEventChange(up_npos);
		replics_save = newStr("replics_save", "replics_save", "");
		if (replics_save.get().length() > 0) {
			String[] l = PApplet.splitTokens(replics_save.get(), OBJ_TOKEN);
			for (String s : l) {
				Replic r = new Replic();
				replics.add(r);
				r.from_str(s);
			}
		}
	}
	void build_normal() {
		super.build_normal();
		com_in = addInput(0,"COM", new nRunnable() {public void run() { 
//			if (com_in.lastPack() == null) return;
//			String m_def = com_in.lastPack().def;
//			String msg = com_in.lastPack().popMsg();
//			if (com_in.lastPack() != null && m_def.equals("ASK") && 
//					com_in.lastPack().hasMsg() &&
//					msg.equals("STRUCT_NEW")) {
//				new_replic();
//			} else if (com_in.lastPack() != null && m_def.equals("ASK") && 
//					com_in.lastPack().hasMsg() &&
//					msg.equals("STRUCT_CLR")) {
//				clear_replic();
//			} else if (com_in.lastPack() != null && m_def.equals("ASK") && 
//					com_in.lastPack().hasMsg() &&
//					msg.equals("STRUCT_DEF")) {
//				while(com_in.lastPack().hasMsg()) {
//					String st = com_in.lastPack().popMsg();
//					Replic r = new Replic();
//					replics.add(r);
//					r.from_str(st);
//					replics_save.set(replics_save.get() + OBJ_TOKEN + st);
//				}
//			} else if (com_in.lastPack() != null && com_in.lastPack().isObject()) {
//				Replic r = (Replic)com_in.lastPack().asObject();
//				r.from_str(first_rep.to_str());
//			} 
		}});
		addEmptyS(1);
		addTrigS(0, "new", new_rep_run.get());
		addTrigS(1, "clear", clear_rep_run.get());
		form_link = addInput(0,"form");
		cam_link = addOutput(1,"cam");
	}
	public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.getTab(2);
	    tab.getShelf()
//	      .addDrawer(10.25, 0.75)
//	      .addModel("Label-S4", "-Canvas Control-").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(new_rep_run, clear_rep_run, 10, 1)
	      .addSeparator(0.125)
	      ;
	}

	public void draw(Rapp a) { 	
		if (replics.size() > 0) {
			MForm mf = null;
			for (Macro_Connexion c : form_link.connected_outputs) {
				if (c.elem.bloc.val_type.get().equals("form")) {
					mf = ((MForm)c.elem.bloc);
					if (mf != null) for (Replic r : replics) {
						a.pushMatrix();
						a.translate(r.pos.x, r.pos.y);
						a.rotate(r.dir.heading());
						a.scale(r.dir.mag());
						mf.draw(a);
						a.popMatrix();
					}
					if (mf != null) last_form = mf;
				}
			}
		}
	}

	public MStructure clear() {
		super.clear(); 
		return this; }
	public MStructure toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
