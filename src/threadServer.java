import java.util.*;
import java.io.*;
import java.net.*;

public class threadServer extends Thread {
	
	/**
	 * @author Jin
	 * unit that stores online client info
	 * giHub addr: git@github.com:jinelong/project1.git
	 */
	
	//public static int maxClient = 100;
	//public static Client clientList[] = new Client[maxClient]; 
	//private static int top = 0;
	
	public int portC;
	private Socket temp;
	//public ServerSocket serverLink;
	public ServerSocket dummyServer;
	static boolean ifTimer = false;
	static Timer timer;
    
	final static int maxChannelNum = 5;
	static int channelCounter = 1;
	
	class Client{
		
		public String name;
		public String ip;
		public String chatPort;
		public int channel;
		
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
		
		public boolean setName (String n, int channel){
			
			
			for(int i=0;i<channelList.get(channel).members.size();i++){
				if(n.equals(channelList.get(channel).members.get(i).name)){
					return false;
				}
			}
			name = n;
			this.channel = channel;
			
			return true;
		}
	//	public void close() throws IOException{
	//		temp.close();
	//	}
	}//client
    
	
	class Channel {
		String name;
		ArrayList<Client> members = new ArrayList();
		public Channel(String n){
			name = n;
		}
		
	}
	public enum command {ERROR, MSG, STAT, CHNL, CLIST };
		
	static ArrayList<Channel> channelList = new ArrayList();
	
	void sendMsg(String ip, String p, String msg, command c ) throws NumberFormatException, UnknownHostException, IOException{
		
	
		String content = "server@";
		
		switch(c){
			
			case ERROR:
					content+="error@"+msg;
					break;
			case MSG:
					content+="msg@"+msg;
					break;
			case STAT:
					sendStat(ip, p);
					break;
			case CHNL:
					content+="chnl@"+msg;
					break;
			case CLIST:
					sendMemberList(ip,p,msg);
			
		}
		Socket s = new Socket(ip, Integer.parseInt(p));
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write(content);
		
		
		wr.flush();
		wr.close();
		s.close();

	}
	// client will use this info to build its own member list
	synchronized void sendMemberList(String ip, String p, String channelNum) throws NumberFormatException, UnknownHostException, IOException{
		int channel = Integer.parseInt(channelNum);
		
		
		if(channelList.get(channel).members.size() == 0){
				sendMsg(ip,p,"you are the only one in the channel", command.MSG);
			
		}//if
		else{
			Socket s = new Socket(ip, Integer.parseInt(p));
			String content = "server@clist@";
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
			for(int i =0; i< channelList.get(channel).members.size(); i++){
				content+= channelList.get(channel).members.get(i).name + "#" + channelList.get(channel).members.get(i).chatPort + "$";
			}
			wr.write(content);
			wr.flush();
			wr.close();
			s.close();
		}//else
		
	}
	synchronized void sendStat(String ip, String p) throws IOException{
		Socket s = new Socket(ip, Integer.parseInt(p));
		
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write(channelList.size() + " out of " + maxChannelNum+ " created");
		if(channelList.size()>0){
			for(int i = 0; i < channelList.size(); i++){
					wr.write("channel " + (i+1) + " has " + channelList.get(i).members.size() + " members\n");

			}
			
		}//if
		else{
			wr.write("no channel created, you are the first one on this server\n you can enter creatChannel@[yourName]@[yourPort] to create one");
		}
		wr.flush();
		wr.close();
		
	}
	
