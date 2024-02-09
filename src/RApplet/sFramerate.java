package RApplet;

import sData.sFlt;
import sData.sInt;
import sData.sValueBloc;

public class sFramerate {
  int frameRate_cible = 60;
  float[] frameR_history;
  int hist_it = 0;
  int frameR_update_rate = 10; // frames between update 
  int frameR_update_counter = frameR_update_rate;

  float current_time = 0;
  float prev_time = 0;
  float frame_length = 0;

  float frame_median = 0;

  float reset_time = 0;
  int frame_counter = 0;

  public sFlt median_framerate;
sFlt current_framerate;
sFlt frame_duration;
  sInt sec_since_reset, frame_since_reset;

  int frameNbForMsDelay(int d) { 
    return (int)(d * median_framerate.get() / 1000);
  }

  int get() { 
    return (int)(median_framerate.get());
  }
  Rapp app;
  
  sFramerate(Rapp a, sValueBloc d, int c) {
	app = a;
    frameRate_cible = c;
    app.frameRate(frameRate_cible);
    frameR_history = new float[frameRate_cible];
    for (int i = 0; i < frameR_history.length; i++) frameR_history[i] = 1000/frameRate_cible;

    sec_since_reset = new sInt(d, 0, "sec_since_reset", "sec");
    frame_since_reset = new sInt(d, 0, "frame_since_reset", "frsr");
    median_framerate = new sFlt(d, 60, "median_framerate", "mfr");
    current_framerate = new sFlt(d, 0, "current_framerate", "cfr");
    frame_duration = new sFlt(d, 0, "frame_duration", "fdur");
  }
  public void reset() { 
    sec_since_reset.set(0); 
    reset_time = app.millis(); 
    frame_counter = 0;
  }

  void frame() {
    frame_counter++;
    frame_since_reset.set(frame_counter);

    current_time = app.millis();
    frame_length = current_time - prev_time;
    frame_duration.set(frame_length);
    current_framerate.set(frame_length / 1000);
    prev_time = current_time;

    sec_since_reset.set((int)((current_time - reset_time) / 1000));

    frameR_history[hist_it] = frame_length;
    hist_it++;
    if (hist_it >= frameR_history.length) { 
      hist_it = 0;
    }

    if (frameR_update_counter == frameR_update_rate) {
      frame_median = 0;
      for (int i = 0; i < frameR_history.length; i++)  frame_median += frameR_history[i];
      frame_median /= frameR_history.length;
      median_framerate.set(1000/frame_median);

      frameR_update_counter = 0;
    }
    frameR_update_counter++;
  }
}
