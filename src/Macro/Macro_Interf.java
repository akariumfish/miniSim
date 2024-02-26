package Macro;

public interface Macro_Interf {
	static final float GRID_SNAP_FACT = 0.375F;
	  static final int INPUT = 1, OUTPUT = 2, NO_CO = 3;
	  static final int HIDE = 0, REDUC = 1, OPEN = 2, DEPLOY = 3;
	  static final String OBJ_TOKEN = "@", GROUP_TOKEN = "¤", INFO_TOKEN = "#", BLOC_TOKEN = "~";
	  
	  final String[] bloc_types1 = {"vecCtrl", "numCtrl", "crossVec", 
	                                "midi", "preset", "toolNC" };
	}
