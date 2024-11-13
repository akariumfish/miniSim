package Macro;

import sData.*;

public class MSRunStatic extends MSetRunner {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("srunstat", "SRun_Static", 
				"Runner for SetCreator objects - Static", "Set Tool"); 
		first_start_show(m); }
		MSRunStatic build(Macro_Sheet s, sValueBloc b) { MSRunStatic m = new MSRunStatic(s, b); return m; }
	}
	
	MSRunStatic(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "srunstat", _bloc); 
	}
	
	void tick_obj(MSet.SetObj o) { o.pos.set(o.pos_strt); o.mov.set(0,0); }
	void init_obj(MSet.SetObj o) { o.mov.set(0,0); }
}

