package Macro;

import java.util.ArrayList;

import RApplet.Rapp;
import RBase.RConst;
import processing.core.PVector;
import sData.sValue;
import sData.sValueBloc;

public class Macro_Packet {
	
	static Macro_Packet newPacketBang() { return new Macro_Packet("bang"); }

	static Macro_Packet newPacketFloat(float f) { return new Macro_Packet("float").addMsg(String.valueOf(f)); }
	static Macro_Packet newPacketFloat(String f) { return new Macro_Packet("float").addMsg(f); }

	static Macro_Packet newPacketInt(int f) { return new Macro_Packet("int").addMsg(String.valueOf(f)); }

	static Macro_Packet newPacketVec(PVector p) { return new Macro_Packet("vec").addMsg(String.valueOf(p.x)).addMsg(String.valueOf(p.y)); }

	static Macro_Packet newPacketCol(int p) { return new Macro_Packet("col")
	  .addMsg(String.valueOf(app.red(p))).addMsg(String.valueOf(app.green(p))).addMsg(String.valueOf(app.blue(p))); }

	static Macro_Packet newPacketStr(String p) { return new Macro_Packet("str").addMsg(RConst.copy(p)); }

	static Macro_Packet newPacketSheet(Macro_Sheet m) { return new Macro_Packet("sheet").setSheet(m); }

	static Macro_Packet newPacketBool(boolean b) { 
	  String r; 
	  if (b) r = "T"; else r = "F"; 
	  return new Macro_Packet("bool").addMsg(r); }
	
	static public Rapp app;
	
  String def = new String();
  ArrayList<String> messages = new ArrayList<String>();
  public Macro_Packet(String d) {
    def = d;
  }
  Macro_Packet addMsg(String m) { messages.add(m); return this; }
  
  boolean isBang()  { return def.equals("bang"); }
  boolean isFloat() { return def.equals("float"); }
  boolean isInt()   { return def.equals("int"); }
  boolean isBool()  { return def.equals("bool"); }
  boolean isVec()   { return def.equals("vec"); }
  boolean isCol()   { return def.equals("col"); }
  boolean isStr()   { return def.equals("str"); }
  
  boolean equalsVec(PVector v)   { return isVec() && v.x == asVec().x && v.y == asVec().y; }
  boolean equalsCol(int v)   { return isCol() && v == asCol(); }
  
  PVector asVec()   { 
    if (isVec()) return new PVector(Float.parseFloat(messages.get(0)), Float.parseFloat(messages.get(1))); else return null; }
  int asCol()   { 
    if (isCol()) return app.color(Float.parseFloat(messages.get(0)), 
    						  Float.parseFloat(messages.get(1)), 
    						  Float.parseFloat(messages.get(2))); else return 0; }
  float   asFloat()   { if (isFloat()) return Float.parseFloat(messages.get(0)); else return 0; }
  int     asInt()   { if (isInt()) return Integer.parseInt(messages.get(0)); else return 0; }
  String  asStr()   { if (isStr()) return messages.get(0); else return ""; }
  boolean asBool()   {
    if (isBool() && messages.get(0).equals("T")) return true; else return false; }
    
  String getText() {
    if (isBang()) return "bang";
    else if (isFloat()) return RConst.trimStringFloat(asFloat());
    else if (isInt()) return String.valueOf(asInt());
    else if (isBool() && messages.get(0).equals("T")) return "true";
    else if (isBool() && !messages.get(0).equals("T")) return "false";
    else if (isVec()) return RConst.trimStringFloat(asVec().x)+","+RConst.trimStringFloat(asVec().y);
    else if (isCol()) return RConst.trimStringFloat(app.red(asCol()))+","+
                             RConst.trimStringFloat(app.green(asCol()))+","+
                             RConst.trimStringFloat(app.blue(asCol()));
    else if (isStr()) return asStr();
    else if (isBloc()) return asBloc().ref;
    else if (isValue()) return asValue().ref;
    else if (isSheet()) return asSheet().value_bloc.ref;
    return "";
  }
  
  sValueBloc bloc = null;
  public Macro_Packet setBloc(sValueBloc b) { bloc = b; return this; }
  boolean isBloc() { return def.equals("bloc"); }
  sValueBloc  asBloc()   { if (isBloc()) return bloc; else return null; }
  
  sValue val = null;
  public Macro_Packet setValue(sValue b) { val = b; return this; }
  boolean isValue() { return def.equals("value"); }
  sValue  asValue()   { if (isValue()) return val; else return null; }
  
  Macro_Sheet psheet = null;
  Macro_Packet setSheet(Macro_Sheet b) { psheet = b; return this; }
  boolean isSheet() { return def.equals("sheet"); }
  Macro_Sheet  asSheet()   { if (isSheet()) return psheet; else return null; }
  
}