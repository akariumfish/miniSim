package sData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Macro.Macro_Packet;
import processing.core.PVector;



public class sValueBloc {
  void runIterator(nIterator<sValue> i) { 
	    for (Map.Entry mev : values.entrySet()) {
	      sValue v = ((sValue)mev.getValue());
	      i.run(v);
	    }
	    for (Map.Entry me : blocs.entrySet()) {
	      sValueBloc vb = ((sValueBloc)me.getValue());
	      vb.runIterator(i);
	    }
	  }
	  void runValueIterator(nIterator<sValue> i) { 
	    for (Map.Entry mev : values.entrySet()) {
	      sValue v = ((sValue)mev.getValue());
	      i.run(v);
	    }
	  }
	  public void runBlocIterator(nIterator<sValueBloc> i) { 
	    for (Map.Entry me : blocs.entrySet()) {
	      sValueBloc vb = ((sValueBloc)me.getValue());
	      i.run(vb); } }
	  int runIterator_Counted(nIterator<sValue> i) { return runIterator_Counted(i, 0); }
	  int runIterator_Counted(nIterator<sValue> i, int c) { 
	    for (Map.Entry mev : values.entrySet()) {
	      sValue v = ((sValue)mev.getValue());
	      i.run(v, c); c++;
	    }
	    for (Map.Entry me : blocs.entrySet()) {
	      sValueBloc vb = ((sValueBloc)me.getValue());
	      c = vb.runIterator_Counted(i, c);
	    }
	    return c;
	  }
	  void runIterator_Filter(String t, nIterator<sValue> i) { 
	    for (Map.Entry mev : values.entrySet()) {
	      sValue v = ((sValue)mev.getValue());
	      if (v.type.equals(t)) i.run(v);
	    }
	    for (Map.Entry me : blocs.entrySet()) {
	      sValueBloc vb = ((sValueBloc)me.getValue());
	      vb.runIterator_Filter(t, i);
	    }
	  }
	  int runIterator_Filter_Counted(String t, nIterator<sValue> i) { return runIterator_Filter_Counted(t, i, 0); }
	  int runIterator_Filter_Counted(String t, nIterator<sValue> i, int c) { 
	    for (Map.Entry mev : values.entrySet()) {
	      sValue v = ((sValue)mev.getValue());
	      if (v.type.equals(t)) { i.run(v, c); c++; }
	    }
	    for (Map.Entry me : blocs.entrySet()) {
	      sValueBloc vb = ((sValueBloc)me.getValue());
	      c = vb.runIterator_Filter_Counted(t, i, c);
	    }
	    return c;
	  }
	  sValue searchValue(String t) { 
	    sValue e = values.get(t);
	    if (e != null) return e;
	    for (Map.Entry me : blocs.entrySet()) {
	      e = ( (sValueBloc)(me.getValue()) ).searchValue(t);
	      if (e != null) return e; }
	    return null;
	  }
	  int getCountOfType(String t) { return getCountOfType(t, 0); }
	  int getCountOfType(String t, int c) {
	    for (Map.Entry mev : values.entrySet()) {
	      sValue v = ((sValue)mev.getValue());
	      if (v.type.equals(t)) c++;
	    }
	    for (Map.Entry me : blocs.entrySet()) {
	      sValueBloc vb = ((sValueBloc)me.getValue());
	      c = vb.getCountOfType(t, c);
	    }
	    return c;
	  }
	  
	  /*
	   bloc event
	      add val
	      add bloc
	      delete val
	      delete bloc
	      delete
	      
	    val event
	      change 
	      delete
	  */
	  
	  public  sValueBloc doEvent(boolean t) { doevent = t; return this; }
	  public  sValueBloc addEventAddValue_Builder(nRunnable r) { r.builder = this; eventsAddVal.add(r); return this; }
	  public  sValueBloc addEventAddBloc_Builder(nRunnable r) { r.builder = this; eventsAddBloc.add(r); return this; }
	  public  sValueBloc addEventDelValue_Builder(nRunnable r) { r.builder = this; eventsDelVal.add(r); return this; }
	  public  sValueBloc addEventDelBloc_Builder(nRunnable r) { r.builder = this; eventsDelBloc.add(r); return this; }
	  public  sValueBloc addEventDelete_Builder(nRunnable r) { r.builder = this; eventsDelete.add(r); return this; }
	  ArrayList<nRunnable> eventsAddVal = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsAddBloc = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsDelVal = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsDelBloc = new ArrayList<nRunnable>();
	  ArrayList<nRunnable> eventsDelete = new ArrayList<nRunnable>();
	  
