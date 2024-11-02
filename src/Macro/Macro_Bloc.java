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

	  
	  
	  
	Macro_Connexion addInputToValue(int c, sValue v) { 
	  Macro_Connexion in = addInput(c, v.ref);
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
        } else if (v.type.equals("run")) { 
            in.setFilterBang()
  	        .addEventReceiveBang(new nRunnable(pin) { public void run() {
  	        	nObjectPair pair = ((nObjectPair)builder);
  	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
  	    			  ((sRun)pair.obj1).run();
  	    	    }});
        } else if (v.type.equals("obj")) { 
            in.setFilterBang()
  	        .addEventReceiveBang(new nRunnable(pin) { public void run() {
  	        	nObjectPair pair = ((nObjectPair)builder);
  	    		  if (((Macro_Connexion)pair.obj2).lastPack() != null) 
  	    			  ((sObj)pair.obj1).set(((Macro_Connexion)pair.obj2).lastPack().asObj());
  	    	    }});
        }
      return in; 
    }
	
	
	
	
	
	Macro_Connexion addValueChangeToOutput(int c, sValue v) { 
	  Macro_Connexion out = addOutput(2, v.ref);
	  changeValueToOutput(out, v);
	  return out;
	}
	Macro_Connexion changeValueToOutput(Macro_Connexion out, sValue v) { 
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
	Macro_Bloc changeValueToRunnable(sValue v, nRunnable run) { 
      nRunnable val_run = new nRunnable(run) { public void run() {
  	  	((nRunnable)builder).run(); }};
	  v.addEventChange(val_run);
	  nObjectPair pr = new nObjectPair();
      pr.obj1 = v; pr.obj2 = val_run;
      addEventClear(new nRunnable(pr) { public void run() {
    	  	nObjectPair pair = ((nObjectPair)builder);
    	  	((sValue)pair.obj1).removeEventChange(((nRunnable)pair.obj2));
	  }});
      return this; 
    }
	Macro_Connexion addAllValueChangeToOutput(int c, sValue v) { 
	  Macro_Connexion out = addOutput(2, v.ref);
	  allChangeValueToOutput(out, v);
	  return out;
	}
	Macro_Connexion allChangeValueToOutput(Macro_Connexion out, sValue v) { 
	  nObjectPair pout = new nObjectPair();
      pout.obj1 = v; pout.obj2 = out;
      nRunnable val_run = new nRunnable(pout) { public void run() {
    	  nObjectPair pair = ((nObjectPair)builder);
  	  	((Macro_Connexion)pair.obj2).send(((sValue)pair.obj1).asPacket());
	  }};
	  v.addEventAllChange(val_run);
	  nObjectPair pr = new nObjectPair();
      pr.obj1 = v; pr.obj2 = val_run;
      addEventClear(new nRunnable(pr) { public void run() {
    	  nObjectPair pair = ((nObjectPair)builder);
    	  	((sValue)pair.obj1).removeEventChange(((nRunnable)pair.obj2));
	  }});
      return out; 
    }
	Macro_Bloc allChangeValueToRunnable(sValue v, nRunnable run) { 
      nRunnable val_run = new nRunnable(run) { public void run() {
  	  	((nRunnable)builder).run(); }};
	  v.addEventAllChange(val_run);
	  nObjectPair pr = new nObjectPair();
      pr.obj1 = v; pr.obj2 = val_run;
      addEventClear(new nRunnable(pr) { public void run() {
    	  	nObjectPair pair = ((nObjectPair)builder);
    	  	((sValue)pair.obj1).removeEventChange(((nRunnable)pair.obj2));
	  }});
      return this; 
    }
	Macro_Connexion addAllValueGetToOutput(int c, sValue v) { 
	  Macro_Connexion out = addOutput(2, v.ref);
	  allValueGetToOutput(out, v);
	  return out;
	}
	Macro_Connexion allValueGetToOutput(Macro_Connexion out, sValue v) { 
	  nObjectPair pout = new nObjectPair();
      pout.obj1 = v; pout.obj2 = out;
      nRunnable val_run = new nRunnable(pout) { public void run() {
    	  nObjectPair pair = ((nObjectPair)builder);
  	  	((Macro_Connexion)pair.obj2).send(((sValue)pair.obj1).asPacket());
	  }};
	  v.addEventAllGet(val_run);
	  nObjectPair pr = new nObjectPair();
      pr.obj1 = v; pr.obj2 = val_run;
      addEventClear(new nRunnable(pr) { public void run() {
    	  nObjectPair pair = ((nObjectPair)builder);
    	  	((sValue)pair.obj1).removeEventChange(((nRunnable)pair.obj2));
	  }});
      return out; 
    }
	Macro_Bloc allValueGetToRunnable(sValue v, nRunnable run) { 
      nRunnable val_run = new nRunnable(run) { public void run() {
  	  	((nRunnable)builder).run(); }};
	  v.addEventAllGet(val_run);
	  nObjectPair pr = new nObjectPair();
      pr.obj1 = v; pr.obj2 = val_run;
      addEventClear(new nRunnable(pr) { public void run() {
    	  	nObjectPair pair = ((nObjectPair)builder);
    	  	((sValue)pair.obj1).removeEventChange(((nRunnable)pair.obj2));
	  }});
      return this; 
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
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setFltPrecision(5).setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nWatcherWidget addLWatcher(Macro_Element e, sValue v) {
	  nWatcherWidget w = e.addWatcherModel("MC_Element_Text", v.ref).setFloatPrecision(5).setLinkedValue(v);
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
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setFltPrecision(5).setLinkedValue(v);
	  w.setInfo(v.ref);
	  e.addValuePanel(v);
	  return w;
	}
	nWatcherWidget addLWatcher_Pan(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_Text").setFloatPrecision(5).setLinkedValue(v);
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
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Field").setFltPrecision(5).setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nWatcherWidget addLWatcher(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_Text").setFloatPrecision(5).setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nLinkedWidget addLinkedLSwitch(int c, sValue v) {
	  Macro_Element e = addEmptyL(c);
	  nLinkedWidget w = e.addLinkedModel("MC_Element_Button").setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	

	nWidget addLinkedSWidget(Macro_Element e, sValue v) {
	  if (v.type.equals("boo")) return addLinkedSSwitch(e, v);
	  else if (v.type.equals("vec") || v.type.equals("col")) 
		  return addSWatcher(e, v);
	  else if (v.type.equals("obj") || v.type.equals("run")) 
		  return e.addModel("MC_Element_Text", v.ref);
	  else return addLinkedSField(e, v);
	}
	nLinkedWidget addLinkedSField(Macro_Element e, sValue v) {
	  nLinkedWidget w = e.addLinkedModel("MC_Element_SField").setFltPrecision(5).setLinkedValue(v);
	  w.setInfo(v.ref);
	  return w;
	}
	nWatcherWidget addSWatcher(Macro_Element e, sValue v) {
	  nWatcherWidget w = e.addWatcherModel("MC_Element_SText", v.ref).setFloatPrecision(5).setLinkedValue(v);
	  w.setBackground();
	  w.setInfo(v.ref);
	  return w;
	}
	nLinkedWidget addLinkedSSwitch(Macro_Element e, sValue v) {
	  nLinkedWidget w = e.addLinkedModel("MC_Element_SButton", v.ref).setLinkedValue(v);
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
	  Macro_Connexion out = addValueChangeToOutput(2, v);
	  in.addBangGet(v, out);
	  return this;
	}
	Macro_Bloc newRowValue_Pan(sValue v) {
	  Macro_Connexion in = addInputToValue(0, v);
	  addLinkedSWidget_Pan(1, v);
	  Macro_Connexion out = addValueChangeToOutput(2, v);
	  in.addBangGet(v, out);
	  return this;
	}
	Macro_Bloc newRowProtectedValue(sValue v) {
	  Macro_Connexion in = addInput(0, "get "+v.ref);
	  Macro_Element e = addEmptyS(1);
	  nWatcherWidget w = e.addWatcherModel("MC_Element_SText").setLinkedValue(v);
	  w.setInfo(v.ref);
	  Macro_Connexion out = addValueChangeToOutput(2, v);
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
    Macro_Element addSSelectToInt(int c, sInt v) {
	    	Macro_Element e = addEmptyS(c);
	    	e.addCtrlModel("MC_Element_SButton", "-").setRunnable(new nRunnable(v) { public void run() {
	    		((sInt)builder).set(((sInt)builder).get()-1);
	    	}}).setPX(ref_size*0.125).setSX(ref_size*0.5);
	    	e.addCtrlModel("MC_Element_SButton", "+").setRunnable(new nRunnable(v) { public void run() {
	    		((sInt)builder).set(((sInt)builder).get()+1);
	    	}}).setPX(ref_size*1.375).setSX(ref_size*0.5);
	    	nWatcherWidget w = e.addWatcherModel("MC_Element_SField").setLinkedValue(v);
	    	w.setInfo(v.ref).setPX(ref_size*0.75).setSX(ref_size*0.5);
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
    return m;
  }
  
  Macro_Element addSelectL(int c, sBoo v1, sBoo v2, sBoo v3, 
                           String s1, String s2, String s3) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    nWidget w3 = m.addLinkedModel("MC_Element_Button_Selector_3", s3).setLinkedValue(v3);
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
    return m;
  }
    
  Macro_Element addSelectS_Excl(int c, sBoo v1, sBoo v2, String s1, String s2) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    w1.addExclude(w2);
    w2.addExclude(w1);
    return m;
  }
  
  Macro_Element addSelectL_Excl(int c, sBoo v1, sBoo v2, sBoo v3, 
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
  
  Macro_Element addSelectL_Excl(int c, sBoo v1, sBoo v2, sBoo v3, sBoo v4, 
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

  Macro_Element addEmptyS(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    return m;
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
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Empty", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    return m.back;
//    if (c >= 0 && c <= shelfs.size()) {
//        Macro_Element m = new Macro_Element(this, "", "MC_Element_Empty", null, NO_CO, NO_CO, false);
//        if (shelfs.size() <= c) {// <= c+1) {
//      	  	addShelf();
//      	  	if (col_rows_nb.length < c+1) {
//	  	    	  	int[] rn = new int[c+1];
//	  	    	  	rn[c] = 0;
//	  	    	  	for (int i = 0 ; i < c-1 ; i++) rn[i] = col_rows_nb[i]; 
//	  	    	  	col_rows_nb = rn; 
//	  	    	}
//        }
//    	  	col_rows_nb[c]++;
//        m.shelf_ind = c;
//        m.row_ind = col_rows_nb[c];
//        getShelf(c).insertDrawer(m);
//        return m.back;
//      } else return null;
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
    addElement(c, m); 
    return m.connect;
  }
  Macro_Connexion addOutput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, OUTPUT, OUTPUT, true);
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
    addElement(c, m); 
    return m;
  }
  Macro_Element addSheetOutput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, INPUT, OUTPUT, true);
    addElement(c, m); 
    return m;
  }
  
  
  

  nCtrlWidget get_param_openner() {
	  if (param_open == null) {
		  param_open = addCtrlModel("MC_Param", "P");
		  param_open.setParent(panel).setInfo("show/hide param");
		  if (openning.get() == REDUC || openning.get() == HIDE) param_open.hide();
	  }
	  return param_open;
  }
  nCtrlWidget get_mirror() {
	  if (mirror_sw == null) {
		  mirror_sw = addCtrlModel("MC_Mirror", "|");
		  mirror_sw.setParent(panel).setInfo("mirror");
		  if (openning.get() == REDUC || openning.get() == HIDE) mirror_sw.hide();
	  }
	  return mirror_sw;
  }
  Macro_Bloc set_param_action(nRunnable r) {
	  get_param_openner().setRunnable(r);
	  return this;
  }
  
  

  ArrayList<Macro_Element> elements = new ArrayList<Macro_Element>();
  nCtrlWidget param_open, mirror_sw;
  
  ArrayList<nRunnable> eventClearRun = new ArrayList<nRunnable>();
  Macro_Bloc addEventClear(nRunnable r) { eventClearRun.add(r); return this; }
  
  ArrayList<nRunnable> eventLinkChangeRun = new ArrayList<nRunnable>();
  Macro_Bloc addEventLinkChange(nRunnable r) { eventLinkChangeRun.add(r); return this; }
  
  ArrayList<Macro_Bloc> link_blocs = new ArrayList<Macro_Bloc>();
  ArrayList<Macro_Connexion> watched_cos = new ArrayList<Macro_Connexion>();
  nRunnable link_change_run;
