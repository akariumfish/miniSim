package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import sData.*;

/*

 bloc extend abstract
 shelfpanel of element
 methods to add and manipulate element for easy macro building
 
 */

//class Connexion_Value_Pair {
//	sValue val; Macro_Connexion co;
//}
//class Run_Value_Pair {
//	sValue val; nRunnable run;
//}

public class Macro_Bloc extends Macro_Abstract {
	nCtrlWidget param_open;
	  Macro_Bloc(Macro_Sheet _sheet, String t, String n, sValueBloc _bloc) {
		    super(_sheet, t, n, _bloc);
//		    mlogln("build bloc "+t+" "+n+" "+ _bloc);
		    addShelf(); 
		    addShelf();
		  }
	  Macro_Bloc(Macro_Sheet _sheet, String t, sValueBloc _bloc) {
		    super(_sheet, t, t, _bloc);
//		    mlogln("build bloc "+t+" "+n+" "+ _bloc);
		    addShelf(); 
		    addShelf();
		  }

	  nCtrlWidget get_param_openner() {
		  if (param_open == null) {
			  param_open = addCtrlModel("MC_Param", "P");
			  param_open.setParent(panel).setInfo("show/hide param");
		  }
		  return param_open;
	  }
	  Macro_Bloc set_param_action(nRunnable r) {
		  get_param_openner().setRunnable(r);
		  return this;
	  }

	  
	  
	  
	Macro_Connexion addInputToValue(int c, sValue v) { 
	  Macro_Connexion in = addInput(0, v.ref);
	  linkInputToValue(in, v);
	  return in;
	}
	Macro_Connexion linkInputToValue(Macro_Connexion in, sValue v) { 
      nObjectPair pin = new nObjectPair();
      pin.obj1 = v; pin.obj2 = in;
      if (v.type.equals("str")) { 
          in.setFilterString()
	        .addEventReceiveStr(new nRunnable(pin) { public void run() {
	        	nObjectPair pair = ((nObjectPair)builder);
	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
	    			  ((sStr)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asStr());
	    	    }});
        } else if (v.type.equals("flt")) { 
            in.setFilterNumber()
	        .addEventReceiveInt(new nRunnable(pin) { public void run() {
	        	nObjectPair pair = ((nObjectPair)builder);
	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
	    			  ((sFlt)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asInt());
	    	    }}).addEventReceiveFloat(new nRunnable(pin) { public void run() {
	    	    	nObjectPair pair = ((nObjectPair)builder);
	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
	    			  ((sFlt)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asFloat());
	    	    }});
        } else if (v.type.equals("int")) { 
	        in.setFilterNumber()
	        	.addEventReceiveInt(new nRunnable(pin) { public void run() {
	        		nObjectPair pair = ((nObjectPair)builder);
	  		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
	  			  ((sInt)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asInt());
	  	  }}).addEventReceiveFloat(new nRunnable(pin) { public void run() {
	  		nObjectPair pair = ((nObjectPair)builder);
	  		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
	  			  ((sInt)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asFloat());
	  	  }});
        } else if (v.type.equals("boo")) { 
            in.setFilterBool()
  	        .addEventReceiveBool(new nRunnable(pin) { public void run() {
  	        	nObjectPair pair = ((nObjectPair)builder);
  	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
  	    			  ((sBoo)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asBool());
  	    	    }});
        } else if (v.type.equals("col")) { 
            in.setFilterColor()
  	        .addEventReceiveCol(new nRunnable(pin) { public void run() {
  	        	nObjectPair pair = ((nObjectPair)builder);
  	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
  	    			  ((sCol)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asCol());
  	    	    }});
        } else if (v.type.equals("vec")) { 
            in.setFilterVec()
  	        .addEventReceiveVec(new nRunnable(pin) { public void run() {
  	        	nObjectPair pair = ((nObjectPair)builder);
  	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
  	    			  ((sVec)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asVec());
  	    	    }});
        }
      return in; 
    }
	
	
	
	
	
