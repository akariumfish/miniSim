package RBase;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public interface InputData {
	
//	//Manette selection
//	final static String[] manetteName = {"Manette 1","Manette 2"};
//
//	final static String buttonCode = "Button "; // + nb
//	final static String hatCode = "Hat Switch";
//	final static String xAxisCode = "X Axis";
//	final static String yAxisCode = "Y Axis";
//	final static String zAxisCode = "Z Axis";
//	final static String zRotCode = "Z Rotation";
	
	// index du Bool dans la liste de Bool de la manette
	final static int INPUT_BOOL_SHARED = 	0;
	final static int INPUT_BOOL_NONE = 		1;
	final static int INPUT_BUTTON_Y = 		2;
	final static int INPUT_BUTTON_B = 		3;
	final static int INPUT_BUTTON_A = 		4;
	final static int INPUT_BUTTON_X = 		5;
	final static int INPUT_BUTTON_Lb = 		6;
	final static int INPUT_BUTTON_Rb = 		7;
	final static int INPUT_BUTTON_Lt = 		8;
	final static int INPUT_BUTTON_Rt = 		9;
	final static int INPUT_BUTTON_Select =  10;
	final static int INPUT_BUTTON_Start = 	11;
	final static int INPUT_BUTTON_LSTICK = 	12;
	final static int INPUT_BUTTON_RSTICK = 	13;
	final static int INPUT_BOOL_LRb_USE = 		14;
	final static int INPUT_BOOL_ARROW_UP = 		15;
	final static int INPUT_BOOL_ARROW_DOWN = 	16;
	final static int INPUT_BOOL_ARROW_LEFT = 	17;
	final static int INPUT_BOOL_ARROW_RIGHT = 	18;
	final static int INPUT_BOOL_ARROW_USE = 	19;
	final static int INPUT_BOOL_ARROW_X = 		20;
	final static int INPUT_BOOL_ARROW_Y = 		21;
	final static int INPUT_BOOL_LSTICK_USE = 	22;
	final static int INPUT_BOOL_LSTICK_X = 		23;
	final static int INPUT_BOOL_LSTICK_Y = 		24;
	final static int INPUT_BOOL_LSTICK_UP = 	25;
	final static int INPUT_BOOL_LSTICK_DOWN = 	26;
	final static int INPUT_BOOL_LSTICK_LEFT = 	27;
	final static int INPUT_BOOL_LSTICK_RIGHT = 	28;
	final static int INPUT_BOOL_RSTICK_USE = 	29;
	final static int INPUT_BOOL_RSTICK_X = 		30;
	final static int INPUT_BOOL_RSTICK_Y = 		31;
	final static int INPUT_BOOL_RSTICK_UP = 	32;
	final static int INPUT_BOOL_RSTICK_DOWN = 	33;
	final static int INPUT_BOOL_RSTICK_LEFT = 	34;
	final static int INPUT_BOOL_RSTICK_RIGHT = 	35;

	final static int[] INPUT_BUTTONS_INDEX = {INPUT_BOOL_SHARED, INPUT_BOOL_NONE, INPUT_BUTTON_Y, INPUT_BUTTON_B
			, INPUT_BUTTON_A, INPUT_BUTTON_X, INPUT_BUTTON_Lb, INPUT_BUTTON_Rb, INPUT_BUTTON_Lt, INPUT_BUTTON_Rt
			, INPUT_BUTTON_Select, INPUT_BUTTON_Start, INPUT_BUTTON_LSTICK, INPUT_BUTTON_RSTICK};
	
	// code de l'input retourner par la lib		13 = none
	final static int[] INPUT_BUTTONS_CODE = {
			13,13,	// id - button
			0,		// 2 y
			1,		// 3 b
			2,		// 4 a
			3,		// 5 x
			4,		// 6 lb
			5,		// 7 rb
			6,		// 8 lt
			7,		// 9 rt
			8,		// 10 select
			9,		// 11 start
			10,		// 12 ls
			11		// 13 rs
		};
	
	final static String[] INPUT_BUTTONS_TEXT = {
			"Shared",
			"None",
			"Y",
			"B",
			"A",
			"X",
			"Lb",
			"Rb",
			"Lt",
			"Rt",
			"select",
			"start",
			"L Stick",
			"R Stick",
			"arrow UP",
			"arrow DOWN",
			"arrow LEFT",
			"arrow RIGHT"
		};

		
	static final Map<String, Object> INPUT_BUTTONS_SHARED =
		Collections.unmodifiableMap(new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
		{
			for (int i = 0; i < 14; i++) {
				put(INPUT_BUTTONS_TEXT[i], INPUT_BUTTONS_INDEX[i]);
			}
		}});
	
	static final Map<String, Object> INPUT_BUTTONS =
			Collections.unmodifiableMap(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 1L;
			{
				for (int i = 1; i < 14; i++) {
					put(INPUT_BUTTONS_TEXT[i], INPUT_BUTTONS_INDEX[i]);
				}
			}}); 
	
	//index dans la liste de double Bool
	final static int INPUT_SLIDE_NONE = 	0;
	final static int INPUT_SLIDE_LRb = 	1;
	final static int INPUT_SLIDE_ARROW_Y = 2;
	final static int INPUT_SLIDE_ARROW_X = 3;
	final static int INPUT_SLIDE_LSTICK_Y = 4;
	final static int INPUT_SLIDE_LSTICK_X = 5;
	final static int INPUT_SLIDE_RSTICK_Y = 6;
	final static int INPUT_SLIDE_RSTICK_X = 7;
	
	final static String[] INPUT_SLIDES_TEXT = {
			"None",
			"Lb - Rb",
			"Arrow V",
			"Arrow H",
			"Left Stick V",
			"Left Stick H",
			"Right Stick V",
			"Right Stick H"
		};
	
	final static int[] INPUT_SLIDES_INDEX = {0,1,2,3,4,5,6,7};
	
	// paire d'index dans la liste de bool
