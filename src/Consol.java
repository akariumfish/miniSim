import RApplet.RConst;
import processing.core.PApplet;

public class Consol {
	public static void main(String[] args) {
		
		test(1.23456F);
		test(-1.23456F);
	}

	static void prt(float i) {
		PApplet.println("for : " + PApplet.str(i));
		for (int tn = 2 ; tn < 6 ; tn++) {
			PApplet.print("" + tn + " : " + RConst.trimFlt(i, tn) + "    ");
		}
		PApplet.println();
		
	}
	static void test(float f) {
		float i = f;
		prt(i);
		for (int m = 0 ; m < 4 ; m++) {
			i = i * 10;
			prt(i); }
		for (int m = 0 ; m < 3 ; m++) {
			i = i * 100;
			prt(i); }
		i = f;
		for (int m = 0 ; m < 4 ; m++) {
			i = i / 10;
			prt(i); }
		for (int m = 0 ; m < 3 ; m++) {
			i = i / 100;
			prt(i); }
		PApplet.println("");
	}
}
