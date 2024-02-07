package RApplet;

public class User {
	String access = "admin";
	User() {}
	User(String a) { access = RConst.copy(a); }
}
