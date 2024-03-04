package Macro;

import RApplet.*;
import UI.*;
import processing.core.*;
import sData.*;

public class M_Var_Transform {}



class MVecCalc extends MBasic {
	static class MVecCalc_Builder extends MAbstract_Builder {
		MVecCalc_Builder() { super("vecCalc", "VectorCalc", "", "Data"); }
		MVecCalc build(Macro_Sheet s, sValueBloc b) { MVecCalc m = new MVecCalc(s, b); return m; }
	}
	Macro_Connexion in1, in2, out;
	sBoo valXY, valROT, valMAG, valSET, valADD, valMUL;
	nWidget wSET, wADD, wMUL;
	sVec vval1,vval2; sFlt fval;
	MVecCalc(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "vecCalc", _bloc); }
	public void init() {
	  	valXY = newBoo("valXY", "valXY", false);
	  	valROT = newBoo("valROT", "valROT", false);
	  	valMAG = newBoo("valMAG", "valMAG", false);
	  	valSET = newBoo("valSET", "valSET", false);
	  	valADD = newBoo("valADD", "valADD", false);
	  	valMUL = newBoo("valMUL", "valMUL", false);

		vval1 = newVec("vval1", "vval1");
		vval2 = newVec("vval2", "vval2");
		fval = newFlt("fval", "fval", 0);
	}
	private boolean valid() {
		return ((valXY.get()  && valADD.get()) ||  
  				(valXY.get()  && valSET.get()) ||  
  				(valROT.get() && valADD.get()) ||  
  				(valROT.get() && valSET.get()) ||  
  				(valMAG.get() && valADD.get()) ||  
  				(valMAG.get() && valSET.get()) ||  
  				(valMAG.get() && valMUL.get()) );
	}
	public void build_param() { 
		addEmpty(1);
		addSelectL(0, valXY, valROT, valMAG, "x y", "rot", "mag");

		addEmpty(1);
	    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
	    addElement(0, m); 
	    wSET = m.addLinkedModel("MC_Element_Button_Selector_1", "set").setLinkedValue(valSET);
	    wADD = m.addLinkedModel("MC_Element_Button_Selector_2", "add").setLinkedValue(valADD);
	    wMUL = m.addLinkedModel("MC_Element_Button_Selector_3", "mult").setLinkedValue(valMUL);
	    wSET.addExclude(wADD).addExclude(wMUL).hide();
	    wADD.addExclude(wSET).addExclude(wMUL).hide();
	    wMUL.addExclude(wADD).addExclude(wSET).hide();
	    if (valXY.get()) wADD.show();
	    if (valROT.get()) { wADD.show(); wSET.show(); }
	    if (valMAG.get()) { wADD.show(); wSET.show(); wMUL.show(); }
	  	nRunnable view_run = new nRunnable() { public void run() {
	  		if      (valXY.get())  { wADD.show(); wSET.show().setText("sub"); wMUL.hide(); }
	  		else if (valROT.get()) { wADD.show(); wSET.show().setText("set"); wMUL.hide(); }
	  		else if (valMAG.get()) { wADD.show(); wSET.show().setText("set"); wMUL.show(); }
	  		else                   { wADD.hide(); wSET.hide(); wMUL.hide(); }
	  	} };
	  	valXY.addEventChange(view_run);
	  	valROT.addEventChange(view_run);
	  	valMAG.addEventChange(view_run);
	    wSET.addEventVisibilityChange(view_run);
	    wADD.addEventVisibilityChange(view_run);
	    wMUL.addEventVisibilityChange(view_run);
		
		if (!valid()) {
		  	nRunnable mode_run = new nRunnable() { public void run() {
		  		if ( param_view.get() && valid() ) {
					param_view.set(false);
		  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  				if (!rebuilding) rebuild(); }});
		  		}
		  	} };
		  	valXY.addEventChange(mode_run);
		  	valROT.addEventChange(mode_run);
		  	valMAG.addEventChange(mode_run);
		  	valSET.addEventChange(mode_run);
		  	valADD.addEventChange(mode_run);
		  	valMUL.addEventChange(mode_run);
		}
	}
	public void build_normal() {
		if (!valid()) {
			param_view.set(true);
			build_param();
			return;
		}
		
		nRunnable calc_run = new nRunnable() { public void run() { do_calc(); }};

		in1 = addInputToValue(0, vval1);
		if (valXY.get()) in2 = addInputToValue(0, vval2);
		else in2 = addInputToValue(0, fval);
		
	  	out = addOutput(1, "out")
			  .setDefVec();
	  	
	  	in1.addPassBang();
	  	in1.addEventReceiveBang(calc_run);
	  	in2.addPassBang();
	  	in2.addEventReceiveBang(calc_run);
	  	
	  	vval1.addEventChange(calc_run);
	  	vval2.addEventChange(calc_run);
	  	fval.addEventChange(calc_run);
	  	
	  	nDrawer dr = addEmptyS(1);
		if (!valXY.get()) dr.addLinkedModel("MC_Element_SField").setLinkedValue(fval);
		
	  	nWidget op = dr.addModel("MC_Element_SField")
	  	.setPosition(ref_size*-1, ref_size*-(7.0 / 16.0)).setSize(ref_size*0.75, ref_size*0.75);
	  	if (valADD.get() && valXY.get()) op.setText("+");
	  	if (valSET.get() && valXY.get()) op.setText("-");
	  	if (valADD.get() && valROT.get()) op.setText("+ROT");
	  	if (valSET.get() && valROT.get()) op.setText("=ROT");
	  	if (valMUL.get() && valMAG.get()) op.setText("xMAG");
	  	if (valADD.get() && valMAG.get()) op.setText("+MAG");
	  	if (valSET.get() && valMAG.get()) op.setText("=MAG");
	}
	void do_calc() { 
		if (out != null) {
			if      (valXY.get()) {
				if      (valADD.get()) out.send(Macro_Packet.newPacketVec(
					new PVector(vval1.get().x + vval2.get().x, vval1.get().y + vval2.get().y)));
				else if (valSET.get()) out.send(Macro_Packet.newPacketVec(
					new PVector(vval1.get().x - vval2.get().x, vval1.get().y - vval2.get().y)));
			}
		  	else if (valROT.get()) {
		  		PVector v = vval1.get();
				if      (valSET.get()) out.send(Macro_Packet.newPacketVec(
						new PVector(v.mag(), 0).rotate(fval.get())));
				else if (valADD.get()) out.send(Macro_Packet.newPacketVec(
						v.rotate(fval.get())));
			}
		  	else if (valMAG.get()) {
		  		PVector v = vval1.get();
				if      (valSET.get()) out.send(Macro_Packet.newPacketVec(
						v.setMag(fval.get())));
				else if (valADD.get()) out.send(Macro_Packet.newPacketVec(
						v.setMag(v.mag() + fval.get())));
				else if (valMUL.get()) out.send(Macro_Packet.newPacketVec(
						v.setMag(v.mag() * fval.get())));
			}
		}
	}
	public MVecCalc clear() {
		super.clear(); return this; }
}



class MNumCalc extends MBasic {
	static class Builder extends MAbstract_Builder {
		Builder() { super("numCalc", "NumberCalc", "", "Data"); }
		MNumCalc build(Macro_Sheet s, sValueBloc b) { MNumCalc m = new MNumCalc(s, b); return m; }
	}
	Macro_Connexion in1, in2, out;
	nLinkedWidget widgADD, widgSUB, widgMUL, widgDEV; 
	sBoo valADD, valSUB, valMUL, valDEV;
	float pin1 = 0, pin2 = 0;
	nLinkedWidget view;
	sStr val_view; 
	MNumCalc(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "numCalc", _bloc); }
	public void init() {
	  	val_view = newStr("val", "val", "");
	  
		valADD = newBoo("valADD", "valADD", false);
	  	valSUB = newBoo("valSUB", "valSUB", false);
	  	valMUL = newBoo("valMUL", "valMUL", false);
	  	valDEV = newBoo("valDEV", "valDEV", false);
	}
	public void build_param() { 
		addEmpty(1);
	  	Macro_Element e = addEmptyL(0);
	  	widgADD = e.addLinkedModel("MC_Element_Button_Selector_1", "+").setLinkedValue(valADD);
	  	widgSUB = e.addLinkedModel("MC_Element_Button_Selector_2", "-").setLinkedValue(valSUB);
	  	widgMUL = e.addLinkedModel("MC_Element_Button_Selector_3", "X").setLinkedValue(valMUL);
	  	widgDEV = e.addLinkedModel("MC_Element_Button_Selector_4", "/").setLinkedValue(valDEV);
	  	widgADD.addExclude(widgDEV).addExclude(widgSUB).addExclude(widgMUL);
	  	widgSUB.addExclude(widgADD).addExclude(widgDEV).addExclude(widgMUL);
	  	widgMUL.addExclude(widgADD).addExclude(widgSUB).addExclude(widgDEV);
	  	widgDEV.addExclude(widgADD).addExclude(widgSUB).addExclude(widgMUL);
		if (!valADD.get() && !valSUB.get() && !valMUL.get() && !valDEV.get()) {
		  	nRunnable mode_run = new nRunnable() { public void run() {
		  		if ( param_view.get() && 
		  				(valADD.get() || valSUB.get() || valMUL.get() || valDEV.get()) ) {
					param_view.set(false);
		  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  				if (!rebuilding) rebuild(); }});
		  		}
		  	} };
			valADD.addEventChange(mode_run);
		  	valSUB.addEventChange(mode_run);
		  	valMUL.addEventChange(mode_run);
		  	valDEV.addEventChange(mode_run);
		
		}
	}
	public void build_normal() {
		if (!valADD.get() && !valSUB.get() && !valMUL.get() && !valDEV.get()) {
			param_view.set(true);
			build_param();
			return;
		}
		in1 = addInput(0, "in").setFilterNumber().setLastFloat(0)
	  		.addEventReceive(new nRunnable() { public void run() { 
	  		      if (in1.lastPack() != null && in1.lastPack().isFloat() && in1.lastPack().asFloat() != pin1) {
	  		          pin1 = in1.lastPack().asFloat(); receive(); } 
	  		      else if (in1.lastPack() != null && in1.lastPack().isInt() && in1.lastPack().asInt() != pin1) {
	  		          pin1 = in1.lastPack().asInt(); receive(); } } });
	  	in2 = addInput(0, "in").setFilterNumber().setLastFloat(0)
	  		.addEventReceive(new nRunnable() { public void run() { 
	  		      if (in2.lastPack() != null && in2.lastPack().isFloat() && in2.lastPack().asFloat() != pin2) {
	  		          pin2 = in2.lastPack().asFloat(); view.setText(RConst.trimFlt(pin2)); receive(); }
	  		      else if (in2.lastPack() != null && in2.lastPack().isInt() && in2.lastPack().asInt() != pin2) {
	  		          pin2 = in2.lastPack().asInt(); view.setText(RConst.trimFlt(pin2)); receive(); } } });
	  
	  	out = addOutput(1, "out")
			  .setDefFloat();
	    
	  	view = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view);
	  	view.addEventFieldChange(new nRunnable() { public void run() { 
	    String t = view.getText();
	    		if (t.length() > 0) {
	    			if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
	      		else if (Rapp.parseFlt(t) != 0) { pin2 = Rapp.parseFlt(t); in2.setLastFloat(pin2); receive(); }
	    		}
	  	} });
	  	
	  	nWidget op = view.getDrawer().addModel("MC_Element_SField")
	  	.setPosition(ref_size*-1, ref_size*-(7.0 / 16.0)).setSize(ref_size*0.75, ref_size*0.75);
	  	if (valADD.get()) op.setText("+");
	  	if (valSUB.get()) op.setText("-");
	  	if (valMUL.get()) op.setText("x");
	  	if (valDEV.get()) op.setText("/");
	  	
	  	String t = view.getText();
	  	if (t.length() > 0) {
		  	if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); }
	    		else if (Rapp.parseFlt(t) != 0) { pin2 = Rapp.parseFlt(t); in2.setLastFloat(pin2); } 
	  	}
	}
	void receive() { 
		if (out != null) {
			if      (valADD.get()) out.send(Macro_Packet.newPacketFloat(pin1 + pin2));
		  	else if (valSUB.get()) out.send(Macro_Packet.newPacketFloat(pin1 - pin2));
		  	else if (valMUL.get()) out.send(Macro_Packet.newPacketFloat(pin1 * pin2));
		  	else if (valDEV.get() && pin2 != 0) out.send(Macro_Packet.newPacketFloat(pin1 / pin2));
		}
	}
	public MNumCalc clear() {
		super.clear(); return this; }
} 