	 static class RemindTask extends TimerTask {
	        public void run() {
	        	int i = 0;
        		System.out.println("in timer");	
        		Socket s = null;
        		
        		//iterate through the channel
	        	for( i=0;i<channelList.size();i++){
	        		//iterate through the member of this channel
	        		for(int j=0;j<channelList.get(i).members.size(); j++){
	        			
	        			
	        		System.out.println("sending heartbeat requst to "+ channelList.get(i).members.get(j).name);
	        		try {
						 s = new Socket(channelList.get(i).members.get(j).ip, Integer.parseInt(channelList.get(i).members.get(j).chatPort));
						s.setSoTimeout(2000);
			    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server@heartbeat@");
			    		wr.flush();
			    		wr.close();

					} catch (SocketTimeoutException e){
						System.out.println("client " + j + "did not respend");
						System.out.println("client name:  " + channelList.get(i).members.get(j).name );
						System.out.println("client ip: " + channelList.get(i).members.get(j).ip);
						
						removeByName(channelList.get(i).members.get(j).name, ""+channelList.get(i).members.get(j).channel);
						
					}catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						
				//		e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						System.out.println("client " + j + "did not respend");
						System.out.println("client name:  " + channelList.get(i).members.get(j).name );
						System.out.println("client ip: " + channelList.get(i).members.get(j).ip);
						

						removeByName(channelList.get(i).members.get(j).name, ""+channelList.get(i).members.get(j).channel);

					//	e.printStackTrace();
					} catch (IOException e) {
						System.out.println("client " + j + "did not respend");
						System.out.println("client name:  " + channelList.get(i).members.get(j).name );
						System.out.println("client ip: " + channelList.get(i).members.get(j).ip);
						
						removeByName(channelList.get(i).members.get(j).name, ""+channelList.get(i).members.get(j).channel);
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
	        		try {
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        		}//for_j
	        	}//for_i
	           }//run
	    }//RemindTask

	
	public void run(){
		
  	    // stat@name@222222
		// createChannel@name@port
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
	        	p = t1.next();//port/channelNum when status == quit
	        	
	        	//stat@name@22222@
	        	//createChannel@name@22222
	        	//joinChannel@name@22222@channelNum
	        	//quit@name@channelNum
	        	
	        	if(status.equals("stat")){
	        		
	        		sendMsg(ip,p,"",command.STAT);
			       			        
		        // server@list@name1$ip1$port1@
		        // server@waning@messageBody
		        // server@heartbeat			        
		        //from client: myName+"@ownPort+"@"
	        	//enter@clientName@port
			       
		      /*  	
			        createClient();
			        if(getCurrentClient().setName(name)){
			        	 getCurrentClient().setIP(ip);
			        	 getCurrentClient().setPort(p);
			        	 
			   
			        	 passUserList(ip, Integer.parseInt(p));
			        	 for(int i =0;i<top;i++){
			        		 passUserList(clientList[i].ip, Integer.parseInt(clientList[i].chatPort));
			        	 }
			        	 
			        		System.out.println("hello sent");
		        	}else{
		        		
		        		System.out.println("name already exist");
		        		Socket s = new Socket(ip, Integer.parseInt(p));
		        		
		        		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			    		wr.write("server@warning@Server: name is already taken, please change a name and reconnect@");
		        		wr.flush();
		        		wr.close();
		        		removeCurrentClient();
		        	}
		        	*/
		        
	         }else if(status.equals("quit")){
	        		
	        		removeByName(name, p);
	        		
	        		//for(int i =0; i<s1.top;i++){
	        			
	        		//	s1.passUserList(s1.clientList[i].ip, Integer.parseInt(s1.clientList[i].chatPort));
	        		//}
	        		
	        
	        }else if(status.equals("heartbeat")){
	        	
	        		System.out.println(name + " is alive");
	        	
	        }else if(status.equals("createChannel")){
	        	
	        	
	        	if(channelCounter <= maxChannelNum){
    		
	        			Client c = new Client();
	        			c.setName(name, channelCounter);
	        			c.setIP(ip);
	        			c.setPort(p);
	        			
	        			Channel newC = new Channel(""+ channelCounter);
	        			newC.members.add(c);
	        			
		        		channelList.add(newC);

		        		
		        		sendMsg(ip, p, ""+channelCounter, command.CHNL);
		        		sendMsg(ip, p, ""+channelCounter, command.CLIST);
		        		channelCounter ++;
	        		
	        	}
	        	else{
	        		sendMsg(ip,p,"server has reached maximum number of channels, maybe you wanna join a chennel?", command.ERROR);
	        	}

	        	
	        }//createChennel
	        else if(status.equals("joinChannel")){
	        	if(t1.hasNext()){
	        		int channelIndex = Integer.parseInt(t1.next());
	        		if(channelIndex > channelCounter){
	        			sendMsg(ip, p, "you are trying to join a channel that has not been created, please create the channel first", command.ERROR);
	        		}
	        		else{
	        			Client c = new Client();
	        			if(c.setName(name, channelIndex)){
	        				c.setPort(p);
	        				c.setIP(ip);
	        				channelList.get(channelIndex).members.add(c);
	        				sendMsg(ip,p,""+channelIndex, command.CHNL);
	        			}
	        		}
	        	}
	        	else{
	        		sendMsg(ip, p, "no channel Number specifiled", command.ERROR);
	        	}
	        	
	        }
	         
	      }//while_readSocket
	       
	        rd.close();
	   
	    } catch(SocketTimeoutException e){
	    	System.err.println("dummyServer Timeout");
	    	
	    }catch (IOException e) {
	    	e.printStackTrace();
	    	System.err.println("something wrong when receiving message from client");
	    	
	    }
		 try {
			temp.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	
	}//run


	    
	    
	
	/*
	public boolean isFull(){
		if(top > maxClient || top == maxClient ) return true;
		else
			return false;
			
	}
	
	
	*/
	
	
	public synchronized static void removeByName(String name, String channel){
		System.out.println("remove callled");
		
		boolean ifRemoved = false;
		int channelNum = Integer.parseInt(channel);
		for(int i =0; i< channelList.get(channelNum).members.size();i++)
			if(channelList.get(channelNum).members.get(i).name.equals(name)){
				channelList.get(channelNum).members.remove(i);
				ifRemoved = true;
				break;
			}
		
		if(ifRemoved)
			System.out.println("remove done");

	}
	
	/*
	public synchronized void  passUserList(String addr, int port) throws UnknownHostException, IOException{
		Socket socket = new Socket(addr, port);
		
		try{
    		//send connection confirmation
	    		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    		
	    		if(top==1)
	    			wr.write("server@list@you are the only one on the server\n");
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
	
	
	public synchronized boolean createClient() throws IOException{
		
		if(!isFull()){
			clientList[top] = new Client();
			top ++;
		}
		else {
			
			return false;
		}
			
		return true;
	}
	
	
	*/
	public threadServer(Socket t){
		
		 temp = t;
		 if(!ifTimer){
			 ifTimer=true;
			 createTimer();
		 }
	     //timer.schedule(new RemindTask(), 0, 3*1000);

		
	}
	 static void createTimer(){
		 timer = new Timer();
	     timer.scheduleAtFixedRate(new RemindTask(), 0, 1000*20);
		
	}
	 /*	
	public synchronized void removeCurrentClient(){
		--top;
	}
	
	public synchronized int getClientNum (){ return top;}
	

	public synchronized Client getCurrentClient(){
			
		return clientList[top-1];
	}
	
*/		
    public static void main(String[] args) throws IOException {
    	
    	//args[0] =  "22222";
        int port = 22222;
    	//port =Integer.parseInt(args[0]);
		ServerSocket tempServer= new ServerSocket(port);

    	while(true){
    		System.out.println("waitting for incomeing connections");
    		Socket temp = tempServer.accept();
        	new Thread( new threadServer(temp)).start();
        	
    	}//while
	
    }//main
   
}//iterServ