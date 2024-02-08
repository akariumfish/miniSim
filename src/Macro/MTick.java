package Macro;

import UI.nFrontPanel;
import UI.nFrontTab;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sInt;
import sData.sRun;
import sData.sValueBloc;

public class MTick extends MBaseMenu { 
	static class MTick_Builder extends MAbstract_Builder {
		MTick_Builder() { super("tick", "time control"); show_in_buildtool = true; }
		MTick build(Macro_Sheet s, sValueBloc b) { MTick m = new MTick(s, b); return m; }
	}
	int run_tck_cnt = 0, met_tck_cnt = 0;
	Macro_Connexion tick_out, rst_out, com_in;
	MTick(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "tick", _bloc); }
	void init() {
		super.init();
	    tick_counter = newInt(0, "tick_counter", "tick");
	    tick_by_frame = newFlt(2, "tick by frame", "tck/frm");
	    tick_sec = newFlt(0, "tick seconde", "tps");
	    pause = newBoo(false, "pause", "pause");
	    force_next_tick = newInt(0, "force_next_tick", "nxt tick");
	    auto_reset = newBoo(true, "auto_reset", "auto reset");
	    auto_reset_rng_seed = newBoo(true, "auto_reset_rng_seed", "auto rng");
	    auto_reset_screenshot = newBoo(false, "auto_rest_screenshot", "auto shot");
	    show_com = newBoo(false, "show_com", "show");
	    auto_reset_turn = newInt(4000, "auto_reset_turn", "auto turn");
	    SEED = newInt(548651008, "SEED", "SEED");
	
	    mmain().inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );

	    srun_tick = newRun("sim_tick", "tick", new nRunnable() { 
	    	  public void run() { 
	    		  run_tck_cnt++; 
	    		  force_next_tick.add(1); 
	    		  while (run_tck_cnt > met_tck_cnt) tick();
	    		  if (tick_out != null) tick_out.send(Macro_Packet.newPacketBang());
	    	} } );
	    srun_reset = newRun("sim_reset", "reset", new nRunnable() { 
	      public void run() { reset(); } } );
	    srun_rngr = newRun("sim_rng_reset", "rst rng", new nRunnable() { 
	      public void run() { resetRng(); } } );
	    srun_nxtf = newRun("sim_next_frame", "nxt frm", new nRunnable() { 
	      public void run() { force_next_tick.set((int)(tick_by_frame.get())); } } );
	    srun_scrsht = newRun("screen_shot", "impr", new nRunnable() { 
	      public void run() { mmain().inter.cam.screenshot = true; } } );
	}
	void build_param() {
		super.build_param();
		
	}
	void build_normal() {
		super.build_normal();
		com_in = addInput(0, "COM");
		tick_out = addOutput(1, "tick");
		rst_out = addOutput(1, "reset");
	}
	

	  sInt tick_counter; //conteur de tour depuis le dernier reset ou le debut
	  sBoo pause; //permet d'interompre le defilement des tour
	  sInt force_next_tick; 
	  sFlt tick_by_frame; //nombre de tour a executé par frame
	  sFlt tick_sec;
	  sInt SEED; //seed pour l'aleatoire
	  sBoo auto_reset, auto_reset_rng_seed, auto_reset_screenshot, show_com;
	  sInt auto_reset_turn;
	  sRun srun_reset, srun_rngr, srun_nxtf, srun_tick, srun_scrsht;
	  sBoo show_toolpanel;

	  float tick_pile = 0; //pile des tick a exec

	
	  public void build_custom_menu(nFrontPanel sheet_front) {
	    nFrontTab tab = sheet_front.getTab(2);
	
	    tab.getShelf()
	      .addDrawer(10.25, 0.6)
	      .addModel("Label-S4", "- Simulation Control -").setFont((int)(ref_size/1.4)).getShelf()
	      .addSeparator(0.125)
	      .addDrawerWatch(tick_counter, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerLargeFieldCtrl(SEED, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerFactValue(tick_by_frame, 2, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerIncrValue(auto_reset_turn, 1000, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(auto_reset, auto_reset_rng_seed, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerDoubleButton(srun_scrsht, auto_reset_screenshot, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(srun_reset, srun_rngr, srun_tick, 10, 1)
	      .addSeparator(0.125)
	      .addDrawerTripleButton(pause, show_com, mmain().inter.cam.grid, 10, 1)
	      .addSeparator(0.125)
	      ;
	  }

  void resetRng() { 
    SEED.set((int)(gui.app.random(1000000000))); 
    reset();
  }
  void reset() {
	gui.app.randomSeed(SEED.get());
    tick_counter.set(0);
    mmain().inter.framerate.reset();
    run_tck_cnt = 0; met_tck_cnt = 0;
    if (rst_out != null) rst_out.send(Macro_Packet.newPacketBang());
  }

  void frame() {
    if (!pause.get()) {
      tick_sec.set(mmain().inter.framerate.median_framerate.get() * tick_by_frame.get());
      
      tick_pile += tick_by_frame.get();

      //auto screenshot before reset
      if (auto_reset.get() && auto_reset_screenshot.get() &&
        auto_reset_turn.get() == tick_counter.get() + tick_by_frame.get() + tick_by_frame.get()) {
        mmain().inter.cam.screenshot = true;
      }

      while (tick_pile >= 1) {
        tick();
        tick_pile--;
      }

    } else tick_sec.set(0);

    // tick by tick control
    if (pause.get() && force_next_tick.get() > 0) { 
      for (int i = 0; i < force_next_tick.get(); i++) tick(); 
      force_next_tick.set(0);
    }
    if (!pause.get() && force_next_tick.get() > 0) { 
      force_next_tick.set(0);
    }

  }

  void tick() {
	  met_tck_cnt++; 
	  while (run_tck_cnt < met_tck_cnt) srun_tick.run();
    //auto reset
    if (auto_reset.get() && auto_reset_turn.get() <= tick_counter.get()) {
      if (auto_reset_rng_seed.get()) {
        SEED.set((int)(gui.app.random(1000000000)));
      }
      reset();
    }
    tick_counter.set(tick_counter.get()+1);
  }

	public MTick clear() {
		super.clear(); 
		return this; }
	public MTick toLayerTop() {
		super.toLayerTop(); 
		return this;
	}
}
