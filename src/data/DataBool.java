package data;

import java.util.ArrayList;

import RBase.REvent;

public class DataBool {
	private boolean val;
	private ArrayList<REvent> onChangeEvents = new ArrayList<REvent>(0);
	private ArrayList<REvent> onTrueEvents = new ArrayList<REvent>(0);
	private ArrayList<REvent> onFalseEvents = new ArrayList<REvent>(0);
	
	public DataBool() {
		val = false;
	}
	
	public boolean get() {
		return val;
	}
	
	public DataBool set(boolean i) {
		if (val != i) {
			for (REvent e : onChangeEvents) e.run();
			if (val) for (REvent e : onTrueEvents) e.run();
			else for (REvent e : onFalseEvents) e.run();
		}
		val = i;
		return this;
	}
	
	public DataBool not() {
		set(!val);
		return this;
	}
	
	public DataBool addOnChangeEvent(REvent e) {
		onChangeEvents.add(e);
		return this;
	}
	
	public DataBool removeOnChangeEvent(REvent e) {
		onChangeEvents.remove(e);
		return this;
	}
	
	public DataBool addOnTrueEvent(REvent e) {
		onTrueEvents.add(e);
		return this;
	}
	
	public DataBool removeOnTrueEvent(REvent e) {
		onTrueEvents.remove(e);
		return this;
	}
	
	public DataBool addOnFalseEvent(REvent e) {
		onFalseEvents.add(e);
		return this;
	}
	
	public DataBool removeOnFalseEvent(REvent e) {
		onFalseEvents.remove(e);
		return this;
	}
}
