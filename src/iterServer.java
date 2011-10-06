import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class iterServer {
	
	/**
	 * @author Jin
	 * giHub addr:git@github.com:jinelong/project1.git
	 */
	
	public int maxClient = 1000;
	public Client clientList[] = new Client[maxClient]; 
	public int portC;
	private int top = 0;
	public ServerSocket serverLink;
	public ServerSocket dummyServer;
	
	public int channelCount = 0;
	
	public ArrayList<Channel> channelLists = new ArrayList();
	
	
    Timer timer;

		int i;
		
		//remindTask is a heartbeat tracer
		//every certain interval, it asks if a client is still online
	    class RemindTask extends TimerTask {
	        public void run() {
        		System.out.println("in timer");	
        		Socket s = null;
	        	for( i=0;i<top;i++){
	        		System.out.println("sending heartbeat requst to "+ clientList[i].name);
	        		try {
						 s = new Socket(clientList[i].ip, Integer.parseInt(clientList[i].chatPort));
						s.setSoTimeout(2000);
			    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server@heartbeat@");
			    		wr.flush();
			    		wr.close();

					} catch (SocketTimeoutException e){
						System.out.println("client " + i + "did not respend");
						System.out.println("client name:  " + clientList[i].name );
						System.out.println("client ip: " + clientList[i].chatPort);
						
						removeByName(clientList[i].name);
						
					}catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						
				//		e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						System.out.println("client " + i + " did not respend");
						System.out.println("client name:  " + clientList[i].name );
						System.out.println("client ip: " + clientList[i].chatPort);
						
						removeByName(clientList[i].name);
					//	e.printStackTrace();
					} catch (IOException e) {
						System.out.println("client " + i + " did not respend");
						System.out.println("client name:  " + clientList[i].name );
						System.out.println("client ip: " + clientList[i].chatPort);
						
						removeByName(clientList[i].name);
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
	        		try {
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        	}//for
	           }//run
	    }//RemindTask

	
		
	class Client{
			
			public String name;
			public String ip;
			public String chatPort;
			
			public Client(){
				name = null;
				ip = null;
				chatPort = null;
			}
			
			
			public boolean setPort(String n){
				chatPort = n;
				return true;
			}
			public boolean setIP(String n){
				ip = n;
				return true;
			}
			
			public boolean setName (String n){
				
				
				for(int i=0;i<top;i++){
					if(n.equals(clientList[i].name)){
						return false;
					}
				}
				name = n;
				return true;
			}
			public void close() throws IOException{
				serverLink.close();
			}
		}//client
	
	class Channel{

		public int channelNum = -1;
		public ArrayList<Client> members = new ArrayList();
		
		public Channel(Client c){
			members.add(c);
		}
		
	}
	
	
	public boolean isFull(){
		if(top > maxClient || top == maxClient ) return true;
		else
			return false;
			
	}
	
	public ServerSocket createServers() {
		
		//make connection
    	try{
    		serverLink = new ServerSocket(portC);
	    	System.out.println("createServer done, top is: " + top);
    	}
    	catch (IOException e){
    		System.err.print("error happened when creating server on port"+ portC+ "\n");
			e.printStackTrace();
			 		
    	}
    
    	System.out.println("chatServer created, port is " + portC);
    	System.out.println("current online players: " + (getClientNum()));
    	return serverLink;
	}	
	
	public void removeByName(String name){
		System.out.println("remove callled");
		
		if(top==1 && name.equals(clientList[0].name)){
			String n = clientList[0].name;
			
			clientList[0]=null;
			top --;
			System.out.println(n + " quit from server");
			return;
			
		}
		else if(name.equals(clientList[top-1].name)){
			clientList[top-1] = null;
			top --;
			System.out.println(name + " quit from server");
			return;
		}
		
		else{	
			for(int i=0;i<top;i++){
				if(clientList[i].name.equals(name)){
						String n =clientList[i].name;
						clientList[i].name = clientList[top-1].name;
						clientList[i].chatPort = clientList[top-1].chatPort;
						clientList[i].ip = clientList[top-1].ip;
					
					
					top--;
					System.out.println(n + " quit from server");
	
					return;
				}
				
			}//for
		}
		
		
		
	}
	
	public void passUserList(String addr, int port) throws UnknownHostException, IOException{
		Socket socket = new Socket(addr, port);
		
		try{
    		//send connection confirmation
	    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    		
	    		if(top==1)
	    			wr.write("server@list@you are the only one on the server, channel created, you are in channel 1\n");
	    		else{
	    			wr.write("server@list@");
	    		
		    		for(int i=0;i< top; i++){
		    			wr.write(clientList[i].name+"$"+clientList[i].ip+"$"+clientList[i].chatPort+"#");
		    			
		        	}
		    		
	    		}
				    
			    
			    wr.close();
	    	}
    		catch(IOException e){
    			System.err.println("something wrong when sending userList");
    		}
		   
		    
			try {socket.close();}
			catch (IOException e) {e.printStackTrace();}

	}//passUserList
	

	public boolean createClient() throws IOException{
		
		if(!isFull()){
			clientList[top] = new Client();
			top ++;
		}
		else {
			
			return false;
		}
			
		return true;
	}
	
	public iterServer(int _port){
		 timer = new Timer();
	     timer.scheduleAtFixedRate(new RemindTask(), 0, 1000*20);
	     //timer.schedule(new RemindTask(), 0, 3*1000);

		portC = _port;
	}
	
	public void removeCurrentClient(){
		--top;
	}
	
	public int getClientNum (){ return top;}
	

	public Client getCurrentClient(){
			
		return clientList[top-1];
	}
	
		
    public static void main(String[] args) throws IOException {
    	
    	//args[0] =  "22222";
        int port = 22222;
    	//port =Integer.parseInt(args[0]);
    	iterServer s1 = new iterServer(port);
    
    	while(true){
    		Socket temp = null;
    	    	
		  	    ServerSocket tempServer= s1.createServers();
		  	    temp = tempServer.accept();
	
		  	    // client login format:
		  	    // enter@name@22223@channelNum
		  	    
		    try {
		        BufferedReader rd = new BufferedReader(new InputStreamReader(temp.getInputStream()));

		        String str= null;
		        String name;
		        String ip = temp.getInetAddress().getHostAddress().toString();
		                 
		        
		        
		        String p;
		        String status;
		        while ((str = rd.readLine()) != null) {
		        //	System.out.println("read from socket: " + str);
		        	
		        	
		        	Scanner t1 = new Scanner(str).useDelimiter("@");
		        		
		        	status = t1.next();// either "enter" or "quit"
		        	name = t1.next();//name
		        	p = t1.next();//port
		        	
		        	if(status.equals("enter") && !s1.isFull()){
				       			        
				        s1.createClient();
				        if(s1.getCurrentClient().setName(name)){
				        	 s1.getCurrentClient().setIP(ip);
				        	 s1.getCurrentClient().setPort(p);
				   
				        	 s1.passUserList(ip, Integer.parseInt(p));
				        	 for(int i =0;i<s1.top;i++){
				        		 s1.passUserList(s1.clientList[i].ip, Integer.parseInt(s1.clientList[i].chatPort));
				        	 }
				        	 
				        		System.out.println("hello sent");
			        	}else{
			        		
			        		System.out.println("name already exist");
			        		Socket s = new Socket(ip, Integer.parseInt(p));
			        		
			        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				    		wr.write("server@warning@Server: name is already taken, please change a name and reconnect@");
			        		wr.flush();
			        		wr.close();
			        		s1.removeCurrentClient();
			        	}
			        
		         }else if(status.equals("quit")){
		        		
		        		s1.removeByName(name);
		        		
		        
		        }else if(status.equals("heartbeat")){
		        	
		        		System.out.println(name + " is alive");
		        	
		        }
		         
		         else{ 
		        	
		        		System.out.println("in full case");

			        	Socket s = new Socket(ip, Integer.parseInt(p));
			        	s.setSoTimeout(2000);
		        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server@warning@server full, come back later");
		        		wr.flush();
		        		wr.close();
		        		s1.removeCurrentClient();
			        	s1.dummyServer.close();
		        		System.out.println("system full in else");
		        		
			        	break; 
			        }
		  
		      }//while_readSocket
		       
		        rd.close();
		   
		    } catch(SocketTimeoutException e){
		    	System.err.println("dummyServer Timeout");
		    	
		    }catch (IOException e) {
		    	e.printStackTrace();
		    	System.err.println("something wrong when receiving message from client");
		    	
		    }
			 temp.close();
    		 s1.serverLink.close();
	    
    	
    	}//while_ture
    	
    }//main
   
}//iterServ