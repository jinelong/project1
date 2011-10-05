package local.bin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;





public class LabCheckActivity extends Activity {
	
	private TextView t = null;
	private final String PATH = "/data/";  //put the downloaded file here
	
	private String [] labName ;
	
	
	private class rooms {
	 	
	 	public String name;
	 	public String room;
	 	public String status;
	 	
	 	public rooms(String n, String r, String s){
	 		name = n;
	 		room = r;
	 		status = s;
	 		
	 	}
	 	
	 }

	ArrayList<rooms> roomList ;
	boolean newRoomFlag = true;
	String bName = null;
	String rName = null;
	String status = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        t= (TextView)findViewById(R.id.t1);
        String a = "https://www.purdue.edu/apps/ics/LabMap";
        String b = "http://www.purdue.edu/";
       // t.setVisibility(View.INVISIBLE);
        
        DownloadFromUrl(a, "/mnt/sdcard/lab");
        
        //FileInputStream fstream = null;
        
        
        
        try {
			read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        finish();
        
        
		
    }
    
    private void parse(String line){
		Pattern room = Pattern.compile("<br>");
		String[] result = room.split(line);
	    
		for (int i=0; i<result.length; i++){
	        	
	        	//will be no room without building
	        	if(result[i].contains("building=")){
	        		
	        		final String temp = "building=";
	        		final String temp2 = "room=";
	        		
	        		int start = result[i].indexOf(temp);
	        		int end = result[i].indexOf("&");
	        		bName = result[i].substring(start+temp.length(), end);
	        		
		        	System.out.println(bName);
		        	Log.d("roomInfo", bName);
		        	if(result[i].contains(temp2)){
		        		
		        		int start2 = result[i].indexOf(temp2);
		        		//System.out.println("start2 : " + start2);
		        		int end2 = result[i].indexOf(">"+bName);
		        		//System.out.println("end2 : " + end2);
		        		rName = result[i].substring(start2+temp2.length(),end2);
		        		System.out.println(rName);
		        		Log.d("roomInfo", rName);
		        	}
	        	
	        	}
	        	else if(result[i].contains("<font size=-2>")){
	        		
	        		
	        		final String temp = "<font size=-2>";
	        		final String temp2 = "</font>";
	        		
	        		int start = result[i].indexOf(temp);
	        		int end = result[i].indexOf(temp2);
	        		
	        		if(start>-1 && end>-1 && start<end){
		        		status = result[i].substring(start+temp.length(), end);
		        		System.out.println(status);
		        		Log.d("roomInfo", status);
		        		newRoomFlag = false;
	        		}
	        		else{
	        			System.out.println(result[i]);
	        			System.out.println("start : " + start);
	        			System.out.println("end : " + end);
	        		
	        		}
	        	}
		
	        	if(!newRoomFlag){
	        		roomList.add(new rooms(bName, rName, status));
	        		newRoomFlag = true;
	        	}
	        }//for
    	}//generate
    
    
    
    private void read() throws IOException {
    	String a ;
        Log.d("downloader", "Reading from file.");
        File fFileName = new File("/mnt/sdcard/lab");
        Scanner scanner = new Scanner(new FileInputStream(fFileName));
    
        Pattern p = Pattern.compile("(\\s+)?maparray\\[\\d+\\]\\[4\\]");
		//Pattern p2 = Pattern.compile("a href=LabInfo?building=\\S+&room=\\d+>");
     
        try {
          while (scanner.hasNextLine()){
            String line = scanner.nextLine();
			Matcher m = p.matcher(line);
			boolean r = m.find();
			if(r)
				parse(line);
				Log.d("downloader", line);
          	
          	}
          }
        
        finally{
        	
        	 scanner.close();
        }
       
        
      }
    	 
        
   

	public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
                try {
                        URL url = new URL(imageURL); //you can write here any link
                        File file = new File(fileName);
 
                        long startTime = System.currentTimeMillis();
                        Log.d("downloader", "download begining");
                        Log.d("downloader", "download url:" + url);
                        Log.d("downloader", "downloaded file name:" + fileName);
                        /* Open a connection to that URL. */
                        URLConnection ucon = url.openConnection();
 
                        /*
                         * Define InputStreams to read from the URLConnection.
                         */
                        InputStream is = ucon.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
 
                        /*
                         * Read bytes to the Buffer until there is nothing more to read(-1).
                         */
                        ByteArrayBuffer baf = new ByteArrayBuffer(50);
                        int current = 0;
                        while ((current = bis.read()) != -1) {
                                baf.append((byte) current);
                        }
 
                        /* Convert the Bytes read to a String. */
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(baf.toByteArray());
                        fos.close();
                        Log.d("downloader", "download ready in"
                                        + ((System.currentTimeMillis() - startTime) / 1000)
                                        + " sec");
 
                } catch (IOException e) {
                        Log.d("downloader", "Error: " + e);
                }
 
        }
    
}