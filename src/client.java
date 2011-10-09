/**
	 * @author Jin
	 * unit that stores online client info
	 * giHub addr: git@github.com:jinelong/project1.git
	 * 
	 */

/*
 * 
 * this class is the generic class of the client
 * server will change  the setName method with polymorphism
 * client will use the default setName method 
 * 
 */

public class client {

	
	public String name;
	public String ip;
	public String chatPort;
	public int channel;
	
	public client(){
		name = null;
		ip = null;
		chatPort = null;
		channel = -1;
	}
	public client(String n, String i, String  p, int c){
		name = n;
		ip = i;
		chatPort = p;
		channel = c;
		
	}
	
	public boolean setPort(String n){
		chatPort = n;
		return true;
	}
	public boolean setIP(String n){
		ip = n;
		return true;
	}
	public void setName (String n){
		name = n;
	}
	
}