class MBoolCalc extends MBasic {
	static class Builder extends MAbstract_Builder {
		Builder() { super("boolCalc", "BooleanCalc", "", "Data"); }
		MBoolCalc build(Macro_Sheet s, sValueBloc b) { MBoolCalc m = new MBoolCalc(s, b); return m; }
	}
	Macro_Connexion in1, in2, out;
	sBoo valAND, valOR, valXOR, valNOT;
	sBoo val1,val2;
	MBoolCalc(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "boolCalc", _bloc); }
	public void init() {
		valAND = newBoo("valAND", "valAND", false);
		valOR = newBoo("valOR", "valOR", false);
		valXOR = newBoo("valXOR", "valXOR", false);
		valNOT = newBoo("valNOT", "valNOT", false);

		val1 = newBoo("val1", "val1", false);
		val2 = newBoo("val2", "val2", false);
	}
	public void build_param() { 
		addEmpty(1);
		addSelectL(0, valAND, valOR, valXOR, "&&", "||", "X||");
		addEmpty(1);
		addEmptyL(0).addLinkedModel("MC_Element_Button_Selector_1", "!")
			.setLinkedValue(valNOT);
		
	  	nRunnable mode_run = new nRunnable() { public void run() {
	  		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  			if (!rebuilding) rebuild(); }});
	  	} };
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  	valAND.addEventChange(mode_run);
		  	valOR.addEventChange(mode_run);
		  	valXOR.addEventChange(mode_run);
		  	valNOT.addEventChange(mode_run); }});
	  	build_normal();
	}
	public void build_normal() {
		nRunnable calc_run = new nRunnable() { public void run() { do_calc(); }};

		in1 = addInputToValue(0, val1);
		in1.addEventReceive(new nRunnable() { public void run() {
			if (in1.lastPack() != null && !doubled() && in1.lastPack().isBool()
				&& in1.lastPack().asBool() == val1.get()) do_calc();
		}});
		if (doubled()) in2 = addInputToValue(0, val2);
	  	out = addOutput(1, "out")
			  .setDefBool();
	  	
	  	in1.addPassBang();
	  	in1.addEventReceiveBang(calc_run);
	  	if (doubled()) in2.addPassBang();
	  	if (doubled()) in2.addEventReceiveBang(calc_run);
	  	
	  	val1.addEventChange(calc_run);
	  	val2.addEventChange(calc_run);
	  	if (doubled()) {
		  	nDrawer dr = addEmptyS(1);
			dr.addLinkedModel("MC_Element_SField").setLinkedValue(val2);
			
		  	nWidget op = dr.addModel("MC_Element_SField")
		  	.setPosition(ref_size*-1, ref_size*-(7.0 / 16.0))
		  	.setSize(ref_size*0.75, ref_size*0.75);
		  	if (valAND.get() && valNOT.get()) op.setText("!&&");
		  	if (valOR.get() && valNOT.get()) op.setText("!||");
		  	if (valXOR.get() && valNOT.get()) op.setText("!X||");
		  	if (valAND.get() && !valNOT.get()) op.setText("&&");
		  	if (valOR.get() && !valNOT.get()) op.setText("||");
		  	if (valXOR.get() && !valNOT.get()) op.setText("X||");
	  	} else {
//		  	in1.elem.addModel("MC_Element_SField", "!")
//		  	.setPosition(ref_size*-2, ref_size*(2.0 / 16.0))
//		  	.setSize(ref_size*0.75, ref_size*0.75)
//		  	.toLayerTop();
	  	}
	}
	private boolean doubled() {
		return (valAND.get() || valOR.get() || valXOR.get());
	}
	void do_calc() { 
		if (out != null) {
			if (valAND.get()) {
				if (valNOT.get()) out.sendBool(!(val1.get() && val2.get()));
				else out.sendBool(val1.get() && val2.get());
			} else if (valOR.get()) {
				if (valNOT.get()) out.sendBool(!(val1.get() || val2.get()));
				else out.sendBool(val1.get() || val2.get());
			} else if (valXOR.get()) {
				if (valNOT.get()) out.sendBool(!(val1.get() ^ val2.get()));
				else out.sendBool(val1.get() ^ val2.get());
			} else if (valNOT.get()) {
				out.sendBool(!val1.get());
			}
		}
	}
	public MBoolCalc clear() {
		super.clear(); return this; }
}



class MTransform extends MBasic {
	static class Builder extends MAbstract_Builder {
		Builder() { super("transf", "Transform", "Transform and Filter data", "Data"); }
		MTransform build(Macro_Sheet s, sValueBloc b) { MTransform m = new MTransform(s, b); return m; }
	}
	Macro_Connexion in1, in2, out1, out2;
	sBoo mode_filter, mode_transf, 
		 filt_bang  , filt_boo   , filt_num  , filt_vec,
		 filt_boo_st, filt_boo_true, fltr_count, fltr_frame,
		 trsf_AtBG  , trsf_BtfBG , trsf_vecXY, trsf_vecMD ;
	sInt fltr_time_count;
	
