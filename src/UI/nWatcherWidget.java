package UI;

import RBase.RConst;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sInt;
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
//  if (b.type.equals("col")) setLinkedValue((sCol)b);
  return this; }
nRunnable val_run;
sValue val;
sBoo bval; sInt ival; sFlt fval; sStr sval; sVec vval; //sCol cval;
String base_text = "";
public nWatcherWidget(nGUI g) { super(g); }
nWatcherWidget setLinkedValue(sInt b) { 
  ival = b; setText(String.valueOf(ival.get()));
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setText(String.valueOf(ival.get())); } };
  b.addEventChange(val_run);
  return this; }
nWatcherWidget setLinkedValue(sFlt b) { 
  fval = b; setText(RConst.trimStringFloat(fval.get()));
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setText(RConst.trimStringFloat(fval.get())); } };
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
//nWatcherWidget setLinkedValue(sCol b) { 
//  cval = b; setStandbyColor(cval.get());
//  val_run = new nRunnable(this) { public void run() { 
//    ((nWatcherWidget)builder).setStandbyColor(cval.get()); } };
//  b.addEventChange(val_run);
//  return this; }
nWatcherWidget setLinkedValue(sVec b) { 
  vval = b; 
  setText(RConst.trimStringFloat(vval.x()) + "," + RConst.trimStringFloat(vval.y()));
  val_run = new nRunnable(this) { public void run() { 
    ((nWatcherWidget)builder).setText(RConst.trimStringFloat(vval.x()) + "," + RConst.trimStringFloat(vval.y())); } };
  b.addEventChange(val_run);
  return this; }
}
