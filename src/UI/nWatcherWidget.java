package UI;

import RApplet.RConst;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sObj;
import sData.sStr;
import sData.sValue;
import sData.sVec;

//liens avec les svalues
public class nWatcherWidget extends nWidget {
	public nWatcherWidget setLinkedValue(sValue b) { 
  if (val != null) val.removeEventChange(val_run);
  val = b;
  if (b.type.equals("flt")) setLinkedValue((sFlt)b);
  if (b.type.equals("int")) setLinkedValue((sInt)b);
  if (b.type.equals("boo")) setLinkedValue((sBoo)b);
  if (b.type.equals("str")) setLinkedValue((sStr)b);
  if (b.type.equals("vec")) setLinkedValue((sVec)b);
  if (b.type.equals("col")) setLinkedValue((sCol)b);
  if (b.type.equals("obj")) setLinkedValue((sObj)b);
  return this; }
nRunnable val_run;
sValue val;
sBoo bval; sInt ival; sFlt fval; sStr sval; sVec vval; sCol cval;
String base_text = "";
public nWatcherWidget(nGUI g) { super(g); }
nWatcherWidget setLinkedValue(sInt b) { 
  ival = b; setText(PApplet.str(ival.get()));
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setText(PApplet.str(ival.get())); } };
  b.addEventChange(val_run);
  return this; }
nWatcherWidget setLinkedValue(sFlt b) { 
  fval = b; setText(RConst.trimFlt(fval.get(), float_pres));
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setText(RConst.trimFlt(fval.get(), float_pres)); } };
  b.addEventChange(val_run);
  return this; }
nWatcherWidget setLinkedValue(sBoo b) { 
  bval = b; 
  if (bval.get()) setText("true"); else setText("false");
  val_run = new nRunnable(this) { public void run() { 
    if (bval.get()) ((nWatcherWidget)builder).setText("true");
    else ((nWatcherWidget)builder).setText("false"); } };
  b.addEventChange(val_run);
  return this; }
nWatcherWidget setLinkedValue(sStr b) { 
  if (sval == null) base_text = getText();
  sval = b;
  setText(base_text + sval.get());
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).changeText(base_text + sval.get()); } };
  b.addEventChange(val_run);
  return this; }
nWatcherWidget setLinkedValue(sCol b) { 
  cval = b; setStandbyColor(cval.get());
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setStandbyColor(cval.get()); } };
  b.addEventChange(val_run);
  return this; }
nWatcherWidget setLinkedValue(sObj b) { 
  setText(b.ref);
  return this; }
nWatcherWidget setLinkedValue(sVec b) { 
  vval = b; 
  setText(RConst.trimFlt(vval.x(), float_pres) + "," + 
		  RConst.trimFlt(vval.y(), float_pres));
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setText(RConst.trimFlt(vval.x(), float_pres) + "," + 
    									 RConst.trimFlt(vval.y(), float_pres)); } };
  b.addEventChange(val_run);
  return this; }
	int float_pres = 3;
	public nWatcherWidget setFloatPrecision(int i) {
		float_pres = i;
		return this;
	}
}