	  public  sValueBloc getBloc(String r) { return blocs.get(r); }
	  public  sValueBloc getLastBloc() { return last_created_bloc; }
	  public  sValue getValue(String r) { return values.get(r); }
	  public  sValueBloc newBloc(String n) { return new sValueBloc(this, n); }
	  public  sInt newInt(String n, String s, int v)       { return new sInt(this, v, n, s); }
	  public  sFlt newFlt(String n, String s, float v)     { return new sFlt(this, v, n, s); }
	  public  sBoo newBoo(String n, String s, boolean v)   { return new sBoo(this, v, n, s); }
	  public sInt newInt(int v, String n, String s)       { return new sInt(this, v, n, s); }
	  public sFlt newFlt(float v, String n, String s)     { return new sFlt(this, v, n, s); }
	  public sBoo newBoo(boolean v, String n, String s)   { return new sBoo(this, v, n, s); }
	  public sStr newStr(String n, String s, String v)    { return new sStr(this, v, n, s); }
	  public sVec newVec(String n, String s, PVector v)   { return new sVec(this, n, s).set(v); }
	  public sVec newVec(String n, String s)              { return new sVec(this, n, s); }
	  public sCol newCol(String n, String s, int v)     { return new sCol(this, n, s).set(v); }
	  public sCol newCol(String n, String s)              { return new sCol(this, n, s); }
	  public sRun newRun(String n, String s, nRunnable v)  { return new sRun(this, n, s, v); }
	  //sBlc newBlc(String n, String s) { return new sBlc(this, n, s); }
	  public sObj newObj(String n, Object v) { return new sObj(this, n, v); }
	  
	  Macro_Packet asPacket() { return new Macro_Packet("bloc").setBloc(this); }
	  
	  public DataHolder data; public sValueBloc parent = null, last_created_bloc = null; 
	  public sValue last_created_value = null;
	  public String ref, base_ref, type = "def", use = "";
	  public HashMap<String, sValue> values = new HashMap<String, sValue>();
	  public HashMap<String, sValueBloc> blocs = new HashMap<String, sValueBloc>();
	  public String adress; boolean doevent = true;
	  sValueBloc() {}    //only for superclass dataholder and saving
	  public sValueBloc(DataHolder d, String r) { base_ref = r;
	    while (d.blocs.get(r) != null) r = r + "_";
	    d.blocs.put(r, this); data = d; parent = d; ref = r; adress = "data/";}
	  public sValueBloc(sValueBloc b, String r) { base_ref = r;
	    while (b.blocs.get(r) != null) r = r + "'";
	    b.blocs.put(r, this); data = b.data; parent = b; 
	    ref = r; adress = b.adress + b.ref + "/"; 
	    if (parent.doevent) parent.last_created_bloc = this; 
	    if (parent.doevent) nRunnable.runEvents(parent.eventsAddBloc); }
	  public void frame() {
	    //for (Map.Entry b : values.entrySet()) { sValue s = (sValue)b.getValue(); s.frame(); }
	    //for (Map.Entry b : blocs.entrySet()) { sValueBloc s = (sValueBloc)b.getValue(); s.frame(); } 
	    
	    tmpblc.clear();
	    for (Map.Entry b : blocs.entrySet()) tmpblc.add((sValueBloc)b.getValue());
	    for (int i = tmpblc.size()-1 ; i >= 0 ; i--) tmpblc.get(i).frame();
	    tmpblc.clear();
	    tmpval.clear();
	    for (Map.Entry b : values.entrySet()) tmpval.add((sValue)b.getValue());
	    for (int i = tmpval.size()-1 ; i >= 0 ; i--) tmpval.get(i).frame();
	    tmpval.clear();
	  }
	  public void clear() {
	    clean();
	    parent.blocs.remove(ref, this);
	  }
	  ArrayList<sValue> tmpval = new ArrayList<sValue>();
	  ArrayList<sValueBloc> tmpblc = new ArrayList<sValueBloc>();
	  public void clean() {
	    //parent.blocs.remove(ref, this);
	    for (Map.Entry b : blocs.entrySet()) { sValueBloc s = (sValueBloc)b.getValue(); s.clean(); } 
	    
	    tmpval.clear();
	    for (Map.Entry b : values.entrySet()) tmpval.add((sValue)b.getValue());
	    for (int i = tmpval.size()-1 ; i >= 0 ; i--) tmpval.get(i).clean();
	    tmpval.clear();
	    
	    //for (Map.Entry b : values.entrySet()) { sValue s = (sValue)b.getValue(); s.clean(); } 
	    blocs.clear(); values.clear();
	    if (doevent) nRunnable.runEvents(eventsDelete); 
	    if (parent.doevent) nRunnable.runEvents(parent.eventsDelBloc);
	  }
	  public void empty() {
	    tmpblc.clear();
	    for (Map.Entry b : blocs.entrySet()) tmpblc.add((sValueBloc)b.getValue());
	    for (int i = tmpblc.size()-1 ; i >= 0 ; i--) tmpblc.get(i).clear();
	    tmpblc.clear();
	    
	    tmpval.clear();
	    for (Map.Entry b : values.entrySet()) tmpval.add((sValue)b.getValue());
	    for (int i = tmpval.size()-1 ; i >= 0 ; i--) tmpval.get(i).clean();
	    tmpval.clear();
	    
	    blocs.clear(); values.clear();
	  }
	  