	//vec XY n MD
  	float x = 0, y = 0;
    float mag = 1, dir = 0;
  	PVector vec;
    nLinkedWidget view1, view2;
    sStr val_view1, val_view2; 
    boolean build_flag = false;
	MTransform(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "transf", _bloc); }
	public void init() {
		mode_filter = newBoo("mode_filter", "mode_filter", false);
		mode_transf = newBoo("mode_transf", "mode_transf", false);
		filt_bang = newBoo("filt_bang", "filt_bang", false);
		filt_boo = newBoo("filt_boo", "filt_boo", false);
		filt_boo_st = newBoo("filt_boo_st", "filt_boo_st", false);
		filt_boo_true = newBoo("filt_boo_true", "filt_boo_true", false);
		fltr_count = newBoo("fltr_count", "fltr_count", false);
		fltr_frame = newBoo("fltr_frame", "fltr_frame", false);
		fltr_time_count = newInt("fltr_time_count", "fltr_time_count", 0);
		filt_num = newBoo("filt_num", "filt_num", false);
		filt_vec = newBoo("filt_vec", "filt_vec", false);

		trsf_AtBG = newBoo("trsf_AtBG", "trsf_AtBG", false);
		trsf_BtfBG = newBoo("trsf_BtfBG", "trsf_BtfBG", false);
		trsf_vecXY = newBoo("trsf_vecXY", "trsf_vecXY", false);
		trsf_vecMD = newBoo("trsf_vecMD", "trsf_vecMD", false);
	}
	public void build_param() { 
		addEmpty(1); addEmpty(2);
		addEmptyXL(0).addLinkedModel("MC_Element_LButton", "Filter").setLinkedValue(mode_filter);
		if (mode_filter.get()) {
			addEmpty(1); addEmpty(2);
			nDrawer m = addEmptyXL(0);
			m.addLinkedModel("MC_Element_Button_Selector_1", "bang").setLinkedValue(filt_bang);
			m.addLinkedModel("MC_Element_Button_Selector_2", "bool").setLinkedValue(filt_boo);
			m.addLinkedModel("MC_Element_Button_Selector_3", "num").setLinkedValue(filt_num);
			m.addLinkedModel("MC_Element_Button_Selector_4", "vec").setLinkedValue(filt_vec);
			if (filt_boo.get()) {
				addEmpty(1); addEmpty(2);
				m = addEmptyXL(0);
				m.addModel("MC_Element_Text", "bool fltr")
				.setSX(ref_size * 1.9).setPX(ref_size * 0.125);
				m.addLinkedModel("MC_Element_Button", "state").setLinkedValue(filt_boo_st)
				.setSX(ref_size * 1.9).setPX(ref_size * 2.15);
				if (filt_boo_st.get()) 
					m.addLinkedModel("MC_Element_Button", "").setLinkedValue(filt_boo_true)
					.setSX(ref_size * 1.9).setPX(ref_size * 4.175);
	  		}
			addSelectS(0, fltr_count, fltr_frame, "CNT", "FRM");
			if (fltr_count.get() || fltr_frame.get()) addSelectToInt(1, fltr_time_count);
			else { addEmptyL(1); addEmpty(2); }
		}
		addEmpty(1); addEmpty(2);
		addEmptyXL(0).addLinkedModel("MC_Element_LButton", "Transform").setLinkedValue(mode_transf);
		if (mode_transf.get()) {
			addEmpty(1); addEmpty(2);
			nDrawer m = addEmptyXL(0);
			nWidget w1 = m.addLinkedModel("MC_Element_SButton", "All>Bang")
				.setLinkedValue(trsf_AtBG)
				.setSX(ref_size * 3).setPX(ref_size * 0.125);
			nWidget w2 = m.addLinkedModel("MC_Element_SButton", "Bang<>Bool")
				.setLinkedValue(trsf_BtfBG)
				.setSX(ref_size * 3).setPX(ref_size * 3.25);
			addEmpty(1); addEmpty(2);
			m = addEmptyXL(0);
			nWidget w3 = m.addLinkedModel("MC_Element_SButton", "Vec<>XY")
				.setLinkedValue(trsf_vecXY)
				.setSX(ref_size * 3).setPX(ref_size * 0.125);
			nWidget w4 = m.addLinkedModel("MC_Element_SButton", "Vec<>MagDir")
				.setLinkedValue(trsf_vecMD)
				.setSX(ref_size * 3).setPX(ref_size * 3.25);
		    w1.addExclude(w2).addExclude(w3).addExclude(w4);
		    w2.addExclude(w1).addExclude(w3).addExclude(w4);
		    w3.addExclude(w2).addExclude(w1).addExclude(w4);
		    w4.addExclude(w2).addExclude(w3).addExclude(w1);
		}
		
	  	nRunnable mode_run = new nRunnable() { public void run() {
	  		if (!build_flag)
	  			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	  				if (!rebuilding) rebuild(); }});
	  		build_flag = true;
	  	} };
	  	mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  	mode_filter.addEventChange(mode_run);
			filt_boo.addEventChange(mode_run);
			filt_boo_st.addEventChange(mode_run);
			fltr_count.addEventChange(mode_run);
			fltr_frame.addEventChange(mode_run);
		  	mode_transf.addEventChange(mode_run);
		  	trsf_AtBG.addEventChange(mode_run);
		  	trsf_BtfBG.addEventChange(mode_run);
		  	trsf_vecXY.addEventChange(mode_run);
		  	trsf_vecMD.addEventChange(mode_run);
	  	}});
		if (valid_params()) build_normal();
	}
	private boolean valid_params() {
		return (mode_filter.get() || 
				trsf_AtBG.get()   || 
				trsf_BtfBG.get()  || 
				trsf_vecXY.get()  || 
				trsf_vecMD.get() );
	}
	private boolean valid_pack(Macro_Packet p) {
		if  (!mode_filter.get() || 
				(p.isBang() && filt_bang.get()) ||
				(p.isBool() && filt_boo.get() && 
				(!filt_boo_st.get() || (p.asBool() == filt_boo_true.get())) ) ||
				((p.isInt() || p.isFloat()) && filt_num.get()) ||
				(p.isVec() && filt_vec.get())) {
			if (!fltr_count.get() && !fltr_frame.get()) {
				return true;
			} else {
				if (fltr_count.get()) {
					t_count++;
					if (t_count >= fltr_time_count.get()) {
						mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
							t_count = 0; }});
						return true;
					}
				} else if (fltr_frame.get()) {
					if (gui.app.global_frame_count >= fr_count + fltr_time_count.get()) {
						mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
							fr_count = gui.app.global_frame_count; }});
						return true;
					}
				}
			}
		}
		return false;
	}
	private int t_count = 0;
	private int fr_count = 0;
	public void build_normal() {
		if (!valid_params()) {
			param_view.set(true);
			mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
  				if (!rebuilding) rebuild(); }});
			return;
		}
		String dscr = "";
		if (trsf_AtBG.get()) {
			out1 = addOutput(2, "out");
			addEmptyS(1).addModel("MC_Element_SText", dscr);
			in1 = addInput(0, "in", new nRunnable() { public void run() {
				if (in1.lastPack() != null && valid_pack(in1.lastPack()))
					out1.sendBang();
			}});
		} else if (trsf_BtfBG.get()) {
			out1 = addOutput(2, "out");
			addEmptyS(1).addModel("MC_Element_SText", dscr);
			in1 = addInput(0, "in", new nRunnable() { public void run() {
				if (in1.lastPack() != null && valid_pack(in1.lastPack())) {
					if (in1.lastPack().isBang()) out1.sendBool(true);
					if (in1.lastPack().isBool()) out1.sendBang();
				}
			}});
		} else if (trsf_vecXY.get()) {
		    in1 = addInput(0, "v/x").addEventReceive(new nRunnable() { public void run() { 
		        if (in1.lastPack() != null && valid_pack(in1.lastPack()) && in1.lastPack().isVec() && 
		        			(in1.lastPack().asVec().x != vec.x || in1.lastPack().asVec().y != vec.y)) {
			        	vec.set(in1.lastPack().asVec());
			        	float m = vec.x; float d = vec.y;
		        		if (m != x) { x = m; out1.send(Macro_Packet.newPacketFloat(m)); }
		        		if (d != y) { y = d; out2.send(Macro_Packet.newPacketFloat(d)); }
		        } else if (in1.lastPack() != null && in1.lastPack().isFloat() && 
		        		in1.lastPack().asFloat() != x) {
			        	x = in1.lastPack().asFloat();
			        	view1.changeText(RConst.trimFlt(x)); 
			        	vec.set(x, y);
			        	out1.send(Macro_Packet.newPacketVec(vec));
		        }
		    } });
		    in2 = addInput(0, "y").addEventReceive(new nRunnable() { public void run() { 
			    	if (in2.lastPack() != null && valid_pack(in2.lastPack()) && in2.lastPack().isFloat() && 
			    			in2.lastPack().asFloat() != y) {
			    		y = in2.lastPack().asFloat();
			    		view2.changeText(RConst.trimFlt(y)); 
			    		vec.set(x, y);
			    		out1.send(Macro_Packet.newPacketVec(vec));
			    	}
		    } });
		    out1 = addOutput(2, "v/x");
		    out2 = addOutput(2, "y");
      
		    vec = new PVector(1, 0);
      
		    val_view1 = newStr("x", "x", "0");
		    val_view2 = newStr("y", "y", "0");
      
		    String t = val_view1.get();
		    if (t.length() > 0) {
		    		if (t.equals("0") || t.equals("0.0")) { x = 0; }
		    		else if (Rapp.parseFlt(t) != 0) { x = Rapp.parseFlt(t); }
		    }
		    t = val_view2.get();
		    if (t.length() > 0) {
			    	if (t.equals("0") || t.equals("0.0")) { y = 0; }
			    	else if (Rapp.parseFlt(t) != 0) { y = Rapp.parseFlt(t); }
		    }
		    vec.set(x, y);
		    view1 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
		    view1.setInfo("x");
		    view1.addEventFieldChange(new nRunnable() { public void run() { 
			    	String t = view1.getText();
			    	float a = x;
			    	if (t.length() > 0) {
			    		if (t.equals("0") || t.equals("0.0")) { x = 0; }
			    		else if (Rapp.parseFlt(t) != 0) { x = Rapp.parseFlt(t); }
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
			    		else if (Rapp.parseFlt(t) != 0) { y = Rapp.parseFlt(t); }
			    	}
			    	if (y != a) {
			    		//view2.changeText(RConst.trimStringFloat(y)); 
			    		vec.set(x, y);
			    		out1.send(Macro_Packet.newPacketVec(vec));
		    		}	
		    } });
		} else if (trsf_vecMD.get()) {
			in1 = addInput(0, "v/mag").addEventReceive(new nRunnable() { public void run() { 
				if (in1.lastPack() != null && valid_pack(in1.lastPack()) && in1.lastPack().isVec() && 
						(in1.lastPack().asVec().x != vec.x || in1.lastPack().asVec().y != vec.y)) {
					vec.set(in1.lastPack().asVec());
					float m = vec.mag(); float d = vec.heading();
					if (m != mag) { val_view1.set(RConst.trimFlt(m)); 
						mag = m; out1.send(Macro_Packet.newPacketFloat(m)); }
					if (d != dir) { val_view2.set(RConst.trimFlt(d)); 
						dir = d; out2.send(Macro_Packet.newPacketFloat(d)); }
				} else if (in1.lastPack() != null && in1.lastPack().isFloat() && 
						in1.lastPack().asFloat() != mag) {
					mag = in1.lastPack().asFloat();
					view1.changeText(RConst.trimFlt(mag)); 
					vec.set(mag, 0).rotate(dir);
					out1.send(Macro_Packet.newPacketVec(vec));
				}
			} });
			in2 = addInput(0, "dir").addEventReceive(new nRunnable() { public void run() { 
				if (in2.lastPack() != null && valid_pack(in2.lastPack()) && in2.lastPack().isFloat() && 
						in2.lastPack().asFloat() != dir) {
					dir = in2.lastPack().asFloat();
					view2.changeText(RConst.trimFlt(dir)); 
					vec.set(mag, 0).rotate(dir);
					out1.send(Macro_Packet.newPacketVec(vec));
				}
			} });
			out1 = addOutput(2, "v/mag");
			out2 = addOutput(2, "dir");
        
			vec = new PVector(1, 0);
        
			val_view1 = newStr("mag", "mag", "1");
			val_view2 = newStr("dir", "dir", "0");
        
			String t = val_view1.get();
			if (t.length() > 0) {
				if (t.equals("0") || t.equals("0.0")) { mag = 0; }
				else if (Rapp.parseFlt(t) != 0) { mag = Rapp.parseFlt(t); }
			}
			t = val_view2.get();
			if (t.length() > 0) {
				if (t.equals("0") || t.equals("0.0")) { dir = 0; }
				else if (Rapp.parseFlt(t) != 0) { dir = Rapp.parseFlt(t); }
			}
			vec.set(mag, 0).rotate(dir);
			view1 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
			view1.setInfo("mag");
	        view1.addEventFieldChange(new nRunnable() { public void run() { 
		        	String t = view1.getText();
		        	float a = mag;
		        	if (t.length() > 0) {
		        		if (t.equals("0") || t.equals("0.0")) { mag = 0; }
		        		else if (Rapp.parseFlt(t) != 0) { mag = Rapp.parseFlt(t); }
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
	        			else if (Rapp.parseFlt(t) != 0) { dir = Rapp.parseFlt(t); }
	        		}
	        		if (dir != a) {
	        			//view2.changeText(RConst.trimStringFloat(dir)); 
	        			vec.set(mag, 0).rotate(dir);
	        			out1.send(Macro_Packet.newPacketVec(vec));
	         	}
	        } });
		} else if (mode_filter.get()) {
			out1 = addOutput(2, "out");
			addEmptyS(1).addModel("MC_Element_SText", dscr);
			in1 = addInput(0, "in", new nRunnable() { public void run() {
				if (in1.lastPack() != null && valid_pack(in1.lastPack()))
					out1.send(in1.lastPack());
			}});
		}
	}
	public MTransform clear() {
		super.clear(); return this; }
}