//	  
//	void light_on() {
//		update_linklist();
//		for (Macro_Connexion mb : watched_cos) mb.enlight();
//        for (Macro_Bloc mb : link_blocs) {
//        		mb.grabber.setOutlineColor(gui.app.color(255, 0, 255));
//        		mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
//        			mb.grabber.setOutlineColor(gui.app.color(180, 150, 120));
//        		}});
//        }
//	}
//	void init_end() {
//		super.init_end();
//		mmain().inter.addEventTwoFrame(new nRunnable() { public void run() { update_linklist(); }});
//		
//
////	    grabber.addEventMouseEnter(new nRunnable() { public void run() { 
////	      if (openning.get() == REDUC && !hide_ctrl) title.show(); } });
////	    grabber.addEventMouseLeave(new nRunnable() { public void run() { 
////	    		update_linklist(); } });
//	    
//	}
//	void test_connect(Macro_Connexion out) {
//		if (!watched_cos.contains(out)) watched_cos.add(out);
//		if (out != null) 
//		for (Macro_Connexion m : out.connected_inputs) {
//			if (m.elem.bloc.val_type.get().equals("node")) {
//				watched_cos.add(m);
//				MNode n = (MNode)m.elem.bloc;
//				link_blocs.add(m.elem.bloc);
//				if (m.linkable) {
//					watched_cos.add(n.in_link);
//					watched_cos.add(n.out_link);
//					test_connect(n.out_link);
//					if (n.in_link != null) for (Macro_Connexion c2 : n.in_link.direct_cos) {
//						watched_cos.add(c2);
//						test_connect(c2);
//					}
//					if (n.out_link != null) for (Macro_Connexion c2 : n.out_link.direct_cos) {
//						watched_cos.add(c2);
//						test_connect(c2);
//					}
//				} 
//				else for (int i = 1 ; i < 9 ; i++) if (m.base_info.equals("in"+i)) {
//					for (Macro_Connexion c : m.direct_cos) { 
//						watched_cos.add(c); test_connect(c); }
//					for (MNode n2 : n.nodes) for (Macro_Connexion c : n2.in_list) {
//						if (c.base_info.equals("in"+i)) {
//							link_blocs.add(n);
//							link_blocs.add(n2);
//							watched_cos.add(n.in_link);
//							watched_cos.add(n.out_link);
//							watched_cos.add(c);
//							test_connect(c);
//							watched_cos.add(n2.in_link);
//							watched_cos.add(n2.out_link);
//							test_connect(n2.out_link);
//							for (Macro_Connexion c2 : c.direct_cos) { 
//								watched_cos.add(c2); test_connect(c2); }
//						}
//					}
//				}
//			}
//			else if (m.elem.bloc.val_type.get().equals("gate")) {
//				MGate g = (MGate)m.elem.bloc;
//				watched_cos.add(m);
//				test_connect(g.get_active_out());
//			} else if (m.linkable || m.link_undefine) {
//				watched_cos.add(m);
//				link_blocs.add(m.elem.bloc);
//				m.elem.bloc.link_blocs.add(out.elem.bloc);
//			}
//		}
//	}
//	boolean updating = false;
//	void update_linklist() {
//		if (!updating) {
//			updating = true;
//			for (Macro_Connexion m : watched_cos) m.removeEventChangeLink(link_change_run);
//			watched_cos.clear();
//			link_blocs.clear();
//			for (Macro_Element e : elements) if (e.connect != null) {
//				watched_cos.add(e.connect);
//	  	    	  	test_connect(e.connect);
//	  			for (Macro_Connexion c : e.connect.direct_cos) {
//					watched_cos.add(c);
//	  				test_connect(c); 
//	  			}
//			  	if (e.sheet_connect != null) {
//					watched_cos.add(e.sheet_connect);
//			  		test_connect(e.sheet_connect);
//		  			for (Macro_Connexion c : e.sheet_connect.direct_cos) {
//						watched_cos.add(c);
//		  				test_connect(c); 
//		  			}
//			  	}
//			}
//			gui.app.logln("uplinklst end bloc:"+link_blocs.size() + " co:"+watched_cos.size());
//			for (Macro_Connexion m : watched_cos) m.addEventChangeLink(link_change_run);
//			updating = false;
//		}
//	}
  
  Macro_Bloc(Macro_Sheet _sheet, String t, String n, sValueBloc _bloc) {
	    super(_sheet.mmain, _sheet, t, n, _bloc);
//		    mlogln("build bloc "+t+" "+n+" "+ _bloc);
	    addShelf(); 
	    addShelf();
	    addShelf();
//	    link_change_run = new nRunnable() {public void run() {
//	    	mmain().inter.addEventTwoFrame(new nRunnable() { public void run() { update_linklist(); }}); } };
		col_rows_nb[0] = 0; col_rows_nb[1] = 0; col_rows_nb[2] = 0;
	  }
  Macro_Bloc(Macro_Sheet _sheet, String t, sValueBloc _bloc) {
	    super(_sheet.mmain, _sheet, t, t, _bloc);
//		    mlogln("build bloc "+t+" "+n+" "+ _bloc);
	    addShelf(); 
	    addShelf();
	    addShelf();
//	    link_change_run = new nRunnable() {public void run() {
//	    	mmain().inter.addEventTwoFrame(new nRunnable() { public void run() { update_linklist(); }}); } };
		col_rows_nb[0] = 0; col_rows_nb[1] = 0; col_rows_nb[2] = 0;			
	  }
  int[] col_rows_nb = new int[3];

  Macro_Element addElement(int c, Macro_Element m) {
    if (c >= 0 && c <= shelfs.size()) {
      if (shelfs.size() <= c) {// <= c+1) {
    	  	addShelf();
    	  	if (col_rows_nb.length < c+1) {
	    	  	int[] rn = new int[c+1];
	    	  	rn[c] = 0;
	    	  	for (int i = 0 ; i < c-1 ; i++) rn[i] = col_rows_nb[i]; 
	    	  	col_rows_nb = rn; 
	    	}
      }
  	  col_rows_nb[c]++;
      elements.add(m);
      m.shelf_ind = c;
      m.row_ind = col_rows_nb[c];
      getShelf(c).insertDrawer(m);
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
  
//  public Macro_Abstract buildLayer() { 
//	  super.buildLayerTo(panel.getDrawable()); 
//	  buildLayerTo(panel.getDrawable()); 
//  return this; }
//  public Macro_Abstract buildLayerTo(Drawable d) { 
//	  	d.clear_coDrawer();
//	    d.add_coDrawer(title.getDrawable()); 
//	    d.add_coDrawer(reduc.getDrawable()); 
//	    d.add_coDrawer(prio_sub.getDrawable()); 
//	    d.add_coDrawer(prio_add.getDrawable()); 
//	    d.add_coDrawer(prio_view.getDrawable()); 
//	    d.add_coDrawer(front.getDrawable()); 
//	    d.add_coDrawer(grab_front.getDrawable()); 
//	    if (elements != null) {
//		    for (Macro_Element e : elements) 
//		    		if (e.spot == null) e.buildLayerTo(d); 
//		    for (Macro_Element e : elements) 
//		    		if (e.spot != null) e.buildLayerTo(d); 
//  		}
//	    d.add_coDrawer(grabber.getDrawable()); 
//
//	    if (param_open != null) d.add_coDrawer(param_open.getDrawable()); 
//  return this; }
  public Macro_Bloc toLayerTop() { 
    
    //if (!gui.app.DEBUG_NOTOLAYTOP) super.toLayerTop(); 

	for (Macro_Element e : elements) if (e.spot == null) e.toLayerTop(); 
	for (Macro_Element e : elements) if (e.spot != null) e.toLayerTop(); 

	panel.toLayerTop();  

	for (Macro_Element e : elements) if (e.spot == null) e.widget_toLayTop(); 
	for (Macro_Element e : elements) if (e.spot != null) e.widget_toLayTop(); 

	front.toLayerTop(); grab_front.toLayerTop(); 
	grabber.toLayerTop(); reduc.toLayerTop(); 
	if (!hide_ctrl) {
		title.toLayerTop(); 
		prio_sub.toLayerTop(); prio_add.toLayerTop(); prio_view.toLayerTop(); 
	}
    if (param_open != null) param_open.toLayerTop();
    if (mirror_sw != null) mirror_sw.toLayerTop();
    return this;
  }
  

  public Macro_Bloc clear() {
    for (nRunnable e : eventClearRun) e.run();
    for (Macro_Element e : elements) e.clear();
    super.clear(); return this; }
  
  Macro_Bloc open() {
    super.open();
    if (param_open != null) if (!hide_ctrl) param_open.show(); else param_open.hide(); 
    for (Macro_Element m : elements) m.show();
    toLayerTop();
    return this;
  }
  Macro_Bloc reduc() {
    super.reduc();
    if (param_open != null) param_open.hide(); 
    for (Macro_Element m : elements) m.reduc();
    toLayerTop();
    return this;
  }
  Macro_Bloc show() {
    super.show();
//    if (param_open != null) if (!hide_ctrl) param_open.show(); else param_open.hide(); 
    for (Macro_Element m : elements) m.show();
    toLayerTop();
    return this;
  }
  Macro_Bloc hide() {
    super.hide(); 
    for (Macro_Element m : elements) m.hide();
    if (param_open != null) param_open.hide();
    return this;
  }
}