	  void load_from_bloc(Save_Bloc sb) {
	    vlogln("svb load " + ref + "  /svb " + sb.blocs.size() + " /sv " + sb.datas.size());
	    
	    for (Map.Entry b : blocs.entrySet()) { 
	      sValueBloc s = (sValueBloc)b.getValue(); 
	      vlogln("test vb "+ s.ref);
	      Save_Bloc child_blocs = sb.getBloc(s.ref);
	      if (child_blocs != null) {
	        vlogln("got save bloc ");
	        s.load_from_bloc(child_blocs);
	      }
	    }
	    
	    for (Map.Entry b : values.entrySet()) { 
	      sValue s = (sValue)b.getValue(); 
	      vlogln("test vb "+ s.ref);
	      Save_Bloc child_blocs = sb.getBloc(s.ref);
	      if (child_blocs != null) {
	        vlogln("got save bloc ");
	        s.load_from_bloc(child_blocs);
	      }
	    }
	  }
	  
	  void load_values_from_bloc(Save_Bloc sb) {
	    vlogln("svb load " + ref + "  /svb " + sb.blocs.size() + " /sv " + sb.datas.size());
	    
	    for (Map.Entry b : values.entrySet()) { 
	      sValue s = (sValue)b.getValue(); 
	      vlogln("test vb "+ s.ref);
	      Save_Bloc child_blocs = sb.getBloc(s.ref);
	      if (child_blocs != null) {
	        vlogln("got save bloc ");
	        s.load_from_bloc(child_blocs);
	      }
	    }
	  }
	  public void preset_value_to_save_bloc(Save_Bloc sb) {
	    dlog("valuebloc " + ref + " saving to savebloc > clearing savebloc >");
	    sb.clear();
	    dlogln(" saving ref typ >");
	    sb.newData("__bloc_type", type);
	    sb.newData("__bloc_ref", ref);
	    sb.newData("__bloc_bas", base_ref);
	    sb.newData("__bloc_use", use);
	    
	    dlogln("saving under values >");
	    for (Map.Entry me : values.entrySet()) { 
	      sValue s = (sValue)me.getValue(); 
	      Save_Bloc sbv = sb.newBloc((String)me.getKey());
	      sbv.newData("__bloc_type", "val");
	      s.save_to_bloc(sbv); } 
	    
	    dlogln("done saving " + ref + " to savebloc");
	  }
	  
	  public String getHierarchy(boolean print_ref) {
	    String struct = "<bloc_"+type;
	    if (print_ref) struct += "_"+ref;
	    struct += ":"+"values<";
	    for (Map.Entry me : values.entrySet()) { 
	      sValue v = (sValue)me.getValue(); 
	      struct += "<val_"+v.type;
	      if (print_ref) struct += "_"+v.ref;
	      struct += ">";
	    } 
	    struct += ">blocs<";
	    for (Map.Entry me : blocs.entrySet()) { 
	      sValueBloc vb = (sValueBloc)me.getValue(); 
	      struct += vb.getHierarchy(print_ref);
	      struct += "-";
	    } 
	    struct += ">>";
	    return struct;
	  }
	  String getValueHierarchy(boolean print_ref) {
	    String struct = "<bloc_"+type;
	    if (print_ref) struct += "_"+ref;
	    struct += ":"+"values<";
	    for (Map.Entry me : values.entrySet()) { 
	      sValue v = (sValue)me.getValue(); 
	      struct += "<val_"+v.type;
	      if (print_ref) struct += "_"+v.ref;
	      struct += ">";
	    } 
	    struct += ">>";
	    return struct;
	  }
	  
	  
	  