class MColRGB extends Macro_Bloc {
	  static class MColRGB_Builder extends MAbstract_Builder {
		  MColRGB_Builder() { super("colRGB", "colRGB", "", "Data"); }
		  MColRGB build(Macro_Sheet s, sValueBloc b) { MColRGB m = new MColRGB(s, b); return m; }
	  }
Macro_Connexion in1,in2,in3,out1,out2,out3;
float r = 0, g = 0, b = 0;
int col = 0;
nLinkedWidget view1, view2, view3;
sInt val_view1, val_view2, val_view3; 
MColRGB(Macro_Sheet _sheet, sValueBloc _bloc) { 
  super(_sheet, "colRGB", "colRGB", _bloc); 
  
  in1 = addInput(0, "c/r").addEventReceive(new nRunnable() { public void run() { 
    if (in1.lastPack() != null && in1.lastPack().isCol() && 
        !in1.lastPack().equalsCol(col)) {
      col = in1.lastPack().asCol();
      float m = gui.app.red(col); float d = gui.app.green(col); float f = gui.app.blue(col);
      if (m != r) { r = m; out1.send(Macro_Packet.newPacketFloat(r)); }
      if (d != g) { g = d; out2.send(Macro_Packet.newPacketFloat(g)); }
      if (f != b) { b = f; out3.send(Macro_Packet.newPacketFloat(b)); }
      col = gui.app.color(r,g,b);
    } else if (in1.lastPack() != null && in1.lastPack().isFloat() && 
               in1.lastPack().asFloat() != g) {
      r = in1.lastPack().asFloat();
      view1.changeText(""+r); 
      col = gui.app.color(r,g,b);
      out1.send(Macro_Packet.newPacketCol(col));
    }
  } });
  in2 = addInput(0, "g").addEventReceive(new nRunnable() { public void run() { 
    if (in2.lastPack() != null && in2.lastPack().isFloat() && 
               in2.lastPack().asFloat() != g) {
      g = in2.lastPack().asFloat();
      view2.changeText(""+g); 
      col = gui.app.color(r,g,b);
      out1.send(Macro_Packet.newPacketCol(col));
    }
  } });
  in3 = addInput(0, "b").addEventReceive(new nRunnable() { public void run() { 
    if (in3.lastPack() != null && in3.lastPack().isFloat() && 
               in3.lastPack().asFloat() != b) {
      b = in3.lastPack().asFloat();
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





class MRandom extends Macro_Bloc { 
	  static class MRandom_Builder extends MAbstract_Builder {
		  MRandom_Builder() { super("rng", "Random", "", "Data"); }
		  MRandom build(Macro_Sheet s, sValueBloc b) { MRandom m = new MRandom(s, b); return m; }
	  }
  Macro_Connexion in, out;
  float min = 0, max = 1;
  nLinkedWidget view1, view2;
  sStr val_view1, val_view2; 
  MRandom(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "rng", "rng", _bloc); 
    
    in = addInput(0, "bang").setFilterBang().addEventReceive(new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBang()) {
        out.send(Macro_Packet.newPacketFloat(gui.app.random(min, max))); } } });
    
    out = addOutput(1, "out")
      .setDefFloat();
      
    val_view1 = newStr("min", "min", "0");
    val_view2 = newStr("max", "max", "1");
    
    String t = val_view1.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { min = 0; }
      else if (Rapp.parseFlt(t) != 0) { min = Rapp.parseFlt(t); }
    }
    t = val_view2.get();
    if (t.length() > 0) {
      if (t.equals("0") || t.equals("0.0")) { max = 0; }
      else if (Rapp.parseFlt(t) != 0) { max = Rapp.parseFlt(t); }
    }
    view1 = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
    view1.setInfo("min");
    view1.addEventFieldChange(new nRunnable() { public void run() { 
      String t = view1.getText();
      if (t.length() > 0) {
        if (t.equals("0") || t.equals("0.0")) { min = 0; }
        else if (Rapp.parseFlt(t) != 0) { min = Rapp.parseFlt(t); }
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
        else if (Rapp.parseFlt(t) != 0) { max = Rapp.parseFlt(t); }
      }
      if (min > max) { float a = min; min = max; max = a; }
      //view1.setText(RConst.trimStringFloat(min)); 
      //view2.setText(RConst.trimStringFloat(max)); 
    } });
  }
  public MRandom clear() {
    super.clear(); return this; }
}








class MVar extends MBasic { 
  static class MVar_Builder extends MAbstract_Builder {
	  MVar_Builder() { super("var", "Variable", "store last received data, send copy at bang", "Data"); }
	  MVar build(Macro_Sheet s, sValueBloc b) { MVar m = new MVar(s, b); return m; }
  }
  sValue cible; 
  sStr var_type; sBoo setup_send, change_send, all_send;
  sBoo bval; sInt ival; sFlt fval; sStr sval; sVec vval; sRun rval; sCol cval; sObj oval;
  nRunnable in_run, val_run;
  boolean btmp; int itmp; float ftmp; String stmp = ""; 
  PVector vtmp = new PVector(); int ctmp = 0;

  Macro_Connexion in, out;
  nWidget val_widget;
  Macro_Element val_elem;
  MVar(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "var", _bloc); }

  	void init() {
		setup_send = newBoo("stp_snd", "stp_snd", false);
		change_send = newBoo("update_send", "update_send", false);
		all_send = newBoo("all_send", "all_send", true);
		var_type = newStr("var_type");
		ival = newInt(0, "int_value");
		fval = newFlt(0, "float_value");
		bval = newBoo(false, "bool_value");
		sval = newStr("str_value");
		vval = newVec("vec_value");
		cval = newCol("col_value");
		rval = newRun("run_value");
		oval = newObj("obj_value");
  	}

    void build_normal() { 
		in = addInput(0, "set", new nRunnable() { public void run() {
			if (in.lastPack() != null)
				if (in.lastPack().isBang() && cible != null) out.send(cible.asPacket());
				if (in.lastPack().isBool()) { 
					bval.set(in.lastPack().asBool()); chooseValue(bval); }
				else if (in.lastPack().isFloat()) { 
					fval.set(in.lastPack().asFloat()); chooseValue(fval); }
				else if (in.lastPack().isInt()) { 
					ival.set(in.lastPack().asInt()); chooseValue(ival); }
				else if (in.lastPack().isStr()) { 
					sval.set(in.lastPack().asStr()); chooseValue(sval); }
				else if (in.lastPack().isCol()) { 
					cval.set(in.lastPack().asCol()); chooseValue(cval); }
				else if (in.lastPack().isVec()) { 
					vval.set(in.lastPack().asVec()); chooseValue(vval); }
		} });
		addEmptyS(1).addCtrlModel("MC_Element_SButton", "send")
			.setRunnable(new nRunnable() { public void run() {
				if (cible != null) out.send(cible.asPacket()); } }).getDrawer()
		.addLinkedModel("MC_Element_MiniButton", "A")
			.setLinkedValue(all_send).setPX(-ref_size*0.25)
			.setInfo("Send on all inputs").getDrawer()
		.addLinkedModel("MC_Element_MiniButton", "B")
			.setPX(-ref_size*9 / 16)
			.setInfo("Send on bang");
		
		out = addOutput(2, "out");
		out.elem.addLinkedModel("MC_Element_MiniButton", "C")
			.setLinkedValue(change_send)
			.setInfo("Send on value change");
		out.elem.addLinkedModel("MC_Element_MiniButton", "S")
			.setLinkedValue(setup_send).setPX(-ref_size*0.25)
			.setInfo("Send at building");  
		
		
		addEmptyS(1); 
		addEmptyS(2).addCtrlModel("MC_Element_SButton", "Set")
		.setRunnable(new nRunnable() { public void run() {
		  if (cible != null) cible.pop_panel(mmain().screen_gui, mmain().inter.taskpanel);
		} }); 
		val_elem = addEmptyS(0);

	    if      (var_type.get().equals("flt")) chooseValue(fval);
	    else if (var_type.get().equals("int")) chooseValue(ival);
	    else if (var_type.get().equals("boo")) chooseValue(bval);
	    else if (var_type.get().equals("str")) chooseValue(sval);
	    else if (var_type.get().equals("run")) chooseValue(rval);
	    else if (var_type.get().equals("vec")) chooseValue(vval);
	    else if (var_type.get().equals("col")) chooseValue(cval);
	    else if (var_type.get().equals("obj")) chooseValue(oval);
    }
  
    void build_param() { 
		addEmpty(1); addEmpty(2);
    	addEmptyXL(0).addTrigSelector(1, "Boo", new nRunnable() { public void run() {
    		bval.set(false); chooseValue(bval);
    	}}).addTrigSelector(2, "Int", new nRunnable() { public void run() {
    		ival.set(0); chooseValue(ival);
    	}}).addTrigSelector(3, "Flt", new nRunnable() { public void run() {
    		fval.set(0); chooseValue(fval);
    	}}).addTrigSelector(4, "Str", new nRunnable() { public void run() {
    		sval.set(""); chooseValue(sval);
    	}}).addTrigSelector(5, "Vec", new nRunnable() { public void run() {
    		vval.set(0, 0); chooseValue(vval);
    	}}).addTrigSelector(6, "Col", new nRunnable() { public void run() {
    		cval.set(0); chooseValue(cval);
    	}}); 
    	build_normal();
    }

	void clearValue() {
		if (cible != null && val_run != null) cible.removeEventChange(val_run);
		if (cible != null && val_run != null) cible.removeEventAllChange(val_run);
		if (in_run != null) in.removeEventReceive(in_run);
		in_run = null;
		cible = null;
		val_run = null;
		if (val_widget != null) val_widget.clear();
	}
  
