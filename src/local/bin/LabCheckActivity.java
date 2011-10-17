package local.bin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


//github addr:
//git@github.com:jinelong/labcheck.git


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





public class LabCheckActivity extends ListActivity {
	
	private enum COMPUTER  {ALL, WIN, MAC}
	
	private TextView t = null;
	
	
	public ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	ArrayAdapter<CharSequence> adapter;
	Spinner selectStation;

	
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

	static ArrayList<rooms> roomList = new ArrayList<rooms>();
	static ArrayList<rooms> roomAvailable = new ArrayList<rooms>();
	static ArrayList<rooms> roomNoAvailable = new ArrayList<rooms>();

	boolean newRoomFlag = true;
	String bName = null;
	String rName = null;
	String status = null;
	String currentLatitude = null;
	String currentLongtitude = null;
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		
		menu.add(0, 1, 1, "find me a PC");
		menu.add(0, 2, 2, "find me a mac");
		menu.add(0, 3, 3, "LabMap");
		menu.add(0, 4, 4, "quit");
	
		return super.onCreateOptionsMenu(menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		
		case 4:
			finish();
			break;

		case 3:
			
			Intent gotoMapview = new Intent();
			gotoMapview.setClass(LabCheckActivity.this, mapview.class);
			this.startActivity(gotoMapview);


			break;
		// about
		case 2:
			updateList(roomAvailable, COMPUTER.MAC);
			//intent.setClass(main.this, about.class);
			//startActivity(intent);
			break;

		// help
		case 1:
			updateList(roomAvailable, COMPUTER.WIN);
			//startActivity(help);
			break;
		}// switch
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		Toast toast = Toast.makeText(this, list.size() + " are available",  Toast.LENGTH_SHORT);
    	toast.show();
    	
    	Log.d("itemClick", "position: " + position);
    	
    	super.onListItemClick(l, v, position, id);

		// when an entry is clicked, connect tio the client, and setup voice
		// clal
	}
	
	void updateList(ArrayList<rooms> r, COMPUTER type ) {

		list.clear();
		
		if(type.equals(COMPUTER.ALL)){
		
			try {
				
				for (int i =0; i< r.size(); i++){
					HashMap<String, String> tempHash = null;				
					
					String name = r.get(i).building + " " + r.get(i).room;
					String status = r.get(i).status;
					
					Log.d("",name);
					Log.d("",status);
					tempHash = new HashMap<String, String>();
					tempHash.put("room", name);
					tempHash.put("status", status);
					
				
					
					list.add(tempHash);
	
				}

			} catch (Exception e) {
				;
			}
		}
		else{
			String t;
			if(type.equals(COMPUTER.WIN))
				 t = "Windows";
			else
				t = "Mac";
			try {
			
				for (int i =0; i< r.size(); i++){
					if(r.get(i).status.contains(t)){
						HashMap<String, String> tempHash = null;
						
						String name = r.get(i).building + " " + r.get(i).room;
						String status = r.get(i).status;
						
						Log.d("",name);
						Log.d("",status);
						//System.out.println("adding ip: " + ip);
		
						tempHash = new HashMap<String, String>();
						tempHash.put("room", name);
						tempHash.put("status", status);
						
						list.add(tempHash);
					}
				}
				
	
			} catch (Exception e) {
				;
			}
			
		}//win || mac
		
		

		Toast toast = Toast.makeText(this, list.size() + " are available",  Toast.LENGTH_SHORT);
    	toast.show();
    	
		SimpleAdapter listAdapter = new SimpleAdapter(this, list, R.layout.list_config, new String[] { "room", "status" },new int[] { R.id.room, R.id.status});
		setListAdapter(listAdapter);

	}

	class SpinnerOnSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			switch(arg2){
			case 0:	updateList(roomAvailable, COMPUTER.ALL);break;
			case 1:	updateList(roomAvailable, COMPUTER.WIN);break;
			case 2:	updateList(roomAvailable, COMPUTER.MAC);break;
				
			}
			
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	} 
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      
        
        adapter = ArrayAdapter.createFromResource(this, R.array.spinnerName, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        t= (TextView)findViewById(R.id.t1);
        
        selectStation = (Spinner)findViewById(R.id.spinner);
        selectStation.setAdapter(adapter);
        selectStation.setPrompt("Find Me a...");
        selectStation.setOnItemSelectedListener(new SpinnerOnSelectedListener());
    
       
        String a = "https://www.purdue.edu/apps/ics/LabMap";
        String b = "http://www.purdue.edu/";
       // t.setVisibility(View.INVISIBLE);
        
        //DownloadFromUrl(a, "/mnt/sdcard/lab");
        
        //FileInputStream fstream = null;
       
        
        try {
			read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //finish();
        String content = roomList.size() + " computer labs found " + roomAvailable.size() + " labs are available now";
        /*for(int i =0; i< roomList.size(); i++){
        	content += "\n" + roomList.get(i).building+" "+roomList.get(i).room+ "\n" + roomList.get(i).longtitude+ roomList.get(i).latitude+"\n" + roomList.get(i).status + "\n";
        	
        }*/
        
        t.setText(content);
        t.setVisibility(View.VISIBLE);
        
        //updateList(roomAvailable, COMPUTER.ALL);
		
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
	        		String s2 = null;
	        		roomList.add(new rooms(bName, rName, status, currentLongtitude, currentLatitude));
	        		
	        		if(status.contains("Class in Session") || status.contains("CLOSED"))
	        			roomNoAvailable.add(new rooms(bName, rName, status, currentLongtitude, currentLatitude));
	        		else if(status.contains("available") && !status.contains("0 of")){
	        			if(status.contains("Windows")){
	        				s2 = (String) status.subSequence(0, status.indexOf("Windows")+7) + " station available";
	        				Log.d("s2",s2);
		        			
	        			}else{
	        				
	        				s2 = (String) status.substring(0, status.indexOf("Mac")+3 ) + " OS X stations available";
	        				
	        			}
	        			roomAvailable.add(new rooms(bName, rName, s2, currentLongtitude, currentLatitude));

	        		}
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
     
        if(!fFileName.equals(null))	{
        	
        	Toast toast = Toast.makeText(this, "LapFile Found",  Toast.LENGTH_SHORT);
        	toast.show();
        	
        }
        
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