package RBase;

import data.DataAngle;

public class Vector {

	private float x,y;

	public Vector() {
		this.x = 0;
		this.y = 0;
	}

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getx() {
		return x;
	}
	
	public float gety() {
		return y;
	}
	
	public Vector setx(float x) {
		this.x = x;
		return this;
	}
	
	public Vector sety(float y) {
		this.y = y;
		return this;
	}

	public Vector set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector set(Vector v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public Vector setFromAngle(float angle) {
		return this.set((float)Math.cos(angle),(float)Math.sin(angle));
	}

	public Vector setFromAngle(DataAngle angle) {
		return this.set((float)Math.cos(angle.get()),(float)Math.sin(angle.get()));
	}
	
	public Vector copy() {
		return new Vector(x, y);
	}

	public float mag() {
		return (float) Math.hypot(x,y);
	}

	public float magSq() {
		return (x*x + y*y);
	}

	public Vector add(Vector v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vector sub(Vector v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vector sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public Vector addx(float x) {
		this.x += x;
		return this;
	}
	
	public Vector addy(float y) {
		this.y += y;
		return this;
	}
	
	public Vector subx(float x) {
		this.x -= x;
		return this;
	}
	
	public Vector suby(float y) {
		this.y -= y;
		return this;
	}

	public Vector mult(float n) {
		x *= n;
		y *= n;
		return this;
	}

	public Vector div(float n) {
		x /= n;
		y /= n;
		return this;
	}

	public float dist(Vector v) {
		float dx = x - v.x;
		float dy = y - v.y;
		return (float) Math.hypot(dx,dy);
	}

	public float dot(Vector v) {
		return x*v.x + y*v.y;
	}

	public float dot(float x, float y) {
		return this.x*x + this.y*y;
	}

	public Vector normalize() {
		float m = mag();
		if (m != 0 && m != 1) {
			div(m);
		}
		return this;
	}

	public Vector limit(float max) {
		if (magSq() > max*max) {
			normalize();
			mult(max);
		}
		return this;
	}

	public Vector setMag(float len) {
		normalize();
		mult(len);
		return this;
	}

	public float heading() {
		float angle = (float) Math.atan2(y, x);
		return angle;
	}

	public Vector rotate(float theta) {
		float temp = x;
		x = (float) (x*Math.cos(theta) - y*Math.sin(theta));
		y = (float) (temp*Math.sin(theta) + y*Math.cos(theta));
		return this;
	}


	public Vector lerp(Vector v, float amt) {
		x = lerp(x, v.x, amt);
		y = lerp(y, v.y, amt);
		return this;
	}

	static public final float lerp(float start, float stop, float amt) {
		return start + (stop-start) * amt;
	}

	public float angleBetween(Vector v2) {
		Vector v1 = this;
		// We get NaN if we pass in a zero vector which can cause problems
		// Zero seems like a reasonable angle between a (0,0,0) vector and something else
		if (v1.x == 0 && v1.y == 0) return 0.0f;
		if (v2.x == 0 && v2.y == 0) return 0.0f;

		double dot = v1.x * v2.x + v1.y * v2.y;
		double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
		double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y);
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
		if (!(obj instanceof Vector)) {
			return false;
		}
		final Vector p = (Vector) obj;
		return x == p.x && y == p.y;
	}
	
	public String toString() {
		return new String("[" + x + "," + y + "]");
	}

	public int hashCode() {
		int result = 1;
		result = 31 * result + Float.floatToIntBits(x);
		result = 31 * result + Float.floatToIntBits(y);
		return result;
	}
}

