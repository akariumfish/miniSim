package data;

import java.util.ArrayList;

import RBase.RConst;
import RBase.REvent;

public class DataVector {
	
	private DataFloat x,y,l;
	private DataAngle a;
	
	ArrayList<REvent> onChange = new ArrayList<REvent>(0);
	
	public DataVector() {
		this.x = new DataFloat();
		this.y = new DataFloat();
		this.l = new DataFloat();
		this.a = new DataAngle();
	}
	
	private void runEvent() {
		for (REvent e : onChange) {
			e.run();
		}
	}

	public DataVector addEvent(REvent event) {
		onChange.add(event);
		return this;
	}

	public DataVector removeEvent(REvent event) {
		onChange.remove(event);
		return this;
	}
	
	public DataFloat getDatax() {
		return x;
	}
	
	public DataFloat getDatay() {
		return y;
	}
	
	public DataFloat getDatal() {
		return l;
	}
	
	public DataAngle getDataa() {
		return a;
	}
	
	public float getx() {
		return x.get();
	}
	
	public float gety() {
		return y.get();
	}
	
	public float getl() {
		return l.get();
	}
	
	public float geta() {
		return a.get();
	}
	
	public DataVector copy() {
		return new DataVector().set(x.get(), y.get());
	}
	
	public DataVector set(float x, float y) {
		this.x.set(x);
		this.y.set(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}

	public DataVector set(DataFloat x, DataFloat y) {
		this.x.set(x.get());
		this.y.set(y.get());
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}

	public DataVector set(DataVector v) {
		this.x.set(v.x.get());
		this.y.set(v.y.get());
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}

	public DataVector setx(float x) {
		this.x.set(x);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}

	public DataVector sety(float y) {
		this.y.set(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}

	public DataVector setx(DataFloat x) {
		this.x.set(x.get());
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}

	public DataVector sety(DataFloat y) {
		this.y.set(y.get());
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector seta(float a) {
		this.rotate(a - heading());
		return this;
	}

//	public DataVector setFromAngle(float angle) {
//		this.set((float)Math.cos(angle),(float)Math.sin(angle));
//		return this;
//	}
//
//	public DataVector setFromAngle(DataAngle angle) {
//		this.set((float)Math.cos(angle.get()),(float)Math.sin(angle.get()));
//		return this;
//	}
	
	public DataVector add(float x, float y) {
		this.x.add(x);
		this.y.add(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector add(DataVector v) {
		this.x.add(v.x);
		this.y.add(v.y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector addx(float x) {
		this.x.add(x);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector addy(float y) {
		this.y.add(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector addx(DataFloat x) {
		this.x.add(x);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector addy(DataFloat y) {
		this.y.add(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector sub(float x, float y) {
		this.x.sub(x);
		this.y.sub(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector sub(DataVector v) {
		this.x.sub(v.x);
		this.y.sub(v.y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector subx(float x) {
		this.x.sub(x);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector suby(float y) {
		this.y.sub(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector subx(DataFloat x) {
		this.x.sub(x);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector suby(DataFloat y) {
		this.y.sub(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector mult(float m) {
		this.x.mult(m);
		this.y.mult(m);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector mult(DataFloat m) {
		this.x.mult(m);
		this.y.mult(m);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector multx(DataFloat x) {
		this.x.mult(x);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector multy(DataFloat y) {
		this.y.mult(y);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector normalize() {
		float m = mag();
		if (m != 0 && m != 1) {
			this.x.div(m);
			this.y.div(m);
			this.l.set(1);
			this.a.set(heading());
			runEvent();
		}
		return this;
	}

	public DataVector setMag(float len) {
		float m = mag();
		if (m != 0) {
			this.x.mult(len/m);
			this.y.mult(len/m);
			this.l.set(mag());
			this.a.set(heading());
			runEvent();
		}
		return this;
	}

	public DataVector limit(float max) {
		if (magSq() > max*max) {
			this.setMag(max);
		}
		return this;
	}

	public DataVector rotate(float theta) {
		float temp = x.get();
		float c = (float) Math.cos(theta);
		float s = (float) Math.sin(theta);
		x.set(x.get()*c - y.get()*s);
		y.set(temp*s + y.get()*c);
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector lerp(DataVector v, float amt) {
		x.set(lerp(x.get(), v.getx(), amt));
		y.set(lerp(y.get(), v.gety(), amt));
		this.l.set(mag());
		this.a.set(heading());
		runEvent();
		return this;
	}
	
	public DataVector project(DataVector vx, DataVector vy) {
		this.set(vx.copy().mult(x)).add(
				 vy.copy().mult(y)
				 );
		return this;
	}

	public float mag() {
		return (float) Math.hypot(x.get(),y.get());
	}

	public float magSq() {
		return (x.get()*x.get() + y.get()*y.get());
	}

	public float dist(DataVector v) {
		float dx = x.get() - v.x.get();
		float dy = y.get() - v.y.get();
		return (float) Math.hypot(dx,dy);
	}

	public float dot(DataVector v) {
		return x.get()*v.x.get() + y.get()*v.y.get();
	}

	public float heading() {
		float angle = (float) Math.atan2(y.get(), x.get());
		return angle;
	}

	public float angleBetween(DataVector v2) {
		DataVector v1 = this;
		// We get NaN if we pass in a zero vector which can cause problems
		// Zero seems like a reasonable angle between a (0,0,0) vector and something else
		if (v1.x.get() == 0 && v1.y.get() == 0) return 0.0f;
		if (v2.x.get() == 0 && v2.y.get() == 0) return 0.0f;

		double dot = ( v1.x.get() * v2.x.get() ) + ( v1.y.get() * v2.y.get() );
		double v1mag = Math.sqrt(( v1.x.get() * v1.x.get() ) + ( v1.y.get() * v1.y.get() ));
		double v2mag = Math.sqrt(( v2.x.get() * v2.x.get() ) + ( v2.y.get() * v2.y.get() ));
		// This should be a number between -1 and 1, since it's "normalized"
		double amt = dot / (v1mag * v2mag);
		// But if it's not due to rounding error, then we need to fix it
		// http://code.google.com/p/processing/issues/detail?id=340
		// Otherwise if outside the range, acos() will return NaN
		// http://www.cppreference.com/wiki/c/math/acos
		if (amt <= -1) {
			return RConst.PI;
		} else if (amt >= 1) {
			// http://code.google.com/p/processing/issues/detail?id=435
			return 0;
		}
		return (float) Math.acos(amt);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DataVector)) {
			return false;
		}
		final DataVector p = (DataVector) obj;
		return x.get() == p.x.get() && y.get() == p.y.get();
	}
	
	public String toString() {
		return new String("[" + x.get() + "," + y.get() + "]");
	}

	static public final float lerp(float start, float stop, float amt) {
		return start + (stop-start) * amt;
	}
}
