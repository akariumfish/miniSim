import processing.core.PApplet;

public class miniSim {
	public static void main(String[] args) {
		PApplet.main("RApplet.Rapp");
	}
}

/* DONT HIT ENTER HERE FFS!!

	VERSION .5 : 
		BUG FIX
big lag when selecting an heavy sheet (maybe only main?)
sequance in param state rebuild continuously (var too?)

some connection lens still viewable when bloc reducted?

sheet explorer list shown when switching taskpanel
update sheet back when adding spot

sequance dont understand param 0 as delay

MCursor should update nCursor even when hided (sheet closed is mouved) 
		IMPORTANT FIX
stop linkbuilding by rightclick, not left, to move cam

dont bloc zoom on cam widgets?

make hovered link bigger

save last deleted link, bloc
	sRun to undo

sBoo cam movable by mouse
sRun to toggle breakpoint
sBoo active link deletion
move reduc from under P button in mpack

hide reduc/prio when a little dezoomed
change gate switch text in param mode
cant delete / hover link and co on other sheet than selected

bring back bin and not : 
	a lot of old bloc can be brought back cause not all of them need to be seen in panel

rightclick on grabber or any panel widget select/deselect macro 
	add rightclick/unclick/press event to widget

when tolayertoping a sheet do macro in prio order, when changing prio tolaytop parent sheet
	apply this order to spots too

keep link and spot when switching blocs from param to normal
	add a links and spots sStr to saved valuebloc (like for template)
	with destroyed bloc links listed
	optional row should always be last so elem dont change name
	can add empty drawer to bloc panel to create space without elem to not influence elem indexing

remove gaps in bloc panel and sheet front
	should fix sheet spot spacing so connection align

	VERSION .6 : 
		IMPORTANT FIX
better sheet spot :
	when creating spot make all elem widget passif
	in spot creation mode add a bp to create empty space
	way to delete spot : add a bp next to each spot when in spot creation mode

connect type : IN OUT LINK differentiated by colors and shape
		change in to circle and link standby color to blue
	only in to out and link to link permitted
	bloc as a method to add a link co with param : in/out, run at link/unlink, name
		when co made get connected block, 
		if pack go throug, if gate go throug if active, in both :
			save it, change if next link delete, bloc deleted
			add method to pack or gate bloc to keep informed
		else store it then call runnable
			method to return connected bloc (like lastPack())
		when co deleted call runnable
			method to return unlinked bloc
	link connection have a validated state activated by blocs when link is in use
		change line color sigtly

blocs elements can be mirrored via a bp
	at widget level a set of parameters can apply mirroring
		for each axis : bool to active and float as mirror axis
	applyed when calling getx gety getsx getsy , consider parent mirroring
		BLOCS MODIF TO DO
MMain :
	method in main to add sBoo or sRun to a shortcut tab in main menu
		select in list, enter key, press bp > set shortcut
	method in main to auto create a sBoo or sRun linked to a widget in maintoolpan
		auto add to shortcut menu
	add bp in main toolpan for selectAll cut and duplicate selected
		A select all bloc in current sheet 
		D del W duplic X cut C copy V paste
	merge save as / save to in main toolpan
	add a field and a bp to build toolpan > new template, a switch > viewable or not
	have a global tick/reset/rngseed base
		for tick/reset bloc have option to use it or have an input
		merge macro main and Mtick
		tickrate is regulated by packet process
			if too much pack for a frame, tick counter dont increment  
MPack : 		Rename in connect?
	row can have 
		optional texts or valviewer (S or L) in col 2
		both in and out, do normal packet and link pass > replace mpoint and sheet co
		each pack row can select a pack channel, 
			meanning it can send to another row index
		if too mush options use 2 row by pack co
	by def its 1 row with just in and out
		in this case only do them both in col 0 (as a MPoint) 
		and move them to be aligned ?

>>>>>> VERSION STABLE <<<<<<<

	VERSION .7 : 
Bloc Modif :
		BASE BLOC
MBase : when build the first time is only a field, if input word is a type it become this type
	hit space : add empty at mouse (a switch in mmain toolpanel can activate this)
MButton : 
	button can output to widget text named channel
	other size : 2X bigger, multiple button of same type in row
	multi button in row, have own text and connections
	in param bp are field to set text
	in param : size, row nb, color, trigger/Switch, setup_send
	String packet can set text >> with a comment it can change channel
MComment : renaming in MText ?
	can select field size while viewong it with mbase
	can text to log at bang, can output text
	print text to screen or camera at given coords and size
		can choose color?
	as bp to insert line end
	can has multiple inputs (small int select for number)
	as bp to insert input last packet getText() (small int select for input cible)
MChan : as an input to set channel
MGate option :
	as inverted output
	at bang in state co let packet throug for next frame
MVar and MValue :
	different passing mode
		send val if : A any in ; B bang in ; C on val change ; S at build
		do this for bloc auto row
	duplicable in row (like sequance) can choose to show in out field (S/L)
MRamp : 
	in : start stop reset periode phi startval endval
	param : linear/sine 1/SAW/LP UP/DW 
MSequance : input / field to set all delay at once

	VERSION .8 : 
		NEW BLOC
MPanel :
	draw a rectangle from the bloc pos of the size of a windowpanel
	can see other blocs inside of him
	use it to build menus and toolpanels (show all bloc elems inside of him)
MHeader : 
	as a bloc selection bp to make group (hided in normal mode if he as a group)
		get current selection if he is selected
	glue group of bloc together, 
	when selecting one, the others get selected automaticaly so they always move together
	as a big grabbable to easily move glued group 
		act as and move to it the selection center screenwidget
MZone :
	draw a rectangle from the bloc pos to a grabbable widget on the opposing corner
	can output info such as coordinates and area
MPOV : extend sheet object, use sheet specialize ?
	as gotoview, nextcam, and deploy as big trig in spots by default
	as a MZone automatically inside to set the view

	VERSION .9 : 
		NEW WORKING BLOCS
MForm 
	fixed size array of vector for shape vertice coords
	typz : RECT, LINE, TRIG, CIRCLE, ARROW   >>  max 4 vector
	param : scale,linew (float) fill,line,type (int)
	methods : center, size x2,/2,x1.1,/1.1 rot various angle
	output : area
MStruct :
	replic : pos (vec) scale,rot,linew (float) fill,line,type (int)
		fixed size array of vector for shape vertice coords (max 4 vector)
	array of replics to draw to a camera
	pencil replic used for holding next adding position, 
		use linked form as shape as own pos and rot
	param in : pen_pos, pen_rot, pen_scale, mov_pos, mov_rot, mov_scale, 
		index, array max size, drawing layer
	outputs : pos, rot, scale, array current size,
		drawn area, bounding box, center and radius
	methods  as inputbang plus bp :		
			add method in bloc for 	inbang+bp infloat+field(S/L) 
									inbool+switch incol/vec/str+picker
		add copy of pencil to array applyong scale factor, 
		move pencil to, move pencil of param value, move pencil to center, 
		output param of pencil, output param of replic at given index,
		delete oldest replic, delete newest replic, delete all replics,
		delete replic at given index
MCamera
	draw linked struct at pos and rot given by linked sheet cursors
	each struct is drawn to cursors with a priority equal to their layer
MCollision
	has two linked structure, on bang test if theirs replics are colliding
	output a boolean with the result, the number of collision pair,
		the collision area.
	find a way to make it output the index pair (use pack?)

>>>>>> VERSION 0.4 <<<<<<<
		



		MACRO BLOC REVIEW
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
		FOR LATER
	Working : Environment:MCamera, MCanvas, MTick   Actifs:MStructure, MForm, MPatern
	Complexes : matrice
	Modify / Interpret : 
		NumCalc : pow sqrt min max lerp abs
		ColorCalc : set add : RGBA SatHue
		Transform/Filter : 	Filter type : str, col
							Filter : only on change : on/off (need saving of all val types)
							Transform : col <> SatHue, all > num/vec/col, col <> RGBA
	GUI : GUIPanel, GUILabel, GUIButton, GUIGraph, GUISlide, GToolPanel
	
	Other : Midi, Preset

		DOABLE IDEE

Mcursor : add obj input to get cursor, use it's pos and dir as referentiel (parent - child) 

cursors can be selected by rightclick-drag and moved with blocs

dont put a sheetmain bloc by def in sheet

hide numbering from titles 

MEnveloppe : 
	series of row with a tick length, an end value, and a courbure
	tick to crawl it

counted tick generator

switch to show/hide mouse coord and fps

build a widget parent relation viewing tool, 
	nWidget methods output limited partial and full description as text
	nGUI method output a text file with the complete structure formatted, 
	parent child relation shown with limited descr and line nb of full descr
	can add debug label to widget, they will show first line with theyr full descr and hyerarchy line

conf file with general param loaded once at start
	fullscreen on/off
	autoload
	file to open (last openned)

trig n switch : screen widget when too dezoomed (on/off) ; picker for bp color

distence mesuring tool

keep your mouse on a cursor for 0.5s > a bar appear under it, 
	its a slider to change scale, 
	always appear centered, go from 0.25x current scale to 4x current scale
	ephemere : will disappear if mouse stop hovering it or the cursor

when selecting a preset auto hide uncompatible

trad README French

'open finished' bp, search file in sub folder, cant save on it again
'save final' bp to save in this folder (verify if new file name is existing to stop you from overwriting)








		R & D
		
mesure and store the relative process time used by different things

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