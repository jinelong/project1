import java.net.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.*;
/**
 * @author Jin
 * unit that stores online client info
 * giHub addr: git@github.com:jinelong/project1.git
 * to run: java iterClient [serverPort] [ownPort] [yourownport]
 */

/*
 * things to do:
 * 1. buffer
 * 2. the broadcast method yet to implement 'reliable' and 'fifo' feature 
 * 3. this piece of code is not tested, i bet there are tons of bugs
 * 
 * 
 */

/*-------reliable broadcast-------
 * 
 * Every process which receives m for the FIRST time sends it to 
 * every other process (except the sender) and delivers it
 * 
 */

public class iterClient {

	public String myName;
	
	public int serverPort;
	public int ownPort;
	public String serverAddr;
	
	public ServerSocket receive = null;
	public Thread rServer = null;
	public static boolean doNotQuit = true;
	
	ArrayList<client> localList = new ArrayList<client>();
	ArrayList<String> sentMsg = new ArrayList<String>();
	
	
	static int myChannelNum = -1;
	static int msgCounter = 0;
	static String receivedMessageFrom = null;
	
	static boolean cantJoinOrCreate = false;
	
	public enum command {createChannel, joinChannel, stat, quit ,heartbeat} 
	
	//
	class bufferSlot  {
		String userID = null;
		PriorityQueue<String> bufferQ= new PriorityQueue<String>();
		
		
		public bufferSlot(String name){
			userID = name;
		}
		public void add(String msg)
		{
			bufferQ.add(msg);
		}
		
		//get the top msg
		public String poll(){
			if(!bufferQ.isEmpty()){
				return bufferQ.poll();
			}
			
			return null;
		}
		
	}

	ArrayList <bufferSlot> messageBuffer = new ArrayList<bufferSlot>();
	
	
	//should be called when received a boradcast
	void cacheMes(String name, String msg){
		for(int i = 0; i< messageBuffer.size();i++){
			if(messageBuffer.get(i).userID.equals(name)){
				messageBuffer.get(i).bufferQ.add(msg);
				return;
			}
		}
		
	}
	
	// boradcast using multi thread
	class broadcastSender extends Thread{
		
		String _ip ;
		int _port;
		String _msg;
		
		
		public broadcastSender(String ip, int port, String msg){
			_ip = ip;
			_port = port;
			_msg = msg;			
			
		}
		public void run(){
			
			Socket s = null; 
			try{
				s = new Socket(_ip, _port);
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				wr.write(_msg);
				wr.flush();
				wr.close();
				s.close();
			}catch(Exception e){
				System.err.println("something wrong when bradcasting to ip: " + _ip);
				e.printStackTrace();
			}
			
		}
	}
	
	
	/*
	 * there are 2 conditions
	 * 1. you initiate a boradcast, the original sender is you
	 * 2. you are being a part of the reliable broadcast network
	 * 
	 * in the first case, we need to attach myName and my own msgCounter, msg contains the raw message
	 * in the second case, just forward what we got, msg is a meta string that contains the original senders info
	 * 
	 */
	public void broadcast(String msg, String from, boolean isForward) throws NumberFormatException, UnknownHostException, IOException{
	
		Socket s = null; 
		
		//don't have to broadcast if you are the only one in the channel 
		if(localList.size()==0){
			System.out.println("forever alone, cannot broadcast");
			return;
		}
		String content = null;
		
		//this message comes from someone else
		//I did not initiate this message
		//then simply forward it to everyone in the network
		if(isForward){
			content = msg;
		}
		else{
			//instead of sending instances of bmsg 
			//we can do the same thing with the broadcasting message, give it a format:
			//b@Jin@23@this is the 23th message@Jin
			//this means: Jin is sending a broadcast message 23, 
			//the message content is : this is the 23th message, and the initiator of the message is Jin
			
			
			
			content = "b"+"@"+myName+"@"+msgCounter+"@"+msg+"@";
			sentMsg.add(myName+"@"+msgCounter+"@"+msg+"@");
			
			synchronized(this){
				msgCounter++;
			}
			
			System.out.println("msgCounter is " + msgCounter);

						
		}//else
		
		//iterate through the list, send message
		for(int i =0;i<localList.size();i++){
			
			//do not send the message to myself or the person you got the message from
			if(localList.get(i).name.equals(myName) || localList.get(i).name.equals(from)) continue;
			
			new Thread (new broadcastSender(localList.get(i).ip, Integer.parseInt(localList.get(i).chatPort), content)).run();
	
		}
		
		
	}