	Macro_Connexion addValueToOutput(int c, sValue v) { 
	  Macro_Connexion out = addOutput(2, v.ref);
	  linkValueToOutput(out, v);
	  return out;
	}
	Macro_Connexion linkValueToOutput(Macro_Connexion out, sValue v) { 
	  nObjectPair pout = new nObjectPair();
      pout.obj1 = v; pout.obj2 = out;
      nRunnable val_run = new nRunnable(pout) { public void run() {
    	  nObjectPair pair = ((nObjectPair)builder);
  	  	((Macro_Connexion)pair.obj2).send(((sValue)pair.obj1).asPacket());
	  }};
	  v.addEventChange(val_run);
	  nObjectPair pr = new nObjectPair();
      pr.obj1 = v; pr.obj2 = val_run;
      addEventClear(new nRunnable(pr) { public void run() {
    	  nObjectPair pair = ((nObjectPair)builder);
    	  	((sValue)pair.obj1).removeEventChange(((nRunnable)pair.obj2));
	  }});
      return out; 
    }
	
	
	
	
	
	nWidget addLinkedLWidget(Macro_Element e, sValue v) {
	  if (v.type.equals("boo")) return addLinkedLSwitch(e, v);
	  else if (v.type.equals("vec") || v.type.equals("col")) 
		  return addLWatcher(e, v);
	  else if (v.type.equals("obj") || v.type.equals("run")) 
		  return e.addModel("MC_Element_Text", v.ref);
	  else return addLinkedLField(e, v);
	}
	nLinkedWidget addLinkedLField(Macro_Element e, sValue v) {
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nWatcherWidget addLWatcher(Macro_Element e, sValue v) {
	  nWatcherWidget w = e.addWatcherModel("MC_Element_Text", v.ref).setLinkedValue(v);
	  w.setBackground();
	  w.setInfo(v.ref);
	  return w;
	}
	nLinkedWidget addLinkedLSwitch(Macro_Element e, sValue v) {
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Button", v.ref).setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	
	
	void addLinkedLWidget_Pan(int c, sValue v) {
	  if (v.type.equals("boo")) addLinkedLSwitch_Pan(c, v);
	  else if (v.type.equals("vec") || v.type.equals("col")) addLWatcher_Pan(c, v);
	  else if (v.type.equals("obj") || v.type.equals("run")) addLabelL(c, v.ref);
	  else addLinkedLField_Pan(c, v);
	}
	nLinkedWidget addLinkedLField_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}
	nWatcherWidget addLWatcher_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_Text").setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}
	nLinkedWidget addLinkedLSwitch_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Button").setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}

	void addLinkedLWidget(int c, sValue v) {
	  if (v.type.equals("boo")) addLinkedLSwitch(c, v);
	  else if (v.type.equals("vec") || v.type.equals("col")) addLWatcher(c, v);
	  else if (v.type.equals("obj") || v.type.equals("run")) addLabelL(c, v.ref);
	  else addLinkedLField(c, v);
	}
	nLinkedWidget addLinkedLField(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nWatcherWidget addLWatcher(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_Text").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nLinkedWidget addLinkedLSwitch(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Button").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	
	
	void addLinkedSWidget_Pan(int c, sValue v) {
	  if (v.type.equals("boo")) addLinkedSSwitch_Pan(c, v);
	  else if (v.type.equals("vec") || v.type.equals("col")) addSWatcher_Pan(c, v);
	  else if (v.type.equals("obj") || v.type.equals("run")) addLabelS(c, v.ref);
	  else addLinkedSField_Pan(c, v);
	}
	nLinkedWidget addLinkedSField_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyS(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_SField").setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}
	nWatcherWidget addSWatcher_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyS(c);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_SText").setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}
	nLinkedWidget addLinkedSSwitch_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyS(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_SButton").setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}

	
	void addLinkedSWidget(int c, sValue v) {
	  if (v.type.equals("boo")) addLinkedSSwitch(c, v);
	  else if (v.type.equals("vec") || v.type.equals("col")) addSWatcher(c, v);
	  else if (v.type.equals("obj") || v.type.equals("run")) addLabelS(c, v.ref);
	  else addLinkedSField(c, v);
	}
	nLinkedWidget addLinkedSField(int c, sValue v) {
	  Macro_Element e = addEmptyS(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_SField").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nWatcherWidget addSWatcher(int c, sValue v) {
	  Macro_Element e = addEmptyS(c);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_SText").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nLinkedWidget addLinkedSSwitch(int c, sValue v) {
	  Macro_Element e = addEmptyS(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_SButton").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	
	
	
	
	Macro_Bloc newRowValue(sValue v) {
	  Macro_Connexion in = addInputToValue(0, v);
	  addLinkedSWidget(1, v);
	  Macro_Connexion out = addValueToOutput(2, v);
	  in.addBangGet(v, out);
	  return this;
	}
	Macro_Bloc newRowValue_Pan(sValue v) {
	  Macro_Connexion in = addInputToValue(0, v);
	  addLinkedSWidget_Pan(1, v);
	  Macro_Connexion out = addValueToOutput(2, v);
	  in.addBangGet(v, out);
	  return this;
	}
	Macro_Bloc newRowProtectedValue(sValue v) {
	  Macro_Connexion in = addInput(0, "get "+v.ref);
	  Macro_Element e = addEmptyS(1);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_SText").setLinkedValue(v);
	  w.setInfo(v.ref);
	  Macro_Connexion out = addValueToOutput(2, v);
	  in.addBangGet(v, out);
	  return this;
	}
	
	
	
	
	
    sInt newRowInt(int d, String r) {
	  sInt v = newInt(d, r, r);
	  newRowValue_Pan(v);
	  return v;
    }
    sFlt newRowFlt(float d, String r) {
  	  sFlt v = newFlt(d, r, r);
	  newRowValue_Pan(v);
	  return v;
    }
    sBoo newRowBoo(boolean d, String r) {
    	  sBoo v = newBoo(d, r, r);
    	  newRowValue_Pan(v);
  	  return v;
    }
    sCol newRowColor(int d, String r) {
    	  sCol v = newCol(d, r, r);
    	  newRowValue_Pan(v);
  	  return v;
    }
    sVec newRowVec(String r) {
      sVec v = newVec(r, r);
  	  newRowValue_Pan(v);
    	  return v;
    }
    sStr newRowStr(String def, String ref) {
    	  sStr v = newStr(ref, ref, def);
  	  newRowValue_Pan(v);
    	  return v;
    }
  
    
    
    
    

    sBoo newSSwitchBoo(int c, boolean d, String r) {
    		sBoo v = newBoo(d, r, r);
    		Macro_Element e = addEmptyS(c);
    		nLinkedWidget w = e.addLinkedModel("MC_Element_SButton").setLinkedValue(v);
    		w.setInfo(v.ref);
    		return v;
    }
    sBoo newSwitchBoo(int c, boolean d, String r) {
		sBoo v = newBoo(d, r, r);
		addEmpty(c+1);
		Macro_Element e = addEmptyL(c);
		nLinkedWidget w = e.addLinkedModel("MC_Element_Button", v.ref).setLinkedValue(v);
		return v;
    }
    sInt newSFieldInt(int c, int d, String r) {
	    	sInt v = newInt(d, r, r);
	    	addLinkedSWidget_Pan(c, v);
	    	return v;
    }
    sFlt newSFieldFlt(int c, float d, String r) {
	    	sFlt v = newFlt(d, r, r);
	    	addLinkedSWidget_Pan(c, v);
		return v;
	}
    sFlt newFieldFlt(int c, float d, String r) {
	    	sFlt v = newFlt(d, r, r);
	    	addLinkedLWidget_Pan(c, v);
		return v;
	}
    sStr newFieldStr(int c, String d, String r) {
	    	sStr v = newStr(d, r, r);
	    	addEmpty(c+1);
  	  	Macro_Element e = addEmptyL(c);
  	  	nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setLinkedValue(v);
	  	w.setInfo(v.ref);
	  	return v;
	}
    Macro_Element addSelectToInt(int c, sInt v) {
	    	addEmpty(c+1);
	    	Macro_Element e = addEmptyL(c);
	    	nWatcherWidget w = e.addWatcherModel("MC_Element_SField").setLinkedValue(v);
	    	w.setInfo(v.ref).setPX(ref_size*1.375);
	    	e.addCtrlModel("MC_Element_SButton", "-").setRunnable(new nRunnable(v) { public void run() {
	    		((sInt)builder).set(((sInt)builder).get()-1);
	    	}}).setPX(ref_size*0.125).setSX(ref_size*0.75);
	    	e.addCtrlModel("MC_Element_SButton", "+").setRunnable(new nRunnable(v) { public void run() {
	    		((sInt)builder).set(((sInt)builder).get()+1);
	    	}}).setPX(ref_size*3.25).setSX(ref_size*0.75);
    		return e;
    }
    sInt newSelectInt(int c, int d, String r) {
	    	sInt v = newInt(d, r, r);
	    	addSelectToInt(c, v);
	    	return v;
    }
    
    
    
    
    
  Macro_Element addSelectS(int c, sBoo v1, sBoo v2, String s1, String s2) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    w1.addExclude(w2);
    w2.addExclude(w1);
    return m;
  }
  
  Macro_Element addSelectL(int c, sBoo v1, sBoo v2, sBoo v3, 
                           String s1, String s2, String s3) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    nWidget w3 = m.addLinkedModel("MC_Element_Button_Selector_3", s3).setLinkedValue(v3);
    w1.addExclude(w2).addExclude(w3);
    w2.addExclude(w1).addExclude(w3);
    w3.addExclude(w2).addExclude(w1);
    return m;
  }
  
  Macro_Element addSelectL(int c, sBoo v1, sBoo v2, sBoo v3, sBoo v4, 
                           String s1, String s2, String s3, String s4) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    nWidget w3 = m.addLinkedModel("MC_Element_Button_Selector_3", s3).setLinkedValue(v3);
    nWidget w4 = m.addLinkedModel("MC_Element_Button_Selector_4", s4).setLinkedValue(v4);
    w1.addExclude(w2).addExclude(w3).addExclude(w4);
    w2.addExclude(w1).addExclude(w3).addExclude(w4);
    w3.addExclude(w2).addExclude(w1).addExclude(w4);
    w4.addExclude(w2).addExclude(w3).addExclude(w1);
    return m;
  }
  
  Macro_Element addEmptyS(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    return m;
  }
  nCtrlWidget addTrigS(int c, String l, nRunnable r) { 
	    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
	    addElement(c, m); 
	    return m.addCtrlModel("MC_Element_SButton", l).setRunnable(r);
	  }
  nCtrlWidget addTrigS(int c, String l, String inf, nRunnable r) { 
	    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
	    addElement(c, m); 
	    nCtrlWidget w = m.addCtrlModel("MC_Element_SButton", l).setRunnable(r);
	    w.setInfo(inf);
	    return w;
	  }
  nLinkedWidget addSwitchS(int c, String l, sBoo r) { 
	    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
	    addElement(c, m); 
	    return m.addLinkedModel("MC_Element_SButton", l).setLinkedValue(r);
	  }
  nCtrlWidget addTrigSwtchS(int c, String sw_txt, sBoo vb, String bp_txt, nRunnable r) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    nCtrlWidget cw = m.addCtrlModel("MC_Element_SButton", bp_txt).setRunnable(r);
    m.addLinkedModel("MC_Element_MiniButton", sw_txt).setLinkedValue(vb);
    return cw;
  }
  Macro_Element addEmptyL(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  Macro_Element addEmptyXL(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Triple", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  Macro_Element addEmptyB(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Big", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  Macro_Element addEmptyXB(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Bigger", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  nWidget addEmpty(int c) { 
    Macro_Element m = new Macro_Element(this, "", "mc_ref", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }

  nWidget addFillR(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Fillright", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }
  nWidget addFillL(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Fillleft", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }

  nWidget addLabelS(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    nWidget w = m.addModel("MC_Element_SText", t);
    return w;
  }
  nWidget addLabelL(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    nWidget w = m.addModel("MC_Element_Text", t);
    return w;
  }

  Macro_Connexion addInput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, INPUT, INPUT, true);
    if (m.sheet_connect != null) m.sheet_connect.direct_connect(m.connect);
    addElement(c, m); 
    return m.connect;
  }
  Macro_Connexion addOutput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, OUTPUT, OUTPUT, true);
    if (m.sheet_connect != null) m.connect.direct_connect(m.sheet_connect);
    addElement(c, m); 
    return m.connect;
  }

  Macro_Connexion addInput(int c, String t, nRunnable r) { 
    return addInput(c, t).addEventReceive(r);
  }
  Macro_Connexion addOutput(int c, String t, nRunnable r) { 
    return addOutput(c, t).addEventReceive(r);
  }

  Macro_Connexion addInputBang(int c, String t, nRunnable r) { 
    return addInput(c, t).setFilterBang().addEventReceiveBang(r); }

  Macro_Connexion addInputBool(int c, String t, nRunnable r) { 
    return addInput(c, t).setFilterBool().addEventReceiveBool(r); }

  Macro_Connexion addInputFloat(int c, String t, nRunnable r) { 
    return addInput(c, t).setFilterFloat().addEventReceiveFloat(r); }

  Macro_Connexion addInputInt(int c, String t, nRunnable r) { 
    return addInput(c, t).setFilterInt().addEventReceiveInt(r); }
  
  Macro_Element addSheetInput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, OUTPUT, INPUT, true);
    if (m.sheet_connect != null) m.sheet_connect.direct_connect(m.connect);
    addElement(c, m); 
    return m;
  }
  Macro_Element addSheetOutput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, INPUT, OUTPUT, true);
    if (m.sheet_connect != null) m.connect.direct_connect(m.sheet_connect);
    addElement(c, m); 
    return m;
  }


