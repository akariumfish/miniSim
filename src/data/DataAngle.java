package data;

import java.util.ArrayList;

import RBase.RConst;
import RBase.REvent;

public class DataAngle {
	
	private float val;
	private ArrayList<REvent> events = new ArrayList<REvent>(0);
	
	public DataAngle() {
		val = 0;
	}
	
	public float get() {
		return val;
	}
	
	public DataAngle set(float i) {
		if (val != i) {
			val = i;
			if (val > RConst.PI) val -= RConst.PI * 2;
			else if (val < RConst.PI) val += RConst.PI * 2;
			for (REvent e : events) e.run();
		}
		return this;
	}
	
	public DataAngle addEvent(REvent e) {
		events.add(e);
		return this;
	}
	
	public DataAngle removeEvent(REvent e) {
		events.remove(e);
		return this;
	}

	public DataAngle copy() {
		return new DataAngle().set(val);
	}

	public DataAngle mult(float f) {
		this.set(val * f);
		return this;
	}
	
	public DataAngle add(float f) {
		this.set(val + f);
		return this;
	}
	
	public DataAngle mult(DataAngle f) {
		this.set(val * f.get());
		return this;
	}
	
	public DataAngle add(DataAngle f) {
		this.set(val + f.get());
		return this;
	}
}
