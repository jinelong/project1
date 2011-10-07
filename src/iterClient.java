import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


// to run:
// java iterClient [serverPort] [ownPort] [yourownport]

/*
 * things to do:
 * 1. buffer
 * 2. the broadcast method yet to implement 'reliable' and 'fifo' feature 
 * 3. this piece of code is not tested, i bet there are tons of bugs
 * 
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
	static int myChannelNum = -1;
	static int msgCounter = 0;
	
	
	public enum command {createChannel, joinChannel, stat, quit ,heartbeat} 
	
	
	
	public void broadcast(String msg) throws NumberFormatException, UnknownHostException, IOException{
	
		Socket s = null; 
		if(localList.size()==0)
			System.out.println("you are the only one in the channel, cannot broadcast");
		else{
			//instead of sending instances of bmsg 
			//we can do the same thing with the broadcasting message, give it a format:
			//b@Jin@23@this is the 23th message
			//this means: Jin is sending a broadcast message 23, the message content is : this is the 23th message
			
			String bMsg = "b"+"@"+myName+"@"+msgCounter+"@"+msg+"@";
			
			//iterate through the list, send message
			for(int i =0;i<localList.size();i++){
				
				s = new Socket(localList.get(i).ip, Integer.parseInt(localList.get(i).chatPort));
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				wr.write(bMsg);
				wr.flush();
				wr.close();
				s.close();
				
			}
		}//else
		
		
		System.out.println("boradcast sent");
		msgCounter++;
		
	}

	public void printClients(){
		
		for(int i =0;i<localList.size();i++){
			System.out.println("-------------\nClient: " + i );
			System.out.println("name: " + localList.get(i).name);
			System.out.println("ip: " + localList.get(i).ip);
			System.out.println("port: " + localList.get(i).chatPort);
		}
	}

	public iterClient(String addr, int sPort, String myName, int oPort)	throws IOException, InterruptedException {
		serverPort = sPort;
		ownPort = oPort;
		serverAddr = addr;
		
		// setup listening server for server info and client message
		// maybe we should make this multi-thread
		rServer = new receiveServer(ownPort);
		rServer.start();
		
		//send stat request to the server
		sendToServer(command.stat);
		
		
	}//constructor
	
	/*
	 * this is the listener
	 * basically all it does is listening to the port that you initialized the 
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
					

					// server@mlist@name1$ip1$port1#name2$ip2$port2# @
					// server@waning@messageBody
					// server@heartbeat

					while ((str = rd.readLine()) != null) {
						
						//System.out.println("receive: " + str);
						Scanner t1 = new Scanner(str).useDelimiter("@");
						id = t1.next();
						
						//server@xxx@msg
						//id = server
						//instruction = xxx
						//the third part is message itselfs
						//server@mlist@name#port$
						if (id.equals("server")) {
							instruction = t1.next();
							if (instruction.equals("mlist")) {
								
								System.out.println(str.substring(13));
								String listInfo = str.substring(13);
								System.out.println("receive listInfo: " + listInfo);
								
								Scanner pond = new Scanner(listInfo).useDelimiter("#");
								String entry = pond.next();
								
								
								do{
									Scanner dollar = new Scanner(entry).useDelimiter("\\$");
									//	public client(String n, String i, String  p, int c){

									localList.add(new client(dollar.next(), dollar.next(), dollar.next(), myChannelNum));
									
									System.out.println(localList.get(localList.size()-1).name + " added to the list" );
									
									try{
										if(pond.hasNext()) entry = pond.next();
										else{ System.err.println("pond does not have next");}
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
								sendToServer(command.heartbeat);
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
								}else{
									System.err.println("something wrong happened, not sure why ,but "  + tempName + " is not removed");
								}
							}//else if rm

						}// if from server
						
						
						/* at this point: 
						 *   Jin@23@this is the 23th message
						 *   
						 * from this line, is the case in which the client got a broadcast
						 * we got a braoadcast message, implementation here
						 * note: 
						 *  	at this point, if you call tempString =  t1.next(),
						 *      tempString is the name of the sender
						 *
						 * 		if you call tempString2 = t1.next() again , you get the seq
						 * 
						 */
						else if(id.equals("b")){
							String senderName = t1.next();
							String seqStr = t1.next();
							String msg = t1.next();
							
							//buffer it or send
							
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
	
	
	public void sendToServer(command c) throws UnknownHostException, IOException {
		

		///stat@name@22222
		//createChannel@name@22222
		//joinChannel@name@22222@channelNum
		//quit@name@channelNum
		String content = null;
		switch (c){
		
			case createChannel:
				content = "createChannel@"+myName+"@"+ownPort+"@";
				break;
			
			case joinChannel:
				content = "joinChannel@"+myName+"@"+ownPort+"@"+myChannelNum+"@";
				break;
				
			case quit:
				content = "quit@"+myName+"@"+myChannelNum+"@";
				break;
				
			case stat:
				content = "stat@"+myName+"@"+ownPort+"@";
				break;
			case heartbeat:
				content = "heartbeat@"+myName+"@"+myChannelNum+"@";
				break;
		
		}
		
		Socket socket = new Socket(serverAddr, serverPort);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		wr.write(content);
		wr.flush();
		wr.close();
		socket.close();
		
	}
	
	/*
	
	public void send(String serverAddr, int serverPort, String message) {

		Socket socket = null;

		try {

			socket = new Socket(serverAddr, serverPort);
//			socket.setSoTimeout(5000);

		} catch (SocketTimeoutException e) {
			System.err.println("send socket timeout, please check server port and address");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// send connection confirmation
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			wr.write(message);
			// wr.write("n5"+"@"+ip+"@"+"23433235@");
			wr.flush();
			wr.close();
		} catch (IOException e) {
		//	e.printStackTrace();	
			System.err.println("something wrong when sending message, try again....");
		}

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
*/
	public static void main(String[] args) throws InterruptedException, IOException {

		Socket socket = null;
		iterClient user = null;
		int serverPort;
		int ownPort;
		String name;
		String serverAddr;

		Scanner s = new Scanner(System.in);

		serverAddr = args[0];
		serverPort = Integer.parseInt(args[1]);
		ownPort = Integer.parseInt(args[2]);
		
		System.out.print("please input your name: ");
		name = s.nextLine();
		s.close();
		
		try {

			user = new iterClient(serverAddr, serverPort, name, ownPort);
			user.myName = name;
		

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String str;
		str = s.nextLine();
		while (user.doNotQuit){
			
			System.out.println("enter your commend, enter 'quit' to disconnect. to chat, type \"chat@[clientNum]@[your message]\"");
			//s.next()  quit or b@message
			if(str.equals("quit")){
				user.sendToServer(command.quit);
				break;
			}
			//b@your_message
			else if(str.indexOf("b@")==0){
				Scanner c =new Scanner (str).useDelimiter("@");
				c.next();
				String content = c.next();
				user.broadcast(content);
			}
			else{
				System.out.println("unrecognized command");
			}
			str=s.nextLine();

		} // @quit@name
		
		user.rServer.stop();
		System.exit(1);
		

	}// main
}// iterClient