	public void printClients(){
		if(localList.size() == 1){
			System.out.println("forever alone.... you are the only one in this channel");
			return;
		}
		
		for(int i =0;i<localList.size();i++){
			System.out.println("-------------\nClient: " + i );
			System.out.println("name: " + localList.get(i).name);
			System.out.println("ip: " + localList.get(i).ip);
			System.out.println("port: " + localList.get(i).chatPort);
		}
	}

	public iterClient(String addr, int sPort, String name, int oPort)	throws IOException, InterruptedException {
		serverPort = sPort;
		ownPort = oPort;
		serverAddr = addr;
		
		myName = name;
		
		
		// setup listening server for server info and client message
		// maybe we should make this multi-thread
		rServer = new receiveServer(ownPort);
		rServer.start();
		
		//send stat request to the server
		sendToServer(command.stat, 0);
		
		
	}//constructor
	
	/*
	 * receiveServer is the listener
	 * it listens to the port that you initialized the 
	 * client with, namely, the global variable ownPort
	 * 
	 */
	class receiveServer extends Thread {
		int port;

		public receiveServer(int i) throws IOException {
			port = i;
			receive = new ServerSocket(port);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Socket temp = null;
			System.out.println("Listening server set up, waiting for server response");
			while (doNotQuit) {

				try {
					temp = receive.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(temp.getInputStream()));
					// sample message from server:
					// server@heartbeat

					String str = null;
					String id;// either "server" or "b"
					String instruction; // serverInstruction
					
					// server message format
					// server@mlist@name1$ip1$port1#name2$ip2$port2# @
					// server@waning@messageBody
					// server@heartbeat

					while ((str = rd.readLine()) != null) {
						
						Scanner t1 = new Scanner(str).useDelimiter("@");
						id = t1.next();
						
						//server@xxx@msg
						//id = server
						//instruction = xxx
						//the third part is message itself
						//server@mlist@name#port$
						if (id.equals("server")) {
							instruction = t1.next();
							if (instruction.equals("mlist")) {
								
								//System.out.println("useful substring: " + str.substring(13));
								String listInfo = str.substring(13);
								//System.out.println("receive listInfo: " + listInfo);
								
								Scanner pond = new Scanner(listInfo).useDelimiter("#");
								String entry = pond.next();
								
								//clean previous record
								localList.clear();
								//System.out.println("localList reset, size: " + localList.size());
								
								do{
									Scanner dollar = new Scanner(entry).useDelimiter("\\$");
									//	public client(String n, String i, String  p, int c){
									// name, ip, port, channelNum

									
									localList.add(new client(dollar.next(), dollar.next(), dollar.next(), myChannelNum));
									
									System.out.println(localList.get(localList.size()-1).name + " added to the list" );
									
									try{
										 entry = pond.next();
										
									}
									catch(Exception e){
										break;
									}
								}while(entry !=null);
							
								System.out.println("server sent list info: "+ localList.size() + " clients added");
								
								//display localList
								printClients();
							}//instruction == List
							else if (instruction.equals("heartbeat")) {
								// server request heartbeat
								sendToServer(command.heartbeat, 0);
							}
							else if (instruction.equals("error")) {
								
								//display the error message
								System.err.println(t1.next());
								
								//quit everything
								doNotQuit = false;
								rServer.stop();
								receive.close();
								
								System.exit(0);
								return;

							}
							//server@msg@message
							else if(instruction.equals("msg")){
								System.out.println(t1.next());
							}
							//server@chnl@yourChannelNum
							else if(instruction.equals("chnl")){
								myChannelNum = Integer.parseInt(t1.next());
								System.out.println("got channel num from server: " + myChannelNum);
							}
							//server@rm@name_of_person_who_left
							else if(instruction.equals("rm")){
								String tempName = t1.next();
								boolean ifRemoved = false;
								for(int i=0;i<localList.size();i++){
									if(localList.get(i).name.equals(tempName)){
										localList.remove(i);
										ifRemoved = true;
									}
									
								}//for
								if(ifRemoved){
									System.out.println(tempName + " is removed");
									printClients();
								}else{
									System.err.println("something wrong happened, not sure why ,but "  + tempName + " is not removed");
								}
							}//else if rm

						}// if from server
						
						
				/* at this point: 
				 *   Jin@23@this is the 23th message
				 *   
				 * from this line, is the case in which the client got a broadcast message
				 * we got a braoadcast message, implementation here
				 * note: 
				 *  	at this point, if you call tempString =  t1.next(),
				 *      tempString is the name of the sender
				 *
				 * 		if you call tempString2 = t1.next() again , you get the seq
				 * 			
				 * 
				 *  sending format:
				 *  	String bMsg = "b"+"@"+myName+"@"+msgCounter+"@"+msg+"@";
				 *
				 */
						else if(id.equals("b")){
							String senderName = t1.next();
							String seqStr = t1.next();
							String msg = t1.next();
							
							String historyMsg = senderName+"@"+seqStr+"@"+msg+"@";
							 
							//System.out.println("got a broadcast: "+ senderName + " says: "  + msg + "seq: " + seqStr);

							if(!sentMsg.contains(historyMsg)){
								sentMsg.add(historyMsg);
								System.out.println(senderName + " says: " + msg);
								broadcast("b@"+historyMsg, senderName, true);
								
							}
							
						}
						else{
							System.out.println("got unknown message: " + str);
							
						}
					}//while read line

					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("something wrong when receiving message from client");
				}

			}//
		}

	}// receive
	
	//the second parameter is only in use when the user tries to join a channel
	public void sendToServer(command c, int channel) throws UnknownHostException, IOException {

		///stat@name@22222
		//createChannel@name@22222
		//joinChannel@name@22222@channelNum
		//quit@name@channelNum
		
		//System.out.print("in sendToServer: ");
		String content = null;
		switch (c){
		
			case createChannel:
				content = "createChannel@"+myName+"@"+ownPort+"@";
				//System.out.println("createChannel");
				break;
			
			case joinChannel:
				content = "joinChannel@"+myName+"@"+ownPort+"@"+channel+"@";
				//System.out.println("joinChannel");
				break;
				
			case quit:
				content = "quit@"+myName+"@"+myChannelNum+"@";
				//System.out.println("quit");
				break;
				
			case stat:
				content = "stat@"+myName+"@"+ownPort+"@";
				//System.out.println("stat");
				break;
			case heartbeat:
				content = "heartbeat@"+myName+"@"+myChannelNum+"@";
				//System.out.println("heartBeat");
				break;
		
		}
		
		Socket socket = new Socket(serverAddr, serverPort);
		//System.out.println("socket created");
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		wr.write(content);
		wr.flush();
		//System.out.println("content: " + content + " sent");
		wr.close();
		socket.close();
		
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {

		Socket socket = null;
		iterClient user = null;
		int serverPort;
		int ownPort;
		String name;
		String serverAddr;

		Scanner s = new Scanner(System.in);

		//serverAddr = args[0];
		//serverPort = Integer.parseInt(args[1]);
		//ownPort = Integer.parseInt(args[2]);
		serverAddr = "sslab01.cs.purdue.edu";
		serverPort = 22222;
		ownPort = 22223;
		
		
		System.out.print("please input your name: ");
		name = s.nextLine();
		
		
		try {
			
			user = new iterClient(serverAddr, serverPort, name, ownPort);
			

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("enter your commend, enter 'quit' to disconnect.");
		String str;
		str = s.nextLine();
		while (user.doNotQuit){
			
			//s.next()  quit or b@message
			if(str.equals("quit")){
				user.sendToServer(command.quit, 0);
				break;
			}
			else if(str.equals("create")   && !cantJoinOrCreate){
				user.sendToServer(command.createChannel, 0);
				cantJoinOrCreate = true;
			}
			//b@your_message
			else if(str.indexOf("b@")==0){
				Scanner c =new Scanner (str).useDelimiter("@");
				c.next();
				String content = c.next();
				user.broadcast(content, "", false );
			}
			else if (str.startsWith("join") && !cantJoinOrCreate){
				System.out.println(str.substring(5) + " <---- channelNum");
				user.sendToServer(command.joinChannel, Integer.parseInt(str.substring(5))-1);
				cantJoinOrCreate = true;
			}
			else{
				System.out.println("unrecognized command. and remember you can only in 1 channel");
			}
			str=s.nextLine();

		} // @quit@name
		
		user.rServer.stop();
		System.exit(1);
		

	}// main
}// iterClient