    void chooseValue(sValue v) {
        mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		    if (v != null) {
		    	clearValue();
			    cible = v; 
			    val_widget = addLinkedLWidget(val_elem, v);
			    val_widget.toLayerTop();
			    var_type.set(cible.type);
			    if      (cible.type.equals("flt")) setValue((sFlt)cible);
			    else if (cible.type.equals("int")) setValue((sInt)cible);
			    else if (cible.type.equals("boo")) setValue((sBoo)cible);
			    else if (cible.type.equals("str")) setValue((sStr)cible);
			    else if (cible.type.equals("run")) setValue((sRun)cible);
			    else if (cible.type.equals("vec")) setValue((sVec)cible);
			    else if (cible.type.equals("col")) setValue((sCol)cible);
			    else if (cible.type.equals("obj")) setValue((sObj)cible);
			    if (setup_send.get()) out.send(cible.asPacket());
		    }
        } } );
  	}
  	
  void setValue(sRun v) {
    rval = v;
    val_run = new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketBang()); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBang()) { 
        rval.doEvent(false); 
        rval.run(); 
        rval.doEvent(true); 
        if (all_send.get()) out.send(Macro_Packet.newPacketBang()); 
      }
    } };
    v.addEventAllChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sObj v) {
	    oval = v;
	    val_run = new nRunnable() { public void run() { 
	    		if (change_send.get()) out.send(oval.asPacket()); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isBang()) 
	    	  	out.send(oval.asPacket()); 
	      if (in.lastPack() != null && in.lastPack().isObj()) {
	    	  	oval.set(in.lastPack().asObj());
	    		if (all_send.get()) out.send(oval.asPacket());
	      }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	  }
  void setValue(sFlt v) {
    fval = v;
    ftmp = v.get();
    
    val_run = new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketFloat(fval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isFloat() && ftmp != in.lastPack().asFloat()) { 
        ftmp = in.lastPack().asFloat();
        fval.set(in.lastPack().asFloat()); }
	  if (all_send.get()) out.send(Macro_Packet.newPacketFloat(fval.get()));
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sInt v) {
    ival = v;
    itmp = v.get();
    val_run = new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketInt(ival.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isInt() && itmp != in.lastPack().asInt()) { 
        itmp = in.lastPack().asInt();
        ival.set(in.lastPack().asInt()); }
	  if (all_send.get()) out.send(Macro_Packet.newPacketInt(ival.get()));
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sBoo v) {
    bval = v;
    btmp = v.get();
    val_run = new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketBool(bval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool() && !(btmp == in.lastPack().asBool())) { 
        btmp = in.lastPack().asBool();
        bval.set(in.lastPack().asBool());
		if (change_send.get()) out.send(Macro_Packet.newPacketBool(bval.get())); }
      if (in.lastPack() != null && in.lastPack().isBang()) { 
        btmp = !btmp;
        bval.set(!bval.get()); }
	  if (all_send.get()) out.send(Macro_Packet.newPacketBool(bval.get()));
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sStr v) {
    sval = v;
    stmp = RConst.copy(v.get());
    val_run = new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketStr(sval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isStr() && !stmp.equals(in.lastPack().asStr())) { 
        stmp = RConst.copy(in.lastPack().asStr());
        sval.set(in.lastPack().asStr()); }
	  if (all_send.get()) out.send(Macro_Packet.newPacketStr(sval.get()));
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }

  void setValue(sVec v) {
    vval = v;
    vtmp.set(v.get());
    val_run = new nRunnable() { public void run() { 
//      mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketVec(vval.get())); 
//    	}}); 
      }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isVec() && !in.lastPack().equalsVec(vtmp)) { 
        vtmp.set(in.lastPack().asVec());
        vval.set(in.lastPack().asVec()); 
      }
		if (all_send.get()) out.send(Macro_Packet.newPacketVec(vval.get())); 
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sCol v) {
    cval = v;
    ctmp = v.get();
    val_run = new nRunnable() { public void run() { 
//      mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
    		if (change_send.get()) out.send(Macro_Packet.newPacketCol(cval.get()));
//    	  }}); 
      }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isCol() && !in.lastPack().equalsCol(ctmp)) { 
        ctmp = in.lastPack().asCol();
        cval.set(in.lastPack().asCol()); 
      }
		if (all_send.get()) out.send(Macro_Packet.newPacketCol(cval.get()));
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  
  public MVar clear() {
    super.clear(); 
    return this; }
  public MVar toLayerTop() {
    super.toLayerTop(); 
    return this; }
}







class MValue extends MBasic { 
  static class MValue_Builder extends MAbstract_Builder {
      MValue_Builder() { super("value", "sValues", "access sValues", "Data"); }
      MValue build(Macro_Sheet s, sValueBloc b) { MValue m = new MValue(s, b); return m; }
    }
  sValue cible; sValueBloc bloc_cible;
  sStr val_cible, val_bloc, val_depth, var_type; sBoo setup_send, panel_open;
  Macro_Connexion in, out;
  nWidget val_widget;
  Macro_Element val_elem;
  
  sBoo bval; sInt ival; sFlt fval; sStr sval; sVec vval; sRun rval; sCol cval; sObj oval;
  nRunnable in_run, val_run;
  boolean btmp; int itmp; float ftmp; String stmp = ""; 
  PVector vtmp = new PVector(); int ctmp = 0;

  MValue(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "value", _bloc); }

  void init() {
	  val_cible = newStr("cible", "cible", "");
	  val_bloc = newStr("bloc", "bloc", sheet.value_bloc.ref);
	  val_depth = newStr("depth", "depth", "/");
	  if (sheet == mmain()) val_depth.set("/");
	  var_type = newStr("type", "type", "");
	  setup_send = newBoo("stp_snd", "stp_snd", false);
	  panel_open = newBoo("pan_opn", "pan_opn", false);
  }
  void build_param() { 
	nDrawer d = addEmptyS(0);
  	d.addCtrlModel("MC_Element_SButton", "pick")
	.setRunnable(new nRunnable() { public void run() {
		if (val_depth.get().equals("/")) bloc_cible = sheet.value_bloc;
	    else if (val_depth.get().equals("..") && sheet != mmain()) 
			bloc_cible = sheet.sheet.value_bloc;
     	new nBlockPicker(mmain().screen_gui, mmain().inter.taskpanel, val_bloc, bloc_cible, true)
        .addEventChoose(new nRunnable() { public void run() { 
        mmain().inter.addEventNextFrame( new nRunnable() { public void run() {
		  if (val_bloc.get().equals(sheet.value_bloc.ref)) 
			  bloc_cible = sheet.value_bloc;
		  else if (sheet != mmain() && val_bloc.get().equals(sheet.sheet.value_bloc.ref)) 
			  bloc_cible = sheet.sheet.value_bloc;
		  else {
			  if (val_depth.get().equals("/")) bloc_cible = sheet.value_bloc;
			  else if (val_depth.get().equals("..") && sheet != mmain()) 
				  bloc_cible = sheet.sheet.value_bloc;
			  if (bloc_cible != null && bloc_cible.getBloc(val_bloc.get()) != null) 
				  bloc_cible = bloc_cible.getBloc(val_bloc.get());
		  }
		  val_bloc.set(bloc_cible.ref);
	      clearValue();
		} } ); } } );
	} });
  	d.addCtrlModel("MC_Element_MiniButton", "/")
  	.setRunnable(new nRunnable() { public void run() {
		if (!val_depth.get().equals("/") || sheet == mmain()) {
			val_depth.set("/"); bloc_cible = sheet.value_bloc; }
		else if (sheet != mmain()) {
			val_depth.set(".."); bloc_cible = sheet.sheet.value_bloc; }
		val_bloc.set(bloc_cible.ref);
		clearValue();
	} });
    addEmpty(2);
    addEmptyL(1).addWatcherModel("MC_Element_Field")
    	.setLinkedValue(val_bloc).setInfo("selected value searching bloc ref")
    .getDrawer().addWatcherModel("MC_Element_SText")
		.setLinkedValue(val_depth).setPX(-ref_size*0.625F).setSX(ref_size*1.0F);
    
    build_normal();
  }

  void build_normal() { 
    addEmptyS(0).addCtrlModel("MC_Element_SButton", "pick")
	.setRunnable(new nRunnable() { public void run() {
		if (bloc_cible != null ) {
		    new nValuePicker(mmain().screen_gui, mmain().inter.taskpanel, val_cible, bloc_cible, true)
		    .addEventChoose(new nRunnable() { public void run() { setValue(); } } );
		}
    } });
    addEmpty(2);
    addEmptyL(1).addWatcherModel("MC_Element_Field")
    	.setLinkedValue(val_cible).setInfo("selected value ref");
    
	addEmptyS(1).addCtrlModel("MC_Element_SButton", "Get")
    .setRunnable(new nRunnable() { public void run() {
    	if (cible != null) out.send(cible.asPacket()); } })
    .getDrawer().addLinkedModel("MC_Element_MiniButton", "st")
		.setLinkedValue(setup_send); 
	out = addOutput(2, "out");
    in = addInput(0, "in", new nRunnable() { public void run() {
        if (in.lastPack() != null && in.lastPack().isBang() && 
        			cible != null && !cible.type.equals("run")) 
        		out.send(cible.asPacket());
      } });

    addEmpty(1); 
    addEmptyS(2).addCtrlModel("MC_Element_SButton", "Set")
    .setRunnable(new nRunnable() { public void run() {
      if (cible != null) cible.pop_panel(mmain().screen_gui, mmain().inter.taskpanel)
		.addEventClose(new nRunnable() { public void run() { panel_open.set(false); } } );
    } }); 
    val_elem = addEmptyL(0);
    
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
    	setValue(); } } );
  }
  
  void setValue() {
	  if (val_bloc.get().equals(sheet.value_bloc.ref)) 
		  bloc_cible = sheet.value_bloc;
	  else if (sheet != mmain() && val_bloc.get().equals(sheet.sheet.value_bloc.ref)) 
		  bloc_cible = sheet.sheet.value_bloc;
	  else {
		  if (val_depth.get().equals("/")) bloc_cible = sheet.value_bloc;
		  else if (val_depth.get().equals("..") && sheet != mmain()) 
			  bloc_cible = sheet.sheet.value_bloc;
	    
		  if (bloc_cible != null && bloc_cible.getBloc(val_bloc.get()) != null) 
			  bloc_cible = bloc_cible.getBloc(val_bloc.get());
	  }
	  if (bloc_cible != null) {
	      cible = bloc_cible.getValue(val_cible.get());
	      setValue(cible);
	  }
  }

  void clearValue() {
	  val_cible.set("");
	  if (cible != null && val_run != null) cible.removeEventChange(val_run);
	  if (cible != null && val_run != null) cible.removeEventAllChange(val_run);
    if (in_run != null) in.removeEventReceive(in_run);
    in_run = null;
    cible = null;
    val_run = null;
    if (val_widget != null) val_widget.clear();
  }

  void setValue(sValue v) {
    if (v != null) {
    		clearValue();
	    val_cible.set(v.ref);
	    cible = v; 
	    val_widget = addLinkedLWidget(val_elem, v);
	    if      (cible.type.equals("flt")) setValue((sFlt)cible);
	    else if (cible.type.equals("int")) setValue((sInt)cible);
	    else if (cible.type.equals("boo")) setValue((sBoo)cible);
	    else if (cible.type.equals("str")) setValue((sStr)cible);
	    else if (cible.type.equals("run")) setValue((sRun)cible);
	    else if (cible.type.equals("vec")) setValue((sVec)cible);
	    else if (cible.type.equals("col")) setValue((sCol)cible);
	    else if (cible.type.equals("obj")) setValue((sObj)cible);
	    if (setup_send.get()) out.send(cible.asPacket());
	    if (panel_open.get()) cible.pop_panel(mmain().screen_gui, mmain().inter.taskpanel)
      		.addEventClose(new nRunnable() { public void run() { panel_open.set(false); } } );
    }
  }
  	int last_send_framenb = 0;
  void setValue(sRun v) {
    rval = v;
    val_run = new nRunnable() { public void run() { 
    		if (last_send_framenb + 1 < gui.app.global_frame_count)
    			out.send(Macro_Packet.newPacketBang()); 
    		last_send_framenb = gui.app.global_frame_count; }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBang()) { 
        rval.run(); 
      }
    } };
    v.addEventAllChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sObj v) {
	    oval = v;
	    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketBang()); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isBang()) 
	    	  	out.send(Macro_Packet.newPacketBang()); 
	      if (in.lastPack() != null && in.lastPack().isObj()) 
	    	  	oval.set(in.lastPack().asObj());
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
}
  void setValue(sFlt v) {
    fval = v;
    ftmp = v.get();
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(fval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isFloat() && ftmp != in.lastPack().asFloat()) { 
        ftmp = in.lastPack().asFloat();
        fval.set(in.lastPack().asFloat()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sInt v) {
    ival = v;
    itmp = v.get();
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketInt(ival.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isInt() && itmp != in.lastPack().asInt()) { 
        itmp = in.lastPack().asInt();
        ival.set(in.lastPack().asInt()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sBoo v) {
    bval = v;
    btmp = v.get();
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketBool(bval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isBool() && !(btmp == in.lastPack().asBool())) { 
        btmp = in.lastPack().asBool();
        bval.set(in.lastPack().asBool()); }
      if (in.lastPack() != null && in.lastPack().isBang()) { 
        btmp = !btmp;
        bval.set(!bval.get()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sStr v) {
    sval = v;
    stmp = RConst.copy(v.get());
    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketStr(sval.get())); }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isStr() && !stmp.equals(in.lastPack().asStr())) { 
        stmp = RConst.copy(in.lastPack().asStr());
        sval.set(in.lastPack().asStr()); }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }

  void setValue(sVec v) {
    vval = v;
    vtmp.set(v.get());
    val_run = new nRunnable() { public void run() { 
      mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
      out.send(Macro_Packet.newPacketVec(vval.get())); }}); 
      }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isVec() && !in.lastPack().equalsVec(vtmp)) { 
        vtmp.set(in.lastPack().asVec());
        vval.set(in.lastPack().asVec()); 
      }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  void setValue(sCol v) {
    cval = v;
    ctmp = v.get();
    val_run = new nRunnable() { public void run() { 
      mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
      out.send(Macro_Packet.newPacketCol(cval.get()));}}); 
      }};
    in_run = new nRunnable() { public void run() { 
      if (in.lastPack() != null && in.lastPack().isCol() && !in.lastPack().equalsCol(ctmp)) { 
        ctmp = in.lastPack().asCol();
        cval.set(in.lastPack().asCol()); 
      }
    } };
    v.addEventChange(val_run);
    in.addEventReceive(in_run);
  }
  public MValue clear() {
	    if (val_run != null && cible != null) cible.removeEventChange(val_run);
	    if (val_run != null && cible != null) cible.removeEventAllChange(val_run);
    super.clear(); 
    return this; }
  public MValue toLayerTop() {
    super.toLayerTop(); 
    return this; }
}













