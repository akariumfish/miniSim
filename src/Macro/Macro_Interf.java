package Macro;

public interface Macro_Interf {
	static final float GRID_SNAP_FACT = 0.375F;
	  static final int INPUT = 1, OUTPUT = 2, NO_CO = 3;
	  static final int HIDE = 0, REDUC = 1, OPEN = 2, DEPLOY = 3;
	  static final int shape_nb = 5, max_vertices = 3;
	  static final int TRIG = 0, LINE = 1, CIRCLE = 2, ARROW = 3, RECT = 4;
	  static final int[] shapes_vertices = {3, 2, 2, 3, 2};
	  static final String[] shapes_names = {"TRIG", "LINE", "CIRCLE", "ARROW", "RECT"};
	  static final String 	OBJ_TOKEN = "@", GROUP_TOKEN = "¤", 
			  				INFO_TOKEN = "#", BLOC_TOKEN = "~", LINK_TOKEN = "%";
	}