  Macro_Element addElement(int c, Macro_Element m) {
    if (c >= 0 && c < 3) {
      if (c == 2 && shelfs.size() < 3) addShelf();
      elements.add(m);
      getShelf(c).insertDrawer(m);
      if (c == 0 && getShelf(c).drawers.size() == 1) getShelf(c).getDrawer(0).ref.setPX(-ref_size*0.0);
      if (c == 1 && getShelf(c).drawers.size() == 1) getShelf(c).getDrawer(0).ref.setPX(ref_size*0.5);
      if (c == 2 && getShelf(c).drawers.size() == 1) getShelf(c).getDrawer(0).ref.setPX(ref_size);
      if (openning.get() == OPEN) for (Macro_Element e : elements) e.show();
      toLayerTop();
      return m;
    } else return null;
  }
  
  String resum_link() { 
    String r = "";
    for (Macro_Element m : elements) {
      if (m.connect != null) for (Macro_Connexion co : m.connect.connected_inputs) 
        r += co.descr + INFO_TOKEN + m.connect.descr + OBJ_TOKEN;
      if (m.connect != null) for (Macro_Connexion co : m.connect.connected_outputs) 
        r += m.connect.descr + INFO_TOKEN + co.descr + OBJ_TOKEN;
    }
    return r; 
  }
  String resum_spot(String spots) { 
    String[] spots_side_list = PApplet.splitTokens(spots, GROUP_TOKEN);
    String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
    if (spots_side_list.length == 2) { 
      left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }
    
    for (Macro_Element m : elements) {
      if (m.spot != null && m.side.equals("left")) 
        left_s += m.descr + OBJ_TOKEN;
      if (m.spot != null && m.side.equals("right")) 
        right_s += m.descr + OBJ_TOKEN;
    }
    return left_s + GROUP_TOKEN + right_s; 
  }
  
