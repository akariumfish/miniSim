import processing.core.PApplet;

public class miniSim {
	public static void main(String[] args) {
		PApplet.main("RApplet.Rapp");
	}
}

/* DONT HIT ENTER HERE FFS!!


		MACRO BLOC
trig switch bin not gate OK!

general bloc capability
	send something at startup
	can open a menupanel
	can rebuild itself
	
method build ui :
	2 bp add/sub + 1 val_view in line > sInt
	in to value : get sValue : custom filters
		filter doublon
		limits, set/reset ...
	self valuebloc out
		

		NEW STRUCTURE 


Make sim and all macro bloc !!
	in and/or out plus label name for each svalue > fat bloc, better inside a sheet
	
	menu bp + in + startup to open . tabs:
		view : list all value + direct control
		preset
		the rest is added by a custom methods
	
	mmain will need a bloc
	
sheet menu : 
	cursors

main menu :
	explorer templates




Collection de position : cursors in a sheet
	position, dir and scale
		add scale value to MCursor or to nCursor ?

form : bloc
	graphic object : line, trig, ellipse, rect...
	axis aligned
	colors, line width
	
structure : bloc
	list of replic
		pos, dir, scale, line width multiplyer and color filter
	replicate a given form
	max_entity and active_entity tools here

patern : bloc
	apply a parametrable patern to a shape
	patern can be driven by a given random seed
	building can be driven by tick input << !! sim random seed will be used !!

	modif continue : create movement by changing given cursors position and direction
		floc
	
Camera dessinateur : bloc
	draw given shape to given cursors positions in camera view

Canvas dessinateur : bloc
	print given cursors as halo to given canvas
	print given shape to given cursors positions as halo to given canvas
	
	print can be different things, add color, modify, decay ...







		TO FIX

number formating!!!


		TO DO
		


sheetobj in 2 line + add ctrl for deploy/open/reduc

Merge MData n MVar in MValue
	will be abel to access sval of any type from sheet or parent sheet
	MValue add watcherwidget for val.ref
	can get a valuebloc as message : will search inside
	
	field search_place 2, ref 3, value 3
	bp picker
	bp panel
	in bang bool nul vec col : if val set > set val
	in new co : if not set > as search place
	out



in bin menu can choose to "double" it > like if msg go through 2 bin >
	filter bool false packet
	
add sheetview menu et sheetobj par def dans spe sheet
	> default sheet front
	
bloc can has mode unique, cad only one of a type by sheet

to big trig n switch : 
	when too dezoomed show a screen widget (on/off)
	on param view : 
	field L set button text (large font)
	bp incremental for size
	picker for bp color


when adding tool or pan rows auto cible last master build


output who send pre given packet auto when new co is made
co who run event when co is created / deleted 
	
link are lightened gradually when packet process slowly to show progression
	see top of Macro_Connexion . send() for methods. 

auto spot : bloc auto add some of his elem to sheet spots at creation

nExplorer : more entry and smaller height

trad README French


		
		DOABLE IDEE
		


conf file with general param loaded once at start
	fullscreen on/off
	autoload
	file to open (last openned)

keep your mouse on a cursor for 0.5s > a bar appear under it, 
	its a slider to change scale, 
	always appear centered, go from 0.25x current scale to 4x current scale
	ephemere : will disappear if mouse stop hovering it or the cursor
	
cursor pointer in losange, point in direction > need custom drawable
	correct centering error on pointer widget

distence mesuring tool

bloc node : one point in and out at the same time
use a custom macro_connect
created by leftclick on empty space

its needed to group shapes to limit object nb

open finished bp, search file in sub folder, cant save on it again
save final bp to save in this folder (verify if new file name is existing to stop you from overwriting)

for macro main toolpanel bp and basic sim features add shortcut menu

list knows dangerous (bugged) actions

sujet principaux dans la doc
	data sval, svalblc save/load templ/prst
	macro sheet bloc co sheet_co
	list detailler bloc sp
	packet process

mesure and store the relative process time used by each special sheet

when selecting a preset auto hide uncompatible widget ? > need to redo explorer

co element made to auto add themself as spot





		BLOC TO CREATE
		
cursor bloc : 
	(like menu) named, listed in mmain so global, 
	the bloc point to an existing cursor or can build a new
	custom setlinkedPosition(svec) setlinkedDir(svec) setlinkedShow(sboo)
	constrain (dir pos mag ..)
	registered independently from theirs sheet and easily accessible for
	new comus start
	global effect field
	multi comu objectif

	auto follow / point to objects, instent or chasing
	memorize path for display
	dif shape / look
MVar
	add a hideable (like com) param drawer : select variable type
	int str and vec better handling 
MComment can log auto text format with insertion token??
camview
	like a menu, name, click to go, 3 field for values, 
	when selected capture n store cam pos / scale
frame delay / tick delay / packet process delay
setreset, counter, sequance, multigate 






		







		R & D

bool has_been_cleared flag destroyed widgets

logarythmic slides

save log at crash? how to detect crash?

sending sval and sbloc through link
	output keep a packet as pointer to last send val, packet is passer

need clean rythme
	all activated outs call conected in for new val then deactive
	all activated in gets val pass it to bloc then deactive
	all activated bloc process the vals
	thats 1 packet process update
	repeat util no more new packet or too much time ellapsed, start again next turn

>>>> reuse the mconnection structure
will just need to add a turn with priority of exe for the blocs
	sval new methods 
	bool isType()
	packet asPacket()
	sbloc new methods
	packet asPacket()
	packet new methods
	sval asType()
	sbloc asBloc()
	bool isType()
	bool isBloc()

can send sbloc as cible for everything! solve the cursor or pairing proble

spe blocs can iterate throug bloc contents on ticking

simply put macro blocs for all svalue handling methods 
	add macro from bloc too

slow mo mode :    
real slow mode or just a slowed down recording of a finished packet process update ??

real permit interaction and observation > 
objectif no number no words just visual for organic learnning

permit to see the content sended through a link visualy
and his speed of evolution > show synchronisation and sequances

along the one update bubble transit along the links 
then bloc lit up if activated ( intensity show process difficuty?? )

for number / bool bubble is more or less filled (min max sval)
text trensit
color trensit

infrastructure :
structural model switchable (patch structure)
need used value to be present  

mtemplate : load / save bang > run
sheet selector : select sheet choosen by name on bang
pack / unpack > build complex packet

visualize sheet val content has phantom on the sheet back?

unique macro bloc custom build by the special sheet, non deletable, non duplicable
ex: Canvas :
will send each pixel throug this:
4 out color of the neigbours
1 out my color
4 in color modif for the neigbours
1 in color modif for myself
by charging the pixels data on the inputs, forcibly running packet process, getting the outs
need no exterior co and no packet delaying

special sheet : effect field
affect a canvas
all pixel under his shape get a custom processing
  
change access
low access hide certain sheet
gain access by meeting enigmatic condition ??

gameplay thinking :
consumable is needed to influence the world :
modifying svalues
diplaying a shape
processing data (running macro)
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