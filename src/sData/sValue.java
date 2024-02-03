package sData;

import java.util.ArrayList;

import Macro.Macro_Packet;
import RBase.RConst;

public abstract class sValue implements RConst {
	  sValueBloc getBloc() { return bloc; }
	  public abstract String getString();
	  void clear() { 
	    clean();
	    bloc.values.remove(ref, this); 
	  }
	  void clean() { 
	    if (doevent) nRunnable.runEvents(eventsDelete);
	    if (bloc.doevent) nRunnable.runEvents(bloc.eventsDelVal);
	  }
	  public sValue doEvent(boolean v) { doevent = v; return this; }
	  public sValue addEventDelete(nRunnable r) { eventsDelete.add(r); return this; }
	  public sValue addEventChange(nRunnable r) { eventsChange.add(r); return this; }
	  public sValue removeEventChange(nRunnable r) { eventsChange.remove(r); return this; }
	  public sValue addEventAllChange(nRunnable r) { eventsAllChange.add(r); return this; }
	  public sValue removeEventAllChange(nRunnable r) { eventsAllChange.remove(r); return this; }
	  void doChange() { if (doevent) nRunnable.runEvents(eventsAllChange); has_changed = true; }
	  public sValueBloc bloc;
	  boolean has_changed = false, doevent = true;
	  public String ref;
	  public String type;
	  public String shrt;
	  //abstract Object def;
	  sValue(sValueBloc b, String t, String r, String s) { 
	    bloc = b; 
	    while (bloc.values.get(r) != null) r = r + "'";
	    type = t; ref = r; shrt = s;
	    bloc.values.put(ref, this); 
	    if (bloc.doevent) bloc.last_created_value = this; 
	    if (bloc.doevent) nRunnable.runEvents(bloc.eventsAddVal); }
	  void frame() { if (has_changed) { if (doevent) nRunnable.runEvents(eventsChange); } has_changed = false; }
	  ArrayList<nRunnable> eventsChange = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsAllChange = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsDelete = new ArrayList<nRunnable>();
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
	  public sValue set_min(float mi) { return this; }
	  public sValue set_max(float d) { return this; }
	  public float getmin() { return 0; }
	  public float getmax() { return 0; }
	  public float getscale() { return 0; }
	  public void setscale(float v) { ; }
	  
	  public float asFloat() { return 0; }
	  
	  public Macro_Packet asPacket() { return new Macro_Packet("value").setValue(this); }
	  
	}
