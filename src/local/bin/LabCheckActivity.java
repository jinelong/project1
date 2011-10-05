package local.bin;


//github addr:
//git@github.com:jinelong/labcheck.git

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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/*
 * further UI design
 * 1. Listview
 * 2. make progress bar (use progressbar to display availability)
 * 3. maybe google map integration
 * 4. splash screen
 * 5. opmize for tablet
 * 
 * back-end
 * 1. pull the geographical location from the source
 * 2. check if the site is down
 * 3. figure out map view
 * 4. add "find a computer" function. PC and Mac
 * 5. 
 * 
 *
 * 
 *		// maparray[i][0] contains the name of the Building
		// maparray[i][1] contains the longitude coordinate
		// maparray[i][2] contains the latitude coordinate
		// maparray[i][3] contains the percent of total computers available in the lab containing the most available computers for the lab
		// maparray[i][4] contains the HTML content of the notes field
 * */





public class LabCheckActivity extends Activity {
	
	private TextView t = null;
	private final String PATH = "/data/";  //put the downloaded file here
	
	private String [] labName ;
	
	
	private class rooms {
	 	
	 	public String building;
	 	public String room;
	 	public String status;
	 	public String longtitude;
	 	public String latitude;
	 	
	 	public rooms(String b, String r, String s, String lg, String la){
	 		building = b;
	 		room = r;
	 		status = s;
	 		longtitude = lg;
	 		latitude = la;
	 		
	 		
	 	}
	 	
	 }//rooms

	ArrayList<rooms> roomList = new ArrayList();
	boolean newRoomFlag = true;
	String bName = null;
	String rName = null;
	String status = null;
	String currentLatitude = null;
	String currentLongtitude = null;
	

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        t= (TextView)findViewById(R.id.t1);
        t.setMovementMethod(new ScrollingMovementMethod());
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
        
        //finish();
        String content = roomList.size() + " computer labs found";
        for(int i =0; i< roomList.size(); i++){
        	content += "\n" + roomList.get(i).building+" "+roomList.get(i).room+ "\n" + roomList.get(i).longtitude+ roomList.get(i).latitude+"\n" + roomList.get(i).status + "\n";
        	
        }
        t.setText(content);
        t.setVisibility(View.VISIBLE);
        
        
        
		
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
		        	
	        		newRoomFlag = true;

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
	        		roomList.add(new rooms(bName, rName, status, currentLongtitude, currentLatitude));
	        		Log.d("roomInfo","item added");
	        		newRoomFlag = true;
	        	}
	        }//for
    	}//generate
    
    
    
    private void read() throws IOException {
    	String a ;
        Log.d("downloader", "Reading from file.");
        File fFileName = new File("/mnt/sdcard/lab");
        Scanner scanner = new Scanner(new FileInputStream(fFileName));
    
        Pattern p = Pattern.compile("(^\\s+)?maparray\\[\\d+\\]\\[4\\]");
        Pattern longtitude =Pattern.compile("(^\\s+)?maparray\\[\\d+\\]\\[1\\]");
        Pattern latitude =Pattern.compile("(^\\s+)?maparray\\[\\d+\\]\\[2\\]");
        
		//Pattern p2 = Pattern.compile("a href=LabInfo?building=\\S+&room=\\d+>");
     
        try {
          while (scanner.hasNextLine()){
            String line = scanner.nextLine();
			
            Matcher m = p.matcher(line);
			Matcher longti = longtitude.matcher(line);
			Matcher lati = latitude.matcher(line);
			
			boolean info = m.find();
			
			
			
			if(info){
				Log.d("downloader", line);
				parse(line);
          	}
			else if( longti.find()){
				Log.d("downloader", line);
				Pattern splitEqual = Pattern.compile("=");
				String[] result = splitEqual.split(line);
				
				currentLongtitude = result[1].substring(0, result[1].length()-1);
				Log.d("roomInfo", currentLongtitude);
			}
			else if(lati.find()){
				Log.d("downloader", line);
				Log.d("downloader", line);
				Pattern splitEqual = Pattern.compile("=");
				String[] result = splitEqual.split(line);
				
				currentLatitude = result[1].substring(0, result[1].length()-1);
				
				Log.d("roomInfo", currentLatitude);
				
			}
          }//while
          
          }//try
        
        finally{
        	 scanner.close();
        }
      }//read
   

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