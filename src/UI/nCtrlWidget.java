package UI;

import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sInt;
import sData.sRun;
import sData.sValue;

//liens avec les svalues
public class nCtrlWidget extends nWidget {
	public nCtrlWidget setLinkedValue(sValue b) { 
  if (b.type.equals("flt")) setLinkedValue((sFlt)b);
  if (b.type.equals("int")) setLinkedValue((sInt)b);
  if (b.type.equals("boo")) setLinkedValue((sBoo)b);
  if (b.type.equals("run")) setLinkedValue((sRun)b);
  return this; }
public nCtrlWidget setRunnable(nRunnable b) { 
  rval = b; setTrigger();
  addEventTrigger(new nRunnable(this) { public void run() { rval.run(); } } ); return this; }
nCtrlWidget setLinkedValue(sRun b) { 
  srval = b; setTrigger();
  addEventTrigger(new nRunnable(this) { public void run() { srval.run(); } } ); return this; }
nCtrlWidget setLinkedValue(sBoo b) { 
  bval = b; setTrigger(); 
  addEventTrigger(new nRunnable(this) { public void run() { modify(); } } ); return this; }
nCtrlWidget setLinkedValue(sInt b) { 
  ival = b; setTrigger(); 
  addEventTrigger(new nRunnable(this) { public void run() { modify(); } } ); return this; }
nCtrlWidget setLinkedValue(sFlt b) { 
  fval = b; setTrigger(); 
  addEventTrigger(new nRunnable(this) { public void run() { modify(); } } ); return this; }
nCtrlWidget setIncrement(float f) { mode = false; factor = f; return this; }
public nCtrlWidget setFactor(float f) { mode = true; factor = f; return this; }
public nCtrlWidget setFactor(double f) { return setFactor((float)f); }

public nCtrlWidget(nGUI g) { super(g); }
nRunnable rval;
sBoo bval; sInt ival; sFlt fval; sRun srval;
float factor = 2.0F; boolean mode = false;
void modify() {
  if (bval != null) bval.set(!bval.get());
  if (ival != null) { if (mode) ival.set((int)(ival.get()*factor)); else ival.set((int)(ival.get()+factor)); }
  if (fval != null) { if (mode) fval.set(fval.get()*factor); else fval.set(fval.get()+factor); }
}
}
