import processing.core.PApplet;

public class miniSim {
	public static void main(String[] args) {
		PApplet.main("RApplet.Rapp");
	}
}

/* DONT HIT ENTER HERE FFS!!

		FIX

finish mirror (wrong pos when mirrored : verify all blocs)

mbasic link saving based on co info find false positive > too much link created
	based on co descr badly support param view switch cause optional elem are on top
	widget build in param counted differently than those in normal 
	widget numbered by their row + col, if dont move dont change name
		> no naming perturbation

add method in bloc :
	ctrl of input envent mode : onbang / on right type / never
	ctrl of holding svalues event mode : onchange / onallchange / never
				inbang+bp ( name, runnable/sRun/sBoo )
				infloat+field(S/L) ( name, sInt/sFlt )
				infloat+minitrigs/trigs add+sub(S/L) ( name, sInt/sFlt )
				inbool+switch ( name, sBoo )
				inall+trig col/vec/str picker ( name, sCol/sVec/sStr )
	link connection adding methods > new object Mecro_Link extends Macro_Connect ?
	
use bloc methods everywhere in bloc construction to shorten it and serialize it
	easyier connection modif

finish bloc cleanup / verif > see start.sdata



		NEW BLOC
MZone : draw a rectangle from the bloc pos to a grabbable widget on the opposing corner
	can output info such as coordinates and area
MPanel :
	draw a rectangle from the bloc pos of the size of a windowpanel
	can see other blocs inside of him
	use it to build menus and toolpanels (show all bloc elems inside of him)
MHeader : 
	is a mzone
	glue all bloc inside, hide their grabber
		OR
	as a bloc selection bp to make group (hided in normal mode if he as a group)
		get current selection if he is selected
	glue group of bloc together, 
	when selecting one, the others get selected automaticaly so they always move together
	as a big grabbable to easily move glued group 
		act as and move to it the selection center screenwidget
MPOV : extend sheet object, use sheet specialize ?
	as gotoview, nextcam, and deploy as big trig in spots by default
	as a MZone automatically inside to set the view

		NEW WORKING BLOCS
MShape output : area
MStructure : array of shape replics to draw to a camera
	param in : index, array max size, drawing layer
	outputs : pos, rot, scale, array current size,
		drawn area, bounding box, center and radius
	methods  as inputbang plus bp :		
		add pencil to index, output param of pencil, output param of replic at given index,
		delete replic at given index, 
		mirror pencil to center/modifier
MCollision
	has two linked structure, on bang test if theirs replics are colliding
	output a boolean with the result, the number of collision pair,
		the collision area.
	find a way to make it output the index pair (use pack?)

Link :
	bloc with link :
		node cursor sheetbloc shape struct camera collision canvas
	bloc passing link : node gate chan
	hilight linked blocs and linking path when hovering link
	bloc as a method to add a link co with param : in or out, name, run at link/unlink
		when co made get connected block, call runnable
			method to return linked bloc (like lastPack())
		when co deleted call runnable
			method to return unlinked bloc
	link connection have a validated state activated by blocs when link is in use
		change line color sigtly

MMain :
	method in main to add sBoo or sRun to a shortcut tab in main menu
		select in list, enter key, press bp > set shortcut
		Ctrl+key shortcut possible
	method in main to auto create a sBoo or sRun linked to a widget in maintoolpan
		auto add to shortcut menu
	add a field and a bp to build toolpan > new template, a switch > viewable or not

>>>>>> VERSION 0.4 <<<<<<<
		



		IDEE
	--Blocs:
MNode
	in point mode hide grabber until hovering lens
	row can have optional valviewer
	each row can select a node channel, 
			meanning it can send to another row index
		if too mush options use 2 row by pack co
	each row is a channel?
MGate : multi row : index set output
	mode : at bang in state : 
		switch state / co let +1 packet throug for this frame / next frame
Mcursor : add obj input to get cursor, use it's pos and dir as referentiel (parent - child) 
cursors can be selected by rightclick-drag and moved with blocs
keep your mouse on a cursor for 0.5s > a bar appear under it, 
	its a slider to change scale, 
	always appear centered, go from 0.25x current scale to 4x current scale
	ephemere : will disappear if mouse stop hovering it or the cursor
MEnveloppe : 
	star value then series of row with a tick length, an end value, and a courbure
	tick (ext/glob) to crawl it
counted tick generator 1 bang > X bangs immediately
distence mesuring tool
MMvar : option : one bang send all
MText : as bp to insert line end
	as bp to insert input last packet getText() (small int select for input cible)
			can has multiple inputs (small int select for number)
MButton : 
	screen widget when too dezoomed (on/off)
	multiple button of same type in ROW
	button can output to widget text named channel
	String packet can set text >> with a MText it can change channel

	--utils :
dont bloc zoom on cam widgets?
build a widget parent relation viewing tool, 
	nWidget methods output limited( DONE ) partial and full description as text
	nGUI method output a text file with the complete structure formatted( mid DONE ), 
	parent child relation shown with limited descr and line nb of full descr
	can add debug label to widget, they will show first line with theyr full descr and hyerarchy line
conf file with general param loaded once at start
	fullscreen on/off
	autoload
	file to open (last openned)
trad README French
finished project folder for easy viewing
	'open finished' bp, search file in sub folder, cant save on it again
	'save final' bp to save in this folder 
		verify if new file name is existing to stop you from overwriting

	--Hard:
some connection lens still viewable when bloc reducted
when selecting a preset auto hide uncompatible
save last deleted link, bloc : sRun to undo
block can have more than 3 col

		MACRO BLOC REVIEW
Blocs List :
		DONE
	Flux : node, chan, gate
	Sheet : Macro_Sheet, Sheet_Main, Cursor
	Stock/access : Variable, sValue
	Input : StandardInputs
	Modify / Interpret : VecCalc, BoolCalc, NumCalc, Random, bin, not
		Filter : type : bang, bool, num, vec ; types limits : bool:TorF
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








		R & D
		
big lag when selecting an heavy sheet caused by toLayerTop() recursive calls
		>>>> sheet bloc number limited

bloc grabbing can be constrained to one axis at will

tickrate is regulated by packet process		TEST IT
	if too much pack for a frame, tick counter dont increment  
			
mesure and store the relative process time used by different things

logarythmic slides
mtemplate : load / save bang > run
sheet selector : select sheet choosen by name on bang
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