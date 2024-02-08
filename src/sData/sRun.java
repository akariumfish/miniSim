package sData;

public class sRun extends sValue {
	  public String getString() { return ref; }
	  public void clear() { super.clear(); }
	  private nRunnable val;
	  public sRun(sValueBloc b, String n, String s, nRunnable r) { super(b, "run", n, s);  val = r; }
	  public nRunnable get() { return val; } // !!!! carefull, no event if run outside !!!!
	  public sRun run() { val.run(); doChange(); return this; }
	  public sRun set(nRunnable v) { val = v; return this; }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb); }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb); }
	}
