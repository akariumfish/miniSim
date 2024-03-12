package sData;

import RApplet.Rapp;
import RApplet.RConst;

public class sCol extends sValue {
	  public int asCol() { return val; }
	public String getString() { return RConst.trimFlt(app.red(val)) + "," + 
			RConst.trimFlt(app.green(val)) + "," + 
			RConst.trimFlt(app.blue(val)); }
	public void clear() { super.clear(); val = def; }
	private int val = 0, def = 0;
	Rapp app;
	public sCol(sValueBloc b, String n, String s) { 
		super(b, "col", n, s); 
		app = b.data.app; 
		val = app.color(255); 
		def = app.color(255); }
	public float getred() { return app.red(val); }
	public float getgreen() { return app.green(val); }
	public float getblue() { return app.blue(val); }
	public float getalpha() { return app.alpha(val); }
	public int get() { return val; }
	public sCol set(int c) { 
		run_events_allset(); 
		if (c != val) {
			val = c;  
			doChange(); 
		} 
		return this;
	}
	public sCol set(int r, int g, int b) { 
		set(app.color(r,g,b));
		return this;
	}
	public sCol set(int r, int g, int b, int a) { 
		set(app.color(r,g,b,a));
		return this;
	}
	void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
		svb.newData("c", val); }
	void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
		set(svb.getInt("c")); }
}
