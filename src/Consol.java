import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import RApplet.RConst;
import processing.core.PApplet;

public class Consol {
	public static void main(String[] args) {
		
		String dir = "/directory_maze/";
		
		
		try {
		    Path path = Paths.get(dir);
		    Files.createDirectories(path);
		    
		    build_stage(dir, 0);
		    
		    System.out.println("Directory maze is created!");
		} catch (IOException e) {
		    System.err.println("Failed to create directory!" + e.getMessage());
		}
		
		
		
		
//		test(1.23456F);
//		test(-1.23456F);
		
		
	}

	static int depth = 8;
	static int spread = 4;
	
	static void build_stage(String dir, int d) {
		if (d < depth) {
			for (int i = 0 ; i < spread ; i++) {
				String ndir = dir + i + "/";
				Path path = Paths.get(ndir);
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
				    System.err.println("Failed to create directory!" + e.getMessage());
				}
				build_stage(ndir, d+1);
			}
		} else {
			try{
		        Writer output = null;
				String fileName = "found.txt";
				File f = new File (dir);
				File actualFile = new File (f, fileName);
		        output = new BufferedWriter(new FileWriter(actualFile));
		        output.close();
		        System.out.println("File has been written");
	
		    } catch (Exception e){
		        System.out.println("Could not create file");
		    }
		}
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
