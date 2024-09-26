package Macro;

import sData.sValueBloc;

public class MCustom extends Macro_Bloc { 
	  static class Builder extends MAbstract_Builder {
		  Builder(Macro_Main m) { super("custom", "Custom", "Custom Connexions", "Sheet"); 
		  first_start_show(m); }
		  MCustom build(Macro_Sheet s, sValueBloc b) { MCustom m = new MCustom(s, b); return m; }
	    }

		public Macro_Connexion in,out;
	  
	  MCustom(Macro_Sheet _sheet, sValueBloc _bloc) { 
	    super(_sheet, "custom", "custom", _bloc);
	    sheet.custom_blocs.add(this);
	    in = addInput(0, "in");
	    out = addOutput(1, "out");
	  }
	  
	  void init_end() {
		  super.init_end();
	  }
	  public MCustom toLayerTop() {
	    super.toLayerTop(); 
	    return this; }

	  public MCustom clear() {
	    super.clear(); 
	    sheet.custom_blocs.remove(this);
	    return this; }
	}
