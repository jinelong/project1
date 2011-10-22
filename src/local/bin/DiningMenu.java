package local.bin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class DiningMenu  extends ListActivity {
	
	private final int EARHART = 2;
	private final int FORD = 10;
	private final int HILLENBRAND = 9;
	private final int WILEY = 4;
	private final int WINDSOR = 11;
	
	private final String PATH = "/mnt/sdcard/";
	
	public boolean DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
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
                return true;

        } catch (IOException e) {
                Log.d("downloader", "Error: " + e);
                return false;
        }

	}//downloader
	
	

    
    private void read(String fileName) throws IOException {
    	String a ;
        Log.d("downloader", "Reading from file.");
        File fFileName = new File(fileName);
        Scanner scanner = new Scanner(new FileInputStream(fFileName));
    
        Pattern p = Pattern.compile("(^\\s+)?maparray\\[\\d+\\]\\[4\\]");
        Pattern longtitude =Pattern.compile("(^\\s+)?maparray\\[\\d+\\]\\[1\\]");
        Pattern latitude =Pattern.compile("(^\\s+)?maparray\\[\\d+\\]\\[2\\]");
     
        
        
        	while (scanner.hasNextLine()){
          
        	
        	}//while
          
      }//read
   
	
	 public void onCreate(Bundle savedInstanceState) {
	    	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        final Calendar c = Calendar.getInstance();
	        int mYear = c.get(Calendar.YEAR);
	        int mMonth = c.get(Calendar.MONTH);
	        int mDay = c.get(Calendar.DAY_OF_MONTH);
	        
	        String downloadAddr = "http://www.housing.purdue.edu/Menus/menu.aspx?hallID="+"9"+"&date=" + mMonth + "/" + mDay + "/"+ mYear;

	        if(DownloadFromUrl(downloadAddr, PATH+"menu1")){
	        	try {
					read(PATH+"menu1");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//call updateList
	        }
	        
	        
	 }
	
	//URL format:
	//http://www.housing.purdue.edu/Menus/menu.aspx?hallID=9&date=10/21/2011
	

}
	