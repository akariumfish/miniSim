package Macro;

import java.util.ArrayList;

import Macro.MEnvelope.CurveSection;
import RApplet.RConst;
import RApplet.Rapp;
import RApplet.sInterface;
import UI.Drawable;
import UI.nDrawer;
import UI.nFrontPanel;
import UI.nGUI;
import UI.nWidget;
import ddf.minim.AudioPlayer;
import ddf.minim.analysis.FFT;
import processing.core.PVector;
import sData.*;

public class MSound extends MBaseMT {

	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { super("sound", "Sound", "Sound player n analyzer", "Work"); 
		first_start_show(m); }
		MSound build(Macro_Sheet s, sValueBloc b) { MSound m = new MSound(s, b); return m; }
	}
	Rapp app;
    sInterface inter;
	public AudioPlayer sound_player;
	FFT         fft;
	
//	int out_nb;
//	int out_rezo;
//    ArrayList<Macro_Connexion> out_cos;
    
    sRun run_play, run_stop, run_restart;
    sBoo val_play, val_mute;
    sInt position_val;
    
    sInt val_band, val_larg, val_thresh;
    sFlt val_band_med;
    Macro_Connexion out_thresh, out_thrsh_state;

    MSound(Macro_Sheet _sheet, sValueBloc _bloc) { 
		super(_sheet, "sound", _bloc); 
	}
	public void init() {
		super.init();
		inter = mmain().inter;
		app = inter.app;
		sound_player = app.sound_player;
		fft = app.fft;
		
//		out_cos = new ArrayList<Macro_Connexion>();
		
		position_val = newInt(0, "position_val");
	    val_play = newBoo(false, "val_play");
	    val_mute = newBoo(false, "val_mute");
	    val_band = newInt(40, "val_band");
	    val_larg = newInt(40, "val_larg");
	    val_thresh = newInt(100, "val_thresh");
	    val_band_med = newFlt(0, "val_band_med");
	    if (!loading_from_bloc) {
	    		val_band.set_limit(0, 512);
	    		val_larg.set_limit(0, 256);
	    		val_thresh.set_limit(0, 256);
	    }
	    sound_player.skip(position_val.get()); 
		if (val_play.get()) sound_player.play();
//		out_rezo = 128;
//		out_nb = fft.specSize() / out_rezo;
		if (val_mute.get()) sound_player.mute();
		val_mute.addEventChange(new nRunnable() { public void run() { 
			if (val_mute.get()) sound_player.mute();
			else sound_player.unmute(); }});
		
		run_play = newRun("play", new nRunnable() { public void run() { 
					val_play.set(true); sound_player.play(); }});
		run_stop = newRun("stop", new nRunnable() { public void run() { 
					val_play.set(false); sound_player.pause(); }});
		run_restart = newRun("restart", new nRunnable() { public void run() { 
					val_play.set(true); sound_player.rewind(); sound_player.play(); }});
		globalSBin(run_play, run_stop, run_restart, false);
		
	    inter.addEventFrame(new nRunnable() { public void run() { frame(); } } );
	}
	void build_param() { 
//		super.build_param(); 
		build_normal();
	}
	void build_normal() { 
	    super.build_normal(); 
	    out_thresh = addOutput(2, "thresh");
	    out_thrsh_state = addOutput(2, "thresh_state");
//	    for (int i = 0 ; i < out_nb ; i++) {
//		    Macro_Connexion o = addOutput(2, "out"+i);
//		    out_cos.add(o);
//	    }
	}
	void init_end() { 
		super.init_end(); 
	}
	void rebuild() { 
		super.rebuild(); 
	}
	public MSound clear() {
		super.clear(); 
		sound_player.pause();
		return this; }
	
	void frame() {
		if (sound_player.isPlaying()) { 
			fft.forward( sound_player.mix ); //analyse
			position_val.set(sound_player.position()); } //memory
		
		int minband = val_band.get() - val_larg.get();
		int maxband = val_band.get() + val_larg.get();
		if (minband < 0) minband = 0;
		if (maxband > 512) maxband = 512;
		int band = 0;
		for (int i = minband ; i < maxband ; i++) {
			band += fft.getBand(i); }
		band /= val_larg.get() * 2.0F;
		val_band_med.set(band);
		if (band > val_thresh.get()) {
			out_thresh.sendBang();
			out_thrsh_state.sendBool(true);
		} else 
			out_thrsh_state.sendBool(false);
	}
	
	void tick() {
//		out_nb = fft.specSize() / out_rezo;
//		for (int i = 0 ; i < out_nb ; i++) {
//			out_cos.get(i).sendFloat(fft.getBand(i * 32));
//		}
	}
	void reset() { }
	
	nWidget graph;
	Drawable g_draw;
	float graphSX = ref_size * (10.0F - 4.0F/16.0F), graphSY = ref_size * 5;
	
	public void build_custom_menu(nFrontPanel sheet_front) {
		if (sheet_front != null) {
			nDrawer dr = sheet_front.getTab(2).getShelf()
		        .addDrawer(10.25, 5.25);
		  
			graph = dr.addModel("Field");
			graph.setPosition(ref_size * 2 / 16, ref_size * 2 / 16)
				.setSize(graphSX, graphSY);
			
			g_draw = new Drawable(sheet_front.gui.drawing_pile, 0) { public void drawing() {
				gui.app.fill(graph.look.standbyColor);
			    gui.app.noStroke();
				gui.app.rect(graph.getX(), graph.getY(), graph.getSX(), graph.getSY());
				PVector ref = new PVector(graph.getX() + ref_size * 2 / 16, 
						graph.getY() + graph.getSY()
						- ref_size * 2 / 16);
				float h = graph.getSY() - ref_size * 4.0F / 16;
				float w = graph.getSX() - ref_size * 4.0F / 16;
				gui.app.stroke(220);
				gui.app.strokeWeight(3);
				gui.app.line(ref.x, ref.y, ref.x + w, ref.y);

				app.pushMatrix();
				app.translate(ref.x, ref.y - h);
				for (int i = 0 ; i < fft.specSize() - 1 ; i++) {
					app.line((w * i / fft.specSize()), h, 
							(w * i / fft.specSize()), 
							h - (h * fft.getBand(i) / 512));
				}
				app.stroke(220, 20, 20);
				app.line((w * (val_band.get() - val_larg.get()) / fft.specSize()), h, 
						(w * (val_band.get() - val_larg.get()) / fft.specSize()), 0);
				app.line((w * (val_band.get() + val_larg.get()) / fft.specSize()), h, 
						(w * (val_band.get() + val_larg.get()) / fft.specSize()), 0);
				app.line(0, h - val_thresh.get(), w, h - val_thresh.get());
				app.popMatrix();
			} };
			graph.setDrawable(g_draw);
			
			dr.getShelf().addDrawer(10.25, 0.0).getShelf()
	  	  		.addDrawerWatch(val_band_med, 10.25F, 1)
	  	  		.addSeparator(0.125)
		  	  	.addDrawerIncrValue(val_band, 20, 10, 1)
		  	  	.addSeparator(0.125)
		  	  	.addDrawerIncrValue(val_larg, 20, 10, 1)
		  	  	.addSeparator(0.125)
		  	  	.addDrawerIncrValue(val_thresh, 20, 10, 1)
		  	  	.addSeparator(0.125)
		  	  	.addDrawerButton(val_mute, 10, 1)
		  	  	.addSeparator(0.125)
				;

		  	sheet_front.addEventClose(new nRunnable(this) { public void run() { 
		  		graph = null; g_draw.clear(); g_draw = null; } } );
		  	sheet_front.toLayerTop();
		}
	}
}