//class MVecXY extends Macro_Bloc {
//	  static class MVecXY_Builder extends MAbstract_Builder {
//		  MVecXY_Builder() { super("vecXY", "vecXY", "", "Data"); }
//		  MVecXY build(Macro_Sheet s, sValueBloc b) { MVecXY m = new MVecXY(s, b); return m; }
//	  }
//  Macro_Connexion in1,in2,out1,out2;
//  float x = 0, y = 0;
//  PVector vec;
//  nLinkedWidget view1, view2;
//  sStr val_view1, val_view2; 
//  MVecXY(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "vecXY", "vecXY", _bloc); 
//    in1 = addInput(0, "v/x").addEventReceive(new nRunnable() { public void run() { 
//      if (in1.lastPack() != null && in1.lastPack().isVec() && 
//          (in1.lastPack().asVec().x != vec.x || in1.lastPack().asVec().y != vec.y)) {
//        vec.set(in1.lastPack().asVec());
//        float m = vec.x; float d = vec.y;
//        if (m != x) { x = m; out1.send(Macro_Packet.newPacketFloat(m)); }
//        if (d != y) { y = d; out2.send(Macro_Packet.newPacketFloat(d)); }
//      } else if (in1.lastPack() != null && in1.lastPack().isFloat() && 
//                 in1.lastPack().asFloat() != x) {
//        x = in1.lastPack().asFloat();
//        view1.changeText(RConst.trimFlt(x)); 
//        vec.set(x, y);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    in2 = addInput(0, "y").addEventReceive(new nRunnable() { public void run() { 
//      if (in2.lastPack() != null && in2.lastPack().isFloat() && 
//                 in2.lastPack().asFloat() != y) {
//        y = in2.lastPack().asFloat();
//        view2.changeText(RConst.trimFlt(y)); 
//        vec.set(x, y);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    out1 = addOutput(1, "v/x");
//    out2 = addOutput(1, "y");
//    
//    vec = new PVector(1, 0);
//    
//    val_view1 = newStr("x", "x", "0");
//    val_view2 = newStr("y", "y", "0");
//    
//    String t = val_view1.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { x = 0; }
//      else if (Rapp.parseFlt(t) != 0) { x = Rapp.parseFlt(t); }
//    }
//    t = val_view2.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { y = 0; }
//      else if (Rapp.parseFlt(t) != 0) { y = Rapp.parseFlt(t); }
//    }
//    vec.set(x, y);
//    view1 = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
//    view1.setInfo("x");
//    view1.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = view1.getText();
//      float a = x;
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { x = 0; }
//        else if (Rapp.parseFlt(t) != 0) { x = Rapp.parseFlt(t); }
//      }
//      if (x != a) {
//        //view1.changeText(RConst.trimStringFloat(x)); 
//        vec.set(x, y);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view2);
//    view2.setInfo("y");
//    view2.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = view2.getText();
//      float a = y;
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { y = 0; }
//        else if (Rapp.parseFlt(t) != 0) { y = Rapp.parseFlt(t); }
//      }
//      if (y != a) {
//        //view2.changeText(RConst.trimStringFloat(y)); 
//        vec.set(x, y);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    mmain().inter.addEventNextFrame(new nRunnable() { public void run() { //add un swtch start send
//      //out1.send(Macro_Packet.newPacketVec(vec));
//    } });
//  }
//  public MVecXY clear() {
//    super.clear(); return this; }
//}
//class MVecMD extends Macro_Bloc {
//	  static class MVecMD_Builder extends MAbstract_Builder {
//		  MVecMD_Builder() { super("vecMD", "vecMD", "", "Data"); }
//		  MVecMD build(Macro_Sheet s, sValueBloc b) { MVecMD m = new MVecMD(s, b); return m; }
//	  }
//  Macro_Connexion in1,in2,out1,out2;
//  float mag = 1, dir = 0;
//  PVector vec;
//  nLinkedWidget view1, view2;
//  sStr val_view1, val_view2; 
//  MVecMD(Macro_Sheet _sheet, sValueBloc _bloc) { 
//    super(_sheet, "vecMD", "vecMD", _bloc); 
//    
//    in1 = addInput(0, "v/mag").addEventReceive(new nRunnable() { public void run() { 
//      if (in1.lastPack() != null && in1.lastPack().isVec() && 
//          (in1.lastPack().asVec().x != vec.x || in1.lastPack().asVec().y != vec.y)) {
//        vec.set(in1.lastPack().asVec());
//        float m = vec.mag(); float d = vec.heading();
//        if (m != mag) { val_view1.set(RConst.trimFlt(m)); 
//        				mag = m; out1.send(Macro_Packet.newPacketFloat(m)); }
//        if (d != dir) { val_view2.set(RConst.trimFlt(d)); 
//						dir = d; out2.send(Macro_Packet.newPacketFloat(d)); }
//      } else if (in1.lastPack() != null && in1.lastPack().isFloat() && 
//                 in1.lastPack().asFloat() != mag) {
//        mag = in1.lastPack().asFloat();
//        view1.changeText(RConst.trimFlt(mag)); 
//        vec.set(mag, 0).rotate(dir);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    in2 = addInput(0, "dir").addEventReceive(new nRunnable() { public void run() { 
//      if (in2.lastPack() != null && in2.lastPack().isFloat() && 
//                 in2.lastPack().asFloat() != dir) {
//        dir = in2.lastPack().asFloat();
//        view2.changeText(RConst.trimFlt(dir)); 
//        vec.set(mag, 0).rotate(dir);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    out1 = addOutput(1, "v/mag");
//    out2 = addOutput(1, "dir");
//    
//    vec = new PVector(1, 0);
//    
//    val_view1 = newStr("mag", "mag", "1");
//    val_view2 = newStr("dir", "dir", "0");
//    
//    String t = val_view1.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { mag = 0; }
//      else if (Rapp.parseFlt(t) != 0) { mag = Rapp.parseFlt(t); }
//    }
//    t = val_view2.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { dir = 0; }
//      else if (Rapp.parseFlt(t) != 0) { dir = Rapp.parseFlt(t); }
//    }
//    vec.set(mag, 0).rotate(dir);
//    view1 = addEmptyS(0).addLinkedModel("MC_Element_SField").setLinkedValue(val_view1);
//    view1.setInfo("mag");
//    view1.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = view1.getText();
//      float a = mag;
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { mag = 0; }
//        else if (Rapp.parseFlt(t) != 0) { mag = Rapp.parseFlt(t); }
//      }
//      if (mag != a) {
//        //view1.changeText(RConst.trimStringFloat(mag)); 
//        vec.set(mag, 0).rotate(dir);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view2);
//    view2.setInfo("dir");
//    view2.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = view2.getText();
//      float a = dir;
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { dir = 0; }
//        else if (Rapp.parseFlt(t) != 0) { dir = Rapp.parseFlt(t); }
//      }
//      if (dir != a) {
//        //view2.changeText(RConst.trimStringFloat(dir)); 
//        vec.set(mag, 0).rotate(dir);
//        out1.send(Macro_Packet.newPacketVec(vec));
//      }
//    } });
//    
//    mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//      out1.send(Macro_Packet.newPacketVec(vec));
//    } });
//  }
//  public MVecMD clear() {
//    super.clear(); return this; }
//}
//
//
//
//
//
//class MVecCtrl extends Macro_Bloc { 
//  void setValue(sValue v) { 
//    if (v.type.equals("vec")) {
//      if (val_run != null && cible != null) cible.removeEventChange(val_run);
//      if (in1_run != null) in1.removeEventReceive(in1_run);
//      if (in2_run != null) in2.removeEventReceive(in2_run);
//      val_cible.set(v.ref);
//      cible = v; val_field.setLinkedValue(cible);
//      val_field.setLook(gui.theme.getLook("MC_Element_Field"));
//      vval = (sVec)cible;
//      out.send(Macro_Packet.newPacketVec(vval.get()));
//      val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketVec(vval.get())); }};
//      in1_run = new nRunnable() { public void run() { 
//        if (in1.lastPack() != null && in1.lastPack().isBang()) { 
//          if (valMAG.get()) {
//            PVector p = new PVector().set(vval.get());
//            p.setMag(p.mag() + mod_f);
//            vval.set(p);
//          } else if (valROT.get()) {
//            PVector p = new PVector(vval.get().mag(), 0);
//            p.rotate(vval.get().heading() + mod_f);
//            vval.set(p);
//          } else if (valADD.get()) {
//            PVector p = new PVector().set(vval.get());
//            p.x += mod_vec.x; p.y += mod_vec.y;
//            vval.set(p);
//          } 
//        }
//      } };
//      in2_run = new nRunnable() { public void run() { 
//        if (in2.lastPack() != null && in2.lastPack().isBang()) { 
//          if (valMAG.get()) {
//            PVector p = new PVector().set(vval.get());
//            p.setMag(p.mag() - mod_f);
//            vval.set(p);
//          } else if (valROT.get()) {
//            PVector p = new PVector(vval.get().mag(), 0);
//            p.rotate(vval.get().heading() - mod_f);
//            vval.set(p);
//          } else if (valADD.get()) {
//            PVector p = new PVector().set(vval.get());
//            p.x -= mod_vec.x; p.y -= mod_vec.y;
//            vval.set(p); //logln("setvec");
//          } 
//        }
//      } };
//      v.addEventChange(val_run);
//      in1.addEventReceive(in1_run);
//      in2.addEventReceive(in2_run);
//    }
//  }
//  nRunnable val_run, in1_run, in2_run;
//  sVec vval;
//  Macro_Connexion in1, in2, in_m, out;
//  sStr val_cible; 
//  sBoo search_folder;
//  sValue cible;
//  nLinkedWidget ref_field, search_view;
//  nWatcherWidget val_field;
//  nLinkedWidget widgMAG, widgROT, widgADD; 
//  sBoo valMAG, valROT, valADD;
//  float mod_f = 0; PVector mod_vec;
//  nLinkedWidget mod_view1, mod_view2;
//  sStr val_mod1, val_mod2; 
//  MVecCtrl(Macro_Sheet _sheet, sValueBloc _bloc, sValue v) { 
//    super(_sheet, "vecCtrl", "vecCtrl", _bloc); 
//    
//    val_cible = newStr("cible", "cible", "");
//    search_folder = newBoo("search_folder", "search_folder", false);
//    mod_vec = new PVector();
//    val_mod1 = newStr("mod1", "mod1", "0");
//    String t = val_mod1.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { mod_f = 0; mod_vec.x = 0; }
//      else if (Rapp.parseFlt(t) != 0) { mod_f = Rapp.parseFlt(t); mod_vec.x = mod_f; }
//    }
//    val_mod2 = newStr("mod2", "mod2", "0");
//    t = val_mod2.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { mod_vec.y = 0; }
//      else if (Rapp.parseFlt(t) != 0) { mod_vec.y = Rapp.parseFlt(t); }
//    }
//    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
//    ref_field.setInfo("value");
//    
//    val_cible.addEventChange(new nRunnable(this) { public void run() { 
//      get_cible(); } } );
//    
//    search_view = ref_field.getDrawer().addLinkedModel("MC_Element_MiniButton").setLinkedValue(search_folder);
//    search_folder.addEventChange(new nRunnable(this) { public void run() { 
//      get_cible(); } } );
//    
//    val_field = addEmptyL(0).addWatcherModel("MC_Element_Text");
//    addEmpty(1); addEmpty(1);
//    
//    in1 = addInput(0, "+").setFilterBang();
//    in2 = addInput(0, "-").setFilterBang();
//    in_m = addInput(0, "modifier").addEventReceive(new nRunnable() { public void run() { 
//      if (in_m.lastPack() != null && in_m.lastPack().isFloat() && 
//          in_m.lastPack().asFloat() != mod_f) {
//        mod_f = in_m.lastPack().asFloat(); 
//        mod_view1.setText(RConst.trimFlt(mod_f)); 
//        mod_view2.setText("-"); 
//      } else if (in_m.lastPack() != null && in_m.lastPack().isVec() && 
//          !in_m.lastPack().equalsVec(mod_vec)) {
//        mod_vec.set(in_m.lastPack().asVec()); 
//        mod_view1.setText(RConst.trimFlt(mod_vec.x)); 
//        mod_view2.setText(RConst.trimFlt(mod_vec.y)); 
//      }
//    } });
//    
//    in1_run = new nRunnable() { public void run() { 
//      if (in1.lastPack() != null && in1.lastPack().isBang()) { 
//        if (valMAG.get()) {
//          PVector p = new PVector(1, 0);
//          p.setMag(mod_f);
//          out.send(Macro_Packet.newPacketVec(p));
//        } else if (valROT.get()) {
//          PVector p = new PVector(1, 0);
//          p.rotate(mod_f);
//          out.send(Macro_Packet.newPacketVec(p));
//        } else if (valADD.get()) {
//          out.send(Macro_Packet.newPacketVec(mod_vec));
//        } 
//      }
//    } };
//    in2_run = new nRunnable() { public void run() { 
//      if (in2.lastPack() != null && in2.lastPack().isBang()) { 
//        if (valMAG.get()) {
//          PVector p = new PVector();
//          p.setMag( - mod_f);
//          out.send(Macro_Packet.newPacketVec(p));
//        } else if (valROT.get()) {
//          PVector p = new PVector(1, 0);
//          p.rotate( - mod_f);
//          out.send(Macro_Packet.newPacketVec(p));
//        } else if (valADD.get()) {
//          PVector p = new PVector();
//          p.x -= mod_vec.x; p.y -= mod_vec.y;
//          out.send(Macro_Packet.newPacketVec(p));
//        } 
//      }
//    } };
//    in1.addEventReceive(in1_run);
//    in2.addEventReceive(in2_run);
//    
//    
//    out = addOutput(1, "out");
//    
//    mod_view1 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod1);
//    mod_view1.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = mod_view1.getText();
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { mod_f = 0; mod_vec.x = 0; }
//        else if (Rapp.parseFlt(t) != 0) { mod_f = Rapp.parseFlt(t); mod_vec.x = mod_f; }
//      }
//    } });
//    mod_view2 = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod2);
//    mod_view2.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = mod_view2.getText();
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { mod_vec.y = 0; }
//        else if (Rapp.parseFlt(t) != 0) { mod_vec.y = Rapp.parseFlt(t); }
//      }
//    } });
//    
//    valMAG = newBoo("valMAG", "valMAG", false);
//    valROT = newBoo("valROT", "valROT", false);
//    valADD = newBoo("valADD", "valADD", false);
//    
//    Macro_Element e = addEmptyL(0);
//    widgMAG = e.addLinkedModel("MC_Element_Button_Selector_1", "Mag").setLinkedValue(valMAG);
//    widgROT = e.addLinkedModel("MC_Element_Button_Selector_2", "Rot").setLinkedValue(valROT);
//    widgADD = e.addLinkedModel("MC_Element_Button_Selector_4", "Add").setLinkedValue(valADD);
//    widgMAG.addExclude(widgROT).addExclude(widgADD);
//    widgROT.addExclude(widgMAG).addExclude(widgADD);
//    widgADD.addExclude(widgROT).addExclude(widgMAG);
//    
//    if (v != null) setValue(v);
//    else {
//      get_cible();
//    }
//  }
//  void get_cible() {
//    val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
//    if (!search_folder.get()) cible = sheet.value_bloc.getValue(val_cible.get());
//    else if (sheet != mmain()) cible = sheet.sheet.value_bloc.getValue(val_cible.get());
//    if (cible != null) setValue(cible);
//  }
//  public MVecCtrl clear() {
//    if (val_run != null && cible != null) cible.removeEventChange(val_run);
//    super.clear(); return this; }
//}
//
//
//
//
//
//
//class MNumCtrl extends Macro_Bloc { 
//  void setValue(sValue v) {
//    if (v.type.equals("flt") || v.type.equals("int")) {
//      if (val_run != null && cible != null) cible.removeEventChange(val_run);
//      if (in1_run != null) in1.removeEventReceive(in1_run);
//      if (in2_run != null) in2.removeEventReceive(in2_run);
//      val_cible.set(v.ref);
//      cible = v; val_field.setLinkedValue(cible);
//      if (cible.type.equals("flt")) setValue((sFlt)cible);
//      if (cible.type.equals("int")) setValue((sInt)cible);
//    }
//  }
//  void setValue(sFlt v) {
//    fval = v;
//    out.send(Macro_Packet.newPacketFloat(v.get()));
//    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(fval.get())); }};
//    in1_run = new nRunnable() { public void run() { 
//      if (in1.lastPack() != null && in1.lastPack().isBang()) { 
//        if (valFAC.get()) fval.set(fval.get()*mod); 
//        if (valINC.get()) fval.set(fval.get()+mod); }
//    } };
//    in2_run = new nRunnable() { public void run() { 
//      if (in2.lastPack() != null && in2.lastPack().isBang()) { 
//        if (valINC.get()) fval.set(fval.get()-mod); 
//        if (valFAC.get() && mod != 0) fval.set(fval.get()/mod); }
//    } };
//    v.addEventChange(val_run);
//    in1.addEventReceive(in1_run);
//    in2.addEventReceive(in2_run);
//  }
//  void setValue(sInt v) {
//    ival = v;
//    out.send(Macro_Packet.newPacketFloat(v.get()));
//    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(ival.get())); }};
//    in1_run = new nRunnable() { public void run() { 
//      if (in1.lastPack() != null && in1.lastPack().isBang()) { 
//        if (valFAC.get()) ival.set((int)(ival.get()*mod)); 
//        if (valINC.get()) ival.set((int)(ival.get()+mod)); }
//    } };
//    in2_run = new nRunnable() { public void run() { 
//      if (in2.lastPack() != null && in2.lastPack().isBang()) { 
//        if (valINC.get()) ival.set((int)(ival.get()-mod)); 
//        if (valFAC.get() && mod != 0) ival.set((int)(ival.get()/mod)); }
//    } };
//    v.addEventChange(val_run);
//    in1.addEventReceive(in1_run);
//    in2.addEventReceive(in2_run);
//  }
//  nRunnable val_run, in1_run, in2_run;
//  sInt ival; sFlt fval;
//  Macro_Connexion in1, in2, in_m, out;
//  sStr val_cible; 
//  sValue cible;
//  nLinkedWidget ref_field, val_field;
//  nLinkedWidget widgFAC, widgINC; 
//  sBoo valFAC, valINC;
//  float mod = 0;
//  nLinkedWidget mod_view;
//  sStr val_mod; 
//  MNumCtrl(Macro_Sheet _sheet, sValueBloc _bloc, sValue v) { 
//    super(_sheet, "numCtrl", "numCtrl", _bloc); 
//    
//    val_cible = newStr("cible", "cible", "");
//    
//    val_mod = newStr("mod", "mod", "2");
//    String t = val_mod.get();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { mod = 0; }
//      else if (Rapp.parseFlt(t) != 0) { mod = Rapp.parseFlt(t); }
//    }
//    
//    ref_field = addEmptyL(0).addLinkedModel("MC_Element_Field").setLinkedValue(val_cible);
//    val_field = addEmptyL(0).addLinkedModel("MC_Element_Field");
//    val_cible.addEventChange(new nRunnable(this) { public void run() { 
//      cible = sheet.value_bloc.getValue(val_cible.get());
//      if (cible != null) setValue(cible); } } );
//    
//    addEmpty(1); addEmpty(1);
//    
//    in1 = addInput(0, "+/x").setFilterBang();
//    in2 = addInput(0, "-//").setFilterBang();
//    in_m = addInput(0, "modifier").setFilterFloat().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
//      if (in_m.lastPack() != null && in_m.lastPack().isFloat() && 
//          in_m.lastPack().asFloat() != mod) {
//        mod = in_m.lastPack().asFloat(); mod_view.setText(RConst.trimFlt(mod)); } } });
//    
//    
//    out = addOutput(1, "out");
//    
//    mod_view = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_mod);
//    mod_view.addEventFieldChange(new nRunnable() { public void run() { 
//      String t = mod_view.getText();
//      if (t.length() > 0) {
//        if (t.equals("0") || t.equals("0.0")) { mod = 0; }
//        else if (Rapp.parseFlt(t) != 0) { mod = Rapp.parseFlt(t); }
//      }
//    } });
//    
//    valFAC = newBoo("valFAC", "valFAC", false);
//    valINC = newBoo("valINC", "valINC", false);
//    
//    Macro_Element e = addEmptyS(1);
//    widgFAC = e.addLinkedModel("MC_Element_Button_Selector_1", "x /").setLinkedValue(valFAC);
//    widgINC = e.addLinkedModel("MC_Element_Button_Selector_2", "+ -").setLinkedValue(valINC);
//    widgFAC.addExclude(widgINC);
//    widgINC.addExclude(widgFAC);
//    
//    if (v != null) setValue(v);
//    else {
//      cible = sheet.value_bloc.getValue(val_cible.get());
//      if (cible != null) setValue(cible);
//    }
//  }
//  public MNumCtrl clear() {
//    if (val_run != null && cible != null) cible.removeEventChange(val_run);
//    super.clear(); return this; }
//}








