import processing.core.PApplet;

public class miniSim {
	public static void main(String[] args) {
		PApplet.main("RApplet.Rapp");
	}
}

/*

TEST

keep your mouse on a cursor for 0.5s > a bar appear under it, 
	its a slider to change scale, 
	always appear centered, go from 0.25x current scale to 4x current scale
	ephemere : will disappear if mouse stop hovering it or the cursor
	
cursor pointer in losange, point in direction > need custom drawable
	correct centering error on pointer widget

working :
	face as basic shape
	sheet as collection of position
	scalable cursor
	camera dessinateur

object info passed at connection : no need for packet exchange when connecting objet
	== static connection ?

co element made to auto add themself as spot

Collection de position : cursors in a specialized sheet
	position, dir and scale
		add scale value to MCursor or to nCursor ?

form : specialized sheet
	graphic object : line, trig, ellipse, rect...
	axis aligned
	colors, line width
	
shape : specialized sheet
	replicate given form in patern
		replicant have own pos, dir, scale, line width multiplyer and color filter
	patern can be driven by a given random seed
	building can be driven by tick input << !! sim random seed will be used !!

Camera dessinateur : specialized sheet
	draw given shape to given cursors positions in camera view

Canvas dessinateur : specialized sheet
	print given cursors as halo to given canvas
	print given shape to given cursors positions as halo to given canvas
	
	print can be different things, add color, modify, decay ...

modificateur de position : specialized sheet
	floc
	create movement by changing given cursors position and direction




corrige error on group_grabber grid snaping, 
small constant misalignment, because of his size not multyple?

add min/max param to slide (/2 X2) > set minmax of sval
event limit change for synchro

number formating!!!

when manually adding try to stay on top of sheet back to keep it small
>> start add place search from existing bloc median pos (or 0) then try in spiral

distence mesuring tool

bloc node : one point in and out at the same time
use a custom macro_connect
created by leftclick on empty space

its needed to group shapes to limit object nb

to big trig et switch : when too dezoomed show a screen widget (on/off)
set button text (large font)






hide majority of macro widgets when dezoom

bloc a faire:
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
	MComment can log received data (auto text format with insertion token??)
	camview
		like a menu, name, click to go, 3 field for values, 
		when selected capture n store cam pos / scale
	frame delay / tick delay / packet process delay
	setreset, counter, sequance, multigate 






open finished bp, search file in sub folder, cant save on it again
save final bp to save in this folder (verify if new file name is existing to stop you from overwriting)

for macro main toolpanel bp and basic sim features add shortcut menu

list knows dangerous (bugged) actions

sujet principaux dans la doc
	data sval, svalblc save/load templ/prst
	macro sheet bloc co sheet_co
	list detailler bloc sp
	packet process

change sheet selection right when click down
	no more diff sheet bloc select in 2 click

nExplorer : more entry and smaller height

save log at crash? how to detect crash?

bool flag destroyed widgets!!!! to filter irregular

TEST packet processing order for inputs

redo link loop protection
	auto delete last link

mesure and store the relative process time used by each special sheet

macro blocs and groups adding position can be controllable
	can be relative or absolute depending on case



  R & D
  
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
  
make organism use a face as exemple for its shape
  
enregistrement de different POV camera entre lesquel on peut basquler
vue macro / vue exploration / vue general
macro camview

change access
low access hide certain sheet
gain access by meeting enigmatic condition ??

when selecting a preset auto hide uncompatible widget ? > need to redo explorer

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