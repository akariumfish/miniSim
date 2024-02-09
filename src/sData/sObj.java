package sData;

import Macro.Macro_Sheet;

public class sObj extends sValue {
	  public Object asObj() { return val; }
	public String getString() { return ref; }
	  public void clear() { super.clear(); }
	  private Object val = null;
	  private boolean is_value = false, is_bloc = false, is_sheet = false;
	  boolean isValue() { return is_value; }
	  boolean isBloc() { return is_bloc; }
	  public boolean isSheet() { return is_sheet; }
	  sObj(sValueBloc b, String n, Object r) { super(b, "obj", n, "obj");  val = r; }
	  public sObj set(Object r) { 
	    if (r instanceof sValue) return set((sValue)r); 
	    if (r instanceof sValueBloc) return set((sValueBloc)r);  
	    if (r instanceof Macro_Sheet) return set((Macro_Sheet)r);  
	    is_value = false; is_bloc = false; is_sheet = false;
	    if (val != r) { val = r; doChange(); } 
	    return this; }
	  sObj set(sValue r) { 
	    is_value = true; is_bloc = false; is_sheet = false; 
	    if (val != r) { val = r; doChange(); } return this; }
	  sObj set(sValueBloc r) { 
	    is_value = false; is_bloc = true; is_sheet = false; 
	    if (val != r) { val = r; doChange(); } return this; }
	  sObj set(Macro_Sheet r) { 
	    is_value = false; is_bloc = false; is_sheet = true; 
	    if (val != r) { val = r; doChange(); } return this; }
	  public Object get() { return val; }
	  
	  sValue asValue() { if (isValue()) return (sValue)val; else return null; }
	  sValueBloc asBloc() { if (isBloc()) return (sValueBloc)val; else return null; }
	  public Macro_Sheet asSheet() { if (isSheet()) return (Macro_Sheet)val; else return null; }
	  
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb); }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb); }
	}