//class MComp extends Macro_Bloc {
//	  static class MComp_Builder extends MAbstract_Builder {
//		  MComp_Builder() { super("comp", "Comparator", "", "Data"); }
//		  MComp build(Macro_Sheet s, sValueBloc b) { MComp m = new MComp(s, b); return m; }
//	  }
//Macro_Connexion in1, in2, out;
//nLinkedWidget widgSUP, widgINF, widgEQ; 
//sBoo valSUP, valINF, valEQ;
//float pin1 = 0, pin2 = 0;
//nLinkedWidget view;
//sStr val_view; 
//MComp(Macro_Sheet _sheet, sValueBloc _bloc) { 
//  super(_sheet, "comp", "comp", _bloc); 
//  
//  valSUP = newBoo("valSUP", "valSUP", false);
//  valINF = newBoo("valINF", "valINF", false);
//  valEQ = newBoo("valEQ", "valEQ", false);
//  
//  valSUP.addEventChange(new nRunnable() { public void run() { if (valSUP.get()) receive(); } });
//  valINF.addEventChange(new nRunnable() { public void run() { if (valINF.get()) receive(); } });
//  valEQ.addEventChange(new nRunnable() { public void run() { if (valEQ.get()) receive(); } });
//  
//  in1 = addInput(0, "in").setFilterNumber().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
//    if (in1.lastPack() != null && in1.lastPack().isFloat() && 
//        in1.lastPack().asFloat() != pin1) {
//      pin1 = in1.lastPack().asFloat(); receive(); 
//    } else if (in1.lastPack() != null && in1.lastPack().isInt() && 
//               in1.lastPack().asInt() != pin1) {
//      pin1 = in1.lastPack().asInt(); receive(); 
//    } 
//  } });
//  in2 = addInput(0, "in").setFilterNumber().setLastFloat(0).addEventReceive(new nRunnable() { public void run() { 
//    if (in2.lastPack() != null && in2.lastPack().isFloat() && 
//        in2.lastPack().asFloat() != pin2) {
//      pin2 = in2.lastPack().asFloat(); view.setText(RConst.trimFlt(pin2)); receive(); 
//    } else if (in2.lastPack() != null && in2.lastPack().isInt() && 
//               in2.lastPack().asInt() != pin2) {
//      pin2 = in2.lastPack().asInt(); view.setText(RConst.trimFlt(pin2)); receive(); 
//    } 
//  } });
//  
//  out = addOutput(1, "out")
//    .setDefBool();
//    
//  val_view = newStr("val", "val", "");
//  
//  view = addEmptyS(1).addLinkedModel("MC_Element_SField").setLinkedValue(val_view);
//  view.addEventFieldChange(new nRunnable() { public void run() { 
//    String t = view.getText();
//    if (t.length() > 0) {
//      if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
//      else if (Rapp.parseFlt(t) != 0) { pin2 = Rapp.parseFlt(t); in2.setLastFloat(pin2); receive(); }
//    }
//  } });
//  String t = view.getText();
//  if (t.length() > 0) {
//    if (t.equals("0") || t.equals("0.0")) { pin2 = 0; in2.setLastFloat(0); receive(); }
//    else if (Rapp.parseFlt(t) != 0) { pin2 = Rapp.parseFlt(t); in2.setLastFloat(pin2); receive(); }
//  }
//  Macro_Element e = addEmptyL(0);
//  widgSUP = e.addLinkedModel("MC_Element_Button_Selector_1", ">").setLinkedValue(valSUP);
//  widgINF = e.addLinkedModel("MC_Element_Button_Selector_2", "<").setLinkedValue(valINF);
//  widgEQ = e.addLinkedModel("MC_Element_Button_Selector_4", "=").setLinkedValue(valEQ);
//  widgSUP.addExclude(widgINF);
//  widgINF.addExclude(widgSUP);
//  
//}
//void receive() { 
//  if      (valSUP.get() && (pin1 > pin2)) out.send(Macro_Packet.newPacketBool(true));
//  else if (valINF.get() && (pin1 < pin2)) out.send(Macro_Packet.newPacketBool(true));
//  else if (valEQ.get() && (pin1 == pin2)) out.send(Macro_Packet.newPacketBool(true));
//  else                                    out.send(Macro_Packet.newPacketBool(false));
//}
//public MComp clear() {
//  super.clear(); return this; }
//}
//
//
//
//
//
//
//class MBool extends Macro_Bloc {
//	  static class MBool_Builder extends MAbstract_Builder {
//		  MBool_Builder() { super("bool", "Boolean", "", "Data"); }
//		  MBool build(Macro_Sheet s, sValueBloc b) { MBool m = new MBool(s, b); return m; }
//	  }
//Macro_Connexion in1, in2, out;
//nLinkedWidget widgAND, widgOR; 
//sBoo valAND, valOR;
//boolean pin1 = false, pin2 = false;
//MBool(Macro_Sheet _sheet, sValueBloc _bloc) { 
//  super(_sheet, "bool", "bool", _bloc); 
//  
//  valAND = newBoo("valAND", "valAND", false);
//  valOR = newBoo("valOR", "valOR", false);
//  
//  in1 = addInput(0, "in").setFilterBool().addEventReceive(new nRunnable() { public void run() { 
//    if (in1.lastPack() != null && in1.lastPack().isBool() && in1.lastPack().asBool() != pin1) {
//      pin1 = in1.lastPack().asBool(); receive(); } } });
//  in2 = addInput(0, "in").setFilterBool().addEventReceive(new nRunnable() { public void run() { 
//    if (in2.lastPack() != null && in2.lastPack().isBool() && in2.lastPack().asBool() != pin2) {
//      pin2 = in2.lastPack().asBool(); receive(); } } });
//  
//  out = addOutput(1, "out")
//    .setDefBool();
//  
//  Macro_Element e = addEmptyS(1);
//  widgAND = e.addLinkedModel("MC_Element_Button_Selector_1", "&&").setLinkedValue(valAND);
//  widgOR = e.addLinkedModel("MC_Element_Button_Selector_2", "||").setLinkedValue(valOR);
//  widgAND.addExclude(widgOR);
//  widgOR.addExclude(widgAND);
//  
//}
//void receive() { 
//  if (valAND.get() && (pin1 && pin2)) 
//      out.send(Macro_Packet.newPacketBool(true));
//  else if (valOR.get() && (pin1 || pin2)) 
//    out.send(Macro_Packet.newPacketBool(true));
//  else if (valAND.get() || valOR.get()) 
//    out.send(Macro_Packet.newPacketBool(false));
//}
//public MBool clear() {
//  super.clear(); return this; }
//}
  