//	final static int[][] INPUT_SLIDES_BOOL_INDEX = {
//			{INPUT_BOOL_NONE, INPUT_BOOL_NONE},
//			{INPUT_BUTTON_Lb,INPUT_BUTTON_Rb},
//			{INPUT_BOOL_ARROW_DOWN,INPUT_BOOL_ARROW_UP},
//			{INPUT_BOOL_ARROW_LEFT,INPUT_BOOL_ARROW_RIGHT},
//			{INPUT_BOOL_LSTICK_DOWN,INPUT_BOOL_LSTICK_UP},
//			{INPUT_BOOL_LSTICK_LEFT,INPUT_BOOL_LSTICK_RIGHT},
//			{INPUT_BOOL_RSTICK_DOWN,INPUT_BOOL_RSTICK_UP},
//			{INPUT_BOOL_RSTICK_LEFT,INPUT_BOOL_RSTICK_RIGHT}
//		};
	
	final static int[] INPUT_SLIDES_SHARING_BOOL = {
			INPUT_BOOL_NONE,
			INPUT_BOOL_LRb_USE,
			INPUT_BOOL_ARROW_Y,
			INPUT_BOOL_ARROW_X,
			INPUT_BOOL_LSTICK_Y,
			INPUT_BOOL_LSTICK_X,
			INPUT_BOOL_RSTICK_Y,
			INPUT_BOOL_RSTICK_X
		};
	
	static final Map<String, Object> INPUT_SLIDES =
			Collections.unmodifiableMap(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 1L;
			{
				for (int i = 0; i < 8; i++) {
					put(INPUT_SLIDES_TEXT[i], INPUT_SLIDES_INDEX[i]);
				}
			}}); 
	
	final static int INPUT_VECTOR_NONE = 0;
	final static int INPUT_VECTOR_LStick = 1;
	final static int INPUT_VECTOR_RStick = 2;
	final static int INPUT_VECTOR_Arrow = 3;
	
	final static String[] INPUT_VECTORS_TEXT = {
			"None",
			"Left Stick",
			"Right Stick",
			"Arrow"
		};
	
	//index dans la liste de PVector	3 is unused
	final static int[] INPUT_VECTORS_INDEX = {0,1,2,3};
	
	final static int[] INPUT_VECTORS_SHARING_BOOL = {
			INPUT_BOOL_NONE,
			INPUT_BOOL_LSTICK_USE,
			INPUT_BOOL_RSTICK_USE,
			INPUT_BOOL_ARROW_USE
	};
	
	static final Map<String, Object> INPUT_VECTORS =
			Collections.unmodifiableMap(new LinkedHashMap<String, Object>() {
				private static final long serialVersionUID = 1L;
			{
				for (int i = 0; i < 4; i++) {
					put(INPUT_VECTORS_TEXT[i], INPUT_VECTORS_INDEX[i]);
				}
			}}); 
}
