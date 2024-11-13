package UI;

import RApplet.RConst;
import RApplet.Rapp;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sInt;
import sData.sRun;
import sData.sStr;
import sData.sValue;
import sData.sVec;

public class nLinkedWidget extends nWidget { 
	public nLinkedWidget setFltPrecision(int b) { pres = b; return this; }
	public nLinkedWidget setLinkedValue(sValue b) { 
	    if (val != null) val.removeEventChange(val_run);
	    val = b;
	    clearEventTrigger();
	    clearEventSwitchOn();
	    clearEventSwitchOff();
	    clearEventFieldChange();
	    if      (b.type.equals("flt")) setLinkedValue((sFlt)b);
	    else if (b.type.equals("int")) setLinkedValue((sInt)b);
	    else if (b.type.equals("boo")) setLinkedValue((sBoo)b);
	    else if (b.type.equals("str")) setLinkedValue((sStr)b);
	    else if (b.type.equals("run")) setLinkedValue((sRun)b);
	    else if (b.type.equals("vec")) setLinkedValue((sVec)b);
	    return this; }
	  nRunnable val_run;
	  sValue val;
	  sBoo bval; sInt ival; sFlt fval; sStr sval; sRun rval; sVec vval;
	  String base_text = "";
	  int pres = 3;
	  public nLinkedWidget(nGUI g) { super(g); }

	  public nLinkedWidget setRunnable(nRunnable b) { 
	  	val_run = b;
	  	addEventTrigger(new nRunnable(this) { public void run() { 
	  		val_run.run(); } } );
	  	setTrigger();
	  	return this; }
	  nLinkedWidget setLinkedValue(sRun b) { 
	    rval = b;
	    addEventTrigger(new nRunnable(this) { public void run() { 
	      rval.run(); } } );
	    setTrigger();
	    return this; }
	  nLinkedWidget setLinkedValue(sBoo b) { 
	    if (b != null) {
	      bval = b;
	      setSwitch();
	      if (b.get()) setOn();
	      val_run = new nRunnable(this) { public void run() { 
	        ((nLinkedWidget)builder).setSwitchState(bval.get()); } } ;
	      b.addEventChange(val_run);
	      addEventSwitchOn(new nRunnable() { public void run() { bval.set(true); } } );
	      addEventSwitchOff(new nRunnable() { public void run() { bval.set(false); } } );
	    }
	    return this; }
	  nLinkedWidget setLinkedValue(sInt b) { 
	    ival = b;
	    setText(PApplet.str(ival.get()));
	    val_run = new nRunnable(this) { public void run() { 
	      ((nLinkedWidget)builder).changeText(PApplet.str(ival.get())); } };
	    b.addEventChange(val_run);
	    setField(true);
	    addEventFieldChange(new nRunnable(this) { public void run() { 
	      String s = ((nLinkedWidget)builder).getText();
	      if (RConst.testParseInt(s)) 
	    	  	ival.set(PApplet.parseInt(s)); } } );
	    return this; }
	  nLinkedWidget setLinkedValue(sFlt b) { 
	    fval = b;
	    setText(RConst.trimFlt(fval.get(), pres));
	    //println(fval.get());
	    val_run = new nRunnable(this) { public void run() { 
	      ((nLinkedWidget)builder).changeText(RConst.trimFlt(fval.get(), pres));  } };
	    b.addEventChange(val_run);
	    setField(true);
	    addEventFieldChange(new nRunnable(this) { public void run() { 
	      String s = ((nLinkedWidget)builder).getText();
	      if (RConst.testParseFlt(s)) 
	    	  	fval.set(Rapp.parseFlt(s)); } } );//Float.parseFloat(s)
	      //!(s.length() > 0 && float(s) == 0) && 
	    return this; }
	  nLinkedWidget setLinkedValue(sVec b) { 
	    vval = b;
	    setGrabbable();
	    setPosition(vval.x(), vval.y());
	    val_run = new nRunnable(this) { public void run() { 
	      ((nLinkedWidget)builder).setPosition(vval.x(), vval.y()); } };
	    b.addEventChange(val_run);
	    addEventPositionChange(new nRunnable(this) { public void run() { 
	      vval.set(((nLinkedWidget)builder).getLocalX(), ((nLinkedWidget)builder).getLocalY()); } } );
	    return this; }
	  nLinkedWidget setLinkedValue(sStr b) { 
	    if (sval == null) base_text = getText();
	    sval = b;
	    setText(base_text + sval.get());
	    val_run = new nRunnable(this) { public void run() { 
	      ((nLinkedWidget)builder).changeText(base_text + sval.get()); } };
	    b.addEventChange(val_run);
	    setField(true);
	    addEventFieldChange(new nRunnable(this) { public void run() { 
	      String s = ((nLinkedWidget)builder).getText();
	      sval.set(s); } } );
	    return this; }
		int float_pres = 3;
		public nLinkedWidget setFloatPrecision(int i) {
			float_pres = i;
			return this;
		}
	}
