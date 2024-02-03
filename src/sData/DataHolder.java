package sData;

import java.util.Map;

import RApplet.Rapp;


/*
  
DataHolding
  svalue bloc map<string name, bloc>   each bloc loaded and saved independently
  runnables map<string name, run>      string-referanced runnables for saving
  
  frame()
    for bloc : map runFrameEventsIf() unFlagChanges()
*/


public class DataHolder extends sValueBloc {
  
  public DataHolder(Rapp a) {
    super(); 
	  app = a; ref = "data"; parent = this; data = this;
  }
  Rapp app;
  String[] types = {"flt", "int", "boo", "str", "vec", "col", "run", "obj"};
  
  int to_save_bloc(Save_Bloc sb) { 
//    dlogln("DataHolder saving to savebloc");
	  int cnt = super.preset_to_save_bloc(sb); 
//    dlogln("saved " + cnt + " values");
    return cnt;
  }

	boolean values_match(sValueBloc b1, sValueBloc b2) {
	  return b1.getValueHierarchy(true).equals(b2.getValueHierarchy(true)); }
	  
	public boolean values_found(sValueBloc from, sValueBloc in) {
	  boolean all_found = true;
	  for (Map.Entry me1 : from.values.entrySet()) { 
	    sValue v1 = (sValue)me1.getValue(); 
	    boolean found = false;
	    for (Map.Entry me2 : in.values.entrySet()) { 
	      sValue v2 = (sValue)me2.getValue(); 
	      found = found || v1.ref.equals(v2.ref);
	    } 
	    all_found = all_found && found;
	  } 
	  return all_found; }
	  
	
	boolean full_match(sValueBloc b1, sValueBloc b2) {
	  return b1.getHierarchy(true).equals(b2.getHierarchy(true)); }
	
	
	public sValueBloc copy_bloc(sValueBloc from, sValueBloc to) {
	  if (from != null && to != null) {
	    Save_Bloc b = new Save_Bloc("");
	    from.preset_to_save_bloc(b);
	    return to.newBloc(b, from.base_ref);
	  } return null;
	}
	public sValueBloc copy_bloc(sValueBloc from, sValueBloc to, String n) {
	  if (from != null && to != null) {
	    Save_Bloc b = new Save_Bloc("");
	    from.preset_to_save_bloc(b);
	    return to.newBloc(b, n);
	  } return null;
	}
	public sValue copy_value(sValue from, sValueBloc to) {
	  if (from != null && to != null) {
	    //logln("copy val from "+from.ref);
	    Save_Bloc b = new Save_Bloc(from.ref);
	    from.save_to_bloc(b); 
	    b.newData("__bloc_type", "val");
	    sValue v = to.newValue(b);
	    //logln("a "+v.ref);
	    return v;
	  } return null;
	}
	void transfer_values(sValueBloc from, sValueBloc to) {
	  if (from != null && to != null &&
	      from.getHierarchy(true).equals(to.getHierarchy(true))) {
	    Save_Bloc b = new Save_Bloc("");
	    from.preset_to_save_bloc(b);
	    to.load_from_bloc(b);
	  } 
	}
	
	void copy_bloc_values(sValueBloc from, sValueBloc to) {
	  if (from != null && to != null) {
	    Save_Bloc b = new Save_Bloc("");
	    from.preset_value_to_save_bloc(b);
	    to.newBloc(b, "copy");
	  } 
	}
	void copy_values(sValueBloc from, sValueBloc to) {
	  if (from != null && to != null) {
	    Save_Bloc b = new Save_Bloc("");
	    from.preset_value_to_save_bloc(b);
	    for (Save_Bloc bl : b.blocs) to.newValue(bl);
	  } 
	}
	public void transfer_bloc_values(sValueBloc from, sValueBloc to) {
	  if (from != null && to != null) {
	    Save_Bloc b = new Save_Bloc("");
	    from.preset_value_to_save_bloc(b);
	    to.load_values_from_bloc(b);
	  } 
	}
}












//void mysetup() {
//  Save_List sl = new Save_List();
//  Save_Bloc sb = new Save_Bloc("save data");
  
//  int a = 0, b = 1, c = 2;
//  println("start: a " + a + " b " + b + " c " + c);
  
//  //gather datas
//  sb.newData("a",str(a));
//  sb.newData("b",str(b));
//  sb.newData("c",str(c));
  
//  //change data
//  sb.setData("b",str(5));
  
//  //save
//  sb.save_to("savetest.txt");
  
//  //load
//  sb.load_from("savetest.txt");
  
//  //retrieve data
//  a = int(sb.getData("a"));
//  b = int(sb.getData("b"));
//  c = int(sb.getData("c"));
  
//  println("end: a " + a + " b " + b + " c " + c);
//}

/*
 //* Listing files in directories and subdirectories
 //* by Daniel Shiffman.  
 //* 
 //* This example has three functions:<br />
 //* 1) List the names of files in a directory<br />
 //* 2) List the names along with metadata (size, lastModified)<br /> 
 //*    of files in a directory<br />
 //* 3) List the names along with metadata (size, lastModified)<br />
 //*    of files in a directory and all subdirectories (using recursion) 



import java.util.Date;

void setup() {

  // Using just the path of this sketch to demonstrate,
  // but you can list any directory you like.
  String path = sketchPath();

  println("Listing all filenames in a directory: ");
  String[] filenames = listFileNames(path);
  printArray(filenames);

  println("\nListing info about all files in a directory: ");
  File[] files = listFiles(path);
  for (int i = 0; i < files.length; i++) {
    File f = files[i];    
    println("Name: " + f.getName());
    println("Is directory: " + f.isDirectory());
    println("Size: " + f.length());
    String lastModified = new Date(f.lastModified()).toString();
    println("Last Modified: " + lastModified);
    println("-----------------------");
  }

  println("\nListing info about all files in a directory and all subdirectories: ");
  ArrayList<File> allFiles = listFilesRecursive(path);

  for (File f : allFiles) {
    println("Name: " + f.getName());
    println("Full path: " + f.getAbsolutePath());
    println("Is directory: " + f.isDirectory());
    println("Size: " + f.length());
    String lastModified = new Date(f.lastModified()).toString();
    println("Last Modified: " + lastModified);
    println("-----------------------");
  }

  noLoop();
}

// Nothing is drawn in this program and the draw() doesn't loop because
// of the noLoop() in setup()
void draw() {
}

// This function returns all the files in a directory as an array of Strings  
String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

// This function returns all the files in a directory as an array of File objects
// This is useful if you want more info about the file
File[] listFiles(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    File[] files = file.listFiles();
    return files;
  } else {
    // If it's not a directory
    return null;
  }
}

// Function to get a list of all files in a directory and all subdirectories
ArrayList<File> listFilesRecursive(String dir) {
  ArrayList<File> fileList = new ArrayList<File>(); 
  recurseDir(fileList, dir);
  return fileList;
}

// Recursive function to traverse subdirectories
void recurseDir(ArrayList<File> a, String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    // If you want to include directories in the list
    a.add(file);  
    File[] subfiles = file.listFiles();
    for (int i = 0; i < subfiles.length; i++) {
      // Call this function on all files in this directory
      recurseDir(a, subfiles[i].getAbsolutePath());
    }
  } else {
    a.add(file);
  }
}
*/





 