  ArrayList<Macro_Element> elements = new ArrayList<Macro_Element>();
  public Macro_Bloc toLayerTop() { 
    super.toLayerTop(); 
    for (Macro_Element e : elements) e.toLayerTop(); 
    for (Macro_Element e : elements) if (e.spot != null) e.toLayerTop(); 
    grabber.toLayerTop();  
    if (param_open != null) param_open.toLayerTop();
    return this;
  }
  

  ArrayList<nRunnable> eventClearRun = new ArrayList<nRunnable>();
  Macro_Bloc addEventClear(nRunnable r) { eventClearRun.add(r); return this; }
  
  public Macro_Bloc clear() {
	    for (nRunnable e : eventClearRun) e.run();
	    for (Macro_Element e : elements) e.clear();
    super.clear(); return this; }

  
  Macro_Bloc set_unique() {
//	  if (sheet.sheet_unique_bloc != null) {
//	    	mmain().inter.addEventNextFrame(new nRunnable(this) { public void run() { clear(); } });
//	    } else {
//	    	sheet.sheet_menu_bloc = this;
//	    }
	  return this;
  }
  
  Macro_Bloc open() {
    super.open();
    for (Macro_Element m : elements) m.show();
    toLayerTop();
    return this;
  }
  Macro_Bloc reduc() {
    super.reduc();
    for (Macro_Element m : elements) m.reduc();
    toLayerTop();
    return this;
  }
  Macro_Bloc show() {
    super.show();
    for (Macro_Element m : elements) m.show();
    toLayerTop();
    return this;
  }
  Macro_Bloc hide() {
    super.hide(); 
    for (Macro_Element m : elements) m.hide();
    //toLayerTop();
    return this;
  }
}





