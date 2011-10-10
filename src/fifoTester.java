import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * @author: Jin
 * hardcoded tester file
 * target machien should be sslab02
 * this program should be running on sslab10
 * 
 * name: tester
 * 
 */
public class fifoTester {
	
	public static class receiveServer extends Thread{
		
		public void run(){
			while(true){
			try {
				ServerSocket s = new ServerSocket(22223);
				Socket s2 = s.accept();
				
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(s2.getInputStream()));
			
				String str = null;
				while ((str = rd.readLine()) != null) {
					System.out.println(str);
				}
				rd.close();
				s2.close();
				s.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}//while
		}//run
		
	}

	public static void main(String[] a) throws UnknownHostException, IOException, InterruptedException{
		
		
		new Thread(new receiveServer()).start();
		
		String m1 = "b@tester@0@this is message 1";
		String m2 = "b@tester@1@this is message 2";
		String m3 = "b@tester@2@this is message 3";
		String m4 = "b@tester@3@this is message 4";
		String m5 = "b@tester@4@this is message 5";
		
		System.out.println("sending message 5");
		Socket s = new Socket("sslab02.cs.purdue.edu", 22223);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write(m5);
		wr.flush();
		wr.close();
		s.close();
		Thread.sleep(2000);
		
		System.out.println("sending message 3");
		s = new Socket("sslab02.cs.purdue.edu", 22223);
		wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write(m3);
		wr.flush();
		wr.close();
		s.close();	
		Thread.sleep(2000);
		
		System.out.println("sending message 2");
		s = new Socket("sslab02.cs.purdue.edu", 22223);
		wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write(m2);
		wr.flush();
		wr.close();
		s.close();	
		Thread.sleep(2000);
		
		System.out.println("sending message 4");
		s = new Socket("sslab02.cs.purdue.edu", 22223);
		wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		wr.write(m4);
		wr.flush();
		wr.close();
		s.close();	
		Thread.sleep(2000);
		
		System.out.println("sending message 1");
		s = new Socket("sslab02.cs.purdue.edu", 22223);
		wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));		
		wr.write(m1);
		wr.flush();
		wr.close();
		s.close();
		
	}
}
