package sData;

import java.util.ArrayList;

import RApplet.Rapp;
import processing.core.PApplet;
import RApplet.RConst;


//#######################################################################
//##                        SAVING N LOADING                           ##
//#######################################################################



public class Save_Bloc {
	
	public static Rapp app;
	  
	public Save_Bloc(String n, int i) { name = RConst.copy(n); index = i; }
	  public Save_Bloc(String n) { name = RConst.copy(n); index = 0; }
	  
	  String name;
	  int index;
	  ArrayList<Save_Data> datas = new ArrayList<Save_Data>();
	  ArrayList<Save_Bloc> blocs = new ArrayList<Save_Bloc>();
	  
	  
	  
	  void runIterator(nIterator<Save_Bloc> i) { 
	    int count = 0;
	    for (Save_Bloc b : blocs) { count++; i.run(b); i.run(b, count); }
	  }
	  
	  Save_Data newData(String n, String d) {
	    Save_Data sd = new Save_Data(n, d); datas.add(sd); return sd; }
	    
	  Save_Data newData(String n, int d) { return newData(n, PApplet.str(d)); } 
	  Save_Data newData(String n, float d) { return newData(n, PApplet.str(d)); } 
	  Save_Data newData(String n, boolean d) { if (d) return newData(n, "1"); else return newData(n, "0"); } 
	  
	  Save_Bloc newBloc(String n) {
	    Save_Bloc sd = new Save_Bloc(n); blocs.add(sd); return sd; }//, blocs.size()
	  Save_Bloc addBloc(Save_Bloc n) {
	    blocs.add(n); return n; }
	  
	  void setData(String n, String d) { for (Save_Data sd : datas) if (sd.name.equals(n)) { sd.set(d); return; } }
	  
	  String getData(String n) { for (Save_Data sd : datas) if (sd.name.equals(n)) return sd.get(); return ""; }
	  int getInt(String n) { 
		  float f = Rapp.parseFlt(getData(n));
		  if (!PApplet.str(f).equals("NaN"))
			  return PApplet.parseInt(f); 
		  else return 0; }
	  float getFloat(String n) { 
		  float f = Rapp.parseFlt(getData(n));
		  if (!PApplet.str(f).equals("NaN")) return f; 
		  else return 0; }
	  boolean getBoolean(String n) { if (getData(n).equals("1")) return true; else return false; }
	  
	  Save_Bloc getBloc(String n) { for (Save_Bloc sd : blocs) if (sd.name.equals(n)) return sd; return null; }
	  
	  public void clear() {
	    for (Save_Data d : datas) d.clear();
	    datas.clear();
	    for (Save_Bloc b : blocs) b.clear();
	    blocs.clear();
	  }
	  
	  
	  public void copy_from(Save_Bloc svb) { 
		  app.slog("bloc - copy from");
	    clear();
	    name = RConst.copy(svb.name); index = svb.index; 
	    Save_List sl = new Save_List();
	    sl.init(svb.size());
	    svb.to_list(sl);
	    from_list(sl);
	  }
	  
	  public void save_to(String savepath) { 
		  app.slog("bloc - save to");
	    Save_List sl = new Save_List();
	    sl.init(size());
	    to_list(sl);
	    app.saveStrings(savepath, sl.list);
	  }
	  public boolean load_from(String savepath) { 
		  app.slog("bloc - load from");
	    clear();
	    String[] load = app.loadStrings(savepath);
	    Save_List sl = new Save_List();
	    boolean b = (load != null);
	    if (b) sl.init(load);
	    if (b) from_list(sl);
	    return b;
	  }
	  
	  void to_list(Save_List sl) {
		  app.slog("Bloc - to string - start");
	    sl.put("name", name);
	    sl.put("total size", PApplet.str(size()));
	    
	    sl.put("datas nb", PApplet.str(datas.size()));
	    int leng = 0;
	    for (Save_Data sd : datas) leng += sd.size();
	    sl.put("datas total size", PApplet.str(leng));
	    app.slog("datas to string start");
	    for (Save_Data sd : datas) { sl.put("name", sd.name); sl.put("data", sd.data); }
	    app.slog("datas to string end");
	    
	    sl.put("child blocs nb", PApplet.str(blocs.size()));
	    leng = 0;
	    for (Save_Bloc sd : blocs) leng += sd.size();
	    sl.put("child blocs total size", PApplet.str(leng));
	    app.slog("child blocs to string start");
	    for (Save_Bloc sb : blocs) sb.to_list(sl);
	    app.slog("child blocs to string end");
	    
	    app.slog("Bloc - to string - end");
	  }
	  
	  int total_size = 0;
	  int datas_nb = 0;
	  int data_size = 0;
	  int bloc_nb = 0;
	  int blocs_total_size = 0;
	  
	  void from_list(Save_List sl) {
//	    slog("Bloc - from string - start");
	    
	    name = sl.get("name");
	    total_size = sl.getInt("total size");
	    
	    datas_nb = sl.getInt("datas nb");
	    data_size = sl.getInt("datas total size");
//	    slog("datas from string start");
	    for (int i = 0; i < datas_nb ; i++) newData(sl.get("name"), sl.get("data"));
//	    slog("datas from string end");
	    
	    bloc_nb = sl.getInt("blocs nb");
	    blocs_total_size = sl.getInt("child blocs total size");
//	    slog("child blocs from string start");
	    for (int i = 0; i < bloc_nb ; i++) {
	      Save_Bloc sb = newBloc("");
	      sb.from_list(sl);
	    }
//	    slog("child blocs from string end");
	    
//	    slog("Bloc - from string - end");
	  }
	  int size() { 
	    int s = 6;
	    for (Save_Data sd : datas) s += sd.size();
	    for (Save_Bloc sb : blocs) s += sb.size();
	    return s; 
	  }
	}

class Save_List {
  String[] list;
  int index = 0;
  
  void put(String log, String s) { 
    list[index] = RConst.copy(s); 
//    slog("put " + log + " " + index + " " + s); 
    index++; }
  String get(String log) { 
    if (index < list.length && index >= 0) {
//    slog("get " + log + " " + index + " " + list[index]); 
    index++; 
    return list[index-1]; }
    else return "";
  }
  int getInt(String log) { return (int)Rapp.parseFlt(get(log)); }
  
  void init(int size) { list = new String[size]; index = 0; }
  void init(String[] l) { list = l; index = 0; }
}


class Save_Data {
  String name, data;
  Save_Data(String n, String d) { name = RConst.copy(n); data = RConst.copy(d); }
  int size() { return 2; }
  String get() { return data; }
  void set(String d) { data = RConst.copy(d); }
  void clear() { }
}