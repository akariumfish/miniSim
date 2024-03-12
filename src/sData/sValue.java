package sData;

import java.util.ArrayList;

import Macro.Macro_Packet;
import RApplet.RConst;
import UI.*;
import processing.core.PVector;

public abstract class sValue implements RConst {
	public boolean log = false;
	  sValueBloc getBloc() { return bloc; }
	  public abstract String getString();
	  void clear() { 
	    clean();
	    bloc.values.remove(ref, this); 
	  }
	  void clean() { 
	    if (doevent && data.doevent) nRunnable.runEvents(eventsDelete);
	    if (bloc.doevent && data.doevent) nRunnable.runEvents(bloc.eventsDelVal);
	  }
	  public boolean doevent() { return doevent; }
	  public sValue doEvent(boolean v) { doevent = v; return this; }
	  public sValue pauseEvent() { pauseevent = true; return this; }
	  public sValue addEventDelete(nRunnable r) { eventsDelete.add(r); return this; }
	  public sValue addEventChange(nRunnable r) { eventsChange.add(r); return this; }
	  public sValue removeEventChange(nRunnable r) { eventsChange.remove(r); return this; }
	  public sValue addEventAllChange(nRunnable r) { eventsAllChange.add(r); return this; }
	  public sValue removeEventAllChange(nRunnable r) { eventsAllChange.remove(r); return this; }
	  public sValue addEventAllGet(nRunnable r) { eventsAllGet.add(r); return this; }
	  public sValue removeEventAllGet(nRunnable r) { eventsAllGet.remove(r); return this; }
	  void doChange() { 
		  if (!pauseevent) { 
			  if (doevent && data.doevent) nRunnable.runEvents(eventsAllChange); 
			  has_changed = true; 
		  } else {
			  was_changed = true;
		  }
	  }
	  public sValueBloc bloc;
	  boolean has_changed = false, doevent = true, pauseevent = false, was_changed = false;
	  public String ref;
	  public String type;
	  public String shrt;
	  DataHolder data;
	  sValue(sValueBloc b, String t, String r, String s) { 
	    bloc = b; data = bloc.data;
	    while (bloc.values.get(r) != null) r = r + "'";
	    type = t; ref = r; shrt = s;
	    bloc.values.put(ref, this); 
	    if (bloc.doevent) bloc.last_created_value = this; 
	    if (bloc.doevent && data.doevent) nRunnable.runEvents(bloc.eventsAddVal); }
	  void frame() { 
		  if (!pauseevent) { 
			  if (has_changed) { 
				  if (doevent && data.doevent) nRunnable.runEvents(eventsChange); 
			  } 
			  has_changed = false; 
		  } else {
			  if (was_changed) { 
				  if (doevent && data.doevent) nRunnable.runEvents(eventsAllChange); 
				  has_changed = true; 
			  }
			  pauseevent = false; 
		  }
	  }
	  ArrayList<nRunnable> eventsChange = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsAllChange = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsAllGet = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsDelete = new ArrayList<nRunnable>();
	  public void run_events_change() {
		  if (doevent && data.doevent) nRunnable.runEvents(eventsChange); }
	  public void run_events_allchange() {
		  if (doevent && data.doevent) nRunnable.runEvents(eventsAllChange); }
	  public void run_events_allset() {
		  if (doevent && data.doevent) nRunnable.runEvents(eventsAllGet); }
	  void save_to_bloc(Save_Bloc sb) {
//	    vlogln("sv save " + ref);
	    sb.newData("ref", ref);
	    sb.newData("typ", type);
	    sb.newData("shr", shrt);
	  }
	  void load_from_bloc(Save_Bloc svb) {
//	    vlogln("sv load " + ref);
	    ref = svb.getData("ref");
	    type = svb.getData("typ");
	    shrt = svb.getData("shr");
	    has_changed = true;
	  }
	  public boolean limited_min = false;
	  public boolean limited_max = false;
	  public sValue set_limit_min(boolean b) {
		  if (b != limited_min) doChange(); 
		  limited_min = b; return this; }
	  public sValue set_limit_max(boolean b) { 
		  if (b != limited_max) doChange(); 
		  limited_max = b; return this; }
	  public sValue set_limit(boolean b1, boolean b2) { 
		  if (b1 != limited_min || b2 != limited_max) doChange(); 
		  limited_min = b1; limited_max = b2; return this; }
	  public sValue set_min(float mi) { return this; }
	  public sValue set_max(float d) { return this; }
	  public float getmin() { return 0; }
	  public float getmax() { return 0; }
	  public float getscale() { return 0; }
	  public void setscale(float v) { ; }

	  public boolean asBoo() { return false; }
	  public int asInt() { return 0; }
	  public float asFlt() { return 0; }
	  public PVector asVec() { return null; }
	  public int asCol() { return 0; }
	  public String asStr() { return ""; }
	  public Object asObj() { return null; }
	  public nRunnable asRun() { return null; }

	  public boolean isRun()   { return type.equals("run"); }
	  public boolean isObj()   { return type.equals("obj"); }
	  public boolean isFloat() { return type.equals("flt"); }
	  public boolean isInt()   { return type.equals("int"); }
	  public boolean isBool()  { return type.equals("boo"); }
	  public boolean isVec()   { return type.equals("vec"); }
	  public boolean isCol()   { return type.equals("col"); }
	  public boolean isStr()   { return type.equals("str"); }
	  
	  public Macro_Packet asPacket() { 
		  if (isBool()) return Macro_Packet.newPacketBool(asBoo());
		  if (isInt()) return Macro_Packet.newPacketInt(asInt());
		  if (isFloat()) return Macro_Packet.newPacketFloat(asFlt());
		  if (isVec()) return Macro_Packet.newPacketVec(asVec());
		  if (isCol()) return Macro_Packet.newPacketCol(asCol());
		  if (isStr()) return Macro_Packet.newPacketStr(asStr());
		  if (isObj()) return Macro_Packet.newPacketObject(asObj());
		  if (isStr()) return Macro_Packet.newPacketStr(asStr());
		  if (isRun()) return Macro_Packet.newPacketRun(asRun());
		  return null;
//		  return new Macro_Packet("value").setValue(this); 
	  }
	  
	  public nWindowPanel pop_panel(nGUI gui, nTaskPanel tpan) {
        if (type.equals("str")) { 
          return new nTextPanel(gui, tpan, (sStr)this);
        } else if (type.equals("flt")) { 
        	  return new nNumPanel(gui, tpan, (sFlt)this);
        } else if (type.equals("int")) { 
        	  return new nNumPanel(gui, tpan, (sInt)this);
        } else if (type.equals("boo")) { 
          return new nBinPanel(gui, tpan, (sBoo)this);
        } else if (type.equals("col")) { 
        	  return new nColorPanel(gui, tpan, (sCol)this);
        } else if (type.equals("vec")) { 
        	  return new nVecPanel(gui, tpan, (sVec)this);
        } else if (type.equals("run")) { 
          return new nBinPanel(gui, tpan, (sRun)this);
	    } else if (type.equals("obj")) { 
	    	  return new nObjectPanel(gui, tpan, (sObj)this);
	    } else return null;
	  }
	}
