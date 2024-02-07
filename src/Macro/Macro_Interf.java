package Macro;

public interface Macro_Interf {
	static final float GRID_SNAP_FACT = 0.375F;
	  static final int INPUT = 1, OUTPUT = 2, NO_CO = 3;
	  static final int HIDE = 0, REDUC = 1, OPEN = 2, DEPLOY = 3;
	  static final String OBJ_TOKEN = "@", GROUP_TOKEN = "¤", INFO_TOKEN = "#", BLOC_TOKEN = "~";
	  
	  final String[] bloc_types1 = {"com", "in", "out", "trig", "switch", "gate", "not", "pulse", "frame", 
	                                "bin", "bool", "var", "rng"};
	  final String[] bloc_types2 = {"vecXY", "vecMD", "vecCtrl", "numCtrl", "mouse", "keyb", "crossVec", 
	                                "midi", "preset", "tool", "tooltri", "toolbin", "toolNC"}; //"cursor", "pancstm", "tmpl", 
	  final String[] bloc_types3 = {"colRGB", "btrig", "bswitch", "comp", "ramp", "chan", "data", "pan", 
              						"panbin", "pansld", "pangrph", "calc" , "menu" };

	  final String[] bloc_types4 = {"com", "in", "out", "trig", "btrig", "switch", "gate", "pulse", "not"};

	  final String[] bloc_types5 = {"bin", "bool", "calc", "comp", "var", "chan", "data", "menu"};
	                                
	  final String[] bloc_info1 = {"commentary", "sheet input", "sheet output", 
	                               "trigger > bang", "switch > bool", "can control msg circulation", 
	                               "invert bool", "let one bang of many pass", "retard msg for one frame", 
	                               "bang <> bool", "AND OR operator", "store received msg, send copy at bang", 
	                               "random numbers"};
	  final String[] bloc_info2 = {"vector <> x / y coords", "vector <> magnitude and heading", 
	                               "easy modify vector sValue", "easy modify numbers sValue", 
	                               "get mouse info", "get selected keyboard inputs", 
	                               "keyboard direction cross ZQSD > vec", 
	                               "not working", "load selected preset at bang", "create a new toolpanel", 
	                               "up to 3 toolpanel triggers", "up to 2 toopanel switchs", 
	                               "add an incr/fact numerical sValue controller to the toolpanel"};
	  final String[] bloc_info3 = {"color <> r / g / b", "the big version", "the big version", "comparator", 
					              "lerp value from a to b, driven by bang, multiple ranpe shape", 
					              "invisible connexion by keywords", 
					              "access sheet sValue, can modify and get change", 
	                              "create a new windowpanel", 
					              "windowpanel binary ctrl", "add a slider to the windowpanel", 
					              "save consecutive inputs as a graph displayed in a windowpanel", 
					              "arythmetic calculator", 
					              "can open sheet menu" };
	}
