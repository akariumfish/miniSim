package RApplet;

public class sInput_Button {
  public boolean state = false;
public boolean trigClick = false;
public boolean trigUClick = false;
  //boolean trigJClick = false, trigJUClick = false;
  char key_char;
  String ref;
  sInput_Button(String r, char c) { 
    ref = RConst.copy(r); 
    key_char = c;
  }
  sInput_Button(String r) { 
    ref = RConst.copy(r);
  }
  void eventPress() {
    state=true;
    trigClick=true;
  }
  void eventRelease() {
    state=false;
    trigUClick=true;
  }
  void frame() {
    trigClick = false; 
    trigUClick = false;
  }
}