	  public int preset_to_save_bloc(Save_Bloc sb) { return preset_to_save_bloc(sb, 0); }
	  int preset_to_save_bloc(Save_Bloc sb, int cnt) {
	    dlog("valuebloc " + ref + " saving to savebloc > val counter: " + cnt + " > clearing savebloc >");
	    sb.clear();
	    dlogln(" saving ref typ >");
	    sb.newData("__bloc_type", type);
	    sb.newData("__bloc_ref", ref);
	    sb.newData("__bloc_bas", base_ref);
	    sb.newData("__bloc_use", use);
	    
	    dlogln("saving under blocs >");
	    for (Map.Entry me : blocs.entrySet()) { 
	      sValueBloc svb = (sValueBloc)me.getValue(); 
	      Save_Bloc sb2 = sb.newBloc(svb.ref);
	      cnt = svb.preset_to_save_bloc(sb2, cnt); 
	    } 
	    dlogln("saving under values >");
	    for (Map.Entry me : values.entrySet()) { 
	      sValue s = (sValue)me.getValue(); 
	      Save_Bloc sbv = sb.newBloc((String)me.getKey());
	      sbv.newData("__bloc_type", "val");
	      cnt++;
	      s.save_to_bloc(sbv); } 
	    
	    dlogln("done saving " + ref + " to savebloc");
	    return cnt;
	  }

	  
	  sValue newValue(Save_Bloc sb) {
	    //logln(ref+" newValue from SB "+sb.name);
	    //logln("   type "+sb.getData("__bloc_type"));
	    sValue nv = null;
	    if (sb.getData("__bloc_type") != null && sb.getData("__bloc_type").equals("val")) {
	      String n = sb.getData("ref");
	      String s = sb.getData("shr");
	      String t = sb.getData("typ");
	      if (t.equals("int")) { nv = new sInt(this, 0, n, s);      nv.load_from_bloc(sb); }
	      if (t.equals("flt")) { nv = new sFlt(this, 0, n, s);      nv.load_from_bloc(sb); }
	      if (t.equals("boo")) { nv = new sBoo(this, false, n, s);  nv.load_from_bloc(sb); }
	      if (t.equals("str")) { nv = new sStr(this, "", n, s);     nv.load_from_bloc(sb); }
	      if (t.equals("vec")) { nv = new sVec(this, n, s);         nv.load_from_bloc(sb); }
//	      if (t.equals("col")) { nv = new sCol(this, n, s);         nv.load_from_bloc(sb); }
	      if (t.equals("run")) { nv = new sRun(this, n, s, null);   nv.load_from_bloc(sb); }
	      //if (t.equals("blc")) { nv = new sBlc(this, n, s);      nv.load_from_bloc(sb); }
	      if (t.equals("obj")) { nv = new sObj(this, n, null);   nv.load_from_bloc(sb); }
	    }
	    return nv;
	  }
	  
	  sValueBloc newBloc(Save_Bloc sb) {
	    dlogln("newbloc");
	    if (sb.getData("__bloc_type") != null && sb.getData("__bloc_ref") != null && 
	        sb.getData("__bloc_bas") != null && sb.getData("__bloc_use") != null && 
	        sb.getData("__bloc_type").equals("def")) {
	      dlogln("got it");
	      String b = sb.getData("__bloc_bas");
	      String u = sb.getData("__bloc_use");
	      sValueBloc vb = new sValueBloc(this, b);
	      vb.use = u;
	      for (Save_Bloc csb : sb.blocs) {
	        String type = csb.getData("__bloc_type");
	        if      (type != null && type.equals("def")) { vb.newBloc(csb); } 
	        else if (type != null && type.equals("val")) { vb.newValue(csb); }
	      }
	      return vb;
	    }
	    return null;
	  }
	  
	  public sValueBloc newBloc(Save_Bloc sb, String n) {
	    dlogln("newbloc");
	    if (sb.getData("__bloc_type") != null && sb.getData("__bloc_ref") != null && 
	        sb.getData("__bloc_bas") != null && sb.getData("__bloc_use") != null && 
	        sb.getData("__bloc_type").equals("def")) {
	      dlogln("got it");
	      //String b = sb.getData("__bloc_bas");
	      String u = sb.getData("__bloc_use");
	      sValueBloc vb = new sValueBloc(this, n);
	      vb.use = u;
	      for (Save_Bloc csb : sb.blocs) {
	        String type = csb.getData("__bloc_type");
	        if      (type != null && type.equals("def")) { vb.newBloc(csb); } 
	        else if (type != null && type.equals("val")) { vb.newValue(csb); }
	      }
	      return vb;
	    }
	    return null;
	  }
	  
	boolean DEBUG_SVALUE = false;
	void vlog(String s) {
	//  if (DEBUG_SVALUE) print(s);
	}
	void vlogln(String s) {
	//  if (DEBUG_SVALUE) println(s);
	}
	

	boolean DEBUG_DATA = false;
	void dlog(String s) {
	//  if (DEBUG_DATA) print(s);
	}
	void dlogln(String s) {
	//  if (DEBUG_DATA) println(s);
	}
	  
	}
