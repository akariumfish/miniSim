package data;

import java.util.ArrayList;

import RBase.REvent;

public class DataFloat {
	private float val;
	private ArrayList<REvent> events = new ArrayList<REvent>(0);
	
	public DataFloat() {
		val = 0;
	}
	
	public float get() {
		return val;
	}
	
	public DataFloat set(float i) {
		if (val != i) {
			val = i;
			runEvent();
		}
		return this;
	}
	
	DataFloat runEvent() {
		for (REvent e : events) e.run();
		return this;
	}
	
	public DataFloat addEvent(REvent e) {
		events.add(e);
		return this;
	}
	
	public DataFloat removeEvent(REvent e) {
		events.remove(e);
		return this;
	}

	public DataFloat copy() {
		return new DataFloat().set(val);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DataFloat)) {
			return false;
		}
		final DataFloat p = (DataFloat) obj;
		return val == p.get();
	}

	public DataFloat mult(float f) {
		this.set(val * f);
		return this;
	}

	public DataFloat mult(DataFloat f) {
		this.set(val * f.get());
		return this;
	}

	public DataFloat div(float f) {
		this.set(val / f);
		return this;
	}
	
	public DataFloat div(DataFloat f) {
		this.set(val / f.get());
		return this;
	}

	public DataFloat add(float f) {
		this.set(val + f);
		return this;
	}

	public DataFloat add(DataFloat f) {
		this.set(val + f.get());
		return this;
	}

	public DataFloat sub(float f) {
		this.set(val - f);
		return this;
	}
	
	public DataFloat sub(DataFloat f) {
		this.set(val - f.get());
		return this;
	}
}
