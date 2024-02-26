import processing.core.PApplet;

public class miniSim {
	public static void main(String[] args) {
		PApplet.main("RApplet.Rapp");
	}
}

/* DONT HIT ENTER HERE FFS!!


		MACRO BLOC review

Blocs List :
		DONE
	Flux : point, chan, gate
	Sheet : Macro_Sheet, Sheet_Main, SheetCo, Cursor
	Stock/access : Variable, sValue
	Input : StandardInputs
	Modify / Interpret : VecCalc, BoolCalc, NumCalc, Random
		Transform/Filter :
			filtered : type : bang, bool, num, vec ; types limits : bool:TorF
				time : cnt(delay), frame
			Transform : bool <> bang, vec <> XY, vec <> MagDir, all > bang/bool
	Complexes : rampe, sequance, setreset, counter
	GUI : Button, Slider, Comment
	
		TO DO
	Transform/Filter Transform : col <> RGBA
		
	GUI : GUIPanel, GUILabel, GUIButton, GUIGraph, GUISlide
	
		FOR LATER
	Working : Environment:MCamera, MCanvas, MTick   Actifs:MStructure, MForm, MPatern
	Complexes : matrice
	Modify / Interpret : 
		NumCalc : pow sqrt min max lerp abs
		ColorCalc : set add : RGBA SatHue
		Transform/Filter : 	Filter type : str, col
							Filter : only on change : on/off (need saving of all val types)
							Transform : col <> SatHue, all > num/vec/col
	GUI : ToolPanel
	Other : Midi, Preset

		




		
		DOABLE IDEE

conf file with general param loaded once at start
	fullscreen on/off
	autoload
	file to open (last openned)

MComment : log text with insertion token??

trig n switch : screen widget when too dezoomed (on/off) ; picker for bp color

distence mesuring tool

keep your mouse on a cursor for 0.5s > a bar appear under it, 
	its a slider to change scale, 
	always appear centered, go from 0.25x current scale to 4x current scale
	ephemere : will disappear if mouse stop hovering it or the cursor

'open finished' bp, search file in sub folder, cant save on it again
'save final' bp to save in this folder (verify if new file name is existing to stop you from overwriting)

add shortcut menu for macro main toolpanel bp and basic sim features 

mesure and store the relative process time used by different things

when selecting a preset auto hide uncompatible

trad README French

list knows dangerous (bugged) actions

sujet principaux dans la doc
	data sval, svalblc save/load templ/prst
	macro sheet bloc co sheet_co
	list detailler bloc sp
	packet process







		R & D

logarythmic slides

mtemplate : load / save bang > run
sheet selector : select sheet choosen by name on bang
pack / unpack > build complex packet

save log at crash? how to detect crash?

	-New Creating Process :

Collection de position : cursors in a sheet
	position, dir and scale
		add scale value to MCursor or to nCursor ?
		
form : bloc
	graphic object : line, trig, ellipse, rect, arrow...
	axis aligned, normalized size, colors, line width
	
structure : bloc
	list of replic : pos, dir, scale, line width multiplyer and color filter
	replicate a given form
	max_entity and active_entity tools here

patern : bloc
	apply a parametrable patern to a structure
	patern can be driven by a given random seed
	building can be driven by tick input << !! sim random seed will be used !!
	modif continue : create movement by changing given structure replics position and direction
	
Camera : bloc
	draw given shape to given cursors positions in camera view
Canvas : bloc
	create a canvas
	print onto itself given shapes to given cursors positions
	printing can be different things, add color as halo, modify, decay ...

	-Hard to do:

slow mo mode : permit interaction and observation > 
	objectif no number no words just visual for organic learnning
	permit to see the content sended through a link visualy
	and his speed of evolution > show synchronisation and sequances
	a bubble transit along the links 
	for number / bool bubble is more or less filled (min max sval) 
	text or color trensit along the link
	then bloc lit up if activated ( intensity show process difficuty?? )

gameplay thinking :
	access level :
		low access hide certain sheet/bloc/values
		gain access by meeting enigmatic condition ??
	consumable is needed to influence the world :
	modifying svalues diplaying a shape processing data (running macro)
	packet hold consummable
	the canvas is the origin and destination of the consumable
	links connect consummable pool with restriction
	macro bloc are pool
	restriction on volume and quantity by tick
	transfer quality : some of the packet consummable is lost to the canvas
	displayed shape are pool
	collision between shape create pool link
	collision area influence link quality



*/